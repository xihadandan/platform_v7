/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserJob;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 用户职位DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-25.1  zhengky	2014-8-25	  Create
 * </pre>
 * @date 2014-8-25
 */
@Repository
public class UserJobDao extends OrgHibernateDao<UserJob, String> {

    private String QUERY_JOB = "from UserJob user_job where user_job.job.uuid = ?";
    private String QUERY_BY_USER_UUID = "from UserJob user_job where user_job.user.uuid = ?";

    private String DELETE_BY_USER_UUID = "delete from UserJob user_job where user_job.user.uuid = ?";
    private String DELETE_JOB_BY_JOB_UUID = "delete from UserJob user_job where user_job.job.uuid = ?";

    private String QUERY_MAJOR_BY_USER_UUID = "from UserJob user_job where user_job.isMajor = true and user_job.user.uuid = ?";
    private String QUERY_OTHER_BY_USER_UUID = "from UserJob user_job where user_job.isMajor = false and user_job.user.uuid = ?";

    private String DELETE_MAJOR_BY_USER_UUID = "delete from UserJob user_job where user_job.isMajor = true and user_job.user.uuid = ?";
    private String DELETE_OTHER_BY_USER_UUID = "delete from UserJob user_job where user_job.isMajor = false and user_job.user.uuid = ?";

    /**
     * @param uuid
     * @return
     */
    public List<UserJob> getByUser(String uuid) {
        return this.find(QUERY_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUser(String uuid) {
        this.batchExecute(DELETE_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteJob(String uuid) {
        this.batchExecute(DELETE_JOB_BY_JOB_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<UserJob> getJobs(String uuid) {
        return this.find(QUERY_JOB, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<UserJob> getMajorJobs(String uuid) {
        return this.find(QUERY_MAJOR_BY_USER_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<UserJob> getOtherJobs(String uuid) {
        return this.find(QUERY_OTHER_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteMajorByUser(String uuid) {
        this.batchExecute(DELETE_MAJOR_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteOtherByUser(String uuid) {
        this.batchExecute(DELETE_OTHER_BY_USER_UUID, uuid);
    }

}
