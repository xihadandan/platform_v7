/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.weixin.dto.WeixinConfigDto;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.facade.service.WeixinOrgSyncFacadeService;
import com.wellsoft.pt.app.weixin.support.WeixinSyncLoggerHolder;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.app.weixin.vo.WeixinDepartmentVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
@Api(tags = "微信配置")
@RestController
@RequestMapping("/api/weixin/config")
public class ApiWeixinConfigConroller extends BaseController {

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinOrgSyncFacadeService weixinOrgSyncFacadeService;

    @ApiOperation(value = "查询当前租户系统微信配置", notes = "查询当前租户系统微信配置")
    @GetMapping("/getByCurrentSystem")
    public ApiResult<WeixinConfigDto> getByCurrentSystem() {
        WeixinConfigDto weixinConfigDto = weixinConfigFacadeService.getDtoBySystem(RequestSystemContextPathResolver.system());
        return ApiResult.success(weixinConfigDto);
    }

    @ApiOperation(value = "获取已启用的应用信息", notes = "获取已启用的应用信息")
    @GetMapping("/getEnabledAppInfo")
    public ApiResult<Map<String, Object>> getEnabledAppInfo() {
        Map<String, Object> appInfo = Maps.newHashMap();
        WeixinConfigDto weixinConfigDto = weixinConfigFacadeService.getDtoBySystem(RequestSystemContextPathResolver.system());
        if (BooleanUtils.isTrue(weixinConfigDto.getEnabled())) {
            appInfo.put("corpId", weixinConfigDto.getCorpId());
            appInfo.put("appId", weixinConfigDto.getAppId());
        }
        return ApiResult.success(appInfo);
    }

    @ApiOperation(value = "保存微信配置", notes = "保存微信配置")
    @PostMapping("/save")
    public ApiResult<Long> saveDto(@RequestBody WeixinConfigDto weixinConfigDto) {
        Long uuid = weixinConfigFacadeService.saveDto(weixinConfigDto);
        return ApiResult.success(uuid);
    }

    @ApiOperation(value = "测试生成访问凭证", notes = "测试生成访问凭证")
    @GetMapping("/testCreateToken")
    public ApiResult testCreateToken(@RequestParam(name = "corpId") String corpId, @RequestParam(name = "appSecret") String appSecret) {
        weixinConfigFacadeService.testCreateToken(corpId, appSecret);
        return ApiResult.success();
    }

    @ApiOperation(value = "组织同步", notes = "组织同步")
    @PostMapping("/syncOrg")
    public ApiResult<Boolean> syncOrg(@RequestBody WeixinConfigDto weixinConfigDto) {
        boolean result = true;
        try {
            Long uuid = weixinConfigFacadeService.saveDto(weixinConfigDto);
            WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoByUuid(uuid);
            WeixinSyncLoggerHolder.create(weixinConfigVo);
            List<WeixinDepartmentVo> departmentVos = WeixinApiUtils.departments(weixinConfigVo);
            weixinOrgSyncFacadeService.syncOrg(departmentVos, weixinConfigVo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            WeixinSyncLoggerHolder.error(e.getMessage());
            result = false;
        } finally {
            if (BooleanUtils.isNotTrue(WeixinSyncLoggerHolder.commit())) {
                result = false;
            }
        }
        return ApiResult.success(result);
    }

}
