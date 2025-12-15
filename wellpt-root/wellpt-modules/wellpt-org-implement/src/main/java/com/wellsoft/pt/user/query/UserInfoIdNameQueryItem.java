/*
 * @(#)1/17/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 1/17/24.1	zhulh		1/17/24		Create
 * </pre>
 * @date 1/17/24
 */
public class UserInfoIdNameQueryItem implements BaseQueryItem {

    private static final long serialVersionUID = 5662484276657081151L;

    private String userName;
    private String userId;
    private String iUserName;

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

    public String getiUserName() {
        return iUserName;
    }

    public void setiUserName(String iUserName) {
        this.iUserName = iUserName;
    }
}
