/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.UserRole;

import java.util.List;

/**
 * Description:用户角色
 *
 * </pre>
 */
public interface UserRoleService {
    /**
     * 根据用户uuid获取角色中间表
     *
     * @param userUuid
     * @return
     */
    public List<UserRole> getUserRoleByUserUuid(String userUuid);

    /**
     * 根据权限uuid获取对应的角色中间表
     *
     * @param roleUuid
     * @return
     */
    public List<UserRole> getUserRoleByRoleUuid(String roleUuid);

    /**
     * 根据用户UUID和角色UUID删除对应的角色信息
     *
     * @param userUuid
     * @param roleUuid
     */
    public void deleteUserRoleByUserUuidAndRoleUuid(String userUuid, String roleUuid);

    public void save(UserRole userRole);
}
