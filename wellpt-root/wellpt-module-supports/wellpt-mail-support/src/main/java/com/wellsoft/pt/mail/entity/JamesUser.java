/*
 * @(#)2015-6-1 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.entity;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Description: james用户
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-1.1	Administrator		2015-6-1		Create
 * </pre>
 * @date 2015-6-1
 */
@Entity
@Table(name = "JAMES_USER")
public class JamesUser implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 592298530045015054L;

    @Id
    private String userName;

    private String passwordHashAlgorithm;

    private String password;

    private Integer version;

    public JamesUser() {

    }

    public JamesUser(String userName, String pass, String passwordHashAlgorithm) {
        this.userName = userName;
        this.passwordHashAlgorithm = passwordHashAlgorithm;
        this.password = hashPassword(userName, pass, passwordHashAlgorithm);
        this.version = Integer.valueOf(1);
    }

    /* lmw 2015-6-3 begin */
    public static String hashPassword(String username, String password, String alg) {
        String newPass;
        if ((alg == null) || (alg.equals("MD5"))) {
            newPass = DigestUtils.md5Hex(password);
        } else if (alg.equals("NONE")) {
            newPass = "password";
        } else if (alg.equals("SHA-256")) {
            newPass = DigestUtils.sha256Hex(password);
        } else if (alg.equals("SHA-512")) {
            newPass = DigestUtils.sha512Hex(password);
        } else {
            newPass = DigestUtils.shaHex(password);
        }
        return newPass;
    }

    /* lmw 2015-6-3 end */

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the passwordHashAlgorithm
     */
    public String getPasswordHashAlgorithm() {
        return passwordHashAlgorithm;
    }

    /**
     * @param passwordHashAlgorithm 要设置的passwordHashAlgorithm
     */
    public void setPasswordHashAlgorithm(String passwordHashAlgorithm) {
        this.passwordHashAlgorithm = passwordHashAlgorithm;
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
        this.password = hashPassword(userName, password, passwordHashAlgorithm);
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

}
