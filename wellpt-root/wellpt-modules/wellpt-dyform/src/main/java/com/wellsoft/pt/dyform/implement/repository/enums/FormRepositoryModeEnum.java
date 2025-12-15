/*
 * @(#)2019年8月20日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 表单存储方式
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月20日.1	zhulh		2019年8月20日		Create
 * </pre>
 * @date 2019年8月20日
 */
public enum FormRepositoryModeEnum {

    Dyform("1", "平台表单存储"), // 平台表单存储
    UserTable("2", "平台用户表(非表单存储自动生成的表)存储"), // 平台用户表(非表单存储自动生成的表)存储
    SoapWebservice("3", "SOAP webservice表单存储"), // SOAP webserivce表单存储
    RestfulApi("4", "restful api表单存储"), // restful api表单存储
    CustomInterface("5", "自定义接口实现存储"); // 自定义接口实现存储

    private String value;
    private String remark;

    private FormRepositoryModeEnum(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    /**
     * @param value
     * @return
     */
    public static FormRepositoryModeEnum value2Enum(String value) {
        for (FormRepositoryModeEnum repositoryModeEnum : values()) {
            if (StringUtils.equals(repositoryModeEnum.getValue(), value)) {
                return repositoryModeEnum;
            }
        }
        return Dyform;
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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
