/*
 * @(#)2021-09-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowSignOpinionSaveTempDao;
import com.wellsoft.pt.workflow.entity.WfFlowSignOpinionSaveTempEntity;
import com.wellsoft.pt.workflow.service.WfFlowSignOpinionSaveTempService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WF_FLOW_SIGN_OPINION_SAVE_TEMP的service服务接口实现类
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
@Service
public class WfFlowSignOpinionSaveTempServiceImpl extends AbstractJpaServiceImpl<WfFlowSignOpinionSaveTempEntity, WfFlowSignOpinionSaveTempDao, String> implements WfFlowSignOpinionSaveTempService {


    @Override
    @Transactional
    public void deleteByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        String DELETE_BY_FLOW_INST_UUID = "DELETE FROM WF_FLOW_SIGN_OPINION_SAVE_TEMP WHERE FLOW_INST_UUID = :flowInstUuid";
        this.dao.deleteByNamedSQL("deleteByFlowInstUuid", values);
    }

    @Override
    public WfFlowSignOpinionSaveTempEntity getSignOpinionAndOpinionPosition(String flowInstUuid, String userId) {
        WfFlowSignOpinionSaveTempEntity entity = new WfFlowSignOpinionSaveTempEntity();
        entity.setFlowInstUuid(flowInstUuid);
        entity.setUserId(userId);
        List<WfFlowSignOpinionSaveTempEntity> entities = this.dao.listByEntity(entity);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }
}
