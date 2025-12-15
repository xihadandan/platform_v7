package com.wellsoft.pt.security.web.controller;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.service.AuditDataLogService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Description: 安全服务管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/12.1	    zenghw		2021/3/12		    Create
 * </pre>
 * @date 2021/3/12
 */
@Api(tags = "安全服务管理接口")
@RestController
@RequestMapping("/api/security/")
public class SecurityApiController extends BaseController {

    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private UserDetailsServiceProvider userDetailsServiceProvider;
    @Autowired
    private AuditDataLogService auditDataLogService;

    @ApiOperation(value = "获取一个用户的所有授权角色", notes = "获取一个用户的所有授权角色")
    @GetMapping("/queryAllGrantedAuthoritiesByUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 10)
    public ApiResult<Set<GrantedAuthority>> queryAllGrantedAuthoritiesByUser(
            @RequestParam(name = "userId", required = true) String userId) {
        Set<GrantedAuthority> grantedAuthorities = securityApiFacade.queryAllGrantedAuthoritiesByUser(userId);
        return ApiResult.success(grantedAuthorities);
    }

    @ApiOperation(value = "按登录名loginName以及loginType生成用户token", notes = "按登录名loginName以及loginType生成用户token（仅开发环境才能执行）")
    @GetMapping("/createToken")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "登录名", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "loginType", value = "loginType 枚举值： 1:// 普通用户登录  ;2://管理员登录; 3://超级管理员登录 4://FJCA证书登录; 5://X509证书登录; 6://RESTful登录; 7://互联网用户登录", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 20)
    public ApiResult<String> createToken(@RequestParam(name = "loginName", required = true) String loginName,
                                         @RequestParam(name = "loginType", required = true) String loginType) {
        String active = System.getProperty(Config.KEY_APP_ENV);
        if (!Config.ENV_DEV.equals(active)) {
            return ApiResult.fail("仅开发环境才能执行");
        }
        try {
            UserDetails userDetails = userDetailsServiceProvider.getUserDetails(loginName);
            String token = JwtTokenUtil.createToken(userDetails);
            return ApiResult.success(token);
        } catch (Exception e) {
            logger.error("createToken:", e);
            return ApiResult.fail(e.getMessage());
        }
    }


    @PostMapping("/audit/dataLog")
    public ApiResult<Void> saveAuditDataLog(@RequestBody AuditDataLogDto dto) {
        auditDataLogService.saveAuditDataLog(dto);
        return ApiResult.success();
    }

}
