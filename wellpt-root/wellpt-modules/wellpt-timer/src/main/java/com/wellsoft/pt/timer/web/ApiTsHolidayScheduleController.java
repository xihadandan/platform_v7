/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.timer.dto.TsHolidayPerYearScheduleCountDto;
import com.wellsoft.pt.timer.dto.TsHolidayScheduleDto;
import com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService;
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
 * Description: 节假日安排API接口
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
@Api(tags = "节假日安排")
@Controller
@RequestMapping("/api/ts/holiday/schedule")
public class ApiTsHolidayScheduleController extends BaseController {

    @Autowired
    private TsHolidayScheduleFacadeService holidayScheduleFacadeService;

    /**
     * 获取节假安排所有年份列表
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listAllYear", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取节假安排所有年份列表", notes = "获取节假安排所有年份列表")
    public ApiResult<List<TsHolidayPerYearScheduleCountDto>> listAllYear() {
        return ApiResult.success(holidayScheduleFacadeService.listAllYear());
    }

    /**
     * 获取节假日安排
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listByYear", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取节假日安排", notes = "获取节假日安排")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<TsHolidayScheduleDto>> listByYear(@RequestParam(name = "year", required = true) String year) {
        return ApiResult.success(holidayScheduleFacadeService.listByYear(year));
    }

    /**
     * 保存节假日安排
     *
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存节假日安排", notes = "保存节假日安排")
    public ApiResult<Void> saveAll(@RequestParam(name = "year", required = true) String year,
                                   @RequestBody List<TsHolidayScheduleDto> holidayDtos) {
        holidayScheduleFacadeService.saveAllDtos(year, holidayDtos);
        return ApiResult.success();
    }

    /**
     * 删除节假日安排
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除节假日安排", notes = "删除节假日安排")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        holidayScheduleFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

}
