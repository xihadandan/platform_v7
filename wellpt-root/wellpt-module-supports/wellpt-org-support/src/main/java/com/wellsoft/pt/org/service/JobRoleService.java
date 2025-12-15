/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.JobRole;

import java.util.List;

/**
 * Description:职位角色
 *
 * </pre>
 */
public interface JobRoleService {
    /**
     * 根据jobuuid获取角色中间表
     *
     * @param jobUuid
     * @return
     */
    public List<JobRole> getJobRoleByJobUuid(String jobUuid);

    /**
     * 根据角色UUID获取角色中间表
     *
     * @param roleUuid
     * @return
     */
    public List<JobRole> getJobRoleByRoleUuid(String roleUuid);

    /**
     * 根据job和role删除对应的信息
     *
     * @param jobUuid
     * @param roleUuid
     */
    public void deleteJobRoleByJobUuidAndRoleUuid(String jobUuid, String roleUuid);

    public void save(JobRole jobRole);

    public void delete(JobRole jobRole);
}
