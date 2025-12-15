/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.support;

import org.javasimon.callback.calltree.CallTree;

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
public abstract class CallTreeUtils {

    private static final ThreadLocal<CallTree> threadCallTree = new ThreadLocal<CallTree>();

    public static CallTree create(Long logThreshold) {
        return new CallTree(logThreshold);
    }

    public static CallTree get() {
        return threadCallTree.get();
    }

    public static void set(CallTree callTree) {
        threadCallTree.set(callTree);
    }

    public static void remove() {
        threadCallTree.remove();
    }
}
