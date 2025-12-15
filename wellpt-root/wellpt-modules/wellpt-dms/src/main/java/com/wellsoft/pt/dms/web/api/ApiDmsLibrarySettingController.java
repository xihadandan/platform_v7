/*
 * @(#)11/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.entity.DmsLibrarySettingEntity;
import com.wellsoft.pt.dms.service.DmsLibrarySettingService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
 * 11/20/25.1	    zhulh		11/20/25		    Create
 * </pre>
 * @date 11/20/25
 */
@Api(tags = "库分类接口")
@RestController
@RequestMapping("/api/dms/library/setting")
public class ApiDmsLibrarySettingController extends BaseController {

    @Autowired
    private DmsLibrarySettingService librarySettingService;

    /**
     * 保存库设置
     *
     * @param settingEntity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存库设置", notes = "保存库设置")
    public ApiResult<Long> save(@RequestBody DmsLibrarySettingEntity settingEntity) {
        if (settingEntity.getUuid() == null) {
            settingEntity.setSystem(RequestSystemContextPathResolver.system());
        }
        librarySettingService.save(settingEntity);
        return ApiResult.success(settingEntity.getUuid());
    }

    /**
     * 获取库分类
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取库分类", notes = "获取库分类")
    public ApiResult<DmsLibrarySettingEntity> get() {
        return ApiResult.success(librarySettingService.getBySystem(RequestSystemContextPathResolver.system()));
    }

}
