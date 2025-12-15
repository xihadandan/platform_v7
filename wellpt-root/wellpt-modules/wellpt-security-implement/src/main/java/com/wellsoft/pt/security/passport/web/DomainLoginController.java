/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.web;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.filter.RedirectAuthorizedHomePageFilter;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.common.verification.entity.VerifyCode;
import com.wellsoft.pt.common.verification.service.VerifyCodeService;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserInfo;
import com.wellsoft.pt.multi.org.enums.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.UserLoginLogEntity;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import com.wellsoft.pt.security.config.service.AppLoginSecurityConfigService;
import com.wellsoft.pt.security.core.authentication.CustomDaoAuthenticationProvider;
import com.wellsoft.pt.security.core.userdetails.*;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.event.LogoutSuccessEvent;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.passport.enums.EnumPasswordEncryptType;
import com.wellsoft.pt.security.passport.enums.EnumSwtich;
import com.wellsoft.pt.security.passport.service.TokenService;
import com.wellsoft.pt.security.support.CasLoginUtils;
import com.wellsoft.pt.security.util.DesCipherUtil;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Description: 租户个性域名登录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-25.1	zhulh		2012-12-25		Create
 * </pre>
 * @date 2012-12-25
 */
@Api(tags = "租户个性域名登录")
@RestController
public class DomainLoginController extends BaseController {

    @Resource(name = BeanIds.USER_DETAILS_SERVICE)
    UserDetailsService userDetailsService;
    @Autowired
    MultiOrgUserService multiOrgUserService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private TenantFacadeService tenantService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private CustomDaoAuthenticationProvider customDaoAuthenticationProvider;
    @Autowired
    private AppLoginPageConfigService appLoginPageConfigService;
    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;
    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private AppLoginSecurityConfigService appLoginSecurityConfigService;
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountService;
    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Value("${pt.oauth2.login.url:}")
    private String oauth2LoginUrl;
    @Value("${spring.security.pwd.encrypt.type:1}")
    /**
     *	// 密码加密模式: 默认1=base64
     *
     **/
    private String passwordEncryptType;
    @Autowired(required = false)
    private TokenService tokenService;

    @Autowired
    private UserInfoFacadeService userInfoFacadeService;

    /**
     * 单位登录
     *
     * @param request
     * @param response
     * @param unitCode
     * @param model
     * @return
     */
    @ApiIgnore
    @ApiOperation(value = "单位登录", notes = "单位登录", tags = {"暂时没用到"})
    @GetMapping("/login/u/{unitCode:[a-zA-Z_0-9]+}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "unitCode", value = "单位unitCode", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "model", value = "Model", paramType = "query", dataType = "String", required = true)})
    public String loginUnit(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("unitCode") String unitCode, Model model) {
        // 获取当前登录用户
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user != null) {
            // 用户已登录跳转到用户首页
            return redirect("/passport/user/login/success");
        }

        if (StringUtils.isNotBlank(unitCode)) {
            MultiOrgSystemUnit unit = orgApiFacade.getSystemUnitByCode(unitCode);
            if (unit == null) {
                throwNotFoundResponse(response);
            }
            // 单位的登录页面
            AppLoginPageConfigEntity pageConfig = appLoginPageConfigService.getBySystemUnitId(unit.getId());
            if (pageConfig == null) {
                pageConfig = appLoginPageConfigService.saveInitPageConfig(Config.DEFAULT_TENANT);
            }
            return forwardToLoginPage(pageConfig, response, model);

        }

        return "";
    }

    @PostMapping("/user/loginLog")
    public ApiResult loginLog(@RequestBody UserLoginLogEntity logEntity) {
        userInfoFacadeService.saveUserLoginLog(logEntity);
        return ApiResult.success();
    }

