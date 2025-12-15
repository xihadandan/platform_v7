/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfOpinionRuleItemDao;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;
import com.wellsoft.pt.workflow.service.WfOpinionRuleItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WF_OPINION_RULE_ITEM的service服务接口实现类
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
public class WfOpinionRuleItemServiceImpl
        extends AbstractJpaServiceImpl<WfOpinionRuleItemEntity, WfOpinionRuleItemDao, String>
        implements WfOpinionRuleItemService {

    @Override
    public List<WfOpinionRuleItemEntity> getWfOpinionRuleItemList(String opinionRuleUuid) {
        WfOpinionRuleItemEntity entity = new WfOpinionRuleItemEntity();
        entity.setOpinionRuleUuid(opinionRuleUuid);
        List<WfOpinionRuleItemEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }

    @Override
    public List<WfOpinionRuleItemEntity> getWfOpinionRuleItemListByOpinionRuleUuids(List<String> opinionRuleUuids) {

        if (opinionRuleUuids.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("opinionRuleUuids", opinionRuleUuids);
        return this.dao.listByNameSQLQuery("getWfOpinionRuleItemListByOpinionRuleUuids", values);
    }

    @Override
    public void deleteByOpinionRuleUuid(String opinionRuleUuid) {

        Map<String, Object> values = Maps.newHashMap();
        values.put("opinionRuleUuid", opinionRuleUuid);
        this.dao.deleteByNamedSQL("deleteByOpinionRuleUuid", values);
    }
}
