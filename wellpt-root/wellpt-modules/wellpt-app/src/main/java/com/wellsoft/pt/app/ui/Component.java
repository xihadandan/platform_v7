/*
 * @(#)2016年5月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

/**
 * Description: 组件定义接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月10日.1	zhulh		2016年5月10日		Create
 * </pre>
 * @date 2016年5月10日
 */
public interface Component {

    /**
     * 返回组件类型，对应于解析时的jquery插件名称，多个组件使用中一个jquery插件解析时，对应的类型为 插件名称 + "_" + 区别号
     *
     * @return
     */
    String getType();

    /**
     * 返回组件名称
     *
     * @return
     */
    String getName();

    /**
     * 组件的解析渲染时对应的后台java类类型，默认为DefaultWidgetDefinitionProxyView.class
     *
     * @return
     */
    <V extends View> Class<V> getViewClass();

    /**
     * 返回组件分类，默认包含布局，基本，应用，可扩展
     *
     * @return
     */
    ComponentCategory getCategory();

}
