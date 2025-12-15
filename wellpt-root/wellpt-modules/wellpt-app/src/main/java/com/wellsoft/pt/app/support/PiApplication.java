/*
 * @(#)2016年8月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import org.apache.commons.lang.StringUtils;

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
public class PiApplication extends Authenticatable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -788184186392038387L;

    private String name;

    private String id;

    private String path;

    private String title;

    // 关联的功能UUID
    private String cfUuid;

    // 关联的功能信息
    private PiFunction cfFunction;

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
     * @return the cfUuid
     */
    public String getCfUuid() {
        return cfUuid;
    }

    /**
     * @param cfUuid 要设置的cfUuid
     */
    public void setCfUuid(String cfUuid) {
        this.cfUuid = cfUuid;
    }

    /**
     * @return the cfFunction
     */
    public PiFunction getCfFunction() {
        return cfFunction;
    }

    /**
     * @param cfFunction 要设置的cfFunction
     */
    public void setCfFunction(PiFunction cfFunction) {
        this.cfFunction = cfFunction;
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
