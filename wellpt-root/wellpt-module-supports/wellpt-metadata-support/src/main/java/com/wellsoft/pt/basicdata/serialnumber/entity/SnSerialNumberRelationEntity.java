/*
 * @(#)7/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流水号关联表字段记录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/22/22.1	zhulh		7/22/22		Create
 * </pre>
 * @date 7/22/22
 */
@ApiModel("流水号关联表字段记录")
@Entity
@Table(name = "sn_serial_number_relation")
@DynamicUpdate
@DynamicInsert
public class SnSerialNumberRelationEntity extends TenantEntity {
    private static final long serialVersionUID = 7499763281817812105L;

    @ApiModelProperty("流水号定义ID")
    private String snId;

    @ApiModelProperty("对象类型：（1：数据库表）")
    private Integer objectType;

    @ApiModelProperty("对象名称")
    private String objectName;

    @ApiModelProperty("字段名")
    private String fieldName;

    /**
     * @return the snId
     */
    public String getSnId() {
        return snId;
    }

    /**
     * @param snId 要设置的snId
     */
    public void setSnId(String snId) {
        this.snId = snId;
    }

    /**
     * @return the objectType
     */
    public Integer getObjectType() {
        return objectType;
    }

    /**
     * @param objectType 要设置的objectType
     */
    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * @param objectName 要设置的objectName
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
