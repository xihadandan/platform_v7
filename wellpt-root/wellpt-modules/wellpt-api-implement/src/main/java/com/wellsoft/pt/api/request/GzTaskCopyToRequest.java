/*
 * @(#)2015年8月15日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.GzTaskCopyToResponse;

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
 * 2015年8月15日.1	zhulh		2015年8月15日		Create
 * </pre>
 * @date 2015年8月15日
 */
public class GzTaskCopyToRequest extends WellptRequest<GzTaskCopyToResponse> {
    // 任务实例UUID
    private String flowInstUuid;

    // 办理人名称
    private String operator;
    // 办理时间
    private String operateTime;
    // 办理意见立场
    private String opinionValue;
    // 办理意见内容
    private String opinionText;

    // 流程定义ID
    private String flowDefinitionId;

    // 提交到的环节(DRAFT、AUTO_SUBMIT)
    private String toTaskId;

    // 表单数据
    private Map<String, Object> formData;

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

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator 要设置的operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the operateTime
     */
    public String getOperateTime() {
        return operateTime;
    }

    /**
     * @param operateTime 要设置的operateTime
     */
    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return opinionValue;
    }

    /**
     * @param opinionValue 要设置的opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
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

    /**
     * @return the flowDefinitionId
     */
    public String getFlowDefinitionId() {
        return flowDefinitionId;
    }

    /**
     * @param flowDefinitionId 要设置的flowDefinitionId
     */
    public void setFlowDefinitionId(String flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    /**
     * @return the toTaskId
     */
    public String getToTaskId() {
        return toTaskId;
    }

    /**
     * @param toTaskId 要设置的toTaskId
     */
    public void setToTaskId(String toTaskId) {
        this.toTaskId = toTaskId;
    }

    /**
     * @return the formData
     */
    public Map<String, Object> getFormData() {
        return formData;
    }

    /**
     * @param formData 要设置的formData
     */
    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.GZ_TASK_COPY_TO;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<GzTaskCopyToResponse> getResponseClass() {
        return GzTaskCopyToResponse.class;
    }

}
