/*
 * @(#)11/21/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/21/22.1	zhulh		11/21/22		Create
 * </pre>
 * @date 11/21/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowBusinessDefinitionJson extends BaseObject {
    private static final long serialVersionUID = 6628151657357937117L;

    // 定义UUID
    private String uuid;
    // 名称
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    // 流程分类UUID
    private String categoryUuid;
    // 流程定义UUID
    private String flowDefUuid;
    // 流程定义ID
    private String flowDefId;
    // 事件监听
    private String listener;
    // 备注
    private String remark;
    // 阶段列表
    private List<FlowBusinessStage> stages;
    // 业务状态
    private List<FlowBusinessState> states;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
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
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the stages
     */
    public List<FlowBusinessStage> getStages() {
        return stages;
    }

    /**
     * @param stages 要设置的stages
     */
    public void setStages(List<FlowBusinessStage> stages) {
        this.stages = stages;
    }

    /**
     * @return the states
     */
    public List<FlowBusinessState> getStates() {
        return states;
    }

    /**
     * @param states 要设置的states
     */
    public void setStates(List<FlowBusinessState> states) {
        this.states = states;
    }

    /**
     * 阶段
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class FlowBusinessStage extends BaseObject {
        private static final long serialVersionUID = -8987231110871053501L;

        // 阶段名称
        private String name;
        // 阶段ID
        private String id;

        // 子阶段
        private List<FlowBusinessStage> children;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the children
         */
        public List<FlowBusinessStage> getChildren() {
            return children;
        }

        /**
         * @param children 要设置的children
         */
        public void setChildren(List<FlowBusinessStage> children) {
            this.children = children;
        }
    }

    /**
     * 业务状态
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FlowBusinessState extends BaseObject {
        private static final long serialVersionUID = 7634471924921474564L;

        // 状态类型名称
        private String stateTypeName;
        // 状态名称写入字段
        private String stateNameField;
        // 状态代码写入字段
        private String stateCodeField;
        // 状态配置
        private List<FlowBusinessStateConfig> stateConfigs;

        /**
         * @return the stateTypeName
         */
        public String getStateTypeName() {
            return stateTypeName;
        }

        /**
         * @param stateTypeName 要设置的stateTypeName
         */
        public void setStateTypeName(String stateTypeName) {
            this.stateTypeName = stateTypeName;
        }

        /**
         * @return the stateNameField
         */
        public String getStateNameField() {
            return stateNameField;
        }

        /**
         * @param stateNameField 要设置的stateNameField
         */
        public void setStateNameField(String stateNameField) {
            this.stateNameField = stateNameField;
        }

        /**
         * @return the stateCodeField
         */
        public String getStateCodeField() {
            return stateCodeField;
        }

        /**
         * @param stateCodeField 要设置的stateCodeField
         */
        public void setStateCodeField(String stateCodeField) {
            this.stateCodeField = stateCodeField;
        }

        /**
         * @return the stateConfigs
         */
        public List<FlowBusinessStateConfig> getStateConfigs() {
            return stateConfigs;
        }

        /**
         * @param stateConfigs 要设置的stateConfigs
         */
        public void setStateConfigs(List<FlowBusinessStateConfig> stateConfigs) {
            this.stateConfigs = stateConfigs;
        }
    }

    /**
     * 业务状态配置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FlowBusinessStateConfig extends BaseObject {
        private static final long serialVersionUID = 6621582048773641105L;

        // 阶段名称
        private String stageName;
        // 阶段ID
        private String stageId;
        // 状态名称脚本类型，1固定值，2freemarker表达式，3groovy脚本
        private String stateNameScriptType;
        // 状态名称值
        private String stateNameValue;
        // 状态代码脚本类型，1固定值，2freemarker表达式，3groovy脚本
        private String stateCodeScriptType;
        // 状态名称值
        private String stateCodeValue;
        // 状态触发类型
        private String triggerType;
        // 归属、操作环节ID
        private List<String> taskIds;
        // 操作类型，Submit提交、Rollback退回、Cancel撤回
        private String actionType;
        // 流向ID
        private List<String> directionIds;
        // 附加条件
        private boolean additionalCondition;
        // 表达式脚本类型
        private String expressionScriptType;
        // 条件表达式
        private String conditionExpression;
        // 状态名称写入字段——冗余字段
        private String stateNameField;
        // 状态代码写入字段——冗余字段
        private String stateCodeField;

        /**
         * @return the stageName
         */
        public String getStageName() {
            return stageName;
        }

        /**
         * @param stageName 要设置的stageName
         */
        public void setStageName(String stageName) {
            this.stageName = stageName;
        }

        /**
         * @return the stageId
         */
        public String getStageId() {
            return stageId;
        }

        /**
         * @param stageId 要设置的stageId
         */
        public void setStageId(String stageId) {
            this.stageId = stageId;
        }

        /**
         * @return the stateNameScriptType
         */
        public String getStateNameScriptType() {
            return stateNameScriptType;
        }

        /**
         * @param stateNameScriptType 要设置的stateNameScriptType
         */
        public void setStateNameScriptType(String stateNameScriptType) {
            this.stateNameScriptType = stateNameScriptType;
        }

        /**
         * @return the stateNameValue
         */
        public String getStateNameValue() {
            return stateNameValue;
        }

        /**
         * @param stateNameValue 要设置的stateNameValue
         */
        public void setStateNameValue(String stateNameValue) {
            this.stateNameValue = stateNameValue;
        }

        /**
         * @return the stateCodeScriptType
         */
        public String getStateCodeScriptType() {
            return stateCodeScriptType;
        }

        /**
         * @param stateCodeScriptType 要设置的stateCodeScriptType
         */
        public void setStateCodeScriptType(String stateCodeScriptType) {
            this.stateCodeScriptType = stateCodeScriptType;
        }

        /**
         * @return the stateCodeValue
         */
        public String getStateCodeValue() {
            return stateCodeValue;
        }

        /**
         * @param stateCodeValue 要设置的stateCodeValue
         */
        public void setStateCodeValue(String stateCodeValue) {
            this.stateCodeValue = stateCodeValue;
        }

        /**
         * @return the triggerType
         */
        public String getTriggerType() {
            return triggerType;
        }

        /**
         * @param triggerType 要设置的triggerType
         */
        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        /**
         * @return the taskIds
         */
        public List<String> getTaskIds() {
            return taskIds;
        }

        /**
         * @param taskIds 要设置的taskIds
         */
        public void setTaskIds(List<String> taskIds) {
            this.taskIds = taskIds;
        }

        /**
         * @return the actionType
         */
        public String getActionType() {
            return actionType;
        }

        /**
         * @param actionType 要设置的actionType
         */
        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        /**
         * @return the directionIds
         */
        public List<String> getDirectionIds() {
            return directionIds;
        }

        /**
         * @param directionIds 要设置的directionIds
         */
        public void setDirectionIds(List<String> directionIds) {
            this.directionIds = directionIds;
        }

        /**
         * @return the additionalCondition
         */
        public boolean getAdditionalCondition() {
            return additionalCondition;
        }

        /**
         * @param additionalCondition 要设置的additionalCondition
         */
        public void setAdditionalCondition(boolean additionalCondition) {
            this.additionalCondition = additionalCondition;
        }

        /**
         * @return the expressionScriptType
         */
        public String getExpressionScriptType() {
            return expressionScriptType;
        }

        /**
         * @param expressionScriptType 要设置的expressionScriptType
         */
        public void setExpressionScriptType(String expressionScriptType) {
            this.expressionScriptType = expressionScriptType;
        }

        /**
         * @return the conditionExpression
         */
        public String getConditionExpression() {
            return conditionExpression;
        }

        /**
         * @param conditionExpression 要设置的conditionExpression
         */
        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        /**
         * @return the stateNameField
         */
        public String getStateNameField() {
            return stateNameField;
        }

        /**
         * @param stateNameField 要设置的stateNameField
         */
        public void setStateNameField(String stateNameField) {
            this.stateNameField = stateNameField;
        }

        /**
         * @return the stateCodeField
         */
        public String getStateCodeField() {
            return stateCodeField;
        }

        /**
         * @param stateCodeField 要设置的stateCodeField
         */
        public void setStateCodeField(String stateCodeField) {
            this.stateCodeField = stateCodeField;
        }
    }
}
