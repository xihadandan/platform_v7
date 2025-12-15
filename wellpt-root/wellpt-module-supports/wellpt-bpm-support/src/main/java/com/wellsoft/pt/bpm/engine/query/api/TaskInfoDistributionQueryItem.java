/*
 * @(#)2019年3月9日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.pt.bpm.engine.entity.TaskInfoDistribution;

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
public class TaskInfoDistributionQueryItem extends TaskInfoDistribution implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1205906674066711176L;

    // 分发的流程实例UUID
    private String distributeFlowInstUuid;

    // 分发的环节实例UUID
    private String distributeTaskInstUuid;

    // 分发的环节ID
    private String distributeTaskId;

    /**
     * @return the distributeFlowInstUuid
     */
    public String getDistributeFlowInstUuid() {
        return distributeFlowInstUuid;
    }

    /**
     * @param distributeFlowInstUuid 要设置的distributeFlowInstUuid
     */
    public void setDistributeFlowInstUuid(String distributeFlowInstUuid) {
        this.distributeFlowInstUuid = distributeFlowInstUuid;
    }

    /**
     * @return the distributeTaskInstUuid
     */
    public String getDistributeTaskInstUuid() {
        return distributeTaskInstUuid;
    }

    /**
     * @param distributeTaskInstUuid 要设置的distributeTaskInstUuid
     */
    public void setDistributeTaskInstUuid(String distributeTaskInstUuid) {
        this.distributeTaskInstUuid = distributeTaskInstUuid;
    }

    /**
     * @return the distributeTaskId
     */
    public String getDistributeTaskId() {
        return distributeTaskId;
    }

    /**
     * @param distributeTaskId 要设置的distributeTaskId
     */
    public void setDistributeTaskId(String distributeTaskId) {
        this.distributeTaskId = distributeTaskId;
    }

}
