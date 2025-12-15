package com.wellsoft.pt.security.util;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SpringSecurity的工具类.
 * <p>
 * 注意. 本类只支持SpringSecurity 3.0.x.
 *
 * @author lilin
 */
@GroovyUseable
public class SpringSecurityUtils {
    /**
     * 取得当前用户, 返回值为SpringSecurity的User类或其子类, 如果当前用户未登录则返回null.
     *
     * @return 当前用户
     */
    @SuppressWarnings("unchecked")
    public static <T extends UserDetails> T getCurrentUser() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return (T) IgnoreLoginUtils.getUserDetails();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return null;
        }

        return (T) principal;
    }

    /**
     * 取得当前互联网用户, 如果无则返回null.
     *
     * @return 当前互联网用户
     */
    public static UserDto getCurrentInternetUserInfo() {
        UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
        if (userInfoFacadeService != null) {
            UserDto userDto = userInfoFacadeService.getFullInternetUserByLoginName(getCurrentLoginName(), "");
            if (userDto != null) {
                userDto.setPassword(StringUtils.EMPTY);
            }
            return userDto;
        }
        return null;
    }

    /**
     * 取得当前用户的用户名, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的用户名
     */
    public static String getCurrentUserName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getUserName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return getCurrentUserNameFromCookie(authentication.getName());
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUserName();
    }

    public static String getCurrentLocalUserName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getUserName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return getCurrentUserNameFromCookie(authentication.getName());
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getLocalUserName();
    }

    /**
     * 取得当前用户的登录名, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的登录名
     */
    public static String getCurrentLoginName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getLoginName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getLoginName();
    }

    /**
     * 获取当前用户的归属单位ID
     */
    public static String getCurrentUserUnitId() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getSystemUnitId();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getSystemUnitId();
    }

    /**
     * 判断是否互联网用户
     *
     * @return
     */
    public static boolean isInternetLoginUser() {
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getName())) {
            return false;
        }
        return authentication.getPrincipal() instanceof InternetUserDetails;
    }

    /**
     * 获取当前用户的归属单位名称
     *
     * @return
     */
    public static String getCurrentUserUnitName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getSystemUnit().getName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        MultiOrgSystemUnit unit = ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getSystemUnit();
        return unit != null ? unit.getName() : null;
    }

    /**
     * 获取当前用户的归属单位短名称
     *
     * @return
     */
    public static String getCurrentUserUnitShortName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getSystemUnit().getShortName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getSystemUnit()
                .getShortName();
    }

    /**
     * 取得当前用户的岗位名称, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的岗位名称
     */
    public static String getCurrentUserJobName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainJobName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }

        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal()).getMainJobName();
    }

    /**
     * 取得当前用户的主部门, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的主部门ID
     */
    public static String getCurrentUserDepartmentId() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainDepartmentId();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }

        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getMainDepartmentId();
    }

    /**
     * 取得当前用户的主部门, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的主部门NAME
     */
    public static String getCurrentUserDepartmentName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainDepartmentName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }

        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getMainDepartmentName();
    }

    /**
     * 取得当前用户的主部门路径, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的主部门路径
     */
    public static String getCurrentUserDepartmentPath() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainDepartmentPath();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }

        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getMainDepartmentPath();
    }

    /**
     * 取得当前用户的租户ID, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的租户ID
     */
    public static String getCurrentTenantId() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getTenantId();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return Config.DEFAULT_TENANT;
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getTenantId();
    }

    /**
     * 取得当前用户的登录名, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户的用户ID
     */
    public static String getCurrentUserId() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getUserId();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return getCurrentUserIdFromCookie(authentication.getName());
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getUserId();
    }

    /**
     * 取得当前用户的用户UUID
     *
     * @return
     */
    public static String getCurrentUserUuid() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getUserUuid();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return getCurrentUserIdFromCookie(authentication.getName());
        }
        if (authentication.getCredentials() != null && "trustedClient".equals(authentication.getCredentials().toString())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getUserUuid();
    }

    public static String getUserMainBusinessUnitId() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainBusinessUnitId();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getMainBusinessUnitId();
    }

    public static String getUserMainBusinessUnitName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainBusinessUnitName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getMainBusinessUnitName();
    }

    public static List<String> getUserOtherBusinessUnitIds() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getOtherBusinessUniIds();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) getCurrentUser()).getOtherBusinessUniIds();
    }

    /**
     * 取得当前用户登录IP, 如果当前用户未登录则返回空字符串.
     *
     * @return 当前用户登录IP
     */
    public static String getCurrentUserIp() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return "";
        }
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return "";
        }

        Object details = authentication.getDetails();
        if (!(details instanceof WebAuthenticationDetails)) {
            return "";
        }

        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;
        return webDetails.getRemoteAddress();
    }

    /**
     * 判断用户是否拥有角色, 如果用户拥有参数中的任意一个角色则返回true.
     *
     * @param roles 角色
     * @return true-用户拥有参数中某一角色;false-未拥有
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> grantedAuthorityList = authentication.getAuthorities();
        for (String role : roles) {
            for (GrantedAuthority authority : grantedAuthorityList) {
                if (role.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 将UserDetails保存到Security Context.
     *
     * @param userDetails 已初始化好的用户信息.
     * @param request     用于获取用户IP地址信息,可为Null.
     */
    public static void saveUserDetailsToContext(UserDetails userDetails, HttpServletRequest request) {
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities());

        if (request != null) {
            authentication.setDetails(new WebAuthenticationDetails(request));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 取得Authentication, 如当前SecurityContext为空时返回null.
     *
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return null;
        }

        return context.getAuthentication();
    }

    public static void setAuthentication(Authentication object) {
        SecurityContextHolder.getContext().setAuthentication(object);
    }

    /**
     * 获取当前用户的授权的资源列表
     *
     * @return 当前用户的资源列表
     */
    public static Collection<SimpleGrantedAuthority> getCurrentUserAuthorities() {
        Collection<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>(0);
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user != null) {
            for (GrantedAuthority authority : user.getAuthorities()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
            }
        }
        return grantedAuthorities;
    }

    public static boolean isAnonymousUser() {
        Authentication authentication = getAuthentication();
        return authentication != null && "anonymousUser".equals(authentication.getName());
    }

    public static Cookie[] getCookies() {
        HttpServletRequest request = null;
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null && (request = attr.getRequest()) != null) {
            return request.getCookies();
        }
        return null;
    }

    public static String getCurrentUserIdFromCookie(String defaultValue) {
        Cookie[] cookies = getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("cookie.current.userId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return defaultValue;
    }

    public static String getCurrentUserNameFromCookie(String defaultValue) {
        Cookie[] cookies = getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("cookie.current.username".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return defaultValue;
    }

    public static void cacheAccessToken(String token, Authentication object) {
        String accessKey = "at-" + token;
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        cacheManager.getCache(ModuleID.SECURITY).put(accessKey, object);
    }

    public static Authentication removeAccessToken(String token) {
        String accessKey = "at-" + token;
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Authentication object = (Authentication) cacheManager.getCache(ModuleID.SECURITY).getValue(accessKey);
        cacheManager.getCache(ModuleID.SECURITY).evict(accessKey);
        return object;
    }

    public static List<String> getAccessableSystem() {
        DefaultUserDetails userDetails = getCurrentUser();
        List<String> sys = Lists.newArrayList();
        List<UserSystemOrgDetails.OrgDetail> orgDetails = userDetails.getUserSystemOrgDetails().getDetails();
        if (CollectionUtils.isNotEmpty(orgDetails)) {
            for (UserSystemOrgDetails.OrgDetail detail : orgDetails) {
                sys.add(detail.getSystem());
            }
        }
        return sys;
    }

    public static String getCurrentUserLocalDepartmentName() {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return IgnoreLoginUtils.getUserDetails().getMainDepartmentName();
        }
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }
        if ("anonymousUser".equals(authentication.getName())) {
            return "";
        }

        return ((com.wellsoft.pt.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getLocalMainDepartmentName();
    }
}
