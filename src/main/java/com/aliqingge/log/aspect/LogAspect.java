package com.aliqingge.log.aspect;

import com.aliqingge.log.bean.LogInfo;
import com.aliqingge.log.processor.LogProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 切面类
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:43
 */
@Aspect
@Component
@ComponentScan
@EnableAspectJAutoProxy(exposeProxy = true)
public class LogAspect {

    @Resource
    private LogProcessor logProcessor;

    /**
     * 将会切被SystemLog注解标记的方法
     */
    @Pointcut(value = "@annotation(SystemLog) || @within(SystemLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object note(ProceedingJoinPoint point) throws Throwable {
        return aopLog(point);
    }

    /**
     * @param point aop 切点对象
     * @return 返回执行结果
     * @throws Throwable Exceptions in AOP should be thrown out and left to the specific business to handle
     */
    private Object aopLog(ProceedingJoinPoint point) throws Throwable {
        try {
            LogInfo.removeCurrent();
            LogInfo data = LogInfo.getCurrent();
            return logProcessor.proceed(data, point);
        } finally {
            LogInfo.removeCurrent();
        }
    }

}
