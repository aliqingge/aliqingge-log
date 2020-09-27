package com.aliqingge.log.collector;

import com.aliqingge.log.bean.LogInfo;

/**
 * @author zhangzhongqing
 * @date 2020/9/27 14:39
 */
@FunctionalInterface
public interface LogCollector {

    /**
     * 日志收集
     *
     * @param data
     */
    void collect(LogInfo data);
}
