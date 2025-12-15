/*
 * @(#)7/4/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;

import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/4/24.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 7/4/24
 */
public class Button extends BaseObject {
    private static final long serialVersionUID = 566035549871763998L;

    private String title;

    private String code;

    private String uuid;

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();// 国际化配置


    /**
     *
     */
    public Button() {
    }

    /**
     * @param title
     * @param code
     */
    public Button(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public Button(String title, String code, Map<String, Map<String, String>> i18n) {
        this.title = title;
        this.code = code;
        this.i18n = i18n;
    }

    public Button(String title, String code, String uuid, Map<String, Map<String, String>> i18n) {
        this.title = title;
        this.code = code;
        this.uuid = uuid;
        this.i18n = i18n;
    }

    public static Button from(String code) {
        return new Button(code, code);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Button button = (Button) o;
        return Objects.equals(code, button.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
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
