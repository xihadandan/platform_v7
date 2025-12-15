/*
 * @(#)2022-04-15 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dto;

import java.io.Serializable;


/**
 * Description: 数据库表APP_COLOR_SETTING的对应的DTO类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-15.1	shenhb		2022-04-15		Create
 * </pre>
 * @date 2022-04-15
 */
public class AppColorSettingClassifyItemDto implements Serializable {

    private static final long serialVersionUID = 4351574469269153060L;

    private String id;
    private String name;
    private String color;

    public AppColorSettingClassifyItemDto() {
    }

    public AppColorSettingClassifyItemDto(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
