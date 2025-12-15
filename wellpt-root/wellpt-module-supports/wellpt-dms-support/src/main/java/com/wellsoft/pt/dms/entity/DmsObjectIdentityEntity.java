/*
 * @(#)Dec 27, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 27, 2017.1	zhulh		Dec 27, 2017		Create
 * </pre>
 * @date Dec 27, 2017
 */
@Entity
@Table(name = "DMS_OBJECT_IDENTITY")
@DynamicUpdate
@DynamicInsert
public class DmsObjectIdentityEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8208537051663049570L;

    // 对象数据标识
    private String objectIdIdentity;

    // 对象类型
    private String objectType;

    /**
     * @return the objectIdIdentity
     */
    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    /**
     * @param objectIdIdentity 要设置的objectIdIdentity
     */
    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType 要设置的objectType
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

}
