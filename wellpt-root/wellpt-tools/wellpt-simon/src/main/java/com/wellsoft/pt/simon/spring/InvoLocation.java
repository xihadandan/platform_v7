/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring;

import org.javasimon.Split;

/**
 * Description: 如何描述该类
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
public class InvoLocation<T> {

    /**
     * Handler (method invocation).
     */
    private T handler;

    /**
     * Currently running split.
     */
    private Split split;

    /**
     *
     * @param logThreshold
     */
    public InvoLocation() {
    }

    /**
     *
     * @param logThreshold
     * @param rootHandler
     * @param rootSplit
     */
    public InvoLocation(T handler) {
        this.handler = handler;
    }

    public T getHandler() {
        return handler;
    }

    public Split getSplit() {
        return split;
    }

    public void setSplit(Split split) {
        this.split = split;
    }

}
