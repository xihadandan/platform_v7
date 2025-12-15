/*
 * @(#)10/9/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
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
 * 10/9/22.1	zhulh		10/9/22		Create
 * </pre>
 * @date 10/9/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDefinitionJson extends BaseObject {
    private static final long serialVersionUID = -8822286713284551776L;

    // 定义UUID
    private String uuid;
    // 名称
    private String name;
    // ID
    private String id;
    // 编号
    private String code;
    // 版本号
    private Double version;
    // true/false，是否启用
    private boolean enabled;
    // 业务ID
    private String businessId;
    // 事件监听
    private String listener;
    // 业务标签ID
    private String tagId;
    // 备注
    private String remark;

    // 业务主体
    private ProcessEntityConfig entityConfig;
    // 业务办理单
    private ProcessFormConfig formConfig;

    // 过程节点信息
    private List<ProcessNodeConfig> nodes = Lists.newArrayListWithCapacity(0);

    // 状态树
    private TreeNode stateTree;

    // 事项流
    private String itemFlows;

    // 布局方式,horizontal水平方向、vertical垂直方向
    private String layout;
    // 图形数据
    private String graphData;

    // 模板定义信息
    private List<DefinitionTemplateInfo> definitionTemplates = Lists.newArrayList();

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
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the enabled
     */
    public boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId 要设置的businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
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
     * @return the entityConfig
     */
    public ProcessEntityConfig getEntityConfig() {
        return entityConfig;
    }

    /**
     * @param entityConfig 要设置的entityConfig
     */
    public void setEntityConfig(ProcessEntityConfig entityConfig) {
        this.entityConfig = entityConfig;
    }

    /**
     * @return the formConfig
     */
    public ProcessFormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * @param formConfig 要设置的formConfig
     */
    public void setFormConfig(ProcessFormConfig formConfig) {
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
     * @return the stateTree
     */
    public TreeNode getStateTree() {
        return stateTree;
    }

    /**
     * @param stateTree 要设置的stateTree
     */
    public void setStateTree(TreeNode stateTree) {
        this.stateTree = stateTree;
    }

    /**
     * @return the itemFlows
     */
    public String getItemFlows() {
        return itemFlows;
    }

    /**
     * @param itemFlows 要设置的itemFlows
     */
    public void setItemFlows(String itemFlows) {
        this.itemFlows = itemFlows;
    }

    /**
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

    /**
     * @param layout 要设置的layout
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * @return the graphData
     */
    public String getGraphData() {
        return graphData;
    }

    /**
     * @param graphData 要设置的graphData
     */
    public void setGraphData(String graphData) {
        this.graphData = graphData;
    }

    /**
     * @return the definitionTemplates
     */
    public List<DefinitionTemplateInfo> getDefinitionTemplates() {
        return definitionTemplates;
    }

    /**
     * @param definitionTemplates 要设置的definitionTemplates
     */
    public void setDefinitionTemplates(List<DefinitionTemplateInfo> definitionTemplates) {
        this.definitionTemplates = definitionTemplates;
    }

    /**
     * 业务主体
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProcessEntityConfig extends BaseObject {
        // 表单定义UUID
        private String formUuid;
        // 业务主体ID字段
        private String entityIdField;
        // 状态定义列表
        private List<StateDefinition> states;
        // 状态计时器
        private List<StateTimerConfig> timers;

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
         * @return the timers
         */
        public List<StateTimerConfig> getTimers() {
            return timers;
        }

        /**
         * @param timers 要设置的timers
         */
        public void setTimers(List<StateTimerConfig> timers) {
            this.timers = timers;
        }
    }

    /**
     * 状态计时器配置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StateTimerConfig extends BaseObject {
        private static final long serialVersionUID = 2612353781442052875L;

        // 计时器名称
        private String name;
        // ID，自动生成
        private String id;
        // 状态代码字段
        private String stateCodeField;
        // 计时器配置ID
        private String timerConfigId;
        // 动态时限字段
        private String timeLimitField;
        // 工作时间方案ID
        private String workTimePlanId;
        // 计时状态ID列表
        private List<String> stateIds;
        // 计时状态代码列表
        private List<String> stateCodes;
        // 离开计时状态时，stop结束计时、pause暂停计时
        private String timerOfStateChanged;

        // 预警处理
        private boolean enableAlarmDoing;
        // 预警时限
        private int alarmTimeLimit;
        // 预警计时方式
        private String alarmTimingMode;
        // 预警次数
        private int alarmCount;
        // 预警消息通知对象
        private List<String> alarmMsgObjects = Lists.newArrayListWithCapacity(0);
        // 预警消息通知其他人员
        private String alarmMsgOtherUsers;
        // 预警消息通知模板
        private String alarmMsgTemplateId;

        // 到期处理
        private boolean enableDueDoing;
        // 到期消息通知对象
        private List<String> dueMsgObjects = Lists.newArrayListWithCapacity(0);
        // 到期消息通知其他人员
        private String dueMsgOtherUsers;
        // 到期消息通知模板
        private String dueMsgTemplateId;

        // 逾期处理
        private boolean enableOverdueDoing;
        // 逾期消息通知对象
        private List<String> overdueMsgObjects = Lists.newArrayListWithCapacity(0);
        // 逾期消息通知其他人员
        private String overdueMsgOtherUsers;
        // 逾期消息通知模板
        private String overdueMsgTemplateId;
        // 逾期处理操作
        private String overdueAction;
        // 逾期处理执行Groovy脚本
        private String overdueGroovyScript;
        // 备注
        private String remark;

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
         * @return the timerConfigId
         */
        public String getTimerConfigId() {
            return timerConfigId;
        }

        /**
         * @param timerConfigId 要设置的timerConfigId
         */
        public void setTimerConfigId(String timerConfigId) {
            this.timerConfigId = timerConfigId;
        }

        /**
         * @return the timeLimitField
         */
        public String getTimeLimitField() {
            return timeLimitField;
        }

        /**
         * @param timeLimitField 要设置的timeLimitField
         */
        public void setTimeLimitField(String timeLimitField) {
            this.timeLimitField = timeLimitField;
        }

        /**
         * @return the workTimePlanId
         */
        public String getWorkTimePlanId() {
            return workTimePlanId;
        }

        /**
         * @param workTimePlanId 要设置的workTimePlanId
         */
        public void setWorkTimePlanId(String workTimePlanId) {
            this.workTimePlanId = workTimePlanId;
        }

        /**
         * @return the stateIds
         */
        public List<String> getStateIds() {
            return stateIds;
        }

        /**
         * @param stateIds 要设置的stateIds
         */
        public void setStateIds(List<String> stateIds) {
            this.stateIds = stateIds;
        }

        /**
         * @return the stateCodes
         */
        public List<String> getStateCodes() {
            return stateCodes;
        }

        /**
         * @param stateCodes 要设置的stateCodes
         */
        public void setStateCodes(List<String> stateCodes) {
            this.stateCodes = stateCodes;
        }

        /**
         * @return the stopTimerWhileStateChanged
         */
        public boolean isStopTimerWhileStateChanged() {
            return StringUtils.equals("stop", timerOfStateChanged);
        }

        /**
         * @return the timerOfStateChanged
         */
        public String getTimerOfStateChanged() {
            return timerOfStateChanged;
        }

        /**
         * @param timerOfStateChanged 要设置的timerOfStateChanged
         */
        public void setTimerOfStateChanged(String timerOfStateChanged) {
            this.timerOfStateChanged = timerOfStateChanged;
        }

        /**
         * @return the enableAlarmDoing
         */
        public boolean isEnableAlarmDoing() {
            return enableAlarmDoing;
        }

        /**
         * @param enableAlarmDoing 要设置的enableAlarmDoing
         */
        public void setEnableAlarmDoing(boolean enableAlarmDoing) {
            this.enableAlarmDoing = enableAlarmDoing;
        }

        /**
         * @return the alarmTimeLimit
         */
        public int getAlarmTimeLimit() {
            return alarmTimeLimit;
        }

        /**
         * @param alarmTimeLimit 要设置的alarmTimeLimit
         */
        public void setAlarmTimeLimit(int alarmTimeLimit) {
            this.alarmTimeLimit = alarmTimeLimit;
        }

        /**
         * @return the alarmTimingMode
         */
        public String getAlarmTimingMode() {
            return alarmTimingMode;
        }

        /**
         * @param alarmTimingMode 要设置的alarmTimingMode
         */
        public void setAlarmTimingMode(String alarmTimingMode) {
            this.alarmTimingMode = alarmTimingMode;
        }

        /**
         * @return the alarmCount
         */
        public int getAlarmCount() {
            return alarmCount;
        }

        /**
         * @param alarmCount 要设置的alarmCount
         */
        public void setAlarmCount(int alarmCount) {
            this.alarmCount = alarmCount;
        }

        /**
         * @return the alarmMsgObjects
         */
        public List<String> getAlarmMsgObjects() {
            return alarmMsgObjects;
        }

        /**
         * @param alarmMsgObjects 要设置的alarmMsgObjects
         */
        public void setAlarmMsgObjects(List<String> alarmMsgObjects) {
            this.alarmMsgObjects = alarmMsgObjects;
        }

        /**
         * @return the alarmMsgOtherUsers
         */
        public String getAlarmMsgOtherUsers() {
            return alarmMsgOtherUsers;
        }

        /**
         * @param alarmMsgOtherUsers 要设置的alarmMsgOtherUsers
         */
        public void setAlarmMsgOtherUsers(String alarmMsgOtherUsers) {
            this.alarmMsgOtherUsers = alarmMsgOtherUsers;
        }

        /**
         * @return the alarmMsgTemplateId
         */
        public String getAlarmMsgTemplateId() {
            return alarmMsgTemplateId;
        }

        /**
         * @param alarmMsgTemplateId 要设置的alarmMsgTemplateId
         */
        public void setAlarmMsgTemplateId(String alarmMsgTemplateId) {
            this.alarmMsgTemplateId = alarmMsgTemplateId;
        }

        /**
         * @return the enableDueDoing
         */
        public boolean isEnableDueDoing() {
            return enableDueDoing;
        }

        /**
         * @param enableDueDoing 要设置的enableDueDoing
         */
        public void setEnableDueDoing(boolean enableDueDoing) {
            this.enableDueDoing = enableDueDoing;
        }

        /**
         * @return the dueMsgObjects
         */
        public List<String> getDueMsgObjects() {
            return dueMsgObjects;
        }

        /**
         * @param dueMsgObjects 要设置的dueMsgObjects
         */
        public void setDueMsgObjects(List<String> dueMsgObjects) {
            this.dueMsgObjects = dueMsgObjects;
        }

        /**
         * @return the dueMsgOtherUsers
         */
        public String getDueMsgOtherUsers() {
            return dueMsgOtherUsers;
        }

        /**
         * @param dueMsgOtherUsers 要设置的dueMsgOtherUsers
         */
        public void setDueMsgOtherUsers(String dueMsgOtherUsers) {
            this.dueMsgOtherUsers = dueMsgOtherUsers;
        }

        /**
         * @return the dueMsgTemplateId
         */
        public String getDueMsgTemplateId() {
            return dueMsgTemplateId;
        }

        /**
         * @param dueMsgTemplateId 要设置的dueMsgTemplateId
         */
        public void setDueMsgTemplateId(String dueMsgTemplateId) {
            this.dueMsgTemplateId = dueMsgTemplateId;
        }

        /**
         * @return the enableOverdueDoing
         */
        public boolean isEnableOverdueDoing() {
            return enableOverdueDoing;
        }

        /**
         * @param enableOverdueDoing 要设置的enableOverdueDoing
         */
        public void setEnableOverdueDoing(boolean enableOverdueDoing) {
            this.enableOverdueDoing = enableOverdueDoing;
        }

        /**
         * @return the overdueMsgObjects
         */
        public List<String> getOverdueMsgObjects() {
            return overdueMsgObjects;
        }

        /**
         * @param overdueMsgObjects 要设置的overdueMsgObjects
         */
        public void setOverdueMsgObjects(List<String> overdueMsgObjects) {
            this.overdueMsgObjects = overdueMsgObjects;
        }

        /**
         * @return the overdueMsgOtherUsers
         */
        public String getOverdueMsgOtherUsers() {
            return overdueMsgOtherUsers;
        }

        /**
         * @param overdueMsgOtherUsers 要设置的overdueMsgOtherUsers
         */
        public void setOverdueMsgOtherUsers(String overdueMsgOtherUsers) {
            this.overdueMsgOtherUsers = overdueMsgOtherUsers;
        }

        /**
         * @return the overdueMsgTemplateId
         */
        public String getOverdueMsgTemplateId() {
            return overdueMsgTemplateId;
        }

        /**
         * @param overdueMsgTemplateId 要设置的overdueMsgTemplateId
         */
        public void setOverdueMsgTemplateId(String overdueMsgTemplateId) {
            this.overdueMsgTemplateId = overdueMsgTemplateId;
        }

        /**
         * @return the overdueAction
         */
        public String getOverdueAction() {
            return overdueAction;
        }

        /**
         * @param overdueAction 要设置的overdueAction
         */
        public void setOverdueAction(String overdueAction) {
            this.overdueAction = overdueAction;
        }

        /**
         * @return the overdueGroovyScript
         */
        public String getOverdueGroovyScript() {
            return overdueGroovyScript;
        }

        /**
         * @param overdueGroovyScript 要设置的overdueGroovyScript
         */
        public void setOverdueGroovyScript(String overdueGroovyScript) {
            this.overdueGroovyScript = overdueGroovyScript;
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
    }

    /**
     * 业务办理单
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProcessFormConfig extends BaseObject {
        private static final long serialVersionUID = 6902459658300079278L;

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
        // 过程节点信息显示位置
        private String processNodePlaceHolder;
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
         * @return the processNodePlaceHolder
         */
        public String getProcessNodePlaceHolder() {
            return processNodePlaceHolder;
        }

        /**
         * @param processNodePlaceHolder 要设置的processNodePlaceHolder
         */
        public void setProcessNodePlaceHolder(String processNodePlaceHolder) {
            this.processNodePlaceHolder = processNodePlaceHolder;
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

}
