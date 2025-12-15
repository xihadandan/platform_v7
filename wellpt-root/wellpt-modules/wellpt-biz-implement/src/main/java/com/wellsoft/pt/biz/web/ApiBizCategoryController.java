/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.web;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.biz.dto.BizCategoryDto;
import com.wellsoft.pt.biz.facade.service.BizCategoryFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Api(tags = "业务分类")
@RestController
@RequestMapping("/api/biz/category")
public class ApiBizCategoryController extends BaseController {

    @Autowired
    private BizCategoryFacadeService bizCategoryFacadeService;

    /**
     * 获取分类业务树
     *
     * @return
     */
    @GetMapping(value = "/getCategoryAndBusinessTree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取分类业务树", notes = "获取分类业务树")
    public ApiResult<TreeNode> getCategoryAndBusinessTree() {
        TreeNode treeNode = bizCategoryFacadeService.getCategoryAndBusinessTree();
        return ApiResult.success(treeNode);
    }

    /**
     * 保存业务分类
     *
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存业务分类", notes = "保存业务分类")
    public ApiResult<Void> save(@RequestBody BizCategoryDto dto) {
        bizCategoryFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    /**
     * 删除业务流程分类
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除业务流程分类", notes = "删除业务流程分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "业务分类UUID列表", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        bizCategoryFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

}
