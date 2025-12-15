/*
 * @(#)2017年4月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mobile.bean;

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
 * 2017年4月14日.1	zhulh		2017年4月14日		Create
 * </pre>
 * @date 2017年4月14日
 */
public class MobileAppNav implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -771761169234224229L;

    private String name;
    private String uuid;
    private String parentUuid;
    private String uri;
    private String resources;

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
     * @return the parentUuid
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri 要设置的uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the resources
     */
    public String getResources() {
        return resources;
    }

    /**
     * @param resources 要设置的resources
     */
    public void setResources(String resources) {
        this.resources = resources;
    }

}
