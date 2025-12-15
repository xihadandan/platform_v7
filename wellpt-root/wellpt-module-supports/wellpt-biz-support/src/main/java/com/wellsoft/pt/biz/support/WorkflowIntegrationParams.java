/*
 * @(#)12/19/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/19/22.1	zhulh		12/19/22		Create
 * </pre>
 * @date 12/19/22
 */
public class WorkflowIntegrationParams extends BaseObject {
    private static final long serialVersionUID = -2399640498008158977L;

    // 流程实例UUID
    private String flowInstUuid;

//    // 当前流程事件
//    private Event event;

    // 发起事项配置
    // private ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig;
    private ItemFlowDefinition.EdgeConfiguration edgeConfiguration;

//    // 业务流程定义
//    private ProcessDefinitionJsonParser parser;

    // 附加参数
    private Map<String, Object> extraParams;

    // 提交结果
    private SubmitResult submitResult;

    // 流程数据
    private WorkBean workData;

    // 办理意见
    private String opinionText;

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

//    /**
//     * @return the event
//     */
//    public Event getEvent() {
//        return event;
//    }
//
//    /**
//     * @param event 要设置的event
//     */
//    public void setEvent(Event event) {
//        this.event = event;
//    }

//    /**
//     * @return the newItemConfig
//     */
//    public ProcessItemConfig.BusinessIntegrationNewItemConfig getNewItemConfig() {
//        return newItemConfig;
//    }
//
//    /**
//     * @param newItemConfig 要设置的newItemConfig
//     */
//    public void setNewItemConfig(ProcessItemConfig.BusinessIntegrationNewItemConfig newItemConfig) {
//        this.newItemConfig = newItemConfig;
//    }


    /**
     * @return the edgeConfiguration
     */
    public ItemFlowDefinition.EdgeConfiguration getEdgeConfiguration() {
        return edgeConfiguration;
    }

    /**
     * @param edgeConfiguration 要设置的edgeConfiguration
     */
    public void setEdgeConfiguration(ItemFlowDefinition.EdgeConfiguration edgeConfiguration) {
        this.edgeConfiguration = edgeConfiguration;
    }

//    /**
//     * @return the parser
//     */
//    public ProcessDefinitionJsonParser getParser() {
//        return parser;
//    }
//
//    /**
//     * @param parser 要设置的parser
//     */
//    public void setParser(ProcessDefinitionJsonParser parser) {
//        this.parser = parser;
//    }

    /**
     * @return the extraParams
     */
    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    /**
     * @param extraParams 要设置的extraParams
     */
    public void setExtraParams(Map<String, Object> extraParams) {
        this.extraParams = extraParams;
    }

    /**
     * @return the submitResult
     */
    public SubmitResult getSubmitResult() {
        return submitResult;
    }

    /**
     * @param submitResult 要设置的submitResult
     */
    public void setSubmitResult(SubmitResult submitResult) {
        this.submitResult = submitResult;
    }

    /**
     * @return the workData
     */
    public WorkBean getWorkData() {
        return workData;
    }

    /**
     * @param workData 要设置的workData
     */
    public void setWorkData(WorkBean workData) {
        this.workData = workData;
    }

    /**
     * @return the opinionText
     */
    public String getOpinionText() {
        return opinionText;
    }

    /**
     * @param opinionText 要设置的opinionText
     */
    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

}
