/*
 * @(#)2021-09-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;


import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowSignOpinionSaveTempDao;
import com.wellsoft.pt.workflow.entity.WfFlowSignOpinionSaveTempEntity;

/**
 * Description: 数据库表WF_FLOW_SIGN_OPINION_SAVE_TEMP的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-09-10.1	zenghw		2021-09-10		Create
 * </pre>
 * @date 2021-09-10
 */
public interface WfFlowSignOpinionSaveTempService extends JpaService<WfFlowSignOpinionSaveTempEntity, WfFlowSignOpinionSaveTempDao, String> {


    /**
     * 删除流程实例uuid
     *
     * @param flowInstUuid 流程实例uuid
     * @return void
     **/
    public void deleteByFlowInstUuid(String flowInstUuid);

    /**
     * 获取签署意见和意见立场
     * 找不到返回null
     *
     * @param flowInstUuid
     * @param userId
     * @return com.wellsoft.pt.workflow.entity.WfFlowSignOpinionSaveTempEntity
     **/
    public WfFlowSignOpinionSaveTempEntity getSignOpinionAndOpinionPosition(String flowInstUuid, String userId);
}
