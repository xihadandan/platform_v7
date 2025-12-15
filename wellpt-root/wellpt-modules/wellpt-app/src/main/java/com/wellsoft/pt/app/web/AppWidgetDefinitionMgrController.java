/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2016-09-18		Create
 * </pre>
 * @date 2016-09-18
 */
@Controller
@RequestMapping(value = "/app/app/widget/definition")
public class AppWidgetDefinitionMgrController extends BaseController {

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String appWidgetDefinitionList() {
        return forward("/app/app_widget_definition_list");
    }

}
