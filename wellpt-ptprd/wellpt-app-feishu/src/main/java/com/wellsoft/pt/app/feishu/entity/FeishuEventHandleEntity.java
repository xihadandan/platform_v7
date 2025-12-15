package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * 飞书事件处理表实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_event_handle")
@DynamicUpdate
@DynamicInsert
public class FeishuEventHandleEntity extends Entity {

    /**
     * 飞书事件表uuid
     */
    private Long feishuEventUuid;

    /**
     * 飞书配置信息uuid
     */
    private Long feishuConfigUuid;

    /**
     * 处理状态：0：待处理，1：处理完成，2：处理失败
     */
    private String handleStatus;

    /**
     * 目标操作类型：新增、修改、删除
     */
    private String targetOperationType;

    /**
     * 目标表：部门表：feishu_dept、用户表：feishu_user
     */
    private String targetTable;

    /**
     * 目标表uuid
     */
    private Long targetUuid;

    /**
     * 目标表uuid对应名称
     */
    private String targetName;

    /**
     * 目标表原数据
     */
    private String targetData;

    /**
     * 错误信息
     */
    private String errorMessage;

    public Long getFeishuEventUuid() {
        return feishuEventUuid;
    }

    public void setFeishuEventUuid(Long feishuEventUuid) {
        this.feishuEventUuid = feishuEventUuid;
    }

    public Long getFeishuConfigUuid() {
        return feishuConfigUuid;
    }

    public void setFeishuConfigUuid(Long feishuConfigUuid) {
        this.feishuConfigUuid = feishuConfigUuid;
    }

    public String getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(String handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getTargetOperationType() {
        return targetOperationType;
    }

    public void setTargetOperationType(String targetOperationType) {
        this.targetOperationType = targetOperationType;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Long getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(Long targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetData() {
        return targetData;
    }

    public void setTargetData(String targetData) {
        this.targetData = targetData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
