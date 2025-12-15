/*
 * @(#)2014-9-23 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;

/**
 * Description: 流程审批返回结果
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-23.1	Asus		2014-9-23		Create
 * </pre>
 * @date 2014-9-23
 */
public class FlowResult extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public static class FlowFormData extends WellptObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5157934016570758341L;

    }

    public static class FlowResultData extends WellptObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 842607801471853690L;

        // true 通过 false 不通过
        private boolean approvalResult;
        // 流程实例的流程定义ID，精确查询
        private String flowDefinitionId;
        // 流程实例UUID
        private String flowInstUuid;
        // 当前节点的任务UUID
        private String taskInstUuid;
        // 当前节点是第几节点
        private int taskId;

        /**
         * @return the approvalResult
         */
        public boolean isApprovalResult() {
            return approvalResult;
        }

        /**
         * @param approvalResult 要设置的approvalResult
         */
        public void setApprovalResult(boolean approvalResult) {
            this.approvalResult = approvalResult;
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
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the taskId
         */
        public int getTaskId() {
            return taskId;
        }

        /**
         * @param taskId 要设置的taskId
         */
        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

    }

}
