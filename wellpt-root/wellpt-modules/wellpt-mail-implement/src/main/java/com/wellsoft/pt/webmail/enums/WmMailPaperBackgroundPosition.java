/*
 * @(#)2018年2月28日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.enums;

/**
 * Description: 邮件背景位置
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月28日.1	chenqiong		2018年2月28日		Create
 * </pre>
 * @date 2018年2月28日
 */
public enum WmMailPaperBackgroundPosition {
    TOP_LEFT("top left"), TOP_CENTER("top center"), TOP_RIGHT("top right"), CENTER_LEFT("center left"), CENTER_RIGHT(
            "center right"), CENTER_CENTER("center center"), BOTTOM_LEFT("bottom left"), BOTTOM_CENTER("bottom center"), BOTTOM_RIGHT(
            "bottom right");

    private String name;

    WmMailPaperBackgroundPosition(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
