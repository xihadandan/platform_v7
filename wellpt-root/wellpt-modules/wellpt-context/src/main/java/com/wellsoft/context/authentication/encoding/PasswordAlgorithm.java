/*
 * @(#)Sep 8, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.authentication.encoding;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 8, 2017.1	zhulh		Sep 8, 2017		Create
 * </pre>
 * @date Sep 8, 2017
 */
public enum PasswordAlgorithm {
    // None("None", "None", "0"),
    Plaintext("Plaintext", "Plaintext", "1"), MD5("MD5", "MD5", "2");

    // 成员变量
    private String name;
    private String value;
    private String code;

    // 构造方法
    private PasswordAlgorithm(String name, String value, String code) {
        this.name = name;
        this.value = value;
        this.code = code;
    }

    /**
     * @param code
     * @return
     */
    public static String getValueByCode(String code) {
        PasswordAlgorithm[] algorithms = values();
        for (PasswordAlgorithm algorithm : algorithms) {
            if (algorithm.getCode().equals(code)) {
                return algorithm.getValue();
            }
        }
        return null;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
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

}
