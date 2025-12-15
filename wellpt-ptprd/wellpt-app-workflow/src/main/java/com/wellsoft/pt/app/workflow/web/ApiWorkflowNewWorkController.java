/*
 * @(#)2021年3月31日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.workflow.facade.service.WorkflowNewWorkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 流程发起工作
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月31日.1	zhulh		2021年3月31日		Create
 * </pre>
 * @date 2021年3月31日
 */
@Api(tags = "流程发起工作")
@RestController
@RequestMapping("/api/workflow/new/work/")
public class ApiWorkflowNewWorkController extends BaseController {

    @Autowired
    private WorkflowNewWorkService workflowNewWorkService;

    /**
     * @return
     */
    @ResponseBody
    @GetMapping("/getMobileFlowDefinitions")
    @ApiOperation(value = "获取手机端发起工作的流程定义信息", notes = "获取手机端发起工作的流程定义信息")
    public ApiResult<List<TreeNode>> getMobileFlowDefinitions() {
        return ApiResult.success(workflowNewWorkService.getMobileFlowDefinitions());
    }

    /**
     * @return
     */
    @ResponseBody
    @GetMapping("/getFlowDefinitions")
    @ApiOperation(value = "获取发起工作的流程定义信息", notes = "获取发起工作的流程定义信息")
    public ApiResult<List<TreeNode>> getFlowDefinitions() {
        return ApiResult.success(workflowNewWorkService.getFlowDefinitions());
    }

    /**
     * @return
     */
    @ResponseBody
    @GetMapping("/getUserRecentUseFlowDefinitions")
    @ApiOperation(value = "获取用户发起工作最近使用的流程定义信息", notes = "获取用户发起工作最近使用的流程定义信息")
    public ApiResult<TreeNode> getUserRecentUseFlowDefinitions() {
        return ApiResult.success(workflowNewWorkService.getUserRecentUseFlowDefinitions());
    }

    /**
     * @return
     */
    @ResponseBody
    @GetMapping("/getUserAllFlowDefinitions")
    @ApiOperation(value = "获取用户发起工作所有的流程定义信息", notes = "获取用户发起工作所有的流程定义信息")
    public ApiResult<List<TreeNode>> getUserAllFlowDefinitions() {
        return ApiResult.success(workflowNewWorkService.getUserAllFlowDefinitions());
    }

}
