/*
 * @(#)2021年1月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.dto;

import com.wellsoft.pt.repository.entity.LogicFileInfo;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月27日.1	zhongzh		2021年1月27日		Create
 * </pre>
 * @date 2021年1月27日
 */
public class LogicFileInfoExt extends LogicFileInfo {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Integer width;
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}
