/*
 * @(#)2015-1-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-23.1	zhulh		2015-1-23		Create
 * </pre>
 * @date 2015-1-23
 */
public class Notice extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1536573089393435392L;
    // UUID
    private String uuid;
    // 公告标题
    private String title;
    // 发布人
    private String publisher;
    // 发布时间
    private Date publishTime;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher 要设置的publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the publishTime
     */
    public Date getPublishTime() {
        return publishTime;
    }

    /**
     * @param publishTime 要设置的publishTime
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

}
