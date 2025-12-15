/*
 * @(#)2013-3-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.access.intercept.provider.UserSecurityMetadataSourceProvider;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import com.wellsoft.pt.security.audit.web.tags.PrivilegeTag;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.passport.service.IpSecurityConfigService;
import com.wellsoft.pt.security.passport.service.SystemAccessService;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 安全服务门面类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-18.1	zhulh		2013-3-18		Create
 * </pre>
 * @date 2013-3-18
 */
@Service
public class SecurityApiFacadeImpl extends AbstractApiFacade implements SecurityApiFacade {

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

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private UserDetailsServiceProvider userDetailsServiceProvider;

    @Autowired(required = false)
    private List<ResourceDataSource> resourceDataSources;

    @Autowired
    private CacheManager cacheManager;

    /**
     * @param authentication
     * @param configAttributes
     * @return
     */
    private static boolean checkGrantedAuthority(Authentication authentication,
                                                 Collection<ConfigAttribute> configAttributes) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (ConfigAttribute attribute : configAttributes) {
            // Attempt to find a matching granted authority
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
    public List<Resource> getDynamicButtonResourcesByCode(String code) {
        return resourceService.getDynamicButtonResourcesByCode(code);
    }

    /**
     * 根据模块资源编号，获取按钮
     *
     * @param code
     * @return
     */
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
        if (SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_ADMIN.name(), BuildInRole.ROLE_TENANT_ADMIN.name())) {// 超级管理员
            return true;
        }
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
        Authentication authentication = SpringSecurityUtils.getAuthentication();
        if (SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_ADMIN.name(), BuildInRole.ROLE_TENANT_ADMIN.name())) {// 超级管理员
            return true;
        }
//        FIXME:  7.0 不再使用 6.2 产品集成树，针对目前在用的页面资源直接放开
//        if (AppFunctionType.AppProductIntegration.equalsIgnoreCase(functionType)) {
//            return true;
//        }
        if (authentication == null || object == null) {
            return false;
        }
        String cacheKey = SpringSecurityUtils.getCurrentUserId() + "_isGranted_" + functionType
                + Separator.UNDERLINE.getValue() + object.toString();
        Cache userSecurityCache = (Cache) cacheManager.getCache(
                UserSecurityMetadataSourceProvider.USER_SECURITY_CACHE_PREFIX + SpringSecurityUtils.getCurrentUserId().toLowerCase());
        Boolean isGranted = userSecurityCache.get(cacheKey, Boolean.class);
        if (isGranted != null) {
            return isGranted;
        }

        Collection<ConfigAttribute> configAttributes = securityMetadataSourceService
                .getAttributes(object, functionType);
        if (configAttributes.isEmpty()) {
            isGranted = false;
        } else {
            isGranted = checkGrantedAuthority(authentication, configAttributes);
        }
        userSecurityCache.put(cacheKey, isGranted);
        return isGranted;
    }

    /**
     * 判断指定的用户是否有指定的角色
     *
     * @param authority
     * @return
     */
    public boolean hasRole(String userId, String roleId) {
        Authentication authentication = SpringSecurityUtils.getAuthentication();
        if (authentication != null && userId != null && userId.equals(
                SpringSecurityUtils.getCurrentUserId())) {
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

    @Override
    public Select2GroupData queryRoleListByUnitForSelect2Group(Select2QueryInfo queryInfo) {
        boolean isContainPT = true;
        if (queryInfo.getParams() != null && "false".equals(
                queryInfo.getParams().get("isContainPT"))) {
            isContainPT = false;
        }
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        Select2GroupData data = new Select2GroupData();
        List<Role> roleList = this.roleService.queryRoleListByUnit(unitId);
        if (roleList != null) {
            if (!CollectionUtils.isEmpty(roleList)) {
                for (Role entity : roleList) {
                    Select2DataBean bean = new Select2DataBean(entity.getUuid(), entity.getName());
                    data.addResultData("单位角色", bean);
                }
            }
        }
        if (!MultiOrgSystemUnit.PT_ID.equals(unitId) && isContainPT) {
            // 查询平台的角色信息
            List<Role> ptRoleList = this.roleService.queryRoleListByUnit(MultiOrgSystemUnit.PT_ID);
            if (ptRoleList != null) {
                if (!CollectionUtils.isEmpty(ptRoleList)) {
                    for (Role entity : ptRoleList) {
                        Select2DataBean bean = new Select2DataBean(entity.getUuid(),
                                entity.getName());
                        data.addResultData("平台角色", bean);
                    }
                }
            }
        }
        return data;
    }

    // 获取一个用户的所有授权角色
    @Override
    public Set<GrantedAuthority> queryAllGrantedAuthoritiesByUser(String userId) {
        Set<GrantedAuthority> set = this.userDetailsServiceProvider.queryAllGrantedAuthoritiesByUserId(userId);
        return set;
    }

    @Override
    public Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute() {
        return resourceService.loadConfigAttribute();
    }


    @Override
    public List<String> queryRoleIdByCurrentUserUnitId() {

        List<Role> roles = roleService.queryRoleByCurrentUserUnitId();
        List<String> roleIds = new ArrayList<String>();
        for (Role role : roles) {
            roleIds.add(role.getUuid());
        }
        return roleIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.facade.service.SecurityApiFacade#addAnonymousResource(java.lang.String)
     */
    @Override
    public void addAnonymousResource(String resourceId) {
        // 1、平台资源
        Resource resource = resourceService.get(resourceId);
        if (resource != null) {
            String functionType = resource.getType();
            if (StringUtils.isNotBlank(functionType)) {
                List<Object> objects = Lists.newArrayList();
                if (StringUtils.isNotBlank(resource.getUrl())) {
                    objects.add(resource.getUrl());
                }
                if (StringUtils.isNotBlank(resource.getCode())) {
                    objects.add(resource.getCode());
                }
                securityMetadataSourceService.addAnonymousAttributes(resourceId, objects,
                        functionType);
            }
        }

        // 2、接口资源
        if (CollectionUtils.isEmpty(resourceDataSources)) {
            return;
        }
        for (ResourceDataSource resourceDataSource : resourceDataSources) {
            Map<String, Collection<Object>> objectIdentities = resourceDataSource.getObjectIdentities(
                    resourceId);
            for (Entry<String, Collection<Object>> entry : objectIdentities.entrySet()) {
                securityMetadataSourceService.addAnonymousAttributes(resourceId, entry.getValue(),
                        entry.getKey());
            }
        }
    }

    @Override
    public void removeAnonymousResource(String resourceId) {
        for (ResourceDataSource resourceDataSource : resourceDataSources) {
            Map<String, Collection<Object>> objectIdentities = resourceDataSource.getObjectIdentities(
                    resourceId);
            for (Entry<String, Collection<Object>> entry : objectIdentities.entrySet()) {
                securityMetadataSourceService.removeAnonymousAttributes(resourceId,
                        entry.getValue(), entry.getKey());
            }
        }
    }

    @Override
    public boolean checkCurrentUserPassword(String password) {
        try {
            TenantContextHolder.setLoginType("6");
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    SpringSecurityUtils.getCurrentLoginName(), password);
            return ApplicationContextHolder.getBean(AuthenticationManager.class).authenticate(
                    authRequest).isAuthenticated();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TenantContextHolder.reset();
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.facade.service.SecurityApiFacade#removeResourceAttribute(java.lang.String)
     */
    @Override
    public void removeResourceAttribute(String resourceId) {
        securityMetadataSourceService.removeResourceAttributes(resourceId);
    }

}
