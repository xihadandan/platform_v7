/*
 * @(#)2019年7月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.dto;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年7月9日.1	zhulh		2019年7月9日		Create
 * </pre>
 * @date 2019年7月9日
 */
public class WidgetDto extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 952105214641859703L;

    // 名称
    private String text;

    // ID
    private String id;

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

}
