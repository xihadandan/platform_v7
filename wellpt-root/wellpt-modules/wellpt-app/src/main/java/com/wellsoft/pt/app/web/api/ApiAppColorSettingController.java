/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.web.api;

import com.wellsoft.context.web.controller.AbstractJsonDataServicesController;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.dto.AppColorSettingClassifyDto;
import com.wellsoft.pt.app.service.AppColorSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Api(tags = "颜色管理控制层_restful接口")
@RestController
@RequestMapping("/api/webapp/color/setting")
public class ApiAppColorSettingController extends AbstractJsonDataServicesController {

    @Resource
    private AppColorSettingService appColorSettingService;

    /**
     *
     */
    @ApiOperation(value = "保存", notes = "保存")
    @PostMapping("/saveBean")
    public void saveBean(@RequestBody AppColorSettingClassifyDto appColorSettingSaveDto)
            throws IOException {
        appColorSettingService.saveBean(appColorSettingSaveDto);
    }

    /**
     *
     */
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping("/deleteBean")
    public void deleteBean(@RequestParam String moduleCode, @RequestParam String type)
            throws IOException {
        appColorSettingService.deleteBean(moduleCode, type);
    }

    /**
     *
     */
    @ApiOperation(value = "获取", notes = "获取")
    @GetMapping("/getAllBean")
    public ApiResult<List<AppColorSettingClassifyDto>> getAllBean()
            throws IOException {
        List<AppColorSettingClassifyDto> appColorSettingSaveDtoList = appColorSettingService.getAllBean();
        return ApiResult.success(appColorSettingSaveDtoList);
    }

    /**
     *
     */
    @ApiOperation(value = "获取", notes = "获取")
    @GetMapping("/getBeanByFilter")
    public ApiResult<AppColorSettingClassifyDto> getBeanByFilter(@RequestParam String moduleCode, @RequestParam String type)
            throws IOException {
        AppColorSettingClassifyDto appColorSettingClassifyDto = appColorSettingService.getBean(moduleCode, type);
        return ApiResult.success(appColorSettingClassifyDto);
    }


}
