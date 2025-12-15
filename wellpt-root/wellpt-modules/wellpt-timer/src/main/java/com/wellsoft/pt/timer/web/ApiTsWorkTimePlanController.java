/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.web;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanHistoryDto;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.support.WorkTime;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作时间方案API接口
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
@Api(tags = "工作时间方案")
@Controller
@RequestMapping("/api/ts/work/time/plan")
public class ApiTsWorkTimePlanController extends BaseController {

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    /**
     * 获取系统当前时间
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getSysDate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取系统当前时间", notes = "获取系统当前时间")
    public ApiResult<Date> getSysDate() {
        return ApiResult.success(workTimePlanFacadeService.getSysDate());
    }

    /**
     * 工作时间方案查询
     *
     * @param name
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "工作时间方案查询按系统单位及名称查询", notes = "工作时间方案查询按系统单位及名称查询")
    public ApiResult<List<TsWorkTimePlanDto>> getAllBySystemUnitIdsLikeName(
            @RequestParam(value = "name", required = false) String name) {
        return ApiResult.success(workTimePlanFacadeService.getAllBySystemUnitIdsLikeName(name));
    }


    @ResponseBody
    @PostMapping(value = "/getAllBySystem", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "工作时间方案查询按系统查询", notes = "工作时间方案查询按系统查询")
    public ApiResult<List<TsWorkTimePlanDto>> getAllBySystem(@RequestBody List<String> system) {
        return ApiResult.success(workTimePlanFacadeService.getAllBySystem(system));
    }

    /**
     * 工作时间方案设置为默认方案
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/setAsDefault", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "工作时间方案设置为默认方案", notes = "工作时间方案设置为默认方案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作时间方案UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> setAsDefault(@RequestParam(name = "uuid", required = true) String uuid) {
        workTimePlanFacadeService.setAsDefaultByUuid(uuid);
        return ApiResult.success();
    }

    /**
     * 获取工作时间方案
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作时间方案", notes = "获取工作时间方案")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作时间方案UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsWorkTimePlanDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(workTimePlanFacadeService.getDto(uuid));
    }

    /**
     * 获取工作时间方案
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/history/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作时间方案历史信息", notes = "获取工作时间方案历史信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作时间方案历史UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsWorkTimePlanHistoryDto> historyGet(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(workTimePlanFacadeService.getHistoryDto(uuid));
    }

    /**
     * 根据工作时间方案UUID列表获取存在新版本的信息提示
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/listNewVersionTipByUuids", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据工作时间方案UUID列表获取存在新版本的信息提示", notes = "根据工作时间方案UUID列表获取存在新版本的信息提示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuids", value = "工作时间方案UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Map<String, Object>> listNewVersionTipByUuids(
            @RequestParam(name = "uuids", required = true) List<String> uuids) {
        return ApiResult.success(workTimePlanFacadeService.listNewVersionTipByUuids(uuids));
    }

    /**
     * 获取工作时间方案的最高版本
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getMaxVersionByUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作时间方案的最高版本", notes = "获取工作时间方案的最高版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "工作时间方案UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getMaxVersionByUuid(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(workTimePlanFacadeService.getMaxVersionByUuid(uuid));
    }

    /**
     * 保存工作时间方案
     *
     * @param workTimePlanDto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存工作时间方案", notes = "保存工作时间方案")
    public ApiResult<String> save(@RequestBody TsWorkTimePlanDto workTimePlanDto) {
        return ApiResult.success(workTimePlanFacadeService.saveDto(workTimePlanDto));
    }

    /**
     * 保存工作时间方案为新版本
     *
     * @param workTimePlanDto
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveAsNewVersion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存工作时间方案为新版本", notes = "保存工作时间方案为新版本")
    public ApiResult<String> saveAsNewVersion(@RequestBody TsWorkTimePlanDto workTimePlanDto) {
        return ApiResult.success(workTimePlanFacadeService.saveAsNewVersion(workTimePlanDto));
    }

    /**
     * 删除工作时间方案
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除工作时间方案", notes = "删除工作时间方案")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        workTimePlanFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 判断工作时间方案是否被使用
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断工作时间方案是否被使用", notes = "判断工作时间方案是否被使用")
    public ResultMessage isUsed(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        return workTimePlanFacadeService.isUsedByUuids(uuids);
    }

    /**
     * 获取工作日
     *
     * @param workTimePlanUuid
     * @param fromDate
     * @param amount
     * @param workUnit
     * @param autoDelay
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkDate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作日", notes = "获取工作日")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "fromDate", value = "开始日期，日期格式yyyy-MM-dd、yyyy-MM-dd HH、yyyy-MM-dd HH:mm或yyyy-MM-dd HH:mm:ss", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "amout", value = "工作时间增量", paramType = "query", dataType = "Double", required = true),
            @ApiImplicitParam(name = "workUnit", value = "工作时间单位", paramType = "query", dataType = "WorkUnit", required = true),
            @ApiImplicitParam(name = "autoDelay", value = "是否自动推迟到下一开始工作时间点", paramType = "query", dataType = "Boolean", required = false)})
    public ApiResult<Date> getWorkDate(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                       @RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "amout") double amount,
                                       @RequestParam(name = "workUnit") WorkUnit workUnit,
                                       @RequestParam(name = "autoDelay", required = false, defaultValue = "false") boolean autoDelay) {
        Date workDate = null;
        try {
            Date fromDateObject = DateUtils.parse(fromDate);
            workDate = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, fromDateObject, amount, workUnit, autoDelay);
        } catch (ParseException e) {
            throw new BusinessException(e);
        }
        return ApiResult.success(workDate);
    }

    /**
     * 获取下一工作日
     *
     * @param workTimePlanUuid
     * @param fromDate
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getNextWorkDate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取下一工作日", notes = "获取下一工作日")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "fromDate", value = "开始时间", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Date> getNextWorkDate(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                           @RequestParam(name = "fromDate") String fromDate) {
        Date workDate = null;
        try {
            Date fromDateObject = DateUtils.parse(fromDate);
            workDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, fromDateObject);
        } catch (ParseException e) {
            throw new BusinessException(e);
        }
        return ApiResult.success(workDate);
    }

    /**
     * 获取前一工作日
     *
     * @param workTimePlanUuid
     * @param fromDate
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getPrevWorkDate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取前一工作日", notes = "获取前一工作日")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "fromDate", value = "开始时间", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Date> getPrevWorkDate(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                           @RequestParam(name = "fromDate") String fromDate) {
        Date workDate = null;
        try {
            Date fromDateObject = DateUtils.parse(fromDate);
            workDate = workTimePlanFacadeService.getPrevWorkDate(workTimePlanUuid, fromDateObject);
        } catch (ParseException e) {
            throw new BusinessException(e);
        }
        return ApiResult.success(workDate);
    }

    /**
     * 判断指定的日期是否工作日
     *
     * @param workTimePlanUuid
     * @param date
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isWorkDay", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断指定的日期是否工作日", notes = "判断指定的日期是否工作日")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "date", value = "日期", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isWorkDay(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                        @RequestParam(name = "date") String date) {
        boolean isWorkDate = false;
        try {
            Date dateObject = DateUtils.parse(date);
            isWorkDate = workTimePlanFacadeService.isWorkDay(workTimePlanUuid, dateObject);
        } catch (ParseException e) {
            isWorkDate = false;
        }
        return ApiResult.success(isWorkDate);
    }

    /**
     * 判断指定的时间是否工作时间
     *
     * @param workTimePlanUuid
     * @param date
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/isWorkHour", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断指定的时间是否工作时间", notes = "判断指定的时间是否工作时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "date", value = "日期", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> isWorkHour(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                         @RequestParam(name = "date") String date) {
        boolean isWorkHour = false;
        try {
            Date dateObject = DateUtils.parse(date);
            isWorkHour = workTimePlanFacadeService.isWorkHour(workTimePlanUuid, dateObject);
        } catch (ParseException e) {
            isWorkHour = false;
        }
        return ApiResult.success(isWorkHour);
    }

    /**
     * 获取工作日的工作时间
     *
     * @param workTimePlanUuid
     * @param workDate
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkTime", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作日的工作时间", notes = "获取工作日的工作时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "workDate", value = "工作日", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkTime> getWorkTime(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                           @RequestParam(name = "workDate") String workDate) {
        WorkTime workTime = null;
        try {
            Date workDateObject = DateUtils.parse(workDate);
            workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, workDateObject);
        } catch (ParseException e) {
            throw new BusinessException(e);
        }
        return ApiResult.success(workTime);
    }

    /**
     * 获取工作时间段信息
     *
     * @param workTimePlanUuid
     * @param fromTime
     * @param toTime
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getWorkTimePeriod", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取工作时间段信息", notes = "获取工作时间段信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workTimePlanUuid", value = "工作时间方案UUID，为空取默认工作时间方案", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "fromTime", value = "开始时间", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "toTime", value = "结束时间", paramType = "query", dataType = "String", required = true)})
    public ApiResult<WorkTimePeriod> getWorkTimePeriod(@RequestParam(name = "workTimePlanUuid", required = false, defaultValue = "") String workTimePlanUuid,
                                                       @RequestParam(name = "fromTime") String fromTime,
                                                       @RequestParam(name = "toTime") String toTime) {
        WorkTimePeriod workTimePeriod = null;
        try {
            Date fromTimeObject = DateUtils.parse(fromTime);
            Date toTimeObject = DateUtils.parse(toTime);
            workTimePeriod = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, fromTimeObject, toTimeObject);
        } catch (ParseException e) {
            throw new BusinessException(e);
        }
        return ApiResult.success(workTimePeriod);
    }


}
