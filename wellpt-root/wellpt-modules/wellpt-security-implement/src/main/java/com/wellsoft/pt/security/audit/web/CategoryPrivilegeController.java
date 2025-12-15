/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.security.audit.service.CategoryPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 权限分类树
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-11.1  zhengky	2014-10-11	  Create
 * </pre>
 * @date 2014-10-11
 */
@Controller
@RequestMapping("/security/category/privilege")
public class CategoryPrivilegeController extends JqGridQueryController {

    @Autowired
    private CategoryPrivilegeService categoryPrivilegeService;

    @RequestMapping(value = "")
    public String categoryPrivilege(Model model) {
        return forward("/security/category_privilege");
    }

    @RequestMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsJson(@RequestParam("categoryUuid") String categoryUuid,
                               HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = categoryPrivilegeService.queryByCategory(categoryUuid, jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

}
