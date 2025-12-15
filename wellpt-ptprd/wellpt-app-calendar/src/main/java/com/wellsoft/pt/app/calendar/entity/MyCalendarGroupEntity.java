/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 群组
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "APP_MY_CALENDAR_GROUP")
@DynamicUpdate
@DynamicInsert
public class MyCalendarGroupEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4192204618873613189L;

    private String groupName; // 组名
    private String groupMembers; // 组成员
    private String groupMembersName; // 组成员名称

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName 要设置的groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the groupMembers
     */
    public String getGroupMembers() {
        return groupMembers;
    }

    /**
     * @param groupMembers 要设置的groupMembers
     */
    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    /**
     * @return the groupMembersName
     */
    public String getGroupMembersName() {
        return groupMembersName;
    }

    /**
     * @param groupMembersName 要设置的groupMembersName
     */
    public void setGroupMembersName(String groupMembersName) {
        this.groupMembersName = groupMembersName;
    }

}
