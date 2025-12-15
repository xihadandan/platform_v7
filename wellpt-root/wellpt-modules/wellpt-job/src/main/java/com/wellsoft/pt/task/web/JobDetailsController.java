/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Controller
@RequestMapping("/task/job/details")
public class JobDetailsController extends BaseController {


    @RequestMapping(value = "setting")
    public String setting(Model model) {
        model.addAttribute("isDebug", super.isDebug());
        return forward("/task/job_details");
    }


    @RequestMapping(value = "/firedHis")
    public String firedHis(Model model) {
        return forward("/task/qrtz_fired_trigger_his");
    }
}
