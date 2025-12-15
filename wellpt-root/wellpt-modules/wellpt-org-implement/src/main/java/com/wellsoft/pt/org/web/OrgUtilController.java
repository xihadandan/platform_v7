/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.service.impl.OrgUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Description: UserController.java
 *
 * @author Administrator
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * </pre>
 */
@Controller
@RequestMapping("/org/util")
public class OrgUtilController extends BaseController {
    @RequestMapping(value = "/getUniqueLoginNameType")
    public void user(Model model, HttpServletRequest request, HttpServletResponse response) {
        Boolean isLoginNameUniqueInGlobal = OrgUtil.isLoginNameUniqueInGlobal();
        Writer io = null;
        try {
            io = response.getWriter();
            io.write(isLoginNameUniqueInGlobal.toString());
            io.flush();
            io.close();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }
}