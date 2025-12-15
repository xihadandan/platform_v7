/*
 * @(#)2016-09-08 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access.intercept.provider;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.security.access.SecurityMetadataSourceProvider;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.support.SecurityResourceConfig;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-08.1	zhulh		2016-09-08		Create
 * </pre>
 * @date 2016-09-08
 */
public class UserSecurityMetadataSourceProvider implements SecurityMetadataSourceProvider {

    // 缓存过期标记
    private static final String CACHE_KEY_SECURITY_METADATA_SOURCE_EXPIRED = "_SecurityMetadataSourceExpired";
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();
    public static final String USER_SECURITY_CACHE_PREFIX = "user_security_";

    private static final Collection<ConfigAttribute> randomConfigAttribute = new ArrayList<ConfigAttribute>();


    static {
        String randomRole = UUID.randomUUID().toString();
        randomConfigAttribute.add(new SecurityConfig(randomRole));
    }


    private volatile Map<String, Map<Object, Collection<ConfigAttribute>>> allConfigAttribute = new ConcurrentHashMap<String, Map<Object, Collection<ConfigAttribute>>>();
    private volatile Map<String, Collection<ConfigAttribute>> requestMap = new ConcurrentHashMap<String, Collection<ConfigAttribute>>();
    private volatile Map<String, RequestMatcher> requestMatcherMap = new ConcurrentHashMap<String, RequestMatcher>();
    private volatile Map<String, String> urlKeyMap = new ConcurrentHashMap<String, String>();
    private Set<String> defaultLoginedAuthenticatedUrl = Sets.newHashSet("/html/**", "/json/data/services", "/api/org/user/loginLog",
            "/wellSocket/**", "/security/user/details/", "/personalinfo/**");
    private Map<String, RequestMatcher> loginedAuthenticateRequestMatcherMap = new ConcurrentHashMap<String, RequestMatcher>();
    private Map<String, Set<GrantedAuthority>> roleGrantedAuthorities = new ConcurrentHashMap<>();
    private Map<String, Collection<ConfigAttribute>> urlConfigAttributes = new HashMap<>();

