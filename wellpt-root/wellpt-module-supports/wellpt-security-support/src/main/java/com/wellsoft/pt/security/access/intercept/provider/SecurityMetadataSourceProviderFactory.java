/*
 * @(#)2013-1-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.intercept.provider;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.access.SecurityMetadataSourceProvider;
import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Callable;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-24.1	zhulh		2013-1-24		Create
 * </pre>
 * @date 2013-1-24
 */
@Component
@Scope(SCOPE_SINGLETON)
public class SecurityMetadataSourceProviderFactory {

    private final static String SECURITY_META_CACHE_NAME = "USER_SECURITY_METADATA_SOURCE";


//	@Resource(name = "superAdminSecurityMetadataSourceProvider")
//	private SuperAdminSecurityMetadataSourceProvider superAdminSecurityMetadataSourceProvider;

    @Resource(name = "anonymousSecurityMetadataSourceProvider")
    private AnonymousSecurityMetadataSourceProvider anonymousSecurityMetadataSourceProvider;
    //@Resource(name = "customFilterSecurityInterceptor")
    //private FilterSecurityInterceptor filterSecurityInterceptor;

    @Autowired
    private TenantFacadeService tenantService;

    @Autowired
    private GuavaCacheManager guavaCacheManager;


    public SecurityMetadataSourceProvider getSecurityMetadataSourceProvider() {
        User user = SpringSecurityUtils.getCurrentUser();
        Cache cache = guavaCacheManager.getCache(SECURITY_META_CACHE_NAME);
        // 取消租户，并且superadmin现在也可以配置角色和权限了，所以这里暂时先
        // 全部放开，大家都公用默认租户T001来统一处理，等后续再分开出来
        if (user instanceof SuperAdminDetails || user instanceof UserDetails) {
            String tenantId = Config.DEFAULT_TENANT;
            return cache.get(tenantId, new Callable<UserSecurityMetadataSourceProvider>() {
                @Override
                public UserSecurityMetadataSourceProvider call() throws Exception {
                    return new UserSecurityMetadataSourceProvider();
                }
            });
        }
        return anonymousSecurityMetadataSourceProvider;


        /*cache.get(SECURITY_META_CACHE_NAME + ".ANONYMOUSE",
                new Callable<AnonymousSecurityMetadataSourceProvider>() {
                    @Override
                    public AnonymousSecurityMetadataSourceProvider call() throws Exception {
                        AnonymousSecurityMetadataSourceProvider anonymousSecurityMetadataSourceProvider = new AnonymousSecurityMetadataSourceProvider();
                        anonymousSecurityMetadataSourceProvider.setFilterSecurityInterceptor(
                                ApplicationContextHolder.getBean("customFilterSecurityInterceptor",
                                        FilterSecurityInterceptor.class));
                        anonymousSecurityMetadataSourceProvider.setSecurityMetadataSourceProviderFactory(
                                SecurityMetadataSourceProviderFactory.this);
                        return anonymousSecurityMetadataSourceProvider;
                    }
                });*/

    }

    public SecurityMetadataSourceProvider getDefaultTenantSecurityMetadataSourceProvider() {
        final Cache cache = guavaCacheManager.getCache(SECURITY_META_CACHE_NAME);
        return cache.get(Config.DEFAULT_TENANT, new Callable<UserSecurityMetadataSourceProvider>() {
            @Override
            public UserSecurityMetadataSourceProvider call() throws Exception {
                Tenant tenant = tenantService.getById(Config.DEFAULT_TENANT);
                UserSecurityMetadataSourceProvider userSecurityMetadataSourceProvider = new UserSecurityMetadataSourceProvider();
                cache.putIfAbsent(tenant.getAccount(), userSecurityMetadataSourceProvider);
                return userSecurityMetadataSourceProvider;
            }
        });
    }


}
