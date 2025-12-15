/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: ButtonElement.java
 *
 * @author zhulh
 * @date 2012-11-17
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-17.1	zhulh		2012-11-17		Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnElement extends BaseObject {

    // 固定列
    public static final String TYPE_FIXED = "1";
    // 扩展列
    public static final String TYPE_EXTEND = "2";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1615582635670720831L;
    private String type;
    private String typeName;
    private String index;
    private String name;

    private String sources;

    //查询标志(1：需要查询，0：不需要查询)
    private Integer searchFlag;

    private String extraColumn;

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
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
     * @return the sources
     */
    public String getSources() {
        return sources;
    }

    /**
     * @param sources 要设置的sources
     */
    public void setSources(String sources) {
        this.sources = sources;
    }

    public Integer getSearchFlag() {
        return searchFlag;
    }

    public void setSearchFlag(Integer searchFlag) {
        this.searchFlag = searchFlag;
    }

    public String getExtraColumn() {
        return extraColumn;
    }

    public void setExtraColumn(String extraColumn) {
        this.extraColumn = extraColumn;
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
