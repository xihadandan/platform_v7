/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.org.service.DepartmentJobService;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: DepartmentJobController.java
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-12.1  zhengky	2014-8-12	  Create
 * </pre>
 * @date 2014-8-12
 */
@Controller
@RequestMapping("/org/department/job")
public class DepartmentJobController extends JqGridQueryController {

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private DepartmentJobService departmentjobservice;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @RequestMapping(value = "")
    public String departmentList(Model model) {
        return forward("/org/department_job");
    }

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(@RequestParam("departmentUuid") String departmentUuid,
                               HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        /**
         * begin 2014-12-11 @yuyq end 2014-12-11
         * 修改默认排序是根据职位序列和职级
         * 原默认queryInfo.setOrderBy("code asc,name");
         */
        queryInfo.setOrderBy("duty.seriesName,duty.dutyLevel desc,name");
        QueryData queryData = departmentjobservice.queryByDepartment(departmentUuid, jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

}
