/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 岗位controller
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-11.1  zhengky	2014-8-11	  Create
 * </pre>
 * @date 2014-8-11
 */
@Controller
@RequestMapping("/org/job")
public class JobController extends BaseController {

    @Autowired
    private JobService jobService;

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/list")
    public String job(Model model) {
        return forward("/org/job");
    }

    @RequestMapping(value = "/delete")
    public @ResponseBody
    ResultMessage delete(@RequestBody Job job) {
        ResultMessage resultMessage = new ResultMessage();
        jobService.deleteById(job.getId());
        return resultMessage;
    }

}