/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.JobPrivilege;

import java.util.List;

/**
 * Description:职位角色
 *
 * </pre>
 */
public interface JobPrivilegeService {
    public List<JobPrivilege> getJobPrivilegeByJobUuid(String jobUuid);

    public List<JobPrivilege> getJobPrivilegeByPrivilegeUuid(String privilegeUuid);
}
