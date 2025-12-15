/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.support;

import org.javasimon.Split;
import org.javasimon.source.StopwatchSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wellsoft.pt.simon.spring.InvoLocation;

/**
 * Description: AbstractStopwatchSource
 *  
 * @author zhongzh
 * @date 2019年11月24日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月24日.1	zhongzh		2019年11月24日		Create
 * </pre>
 *
 */
public abstract class AbstractInterceptor<A, T extends InvoLocation<A>> {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final StopwatchSource<T> stopwatchSource;

    /**
     *
     * @param stopwatchSource stopwatch provider for method invocation
     */
    public AbstractInterceptor(StopwatchSource<T> stopwatchSource) {
        this.stopwatchSource = stopwatchSource;
    }

    /**
     * Start stopwatch for given name and thread.
     *
     * @return Running split
     */
    protected final Split startWatch(T location) {
        Split split = stopwatchSource.start(location);
        location.setSplit(split);
        return split;
    }

    protected final Split stopWatch(InvoLocation<String> location, Object data) {
        Split split = null;
        if (location != null) {
            split = location.getSplit();
            split.setAttribute("data", data);
            split.stop();
            location.setSplit(null);
        }
        return split;
    }

}
