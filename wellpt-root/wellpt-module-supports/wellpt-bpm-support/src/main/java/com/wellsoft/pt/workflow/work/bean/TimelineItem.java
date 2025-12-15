/*
 * @(#)2013-11-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import java.io.Serializable;
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
 * 2013-11-23.1	zhulh		2013-11-23		Create
 * </pre>
 * @date 2013-11-23
 */
public class TimelineItem implements Serializable, Comparable<TimelineItem> {

    public static final String TYPE_TIMER = "timer";
    public static final String TYPE_TASK = "task";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8720150702642980731L;
    // timer、task
    private String type;

    private String id;

    private String taskName;

    private String taskId;

    private Date time;

    private String title;

    private String user;

    private String content;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time 要设置的time
     */
    public void setTime(Date time) {
        this.time = time;
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
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user 要设置的user
     */
    public void setUser(String user) {
        this.user = user;
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
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TimelineItem o) {
        return this.getTime().compareTo(o.getTime());
    }

}
