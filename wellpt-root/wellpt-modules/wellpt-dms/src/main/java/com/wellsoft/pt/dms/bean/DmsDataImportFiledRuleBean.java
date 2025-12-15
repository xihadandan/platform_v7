/*
 * @(#)2018年9月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author {zhongwd}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月5日.1	{zhongwd}		2018年9月5日		Create
 * </pre>
 * @date 2018年9月5日
 */
public class DmsDataImportFiledRuleBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5387237845171717112L;
    //必填
    private String requireds;
    //校验方式名称
    private String verificationText;
    //校验方式
    private String verification;
    //正则校验规则
    private String regularType;
    private String regularTypeText;
    //正则校验规则值
    private String regularValue;
    //groovy校验规则值
    private String groovyValue;

    /**
     * @return the requireds
     */
    public String getRequireds() {
        return requireds;
    }

    /**
     * @param requireds 要设置的requireds
     */
    public void setRequireds(String requireds) {
        this.requireds = requireds;
    }

    /**
     * @return the verificationText
     */
    public String getVerificationText() {
        return verificationText;
    }

    /**
     * @param verificationText 要设置的verificationText
     */
    public void setVerificationText(String verificationText) {
        this.verificationText = verificationText;
    }

    /**
     * @return the verification
     */
    public String getVerification() {
        return verification;
    }

    /**
     * @param verification 要设置的verification
     */
    public void setVerification(String verification) {
        this.verification = verification;
    }

    /**
     * @return the regularType
     */
    public String getRegularType() {
        return regularType;
    }

    /**
     * @param regularType 要设置的regularType
     */
    public void setRegularType(String regularType) {
        this.regularType = regularType;
    }

    /**
     * @return the regularValue
     */
    public String getRegularValue() {
        return regularValue;
    }

    /**
     * @param regularValue 要设置的regularValue
     */
    public void setRegularValue(String regularValue) {
        this.regularValue = regularValue;
    }

    /**
     * @return the groovyValue
     */
    public String getGroovyValue() {
        return groovyValue;
    }

    /**
     * @param groovyValue 要设置的groovyValue
     */
    public void setGroovyValue(String groovyValue) {
        this.groovyValue = groovyValue;
    }

    /**
     * @return the regularTypeText
     */
    public String getRegularTypeText() {
        return regularTypeText;
    }

    /**
     * @param regularTypeText 要设置的regularTypeText
     */
    public void setRegularTypeText(String regularTypeText) {
        this.regularTypeText = regularTypeText;
    }

}
