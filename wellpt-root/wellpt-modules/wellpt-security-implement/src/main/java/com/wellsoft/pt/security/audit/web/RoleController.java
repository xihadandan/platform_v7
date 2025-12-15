/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 角色维护控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@Controller
@RequestMapping("/security/role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    /**
     * 打开角色列表界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String role() {
        return forward("/security/role");
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = roleService.query(queryInfo);
        return queryData;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public ResultMessage get(@RequestBody RoleBean role) {
        ResultMessage resultMessage = new ResultMessage();
        RoleBean bean = roleService.getBean(role.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/save")
    @ResponseBody
    public ResultMessage save(@RequestBody RoleBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        roleService.saveBean(bean);
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ResultMessage delete(@RequestBody RoleBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        roleService.remove(bean.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    /**
     * 加载角色权限树，选择已选角色权限，不包含当前角色
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/get/role/privilege/tree")
    @ResponseBody
    public ResultMessage getPrivilegeTree(@RequestBody RoleBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        TreeNode treeNode = roleService.getRolePrivilegeTree(bean.getUuid(), null);
        resultMessage.setData(treeNode);
        return resultMessage;
    }

}
