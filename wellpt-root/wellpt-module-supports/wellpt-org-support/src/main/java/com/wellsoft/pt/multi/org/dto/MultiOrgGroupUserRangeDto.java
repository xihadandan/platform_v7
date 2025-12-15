/*
 * @(#)2022-04-16 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.dto;

import java.io.Serializable;


/**
 * Description: 数据库表MULTI_ORG_GROUP_USER_RANGE的对应的DTO类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-16.1	zenghw		2022-04-16		Create
 * </pre>
 * @date 2022-04-16
 */
public class MultiOrgGroupUserRangeDto implements Serializable {

    private static final long serialVersionUID = 1650080359556L;

    // 添加使用范围人员真实值字段
    private String userRangeReal;
    // 添加使用范围人员显示值字段
    private String userRangeDisplay;
    // 群组id
    private String groupId;

    /**
     * @return the userRangeReal
     */
    public String getUserRangeReal() {
        return this.userRangeReal;
    }

    /**
     * @param userRangeReal
     */
    public void setUserRangeReal(String userRangeReal) {
        this.userRangeReal = userRangeReal;
    }

    /**
     * @return the userRangeDisplay
     */
    public String getUserRangeDisplay() {
        return this.userRangeDisplay;
    }

    /**
     * @param userRangeDisplay
     */
    public void setUserRangeDisplay(String userRangeDisplay) {
        this.userRangeDisplay = userRangeDisplay;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
