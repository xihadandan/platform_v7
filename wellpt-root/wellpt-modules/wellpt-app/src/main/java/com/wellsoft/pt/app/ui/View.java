/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.context.AppContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public interface View {

    /**
     * 返回组件ID
     *
     * @return
     */
    String getId();

    /**
     * 返回组件标题
     *
     * @return
     */
    String getTitle();

    /**
     * 返回组件定义JSON
     *
     * @return
     * @throws Exception
     */
    String getDefinitionJson() throws Exception;

    /**
     * 是否使用组件定义的html内容，若为false会调用render方法
     *
     * @return
     */
    boolean useDefinitionHtml();

    /**
     * 渲染组件内容
     *
     * @param appContext
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
