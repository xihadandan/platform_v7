/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.JobFunction;

import java.util.List;

/**
 * Description:岗位职能
 *
 * </pre>
 */
public interface JobFunctionService {
    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getByJob(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByJob(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteFunction(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getFunctions(String uuid);

    public void save(JobFunction entity);

}
