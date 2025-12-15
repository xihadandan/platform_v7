/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfOpinionCheckSetDao;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;
import com.wellsoft.pt.workflow.service.WfOpinionCheckSetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WF_OPINION_CHECK_SET的service服务接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-05-11.1	zenghw		2021-05-11		Create
 * </pre>
 * @date 2021-05-11
 */
@Service
public class WfOpinionCheckSetServiceImpl
        extends AbstractJpaServiceImpl<WfOpinionCheckSetEntity, WfOpinionCheckSetDao, String>
        implements WfOpinionCheckSetService {

    @Override
    public Boolean isReferencedByOpinionRuleUuids(List<String> opinionRuleUuids) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("opinionRuleUuids", opinionRuleUuids);
        List<WfOpinionCheckSetEntity> entities = this.dao.listByNameSQLQuery("isReferencedByOpinionRuleUuids", values);
        return entities.size() > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<WfOpinionCheckSetEntity> getOpinionCheckSets(String flowId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowId", flowId);

        return this.dao.listByNameSQLQuery("getOpinionCheckSets", values);
    }

    @Override
    public void deleteByFlowId(String flowId) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowId", flowId);
        this.dao.deleteByNamedSQL("deleteByFlowId", values);
    }
}
