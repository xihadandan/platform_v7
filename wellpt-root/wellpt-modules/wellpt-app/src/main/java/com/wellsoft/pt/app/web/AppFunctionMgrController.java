/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
@Controller
@RequestMapping({"/app/app/function", "/api/app/function"})
public class AppFunctionMgrController extends BaseController {

    @Autowired
    private AppFunctionService appFunctionService;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String appFunctionList(Model model) {
        model.addAttribute("appFunction", new AppFunction());
        return forward("/app/app_function_list");
    }

    /**
     * 保存组件功能元素
     *
     * @return
     */
    @RequestMapping(value = "/saveFunctionElements")
    @ResponseBody
    public ApiResult<Map<String, List<AppFunction>>> saveFunctionElements(@RequestBody Map<String, List<FunctionElement>> functionElementsMap) {
        return ApiResult.success(appFunctionService.saveFunctionElements(functionElementsMap));
    }

}
