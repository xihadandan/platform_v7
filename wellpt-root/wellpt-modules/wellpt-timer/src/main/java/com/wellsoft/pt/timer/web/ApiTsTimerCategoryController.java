/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.timer.dto.TsTimerCategoryDto;
import com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 计时服务分类API接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
@Api(tags = "计时服务分类")
@Controller
@RequestMapping("/api/ts/timer/category")
public class ApiTsTimerCategoryController extends BaseController {

    @Autowired
    private TsTimerCategoryFacadeService timerCategoryFacadeService;

    /**
     * 计时服务分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "计时服务分类按系统单位及名称查询", notes = "计时服务分类按系统单位及名称查询")
    public ApiResult<List<TsTimerCategoryDto>> getAllBySystemUnitIdsLikeName(
            @RequestParam(value = "name", required = false) String name) {
        return ApiResult.success(timerCategoryFacadeService.getAllBySystemUnitIdsLikeName(name));
    }

    /**
     * 获取计时服务分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取计时服务分类", notes = "获取计时服务分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "计时服务分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsTimerCategoryDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(timerCategoryFacadeService.getDto(uuid));
    }

    /**
     * 保存计时服务分类
     *
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存计时服务分类", notes = "保存计时服务分类")
    public ApiResult<Void> save(@RequestBody TsTimerCategoryDto timerCategoryDto) {
        timerCategoryFacadeService.saveDto(timerCategoryDto);
        return ApiResult.success();
    }

    /**
     * 删除计时服务分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除计时服务分类", notes = "删除计时服务分类")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        timerCategoryFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 判断计时服务分类是否被使用
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断计时服务分类是否被使用", notes = "判断计时服务分类是否被使用")
    public ResultMessage isUsed(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        return timerCategoryFacadeService.isUsedByUuids(uuids);
    }

}
