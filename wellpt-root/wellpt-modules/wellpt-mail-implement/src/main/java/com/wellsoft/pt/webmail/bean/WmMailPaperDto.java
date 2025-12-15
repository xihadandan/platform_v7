/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import com.wellsoft.pt.webmail.enums.WmMailPaperBackgroundPosition;
import com.wellsoft.pt.webmail.enums.WmMailPaperBackgroundRepeat;

import java.io.Serializable;

/**
 * Description: 信纸dto
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月7日.1	chenqiong		2018年3月7日		Create
 * </pre>
 * @date 2018年3月7日
 */
public class WmMailPaperDto implements Serializable {

    private static final long serialVersionUID = 8739875123668757921L;

    private Boolean isDefault;

    private String backgroundImgUrl;

    private String backgroundColor;

    private WmMailPaperBackgroundRepeat backgroundRepeat;

    private WmMailPaperBackgroundPosition backgroundPosition;

    private String uuid;

    private String userId;

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the backgroundImgUrl
     */
    public String getBackgroundImgUrl() {
        return backgroundImgUrl;
    }

    /**
     * @param backgroundImgUrl 要设置的backgroundImgUrl
     */
    public void setBackgroundImgUrl(String backgroundImgUrl) {
        this.backgroundImgUrl = backgroundImgUrl;
    }

    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor 要设置的backgroundColor
     */
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the backgroundRepeat
     */
    public WmMailPaperBackgroundRepeat getBackgroundRepeat() {
        return backgroundRepeat;
    }

    /**
     * @param backgroundRepeat 要设置的backgroundRepeat
     */
    public void setBackgroundRepeat(WmMailPaperBackgroundRepeat backgroundRepeat) {
        this.backgroundRepeat = backgroundRepeat;
    }

    /**
     * @return the backgroundPosition
     */
    public WmMailPaperBackgroundPosition getBackgroundPosition() {
        return backgroundPosition;
    }

    /**
     * @param backgroundPosition 要设置的backgroundPosition
     */
    public void setBackgroundPosition(WmMailPaperBackgroundPosition backgroundPosition) {
        this.backgroundPosition = backgroundPosition;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