    @ApiOperation(value = "返回主页", notes = "跳转登录后的页面", tags = {"单位管理员登录"})
    @GetMapping("/tenant/{tenant:[a-zA-Z_0-9]+}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest url的参数：<br/> loginType：登录类型；<br/>"),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse"),
            @ApiImplicitParam(name = "tenantId", value = "租户ID"), @ApiImplicitParam(name = "model", value = "Model")})
    public String home(HttpServletRequest request, HttpServletResponse response,
                       @PathVariable("tenant") String tenantId, Model model) {
        // 获取当前登录用户
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user != null) {
            // 用户已登录跳转到用户首页
            if (user.getTenant().equals(tenantId)) {
                return redirect("/passport/user/login/success");
            }
            try {
                // 跳转到用户不存在页面
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.sendError(HttpStatus.NOT_FOUND.value());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
            // 跳转到用户不存在页面
            // return forward("/user/notexists");
        }

        // if (Config.getOAuthEnable()) {//oauth2认证登录
        // try {
        // response.sendRedirect("/login/oauth2");
        // } catch (IOException e) {
        //
        // }
        // return null;
        // }

        // 跳转到租户各自的登录页面
        model.addAttribute("tenant", tenantId);
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("loginType", request.getParameter("loginType"));
        // redirectAttributes.addFlashAttribute("tenant", tenant);
        // 获取默认租户的登录页面
        AppLoginPageConfigEntity pageConfig = appLoginPageConfigService.getBySystemUnitId(Config.DEFAULT_TENANT);
        if (request.getCookies() != null) {
            for (Cookie ck : request.getCookies()) {
                if (ck.getName().equalsIgnoreCase("cookie.current.userId")) {
                    MultiOrgUserAccount account = orgApiFacade.getAccountByUserId(ck.getValue());
                    if (account != null) {
                        pageConfig = appLoginPageConfigService.getBySystemUnitId(account.getSystemUnitId());
                        break;
                    }
                }
            }
        }

        if (pageConfig == null) {
            pageConfig = appLoginPageConfigService.saveInitPageConfig(Config.DEFAULT_TENANT);
        }
        return forwardToLoginPage(pageConfig, response, model);
    }

    @ApiOperation(value = "用户已登录跳转到用户首页", notes = "用户已登录跳转到用户首页", tags = {"单位管理员登录"})
    @GetMapping("/passport/user/login/success")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "model", value = "Model", paramType = "query", dataType = "String", required = true)})
    public String success(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user != null) {
            Boolean checkedSmsVerifyCode = null;
            // 断判断是否需要客户端短信验证码
            if (securityApiFacade.isRequiredSmsVerifyCode(user.getUserId(), ServletUtils.getRemoteAddr(request))
                    && ((checkedSmsVerifyCode = (Boolean) request.getSession()
                    .getAttribute("checkedSmsVerifyCode")) == null || checkedSmsVerifyCode != true)) {
                request.setAttribute("requiredSmsVerifyCode", true);

                AppLoginPageConfigEntity pageConfig = appLoginPageConfigService
                        .getBySystemUnitId(MultiOrgSystemUnit.PT_ID);
                if (pageConfig == null) {
                    pageConfig = appLoginPageConfigService.saveInitPageConfig(MultiOrgSystemUnit.PT_ID);
                }
                return forward("/user/domain_login3" + StringUtils.trimToEmpty(pageConfig.getPageStyle()));
            }
            // try {
            // Cookie userNameCookie = new Cookie("cookie.name.user",
            // URLEncoder.encode(user.getLoginName(), "UTF-8"));
            // userNameCookie.setMaxAge(30 * 60);
            // userNameCookie.setPath(getCookiePath(request));
            // userNameCookie.setSecure(request.isSecure());
            // response.addCookie(userNameCookie);

            // Cookie tenantAccountCookie = new Cookie("cookie.tenant.account",
            // URLEncoder.encode(user.getTenant(),
            // "UTF-8"));
            // tenantAccountCookie.setMaxAge(60 * 60 * 24 * 7);
            // tenantAccountCookie.setPath(getCookiePath(request));
            // tenantAccountCookie.setSecure(request.isSecure());
            // response.addCookie(tenantAccountCookie);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }

            request.getSession().setAttribute("tenantId", user.getTenantId());
            request.getSession().setAttribute("userId", user.getUserId());

            // System.out.println(unitApiFacade.getCommonUnitByUserId(user.getUser().getId()));

            if (user.isSuperAdmin()) {
                return redirect("/superadmin/main?version=2.0");
            } else if (user.isAdmin()) {
                return redirect("/passport/admin/main?version=2.0");
            } else if (LoginType.RESTful.equals(user.getLoginType())) {
                String url = request.getHeader("referer");
                return redirect(url);
            } else {
                return redirect(RedirectAuthorizedHomePageFilter.SECURITY_HOMEPAGE);
            }
        }
        // request.getSession(false).invalidate();
        if (CasLoginUtils.isUseCas()) {
            return redirect("/j_spring_cas_security_logout");
        } else {
            return redirect("/security_logout");
        }

    }

    /**
     * 兼容旧的登出地址
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping("/security_logout")
    @ApiOperation(value = "兼容旧的登出地址", notes = "兼容旧的登出地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "model", value = "Model", paramType = "query", dataType = "String", required = true)})
    public String securityLogout(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        UserDetailsCacheHolder.removeUserFromCache(userDetails.getUsername());
        // 发布退出成功事件
        ApplicationContextHolder.getApplicationContext().publishEvent(new LogoutSuccessEvent(userDetails));
        return redirect("/j_spring_security_logout");
    }

    protected String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : "/";
    }

    /**
     * 互联网统一认证登录
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/iuser/login")
    @ApiOperation(value = "互联网统一认证登录", notes = "互联网统一认证登录", tags = {"互联网统一认证登录"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest url的参数包含的值：error", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true)})
    public String login(HttpServletRequest request, HttpServletResponse response) {
        if (SpringSecurityUtils.getAuthentication() != null) {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            if (userDetails instanceof InternetUserDetails) {
                return redirect("/passport/iuser/login/success");
            } else if (userDetails instanceof DefaultUserDetails) {
                return redirect("/passport/user/login/success");
            }
        }
        if (StringUtils.isNotBlank(oauth2LoginUrl)) {
            if (request.getParameter("error") != null) {
                String redirectUrl = oauth2LoginUrl;
                if (redirectUrl.indexOf("?") == -1) {
                    redirectUrl += "?error=1";
                } else {
                    redirectUrl += "&error=1";
                }
                return redirect(redirectUrl);
            }
            return redirect(oauth2LoginUrl);
        }

        // 跳转到自定义的登录页面
        AppLoginPageConfigEntity pageConfig = appLoginPageConfigService.getBySystemUnitId(Config.DEFAULT_TENANT);
        // 默认租户的登录页面
        if (pageConfig != null && EnumSwtich.OPEN.getValue().equals(pageConfig.getEnableOauth())
                && Context.isOauth2Enable()) {
            if (EnumSwtich.OPEN.getValue().equals(pageConfig.getEnableOauthLogin())) {
                // 使用统一认证服务的登录页
                // oauth2过滤器处理该地址
                return redirect("/login/oauth2");
            }
            if (EnumSwtich.OPEN.getValue().equals(pageConfig.getEnableCustomizeOauthLogin())
                    && StringUtils.isNotBlank(pageConfig.getCustomizeOauthLoginUri())
                    && !pageConfig.getCustomizeOauthLoginUri().equalsIgnoreCase("/iuser/login")) {
                String redirectUrl = pageConfig.getCustomizeOauthLoginUri();
                if (redirectUrl.indexOf("?") == -1) {
                    redirectUrl += "?error=1";
                } else {
                    redirectUrl += "&error=1";
                }
                return redirect(pageConfig.getCustomizeOauthLoginUri());
            }

            // 平台默认的统一认证登录页
            return forward("/user/oauth2_login");

        }
        return redirect("/tenant/T001");

    }

    @GetMapping("/passport/iuser/login/success")
    @ApiOperation(value = "互联网用户已登录跳转到用户首页", notes = "互联网用户已登录跳转到用户首页", tags = {"互联网统一认证登录"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "model", value = "Model", paramType = "query", dataType = "String", required = true)})
    public String iuserSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 获取有权限的主页
        if (user != null && user instanceof InternetUserDetails) {
            Iterator iterator = user.getAuthorities().iterator();
            Set<String> roles = Sets.newHashSet();
            while (iterator.hasNext()) {
                roles.add(((GrantedAuthority) iterator.next()).getAuthority());
            }
            List<AppPageDefinition> appPageDefinitions = appPageDefinitionMgr.queryByRoleIds(roles);
            if (Collections.isEmpty(appPageDefinitions)) {
                try {
                    response.sendRedirect("/pt/common/403.jsp");
                } catch (Exception e) {
                }
                return null;
            }
            AppProductIntegration appProductIntegration = appProductIntegrationService
                    .get(appPageDefinitions.get(0).getAppPiUuid());
            String url = AppConstants.WEB_APP_PATH + appProductIntegration.getDataPath() + AppConstants.DOT_HTML
                    + "?pageUuid=" + appPageDefinitions.get(0).getUuid();
            return redirect(url);
        }
        return redirect("/passport/user/login/success");
    }

    /**
     * 前端登录验证，返回token
     *
     * @param request
     * @param params
     * @return
     */
    @PostMapping("/login/{type}")
    @ApiOperation(value = "前端登录验证", notes = "前端登录验证，登录成功返回token", tags = {"单位管理员登录", "超管登录"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "登录用户类型", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "params", value = "扩展参数Map对象 key:value:<br/> username:用户名 <br/> password:密码 <br/> "
                    + "passwordEncryptType:密码加密类型 <br/> passwordEncryptKey:passwordEncryptType类型为2时，此值不能为空 <br/>", paramType = "query", dataType = "String", required = true)})
    public @ResponseBody
    ResponseEntity<ApiResponse> login(@PathVariable String type, HttpServletRequest request,
                                      @RequestBody Map<String, Object> params) {
        if (StringUtils.isBlank((String) params.get("username"))
                || StringUtils.isBlank((String) params.get("password"))) {
            return ResponseEntity.ok(ApiResponse.build().code(-2).msg("用户名或者密码为空"));
        }
        UserAccountEntity user = userInfoFacadeService.getUserAccountByLoginName(String.valueOf(params.get("username")));
        UserAcctPasswordRules passwordRules = null;
        try {
            if (LoginType.USER.equals(type) || LoginType.SUPER_ADMIN.equals(type)) {
                TenantContextHolder.setLoginType(type);
                if (StringUtils.isNotBlank((String) params.get("passwordEncryptType"))) {
                    if (((String) params.get("passwordEncryptType"))
                            .equals(EnumPasswordEncryptType.BASE64.getValue())) {
                        params.put("password", PwdUtils.decodePwdBybase64AndUnicode(params.get("password").toString()));
                    } else if (((String) params.get("passwordEncryptType"))
                            .equals(EnumPasswordEncryptType.BASE64_KEY.getValue())) {
                        params.put("password", DesCipherUtil.decrypt(params.get("password").toString(),
                                params.get("passwordEncryptKey").toString()));
                    }
                }
                UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) customDaoAuthenticationProvider
                        .authenticate(new UsernamePasswordAuthenticationToken(params.get("username"),
                                params.get("password")));
                String password = (String) params.get("password");
                if (PwdUtils.checkChinese(password)) {
                    return ResponseEntity.ok(ApiResponse.build().code(-1).msg("密码不能包含中文"));
                }


                TenantContextHolder.reset();
                if (Config.getSecurityJwtEnable() || Config.isBackendServer()) {
                    UserDetailsCacheHolder.putUserInCache(
                            (org.springframework.security.core.userdetails.UserDetails) userToken.getPrincipal());
                    DefaultUserDetails userDetails = (DefaultUserDetails) userToken.getPrincipal();

                    if (null != params.get("loginUrl") && StringUtils.isNotBlank(params.get("loginUrl").toString())) {
                        String loginUrl = params.get("loginUrl").toString();
                        // 单位登录页登录的用户，判断用户是否在单位下
                        AppLoginPageConfigEntity entity = new AppLoginPageConfigEntity();
                        // /login/unit/ 这部分不算入
                        entity.setUnitLoginPageUri(loginUrl);
                        List<AppLoginPageConfigEntity> result = appLoginPageConfigService.listByEntity(entity);
                        if (null != result && !result.isEmpty()) {
                            // 配置了单位登录页
                            entity = result.get(0);
                            if (!entity.getSystemUnitId().equals(userDetails.getSystemUnitId())) {
                                return ResponseEntity.ok(ApiResponse.build().code(-1).msg("找不到用户"));
                            }
                        } else {
                            // 未配置登录页，那么。。。用户为啥会输入这么个地址来登录？ 0——0
                            return ResponseEntity.ok(ApiResponse.build().code(-1).msg("找不到用户"));
                        }
                    }
                    userInfoFacadeService.updateLastLoginTime(userDetails.getLoginName());

//                    user = userInfoFacadeService.getuser.getAccountByUserId(userDetails.getUserId());
//                    if (StringUtils.isNotBlank(userDetails.getUserUuid())) {
//                        userService.loginUserBusinessTransaction(userDetails.getUserUuid());
//                    } else if ("admin".equals(userDetails.getLoginName())) {
//                        user = multiOrgUserAccountService.getUserByLoginNameIgnoreCase(userDetails.getLoginName(), "");
//                        userService.loginUserBusinessTransaction(user.getUuid());
//                    }

                    if (params.containsKey("tokenExpiration")) {
                        String tokenExpiration = params.get("tokenExpiration").toString();
                        if (NumberUtils.isNumber(tokenExpiration)) {
                            JwtTokenUtil.setLocalExpiration(Long.parseLong(tokenExpiration));
                        }
                    }
                    userDetails.setToken(JwtTokenUtil.createToken((UserDetails) userToken.getPrincipal()));
                    String env = System.getProperty(Config.KEY_APP_ENV);
                    SpringSecurityUtils.setAuthentication(userToken);
                    if (!Config.ENV_DEV.equalsIgnoreCase(env) && !SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name(), BuildInRole.ROLE_ADMIN.name())) {
                        passwordRules = userInfoFacadeService.getUserAcctPasswordRules();
                        doUserAcctPasswordRuleCheck(userDetails, user, passwordRules, password);
                    }
                    return ResponseEntity.ok(ApiResponse.build().data(userDetails));
                }
            }
        } catch (Exception e) {
            logger.error("登录信息：{} , 登录服务异常：{}", params, Throwables.getStackTraceAsString(e));
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.build().code(-1).msg(e.getMessage()));
            }
            if (e instanceof BadCredentialsException) {
                if (passwordRules == null) {
                    passwordRules = userInfoFacadeService.getUserAcctPasswordRules();
                }
                if (user != null && passwordRules != null) {
                    int errorNum = userInfoFacadeService.updateAcctPasswordErrorNum(user.getLoginName());
                    Integer limitErrorNum = passwordRules.getInt(UserAcctPasswordRules.RuleKey.lockIfInputErrorNum.name());
                    if (limitErrorNum != null && errorNum >= limitErrorNum && passwordRules.enabled(UserAcctPasswordRules.RuleKey.lockIfInputError)) {
                        Map<String, Object> data = Maps.newHashMap();
                        data.put("times", limitErrorNum);
                        data.put("minutes", passwordRules.getInt(UserAcctPasswordRules.RuleKey.lockMinuteIfInputError.name()));
                        return ResponseEntity.ok(ApiResponse.build().code(-1).msg(AppCodeI18nMessageSource.getMessage("User.PasswordInputErrorLocked", "pt-org", LocaleContextHolder.getLocale().toString()
                                , ImmutableMap.<String, Object>builder().put("count", limitErrorNum).put("minute", data.get("minutes")).build(), "密码输入错误次数已达" + limitErrorNum + "次, 账号锁定" + data.get("minutes") + "分钟")));
                    }
                }
            }
            //FIXME: 密码规则校验7.0账号完善密码规则后补充
            // 用户名或密码错误
