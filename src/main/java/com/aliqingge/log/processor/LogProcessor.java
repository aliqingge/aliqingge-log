package com.aliqingge.log.processor;

import com.aliqingge.log.annotation.SystemLog;
import com.aliqingge.log.bean.LogInfo;
import com.aliqingge.log.collector.LogCollectorExecutor;
import com.aliqingge.log.extractor.LogExtractor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 处理器
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:43
 */
@Component
public class LogProcessor {

    private final LogCollectorExecutor logCollectorExecutor;

    private final String appName;

    public LogProcessor(@Autowired LogCollectorExecutor logCollectorExecutor) {
        this.logCollectorExecutor = logCollectorExecutor;
        this.appName = logCollectorExecutor.getApplicationContext().getId();
    }

    /**
     * 处理 日志数据切面
     *
     * @param data  日志数据
     * @param point 切入point对象
     * @return 返回执行结果
     * @throws Throwable Exceptions in AOP should be thrown out and left to the specific business to handle
     */
    public Object proceed(LogInfo data, ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        SystemLog systemLog = signature.getMethod().getAnnotation(SystemLog.class);
        if (systemLog == null) {
            systemLog = point.getTarget().getClass().getAnnotation(SystemLog.class);
        }
        if (systemLog != null) {
            if (!systemLog.logOnError()) {
                logProcessBefore(systemLog, data, point);
            }
            return proceed(systemLog, data, point);
        }
        return point.proceed();
    }


    /**
     * 执行前记录 app应用信息 http等信息
     *
     * @param SystemLog 注解对象
     * @param data      日志数据
     * @param point     切入point对象
     */
    public void logProcessBefore(SystemLog SystemLog, LogInfo data, ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        data.setAppName(appName);
        data.setType(SystemLog.type());
        data.setMethod(signature.getDeclaringTypeName() + "#" + signature.getName());
        LogExtractor.logHttpRequest(data, SystemLog.headers());
        if (SystemLog.args()) {
            data.setArgs(LogExtractor.getArgs(signature.getParameterNames(), point.getArgs()));
        }
    }

    /**
     * 方法执行处理记录
     *
     * @param SystemLog 注解对象
     * @param data      日志数据
     * @param point     切入point对象
     * @return 返回执行结果
     * @throws Throwable Exceptions in AOP should be thrown out and left to the specific business to handle
     */
    private Object proceed(SystemLog SystemLog, LogInfo data, ProceedingJoinPoint point) throws Throwable {
        try {
            Object result = point.proceed();
            if (SystemLog.responseBody()) {
                data.setRespBody(LogExtractor.getResult(result));
            }
            data.setSuccess(true);
            return result;
        } catch (Throwable throwable) {
            if (SystemLog.logOnError()) {
                logProcessBefore(SystemLog, data, point);
            }
            data.setSuccess(false);
            if (SystemLog.stackTraceOnError()) {
                try (StringWriter sw = new StringWriter(); PrintWriter writer = new PrintWriter(sw, true)) {
                    throwable.printStackTrace(writer);
                    LogInfo.step("Fail : \n" + sw.toString());
                }
            }
            throw throwable;
        } finally {
            data.toCostTime();
            LogInfo.setCurrent(data);
            boolean bool = SystemLog.logOnError() && !data.isSuccess();
            if (!SystemLog.logOnError() || bool) {
                if (SystemLog.asyncMode()) {
                    logCollectorExecutor.asyncExecute(SystemLog.collector(), data);
                } else {
                    logCollectorExecutor.execute(SystemLog.collector(), data);
                }
            }
        }
    }

}
