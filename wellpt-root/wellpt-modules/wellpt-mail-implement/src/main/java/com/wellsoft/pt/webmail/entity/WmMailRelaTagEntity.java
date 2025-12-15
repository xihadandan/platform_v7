/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件关联的标签
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
@Table(name = "WM_MAIL_RELA_TAG")
@DynamicUpdate
@DynamicInsert
public class WmMailRelaTagEntity extends IdEntity {

    private static final long serialVersionUID = 8060237996524711307L;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 邮件uuid
     */
    private String mailUuid;

    /**
     * 标签uuid
     */
    private String tagUuid;

    /**
     * 获取 系统单位Id
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置 系统单位Id
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * 获取 邮件uuid
     *
     * @return the mailUuid
     */
    public String getMailUuid() {
        return mailUuid;
    }

    /**
     * 设置 邮件uuid
     *
     * @param mailUuid 要设置的mailUuid
     */
    public void setMailUuid(String mailUuid) {
        this.mailUuid = mailUuid;
    }

    /**
     * 获取 标签uuid
     *
     * @return the tagUuid
     */
    public String getTagUuid() {
        return tagUuid;
    }

    /**
     * 设置 标签uuid
     *
     * @param tagUuid 要设置的tagUuid
     */
    public void setTagUuid(String tagUuid) {
        this.tagUuid = tagUuid;
    }

}
