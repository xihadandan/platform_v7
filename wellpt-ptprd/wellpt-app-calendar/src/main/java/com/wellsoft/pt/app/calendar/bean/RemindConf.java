/*
 * @(#)2018年11月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.bean;

import com.wellsoft.context.util.json.JsonUtils;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author {Svn璐﹀彿}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月14日.1	{Svn璐﹀彿}		2018年11月14日		Create
 * </pre>
 * @date 2018年11月14日
 */
public class RemindConf implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6128422912300735119L;
    private String interval;
    private String intervalUnit;

    @Override
    public String toString() {
        return JsonUtils.object2Json(this);
    }

    /**
     * @return the interval
     */
    public String getInterval() {
        return interval;
    }

    /**
     * @param interval 要设置的interval
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * @return the intervalUnit
     */
    public String getIntervalUnit() {
        return intervalUnit;
    }

    /**
     * @param intervalUnit 要设置的intervalUnit
     */
    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }
}
