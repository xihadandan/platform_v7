/*
 * @(#)11/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.entity.DmsLibraryCategoryEntity;
import com.wellsoft.pt.dms.service.DmsLibraryCategoryService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
 * 11/20/25.1	    zhulh		11/20/25		    Create
 * </pre>
 * @date 11/20/25
 */
@Api(tags = "库分类接口")
@RestController
@RequestMapping("/api/dms/library/category")
public class ApiDmsLibraryCategoryController extends BaseController {

    @Autowired
    private DmsLibraryCategoryService libraryCategoryService;

    /**
     * 保存库分类
     *
     * @param categoryEntity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存库分类", notes = "保存库分类")
    public ApiResult<Long> save(@RequestBody DmsLibraryCategoryEntity categoryEntity) {
        if (categoryEntity.getUuid() == null) {
            categoryEntity.setSystem(RequestSystemContextPathResolver.system());
        }
        libraryCategoryService.save(categoryEntity);
        return ApiResult.success(categoryEntity.getUuid());
    }

    /**
     * 删除库分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除库分类", notes = "删除库分类")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<Long> uuids) {
        libraryCategoryService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    /**
     * 获取库分类
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取库分类", notes = "获取库分类")
    public ApiResult<List<DmsLibraryCategoryEntity>> list() {
        return ApiResult.success(libraryCategoryService.listBySystem(RequestSystemContextPathResolver.system()));
    }

}
