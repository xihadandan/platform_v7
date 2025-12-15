/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web;

import com.wellsoft.pt.dms.core.context.ActionContext;
import org.springframework.ui.Model;

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
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
public interface View {

    /**
     * @param model
     * @param actionContext
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    void loadActionData(Model model, ActionContext actionContext, HttpServletRequest request,
                        HttpServletResponse response) throws Exception;

    /**
     * @return
     */
    String getViewName();

}
