/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.org.service.DutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 职位control
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */
@Controller
@RequestMapping("/org/duty")
public class DutyController extends BaseController {

    @Autowired
    private DutyService dutyService;

    @RequestMapping(value = "/list")
    public String job(Model model) {
        return forward("/org/duty");
    }

    @RequestMapping(value = "/delete")
    public @ResponseBody
    ResultMessage delete(@RequestBody Duty duty) {
        ResultMessage resultMessage = new ResultMessage();
        dutyService.remove(duty.getUuid());
        return resultMessage;
    }

}