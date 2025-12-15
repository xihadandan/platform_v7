/*
 * @(#)2022-04-15 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


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
public class AppColorSettingClassifyDto implements Serializable {

    private static final long serialVersionUID = 4351574469269153060L;

    // 模块编码
    private String moduleCode;
    // 分类/类型
    private String type;

    private List<AppColorSettingClassifyItemDto> valueList = new ArrayList<>();

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AppColorSettingClassifyItemDto> getValueList() {
        return valueList;
    }

    public void setValueList(List<AppColorSettingClassifyItemDto> valueList) {
        this.valueList = valueList;
    }
}
