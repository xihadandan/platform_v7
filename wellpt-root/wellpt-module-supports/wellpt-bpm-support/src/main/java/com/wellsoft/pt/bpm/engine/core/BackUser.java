/*
 * @(#)2013-10-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-24.1	zhulh		2013-10-24		Create
 * </pre>
 * @date 2013-10-24
 */
public class BackUser {
    private String aUser;

    private String aUserId;

    private List<String> bUsers;

    private List<String> bUserIds;

    /**
     * @return the aUser
     */
    public String getaUser() {
        return aUser;
    }

    /**
     * @param aUser 要设置的aUser
     */
    public void setaUser(String aUser) {
        this.aUser = aUser;
    }

    /**
     * @return the aUserId
     */
    public String getaUserId() {
        return aUserId;
    }

    /**
     * @param aUserId 要设置的aUserId
     */
    public void setaUserId(String aUserId) {
        this.aUserId = aUserId;
    }

    /**
     * @return the bUsers
     */
    public List<String> getbUsers() {
        return bUsers;
    }

    /**
     * @param bUsers 要设置的bUsers
     */
    public void setbUsers(List<String> bUsers) {
        this.bUsers = bUsers;
    }

    /**
     * @return the bUserIds
     */
    public List<String> getbUserIds() {
        return bUserIds;
    }

    /**
     * @param bUserIds 要设置的bUserIds
     */
    public void setbUserIds(List<String> bUserIds) {
        this.bUserIds = bUserIds;
    }

}