    private static final String getCacheKey() {
        return SpringSecurityUtils.getCurrentTenantId() + CACHE_KEY_SECURITY_METADATA_SOURCE_EXPIRED;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getAllConfigAttributeMap()
     */
    @Override
    public Map<Object, Collection<ConfigAttribute>> getAllConfigAttributeMap() {
        Map<Object, Collection<ConfigAttribute>> map = new HashMap<Object, Collection<ConfigAttribute>>();
        for (Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : checkAndGetAllConfigAttributes()
                .entrySet()) {
            map.putAll(entry.getValue());
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getConfigAttributeMap(String)
     */
    @Override
    public Map<Object, Collection<ConfigAttribute>> getConfigAttributeMap(String functionType) {
        return checkAndGetAllConfigAttributes().get(functionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getAllConfigAttributes()
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Collection<ConfigAttribute> configAttributes = new HashSet<ConfigAttribute>();
        for (Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : checkAndGetAllConfigAttributes()
                .entrySet()) {
            Map<Object, Collection<ConfigAttribute>> value = entry.getValue();
            for (Entry<Object, Collection<ConfigAttribute>> valueEntry : value.entrySet()) {
                configAttributes.addAll(valueEntry.getValue());
            }
        }
        return configAttributes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#getRequestAttributes(Object)
     */
    @Override
    public Collection<ConfigAttribute> getRequestAttributes(Object object) {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        String url = urlPathHelper.getLookupPathForRequest(request);
        Stopwatch matchTimer = Stopwatch.createStarted();
        Collection configAttributes = Collections.EMPTY_LIST;
        try {
            if (urlConfigAttributes.containsKey(url)) {
                return urlConfigAttributes.get(url);
            }
            if (url.startsWith("/web/app/") && request.getParameter("pageUuid") != null) {
                // cms页面的权限由cms解析器判断
                configAttributes = Lists.newArrayList(new SecurityResourceConfig(url, BuildInRole.ROLE_USER.name())
                        , new SecurityResourceConfig(url, BuildInRole.ROLE_INTERNET_USER.name()));
                return configAttributes;
            }

            // 默认请求地址，登录即可
            if (SpringSecurityUtils.getAuthentication().isAuthenticated()) {
                for (String k : loginedAuthenticateRequestMatcherMap.keySet()) {
                    RequestMatcher matcher = loginedAuthenticateRequestMatcherMap.get(k);
                    if (matcher.matches(request)) {
                        configAttributes = Lists.newArrayList(new SecurityResourceConfig(url, BuildInRole.ROLE_USER.name())
                                , new SecurityResourceConfig(url, BuildInRole.ROLE_INTERNET_USER.name()));
                        return configAttributes;
                    }
                }
            }

            // 获取当前租户的权限资源
            Stopwatch timer = Stopwatch.createStarted();
            Map<String, Collection<ConfigAttribute>> requestMap = getRequestMap();
            if (urlKeyMap.containsKey(url)) {
                configAttributes = requestMap.get(urlKeyMap.get(url));
                return configAttributes;
            }

            if (requestMap.containsKey(url)) {
                configAttributes = requestMap.get(url);
                return configAttributes;
            }

            for (String key : requestMap.keySet()) {
                if (!requestMatcherMap.containsKey(key)) {
                    requestMatcherMap.put(key, new AntPathRequestMatcher(key));
                }
                RequestMatcher matcher = requestMatcherMap.get(key);
                if (matcher.matches(request)) {
                    urlKeyMap.put(url, key);
                    configAttributes = requestMap.get(key);
                    return configAttributes;
                }
            }

            // 使用applicationContext-security.xml中配置的URL访问角色判断
            FilterSecurityInterceptor filterSecurityInterceptor = ApplicationContextHolder
                    .getBean(FilterSecurityInterceptor.class);
            Collection<ConfigAttribute> anonymousConfigAttributes = filterSecurityInterceptor
                    .obtainSecurityMetadataSource().getAttributes(object);
            if (!(anonymousConfigAttributes == null || anonymousConfigAttributes.isEmpty())) {
                configAttributes = anonymousConfigAttributes;
                return configAttributes;
            }
            configAttributes = randomConfigAttribute;
        } catch (Exception e) {
            logger.error("判断地址权限异常：", e);
        } finally {
            logger.info("获取URL = {} 对应的权限属性耗时：{}", url, matchTimer.stop());
            if (!urlConfigAttributes.containsKey(url)) {
                urlConfigAttributes.put(url, configAttributes);
            }
        }
        return configAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getRequestURLAttributes(String url) {
        Map<String, Collection<ConfigAttribute>> requestMap = getRequestMap();
        if (urlKeyMap.containsKey(url)) {
            return requestMap.get(urlKeyMap.get(url));
        }

        if (requestMap.containsKey(url)) {
            return requestMap.get(url);
        }

        for (String key : requestMap.keySet()) {
            if (new AntPathMatcher().match(key, url)) {
                urlKeyMap.put(url, key);
                return requestMap.get(key);
            }
        }

        return null;
    }

    @Override
    public Set<GrantedAuthority> getGrantedAuthority(String roleUuid) {
        return this.roleGrantedAuthorities.get(roleUuid);
    }

    @Override
    public boolean isSecurityCacheExpired() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Boolean securityMetadataSourceExpired = (Boolean) cacheManager.getCache(ModuleID.SECURITY).getValue(getCacheKey());
        return BooleanUtils.isTrue(securityMetadataSourceExpired);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#reload()
     */
    @Override
    public void reload() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        cacheManager.getCache(ModuleID.SECURITY).put(getCacheKey(), true);
        checkAndGetAllConfigAttributes();

        if (Config.getValue("spring.security.default.authenticated.url") != null) {
            this.defaultLoginedAuthenticatedUrl.addAll(Arrays.asList(Config.getValue("spring.security.default.authenticated.url").split(",|;")));
        }
        for (String url : this.defaultLoginedAuthenticatedUrl) {
            loginedAuthenticateRequestMatcherMap.put(url, new AntPathRequestMatcher(url));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#reload(String, String)
     */
    @Override
    public void reload(String configUuid, String configType) {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        cacheManager.getCache(ModuleID.SECURITY).put(getCacheKey(), true);
        checkAndGetAllConfigAttributes();
        // reload();
    }

    /**
     * @param tenant
     * @return
     */
    private Map<String, Collection<ConfigAttribute>> getRequestMap() {
        if (!requestMap.isEmpty()) {
            return requestMap;
        }
        requestMapInitial();
        return requestMap;
    }

    private synchronized void requestMapInitial() {
        // [资源类型：{资源标识：[权限角色]}]
        for (Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : checkAndGetAllConfigAttributes()
                .entrySet()) {
            Map<Object, Collection<ConfigAttribute>> value = entry.getValue();
            for (Entry<Object, Collection<ConfigAttribute>> valueEntry : value.entrySet()) {
                addRequstMap(valueEntry.getKey(), valueEntry.getValue());
            }
        }
    }

    private Map<String, Map<Object, Collection<ConfigAttribute>>> checkAndGetAllConfigAttributes() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(ModuleID.SECURITY);
        String cacheKey = getCacheKey();
        Boolean securityMetadataSourceExpired = (Boolean) cache.getValue(cacheKey);
        RoleFacadeService roleFacadeService = ApplicationContextHolder.getBean(RoleFacadeService.class);
        if (!Boolean.FALSE.equals(securityMetadataSourceExpired) || allConfigAttribute.isEmpty()) {
            synchronized (UserSecurityMetadataSourceProvider.class) {
                // 重新取值
                securityMetadataSourceExpired = (Boolean) cache.getValue(cacheKey);
                if (!Boolean.FALSE.equals(
                        securityMetadataSourceExpired) || allConfigAttribute.isEmpty()) {
                    logger.info("#### 加载权限资源数据 ####");
                    StopWatch watch = new StopWatch("LOAD SECURITY METADATA");
                    SecurityApiFacade securityApiFacade = ApplicationContextHolder.getBean(
                            SecurityApiFacade.class);
                    // allConfigAttribute.clear();
                    watch.start("loadConfigAttribute");
                    allConfigAttribute = securityApiFacade.loadConfigAttribute();
                    watch.stop();
                    // 加载角色嵌套树
                    Stopwatch roleTimer = Stopwatch.createStarted();
                    watch.start("queryRoleGrantedAuthorities");
                    Map<String, Set<GrantedAuthority>> allRoleGrantedAuthorites = roleFacadeService.queryAllRoleNestedRoleGrantedAuthorities();
                    this.roleGrantedAuthorities.clear();
                    this.roleGrantedAuthorities.putAll(allRoleGrantedAuthorites);
                    watch.stop();
                    logger.info("<<<<<<<<<<<< 全量获取角色权限耗时：{}", watch.prettyPrint());

                    cache.clear();
                    cacheManager.getCache(ModuleID.APP).clear();
                    cache.put(cacheKey, false);
                    urlKeyMap.clear();
                    requestMap.clear();
                    requestMatcherMap.clear();
                    urlConfigAttributes.clear();

                    // 发布安全配置已加载事件
//                    ApplicationContextHolder.getApplicationContext().publishEvent(
//                            new SecurityConfigReloadedEvent(allConfigAttribute));
                }
            }
        }
        return allConfigAttribute;
    }

    @Override
    public void appStartLoadConfigAttribute() {
        this.reload();
        this.getRequestMap();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#addAnonymousAttributes(java.lang.String, java.util.Collection, java.lang.String)
     */
    @Override
    public void addAnonymousAttributes(String resouceId, Collection<Object> objects,
                                       String functionType) {
        Map<Object, Collection<ConfigAttribute>> attributesMap = checkAndGetAllConfigAttributes().get(
                functionType);
        if (attributesMap == null) {
            attributesMap = new HashMap<Object, Collection<ConfigAttribute>>();
            allConfigAttribute.put(functionType, attributesMap);
        }
        for (Object object : objects) {
            if (!attributesMap.containsKey(object)) {
                attributesMap.put(object, new ArrayList<ConfigAttribute>());
            }
            SecurityConfig securityConfig = new SecurityResourceConfig(resouceId,
                    BuildInRole.ROLE_ANONYMOUS.name());
            if (!attributesMap.get(object).contains(securityConfig)) {
                attributesMap.get(object).add(securityConfig);
                addRequstMap(object, Sets.newHashSet(securityConfig));
            }
        }
    }


    private void addRequstMap(Object object, Collection securityConfig) {
        String key = ObjectUtils.toString(object, StringUtils.EMPTY);
        if (key.startsWith(Separator.SLASH.getValue())) {//资源标识以'/'开头则表示请求地址
            if (!requestMap.containsKey(key)) {
                requestMap.put(key, new LinkedHashSet<ConfigAttribute>());
            }
            requestMap.get(key).addAll(securityConfig);
            if (!requestMatcherMap.containsKey(key)) {
                requestMatcherMap.put(key, new AntPathRequestMatcher(key));
            }
        }
    }

    private void removeRequestMap(Object object, Collection<ConfigAttribute> removes) {
        String key = ObjectUtils.toString(object, StringUtils.EMPTY);
        if (key.startsWith(Separator.SLASH.getValue())) {//资源标识以'/'开头则表示请求地址
            if (!requestMap.containsKey(key)) {
                return;
            }
            Collection<ConfigAttribute> existAttributes = requestMap.get(key);
            existAttributes.removeAll(removes);
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.access.SecurityMetadataSourceProvider#removeResourceAttributes(java.lang.String)
     */
    @Override
    public void removeResourceAttributes(String resourceId) {
        for (Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : checkAndGetAllConfigAttributes()
                .entrySet()) {
            Map<Object, Collection<ConfigAttribute>> value = entry.getValue();
            for (Entry<Object, Collection<ConfigAttribute>> valueEntry : value.entrySet()) {
                Collection<ConfigAttribute> configAttributes = valueEntry.getValue();
                List<ConfigAttribute> resourceAttributes = Lists.newArrayList();
                for (ConfigAttribute configAttribute : configAttributes) {
                    if (configAttribute instanceof SecurityResourceConfig
                            && StringUtils.equals(resourceId,
                            ((SecurityResourceConfig) configAttribute).getResourceId())) {
                        resourceAttributes.add(configAttribute);
                    }
                }
                configAttributes.removeAll(resourceAttributes);
                removeRequestMap(valueEntry.getKey(), resourceAttributes);
            }
        }

    }

    @Override
    public void removeAnonymousAttributes(String resourceId) {
        for (Entry<String, Map<Object, Collection<ConfigAttribute>>> entry : checkAndGetAllConfigAttributes()
                .entrySet()) {
            Map<Object, Collection<ConfigAttribute>> value = entry.getValue();
            for (Entry<Object, Collection<ConfigAttribute>> valueEntry : value.entrySet()) {
                Collection<ConfigAttribute> configAttributes = valueEntry.getValue();
                List<ConfigAttribute> resourceAttributes = Lists.newArrayList();
                for (ConfigAttribute configAttribute : configAttributes) {
                    if (configAttribute instanceof SecurityResourceConfig
                            && StringUtils.equals(resourceId,
                            ((SecurityResourceConfig) configAttribute).getResourceId()) && configAttribute.getAttribute().equals(
                            BuildInRole.ROLE_ANONYMOUS.name())) {
                        resourceAttributes.add(configAttribute);
                    }
                }
                configAttributes.removeAll(resourceAttributes);
                removeRequestMap(valueEntry.getKey(), resourceAttributes);
            }
        }

    }


}
