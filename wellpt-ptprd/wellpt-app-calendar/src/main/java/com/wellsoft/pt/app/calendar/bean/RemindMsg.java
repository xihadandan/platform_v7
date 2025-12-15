/*
 * @(#)2018年6月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 提醒消息
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月5日.1	zyguo		2018年6月5日		Create
 * </pre>
 * @date 2018年6月5日
 */

public class RemindMsg extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4538372387143710692L;
    private String title;
    private String content;
    private String remindTime;
    private String startTime;
    private String endTime;
    private String address;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the remindTime
     */
    public String getRemindTime() {
        return remindTime;
    }

    /**
     * @param remindTime 要设置的remindTime
     */
    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address 要设置的address
     */
    public void setAddress(String address) {
        this.address = address;
    }

}
