/*
 * @(#)2022-04-16 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据库表MULTI_ORG_GROUP_USER_RANGE的实体类
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
@Entity
@Table(name = "MULTI_ORG_GROUP_USER_RANGE")
@DynamicUpdate
@DynamicInsert
public class MultiOrgGroupUserRangeEntity extends IdEntity {

    private static final long serialVersionUID = 1650080359556L;

    // 添加使用范围人员真实值字段
    private String userRangeReal;
    // 添加使用范围人员显示值字段
    private String userRangeDisplay;
    // 群组id
    private String groupId;

    public static String userRangeListToIds(List<MultiOrgGroupUserRangeEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgGroupUserRangeEntity m : list) {
                ids.add(m.getUserRangeReal());
            }
            return StringUtils.join(ids, ";");
        }
    }

    public static String userRangeListToNames(List<MultiOrgGroupUserRangeEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgGroupUserRangeEntity m : list) {
                ids.add(m.userRangeDisplay);
            }
            return StringUtils.join(ids, ";");
        }
    }

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
