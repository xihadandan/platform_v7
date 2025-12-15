/*
 * @(#)2020年6月5日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.app.dingtalk.entity.EventCallBack;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月5日.1	zhongzh		2020年6月5日		Create
 * </pre>
 * @date 2020年6月5日
 */
public class CallBackEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private EventCallBack eventCallBack;

    /**
     * @param source
     * @param eventCallBack
     */
    public CallBackEvent(Object source, EventCallBack eventCallBack) {
        super(source);
        this.eventCallBack = eventCallBack;
    }

    public EventCallBack getEventCallBack() {
        return eventCallBack;
    }

}
