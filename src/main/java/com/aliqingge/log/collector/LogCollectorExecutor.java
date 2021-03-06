package com.aliqingge.log.collector;

import com.aliqingge.log.bean.LogInfo;
import com.aliqingge.log.collector.impl.NothingCollector;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志收集执行者
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:46
 */
@Component
public class LogCollectorExecutor {

    @Resource
    private LogCollector collector;

    private Map<Class<? extends LogCollector>, LogCollector> collectors = new HashMap<>();

    private ApplicationContext applicationContext;

    public LogCollectorExecutor(@Autowired ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * AsyncMode log collection 异步模式日志收集
     *
     * @param clz     日志收集器Class对象
     * @param LogInfo 日志数据
     */
    @Async
    public void asyncExecute(Class<? extends LogCollector> clz, LogInfo LogInfo) {
        execute(clz, LogInfo);
    }

    /**
     * 同步模式收集日志
     *
     * @param clz     日志收集器Class对象
     * @param LogInfo 日志数据
     */
    public void execute(Class<? extends LogCollector> clz, LogInfo LogInfo) {
        getExecuteLogCollector(clz).collect(LogInfo);
    }

    /**
     * Get the specified log collector 获取指定的日志收集器
     *
     * @param clz 日志收集器Class对象
     * @return 获取指定的日志收集器
     */
    private LogCollector getExecuteLogCollector(Class<? extends LogCollector> clz) {
        if (clz != NothingCollector.class) {
            LogCollector c;
            try {
                c = applicationContext.getBean(clz);
            } catch (Exception e) {
                c = collectors.get(clz);
                if (c == null) {
                    c = BeanUtils.instantiateClass(clz);
                    collectors.put(clz, c);
                }
            }
            return c;
        } else {
            return collector;
        }
    }

}
