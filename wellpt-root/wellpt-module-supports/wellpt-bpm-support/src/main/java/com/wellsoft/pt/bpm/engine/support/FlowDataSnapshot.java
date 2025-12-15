/*
 * @(#)2019年9月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

/**
 * Description: 流程数据快照
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月5日.1	zhulh		2019年9月5日		Create
 * </pre>
 * @date 2019年9月5日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDataSnapshot extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4631437272423175439L;

    // 快照ID
    private String _id;

    // 快照UUID
    private String uuid;

    // 流程标题
    private String title;

    // 流程名称
    private String flowName;

    // 流程定义ID
    private String flowDefId;

    // 流程实例UUID
    private String flowInstUuid;

    // 环节实例UUID
    private String taskInstUuid;

    // 环节名称
    private String taskName;

    // 环节ID
    private String taskId;

    // 环节操作UUID
    private String taskOperationUuid;

    // 环节操作名称
    private String actionName;

    // 环节操作类型
    private String actionType;

    // 表单formUuid
    private String formUuid;

    // 表单dataUuid
    private String dataUuid;

    // 表单数据JSON
    private String formDatas;

    // 创建人ID
    private String createUserId;

    // 创建人名称
    private String createUserName;

    // 创建时间
    private Date createTime;

    // 归属系统
    private String system;
    // 归属租户
    private String tenant;

    /**
     * @return the _id
     */
    public String get_id() {
        return _id;
    }

    /**
     * @param _id 要设置的_id
     */
    public void set_id(String _id) {
        this._id = _id;
    }

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
     * 获取流程标题
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置流程标题
     *
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * 获取流程实例UUID
     *
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * 设置流程实例UUID
     *
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * 获取环节实例UUID
     *
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * 设置环节实例UUID
     *
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取环节操作UUID
     *
     * @return the taskOperationUuid
     */
    public String getTaskOperationUuid() {
        return taskOperationUuid;
    }

    /**
     * 设置环节操作UUID
     *
     * @param taskOperationUuid 要设置的taskOperationUuid
     */
    public void setTaskOperationUuid(String taskOperationUuid) {
        this.taskOperationUuid = taskOperationUuid;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName 要设置的actionName
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
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
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the formDatas
     */
    public String getFormDatas() {
        return formDatas;
    }

    /**
     * @param formDatas 要设置的formDatas
     */
    public void setFormDatas(String formDatas) {
        this.formDatas = formDatas;
    }

    /**
     * 获取创建人ID
     *
     * @return the createUserId
     */
    public String getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置创建人ID
     *
     * @param createUserId 要设置的createUserId
     */
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 获取创建人名称
     *
     * @return the createUserName
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 设置创建人名称
     *
     * @param createUserName 要设置的createUserName
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    /**
     * 获取创建时间
     *
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
