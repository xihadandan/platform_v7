/*
 * @(#)4/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.web;

import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.dingtalk.dto.DingtalkConfigDto;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkOrgSyncFacadeService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkSyncLoggerHolder;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkEventUtils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkDepartmentVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/16/25.1	    zhulh		4/16/25		    Create
 * </pre>
 * @date 4/16/25
 */
@Api(tags = "钉钉配置")
@RestController
@RequestMapping("/api/dingtalk/config")
public class DingtalkConfigController extends BaseController {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @Autowired
    private DingtalkOrgSyncFacadeService dingtalkOrgSyncFacadeService;

    @ApiOperation(value = "查询当前租户系统钉钉配置", notes = "查询当前租户系统钉钉配置")
    @GetMapping("/getByCurrentSystem")
    public ApiResult<DingtalkConfigDto> getByCurrentSystem() {
        DingtalkConfigDto dingtalkConfigDto = dingtalkConfigFacadeService.getDtoBySystem(RequestSystemContextPathResolver.system());
        return ApiResult.success(dingtalkConfigDto);
    }

    @ApiOperation(value = "保存钉钉配置", notes = "保存钉钉配置")
    @PostMapping("/save")
    public ApiResult<Long> saveDto(@RequestBody DingtalkConfigDto dingtalkConfigDto) {
        Long uuid = dingtalkConfigFacadeService.saveDto(dingtalkConfigDto);
        return ApiResult.success(uuid);
    }

    @ApiOperation(value = "测试生成访问凭证", notes = "测试生成访问凭证")
    @GetMapping("/testCreateToken")
    public ApiResult testCreateToken(@RequestParam(name = "clientId") String clientId, @RequestParam(name = "clientSecret") String clientSecret,
                                     @RequestParam(name = "baseUrl") String baseUrl) {
        dingtalkConfigFacadeService.testCreateToken(clientId, clientSecret, baseUrl);
        return ApiResult.success();
    }

    @ApiOperation(value = "组织同步", notes = "组织同步")
    @PostMapping("/syncOrg")
    public ApiResult<Boolean> syncOrg(@RequestBody DingtalkConfigDto dingtalkConfigDto) {
        boolean result = true;
        try {
            Long uuid = dingtalkConfigFacadeService.saveDto(dingtalkConfigDto);
            DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoByUuid(uuid);
            DingtalkSyncLoggerHolder.create(dingtalkConfigVo);
            List<DingtalkDepartmentVo> departmentVos = DingtalkApiV2Utils.departments(dingtalkConfigVo);
            dingtalkOrgSyncFacadeService.syncOrg(departmentVos, dingtalkConfigVo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DingtalkSyncLoggerHolder.error(e.getMessage());
            result = false;
        } finally {
            if (BooleanUtils.isNotTrue(DingtalkSyncLoggerHolder.commit())) {
                result = false;
            }
        }
        return ApiResult.success(result);
    }

    @ApiOperation(value = "获取已启用的企业ID", notes = "获取已启用的企业ID")
    @GetMapping("/getEnabledCorpId")
    public ApiResult<String> getEnabledCorpId() {
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        return ApiResult.success(dingtalkConfigVo.getEnabled() ? dingtalkConfigVo.getCorpId() : null);
    }

    @ApiOperation(value = "获取已启用的Client ID", notes = "获取已启用的ClientID")
    @GetMapping("/getEnabledClientId")
    public ApiResult<String> getEnabledClientId() {
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        return ApiResult.success(dingtalkConfigVo.getEnabled() ? dingtalkConfigVo.getClientId() : null);
    }

    @ApiOperation(value = "获取未处理的事件", notes = "获取未处理的事件")
    @GetMapping("/unprocessedEvents")
    public ApiResult<List<GenericOpenDingTalkEvent>> unprocessedEvents() {
        List<GenericOpenDingTalkEvent> genericOpenDingTalkEvents = DingtalkEventUtils.getEvents();
        return ApiResult.success(genericOpenDingTalkEvents);
    }

}
