/*
 * @(#)2017-01-23 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-23.1	zhulh		2017-01-23		Create
 * </pre>
 * @date 2017-01-23
 */
@Controller
@RequestMapping("/visit/jar/resources/")
public class VisitJarResourcesController extends BaseController {

    /**
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/jsp", method = RequestMethod.GET)
    public String visitJsp(Model model) {
        return forward("/ptprd/app/workflow/b");
    }

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/ftl", method = RequestMethod.GET)
    public String visitFtl(Model model) {
        return forward("/ptprd/app/workflow/c");
    }

}
