/*
 * @(#)6/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.fulltext.dto.FulltextSettingDto;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@Api(tags = "全文检索设置")
@RestController
@RequestMapping("/api/fulltext/setting")
public class ApiFulltextSettingController extends BaseController {

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    @PostMapping("/save")
    @ApiOperation(value = "保存全文检索设置", notes = "保存全文检索数据模型")
    public ApiResult<Void> save(@RequestBody FulltextSettingDto dto) {
        fulltextSettingFacadeService.saveDto(dto);
        return ApiResult.success();
    }

    @GetMapping("/getSearch")
    @ApiOperation(value = "获取搜索设置", notes = "获取搜索设置")
    public ApiResult<FulltextSettingDto> getSearch() {
        FulltextSettingDto dto = fulltextSettingFacadeService.getByTypeAndSystem("search", RequestSystemContextPathResolver.system());
        return ApiResult.success(dto);
    }

    @GetMapping("/getIndex")
    @ApiOperation(value = "获取索引设置", notes = "获取索引设置")
    public ApiResult<FulltextSettingDto> getIndex() {
        FulltextSettingDto dto = fulltextSettingFacadeService.getByTypeAndSystem("index", RequestSystemContextPathResolver.system());
        return ApiResult.success(dto);
    }

}
