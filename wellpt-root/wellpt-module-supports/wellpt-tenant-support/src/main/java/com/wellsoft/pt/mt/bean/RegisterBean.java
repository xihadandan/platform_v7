/*
 * @(#)2013-4-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.bean;

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
 * 2013-4-18.1	zhulh		2013-4-18		Create
 * </pre>
 * @date 2013-4-18
 */
public class RegisterBean implements Serializable {

    private static final long serialVersionUID = -3526987751552405716L;

    // 公司名称
    private String companyName;
    // 账号
    private String account;
    // 注册密码
    private String password;
    // 联系邮箱
    private String email;

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName 要设置的companyName
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account 要设置的account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 要设置的password
     */
    public void setPassword(String password) {
        this.password = password;
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

}
