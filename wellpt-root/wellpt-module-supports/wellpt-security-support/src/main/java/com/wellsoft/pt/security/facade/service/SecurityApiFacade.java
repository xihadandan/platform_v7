/*
 * @(#)2013-3-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.facade.service;

import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.security.audit.entity.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
@GroovyUseable
public interface SecurityApiFacade extends BaseService {

    boolean isAllowDomainLogin(String userId, String domainAddr);

    int getSmsVerifyCodeTimeOut();

    boolean isRequiredSmsVerifyCode(String userId, String ipAddr);

    boolean isAllowLogin(String userId, String ipAddr);

    boolean isAllowLogin(String userId);

    boolean isRequiredLoginVerifyCode(String ipAddr);

    boolean hasRole(String userId, String roleId);

    boolean isGranted(Object object, String functionType);

    boolean isGranted(String code);

    Resource getResourceByCode(String code);

    Resource getButtonByCode(String code);

    List<Resource> getDynamicButtonResourcesByCode(String code);

    /**
     * 获取系统单位级和平台级的所有角色，以分组下拉框方式展示， 默认一起返回平台角色
     */
    public Select2GroupData queryRoleListByUnitForSelect2Group(Select2QueryInfo queryInfo);

    Set<GrantedAuthority> queryAllGrantedAuthoritiesByUser(String userId);

    Map<String, Map<Object, Collection<ConfigAttribute>>> loadConfigAttribute();

    List<String> queryRoleIdByCurrentUserUnitId();

    /**
     * 添加不受权限控制的资源
     *
     * @param resouceUuid
     */
    void addAnonymousResource(String resourceId);

    /**
     * 删除资源的权限控制
     *
     * @param resourceId
     */
    void removeResourceAttribute(String resourceId);

    void removeAnonymousResource(String resourceId);

    boolean checkCurrentUserPassword(String password);
}
