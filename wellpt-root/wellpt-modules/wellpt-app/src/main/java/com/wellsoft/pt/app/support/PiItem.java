/*
 * @(#)2016年8月15日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import java.io.Serializable;

/**
 * Description: 集成项
 *
 * @author huanglc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月15日.1	huanglc		2016年8月15日		Create
 * </pre>
 * @date 2016年8月15日
 */
public class PiItem implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8993448952864113048L;

    private String uuid;

    private String dataUuid;

    private String name;

    private String id;

    private String type;

    private String path;

    private String parentUuid;

    // 产品UUID
    private String productUuid;
    // 系统UUID
    private String systemUuid;
    // 页面UUID
    private String pageUuid;

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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
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
     * @return the productUuid
     */
    public String getProductUuid() {
        return productUuid;
    }

    /**
     * @param productUuid 要设置的productUuid
     */
    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    /**
     * @return the systemUuid
     */
    public String getSystemUuid() {
        return systemUuid;
    }

    /**
     * @param systemUuid 要设置的systemUuid
     */
    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }

    /**
     * @return the pageUuid
     */
    public String getPageUuid() {
        return pageUuid;
    }

    /**
     * @param pageUuid 要设置的pageUuid
     */
    public void setPageUuid(String pageUuid) {
        this.pageUuid = pageUuid;
    }

}
