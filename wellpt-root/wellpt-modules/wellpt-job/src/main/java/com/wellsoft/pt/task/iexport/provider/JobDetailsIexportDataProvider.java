/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.task.entity.JobDetails;
import com.wellsoft.pt.task.iexport.acceptor.JobDetailsIexportData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 任务管理
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
@Service
@Transactional(readOnly = true)
public class JobDetailsIexportDataProvider extends AbstractIexportDataProvider<JobDetails, String> {
    static {
        TableMetaData.register(IexportType.JobDetails, "任务调度配置", JobDetails.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.JobDetails;
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
        JobDetails jobDetails = this.dao.get(JobDetails.class, uuid);
        if (jobDetails == null) {
            return new ErrorDataIexportData(IexportType.JobDetails, "找不到对应的任务管理依赖关系,可能已经被删除", "任务管理", uuid);
        }
        return new JobDetailsIexportData(jobDetails);
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 任务调度配置ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.JobDetails), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(JobDetails.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(JobDetails jobDetails) {
        return new JobDetailsIexportData(jobDetails).getName();
    }


    @Override
    public void putChildProtoDataHqlParams(JobDetails jobDetails, Map<String, JobDetails> parentMap, Map<String, ProtoDataHql> hqlMap) {
        if (StringUtils.isNotBlank(jobDetails.getModuleId())) {
            this.putAppFunctionParentMap(jobDetails, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<JobDetails>> getParentMapList(ProtoDataHql protoDataHql) {
        List<JobDetails> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), JobDetails.class);
        Map<String, List<JobDetails>> map = new HashMap<>();
        // 页面或组件定义依赖的任务配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (JobDetails jobDetails : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, jobDetails, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (JobDetails jobDetails : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + jobDetails.getUuid();
                this.putParentMap(map, jobDetails, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.DataIntegrationConfImpExp)) {
            for (JobDetails jobDetails : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + jobDetails.getUuid();
                this.putParentMap(map, jobDetails, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
