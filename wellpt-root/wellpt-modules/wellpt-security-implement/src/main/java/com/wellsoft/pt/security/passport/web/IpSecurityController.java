/*
 * @(#)2013-4-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.passport.entity.IpSecurityConfig;
import com.wellsoft.pt.security.passport.service.IpSecurityConfigService;
import com.wellsoft.pt.security.passport.web.request.IpSecurityConfigSaveRequest;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-30.1	zhulh		2013-4-30		Create
 * </pre>
 * @date 2013-4-30
 */
@Api(tags = "IP安全配置")
@RestController
@RequestMapping("/api/security/ip")
public class IpSecurityController extends BaseController {

    @Autowired
    IpSecurityConfigService ipSecurityConfigService;

    @GetMapping("/getAllIpSecurityConfig")
    public ApiResult<List<IpSecurityConfig>> getAllIpSecurityConfig(@RequestParam(required = false) String system,
                                                                    @RequestParam(required = false) String tenant) {
        return ApiResult.success(ipSecurityConfigService.getBySystemAndTenant(system, tenant));
    }


    @PostMapping("/saveAllIpSecurityConfig")
    public ApiResult<Boolean> saveAllIpSecurityConfig(@RequestBody IpSecurityConfigSaveRequest request) {
        ipSecurityConfigService.saveAllIpSecurityConfig(request.getSystem(), request.getTenant(), request.getConfig());
        return ApiResult.success(true);
    }
}
