/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * Description: 邮件标签DTO
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
public class WmMailTagDto implements Serializable {

    private static final long serialVersionUID = 3463151858494057886L;

    private String uuid;

    private String tagName;

    private String tagColor;

    private Integer seq;

    /**
     * @return the tagName
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * @param tagName 要设置的tagName
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * @return the tagColor
     */
    public String getTagColor() {
        return tagColor;
    }

    /**
     * @param tagColor 要设置的tagColor
     */
    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    /**
     * @return the seq
     */
    public Integer getSeq() {
        return seq;
    }

    /**
     * @param seq 要设置的seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
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

}
