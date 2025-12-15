/*
 * @(#)2012-11-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

import org.springframework.web.servlet.view.JstlView;

import java.util.Locale;

/**
 * Description: 检查JSP资源是否存在的视图类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-30.1	zhulh		2012-11-30		Create
 * </pre>
 * @date 2012-11-30
 */
public class CheckResourceJstlView extends JstlView {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.view.AbstractUrlBasedView#checkResource(java.util.Locale)
     */
    @Override
    public boolean checkResource(Locale locale) throws Exception {
        return this.getServletContext().getResource(this.getUrl()) != null;
    }

}
