/*
 * @(#)2019年3月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;

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
 * 2019年3月8日.1	zhulh		2019年3月8日		Create
 * </pre>
 * @date 2019年3月8日
 */
public class TaskInfoDistributionData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5540162995872543890L;

    // 标题
    private String title;

    // 归属流程实例UUID
    @ApiModelProperty("归属流程实例UUID")
    private String belongToFlowInstUuid;

    // 信息分发
    private List<TaskInfoDistributionBean> distributeInfos = Lists.newArrayListWithCapacity(0);

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the belongToFlowInstUuid
     */
    public String getBelongToFlowInstUuid() {
        return belongToFlowInstUuid;
    }

    /**
     * @param belongToFlowInstUuid 要设置的belongToFlowInstUuid
     */
    public void setBelongToFlowInstUuid(String belongToFlowInstUuid) {
        this.belongToFlowInstUuid = belongToFlowInstUuid;
    }

    /**
     * @return the distributeInfos
     */
    public List<TaskInfoDistributionBean> getDistributeInfos() {
        return distributeInfos;
    }

    /**
     * @param distributeInfos 要设置的distributeInfos
     */
    public void setDistributeInfos(List<TaskInfoDistributionBean> distributeInfos) {
        this.distributeInfos = distributeInfos;
    }

}
