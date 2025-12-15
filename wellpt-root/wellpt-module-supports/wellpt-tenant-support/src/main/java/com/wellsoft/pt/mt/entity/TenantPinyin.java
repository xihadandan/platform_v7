/*
 * @(#)2014-1-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
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
 * 2014-1-2.1	zhulh		2014-1-2		Create
 * </pre>
 * @date 2014-1-2
 */
@Entity
@CommonEntity
@Table(name = "mt_pinyin")
@DynamicUpdate
@DynamicInsert
public class TenantPinyin extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1058506165758323165L;

    // 实体类型，一般存储实体类简称
    private String type;

    // 实体UUID
    private String entityUuid;

    // 拼音
    private String pinyin;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the entityUuid
     */
    public String getEntityUuid() {
        return entityUuid;
    }

    /**
     * @param entityUuid 要设置的entityUuid
     */
    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    /**
     * @return the pinyin
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * @param pinyin 要设置的pinyin
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

}
