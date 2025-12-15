/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import org.springframework.stereotype.Component;

/**
 * Description: 子任务流向解析类
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
@Component
public class SubTaskTransitionResolver extends TaskTransitionResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.TransitionResolver#resolveForkTask(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.core.Token)
     */
    @Override
    public void resolveForkTask(Transition transition, Token token) {
        super.resolveForkTask(transition, token);
    }

}