//            if (e.getMessage().indexOf("用户名或密码错误") >= 0) {
//                PwdErrorDto pwdErrorDto = PwdUtils.getPwdErrorDto(user, multiOrgPwdSettingDto, PwdUtils.loginPwdType);
//                // 【密码规则管理】中的【密码输入错误时账号锁定】开启时
//                if (IsPwdErrorLockEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getIsPwdErrorLock())) {
//                    multiOrgUserAccountService.save(user);
//                    return ResponseEntity.ok(ApiResponse.build().code(-1).msg(pwdErrorDto.getMessage()));
//                }
//            }
            return ResponseEntity.ok(ApiResponse.build().code(-1).msg(e.getMessage()));
        }
        return ResponseEntity.ok(ApiResponse.build().code(-3).msg("登录无效"));

    }

    private void doUserAcctPasswordRuleCheck(DefaultUserDetails userDetails, UserAccountEntity user, UserAcctPasswordRules rules, String password) {
        if (rules.enabled(UserAcctPasswordRules.RuleKey.enablePasswordTimeLimit) && user.getPasswordExpiredTime() != null && user.getPasswordExpiredTime().compareTo(new Date()) <= 0) {
            userDetails.setLoginNextAction(new LoginNextAction("forceModifyPasswordCauseByExpired"));
            return;

        }
        if (rules.enabled(UserAcctPasswordRules.RuleKey.forceModifySysInitPassword) && BooleanUtils.isFalse(user.getPasswordModifiedByUser())) {
            userDetails.setLoginNextAction(new LoginNextAction("forceModifyPasswordCauseBySystemInit"));
            return;
        }
        if (rules.enabled(UserAcctPasswordRules.RuleKey.loginCheckPasswordPattern)) {
            if ((password.length() < rules.getInt(UserAcctPasswordRules.RuleKey.minLength)
                    || password.length() > rules.getInt(UserAcctPasswordRules.RuleKey.maxLength)) || !UserAcctPasswordRules.isValidPasswordFlexible(password, rules.getInt(UserAcctPasswordRules.RuleKey.charTypeNumRequire), rules.enabled(UserAcctPasswordRules.RuleKey.mustContainsUpperLowerCaseChar))) {
                userDetails.setLoginNextAction(new LoginNextAction("forceModifyPasswordCauseByPatternRequired"));
                return;
            }
        }

    }

    /**
     * 通过密码规则校验，得到密码校验输出对象
     *
     * @param pwd                   未加密密码字符串
     * @param multiOrgPwdSettingDto 密码规则对象
     * @param user                  账号表
     * @return 密码校验输出对象
     **/
    private PwdRoleCheckObj pwdRoleCheck(String pwd, MultiOrgPwdSettingDto multiOrgPwdSettingDto,
                                         MultiOrgUserAccount user) {
        PwdRoleCheckObj pwdRoleCheckObj = new PwdRoleCheckObj();
        // 密码是否过期：过期时则校验不通过
        if (IsPwdValidityEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getIsPwdValidity())
                && multiOrgPwdSettingDto.getPwdValidity() > 0) {
            // 【密码规则管理】中的密码过期时间＞0时，才校验
            if (user.getPwdCreateTime() == null) {
                user.setPwdCreateTime(new Date());
            }
            Date pwdValidityDate = DateUtil.getNextDate(user.getPwdCreateTime(),
                    multiOrgPwdSettingDto.getPwdValidity());
            if (pwdValidityDate.compareTo(new Date()) <= 0) {
                pwdRoleCheckObj.setSuccess(false);
                pwdRoleCheckObj.setOpenUpdatePwdPage(true);
                pwdRoleCheckObj.setMessage("您的密码已过期，必须修改！");
                return pwdRoleCheckObj;
            }
        }
        // 密码是否符合密码格式：不符合时则校验不通过
        if (IsEnforceUpdatePwdEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getIsEnforceUpdatePwd())) {
            // 【密码规则管理】中的【登录时校验密码格式】开启时，才校验
            if (!PwdUtils.ckeckPwdRole(pwd, multiOrgPwdSettingDto)) {
                pwdRoleCheckObj.setSuccess(false);
                pwdRoleCheckObj.setOpenUpdatePwdPage(true);
                pwdRoleCheckObj.setMessage("您的密码不符合密码格式，必须修改！");
            }
        }
        // 账号是否被锁定：锁定时则校验不通过
        if (PwdErrorLockEnum.Locked.getValue().equals(user.getPwdErrorLock())) {
            pwdRoleCheckObj.setSuccess(false);
            pwdRoleCheckObj.setLocked(true);
            StringBuilder message = new StringBuilder();
            message.append("账号" + user.getLoginName() + "的密码已输入错误" + user.getPwdErrorNum() + "次，账号被锁定！\n");
            message.append("锁定期间无法使用，" + DateUtils.formatDateTimeMin(user.getLastUnLockedTime()) + "将自动解锁！\n");
            pwdRoleCheckObj.setMessage(message.toString());
        }

        // 密码是否【非用户设置的密码】，是时则校验不通过
        // 历史数据处理(值为null)：所有密码均作为【用户设置的密码】
        if (IsNotUserSetPwdUpdateEnum.Yes.getValue().equals(multiOrgPwdSettingDto.getIsNotUserSetPwdUpdate())
                && (user.getIsUserSettingPwd() != null
                && IsUserSettingPwdEnum.NO.getValue().equals(user.getIsUserSettingPwd()))) {
            pwdRoleCheckObj.setSuccess(false);
            pwdRoleCheckObj.setOpenUpdatePwdPage(true);
            pwdRoleCheckObj.setMessage("您当前密码为系统初始密码或重置密码，必须修改！");
        }
        return pwdRoleCheckObj;
    }

    private String forwardToLoginPage(AppLoginPageConfigEntity pageConfig, HttpServletResponse response, Model model) {
        model.addAttribute("config", pageConfig);
        model.addAttribute("pwdEncryptType", passwordEncryptType);
        if (pageConfig != null && EnumSwtich.CLOSE.getValue().equals(pageConfig.getLoginBoxHardware())) {
            fjca.FJCAApps ca = new fjca.FJCAApps();
            // 社保4000
            fjca.FJCAApps.setErrorBase(4000);
            // Windows 验证服务器IP
            // ca.setServerURL("222.76.242.238:7000");
            //
            fjca.FJCAApps.setServerURL(Config.getValue(UserDetailsServiceProvider.KEY_FJCA_SERVER_URL));

            // 产生随机数,正式的应用，随机数要在服务端产生传给客户端，登陆成功后清空。
            String strRandom = ca.FJCA_GenRandom2();
            model.addAttribute("strRandom", strRandom);
        }

        // 背景图转为base64编码输出
        try {
            String imageBase64 = IOUtils.toString(pageConfig.getPageBackgroundImage().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                model.addAttribute("backgroundImage",
                        "url(data:image/png;base64," + imageBase64.replaceAll("\r|\n", "") + ")");
            }

            imageBase64 = IOUtils.toString(pageConfig.getPageLogo().getCharacterStream());
            if (StringUtils.isNotBlank(imageBase64)) {
                if ("_right".equalsIgnoreCase(pageConfig.getPageStyle())) {
                    model.addAttribute("headerImage", "data:image/png;base64," + imageBase64.replaceAll("\r|\n", ""));
                } else {
                    model.addAttribute("headerImage",
                            "url(data:image/png;base64," + imageBase64.replaceAll("\r|\n", "") + ")");
                }
            }
        } catch (Exception e) {

        }
        return forward("/user/domain_login3" + StringUtils.trimToEmpty(pageConfig.getPageStyle()));
    }

    /**
     * 用户不存在页面配置response
     *
     * @param response
     * @return void
     **/
    private void throwNotFoundResponse(HttpServletResponse response) {
        try {
            // 跳转到用户不存在页面
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.sendError(HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/checkToken")
    public @ResponseBody
    ResponseEntity<ApiResponse> checkToken(@RequestParam("token") String token) {
        try {
            JwtTokenUtil.getClaims(token);
            return ResponseEntity.ok(ApiResponse.build());
        } catch (Exception ex) {
            return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.VERIFY_TOKEN_ERROR));
        }
    }

    @GetMapping("loginByIdNumberToken")
    public ResponseEntity<ApiResponse> loginByIdNumberToken(@RequestParam String accessToken,
                                                            @RequestParam String idNumber, @RequestParam(required = false) String appId,
                                                            @RequestParam(required = false) Boolean encodeIdNumber) {
        if (tokenService == null) {
            return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR).msg("未发现tokenService"));
        }
        try {
            tokenService.verifyToken(accessToken, appId);
        } catch (AccessDeniedException e) {
            return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.VERIFY_TOKEN_ERROR));
        }
        if (BooleanUtils.isTrue(encodeIdNumber)) {
            idNumber = new String(Base64Utils.decodeFromString(idNumber));
        }
        MultiOrgUserInfo userInfo = multiOrgUserService.getUserByIdNumber(idNumber);
        if (userInfo != null) {
            MultiOrgUserAccount account = multiOrgUserAccountService.getAccountByUserId(userInfo.getUserId());
            TenantContextHolder.setLoginType(LoginType.USER);
            DefaultUserDetails userDetails = (DefaultUserDetails) userDetailsService
                    .loadUserByUsername(account.getLoginName());
            TenantContextHolder.reset();
            userDetails.setToken(JwtTokenUtil.createToken(userDetails));
            userDetails.setIsAllowMultiDeviceLogin(
                    appLoginSecurityConfigService.isAllowMultiDeviceLogin(userDetails.getSystemUnitId()));
            return ResponseEntity.ok(ApiResponse.build().data(userDetails));
        }
        return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST).msg("查询不到身份证对应的用户信息"));
    }


    @GetMapping("loginByJwt")
    public ResponseEntity<ApiResponse> loginByJwt(HttpServletRequest request) {
        String token = JwtTokenUtil.getToken(request);
        if (StringUtils.isNotBlank(token)) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                    ((DefaultUserDetails) authentication.getPrincipal()).setToken(token);
                    return ResponseEntity.ok(ApiResponse.build().data(authentication.getPrincipal()));
                }
            } catch (Exception e) {
                logger.error("jwt登录异常：", e);
            }
        }
        return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.VERIFY_TOKEN_ERROR));

    }

    /**
     * 用户名、手机号码及密码登录
     *
     * @param request
     * @param params
     * @return
     */
    @PostMapping("/loginByUsrPhonePwd")
    public ResponseEntity<ApiResponse> loginByUsrPhonePwd(HttpServletRequest request,
                                                          @RequestBody Map<String, Object> params) {
        String username = Objects.toString(params.get("username"), StringUtils.EMPTY);
        String mobilePhone = Objects.toString(params.get("mobilePhone"), StringUtils.EMPTY);

        if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(mobilePhone)) {
            return ResponseEntity.ok(ApiResponse.build().code(-2).msg("用户名或者手机号码为空"));
        }

        MultiOrgUserAccount user = multiOrgUserAccountService.getUserByLoginNameIgnoreCase(username, "");
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.build().code(-1).msg("找不到用户"));
        }
        OrgUserVo userVo = multiOrgUserService.getUserById(user.getId(), false, false, false);
        if (userVo == null) {
            return ResponseEntity.ok(ApiResponse.build().code(-1).msg("找不到用户"));
        }
        if (!StringUtils.equals(mobilePhone, userVo.getMobilePhone())) {
            return ResponseEntity.ok(ApiResponse.build().code(-2).msg("手机号码错误"));
        }

        return login("1", request, params);
    }

    /**
     * 手机短信验证码登录
     *
     * @param params
     * @return
     */
    @PostMapping("loginByMobileSms")
    public ResponseEntity<ApiResponse> loginByMobileSms(@RequestBody Map<String, Object> params) {
        String mobilePhone = Objects.toString(params.get("mobilePhone"), StringUtils.EMPTY);
        String smsVerifyCode = Objects.toString(params.get("smsVerifyCode"), StringUtils.EMPTY);
        String smsId = Objects.toString(params.get("smsId"), StringUtils.EMPTY);
        Integer result = verifyCodeService.verify(smsId, smsVerifyCode, mobilePhone);
        if (VerifyCode.VERIFY_RESULT_EXPIRE.equals(result)) {
            throw new BusinessException("验证码已无效");
        } else if (VerifyCode.VERIFY_RESULT_FAILURE.equals(result)) {
            throw new BusinessException("验证码错误");
        }

        List<MultiOrgUserInfo> userInfos = multiOrgUserService.listUserByMobilePhone(mobilePhone);
        if (Collections.size(userInfos) == 1) {
            MultiOrgUserInfo userInfo = userInfos.get(0);
            MultiOrgUserAccount account = multiOrgUserAccountService.getAccountByUserId(userInfo.getUserId());
            TenantContextHolder.setLoginType(LoginType.USER);
            DefaultUserDetails userDetails = (DefaultUserDetails) userDetailsService
                    .loadUserByUsername(account.getLoginName());
            TenantContextHolder.reset();
            userDetails.setToken(JwtTokenUtil.createToken(userDetails));
            userDetails.setIsAllowMultiDeviceLogin(
                    appLoginSecurityConfigService.isAllowMultiDeviceLogin(userDetails.getSystemUnitId()));
            return ResponseEntity.ok(ApiResponse.build().data(userDetails));
        } else if (Collections.size(userInfos) > 1) {
            return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST).msg("登录失败，手机号码存在多个对应的用户信息"));
        }
        return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST).msg("不存在手机号码对应的用户信息"));
    }


    /**
     * 本机一键登录
     *
     * @param params
     * @return
     */
    @PostMapping("/loginByUniverify")
    public ResponseEntity<ApiResponse> loginByUniverify(@RequestBody Map<String, Object> params) {
        String mobilePhone = Objects.toString(params.get("mobilePhone"), StringUtils.EMPTY);
        String apiKey = Objects.toString(params.get("apiKey"), StringUtils.EMPTY);
        String apiSecret = Objects.toString(params.get("apiSecret"), StringUtils.EMPTY);
        String configKey = SystemParams.getValue("login.univerify.key");
        String configSecret = SystemParams.getValue("login.univerify.secret");
        if (!StringUtils.equals(apiKey, configKey) || !StringUtils.equals(apiSecret, configSecret)) {
            throw new BusinessException("一键登录API密钥错误");
        }

        List<MultiOrgUserInfo> userInfos = multiOrgUserService.listUserByMobilePhone(mobilePhone);
        if (Collections.size(userInfos) == 1) {
            MultiOrgUserInfo userInfo = userInfos.get(0);
            MultiOrgUserAccount account = multiOrgUserAccountService.getAccountByUserId(userInfo.getUserId());
            TenantContextHolder.setLoginType(LoginType.USER);
            DefaultUserDetails userDetails = (DefaultUserDetails) userDetailsService
                    .loadUserByUsername(account.getLoginName());
            TenantContextHolder.reset();
            userDetails.setToken(JwtTokenUtil.createToken(userDetails));
            userDetails.setIsAllowMultiDeviceLogin(
                    appLoginSecurityConfigService.isAllowMultiDeviceLogin(userDetails.getSystemUnitId()));
            return ResponseEntity.ok(ApiResponse.build().data(userDetails));
        } else if (Collections.size(userInfos) > 1) {
            return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST).msg("登录失败，手机号码存在多个对应的用户信息"));
        }
        return ResponseEntity.ok(ApiResponse.build().code(ApiCodeEnum.API_RESOURCE_NOT_EXIST).msg("不存在手机号码对应的用户信息"));
    }

}
