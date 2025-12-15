/*
 * @(#)2014-2-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.FlowInstanceParameterDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
 * 2014-2-27.1	zhulh		2014-2-27		Create
 * </pre>
 * @date 2014-2-27
 */
@Service
public class FlowInstanceParameterServiceImpl extends
        AbstractJpaServiceImpl<FlowInstanceParameter, FlowInstanceParameterDao, String> implements
        FlowInstanceParameterService {

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from FlowInstanceParameter t where t.flowInstUuid = :flowInstUuid";

    @Override
    @SuppressWarnings({"cast"})
    public List<FlowInstanceParameter> findByExample(FlowInstanceParameter example) {
        return dao.listByEntity(example);
    }

    @Override
    public List<FlowInstanceParameter> getByFlowInstanceUuid(String flowInstUuid) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        return this.findByExample(example);
    }

    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService#countByFlowInstUuidAndName(java.lang.String, java.lang.String)
     */
    @Override
    public long countByFlowInstUuidAndName(String flowInstUuid, String name) {
        FlowInstanceParameter parameter = new FlowInstanceParameter();
        parameter.setFlowInstUuid(flowInstUuid);
        parameter.setName(name);
        return this.dao.countByEntity(parameter);
    }

    /**
     * @param flowInstUuid
     * @param name
     * @return
     */
    @Override
    public FlowInstanceParameter getByFlowInstUuidAndName(String flowInstUuid, String name) {
        FlowInstanceParameter parameter = new FlowInstanceParameter();
        parameter.setFlowInstUuid(flowInstUuid);
        parameter.setName(name);
        List<FlowInstanceParameter> parameters = this.listByEntity(parameter);
        if (CollectionUtils.isNotEmpty(parameters)) {
            return parameters.get(0);
        }
        return null;
    }

}
