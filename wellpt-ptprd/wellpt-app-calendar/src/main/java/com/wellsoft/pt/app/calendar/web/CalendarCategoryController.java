/*
 * @(#)2020年12月8日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.app.calendar.facade.service.CalendarFacade;
import com.wellsoft.pt.app.calendar.service.MyCalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月27日.1	zhongzh		2021年7月27日		Create
 * </pre>
 * @date 2021年7月27日
 */
@Api(tags = "日程分类")
@Controller
@RequestMapping("/api/calendar/category")
public class CalendarCategoryController extends BaseController {

    @Autowired
    private CalendarFacade calendarFacade;

    @Autowired
    private MyCalendarService myCalendarService;

    /**
     * 日程分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getAllBySystemUnitIdsLikeName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "日程分类按系统单位及名称查询", notes = "日程分类按系统单位及名称查询")
    public ApiResult<List<MyCalendarEntity>> getAllBySystemUnitIdsLikeName() {
        return ApiResult.success(calendarFacade.queryAppMyCalendarListAndCheckDefaultCalendar());
    }

    /**
     * 日程分类查询
     *
     * @param keyword
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "日程分类查询", notes = "日程分类查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "查询关键字", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "uuids", value = "日程分类UUID，多个以逗号隔开", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNo", value = "页码", paramType = "query", dataType = "int", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "query", dataType = "int", required = false)})
    public ApiResult<List<MyCalendarEntity>> query(@RequestParam(name = "keyword") String keyword,
                                                   @RequestParam(name = "uuids", required = false) List<String> uuids,
                                                   @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ApiResult.success(null);
    }

    /**
     * 获取日程分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取日程分类", notes = "获取日程分类")
    @ApiImplicitParams({@ApiImplicitParam(name = "uuid", value = "日程分类UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<MyCalendarEntity> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(myCalendarService.getBean(uuid));
    }

    /**
     * 保存日程分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存日程分类", notes = "保存日程分类")
    public ApiResult<Void> generateCalendarCategoryCode(@RequestBody MyCalendarEntity bean) {
        if (StringUtils.isBlank(bean.getPublicRange()) || StringUtils.isBlank(bean.getPublicRangeValue())
                || StringUtils.isBlank(bean.getPublicRangeShow())) {
            JSONObject j = new JSONObject();
            try {
                j.put("0", "不公开");
                bean.setPublicRange(j.toString());
                bean.setPublicRangeValue("0");
                bean.setPublicRangeShow("不公开");
            } catch (JSONException e) {
            }
        }
        myCalendarService.saveBean(bean);
        return ApiResult.success();
    }

    /**
     * 生成日程分类编号
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/generateCalendarCategoryCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "生成日程分类编号", notes = "生成日程分类编号")
    public ApiResult<String> generateCalendarCategoryCode() {
        return ApiResult.success(myCalendarService.generateCalendarCategoryCode());
    }

    /**
     * 删除日程分类
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除日程分类", notes = "删除日程分类")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        myCalendarService.removeAllByPk(uuids);
        return ApiResult.success();
    }

    /**
     * 删除没用的日程分类
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteWhenNotUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除没用的日程分类", notes = "删除没用的日程分类")
    public ApiResult<Integer> deleteWhenNotUsed(@RequestParam(name = "uuid", required = true) String uuid) {
        myCalendarService.delete(uuid);
        return ApiResult.success(0);
    }

}
