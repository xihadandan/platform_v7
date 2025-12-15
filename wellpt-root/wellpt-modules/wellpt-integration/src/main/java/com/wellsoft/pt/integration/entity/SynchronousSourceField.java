/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-9.1	ruanhg		2014-7-9		Create
 * </pre>
 * @date 2014-7-9
 */
@Entity
@Table(name = "is_synchronous_source_field")
@DynamicUpdate
@DynamicInsert
public class SynchronousSourceField extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3318065184204150427L;

    private String fieldCnName;

    private String fieldEnName;

    private String dataType;

    private Integer dataLength;

    private Boolean isUserUse;

    private String foreignKeyTable;

    @UnCloneable
    private SynchronousSourceTable synchronousSourceTable;

    public String getFieldCnName() {
        return fieldCnName;
    }

    public void setFieldCnName(String fieldCnName) {
        this.fieldCnName = fieldCnName;
    }

    public String getFieldEnName() {
        return fieldEnName;
    }

    public void setFieldEnName(String fieldEnName) {
        this.fieldEnName = fieldEnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_uuid", nullable = false)
    public SynchronousSourceTable getSynchronousSourceTable() {
        return synchronousSourceTable;
    }

    public void setSynchronousSourceTable(SynchronousSourceTable synchronousSourceTable) {
        this.synchronousSourceTable = synchronousSourceTable;
    }

    public Boolean getIsUserUse() {
        return isUserUse;
    }

    public void setIsUserUse(Boolean isUserUse) {
        this.isUserUse = isUserUse;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }

}
