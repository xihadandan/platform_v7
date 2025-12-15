/*
 * @(#)May 24, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.support.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 24, 2017.1	zhulh		May 24, 2017		Create
 * </pre>
 * @date May 24, 2017
 */
public class AppActionProxy extends ActionProxy {
    @JsonIgnore
    private Object action;

    /**
     * @param piFunction
     */
    @SuppressWarnings("unchecked")
    public AppActionProxy(PiItem piItem, String text) {
        this.action = piItem;
        this.setId(piItem.getId());
        this.setName(text);
        this.setFullName(piItem.getName());
        Map<String, Object> properties = getProperties();
        properties.put(AppConstants.KEY_APP_TYPE, piItem.getType());
        properties.put(AppConstants.KEY_APP_PATH, piItem.getPath());
        if (AppType.FUNCTION.equals(Integer.valueOf(piItem.getType()))) {
            PiFunction piFunction = AppCacheUtils.getPiFunction(piItem.getUuid());
            Map<String, Object> functionProperties = JsonUtils.json2Object(piFunction.getDefinitionJson(), Map.class);
            properties.putAll(functionProperties);
        }
    }
}
