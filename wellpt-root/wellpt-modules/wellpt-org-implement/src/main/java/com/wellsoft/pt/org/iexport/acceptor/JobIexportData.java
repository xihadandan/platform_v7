/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.org.entity.Job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-30	linz		2015-6-30		Create
 * </pre>
 * @date 2015-6-30
 */
public class JobIexportData extends IexportData {
    public Job job;

    public JobIexportData(Job job) {
        this.job = job;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return job.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return job.getName();
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return job.getRecVer();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.Job;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, job);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        //职务
        Duty duty = job.getDuty();
        if (duty != null)
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Duty).getData(duty.getUuid()));
        //		//人员
        //		for (UserJob userJob : job.getJobUsers()) {
        //			dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.User).getData(
        //					userJob.getUser().getUuid()));
        //		}
        //		//部门
        //		dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Department).getData(
        //				job.getDepartmentUuid()));
        return dependencies;
    }

}
