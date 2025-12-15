/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
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
public class EndTransitionResolver implements TransitionResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.TransitionResolver#resolveForkTask(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.core.Token)
     */
    @Override
    public void resolveForkTask(Transition transition, Token token) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#checkAndPrepare(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public void checkAndPrepare(Transition transition, Token token) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#resolveJoinTask(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public boolean resolveJoinTask(Transition transition, Token token) {
        return true;
    }

}
