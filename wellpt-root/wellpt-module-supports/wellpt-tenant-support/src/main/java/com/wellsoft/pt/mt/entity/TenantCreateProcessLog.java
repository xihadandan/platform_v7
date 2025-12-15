/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 租户创建日记
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2016-03-15.1	linz		2016-03-15		Create
 * </pre>
 * @date 2016-03-15
 */
@Entity
@CommonEntity
@Table(name = "MT_TENANT_CREATE_PROCESS_LOG")
@DynamicUpdate
@DynamicInsert
public class TenantCreateProcessLog extends IdEntity {
    public static final int SUCCESS = 1;
    public static final int COMPLETE = 2;
    public static final int FAIL = 0;
    private static final long serialVersionUID = 2058469369113091726L;
    /**
     * 过程步骤名称
     */
    private String name;
    /**
     * 租户ID
     */
    private String tenantUuid;
    /**
     * 排序号，从1开始递增
     */
    private int sortOrder;
    /**
     * 0失败，1成功
     */
    private int status;
    /**
     * 租户密码
     */
    private String content;
    /**
     * 创建批次号，第一次创建为1，以后从错误中创建的依次递增1
     */
    private int batchNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(int batchNo) {
        this.batchNo = batchNo;
    }
}
