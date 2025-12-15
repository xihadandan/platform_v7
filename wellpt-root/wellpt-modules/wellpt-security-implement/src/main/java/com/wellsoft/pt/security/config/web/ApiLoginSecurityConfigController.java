package com.wellsoft.pt.security.config.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.config.dto.AppLoginSecurityConfigDto;
import com.wellsoft.pt.security.config.entity.AppLoginSecurityConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginSecurityConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月07日   chenq	 Create
 * </pre>
 */
@Api(tags = "登录安全配置")
@RestController
@RequestMapping("/api/security/login")
public class ApiLoginSecurityConfigController extends BaseController {

    @Autowired
    AppLoginSecurityConfigService loginSecurityConfigService;

    @ApiOperation(value = "保存登录安全配置", notes = "保存登录安全配置")
    @PostMapping("/save")
    public ApiResult save(@ApiParam(value = "配置", required = true) @RequestBody AppLoginSecurityConfigDto dto) {
        AppLoginSecurityConfigEntity entity = loginSecurityConfigService.getBySystemAndTenant(dto.getSystem(), dto.getTenant());
        if (entity == null) {
            entity = new AppLoginSecurityConfigEntity();
        }
        entity.setIsAllowMultiDeviceLogin(dto.getIsAllowMultiDeviceLogin());
        loginSecurityConfigService.save(entity);
        return ApiResult.success();
    }

    @ApiOperation(value = "获取登录安全配置", notes = "获取登录安全配置", response = AppLoginSecurityConfigDto.class)
    @GetMapping("/getSystemUnitLoginSecurityConfig")
    public ApiResult getSystemUnitLoginSecurityConfig(@ApiParam(value = "系统", required = false) @RequestParam(required = false) String system
            , @RequestParam(required = false) String tenant) {
        AppLoginSecurityConfigEntity config = loginSecurityConfigService.getBySystemAndTenant(system, tenant);
        if (config != null) {
            AppLoginSecurityConfigDto dto = new AppLoginSecurityConfigDto();
            BeanUtils.copyProperties(config, dto);
            return ApiResult.success(dto);
        }
        return ApiResult.success();
    }


}
