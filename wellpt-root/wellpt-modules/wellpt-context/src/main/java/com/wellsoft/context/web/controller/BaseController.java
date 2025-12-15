/*
 * @(#)Snippet.java 2012-10-15 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.context.util.security.SpringSecurityUtilsFacade;
import com.wellsoft.context.util.str.StringEscapeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Description: Snippet.java
 *
 * @author zhulh
 * @date 2012-10-15
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-15.1	zhulh		2012-10-15		Create
 * </pre>
 */
public class BaseController {
    /**
     * 重定向前缀
     */
    private static final String REDIRECT_SUFFIX = "redirect:";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringSecurityUtilsFacade springSecurityUtilsFacade;

    protected String getMessage(String code, Object... args) {
        return MsgUtils.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * 转发
     *
     * @param viewName
     * @return
     */
    protected String forward(String viewName) {
        return viewName;
    }

    /**
     * 重定向
     *
     * @param viewName
     * @return
     */
    protected String redirect(String viewName) {
        return REDIRECT_SUFFIX + viewName;
    }

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {

        /**
         * 防止XSS攻击
         */
        binder.registerCustomEditor(String.class, new StringEscapeEditor(true, false));

    }

    @ModelAttribute
    public void initModel(Model model) {
        model.addAttribute("currUser", springSecurityUtilsFacade.getCurrentUser());
    }

    protected boolean isDebug() {
        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            if (arg.startsWith("-Xrunjdwp") || arg.startsWith("-agentlib:jdwp")) {
                return true;

            }
        }
        return false;
    }
}
