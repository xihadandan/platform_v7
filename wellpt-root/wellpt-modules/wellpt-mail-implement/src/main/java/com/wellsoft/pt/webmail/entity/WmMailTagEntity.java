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
@Table(name = "WM_MAIL_TAG")
@DynamicUpdate
@DynamicInsert
public class WmMailTagEntity extends IdEntity {

    private static final long serialVersionUID = -7158162376042400324L;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签颜色
     */
    private String tagColor;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 获取 用户Id
     *
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置 用户Id
     *
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

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
     * 获取 标签名称
     *
     * @return the tagName
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 设置 标签名称
     *
     * @param tagName 要设置的tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 获取 标签颜色
     *
     * @return the tagColor
     */
    public String getTagColor() {
        return tagColor;
    }

    /**
     * 设置 标签颜色
     *
     * @param tagColor 要设置的tagColor
     */
    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    /**
     * 获取 排序号
     *
     * @return the seq
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * 设置 排序号
     *
     * @param seq 要设置的seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
    }

}
