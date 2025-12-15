/*
 * @(#)2022-04-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.dto;

import java.io.Serializable;
import java.util.Date;


/**
 * Description: 数据库表APP_BASE_CALENDAR_EVENT的对应的DTO类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-13.1	shenhb		2022-04-13		Create
 * </pre>
 * @date 2022-04-13
 */
public class AppBaseCalendarEventDto implements Serializable {

    private static final long serialVersionUID = 1649830099695L;

    // 开始时间
    private Date startTime;
    // 结束时间
    private Date endTime;
    // 事项归属对象ID
    private String belongObjId;
    // 标题
    private String title;
    // 事项内容
    private String eventContent;

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the belongObjId
     */
    public String getBelongObjId() {
        return this.belongObjId;
    }

    /**
     * @param belongObjId
     */
    public void setBelongObjId(String belongObjId) {
        this.belongObjId = belongObjId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the eventContent
     */
    public String getEventContent() {
        return this.eventContent;
    }

    /**
     * @param eventContent
     */
    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

}
