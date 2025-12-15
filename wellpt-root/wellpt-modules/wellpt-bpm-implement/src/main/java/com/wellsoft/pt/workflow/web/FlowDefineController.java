/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 流程规划控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-23.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 2012-10-23
 */
@Controller
@RequestMapping("/workflow/define/flow")
public class FlowDefineController extends JqGridQueryController {

    @Autowired
    private FlowDefineService workFlowDefineService;

    /**
     * 跳到流程定义界面
     *
     * @return
     */
    @RequestMapping("")
    public String flowDefine() {
        return forward("/workflow/define/define");
    }

    @RequestMapping(value = "/list/tree")
    public @ResponseBody
    JqGridQueryData listAsTreeJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = workFlowDefineService.getForPageAsTree(jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    /**
     * 删除流程定义
     *
     * @param uuid
     * @return
     */
    @RequestMapping("/delete")
    public @ResponseBody
    ResultMessage flowDelete(@RequestParam("uuid") String uuid) {
        workFlowDefineService.remove(uuid);
        return new ResultMessage();
    }

}
