/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.web;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Description: 计时服务API接口
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
@Api(tags = "计时服务配置")
@Controller
@RequestMapping("/api/ts/timer/config")
public class ApiTsTimerConfigController extends BaseController {

    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    /**
     * 获取计时器配置
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取计时器配置", notes = "获取计时器配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "计时器配置UUID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsTimerConfigDto> get(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(timerConfigFacadeService.getDto(uuid));
    }

    /**
     * 获取计时器配置
     *
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取计时器配置", notes = "获取计时器配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "计时器配置ID", paramType = "query", dataType = "String", required = true)})
    public ApiResult<TsTimerConfigDto> getById(@RequestParam(name = "id", required = true) String id) {
        return ApiResult.success(timerConfigFacadeService.getDtoById(id));
    }

    /**
     * 获取计时器配置下拉选择数据
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/selectdata", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取计时器配置下拉选择数据", notes = "获取计时器配置下拉选择数据")
    public ApiResult<List<Select2DataBean>> getSelectData(@RequestParam(name = "excludedCategoryId") String excludedCategoryId) {
        List<String> systemUnitIds = Lists.newArrayList();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        List<TsTimerConfigEntity> timerConfigEntities = timerConfigFacadeService.listBySystemAndTenant(system, tenant, excludedCategoryId);
        Collections.sort(timerConfigEntities, IdEntityComparators.CREATE_TIME_ASC);
        List<Select2DataBean> dataBeans = Lists.newArrayList();
        for (TsTimerConfigEntity timerConfig : timerConfigEntities) {
            String id = timerConfig.getId();
            EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timerConfig.getTimingMode());
            String text = timerConfig.getName() + "——"
                    + (enumTimingMode != null ? enumTimingMode.getName() : "计时方式未配置");
            Select2DataBean bean = new Select2DataBean(id, text);
            dataBeans.add(bean);
        }
        return ApiResult.success(dataBeans);
    }

    /**
     * 保存计时器配置
     *
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存计时器配置", notes = "保存计时器配置")
    public ApiResult<Void> save(@RequestBody TsTimerConfigDto timerConfigDto) {
        timerConfigFacadeService.saveDto(timerConfigDto);
        return ApiResult.success();
    }

    /**
     * 删除计时器配置
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除计时器配置", notes = "删除计时器配置")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        timerConfigFacadeService.deleteAll(uuids);
        return ApiResult.success();
    }

    /**
     * 判断计时器配置是否被使用
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/isUsed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "判断计时器配置是否被使用", notes = "判断计时器配置是否被使用")
    public ResultMessage isUsed(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        return timerConfigFacadeService.isUsedByUuids(uuids);
    }

    /**
     * @param timerUuid
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getTimerWorkTime", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取计时器的工作时间信息", notes = "获取计时器的工作时间信息")
    public ApiResult<TimerWorkTime> getTimerWorkTime(@RequestParam(name = "timerUuid", required = true) String timerUuid) {
        return ApiResult.success(timerFacadeService.getTimerWorkTime(timerUuid));
    }

}
