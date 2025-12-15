/*
 * @(#)2020年12月25日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.preferences.web;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.preferences.dto.CdUserPreferencesDto;
import com.wellsoft.pt.basicdata.preferences.entity.CdUserPreferencesEntity;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 用户偏好api
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月25日.1	zhulh		2020年12月25日		Create
 * </pre>
 * @date 2020年12月25日
 */
@Api(tags = "用户偏好")
@Controller
@RequestMapping("/api/user/preferences")
public class ApiUserPreferencesController extends BaseController {

    @Autowired
    private CdUserPreferencesFacadeService cdUserPreferencesFacadeService;

    /**
     * 根据用户ID、模块ID、功能ID、数据键获取用户偏好
     *
     * @param userId
     * @param moduleId
     * @param functionId
     * @param dataKey
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取用户偏好", notes = "根据用户ID（不传的情况下是当前用户ID）、模块ID、功能ID、数据键获取用户偏好")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID（不传的情况下是当前用户ID）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionId", value = "功能ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "dataKey", value = "数据键", paramType = "query", dataType = "String", required = true)})
    public ApiResult<CdUserPreferencesDto> get(@RequestParam(name = "userId", required = false) String userId,
                                               @RequestParam(name = "moduleId", required = true) String moduleId,
                                               @RequestParam(name = "functionId", required = false, defaultValue = StringUtils.EMPTY) String functionId,
                                               @RequestParam(name = "dataKey", required = true) String dataKey) {
        String currentUserId = userId;
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        CdUserPreferencesDto cdUserPreferencesDto = cdUserPreferencesFacadeService.get(moduleId, functionId,
                currentUserId, dataKey);
        return ApiResult.success(cdUserPreferencesDto);
    }

    /**
     * 根据用户ID、模块ID、功能ID、数据键获取用户偏好数据值
     *
     * @param userId
     * @param moduleId
     * @param functionId
     * @param dataKey
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getValue", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取用户偏好数据值", notes = "根据用户ID（不传的情况下是当前用户ID）、模块ID、功能ID、数据键获取用户偏好数据值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID（不传的情况下是当前用户ID）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionId", value = "功能ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "dataKey", value = "数据键", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getValue(@RequestParam(name = "userId", required = false) String userId,
                                      @RequestParam(name = "moduleId", required = true) String moduleId,
                                      @RequestParam(name = "functionId", required = false, defaultValue = StringUtils.EMPTY) String functionId,
                                      @RequestParam(name = "dataKey", required = true) String dataKey) {
        String currentUserId = userId;
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        CdUserPreferencesDto cdUserPreferencesDto = cdUserPreferencesFacadeService.get(moduleId, functionId,
                currentUserId, dataKey);
        return ApiResult.success(cdUserPreferencesDto.getDataValue());
    }

    /**
     * 获取当前用户的主题偏好数据值
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getThemeDataValue", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取当前用户的主题偏好数据值", notes = "获取当前用户的主题偏好数据值")
    @ApiImplicitParams({})
    public ApiResult<String> getThemeDataValue() {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        if (StringUtils.isBlank(currentUserId)) {
            return ApiResult.fail("当前用户ID获取不到，请登录再试");
        }
        String value = cdUserPreferencesFacadeService.getThemeDataValue(currentUserId);
        return ApiResult.success(value);
    }

    /**
     * 保存用户偏好
     *
     * @param userId
     * @param moduleId
     * @param functionId
     * @param dataKey
     * @param dataValue
     * @param remark
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存用户偏好", notes = "保存用户偏好")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID（不传的情况下是当前用户ID）", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "moduleId", value = "模块ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "functionId", value = "功能ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "dataKey", value = "数据键", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataValue", value = "数据值", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> save(@RequestParam(name = "userId", required = false) String userId,
                                @RequestParam(name = "moduleId", required = true) String moduleId,
                                @RequestParam(name = "functionId", required = false, defaultValue = StringUtils.EMPTY) String functionId,
                                @RequestParam(name = "dataKey", required = true) String dataKey,
                                @RequestParam(name = "dataValue", required = true) String dataValue,
                                @RequestParam(name = "remark", required = true) String remark) {
        String currentUserId = userId;
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        cdUserPreferencesFacadeService.save(moduleId, functionId, currentUserId, dataKey, dataValue, remark);
        return ApiResult.success();
    }

    @ResponseBody
    @PostMapping(value = "/saveUserPreference", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<Boolean> saveUserPreference(@RequestBody CdUserPreferencesEntity data) {
        String currentUserId = data.getUserId();
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        cdUserPreferencesFacadeService.save(data.getModuleId(), data.getFunctionId(), currentUserId, data.getDataKey(), data.getDataValue(), data.getRemark());
        return ApiResult.success(true);
    }

    @ResponseBody
    @PostMapping(value = "/dropUserPreference", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<Boolean> dropUserPreference(@RequestBody CdUserPreferencesEntity data) {
        String currentUserId = data.getUserId();
        if (StringUtils.isBlank(currentUserId)) {
            currentUserId = SpringSecurityUtils.getCurrentUserId();
        }
        cdUserPreferencesFacadeService.delete(data.getModuleId(), data.getFunctionId(), currentUserId, data.getDataKey());
        return ApiResult.success(true);
    }

    /**
     * 保存用户偏好_保存主题信息
     *
     * @param dataValue
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/saveTheme", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存用户偏好_保存主题信息", notes = "保存用户偏好_保存主题信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataValue", value = "数据值", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Void> saveTheme(
            @RequestParam(name = "dataValue", required = true) String dataValue) {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        if (StringUtils.isBlank(currentUserId)) {
            return ApiResult.fail("当前用户ID获取不到，请登录再试");
        }
        cdUserPreferencesFacadeService.save(ModuleID.THEME.getValue(), "", currentUserId, ModuleID.THEME.getValue(), dataValue, "主题信息");
        return ApiResult.success();
    }

}
