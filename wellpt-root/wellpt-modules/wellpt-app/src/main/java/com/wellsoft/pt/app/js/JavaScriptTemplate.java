/*
 * @(#)2017-01-03 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.js;

import org.springframework.core.Ordered;

import java.io.Serializable;

/**
 * Description: JS模板，用于JS模板引擎
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-03.1	zhulh		2017-01-03		Create
 * </pre>
 * @date 2017-01-03
 */
public interface JavaScriptTemplate extends Ordered, Serializable {

    /**
     * 返回模板ID
     *
     * @return
     */
    String getId();

    /**
     * 返回模板名称
     *
     * @return
     */
    String getName();

    /**
     * 返回模板内容
     *
     * @return
     */
    String getContent();

}
