/*
 * @(#)11/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.entity.DmsLibraryTemplateEntity;
import com.wellsoft.pt.dms.service.DmsLibraryTemplateService;
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
 * 11/26/25.1	    zhulh		11/26/25		    Create
 * </pre>
 * @date 11/26/25
 */
@Api(tags = "库模板接口")
@RestController
@RequestMapping("/api/dms/library/template")
public class ApiDmsLibraryTemplateController extends BaseController {

    @Autowired
    private DmsLibraryTemplateService libraryTemplateService;

    /**
     * 保存库模板
     *
     * @param templateEntity
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存库模板", notes = "保存库模板")
    public ApiResult<Long> save(@RequestBody DmsLibraryTemplateEntity templateEntity) {
        if (templateEntity.getUuid() == null) {
            templateEntity.setSystem(RequestSystemContextPathResolver.system());
        }
        libraryTemplateService.save(templateEntity);
        return ApiResult.success(templateEntity.getUuid());
    }

    /**
     * 获取库模板
     *
     * @return
     */
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取库模板", notes = "获取库模板")
    public ApiResult<DmsLibraryTemplateEntity> get(@RequestParam(name = "uuid") Long uuid) {
        return ApiResult.success(libraryTemplateService.getOne(uuid));
    }

    /**
     * 删除库模板
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除库模板", notes = "删除库模板")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<Long> uuids) {
        libraryTemplateService.deleteByUuids(uuids);
        return ApiResult.success();
    }

}
