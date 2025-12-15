/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.UserPrivilege;

import java.util.List;

/**
 * Description:用户权限
 *
 * </pre>
 */
public interface UserPrivilegeService {
    public List<UserPrivilege> getUserPrivilegeByUserUuid(String userUuid);

    public List<UserPrivilege> getUserPrivilegeByPrivilegeUuid(String privilegeUuid);

    public void save(UserPrivilege userPrivilege);
}
