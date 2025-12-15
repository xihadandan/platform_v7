/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.web;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.timer.dto.TsHolidayDto;
import com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService;
import com.wellsoft.pt.timer.facade.service.TsHolidayInstanceFacadeService;
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
 * Description: 节假日管理API接口
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
@Api(tags = "节假日管理")
@Controller
@RequestMapping("/api/ts/holiday")
public class ApiTsHolidayController extends BaseController {

    @Autowired
    private TsHolidayFacadeService holidayFacadeService;

    /**
     * 节假日管理查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "节假日管理按系统单位及名称查询", notes = "节假日管理按系统单位及名称查询")
    public ApiResult<List<TsHolidayDto>> getAllBySystemUnitIdsLikeName(
            @RequestParam(value = "name", required = false) String name) {
        return ApiResult.success(holidayFacadeService.getAllBySystemUnitIdsLikeName(name));
    }

    /**
     * 节假日管理查询
     *
     * @param keyword
     * @param tags
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeFields", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "节假日管理按系统单位及名称、日期、标签查询", notes = "节假日管理按系统单位及名称、日期、标签查询")
    public ApiResult<List<TsHolidayDto>> getAllBySystemUnitIdsLikeFields(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tags", required = false) String tags) {
        return ApiResult.success(holidayFacadeService.getAllBySystemUnitIdsLikeFields(keyword, tags));
    }

    /**
     * 获取节假日管理
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取节假日管理", notes = "获取节假日管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "节假日管理UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsHolidayDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(holidayFacadeService.getDto(uuid));
    }

    /**
     * 获取指定年份的节假日实例日期
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getHolidayInstanceDate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取指定年份的节假日实例日期", notes = "获取指定年份的节假日实例日期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "节假日管理UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getHolidayInstanceDate(@RequestParam(name = "uuid", required = true) String uuid,
                                                    @RequestParam(name = "year", required = true) String year) {
        ApplicationContextHolder.getBean(TsHolidayInstanceFacadeService.class).syncHolidayInstances();
        return ApiResult.success(holidayFacadeService.getHolidayInstanceDate(uuid, year));
    }

    /**
     * 保存节假日管理
     *
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存节假日管理", notes = "保存节假日管理")
    public ApiResult<Void> save(@RequestBody TsHolidayDto holidayDto) {
        holidayFacadeService.saveDto(holidayDto);
        return ApiResult.success();
    }

    /**
     * 删除节假日管理
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除节假日管理", notes = "删除节假日管理")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        holidayFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 判断节假日是否被使用
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断节假日是否被使用", notes = "判断节假日是否被使用")
    public ResultMessage isUsed(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        return holidayFacadeService.isUsedByUuids(uuids);
    }

    /**
     * 初始系统内置节假日
     *
     * @param system
     * @param tenant
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/initBuildInHoliday", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "初始系统内置节假日", notes = "初始系统内置节假日")
    public ApiResult<Void> initBuildInHoliday(@RequestParam(name = "system", required = true) String system,
                                              @RequestParam(name = "tenant", required = true) String tenant) {
        holidayFacadeService.initBySystemAndTenant(system, tenant);
        return ApiResult.success();
    }

}
