/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
@javax.persistence.Entity
@Table(name = "weixin_sync_log_detail")
@DynamicUpdate
@DynamicInsert
public class WeixinSyncLogDetailEntity extends SysEntity {

    // 同步日志uuid
    private Long syncLogUuid;

    // 目标操作类型：新增、修改、删除
    private String targetOperationType;

    // 目标表：部门表：weixin_dept、用户表：weixin_user
    private String targetTable;

    // 目标表uuid
    private Long targetUuid;

    // 目标表uuid对应名称
    private String targetName;

    // 目标表数据
    private String targetData;

    // 同步状态：1:成功、0:失败
    private Integer syncStatus;

    // 错误信息
    private String errorMessage;

    /**
     * @return the syncLogUuid
     */
    public Long getSyncLogUuid() {
        return syncLogUuid;
    }

    /**
     * @param syncLogUuid 要设置的syncLogUuid
     */
    public void setSyncLogUuid(Long syncLogUuid) {
        this.syncLogUuid = syncLogUuid;
    }

    /**
     * @return the targetOperationType
     */
    public String getTargetOperationType() {
        return targetOperationType;
    }

    /**
     * @param targetOperationType 要设置的targetOperationType
     */
    public void setTargetOperationType(String targetOperationType) {
        this.targetOperationType = targetOperationType;
    }

    /**
     * @return the targetTable
     */
    public String getTargetTable() {
        return targetTable;
    }

    /**
     * @param targetTable 要设置的targetTable
     */
    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    /**
     * @return the targetUuid
     */
    public Long getTargetUuid() {
        return targetUuid;
    }

    /**
     * @param targetUuid 要设置的targetUuid
     */
    public void setTargetUuid(Long targetUuid) {
        this.targetUuid = targetUuid;
    }

    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName 要设置的targetName
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * @return the targetData
     */
    public String getTargetData() {
        return targetData;
    }

    /**
     * @param targetData 要设置的targetData
     */
    public void setTargetData(String targetData) {
        this.targetData = targetData;
    }

    /**
     * @return the syncStatus
     */
    public Integer getSyncStatus() {
        return syncStatus;
    }

    /**
     * @param syncStatus 要设置的syncStatus
     */
    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage 要设置的errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
