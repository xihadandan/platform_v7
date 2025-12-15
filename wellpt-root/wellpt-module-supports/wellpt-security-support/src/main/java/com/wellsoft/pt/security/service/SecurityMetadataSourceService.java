/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/**
 * Description: 用于加载SecurityMetadataSource数据的服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-16.1	zhulh		2013-1-16		Create
 * </pre>
 * @date 2013-1-16
 */
public interface SecurityMetadataSourceService {
    /**
     * 资源、权限、角色更新后，调用这方法更新当前租户的系统资源，以后可考虑用消息通知更新
     */
    void loadSecurityMetadataSource();

    /**
     * 资源、权限、角色更新后，调用这方法更新当前租户的系统资源，以后可考虑用消息通知更新
     *
     * @param configUuid
     * @param configType
     */
    void loadSecurityMetadataSource(String configUuid, String configType);

    /**
     * 获取指定类型对像的角色配置
     *
     * @param object
     * @param functionType
     * @return
     */
    Collection<ConfigAttribute> getAttributes(Object object, String functionType);

    /**
     * 指定类型对象的匿名角色配置
     *
     * @param resouceId
     * @param key
     * @param value
     */
    void addAnonymousAttributes(String resouceId, Collection<Object> objects, String functionType);

    /**
     * @param resourceId
     */
    void removeResourceAttributes(String resourceId);

    void removeAnonymousAttributes(String resourceId, Collection<Object> value, String key);

    Set<GrantedAuthority> getGrantedAuthority(String roleUuid);

    Set<GrantedAuthority> getUserGrantedAuthority(String roleUuid);

    boolean isSecurityCacheExpired();
}
