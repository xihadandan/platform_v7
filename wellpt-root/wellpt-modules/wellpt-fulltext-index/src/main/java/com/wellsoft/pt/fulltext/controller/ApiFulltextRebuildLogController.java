/*
 * @(#)6/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.fulltext.entity.FulltextRebuildLogEntity;
import com.wellsoft.pt.fulltext.service.FulltextRebuildLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
 * 6/16/25.1	    zhulh		6/16/25		    Create
 * </pre>
 * @date 6/16/25
 */
@Api(tags = "全文检索索引重建日志")
@RestController
@RequestMapping("/api/fulltext/rebuild/log")
public class ApiFulltextRebuildLogController extends BaseController {

    @Autowired
    private FulltextRebuildLogService fulltextRebuidLogService;

    @GetMapping("listBySettingUuid")
    @ApiOperation(value = "获取索引重建日志列表", notes = "获取索引重建日志列表")
    public ApiResult<List<FulltextRebuildLogEntity>> listBySettingUuid(@RequestParam("settingUuid") Long settingUuid) {
        return ApiResult.success(fulltextRebuidLogService.listBySettingUuid(settingUuid));
    }

}
