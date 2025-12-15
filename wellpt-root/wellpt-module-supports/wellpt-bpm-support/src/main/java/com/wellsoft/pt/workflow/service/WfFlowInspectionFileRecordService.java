/*
 * @(#)2021-08-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;


import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowInspectionFileRecordDao;
import com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity;

import java.util.List;

/**
 * Description: 数据库表WF_FLOW_INSPECTION_FILE_RECORD的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-08-25.1	zenghw		2021-08-25		Create
 * </pre>
 * @date 2021-08-25
 */
public interface WfFlowInspectionFileRecordService extends JpaService<WfFlowInspectionFileRecordEntity, WfFlowInspectionFileRecordDao, String> {

    /**
     * 手写签批附件详情
     *
     * @param fileUuid 原附件uuid
     * @return com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity
     **/
    public WfFlowInspectionFileRecordEntity getFlowInspectionFileRecordByFileUuid(String fileUuid);

    /**
     * 获取签批记录列表通过流程实例uuid
     *
     * @param flowInstUuid
     * @return java.util.List<com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity>
     **/
    public List<WfFlowInspectionFileRecordEntity> getRecordListByFlowInstUuid(String flowInstUuid);
}
