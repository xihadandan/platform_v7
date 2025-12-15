/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.JobPrincipal;

import java.util.List;

/**
 * Description:部门负责人
 *
 * </pre>
 */
public interface JobPrincipalService {

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<JobPrincipal> getPrincipal(String deptUuid);

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid);

}
