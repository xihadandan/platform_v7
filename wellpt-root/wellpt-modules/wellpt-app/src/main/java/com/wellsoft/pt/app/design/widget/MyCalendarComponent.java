/*
 * @(#)2021年7月26日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.widget;

import com.wellsoft.pt.app.js.JavaScriptModule;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月26日.1	zhongzh		2021年7月26日		Create
 * </pre>
 * @date 2021年7月26日
 */
@Component
public class MyCalendarComponent extends FullCalendarComponent {

    @Override
    public String getType() {
        return "wMyCalendar";
    }

    @Override
    public String getName() {
        return "资源日历组件(新)";
    }

    @Override
    public JavaScriptModule getJavaScriptModule() {
        return getJavaScriptModule("pt.js.module.widget_mycalendar.id", "widget_mycalendar");
    }

}
