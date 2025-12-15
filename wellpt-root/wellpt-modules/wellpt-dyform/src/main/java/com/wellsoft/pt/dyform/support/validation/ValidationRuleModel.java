/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

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
 * 2015-9-28.1	zhulh		2015-9-28		Create
 * </pre>
 * @date 2015-9-28
 */
public class ValidationRuleModel implements Serializable {

    public static final String MAX_LENGTH = "maxLength";
    public static final String NOT_BLANK = "notBlank";
    public static final String UNIQUE = "unique";
    public static final String URL = "url";
    public static final String EMAIL = "email";
    public static final String ID_CARD_NUMBER = "idCardNumber";
    public static final String TELEPHONE = "telephone";
    public static final String MOBILE_PHONE = "mobilePhone";
    public static final String ZIP_CODE = "zipCode";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1360759916231993703L;
    // 表单定义ID
    private String formId;
    // 表单数据UUID
    private String dataUuid;
    // 数据库表名
    private String tableName;
    // 字段名
    private String fieldName;
    // 长度
    private int length;

    // 最大长度
    @MaxLength
    private Object maxLength;
    // 非空
    @NotBlank
    private String notBlank;
    // 唯一
    @Unique
    private Object unique;
    // URL
    @URL
    private String url;
    // Email
    @Email
    private String email;
    // 身份证号
    @IdCardNumber
    private Object idCardNumber;
    // 固定电话
    @Telephone
    private Object telephone;
    // 手机
    @MobilePhone
    private Object mobilePhone;
    // 邮编
    @ZipCode
    private Object zipCode;

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
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
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName 要设置的tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length 要设置的length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the maxLength
     */
    public Object getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength 要设置的maxLength
     */
    public void setMaxLength(Object maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return the notBlank
     */
    public String getNotBlank() {
        return notBlank;
    }

    /**
     * @param notBlank 要设置的notBlank
     */
    public void setNotBlank(String notBlank) {
        this.notBlank = notBlank;
    }

    /**
     * @return the unique
     */
    public Object getUnique() {
        return unique;
    }

    /**
     * @param unique 要设置的unique
     */
    public void setUnique(Object unique) {
        this.unique = unique;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 要设置的email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the idCardNumber
     */
    public Object getIdCardNumber() {
        return idCardNumber;
    }

    /**
     * @param idCardNumber 要设置的idCardNumber
     */
    public void setIdCardNumber(Object idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    /**
     * @return the telephone
     */
    public Object getTelephone() {
        return telephone;
    }

    /**
     * @param telephone 要设置的telephone
     */
    public void setTelephone(Object telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the mobilePhone
     */
    public Object getMobilePhone() {
        return mobilePhone;
    }

    /**
     * @param mobilePhone 要设置的mobilePhone
     */
    public void setMobilePhone(Object mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Object getZipCode() {
        return zipCode;
    }

    public void setZipCode(Object zipCode) {
        this.zipCode = zipCode;
    }
}
