/*
 * @(#)2018年3月29日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.MappedSuperclass;

/**
 * Description: 租户化的实体类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月29日.1	chenqiong		2018年3月29日		Create
 * </pre>
 * @date 2018年3月29日
 */
@MappedSuperclass
public abstract class TenantEntity extends IdEntity {

    public static final String SYSTEM_UNIT_ID = "systemUnitId";
    public static final String SYSTEM_UNIT_ID4DB = "SYSTEM_UNIT_ID";
    private static final long serialVersionUID = 8498694179441170788L;
    @ApiModelProperty("系统单位Id")
    private String systemUnitId;

    public TenantEntity() {
    }

    public TenantEntity(String uuid) {
        this.uuid = uuid;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

}
