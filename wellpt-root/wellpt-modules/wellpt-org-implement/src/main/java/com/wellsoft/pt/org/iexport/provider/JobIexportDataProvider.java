/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.iexport.provider;

import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.iexport.acceptor.JobIexportData;

/**
 * Description: 消息模板
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-19.1	linz		2016-1-19		Create
 * </pre>
 * @date 2016-1-19
 */
//@Service
//@Transactional(readOnly = true)
public class JobIexportDataProvider extends AbstractIexportDataProvider<Job, String> {
    static {
        TableMetaData.register(IexportType.Job, "职位", Job.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Job;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        Job job = this.dao.get(Job.class, uuid);
        if (job == null) {
            return new ErrorDataIexportData(IexportType.Job, "找不到对应的职位依赖关系,可能已经被删除", "职位", uuid);
        }
        return new JobIexportData(job);
    }

    @Override
    public String getTreeName(Job job) {
        return new JobIexportData(job).getName();
    }

}
