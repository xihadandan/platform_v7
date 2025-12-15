/*
 * @(#)5/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.web;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinUserService;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
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
 * 5/27/25.1	    zhulh		5/27/25		    Create
 * </pre>
 * @date 5/27/25
 */
@Api(tags = "微信登录")
@RestController
@RequestMapping("/api/weixin/")
public class ApiWeixinLoginController extends BaseController {

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinUserService weixinUserService;

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
//            if (ssoLogin) {
//                oaUserId = getOaUserIdBySsoCode(authCode);
//            } else {
            oaUserId = getOaUserIdByQrCode(authCode);
//            }

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

    private String getOaUserIdByQrCode(String authCode) {
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(RequestSystemContextPathResolver.system());
        String userId = WeixinApiUtils.getUserIdByCode(authCode, weixinConfigVo);
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException("授权失败！");
        }

        String oaUserId = weixinUserService.getOaUserIdByUserIdAndCorpId(userId, weixinConfigVo.getCorpId());
        if (StringUtils.isBlank(oaUserId)) {
            throw new BusinessException("获取用户信息失败！");
        }
        return oaUserId;
    }

}
