/*
 * @(#)2016-07-04 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2016-07-04		Create
 * </pre>
 * @date 2016-07-04
 */
@Controller
@RequestMapping(value = "/workflow/work/task/delegation")
public class WfTaskDelegationMgrController extends JqGridQueryController {

    @Autowired
    private WfTaskDelegationMgr wfTaskDelegationMgr;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String wfTaskDelegationList() {
        return forward("/workflow/work/work_task_delegation_list");
    }

    /**
     * jQgrid查询
     *
     * @param request
     * @param jqGridQueryInfo
     * @return
     */
    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = wfTaskDelegationMgr.query(queryInfo);
        return convertToJqGridQueryData(queryData);
    }
}
