/*
 * @(#)2013-6-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.generator.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-23.1	rzhu		2013-6-23		Create
 * </pre>
 * @date 2013-6-23
 */
@Entity
@Table(name = "cd_id_generator")
@DynamicUpdate
@DynamicInsert
public class IdGenerator extends IdEntity {
    private static final long serialVersionUID = 3723895529914963601L;

    private String entityClassName;

    private Long pointer;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the entityClassName
     */
    public String getEntityClassName() {
        return entityClassName;
    }

    /**
     * @param entityClassName 要设置的entityClassName
     */
    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    /**
     * @return the pointer
     */
    public Long getPointer() {
        return pointer;
    }

    /**
     * @param pointer 要设置的pointer
     */
    public void setPointer(Long pointer) {
        this.pointer = pointer;
    }

}
