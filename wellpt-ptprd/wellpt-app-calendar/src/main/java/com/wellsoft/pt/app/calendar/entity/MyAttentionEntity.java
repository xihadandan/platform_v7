/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件标签
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "APP_MY_ATTENTION")
@DynamicUpdate
@DynamicInsert
public class MyAttentionEntity extends IdEntity {

    public static final String TYPE_USER = "U";
    public static final String TYPE_GROUP = "G";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7721588107815124544L;
    private String attentionObjId;
    private String attentionObjType;

    /**
     * @return the attentionObjId
     */
    public String getAttentionObjId() {
        return attentionObjId;
    }

    /**
     * @param attentionObjId 要设置的attentionObjId
     */
    public void setAttentionObjId(String attentionObjId) {
        this.attentionObjId = attentionObjId;
    }

    /**
     * @return the attentionObjType
     */
    public String getAttentionObjType() {
        return attentionObjType;
    }

    /**
     * @param attentionObjType 要设置的attentionObjType
     */
    public void setAttentionObjType(String attentionObjType) {
        this.attentionObjType = attentionObjType;
    }

}
