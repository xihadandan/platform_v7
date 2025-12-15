/*
 * @(#)Apr 16, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Apr 16, 2018.1	zhulh		Apr 16, 2018		Create
 * </pre>
 * @date Apr 16, 2018
 */
public class DmsTagData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4472651870782201846L;

    private String tagUuid;
    private String dataUuid;
    private String tagName;
    private String tagColor;

    /**
     * @return the tagUuid
     */
    public String getTagUuid() {
        return tagUuid;
    }

    /**
     * @param tagUuid 要设置的tagUuid
     */
    public void setTagUuid(String tagUuid) {
        this.tagUuid = tagUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

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

}
