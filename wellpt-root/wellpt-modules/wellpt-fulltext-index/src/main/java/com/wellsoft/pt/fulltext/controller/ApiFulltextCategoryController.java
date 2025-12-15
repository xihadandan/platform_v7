/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.controller;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.fulltext.dto.FulltextCategoryDto;
import com.wellsoft.pt.fulltext.facade.service.FulltextCategoryFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/13/25.1	    zhulh		6/13/25		    Create
 * </pre>
 * @date 6/13/25
 */
@Api(tags = "全文检索分类管理")
@RestController
@RequestMapping("/api/fulltext/category")
public class ApiFulltextCategoryController {

    @Autowired
    private FulltextCategoryFacadeService fulltextCategoryFacadeService;

    @PostMapping("/save")
    @ApiOperation(value = "保存全文检索分类", notes = "保存全文检索分类")
    public ApiResult<Long> save(@RequestBody FulltextCategoryDto categoryDto) {
        return ApiResult.success(fulltextCategoryFacadeService.saveDto(categoryDto));
    }

    @GetMapping("/get/{uuid}")
    @ApiOperation(value = "获取全文检索分类", notes = "获取全文检索分类")
    public ApiResult<FulltextCategoryDto> get(@PathVariable(name = "uuid") Long uuid) {
        return ApiResult.success(fulltextCategoryFacadeService.getDto(uuid));
    }

    @DeleteMapping("/delete/{uuid}")
    @ApiOperation(value = "删除全文检索分类", notes = "删除全文检索分类")
    public ApiResult<Void> delete(@PathVariable(name = "uuid") Long uuid) {
        fulltextCategoryFacadeService.deleteByUuid(uuid);
        return ApiResult.success();
    }

    @GetMapping("/listAsTree")
    @ApiOperation(value = "根据模块ID获取全文检索分类树", notes = "根据模块ID获取全文检索分类树")
    public ApiResult<List<TreeNode>> listAsTree(@RequestParam(name = "moduleId", required = false) String moduleId) {
        List<TreeNode> treeNodes = fulltextCategoryFacadeService.listAsTreeByModuleId(moduleId);
        return ApiResult.success(treeNodes);
    }

    @GetMapping("/listBySystem")
    @ApiOperation(value = "根据系统ID获取全文检索分类", notes = "根据系统ID获取全文检索分类")
    public ApiResult<List<FulltextCategoryDto>> listBySystem() {
        String systemId = RequestSystemContextPathResolver.system();
        List<FulltextCategoryDto> dtos = fulltextCategoryFacadeService.listBySystem(systemId);
        return ApiResult.success(dtos);
    }

}
