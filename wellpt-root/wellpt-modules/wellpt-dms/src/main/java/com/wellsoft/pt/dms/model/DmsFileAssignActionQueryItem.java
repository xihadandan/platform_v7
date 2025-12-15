/*
 * @(#)5/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

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
 * 5/28/24.1	zhulh		5/28/24		Create
 * </pre>
 * @date 5/28/24
 */
public class DmsFileAssignActionQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = 2431066500973296278L;

    // 分配的组织相关ID
    private String orgId;

    // 操作权限
    private String action;

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(String action) {
        this.action = action;
    }
}
