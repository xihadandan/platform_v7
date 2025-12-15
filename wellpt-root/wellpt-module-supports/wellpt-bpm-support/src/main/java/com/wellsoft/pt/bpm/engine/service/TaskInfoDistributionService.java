/*
 * @(#)2019年3月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskInfoDistributionDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInfoDistribution;
import com.wellsoft.pt.bpm.engine.query.api.TaskInfoDistributionQueryItem;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2019年3月9日.1	zhulh		2019年3月9日		Create
 * </pre>
 * @date 2019年3月9日
 */
public interface TaskInfoDistributionService extends JpaService<TaskInfoDistribution, TaskInfoDistributionDao, String> {

    /**
     * 获取分支流信息分发数据
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskInfoDistributionQueryItem> getBranchTaskDistributeInfos(String flowInstUuid);

    /**
     * 获取子流程信息分发数据
     *
     * @param flowInstUuid
     * @return
     */
    Page<TaskInfoDistributionQueryItem> getSubFlowDistributeInfos(String flowInstUuid, String keyword, Page<TaskInfoDistributionQueryItem> page);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskInfoDistribution> listByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);
}
