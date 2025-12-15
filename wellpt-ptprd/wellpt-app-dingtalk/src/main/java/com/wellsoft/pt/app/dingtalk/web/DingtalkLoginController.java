/*
 * @(#)4/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.web;

import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.dingtalk.api.response.OapiV2UserGetuserinfoResponse;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.PwdRoleCheckObj;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/27/25.1	    zhulh		4/27/25		    Create
 * </pre>
 * @date 4/27/25
 */
@Api(tags = "飞书登录")
@RestController
@RequestMapping("/api/dingtalk/")
public class DingtalkLoginController extends BaseController {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @Autowired
    private DingtalkUserService dingtalkUserService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Resource(name = BeanIds.USER_DETAILS_SERVICE)
    private UserDetailsService userDetailsService;

    @IgnoreResultAdvice
    @ApiIgnore
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @PostMapping("/auth")
    public ApiResult<DefaultUserDetails> getUserInfo(@RequestParam(name = "code") String authCode,
                                                     @RequestParam(name = "sso", required = false, defaultValue = "false") boolean ssoLogin) {
        try {
            String oaUserId = null;
            if (ssoLogin) {
                oaUserId = getOaUserIdBySsoCode(authCode);
            } else {
                oaUserId = getOaUserIdByQrCode(authCode);
            }

            UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByUserId(oaUserId);

            TenantContextHolder.setLoginType(LoginType.USER);
            DefaultUserDetails userDetails = (DefaultUserDetails) userDetailsService.loadUserByUsername(userInfoEntity.getLoginName());

            userDetails.setToken(JwtTokenUtil.createToken(userDetails));
            userDetails.setIsAllowMultiDeviceLogin(true);
            userDetails.setPwdRoleCheckObj(new PwdRoleCheckObj());
            return ApiResult.success(userDetails);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ApiResult.fail(e.getMessage());
        } finally {
            TenantContextHolder.reset();
        }
    }

    private String getOaUserIdBySsoCode(String authCode) {
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        OapiV2UserGetuserinfoResponse.UserGetByCodeResponse userGetByCodeResponse = DingtalkApiV2Utils.getUserInfoByCode(authCode, dingtalkConfigVo);
        if (userGetByCodeResponse == null) {
            throw new BusinessException("获取用户信息失败！");
        }

        String oaUserId = dingtalkUserService.getOaUserIdByUnionIdAndAppId(userGetByCodeResponse.getUnionid(), dingtalkConfigVo.getAppId());
        if (StringUtils.isBlank(oaUserId)) {
            throw new BusinessException("获取用户信息失败！");
        }
        return oaUserId;
    }

    private String getOaUserIdByQrCode(String authCode) {
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        String accessToken = DingtalkApiV2Utils.getUserAccessToken(authCode, dingtalkConfigVo);
        if (StringUtils.isBlank(accessToken)) {
            throw new BusinessException("授权失败！");
        }

        GetUserResponseBody getUserResponseBody = DingtalkApiV2Utils.getUserInfo(accessToken, dingtalkConfigVo);
        if (getUserResponseBody == null) {
            throw new BusinessException("获取用户信息失败！");
        }

        String oaUserId = dingtalkUserService.getOaUserIdByUnionIdAndAppId(getUserResponseBody.getUnionId(), dingtalkConfigVo.getAppId());
        if (StringUtils.isBlank(oaUserId)) {
            throw new BusinessException("获取用户信息失败！");
        }
        return oaUserId;
    }

}
