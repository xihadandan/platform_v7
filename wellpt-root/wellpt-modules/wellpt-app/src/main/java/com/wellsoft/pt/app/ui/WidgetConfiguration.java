/*
 * @(#)Mar 3, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
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
 * Mar 3, 2017.1	zhulh		Mar 3, 2017		Create
 * </pre>
 * @date Mar 3, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface WidgetConfiguration extends Serializable {

    void afterPropertiesSet() throws Exception;

    /**
     * 获取组件功能元素
     *
     * @return
     */
    List<FunctionElement> getFunctionElements();

    /**
     * @return
     */
    public String getType();

}
