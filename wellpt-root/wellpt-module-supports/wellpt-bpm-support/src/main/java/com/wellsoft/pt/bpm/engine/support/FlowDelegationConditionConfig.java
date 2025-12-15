/*
 * @(#)1/11/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/11/25.1	    zhulh		1/11/25		    Create
 * </pre>
 * @date 1/11/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("委托条件配置")
public class FlowDelegationConditionConfig extends BaseObject {
    private static final long serialVersionUID = 7329094300646853930L;

    public static final String MATCH_ALL = "all";

    @ApiModelProperty("匹配方式，all满足全部条件、any满足任一条件")
    private String match;

    @ApiModelProperty("条件列表")
    private List<FlowDelegationCondition> conditions;

    /**
     * @return the match
     */
    public String getMatch() {
        return match;
    }

    /**
     * @param match 要设置的match
     */
    public void setMatch(String match) {
        this.match = match;
    }

    /**
     * @return the conditions
     */
    public List<FlowDelegationCondition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<FlowDelegationCondition> conditions) {
        this.conditions = conditions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FlowDelegationCondition extends BaseObject {
        private static final long serialVersionUID = -6352761448874421317L;

        public static final String TYPE_FORM_FIELD = "formField";
        public static final String TYPE_WORKFLOW_FIELD = "workflowField";

        // 表达式类型(formField表单字段、workflowField工作流字段)
        private String type;
        // 流程定义ID
        private String flowDefId;
        // 表单字段
        private String formField;
        // 流程字段
        private String workflowField;
        // 操作符
        private String operator;
        // 比较值
        private String value;

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the flowDefId
         */
        public String getFlowDefId() {
            return flowDefId;
        }

        /**
         * @param flowDefId 要设置的flowDefId
         */
        public void setFlowDefId(String flowDefId) {
            this.flowDefId = flowDefId;
        }

        /**
         * @return the formField
         */
        public String getFormField() {
            return formField;
        }

        /**
         * @param formField 要设置的formField
         */
        public void setFormField(String formField) {
            this.formField = formField;
        }

        /**
         * @return the workflowField
         */
        public String getWorkflowField() {
            return workflowField;
        }

        /**
         * @param workflowField 要设置的workflowField
         */
        public void setWorkflowField(String workflowField) {
            this.workflowField = workflowField;
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
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
