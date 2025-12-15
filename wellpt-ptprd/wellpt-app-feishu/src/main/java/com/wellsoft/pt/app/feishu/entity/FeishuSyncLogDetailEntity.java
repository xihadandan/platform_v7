package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * 飞书同步日志明细表实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_sync_log_detail")
@DynamicUpdate
@DynamicInsert
public class FeishuSyncLogDetailEntity extends SysEntity {
    /**
     * 同步日志uuid
     */
    private Long syncLogUuid;

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
     * 同步状态：1:成功、0:失败
     */
    private Integer syncStatus;

    /**
     * 错误信息
     */
    private String errorMessage;

    public Long getSyncLogUuid() {
        return syncLogUuid;
    }

    public void setSyncLogUuid(Long syncLogUuid) {
        this.syncLogUuid = syncLogUuid;
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

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
