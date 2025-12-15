/*
 * @(#)2013-3-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

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
 * 2013-3-28.1	zhulh		2013-3-28		Create
 * </pre>
 * @date 2013-3-28
 */
public class CustomDynamicColumn extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6868929500623049540L;

    // 列类型
    private String type;
    // 列索引
    private String index;
    // 列名称
    private String name;
    // 列样式
    private String className;

    private String uuid;

    private String configuration;

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
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index 要设置的index
     */
    public void setIndex(String index) {
        this.index = index;
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
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the configuration
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
