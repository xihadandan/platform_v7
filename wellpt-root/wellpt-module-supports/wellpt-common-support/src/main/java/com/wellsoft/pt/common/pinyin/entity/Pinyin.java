/*
 * @(#)2013-8-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.pinyin.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 用户登录名、姓名拼音实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-3.1	zhulh		2013-8-3		Create
 * </pre>
 * @date 2013-8-3
 */
@Entity
@Table(name = "cd_pinyin")
@DynamicUpdate
@DynamicInsert
public class Pinyin extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8984298126862172001L;

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

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((entityUuid == null) ? 0 : entityUuid.hashCode());
        result = prime * result + ((pinyin == null) ? 0 : pinyin.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pinyin other = (Pinyin) obj;
        if (entityUuid == null) {
            if (other.entityUuid != null)
                return false;
        } else if (!entityUuid.equals(other.entityUuid))
            return false;
        if (pinyin == null) {
            if (other.pinyin != null)
                return false;
        } else if (!pinyin.equals(other.pinyin))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
