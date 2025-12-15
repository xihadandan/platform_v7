/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyElement implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2255590560718380382L;

    private String categorySN;

    private String formID;

    private EqualFlowElement equalFlow;

    private String isFree;

    private String isActive;

    private String pcShowFlag;

    private String isMobileShow;
    /* add by zenghw 2021.06.09 begin */
    // 是否自动更新标题
    private String autoUpdateTitle;
    /* add by zenghw 2021.06.09 begin */
    private String remark;

    private String keepRuntimePermission;

    private String granularity;

    private String isOnlyOneGranularity;

    private String enableAccessPermissionProvider;

    private String accessPermissionProvider;

    private String onlyUseAccessPermissionProvider;

    private String isSendFile;

    private String isSendMsg;

    private String fileTemplate;

    private String printTemplate;

    private String printTemplateId;

    private String printTemplateUuid;

    private String listener;

    private String globalTaskListener;

    private String timerListener;

    // 加载的JS模块
    private String customJsModule;

    private List<UserUnitElement> bakUsers;

    private List<FlowStateElement> flowStates;

    private List<MessageTemplateElement> messageTemplates;

    private List<UserUnitElement> creators;

    private List<UserUnitElement> users;

    private List<UserUnitElement> monitors;

    private List<UserUnitElement> admins;

    private List<UnitElement> fileRecipients;

    private List<UnitElement> msgRecipients;

    private String dueTime;

    private String timeUnit;

    private List<UnitElement> beginDirections;

    private List<UnitElement> endDirections;

    // 是否使用系统默认组织，1是0否
    private String useDefaultOrg;
    // 使用的组织ID
    private String orgId;
    // 可用业务组织，none不可用，all全部业务组织，assign指定业务组织
    private String availableBizOrg;
    // 指定业务组织，多个以分号隔开
    private String bizOrgId;
    // 是否启用多组织，1是0否
    private String enableMultiOrg;
    // 使用的多组织ID，多个以分号隔开
    private String multiOrgId;
    // 使用的多组织
    private List<MultiOrgElement> multiOrgs;
    // 是否自动更新最新的组织版本，1是0否
    private String autoUpgradeOrgVersion;

    // 多职流转设置,flow_by_user_all_jobs以全部身份流转、flow_by_user_main_job以主身份流转、flow_by_user_select_job选择具体身份流转
    private String multiJobFlowType;

    // 获取不到主身份时，flow_by_user_select_job选择具体身份、flow_by_user_all_jobs以全部身份流转
    private String mainJobNotFoundFlowType;

    // 身份选择，single单选身份、multiple多选身份
    private String selectJobMode;

    // 发起流程时通过表单字段选择身份
    private boolean selectJobField;

    // 身份选择字段
    private String jobField;

    // 索引设置
    private String indexType;
    // 索引标题表达式
    private String indexTitleExpression;
    // 索引摘要表达式
    private String indexContentExpression;
    // 索引字段表达式
    private String indexRemarkExpression;

    /* lmw 2015-4-23 11:07 begin */
    private List<RecordElement> records = new ArrayList<RecordElement>(0);
    /* lmw 2015-4-23 11:07 end */

    /* add by huanglinchuan 2014.10.28 begin */
    private List<UserUnitElement> viewers;
    /* add by huanglinchuan 2014.10.28 end */

    /* add by zenghw 2021.05.17 begin */
    // 签署意见校验设置
    private List<OpinionCheckSetElement> opinionCheckSets;
    /* add by zenghw 2021.05.17 begin */
    // 事件脚本
    private List<ScriptElement> eventScripts;

    // 是否开启审批去重功能
    private boolean enabledAutoSubmit;

    // 审批去重规则
    private AutoSubmitRuleElement autoSubmitRule;

    public String getPcShowFlag() {
        return this.pcShowFlag;
    }

    public void setPcShowFlag(final String pcShowFlag) {
        this.pcShowFlag = pcShowFlag;
    }

    public String getAutoUpdateTitle() {
        return autoUpdateTitle;
    }

    @JsonIgnore
    public boolean getIsAutoUpdateTitle() {
        return "1".equals(autoUpdateTitle);
    }

    public void setAutoUpdateTitle(String autoUpdateTitle) {
        this.autoUpdateTitle = autoUpdateTitle;
    }

    public List<OpinionCheckSetElement> getOpinionCheckSets() {
        return opinionCheckSets;
    }

    public void setOpinionCheckSets(final List<OpinionCheckSetElement> opinionCheckSets) {
        this.opinionCheckSets = opinionCheckSets;
    }

    /**
     * @return the categorySN
     */
    public String getCategorySN() {
        return categorySN;
    }

    /**
     * @param categorySN 要设置的categorySN
     */
    public void setCategorySN(String categorySN) {
        this.categorySN = categorySN;
    }

    /**
     * @return the formID
     */
    public String getFormID() {
        return formID;
    }

    /**
     * @param formID 要设置的formID
     */
    public void setFormID(String formID) {
        this.formID = formID;
    }

    /**
     * @return the equalFlow
     */
    public EqualFlowElement getEqualFlow() {
        return equalFlow;
    }

    /**
     * @param equalFlow 要设置的equalFlow
     */
    public void setEqualFlow(EqualFlowElement equalFlow) {
        this.equalFlow = equalFlow;
    }

    /**
     * @return the isFree
     */
    public String getIsFree() {
        return isFree;
    }

    /**
     * @param isFree 要设置的isFree
     */
    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isFree() {
        return "1".equals(isFree);
    }

    /**
     * @return the isActive
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * @param isActive 要设置的isActive
     */
    public void setIsActive(String isActive) {
        this.isActive = isActive;
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
     * @return the keepRuntimePermission
     */
    public String getKeepRuntimePermission() {
        return keepRuntimePermission;
    }

    /**
     * @param keepRuntimePermission 要设置的keepRuntimePermission
     */
    public void setKeepRuntimePermission(String keepRuntimePermission) {
        this.keepRuntimePermission = keepRuntimePermission;
    }

    /**
     * @return the granularity
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * @param granularity 要设置的granularity
     */
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    /**
     * @return the isOnlyOneGranularity
     */
    public String getIsOnlyOneGranularity() {
        return isOnlyOneGranularity;
    }

    /**
     * @param isOnlyOneGranularity 要设置的isOnlyOneGranularity
     */
    public void setIsOnlyOneGranularity(String isOnlyOneGranularity) {
        this.isOnlyOneGranularity = isOnlyOneGranularity;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isOnlyOneGranularity() {
        return "1".equals(isOnlyOneGranularity);
    }

    /**
     * @return the enableAccessPermissionProvider
     */
    public String getEnableAccessPermissionProvider() {
        return enableAccessPermissionProvider;
    }

    /**
     * @param enableAccessPermissionProvider 要设置的enableAccessPermissionProvider
     */
    public void setEnableAccessPermissionProvider(String enableAccessPermissionProvider) {
        this.enableAccessPermissionProvider = enableAccessPermissionProvider;
    }

    /**
     * @return the accessPermissionProvider
     */
    public String getAccessPermissionProvider() {
        return accessPermissionProvider;
    }

    /**
     * @param accessPermissionProvider 要设置的accessPermissionProvider
     */
    public void setAccessPermissionProvider(String accessPermissionProvider) {
        this.accessPermissionProvider = accessPermissionProvider;
    }

    /**
     * @return the onlyUseAccessPermissionProvider
     */
    public String getOnlyUseAccessPermissionProvider() {
        return onlyUseAccessPermissionProvider;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsOnlyUseAccessPermissionProvider() {
        return "1".equals(enableAccessPermissionProvider) && "1".equals(onlyUseAccessPermissionProvider);
    }

    /**
     * @param onlyUseAccessPermissionProvider 要设置的onlyUseAccessPermissionProvider
     */
    public void setOnlyUseAccessPermissionProvider(String onlyUseAccessPermissionProvider) {
        this.onlyUseAccessPermissionProvider = onlyUseAccessPermissionProvider;
    }

    /**
     * @return the dueTime
     */
    public String getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the timeUnit
     */
    public String getTimeUnit() {
        return timeUnit;
    }

    /**
     * @param timeUnit 要设置的timeUnit
     */
    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * @return the isSendFile
     */
    public String getIsSendFile() {
        return isSendFile;
    }

    /**
     * @param isSendFile 要设置的isSendFile
     */
    public void setIsSendFile(String isSendFile) {
        this.isSendFile = isSendFile;
    }

    /**
     * @return the isSendMsg
     */
    public String getIsSendMsg() {
        return isSendMsg;
    }

    /**
     * @param isSendMsg 要设置的isSendMsg
     */
    public void setIsSendMsg(String isSendMsg) {
        this.isSendMsg = isSendMsg;
    }

    /**
     * @return the fileTemplate
     */
    public String getFileTemplate() {
        return fileTemplate;
    }

    /**
     * @param fileTemplate 要设置的fileTemplate
     */
    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    /**
     * @return the printTemplate
     */
    public String getPrintTemplate() {
        return printTemplate;
    }

    /**
     * @param printTemplate 要设置的printTemplate
     */
    public void setPrintTemplate(String printTemplate) {
        this.printTemplate = printTemplate;
    }

    /**
     * @return the printTemplateId
     */
    public String getPrintTemplateId() {
        return printTemplateId;
    }

    /**
     * @param printTemplateId 要设置的printTemplateId
     */
    public void setPrintTemplateId(String printTemplateId) {
        this.printTemplateId = printTemplateId;
    }

    /**
     * @return the printTemplateUuid
     */
    public String getPrintTemplateUuid() {
        return printTemplateUuid;
    }

    /**
     * @param printTemplateUuid 要设置的printTemplateUuid
     */
    public void setPrintTemplateUuid(String printTemplateUuid) {
        this.printTemplateUuid = printTemplateUuid;
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
     * @return the listener
     */
    @JsonIgnore
    public String[] getListeners() {
        if (StringUtils.isBlank(listener)) {
            return new String[0];
        }
        return StringUtils.split(listener, Separator.SEMICOLON.getValue());
    }

    /**
     * @return the globalTaskListener
     */
    public String getGlobalTaskListener() {
        return globalTaskListener;
    }

    /**
     * @param globalTaskListener 要设置的globalTaskListener
     */
    public void setGlobalTaskListener(String globalTaskListener) {
        this.globalTaskListener = globalTaskListener;
    }

    /**
     * @return
     */
    @JsonIgnore
    public String[] getGlobalTaskListeners() {
        if (StringUtils.isBlank(globalTaskListener)) {
            return new String[0];
        }
        return StringUtils.split(globalTaskListener, Separator.SEMICOLON.getValue());
    }

    /**
     * @return the timerListener
     */
    public String getTimerListener() {
        return timerListener;
    }

    /**
     * @param timerListener 要设置的timerListener
     */
    public void setTimerListener(String timerListener) {
        this.timerListener = timerListener;
    }

    /**
     * @return the customJsModule
     */
    public String getCustomJsModule() {
        return customJsModule;
    }

    /**
     * @param customJsModule 要设置的customJsModule
     */
    public void setCustomJsModule(String customJsModule) {
        this.customJsModule = customJsModule;
    }

    /**
     * @return the beginDirections
     */
    public List<UnitElement> getBeginDirections() {
        return beginDirections;
    }

    /**
     * @param beginDirections 要设置的beginDirections
     */
    public void setBeginDirections(List<UnitElement> beginDirections) {
        this.beginDirections = beginDirections;
    }

    /**
     * @return the endDirections
     */
    public List<UnitElement> getEndDirections() {
        return endDirections;
    }

    /**
     * @param endDirections 要设置的endDirections
     */
    public void setEndDirections(List<UnitElement> endDirections) {
        this.endDirections = endDirections;
    }

    /**
     * @return the bakUsers
     */
    public List<UserUnitElement> getBakUsers() {
        return bakUsers;
    }

    /**
     * @param bakUsers 要设置的bakUsers
     */
    public void setBakUsers(List<UserUnitElement> bakUsers) {
        this.bakUsers = bakUsers;
    }

    /**
     * @return the flowStates
     */
    public List<FlowStateElement> getFlowStates() {
        return flowStates;
    }

    /**
     * @param flowStates 要设置的flowStates
     */
    public void setFlowStates(List<FlowStateElement> flowStates) {
        this.flowStates = flowStates;
    }

    /**
     * @return the messageTemplates
     */
    public List<MessageTemplateElement> getMessageTemplates() {
        return messageTemplates;
    }

    /**
     * @param messageTemplates 要设置的messageTemplates
     */
    public void setMessageTemplates(List<MessageTemplateElement> messageTemplates) {
        this.messageTemplates = messageTemplates;
    }

    /**
     * @return the creators
     */
    public List<UserUnitElement> getCreators() {
        return creators;
    }

    /**
     * @param creators 要设置的creators
     */
    public void setCreators(List<UserUnitElement> creators) {
        this.creators = creators;
    }

    /**
     * @return the users
     */
    public List<UserUnitElement> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<UserUnitElement> users) {
        this.users = users;
    }

    /**
     * @return the monitors
     */
    public List<UserUnitElement> getMonitors() {
        return monitors;
    }

    /**
     * @param monitors 要设置的monitors
     */
    public void setMonitors(List<UserUnitElement> monitors) {
        this.monitors = monitors;
    }

    /**
     * @return the admins
     */
    public List<UserUnitElement> getAdmins() {
        return admins;
    }

    /**
     * @param admins 要设置的admins
     */
    public void setAdmins(List<UserUnitElement> admins) {
        this.admins = admins;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getUnitAdminValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : admins) {
            // 1 组织选择框
            if (Integer.valueOf(1).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getFormFieldAdminValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : admins) {
            // 2 表单域
            if (Integer.valueOf(2).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getOptionAdminValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : admins) {
            // 8 参与者
            if (Integer.valueOf(8).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return the fileRecipients
     */
    public List<UnitElement> getFileRecipients() {
        return fileRecipients;
    }

    /**
     * @param fileRecipients 要设置的fileRecipients
     */
    public void setFileRecipients(List<UnitElement> fileRecipients) {
        this.fileRecipients = fileRecipients;
    }

    /**
     * @return the msgRecipients
     */
    public List<UnitElement> getMsgRecipients() {
        return msgRecipients;
    }

    /**
     * @param msgRecipients 要设置的msgRecipients
     */
    public void setMsgRecipients(List<UnitElement> msgRecipients) {
        this.msgRecipients = msgRecipients;
    }

    /* lmw 2015-4-23 11:06 begin */
    public List<RecordElement> getRecords() {
        return records;
    }

    public void setRecords(List<RecordElement> records) {
        this.records = records;
    }

    /* lmw 2015-4-23 11:06 end */

    /**
     * @return the useDefaultOrg
     */
    public String getUseDefaultOrg() {
        return useDefaultOrg;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsUseDefaultOrg() {
        return StringUtils.equals(useDefaultOrg, "1");
    }

    /**
     * @param useDefaultOrg 要设置的useDefaultOrg
     */
    public void setUseDefaultOrg(String useDefaultOrg) {
        this.useDefaultOrg = useDefaultOrg;
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the availableBizOrg
     */
    public String getAvailableBizOrg() {
        return availableBizOrg;
    }

    /**
     * @param availableBizOrg 要设置的availableBizOrg
     */
    public void setAvailableBizOrg(String availableBizOrg) {
        this.availableBizOrg = availableBizOrg;
    }

    /**
     * @return the bizOrgId
     */
    public String getBizOrgId() {
        return bizOrgId;
    }

    /**
     * @param bizOrgId 要设置的bizOrgId
     */
    public void setBizOrgId(String bizOrgId) {
        this.bizOrgId = bizOrgId;
    }

    /**
     * @return the enableMultiOrgVersion
     */
    public String getEnableMultiOrg() {
        return enableMultiOrg;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnableMultiOrg() {
        return StringUtils.equals(enableMultiOrg, "1");
    }

    /**
     * @param enableMultiOrg 要设置的enableMultiOrg
     */
    public void setEnableMultiOrg(String enableMultiOrg) {
        this.enableMultiOrg = enableMultiOrg;
    }

    /**
     * @return the multiOrgVersionId
     */
    public String getMultiOrgId() {
        return multiOrgId;
    }

    /**
     * @param multiOrgId 要设置的multiOrgId
     */
    public void setMultiOrgId(String multiOrgId) {
        this.multiOrgId = multiOrgId;
    }

    /**
     * @return the multiOrgs
     */
    public List<MultiOrgElement> getMultiOrgs() {
        return multiOrgs;
    }

    /**
     * @param multiOrgs 要设置的multiOrgs
     */
    public void setMultiOrgs(List<MultiOrgElement> multiOrgs) {
        this.multiOrgs = multiOrgs;
    }

    /**
     * @return the autoUpgradeOrgVersion
     */
    public String getAutoUpgradeOrgVersion() {
        return autoUpgradeOrgVersion;
    }

    @JsonIgnore
    public boolean getIsAutoUpgradeOrgVersion() {
        return StringUtils.equals(autoUpgradeOrgVersion, "1");
    }

    /**
     * @param autoUpgradeOrgVersion 要设置的autoUpgradeOrgVersion
     */
    public void setAutoUpgradeOrgVersion(String autoUpgradeOrgVersion) {
        this.autoUpgradeOrgVersion = autoUpgradeOrgVersion;
    }

    /**
     * @return
     */
    public List<UserUnitElement> getViewers() {
        return viewers;
    }

    /**
     * @param viewers
     */
    public void setViewers(List<UserUnitElement> viewers) {
        this.viewers = viewers;
    }

    /**
     * @return the eventScripts
     */
    public List<ScriptElement> getEventScripts() {
        return eventScripts;
    }

    /**
     * @param eventScripts 要设置的eventScripts
     */
    public void setEventScripts(List<ScriptElement> eventScripts) {
        this.eventScripts = eventScripts;
    }

    public String getIsMobileShow() {
        return isMobileShow;
    }

    public void setIsMobileShow(String mobileShow) {
        this.isMobileShow = mobileShow;
    }

    public String getMultiJobFlowType() {
        return multiJobFlowType;
    }

    public void setMultiJobFlowType(String multiJobFlowType) {
        this.multiJobFlowType = multiJobFlowType;
    }

    /**
     * @return the mainJobNotFoundFlowType
     */
    public String getMainJobNotFoundFlowType() {
        return mainJobNotFoundFlowType;
    }

    /**
     * @param mainJobNotFoundFlowType 要设置的mainJobNotFoundFlowType
     */
    public void setMainJobNotFoundFlowType(String mainJobNotFoundFlowType) {
        this.mainJobNotFoundFlowType = mainJobNotFoundFlowType;
    }

    /**
     * @return the selectJobMode
     */
    public String getSelectJobMode() {
        return selectJobMode;
    }

    /**
     * @param selectJobMode 要设置的selectJobMode
     */
    public void setSelectJobMode(String selectJobMode) {
        this.selectJobMode = selectJobMode;
    }

    /**
     * @return the selectJobField
     */
    public boolean isSelectJobField() {
        return selectJobField;
    }

    /**
     * @param selectJobField 要设置的selectJobField
     */
    public void setSelectJobField(boolean selectJobField) {
        this.selectJobField = selectJobField;
    }

    public String getJobField() {
        return jobField;
    }

    public void setJobField(String jobField) {
        this.jobField = jobField;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIndexTitleExpression() {
        return indexTitleExpression;
    }

    public void setIndexTitleExpression(String indexTitleExpression) {
        this.indexTitleExpression = indexTitleExpression;
    }

    public String getIndexContentExpression() {
        return indexContentExpression;
    }

    public void setIndexContentExpression(String indexContentExpression) {
        this.indexContentExpression = indexContentExpression;
    }

    /**
     * @return the indexRemarkExpression
     */
    public String getIndexRemarkExpression() {
        return indexRemarkExpression;
    }

    /**
     * @param indexRemarkExpression 要设置的indexRemarkExpression
     */
    public void setIndexRemarkExpression(String indexRemarkExpression) {
        this.indexRemarkExpression = indexRemarkExpression;
    }

    /**
     * @return the enabledAutoSubmit
     */
    public boolean isEnabledAutoSubmit() {
        return enabledAutoSubmit;
    }

    /**
     * @param enabledAutoSubmit 要设置的enabledAutoSubmit
     */
    public void setEnabledAutoSubmit(boolean enabledAutoSubmit) {
        this.enabledAutoSubmit = enabledAutoSubmit;
    }

    /**
     * @return the autoSubmitRule
     */
    public AutoSubmitRuleElement getAutoSubmitRule() {
        return autoSubmitRule;
    }

    /**
     * @param autoSubmitRule 要设置的autoSubmitRule
     */
    public void setAutoSubmitRule(AutoSubmitRuleElement autoSubmitRule) {
        this.autoSubmitRule = autoSubmitRule;
    }

}
