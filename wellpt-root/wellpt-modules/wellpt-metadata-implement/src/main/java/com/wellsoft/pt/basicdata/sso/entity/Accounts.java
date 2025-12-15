/*
 * @(#)2014-8-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.sso.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-9.1	wubin		2014-8-9		Create
 * </pre>
 * @date 2014-8-9
 */
@Entity
@Table(name = "xzsp_old_account")
@DynamicUpdate
@DynamicInsert
public class Accounts extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2586029487331267821L;

    private String userName;

    private String passWord;

    private String userId;

    private String sysId;

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
     * @return the passWord
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * @param passWord 要设置的passWord
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the sysId
     */
    public String getSysId() {
        return sysId;
    }

    /**
     * @param sysId 要设置的sysId
     */
    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

}
