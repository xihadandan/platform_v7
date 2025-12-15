/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.org.bean.DepartmentBean;
import com.wellsoft.pt.org.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description: DepartmentController.java
 *
 * @author zhulh
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * </pre>
 */
@Controller
@RequestMapping("/org/department")
public class DepartmentController extends JqGridQueryController {
    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/list")
    public String departmentList() {
        return forward("/org/department");
    }

    @RequestMapping(value = "/list/tree")
    public @ResponseBody
    JqGridQueryData listAsTreeJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = departmentService.getForPageAsTree(jqGridQueryInfo, queryInfo);
        return convertToJqGridQueryData(queryData);
    }

    @RequestMapping(value = "/get")
    public @ResponseBody
    ResultMessage get(@RequestBody DepartmentBean department) {
        ResultMessage resultMessage = new ResultMessage();
        DepartmentBean bean = departmentService.getBeanById(department.getId());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/save")
    public @ResponseBody
    ResultMessage save(@RequestBody DepartmentBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        departmentService.saveBean(bean);
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/delete")
    public @ResponseBody
    ResultMessage delete(@RequestBody DepartmentBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        departmentService.removeById(bean.getId());
        resultMessage.setData(bean);
        return resultMessage;
    }

    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    // @ModelAttribute("preloadDepartment")
    // public Department getDepartment(@RequestParam(value = "uuid", required =
    // false) String uuid,
    // @RequestParam(value = "parentUuid", required = false) String parentUuid)
    // {
    // if (uuid != null) {
    // Department department = departmentService.get(uuid);
    // if (StringUtils.isNotBlank(parentUuid)) {
    // department.setParent(departmentService.get(parentUuid));
    // }
    // return department;
    // }
    // return null;
    // }
    @RequestMapping(value = "/get/tree/{uuid}")
    public @ResponseBody
    List<TreeNode> getTree(@PathVariable("uuid") String excludeUuid) {
        TreeNode treeNode = departmentService.getAsTree(excludeUuid);
        return treeNode.getChildren();
    }
}
