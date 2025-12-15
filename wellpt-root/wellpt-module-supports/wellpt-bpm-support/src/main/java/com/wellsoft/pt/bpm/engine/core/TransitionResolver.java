/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

/**
 * Description: 流向转换解析接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
public interface TransitionResolver {
    /**
     * 解析分支结点的任务
     *
     * @param transition
     * @param token
     */
    void resolveForkTask(Transition transition, Token token);

    /**
     * 查检开始结点的分支模式、结束结点的聚合模式的正确性
     *
     * @param transition
     * @param token
     */
    void checkAndPrepare(Transition transition, Token token);

    /**
     * 解析聚合结点的任务
     *
     * @param transition
     * @param token
     */
    boolean resolveJoinTask(Transition transition, Token token);

}
