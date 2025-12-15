/*
 * @(#)3/31/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.web;

import com.google.common.collect.Maps;
import com.lark.oapi.service.authen.v1.model.CreateOidcAccessTokenRespBody;
import com.lark.oapi.service.authen.v1.model.GetUserInfoRespBody;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.PwdRoleCheckObj;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.net.URL;
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
 * 3/31/25.1	    zhulh		3/31/25		    Create
 * </pre>
 * @date 3/31/25
 */
@Api(tags = "飞书登录")
@RestController
@RequestMapping("/api/feishu/")
public class FeishuLoginController extends BaseController {

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Resource(name = BeanIds.USER_DETAILS_SERVICE)
    private UserDetailsService userDetailsService;

    @IgnoreResultAdvice
    @ApiOperation(value = "获取用户访问凭证信息", notes = "获取用户访问凭证信息")
    @PostMapping("/getUserTokenInfo")
    public ApiResult<Map<String, Object>> getUserTokenInfo(@RequestBody Map<String, String> requestMap) {
        FeishuConfigVo feishuConfigVo = feishuConfigService.query();
//        Map<String, Object> result = Maps.newHashMap();
//        result.put("code", 0);
//        result.put("msg", "get userinfo success");
        Map<String, Object> tokenInfo = getTokenInfo(requestMap, feishuConfigVo);
//        result.put("tokenInfo", tokenInfo);
//        if (Objects.toString(requestMap.get("fetchUserInfo"), "false").equals("true")) {
//            result.put("qrUserInfo", getUserInfo((String) tokenInfo.get("accessToken"), feishuConfigVo));
//        }
        return ApiResult.success(tokenInfo);
    }

    private Map<String, Object> getTokenInfo(Map<String, String> requestMap, FeishuConfigVo feishuConfigVo) {
        Map<String, Object> tokenInfo = Maps.newHashMap();
        String authorizationCode = extractAuthorizationCode(requestMap);
        if (StringUtils.isBlank(authorizationCode)) {
            return tokenInfo;
        }

        String appAccessToken = FeishuApiUtils.getAppAccessToken(feishuConfigVo);
        CreateOidcAccessTokenRespBody createOidcAccessTokenRespBody = FeishuApiUtils.createOidcAccessToken(appAccessToken, authorizationCode, feishuConfigVo);
        if (createOidcAccessTokenRespBody == null) {
            return tokenInfo;
        }

        createOidcAccessTokenRespBody.getAccessToken();
        tokenInfo.put("accessToken", createOidcAccessTokenRespBody.getAccessToken());
        tokenInfo.put("refreshToken", createOidcAccessTokenRespBody.getRefreshToken());
        tokenInfo.put("tokenType", createOidcAccessTokenRespBody.getTokenType());
        return tokenInfo;
    }


    private Map<String, Object> getUserInfo(String userAccessToken, FeishuConfigVo feishuConfigVo) {
        Map<String, Object> userInfo = Maps.newHashMap();
        if (StringUtils.isBlank(userAccessToken)) {
            return userInfo;
        }

        GetUserInfoRespBody getUserInfoRespBody = FeishuApiUtils.getUserInfo(userAccessToken, feishuConfigVo);
        if (getUserInfoRespBody == null) {
            return userInfo;
        }

        userInfo.put("name", getUserInfoRespBody.getName());
        userInfo.put("openId", getUserInfoRespBody.getOpenId());
        userInfo.put("userId", getUserInfoRespBody.getUserId());
        userInfo.put("tenantKey", getUserInfoRespBody.getTenantKey());
        userInfo.put("avatarUrl", getUserInfoRespBody.getAvatarUrl());

        return userInfo;
    }

    private String extractAuthorizationCode(Map<String, String> requestMap) {
        String code = requestMap.get("code");
        if (StringUtils.isNotBlank(code)) {
            return code;
        }
        String urlString = requestMap.get("url");
        if (StringUtils.isBlank(urlString)) {
            return StringUtils.EMPTY;
        }
        try {
            URL url = new URL(urlString);
            String[] pairs = url.getQuery().split("&");
            for (String pair : pairs) {
                if (StringUtils.startsWith(pair, "code=")) {
                    return StringUtils.substringAfter(pair, "=");
                }
            }
        } catch (Exception e) {
        }
        return StringUtils.EMPTY;
    }

    @IgnoreResultAdvice
    @ApiIgnore
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @PostMapping("/getUserInfo")
    public ApiResult<DefaultUserDetails> getUserInfo(@RequestParam(name = "userAccessToken") String userAccessToken) {
        FeishuConfigVo feishuConfigVo = feishuConfigService.query();
        Map<String, Object> userInfo = getUserInfo(userAccessToken, feishuConfigVo);
        String openId = (String) userInfo.get("openId");
        String oaUserId = feishuUserService.getOaUserIdByOpenIdAndConfigUuid(openId, feishuConfigVo.getUuid());
        UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByUserId(oaUserId);
        try {
            TenantContextHolder.setLoginType(LoginType.USER);
            DefaultUserDetails userDetails = (DefaultUserDetails) userDetailsService.loadUserByUsername(userInfoEntity.getLoginName());

            userDetails.setToken(JwtTokenUtil.createToken(userDetails));
            userDetails.setIsAllowMultiDeviceLogin(true);
            userDetails.setPwdRoleCheckObj(new PwdRoleCheckObj());
            return ApiResult.success(userDetails);
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
        } finally {
            TenantContextHolder.reset();
        }
        return ApiResult.fail();
    }

}
