/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.EmployeeJob;

import java.util.List;

/**
 * 员工职位
 *
 * </pre>
 */
public interface EmployeeJobService {
    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getByEmployee(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByEmployee(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteJob(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getJobs(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getMajorJobs(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getOtherJobs(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteMajorByEmployee(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteOtherByEmployee(String uuid);
}
