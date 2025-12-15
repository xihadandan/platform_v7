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
public class RepeatConf implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6128422912300735119L;
    private String repeatType;
    private String repeatValue;

    /**
     * @return the repeatType
     */
    public String getRepeatType() {
        return repeatType;
    }

    /**
     * @param repeatType 要设置的repeatType
     */
    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    /**
     * @return the repeatValue
     */
    public String getRepeatValue() {
        return repeatValue;
    }

    /**
     * @param repeatValue 要设置的repeatValue
     */
    public void setRepeatValue(String repeatValue) {
        this.repeatValue = repeatValue;
    }

    @Override
    public String toString() {
        return JsonUtils.object2Json(this);
    }
}
