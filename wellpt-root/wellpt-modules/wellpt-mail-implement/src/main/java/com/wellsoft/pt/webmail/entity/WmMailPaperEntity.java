/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.webmail.enums.WmMailPaperBackgroundPosition;
import com.wellsoft.pt.webmail.enums.WmMailPaperBackgroundRepeat;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Description: 邮件信纸
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
@Table(name = "WM_MAIL_PAPER")
@DynamicUpdate
@DynamicInsert
public class WmMailPaperEntity extends IdEntity {

    private static final long serialVersionUID = -496795034127995371L;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 背景图片地址
     */
    private String backgroundImgUrl;

    /**
     * 背景颜色
     */
    private String backgroundColor;


    /**
     * 背景位置
     */
    private WmMailPaperBackgroundPosition backgroundPosition;

    /**
     * 背景重复
     */
    private WmMailPaperBackgroundRepeat backgroundRepeat;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 系统单位Id
     */
    private String systemUnitId;

    /**
     * 获取 是否默认
     *
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * 设置 是否默认
     *
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * 获取 背景图片地址
     *
     * @return the backgroundImgUrl
     */
    public String getBackgroundImgUrl() {
        return backgroundImgUrl;
    }

    /**
     * 设置 背景图片地址
     *
     * @param backgroundImgUrl 要设置的backgroundImgUrl
     */
    public void setBackgroundImgUrl(String backgroundImgUrl) {
        this.backgroundImgUrl = backgroundImgUrl;
    }

    /**
     * 获取 背景颜色
     *
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * 设置 背景颜色
     *
     * @param backgroundColor 要设置的backgroundColor
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

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
     * 获取 背景重复
     *
     * @return the backgroundRepeat
     */
    @Enumerated(value = EnumType.STRING)
    public WmMailPaperBackgroundRepeat getBackgroundRepeat() {
        return backgroundRepeat;
    }

    /**
     * 设置 背景重复
     *
     * @param backgroundRepeat 要设置的backgroundRepeat
     */
    public void setBackgroundRepeat(WmMailPaperBackgroundRepeat backgroundRepeat) {
        this.backgroundRepeat = backgroundRepeat;
    }

    /**
     * 获取 背景位置
     *
     * @return the backgroundPosition
     */
    @Enumerated(value = EnumType.STRING)
    public WmMailPaperBackgroundPosition getBackgroundPosition() {
        return backgroundPosition;
    }

    /**
     * 设置 背景位置
     *
     * @param backgroundPosition 要设置的backgroundPosition
     */
    public void setBackgroundPosition(WmMailPaperBackgroundPosition backgroundPosition) {
        this.backgroundPosition = backgroundPosition;
    }

}
