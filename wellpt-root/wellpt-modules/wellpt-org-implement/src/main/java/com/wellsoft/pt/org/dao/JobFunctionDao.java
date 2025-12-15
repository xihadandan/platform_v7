/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.JobFunction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 岗位职能DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-24.1  zhengky	2014-8-24	  Create
 * </pre>
 * @date 2014-8-24
 */
@Repository
public class JobFunctionDao extends OrgHibernateDao<JobFunction, String> {

    private String QUERY_FUNCTION = "from JobFunction job_function where job_function.function.uuid = ?";
    private String QUERY_BY_JOB_UUID = "from JobFunction job_function where job_function.job.uuid = ?";
    private String DELETE_BY_JOB_UUID = "delete from JobFunction job_function where job_function.job.uuid = ?";
    private String DELETE_FUNCTION_BY_JOB_UUID = "delete from JobFunction job_function where job_function.function.uuid = ?";

    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getByJob(String uuid) {
        return this.find(QUERY_BY_JOB_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByJob(String uuid) {
        this.batchExecute(DELETE_BY_JOB_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteFunction(String uuid) {
        this.batchExecute(DELETE_FUNCTION_BY_JOB_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<JobFunction> getFunctions(String uuid) {
        return this.find(QUERY_FUNCTION, uuid);
    }

}
