/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.context.util.ApplicationContextHolder;

/**
 * Description: 任务流向转换解析工厂类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public class TransitionResolverFactory {

    /**
     * 根据流向转换类型返回相应的解析类
     *
     * @param transition
     * @return
     */
    public static TransitionResolver getResolver(Transition transition) {
        if (transition instanceof StartTransition) {
            return ApplicationContextHolder.getBean(StartTransitionResolver.class);
        }

        if (transition instanceof TaskTransition) {
            return (TransitionResolver) ApplicationContextHolder.getBean(TaskTransitionResolver.BEAN_NAME);
        }

        if (transition instanceof SubTaskTransition) {
            return ApplicationContextHolder.getBean(SubTaskTransitionResolver.class);
        }

        if (transition instanceof EndTransition) {
            return ApplicationContextHolder.getBean(EndTransitionResolver.class);
        }

        return null;
    }
}
