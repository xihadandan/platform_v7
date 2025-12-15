/*
 * @(#)2021年7月22日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.enums;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月22日.1	zhongzh		2021年7月22日		Create
 * </pre>
 * @date 2021年7月22日
 */
public enum CalendarItemStatus {
    uncomplete("uncomplete", "未完成", ""), done("done", "已完成", "");

    private String id;
    private String name;
    private String color;

    CalendarItemStatus(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

}
