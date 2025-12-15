/*
 * @(#)Jan 25, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;

import java.util.Collections;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 25, 2018.1	zhulh		Jan 25, 2018		Create
 * </pre>
 * @date Jan 25, 2018
 */
public abstract class AbstractWidgetConfiguration implements WidgetConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 630909655353471432L;

    private String type;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.WidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.WidgetConfiguration#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
