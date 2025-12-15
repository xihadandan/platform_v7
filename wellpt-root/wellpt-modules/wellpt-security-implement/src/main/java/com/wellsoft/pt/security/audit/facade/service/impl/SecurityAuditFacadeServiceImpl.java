package com.wellsoft.pt.security.audit.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.security.audit.bean.GrantedObjectDto;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.audit.web.tags.PrivilegeTag;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.passport.service.IpSecurityConfigService;
import com.wellsoft.pt.security.passport.service.SystemAccessService;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SecurityAuditFacadeServiceImpl implements SecurityAuditFacadeService {

    protected static Logger logger = LoggerFactory.getLogger(SecurityAuditFacadeServiceImpl.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;

    @Autowired
    private SystemAccessService systemAccessService;

    @Autowired
    private IpSecurityConfigService ipSecurityConfigService;
    @Autowired
    private PrivilegeService privilegeService;

    /**
     * @param authentication
     * @param configAttributes
     * @return
     */
    private static boolean checkGrantedAuthority(Authentication authentication,
                                                 Collection<ConfigAttribute> configAttributes) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (ConfigAttribute attribute : configAttributes) {
            for (GrantedAuthority auth : authorities) {
                if (attribute.getAttribute().equals(auth.getAuthority())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 根据模块资源编号，获该模块下的所有动态按钮
     *
     * @param code
     * @return
     */
    @Override
    public List<Resource> getDynamicButtonResourcesByCode(String code) {
        return resourceService.getDynamicButtonResourcesByCode(code);
    }

    /**
     * 根据模块资源编号，获取按钮
     *
     * @param code
     * @return
     */
    @Override
    public Resource getButtonByCode(String code) {
        return resourceService.getResourceByCode(code);
    }

    /**
     * 根据模块资源编号，获取资源
     *
     * @param code
     * @return
     */
    public Resource getResourceByCode(String code) {
        return resourceService.getResourceByCode(code);
    }

    /**
     * 判断当前用户是否有权限指定权限
     *
     * @param code
     * @return
     */
    public boolean isGranted(String code) {
        return PrivilegeTag.isGranted(code);
    }

    /**
     * 判断当前用户是否有指定类型的功能权限
     *
     * @param object
     * @param functionType
     * @return
     */
    public boolean isGranted(Object object, String functionType) {
        try {
            // 平台管理员，拥有所有权限
            String currUserId = SpringSecurityUtils.getCurrentUserId();
            if (MultiOrgUserAccount.PT_ACCOUNT_ID.equals(currUserId) || SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_ADMIN.name(), BuildInRole.ROLE_TENANT_ADMIN.name())) {
                return true;
            }

            Authentication authentication = SpringSecurityUtils.getAuthentication();
            if (authentication == null || object == null) {
                return false;
            }

            //            String cacheKey = SpringSecurityUtils.getCurrentUserId() + "_isGranted_" + functionType
            //                    + Separator.UNDERLINE.getValue() + object.toString();
            //            CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
            //            Cache cache = cacheManager.getCache(ModuleID.SECURITY);
            boolean isGranted = false;// (Boolean) cache.getValue(cacheKey);
            //            if (isGranted != null) {
            //                 return isGranted;
            //            }
            Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(object,
                    functionType);
            if (configAttributes.isEmpty()) {
                isGranted = false;
            } else {
                isGranted = checkGrantedAuthority(authentication, configAttributes);
            }

            // cache.put(cacheKey, isGranted);
            return isGranted;
        } catch (Exception e) {
            logger.error("权限判断逻辑异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<Object, Boolean> isGranted(HashSet<GrantedObjectDto> grantedObjectDtos) {
        Map<Object, Boolean> results = Maps.newHashMap();
        for (GrantedObjectDto dto : grantedObjectDtos) {
            results.put(dto.getObject(), this.isGranted(dto.getMd5hex() ? DigestUtils.md5Hex(dto.getObject().toString()) : dto.getObject(), dto.getFunctionType()));
        }
        return results;
    }

    /**
     * 与{@link #isGranted(Object, String)}保持一致，用于前端调试权限。
     *
     * @param object       资源
     * @param functionType 资源类型
     * @return 指定资源的授权角色列表
     */
    public Map<String, Collection<String>> fetchGrantedAuthority(Object object, String functionType) {
        try {
            // 平台管理员，拥有所有权限
            Map<String, Collection<String>> result = Maps.newHashMap();
            Collection<String> granted = Lists.newArrayList();
            Collection<String> hasRoles = Lists.newArrayList();
            Collection<String> needRoles = Lists.newArrayList();
            Authentication authentication = SpringSecurityUtils.getAuthentication();
            if (authentication != null) {
                hasRoles = Collections2.transform(authentication.getAuthorities(),
                        new Function<GrantedAuthority, String>() {
                            @Override
                            public String apply(GrantedAuthority input) {
                                return input.getAuthority();
                            }
                        });
            }
            if (object != null) {
                needRoles = Collections2.transform(securityMetadataSourceService.getAttributes(object, functionType),
                        new Function<ConfigAttribute, String>() {
                            @Override
                            public String apply(ConfigAttribute input) {
                                return input.getAttribute();
                            }
                        });
            }
            granted = Sets.intersection(Sets.newHashSet(hasRoles), Sets.newHashSet(needRoles));
            result.put("hasRoles", hasRoles);
            result.put("needRoles", needRoles);
            result.put("intersectRoles", granted);// 交集
            return result;
        } catch (Exception e) {
            logger.error("权限判断逻辑异常：{}", Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }

    }

    /**
     * 判断指定的用户是否有指定的角色
     *
     * @param authority
     * @return
     */
    public boolean hasRole(String userId, String roleId) {
        Authentication authentication = SpringSecurityUtils.getAuthentication();
        if (authentication != null && userId != null && userId.equals(SpringSecurityUtils.getCurrentUserId())) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(roleId)) {
                    return true;
                }
            }
        }
        return roleService.hasRole(userId, roleId);
    }

    /**
     * 判断指定的IP地址是否需要登录验证码，若需要返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param ipAddr
     * @return
     */
    public boolean isRequiredLoginVerifyCode(String ipAddr) {
        return ipSecurityConfigService.isRequiredLoginVerifyCode(ipAddr);
    }

    /**
     * 判断指定的用户ID是否允许登录系统
     *
     * @param userId
     * @return
     */
    public boolean isAllowLogin(String userId) {
        return systemAccessService.isAllowLogin(userId);
    }

    /**
     * 判断指定的用户ID是否允许从指定的IP地址登录系统，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param userId
     * @param ipAddr
     * @return
     */
    public boolean isAllowLogin(String userId, String ipAddr) {
        return ipSecurityConfigService.isAllowLogin(userId, ipAddr);
    }

    /**
     * 判断指定的用户ID是否需要短信验证码，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param userId
     * @param ipAddr
     * @return
     */
    public boolean isRequiredSmsVerifyCode(String userId, String ipAddr) {
        return ipSecurityConfigService.isRequiredSmsVerifyCode(userId, ipAddr);
    }

    /**
     * 获取短信验证码的超时时间。如果没有配置返回0，表示永不超时，否则返回配置的秒数
     *
     * @return
     */
    public int getSmsVerifyCodeTimeOut() {
        return ipSecurityConfigService.getSmsVerifyCodeTimeOut();
    }

    /**
     * 登录域名判断，是否在指定的范围内
     *
     * @param userId
     * @param domainAddr
     * @return
     */
    public boolean isAllowDomainLogin(String userId, String domainAddr) {
        return ipSecurityConfigService.isAllowDomainLogin(userId, domainAddr);
    }

}
