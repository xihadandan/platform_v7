/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.GroupPrivilege;

import java.util.List;

/**
 * Description:群组权限
 *
 * </pre>
 */
public interface GroupPrivilegeService {
    public List<GroupPrivilege> getGroupPrivilegeByGroupUuid(String groupUuid);

    public List<GroupPrivilege> getGroupPrivilegeByPrivilegeUuid(String privilegeUuid);

    public void save(GroupPrivilege groupPrivilege);

    public void deleteGroupPrivilegeByGroupUuidAndPrivilegeUuid(String groupUuid, String privilegeUuid);
}
