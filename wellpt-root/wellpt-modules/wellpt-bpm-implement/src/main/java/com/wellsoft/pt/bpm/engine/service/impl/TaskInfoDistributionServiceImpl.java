/*
 * @(#)2019年3月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.dao.TaskInfoDistributionDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInfoDistribution;
import com.wellsoft.pt.bpm.engine.query.api.TaskInfoDistributionQueryItem;
import com.wellsoft.pt.bpm.engine.service.TaskInfoDistributionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
 * 2019年3月9日.1	zhulh		2019年3月9日		Create
 * </pre>
 * @date 2019年3月9日
 */
@Service
public class TaskInfoDistributionServiceImpl extends
        AbstractJpaServiceImpl<TaskInfoDistribution, TaskInfoDistributionDao, String> implements
        TaskInfoDistributionService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInfoDistributionService#getBranchTaskDistributeInfos(java.lang.String)
     */
    @Override
    public List<TaskInfoDistributionQueryItem> getBranchTaskDistributeInfos(String flowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listItemByNameSQLQuery("branchTaskDistributeInfoQuery", TaskInfoDistributionQueryItem.class,
                values, new PagingInfo(1, Integer.MAX_VALUE, false));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInfoDistributionService#getSubFlowDistributeInfos(java.lang.String)
     */
    @Override
    public Page<TaskInfoDistributionQueryItem> getSubFlowDistributeInfos(String flowInstUuid, String keyword, Page<TaskInfoDistributionQueryItem> page) {
        PagingInfo pagingInfo = null;
        if (page != null) {
            pagingInfo = new PagingInfo(page.getPageNo(), page.getPageSize(), true);
        } else {
            page = new Page<>();
            pagingInfo = new PagingInfo(1, Integer.MAX_VALUE, false);
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        if (StringUtils.isNotBlank(keyword)) {
            values.put("keyword", "%" + keyword + "%");
        }
        page.setResult(this.dao.listItemByNameSQLQuery("subFlowDistributeInfoQueryByParentFlowInstUuid", TaskInfoDistributionQueryItem.class,
                values, pagingInfo));
        page.setTotalCount(pagingInfo.getTotalCount());
        return page;
    }

    /**
     * @param flowInstUuid
     * @return
     */
    @Override
    public List<TaskInfoDistribution> listByFlowInstUuid(String flowInstUuid) {
        return this.dao.listByFieldEqValue("flowInstUuid", flowInstUuid);
    }

    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        String hql = "delete from TaskInfoDistribution t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

}
