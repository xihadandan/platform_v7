/*
 * @(#)2015-3-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.AutoSubmitRuleElement;
import com.wellsoft.pt.bpm.engine.node.Node;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-10.1	zhulh		2015-3-10		Create
 * </pre>
 * @date 2015-3-10
 */
public interface SameUserSubmitService extends BaseService {

    String KEY_SUPPLEMENT_INFO = "sameUserSupplementInfo";

    /**
     *
     */
    void doSameUserSubmit(ExecutionContext executionContext, String taskId, String sameUserSubmit);

    /**
     * 审批去重
     *
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    void doAutoSubmit(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement);

    /**
     * 后置审批，补审补办
     *
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    boolean doAutoSupplement(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement);

    /**
     * 补审补办提交结束
     *
     * @param token
     * @return
     */
    boolean isSupplementToCompleted(Token token);

    /**
     * 完成补件
     *
     * @param executionContext
     * @param node
     */
    void completedSupplement(ExecutionContext executionContext, Node node);

    /**
     * 补审补办提交后撤回，更新任务名称
     *
     * @param executionContext
     * @param node
     * @param autoSubmitRuleElement
     */
    void updateCanceledSupplementTaskName(ExecutionContext executionContext, Node node, AutoSubmitRuleElement autoSubmitRuleElement);

    //   AutoSubmitRuleElement getAutoSubmitRuleElement();

}
