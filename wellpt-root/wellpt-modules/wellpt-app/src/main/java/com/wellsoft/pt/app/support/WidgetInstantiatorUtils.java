/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.pt.app.ui.Widget;

/**
 * Description: 组件定义实例化工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-12.1	zhulh		2016-09-12		Create
 * </pre>
 * @date 2016-09-12
 */
public class WidgetInstantiatorUtils {

    /**
     * 构建组件对象实例
     *
     * @param viewCls
     * @param viewDefinition
     * @return
     * @throws Exception
     */
    public static Widget instantiate(Class<?> viewCls, String viewDefinition) throws Exception {
        return (Widget) viewCls.getConstructor(String.class).newInstance(viewDefinition);
    }

    /**
     * 构建组件对象实例
     *
     * @param viewCls
     * @param viewDefinition
     * @param itemViewCls
     * @return
     * @throws Exception
     */
    public static Widget instantiate(Class<?> viewCls, String viewDefinition, Class<?> itemViewCls) throws Exception {
        return (Widget) viewCls.getConstructor(String.class, Class.class).newInstance(viewDefinition, itemViewCls);
    }

    public static Widget instantiate(Class<?> viewCls, String viewDefinition, boolean parseJSON, Class<?> itemViewCls) throws Exception {
        return (Widget) viewCls.getConstructor(String.class, boolean.class, Class.class).newInstance(viewDefinition, parseJSON, itemViewCls);
    }
}
