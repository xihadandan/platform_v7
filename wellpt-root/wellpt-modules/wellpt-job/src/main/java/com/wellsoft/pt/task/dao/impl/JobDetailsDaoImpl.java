/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.task.dao.JobDetailsDao;
import com.wellsoft.pt.task.entity.JobDetails;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Repository
public class JobDetailsDaoImpl extends AbstractJpaDaoImpl<JobDetails, String> implements
        JobDetailsDao {

    private static String DELETE_BY_NAME = "delete from JobDetails job_details where job_details.name = :name";

    /**
     * @param name
     */
    @Override
    public void deleteByName(String name) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", name);
        this.deleteByHQL(DELETE_BY_NAME, values);
    }

    @Override
    public void updateLastExecuteInstance(String jobName, String instance) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("jobName", jobName);
        param.put("instance", instance);
        this.updateBySQL(
                "update task_job_details set last_execute_instance=:instance where uuid=:jobName",
                param);
    }

}
