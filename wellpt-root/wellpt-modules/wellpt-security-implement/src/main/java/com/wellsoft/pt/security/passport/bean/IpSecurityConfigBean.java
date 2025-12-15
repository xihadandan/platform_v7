/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.bean;

import com.wellsoft.pt.security.passport.entity.IpSecurityConfig;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-2.1	zhulh		2013-5-2		Create
 * </pre>
 * @date 2013-5-2
 */
public class IpSecurityConfigBean extends IpSecurityConfig {

    public static final String ROW_STATUS_ADDED = "added";
    public static final String ROW_STATUS_EDITED = "edited";
    public static final String ROW_STATUS_DELETED = "deleted";
    private static final long serialVersionUID = -1401088464235989245L;
    // jqGrid的行标识
    private String id;

    // 数据行状态added、edited、deleted
    private String rowStatus;

    // 用户ID
    private String userIds;

    // 用户名
    private String usernames;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the rowStatus
     */
    public String getRowStatus() {
        return rowStatus;
    }

    /**
     * @param rowStatus 要设置的rowStatus
     */
    public void setRowStatus(String rowStatus) {
        this.rowStatus = rowStatus;
    }

    /**
     * @return the userIds
     */
    public String getUserIds() {
        return userIds;
    }

    /**
     * @param userIds 要设置的userIds
     */
    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    /**
     * @return the usernames
     */
    public String getUsernames() {
        return usernames;
    }

    /**
     * @param usernames 要设置的usernames
     */
    public void setUsernames(String usernames) {
        this.usernames = usernames;
    }

}
