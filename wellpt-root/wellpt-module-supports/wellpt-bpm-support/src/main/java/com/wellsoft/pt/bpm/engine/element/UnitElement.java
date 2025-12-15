/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7719630605874776249L;

    private Integer type;

    private String value;
    private String argValue;

    private String uuid;
    private Map<String, Map<String, String>> i18n = Maps.newHashMap();// 国际化配置

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getArgValue() {
        return argValue;
    }

    public void setArgValue(String argValue) {
        this.argValue = argValue;
    }

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
