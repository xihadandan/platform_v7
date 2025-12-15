/*
 * @(#)2021-08-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowInspectionFileRecordDao;
import com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity;
import com.wellsoft.pt.workflow.service.WfFlowInspectionFileRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表WF_FLOW_INSPECTION_FILE_RECORD的service服务接口实现类
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
@Service
public class WfFlowInspectionFileRecordServiceImpl extends AbstractJpaServiceImpl<WfFlowInspectionFileRecordEntity, WfFlowInspectionFileRecordDao, String> implements WfFlowInspectionFileRecordService {


    @Override
    public WfFlowInspectionFileRecordEntity getFlowInspectionFileRecordByFileUuid(String fileUuid) {
        WfFlowInspectionFileRecordEntity entity = new WfFlowInspectionFileRecordEntity();
        entity.setFileUuid(fileUuid);
        List<WfFlowInspectionFileRecordEntity> entities = this.dao.listByEntity(entity);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    @Override
    public List<WfFlowInspectionFileRecordEntity> getRecordListByFlowInstUuid(String flowInstUuid) {
        WfFlowInspectionFileRecordEntity entity = new WfFlowInspectionFileRecordEntity();
        entity.setFlowInstUuid(flowInstUuid);
        List<WfFlowInspectionFileRecordEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }
}
