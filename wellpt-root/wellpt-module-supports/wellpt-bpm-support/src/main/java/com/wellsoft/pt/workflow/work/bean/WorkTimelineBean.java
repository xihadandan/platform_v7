/*
 * @(#)2013-11-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作时间轴VO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-6.1	zhulh		2013-11-6		Create
 * </pre>
 * @date 2013-11-6
 */
public class WorkTimelineBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1023260455416272773L;

    private String flowName;

    private String flowId;

    private String title;

    private List<Map<String, String>> timers = new ArrayList<Map<String, String>>();

    private List<TimelineItem> items = new ArrayList<TimelineItem>();

    /**
     * @return the flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName 要设置的flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return the flowId
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * @param flowId 要设置的flowId
     */
    public void setFlowId(String flowId) {
        this.flowId = flowId;
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
     * @return the timers
     */
    public List<Map<String, String>> getTimers() {
        return timers;
    }

    /**
     * @param timers 要设置的timers
     */
    public void setTimers(List<Map<String, String>> timers) {
        this.timers = timers;
    }

    /**
     * @return the items
     */
    public List<TimelineItem> getItems() {
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(List<TimelineItem> items) {
        this.items = items;
    }

}
