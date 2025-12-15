/*
 * @(#)2013-12-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	zhulh		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
@Entity
@CommonEntity
@Table(name = "mt_id_generator")
public class TenantIdGenerator implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2043889219150900297L;

    private String entityClassName;

    private Long pointer;

    /**
     * @return the entityClassName
     */
    @Id
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
