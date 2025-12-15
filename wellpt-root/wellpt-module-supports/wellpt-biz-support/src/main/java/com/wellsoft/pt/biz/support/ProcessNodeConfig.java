/*
 * @(#)10/9/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/9/22.1	zhulh		10/9/22		Create
 * </pre>
 * @date 10/9/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessNodeConfig extends BaseObject {
    private static final long serialVersionUID = 7693653557314595593L;

    // 引用业务流程定义UUID
    private String refProcessDefUuid;
    // 过程节点名称
    private String name;
    // 过程节点ID
    private String id;
    // 过程节点编码
    private String code;
    // 时限标限
    private int timeLimit;
    // 时限类型，1工作日，2自然日
    private int timeLimitType;
    // 是否里程碑节点
    private boolean milestone;
    // 状态条件
    private List<StateCondition> stateConditions = Lists.newArrayListWithCapacity(0);
    // 事件监听
    private String listener;
    // 业务标签ID
    private String tagId;
    // 备注
    private String remark;

    // 扩展属性
    private Map<String, Object> extAttrs = Maps.newHashMapWithExpectedSize(0);

    // 过程办理单
    private ProcessNodeFormConfig formConfig;

    // 下一级过程节点信息
    private List<ProcessNodeConfig> nodes = Lists.newArrayListWithCapacity(0);

    // 事项节点信息
    private List<ProcessItemConfig> items = Lists.newArrayListWithCapacity(0);

    /**
     * @return the refProcessDefUuid
     */
    public String getRefProcessDefUuid() {
        return refProcessDefUuid;
    }

    /**
     * @param refProcessDefUuid 要设置的refProcessDefUuid
     */
    public void setRefProcessDefUuid(String refProcessDefUuid) {
        this.refProcessDefUuid = refProcessDefUuid;
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
     * @return the timeLimit
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the timeLimitType
     */
    public int getTimeLimitType() {
        return timeLimitType;
    }

    /**
     * @param timeLimitType 要设置的timeLimitType
     */
    public void setTimeLimitType(int timeLimitType) {
        this.timeLimitType = timeLimitType;
    }

    /**
     * @return the milestone
     */
    public boolean getMilestone() {
        return milestone;
    }

    /**
     * @param milestone 要设置的milestone
     */
    public void setMilestone(boolean milestone) {
        this.milestone = milestone;
    }

    /**
     * @return the stateConditions
     */
    public List<StateCondition> getStateConditions() {
        return stateConditions;
    }

    /**
     * @param stateConditions 要设置的stateConditions
     */
    public void setStateConditions(List<StateCondition> stateConditions) {
        this.stateConditions = stateConditions;
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
     * @return the tagId
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * @param tagId 要设置的tagId
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
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
     * @return the extAttrs
     */
    public Map<String, Object> getExtAttrs() {
        return extAttrs;
    }

    /**
     * @param extAttrs 要设置的extAttrs
     */
    public void setExtAttrs(Map<String, Object> extAttrs) {
        this.extAttrs = extAttrs;
    }

    /**
     * @return the formConfig
     */
    public ProcessNodeFormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * @param formConfig 要设置的formConfig
     */
    public void setFormConfig(ProcessNodeFormConfig formConfig) {
        this.formConfig = formConfig;
    }

    /**
     * @return the nodes
     */
    public List<ProcessNodeConfig> getNodes() {
        return nodes;
    }

    /**
     * @param nodes 要设置的nodes
     */
    public void setNodes(List<ProcessNodeConfig> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the items
     */
    public List<ProcessItemConfig> getItems() {
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(List<ProcessItemConfig> items) {
        this.items = items;
    }

    /**
     * @param changedState
     * @return
     */
    public StateCondition getStateConditionByChangedState(String changedState) {
        if (CollectionUtils.isEmpty(stateConditions)) {
            return null;
        }
        for (StateCondition stateCondition : stateConditions) {
            if (StringUtils.equals(stateCondition.getChangedState(), changedState)) {
                return stateCondition;
            }
        }
        return null;
    }

    /**
     * 表单设置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ProcessNodeFormConfig extends BaseObject {
        private static final long serialVersionUID = 6423589993234976411L;

        // 配置类型，1引用模板，2自定义
        private String configType;
        // 引用模板UUID
        private String templateUuid;
        // 引用模板名称
        private String templateName;
        // 表单定义UUID
        private String formUuid;
        // 业务主体名称字段
        private String entityNameField;
        // 业务主体ID字段
        private String entityIdField;
        // 事项办理信息显示位置
        private String itemPlaceHolder;
        // 状态定义列表
        private List<StateDefinition> states;

        // 是否启用表单设置
        private boolean enabledDyformSetting;
        // 表单设置配置
        private Map<String, Object> widgetDyformSetting;

        /**
         * @return the configType
         */
        public String getConfigType() {
            return configType;
        }

        /**
         * @param configType 要设置的configType
         */
        public void setConfigType(String configType) {
            this.configType = configType;
        }

        /**
         * @return the templateUuid
         */
        public String getTemplateUuid() {
            return templateUuid;
        }

        /**
         * @param templateUuid 要设置的templateUuid
         */
        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        /**
         * @return the templateName
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         * @param templateName 要设置的templateName
         */
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the entityNameField
         */
        public String getEntityNameField() {
            return entityNameField;
        }

        /**
         * @param entityNameField 要设置的entityNameField
         */
        public void setEntityNameField(String entityNameField) {
            this.entityNameField = entityNameField;
        }

        /**
         * @return the entityIdField
         */
        public String getEntityIdField() {
            return entityIdField;
        }

        /**
         * @param entityIdField 要设置的entityIdField
         */
        public void setEntityIdField(String entityIdField) {
            this.entityIdField = entityIdField;
        }

        /**
         * @return the itemPlaceHolder
         */
        public String getItemPlaceHolder() {
            return itemPlaceHolder;
        }

        /**
         * @param itemPlaceHolder 要设置的itemPlaceHolder
         */
        public void setItemPlaceHolder(String itemPlaceHolder) {
            this.itemPlaceHolder = itemPlaceHolder;
        }

        /**
         * @return the states
         */
        public List<StateDefinition> getStates() {
            return states;
        }

        /**
         * @param states 要设置的states
         */
        public void setStates(List<StateDefinition> states) {
            this.states = states;
        }

        /**
         * @return the enabledDyformSetting
         */
        public boolean isEnabledDyformSetting() {
            return enabledDyformSetting;
        }

        /**
         * @param enabledDyformSetting 要设置的enabledDyformSetting
         */
        public void setEnabledDyformSetting(boolean enabledDyformSetting) {
            this.enabledDyformSetting = enabledDyformSetting;
        }

        /**
         * @return the widgetDyformSetting
         */
        public Map<String, Object> getWidgetDyformSetting() {
            return widgetDyformSetting;
        }

        /**
         * @param widgetDyformSetting 要设置的widgetDyformSetting
         */
        public void setWidgetDyformSetting(Map<String, Object> widgetDyformSetting) {
            this.widgetDyformSetting = widgetDyformSetting;
        }
    }

    /**
     * 状态条件类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class StateCondition extends BaseObject {
        private static final long serialVersionUID = -813011357126974185L;

        // 要变更的状态
        private String changedState;

        private String changedStateName;

        // 条件名称
        private String conditionName;

        // 条件ID，自动生成
        private String id;

        // 状态条件配置列表
        private List<StateConditionConfig> configs = Lists.newArrayListWithCapacity(0);

        /**
         * @return the changedState
         */
        public String getChangedState() {
            return changedState;
        }

        /**
         * @param changedState 要设置的changedState
         */
        public void setChangedState(String changedState) {
            this.changedState = changedState;
        }

        /**
         * @return the changedStateName
         */
        public String getChangedStateName() {
            return changedStateName;
        }

        /**
         * @param changedStateName 要设置的changedStateName
         */
        public void setChangedStateName(String changedStateName) {
            this.changedStateName = changedStateName;
        }

        /**
         * @return the conditionName
         */
        public String getConditionName() {
            return conditionName;
        }

        /**
         * @param conditionName 要设置的conditionName
         */
        public void setConditionName(String conditionName) {
            this.conditionName = conditionName;
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
         * @return the configs
         */
        public List<StateConditionConfig> getConfigs() {
            return configs;
        }

        /**
         * @param configs 要设置的configs
         */
        public void setConfigs(List<StateConditionConfig> configs) {
            this.configs = configs;
        }
    }

    /**
     * 状态条件配置类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class StateConditionConfig extends BaseObject {
        private static final long serialVersionUID = -502557434882036743L;

        // 条件名称
        private String name;
        // 表达式类型，1通过其他过程状态判断，2通过事项到达里程碑判断，9逻辑条件
        private String type;
        // 连接符，表达式类型为逻辑条件时有效
        private String connector;
        // 连接符名称，表达式类型为逻辑条件时有效
        private String connectorName;
        // 左括号
        private String leftBracket;
        // 过程节点编码
        private String processNodeCode;
        // 过程节点名称
        private String processNodeName;
        // 过程节点状态
        private String processNodeState;
        // 过程节点状态名称
        private String processNodeStateName;
        // 事项编码
        private String processItemCode;
        // 事项名称
        private String processItemName;
        // 右括号
        private String rightBracket;

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
         * @return the connector
         */
        public String getConnector() {
            return connector;
        }

        /**
         * @param connector 要设置的connector
         */
        public void setConnector(String connector) {
            this.connector = connector;
        }

        /**
         * @return the connectorName
         */
        public String getConnectorName() {
            return connectorName;
        }

        /**
         * @param connectorName 要设置的connectorName
         */
        public void setConnectorName(String connectorName) {
            this.connectorName = connectorName;
        }

        /**
         * @return the leftBracket
         */
        public String getLeftBracket() {
            return Objects.toString(leftBracket, StringUtils.EMPTY);
        }

        /**
         * @param leftBracket 要设置的leftBracket
         */
        public void setLeftBracket(String leftBracket) {
            this.leftBracket = leftBracket;
        }

        /**
         * @return the processNodeCode
         */
        public String getProcessNodeCode() {
            return processNodeCode;
        }

        /**
         * @param processNodeCode 要设置的processNodeCode
         */
        public void setProcessNodeCode(String processNodeCode) {
            this.processNodeCode = processNodeCode;
        }

        /**
         * @return the processNodeName
         */
        public String getProcessNodeName() {
            return processNodeName;
        }

        /**
         * @param processNodeName 要设置的processNodeName
         */
        public void setProcessNodeName(String processNodeName) {
            this.processNodeName = processNodeName;
        }

        /**
         * @return the processNodeState
         */
        public String getProcessNodeState() {
            return processNodeState;
        }

        /**
         * @param processNodeState 要设置的processNodeState
         */
        public void setProcessNodeState(String processNodeState) {
            this.processNodeState = processNodeState;
        }

        /**
         * @return the processNodeStateName
         */
        public String getProcessNodeStateName() {
            return processNodeStateName;
        }

        /**
         * @param processNodeStateName 要设置的processNodeStateName
         */
        public void setProcessNodeStateName(String processNodeStateName) {
            this.processNodeStateName = processNodeStateName;
        }

        /**
         * @return the processItemCode
         */
        public String getProcessItemCode() {
            return processItemCode;
        }

        /**
         * @param processItemCode 要设置的processItemCode
         */
        public void setProcessItemCode(String processItemCode) {
            this.processItemCode = processItemCode;
        }

        /**
         * @return the processItemName
         */
        public String getProcessItemName() {
            return processItemName;
        }

        /**
         * @param processItemName 要设置的processItemName
         */
        public void setProcessItemName(String processItemName) {
            this.processItemName = processItemName;
        }

        /**
         * @return the rightBracket
         */
        public String getRightBracket() {
            return Objects.toString(rightBracket, StringUtils.EMPTY);
        }

        /**
         * @param rightBracket 要设置的rightBracket
         */
        public void setRightBracket(String rightBracket) {
            this.rightBracket = rightBracket;
        }
    }

}
