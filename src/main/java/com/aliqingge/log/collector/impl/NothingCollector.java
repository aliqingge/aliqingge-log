package com.aliqingge.log.collector.impl;

import com.aliqingge.log.bean.LogInfo;
import com.aliqingge.log.collector.LogCollector;

/**
 * 默认空的收集器
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:39
 */
public class NothingCollector implements LogCollector {

    @Override
    public void collect(LogInfo data) {

    }
}
