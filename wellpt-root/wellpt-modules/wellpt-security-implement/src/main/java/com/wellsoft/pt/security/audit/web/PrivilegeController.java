/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import com.wellsoft.pt.security.audit.support.ResourceDataSourceComparator;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 权限维护控制器类
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
@Api(tags = "权限维护控制器类")
@Controller
@RequestMapping("/security/privilege")
public class PrivilegeController extends BaseController {
    @Autowired
    private PrivilegeService privilegeService;

    @Autowired(required = false)
    private List<ResourceDataSource> resourceDataSources;

    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    /**
     * 打开角色列表界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String privilege(Model model) {
        // 加入其他类型的资源
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (resourceDataSources != null) {
            Collections.sort(resourceDataSources, new ResourceDataSourceComparator());
            for (ResourceDataSource resourceDataSource : resourceDataSources) {
                TreeNode treeNode = new TreeNode();
                treeNode.setName(resourceDataSource.getName());
                treeNode.setId(resourceDataSource.getId());
                treeNodes.add(treeNode);
            }
        }
        model.addAttribute("otherReses", treeNodes);
        return forward("/security/privilege");
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public JqGridQueryData listAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = privilegeService.query(queryInfo);
        return queryData;
    }

    @RequestMapping(value = "/get")
    @ResponseBody
    public ResultMessage get(@RequestBody PrivilegeBean permission) {
        ResultMessage resultMessage = new ResultMessage();
        PrivilegeBean bean = privilegeService.getBean(permission.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ResultMessage delete(@RequestBody PrivilegeBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        privilegeService.remove(bean.getUuid());
        resultMessage.setData(bean);
        return resultMessage;
    }

    @RequestMapping(value = "/get/resource/tree")
    @ResponseBody
    public ResultMessage getResourceTree(@RequestBody PrivilegeBean bean) {
        ResultMessage resultMessage = new ResultMessage();
        TreeNode treeNode = privilegeService.getResourceTree(bean.getUuid());
        resultMessage.setData(treeNode);
        return resultMessage;
    }

    @ApiOperation(value = "获取其他权限树有勾选的权限", notes = "获取其他权限树有勾选的权限")
    @GetMapping("/getOtherResourceTreeOnlyCheck")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "根据权限UUID获取相应的其他权限树", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "privilegeUuid", value = "根据权限uuid获取其他权限资源", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<List<TreeNode>> getOtherResourceTreeOnlyCheck(
            @RequestParam(name = "uuid", required = false) String uuid,
            @RequestParam(name = "privilegeUuid", required = false) String privilegeUuid) {
        List<TreeNode> list = privilegeFacadeService.getOtherResourceTreeOnlyCheck(uuid, privilegeUuid);
        return ApiResult.success(list);
    }
}
