/*
 * @(#)2016年8月12日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 上下文数据存储接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月12日.1	zhulh		2016年8月12日		Create
 * </pre>
 * @date 2016年8月12日
 */
public interface AppContextRepository {

    /**
     * 加载应用上下文信息
     *
     * @param request
     * @param response
     * @return
     */
    AppContext loadContext(HttpServletRequest request, HttpServletResponse response);

    /**
     * 保存应用上下文信息
     *
     * @param appContext
     * @param request
     * @param response
     */
    void saveContext(AppContext appContext, HttpServletRequest request, HttpServletResponse response);

}
