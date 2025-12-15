/*
 * @(#)2014-2-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import org.apache.commons.lang.StringUtils;

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
 * 2014-2-25.1	zhulh		2014-2-25		Create
 * </pre>
 * @date 2014-2-25
 */
public class NewFlow extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5859923780669206480L;

    // 新流程ID
    private String id;
    // 流程名称
    private String flowName;
    // 流程ID
    private String flowId;
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
    private boolean isMainFormCreateWay;
    // 字段
    private String taskUsers;
    // 创建方式
    private String createInstanceWay;
    // 按从表行分批次生成实例
    private boolean createInstanceBatch;
    // 新流程创建流程实例的自定义接口名称
    private String interfaceName;
    // 新流程创建流程实例的自定义接口
    private String interfaceValue;
    // 新流程是否主办
    private boolean isMajor;
    // 新流程是否合并(子流程数据合并)
    private boolean isMerge;
    // 新流程是否等待(主子流程同异步及回调关系)
    private boolean isWait;
    // 新流程是否共享
    private boolean isShare;
    // 新流程办结通知其他子流程在办人员
    private boolean notifyDoing;
    // 新流程提交环节名称
    private String toTaskName;
    // 新流程提交环节ID
    private String toTaskId;
    // 拷贝信息
    private String copyBotRuleId;
    // 实时同步
    private String syncBotRuleId;
    // 办结时反馈
    private boolean returnWithOver;
    // 指定反馈流向
    private boolean returnWithDirection;
    // 反馈流向
    private String returnDirectionId;
    // 反馈信息
    private String returnBotRuleId;
    // 新流程要拷贝的字段
    private String copyFields;
    // 新流程要返回复盖的字段
    private String returnOverrideFields;
    // 新流程要返回附加的字段
    private String returnAdditionFields;

    private int sortOrder;

    private String subformId;

    // 办理进度显示位置，1同主流程显示位置,其他指定显示位置
    private String undertakeSituationPlaceHolder;
    // 信息分发显示位置，1同主流程显示位置,其他指定显示位置
    private String infoDistributionPlaceHolder;
    // 操作记录显示位置，1同主流程显示位置,其他指定显示位置
    private String operationRecordPlaceHolder;

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();

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
     * @return the flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName 要设置的flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return the flowId
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * @param flowId 要设置的flowId
     */
    public void setFlowId(String flowId) {
        this.flowId = flowId;
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
     * @return
     */
    public String getSidGranularity() {
        String sidGranularity = SidGranularity.USER;
        if (StringUtils.isBlank(this.granularity)) {
            return sidGranularity;
        }

        switch (this.granularity) {
            case "dept":
                sidGranularity = SidGranularity.DEPARTMENT;
                break;
            case "job":
                sidGranularity = SidGranularity.JOB;
                break;
            default:
                sidGranularity = SidGranularity.USER;
                break;
        }
        return sidGranularity;
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
    public boolean isMainFormCreateWay() {
        return isMainFormCreateWay;
    }

    /**
     * @param isMainFormCreateWay 要设置的isMainFormCreateWay
     */
    public void setMainFormCreateWay(boolean isMainFormCreateWay) {
        this.isMainFormCreateWay = isMainFormCreateWay;
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

    /**
     * @return the createInstanceWay
     */
    public String getCreateInstanceWay() {
        return createInstanceWay;
    }

    /**
     * @param createInstanceWay 要设置的createInstanceWay
     */
    public void setCreateInstanceWay(String createInstanceWay) {
        this.createInstanceWay = createInstanceWay;
    }

    /**
     * @return the createInstanceBatch
     */
    public boolean isCreateInstanceBatch() {
        return createInstanceBatch;
    }

    /**
     * @param createInstanceBatch 要设置的createInstanceBatch
     */
    public void setCreateInstanceBatch(boolean createInstanceBatch) {
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
    public boolean isMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setMajor(boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * 替换为isReturnWithOver
     *
     * @return the isMerge
     */
    @Deprecated
    public boolean isMerge() {
        return isMerge;
    }

    /**
     * @param isMerge 要设置的isMerge
     */
    public void setMerge(boolean isMerge) {
        this.isMerge = isMerge;
    }

    /**
     * @return the isWait
     */
    public boolean isWait() {
        return isWait;
    }

    /**
     * @param isWait 要设置的isWait
     */
    public void setWait(boolean isWait) {
        this.isWait = isWait;
    }

    /**
     * @return the isShare
     */
    public boolean isShare() {
        return isShare;
    }

    /**
     * @param isShare 要设置的isShare
     */
    public void setShare(boolean isShare) {
        this.isShare = isShare;
    }

    /**
     * @return the notifyDoing
     */
    public boolean isNotifyDoing() {
        return notifyDoing;
    }

    /**
     * @param notifyDoing 要设置的notifyDoing
     */
    public void setNotifyDoing(boolean notifyDoing) {
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
    public boolean isReturnWithOver() {
        return returnWithOver;
    }

    /**
     * @param returnWithOver 要设置的returnWithOver
     */
    public void setReturnWithOver(boolean returnWithOver) {
        this.returnWithOver = returnWithOver;
    }

    /**
     * @return the returnWithDirection
     */
    public boolean isReturnWithDirection() {
        return returnWithDirection;
    }

    /**
     * @param returnWithDirection 要设置的returnWithDirection
     */
    public void setReturnWithDirection(boolean returnWithDirection) {
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
     * @return the sortOrder
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
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
     *
     */
    public boolean isCustomUndertakeSituationPlaceHolder() {
        return StringUtils.isNotBlank(undertakeSituationPlaceHolder) && !"1".equals(undertakeSituationPlaceHolder);
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
     *
     */
    public boolean isCustomInfoDistributionPlaceHolder() {
        return StringUtils.isNotBlank(infoDistributionPlaceHolder) && !"1".equals(infoDistributionPlaceHolder);
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

    /**
     *
     */
    public boolean isCustomOperationRecordPlaceHolder() {
        return StringUtils.isNotBlank(operationRecordPlaceHolder) && !"1".equals(operationRecordPlaceHolder);
    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }
}
