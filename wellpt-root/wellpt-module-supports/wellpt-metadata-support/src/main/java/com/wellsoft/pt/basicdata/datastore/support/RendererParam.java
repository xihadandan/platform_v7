/*
 * @(#)2017年5月17日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import io.swagger.annotations.ApiModel;

import java.util.HashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月17日.1	zhulh		2017年5月17日		Create
 * </pre>
 * @date 2017年5月17日
 */
@ApiModel("渲染器参数")
public class RendererParam extends HashMap<String, Object> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2805811160519919683L;

    /**
     * @param key
     * @return
     */
    public String getString(String key) {
        return this.getString(key, null);
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        Object object = this.get(key);
        if (object == null) {
            return defaultValue;
        }
        return object.toString();
    }
}
