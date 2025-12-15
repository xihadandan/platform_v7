/*
 * @(#)2013-5-10 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
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
 * 2013-5-10.1	zhulh		2013-5-10		Create
 * </pre>
 * @date 2013-5-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewFlowElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8011711862613511292L;

    // 新流程ID
    private String id;
    // 流程名称
    private String name;
    // 流程ID
    private String value;
    // 流程标签
    private String label;
    // 分发条件
    private String conditions;
    // 分发粒度，dept部门、job职位、user人员、bizItem业务项/部门、bizRole业务角色、user成员
    private String granularity;
    // 业务角色ID，多个以分号隔开
    private String bizRoleId;
    // 来源
    private String createWay;
    // 表单
    private String createInstanceFormId;
    // 是否主表的创建方式
    private String isMainFormCreateWay;
    // 字段
    private String taskUsers;
    // 创建方式
    private String createInstanceWay;
    // 按从表行分批次生成实例
    private String createInstanceBatch;

    private String interfaceName;
    // 接口实现
    private String interfaceValue;

    private String isMajor;

    private String isMerge;

    private String isWait;

    private String isShare;

    private String notifyDoing;

    private String toTaskName;

    private String toTaskId;

    // 子流程标题设置（default：默认；custom：自定义）
    private String title;
    // 子流程标题表达式
    private String titleExpression;

    // 拷贝信息
    private String copyBotRuleId;
    // 实时同步
    private String syncBotRuleId;
    // 办结时反馈
    private String returnWithOver;
    // 指定反馈流向
    private String returnWithDirection;
    // 反馈流向
    private String returnDirectionId;
    // 反馈信息
    private String returnBotRuleId;

    private String copyFields;

    private String returnOverrideFields;

    private String returnAdditionFields;

    /* add by huanglinchuan 2014.10.24 begin */

    private String subformId;

    /* add by huanglinchuan 2014.10.24 end */

    // 办理进度显示位置，1同主流程显示位置,其他指定显示位置
    private String undertakeSituationPlaceHolder;
    // 信息分发显示位置，1同主流程显示位置,其他指定显示位置
    private String infoDistributionPlaceHolder;
    // 操作记录显示位置，1同主流程显示位置,其他指定显示位置
    private String operationRecordPlaceHolder;

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();


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

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label 要设置的label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the conditions
     */
    public String getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
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
     * @return the bizRoleId
     */
    public String getBizRoleId() {
        return bizRoleId;
    }

    /**
     * @param bizRoleId 要设置的bizRoleId
     */
    public void setBizRoleId(String bizRoleId) {
        this.bizRoleId = bizRoleId;
    }

    /**
     * @return the createWay
     */
    public String getCreateWay() {
        return createWay;
    }

    /**
     * @param createWay 要设置的createWay
     */
    public void setCreateWay(String createWay) {
        this.createWay = createWay;
    }

    /**
     * @return the createInstanceFormId
     */
    public String getCreateInstanceFormId() {
        return createInstanceFormId;
    }

    /**
     * @param createInstanceFormId 要设置的createInstanceFormId
     */
    public void setCreateInstanceFormId(String createInstanceFormId) {
        this.createInstanceFormId = createInstanceFormId;
    }

    /**
     * @return the isMainFormCreateWay
     */
    public String getIsMainFormCreateWay() {
        return isMainFormCreateWay;
    }

    /**
     * @param isMainFormCreateWay 要设置的isMainFormCreateWay
     */
    public void setIsMainFormCreateWay(String isMainFormCreateWay) {
        this.isMainFormCreateWay = isMainFormCreateWay;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isMainFormCreateWay() {
        return "1".equals(isMainFormCreateWay);
    }

    /**
     * @return the taskUsers
     */
    public String getTaskUsers() {
        return taskUsers;
    }

    /**
     * @param taskUsers 要设置的taskUsers
     */
    public void setTaskUsers(String taskUsers) {
        this.taskUsers = taskUsers;
    }

    public String getCreateInstanceWay() {
        return createInstanceWay;
    }

    public void setCreateInstanceWay(String createInstanceWay) {
        this.createInstanceWay = createInstanceWay;
    }

    /**
     * @return the createInstanceBatch
     */
    public String getCreateInstanceBatch() {
        return createInstanceBatch;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsCreateInstanceBatch() {
        return "1".equals(createInstanceBatch);
    }

    /**
     * @param createInstanceBatch 要设置的createInstanceBatch
     */
    public void setCreateInstanceBatch(String createInstanceBatch) {
        this.createInstanceBatch = createInstanceBatch;
    }

    /**
     * @return the interfaceName
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * @param interfaceName 要设置的interfaceName
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * @return the interfaceValue
     */
    public String getInterfaceValue() {
        return interfaceValue;
    }

    /**
     * @param interfaceValue 要设置的interfaceValue
     */
    public void setInterfaceValue(String interfaceValue) {
        this.interfaceValue = interfaceValue;
    }

    /**
     * @return the isMajor
     */
    public String getIsMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setIsMajor(String isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * 是否主办
     *
     * @return
     */
    @JsonIgnore
    public boolean isMajor() {
        return "1".equals(isMajor);
    }

    /**
     * @return the isMerge
     */
    public String getIsMerge() {
        return isMerge;
    }

    /**
     * @param isMerge 要设置的isMerge
     */
    public void setIsMerge(String isMerge) {
        this.isMerge = isMerge;
    }

    /**
     * 是否合并
     *
     * @return
     */
    @JsonIgnore
    public boolean isMerge() {
        return "1".equals(isMerge);
    }

    /**
     * @return the isWait
     */
    public String getIsWait() {
        return isWait;
    }

    /**
     * @param isWait 要设置的isWait
     */
    public void setIsWait(String isWait) {
        this.isWait = isWait;
    }

    /**
     * 是否等待
     *
     * @return
     */
    @JsonIgnore
    public boolean isWait() {
        return "1".equals(isWait);
    }

    /**
     * @return the isShare
     */
    public String getIsShare() {
        return isShare;
    }

    /**
     * @param isShare 要设置的isShare
     */
    public void setIsShare(String isShare) {
        this.isShare = isShare;
    }

    /**
     * 是否共享
     *
     * @return
     */
    @JsonIgnore
    public boolean isShare() {
        return "1".equals(isShare);
    }

    /**
     * @return the notifyDoing
     */
    public String getNotifyDoing() {
        return notifyDoing;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsNotifyDoing() {
        return "1".equals(notifyDoing);
    }

    /**
     * @param notifyDoing 要设置的notifyDoing
     */
    public void setNotifyDoing(String notifyDoing) {
        this.notifyDoing = notifyDoing;
    }

    /**
     * @return the toTaskName
     */
    public String getToTaskName() {
        return toTaskName;
    }

    /**
     * @param toTaskName 要设置的toTaskName
     */
    public void setToTaskName(String toTaskName) {
        this.toTaskName = toTaskName;
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
     * @return the copyBotRuleId
     */
    public String getCopyBotRuleId() {
        return copyBotRuleId;
    }

    /**
     * @param copyBotRuleId 要设置的copyBotRuleId
     */
    public void setCopyBotRuleId(String copyBotRuleId) {
        this.copyBotRuleId = copyBotRuleId;
    }

    /**
     * @return the syncBotRuleId
     */
    public String getSyncBotRuleId() {
        return syncBotRuleId;
    }

    /**
     * @param syncBotRuleId 要设置的syncBotRuleId
     */
    public void setSyncBotRuleId(String syncBotRuleId) {
        this.syncBotRuleId = syncBotRuleId;
    }

    /**
     * @return the returnWithOver
     */
    public String getReturnWithOver() {
        return returnWithOver;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsReturnWithOver() {
        return "1".equals(returnWithOver);
    }

    /**
     * @param returnWithOver 要设置的returnWithOver
     */
    public void setReturnWithOver(String returnWithOver) {
        this.returnWithOver = returnWithOver;
    }

    /**
     * @return the returnWithDirection
     */
    public String getReturnWithDirection() {
        return returnWithDirection;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsReturnWithDirection() {
        return "1".equals(returnWithDirection);
    }

    /**
     * @param returnWithDirection 要设置的returnWithDirection
     */
    public void setReturnWithDirection(String returnWithDirection) {
        this.returnWithDirection = returnWithDirection;
    }

    /**
     * @return the returnDirectionId
     */
    public String getReturnDirectionId() {
        return returnDirectionId;
    }

    /**
     * @param returnDirectionId 要设置的returnDirectionId
     */
    public void setReturnDirectionId(String returnDirectionId) {
        this.returnDirectionId = returnDirectionId;
    }

    /**
     * @return the returnBotRuleId
     */
    public String getReturnBotRuleId() {
        return returnBotRuleId;
    }

    /**
     * @param returnBotRuleId 要设置的returnBotRuleId
     */
    public void setReturnBotRuleId(String returnBotRuleId) {
        this.returnBotRuleId = returnBotRuleId;
    }

    /**
     * @return the copyFields
     */
    public String getCopyFields() {
        return copyFields;
    }

    /**
     * @param copyFields 要设置的copyFields
     */
    public void setCopyFields(String copyFields) {
        this.copyFields = copyFields;
    }

    /**
     * @return the returnOverrideFields
     */
    public String getReturnOverrideFields() {
        return returnOverrideFields;
    }

    /**
     * @param returnOverrideFields 要设置的returnOverrideFields
     */
    public void setReturnOverrideFields(String returnOverrideFields) {
        this.returnOverrideFields = returnOverrideFields;
    }

    /**
     * @return the returnAdditionFields
     */
    public String getReturnAdditionFields() {
        return returnAdditionFields;
    }

    /**
     * @param returnAdditionFields 要设置的returnAdditionFields
     */
    public void setReturnAdditionFields(String returnAdditionFields) {
        this.returnAdditionFields = returnAdditionFields;
    }

    /**
     * @return the subformId
     */
    public String getSubformId() {
        return subformId;
    }

    /**
     * @param subformId 要设置的subformId
     */
    public void setSubformId(String subformId) {
        this.subformId = subformId;
    }

    /**
     * @return the undertakeSituationPlaceHolder
     */
    public String getUndertakeSituationPlaceHolder() {
        return undertakeSituationPlaceHolder;
    }

    /**
     * @param undertakeSituationPlaceHolder 要设置的undertakeSituationPlaceHolder
     */
    public void setUndertakeSituationPlaceHolder(String undertakeSituationPlaceHolder) {
        this.undertakeSituationPlaceHolder = undertakeSituationPlaceHolder;
    }

    /**
     * @return the infoDistributionPlaceHolder
     */
    public String getInfoDistributionPlaceHolder() {
        return infoDistributionPlaceHolder;
    }

    /**
     * @param infoDistributionPlaceHolder 要设置的infoDistributionPlaceHolder
     */
    public void setInfoDistributionPlaceHolder(String infoDistributionPlaceHolder) {
        this.infoDistributionPlaceHolder = infoDistributionPlaceHolder;
    }

    /**
     * @return the operationRecordPlaceHolder
     */
    public String getOperationRecordPlaceHolder() {
        return operationRecordPlaceHolder;
    }

    /**
     * @param operationRecordPlaceHolder 要设置的operationRecordPlaceHolder
     */
    public void setOperationRecordPlaceHolder(String operationRecordPlaceHolder) {
        this.operationRecordPlaceHolder = operationRecordPlaceHolder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleExpression() {
        return titleExpression;
    }

    public void setTitleExpression(String titleExpression) {
        this.titleExpression = titleExpression;
    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }
}
