/*
 * @(#)2016-09-08 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
public interface SecurityMetadataSourceProvider {

    Logger logger = LoggerFactory.getLogger(SecurityMetadataSourceProvider.class);


    /**
     * 获取所有类型的功能的角色列表
     *
     * @return
     */
    Map<Object, Collection<ConfigAttribute>> getAllConfigAttributeMap();

    /**
     * 获取指定类型的角色列表
     *
     * @param functionType
     * @return
     */
    Map<Object, Collection<ConfigAttribute>> getConfigAttributeMap(String functionType);

    /**
     * 获取所有的角色列表
     *
     * @return
     */
    Collection<ConfigAttribute> getAllConfigAttributes();

    /**
     * 应用启动时候，主动加载相关权限角色配置到内存
     */
    void appStartLoadConfigAttribute();

    /**
     * 获取请求所需要的角色
     *
     * @param request
     * @return
     */
    Collection<ConfigAttribute> getRequestAttributes(Object object);


    /**
     * 重新加载功能角色的配置信息
     */
    void reload();

    /**
     * 如何描述该方法
     *
     * @param configUuid
     * @param configType
     */
    void reload(String configUuid, String configType);

    /**
     * @param resouceId
     * @param objects
     * @param functionType
     */
    void addAnonymousAttributes(String resouceId, Collection<Object> objects, String functionType);

    /**
     * @param resourceId
     */
    void removeResourceAttributes(String resourceId);

    void removeAnonymousAttributes(String resourceId);

    public Collection<ConfigAttribute> getRequestURLAttributes(String url);

    Set<GrantedAuthority> getGrantedAuthority(String roleUuid);

    boolean isSecurityCacheExpired();
}
