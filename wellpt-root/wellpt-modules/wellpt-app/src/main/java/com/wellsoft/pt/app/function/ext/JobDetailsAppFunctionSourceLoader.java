/*
 * @(#)2018年8月20日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.task.entity.JobDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年8月20日.1	zhulh		2018年8月20日		Create
 * </pre>
 * @date 2018年8月20日
 */
@Service
@Transactional(readOnly = true)
public class JobDetailsAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.JobDetails;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<JobDetails> jobDetailsList = this.dao.getAll(JobDetails.class);
        for (JobDetails jobDetails : jobDetailsList) {
            appFunctionSources.add(convert2AppFunctionSource(jobDetails));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        JobDetails jobDetails = (JobDetails) item;
        String uuid = jobDetails.getUuid();
        String fullName = jobDetails.getName() + "_" + jobDetails.getId();
        String name = "定时任务_" + jobDetails.getName();
        String id = jobDetails.getId() + "_" + jobDetails.getJobClassName();
        String code = jobDetails.getCode();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                true, category, false,
                null, StringUtils.isNotBlank(
                jobDetails.getJobClassName()) ? ClassUtils.getClassMethodDescriptions().get(
                jobDetails.getJobClassName()) : "");

    }

}
