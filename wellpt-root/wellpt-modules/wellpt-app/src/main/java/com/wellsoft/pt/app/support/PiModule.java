/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月13日.1	zhulh		2016年8月13日		Create
 * </pre>
 * @date 2016年8月13日
 */
public class PiModule implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1370850764066907431L;

    private String name;

    private String id;

    private String title;

    // 集成信息UUID
    private String piUuid;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return StringUtils.isBlank(title) ? name : title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the piUuid
     */
    public String getPiUuid() {
        return piUuid;
    }

    /**
     * @param piUuid 要设置的piUuid
     */
    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

}
