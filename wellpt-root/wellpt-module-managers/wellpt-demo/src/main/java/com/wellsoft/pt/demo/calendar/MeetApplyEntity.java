/*
 * @(#)2021年8月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.demo.calendar;

import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月9日.1	zhongzh		2021年8月9日		Create
 * </pre>
 * @date 2021年8月9日
 */
@Entity
@Table(name = "UF_MEET_APPLY")
@DynamicUpdate
@DynamicInsert
public class MeetApplyEntity extends CalendarEventEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String meetName;

    public String getMeetName() {
        return meetName;
    }

    public void setMeetName(String meetName) {
        this.meetName = meetName;
    }

}
