/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.GroupRole;

import java.util.List;

/**
 * Description:群组角色
 *
 * </pre>
 */
public interface GroupRoleService {
    /**
     * 获取群组角色中间表
     *
     * @param groupUuid 群组UUID
     * @return
     */
    public List<GroupRole> getGroupRoleByGroupUuid(String groupUuid);

    /**
     * 获取群组角色中间表
     *
     * @param roleUuid 角色UUID
     * @return
     */
    public List<GroupRole> getGroupRoleByRoleUuid(String roleUuid);

    /**
     * 根据群组UUID和角色UUID删除对应的中间角色表
     *
     * @param groupUuid
     * @param roleUuid
     */
    public void deleteGroupRoleByGroupUuidAndRoleUuid(String groupUuid, String roleUuid);

    public void save(GroupRole groupRole);
}
