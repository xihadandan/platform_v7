/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.dao.OpinionRuleDao;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.entity.OpinionRuleEntity;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;
import com.wellsoft.pt.workflow.service.OpinionRuleService;
import com.wellsoft.pt.workflow.service.WfOpinionRuleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表UF_OPINION_RULE的service服务接口实现类
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
public class OpinionRuleServiceImpl extends AbstractJpaServiceImpl<OpinionRuleEntity, OpinionRuleDao, String>
        implements OpinionRuleService {

    @Autowired
    private WfOpinionRuleItemService wfOpinionRuleItemService;

    @Override
    public List<OpinionRuleEntity> getOpinionRuleListBySystemUintIds(List<String> systemUintIds) {
        Map<String, Object> values = Maps.newHashMap();
        if (systemUintIds.size() == 0) {
            return new ArrayList<OpinionRuleEntity>();
        }
        values.put("systemUintIds", systemUintIds);
        values.put("system", RequestSystemContextPathResolver.system());
        return this.dao.listByNameSQLQuery("getOpinionRuleListBySystemUintIds", values);
    }

    @Override
    public List<OpinionRuleIncludeItemDto> getOpinionRuleIncludeItemDtos(List<String> ruleUuidList) {
        if (ruleUuidList.size() == 0) {
            return new ArrayList<>();
        }
        List<OpinionRuleEntity> list = this.listByUuids(ruleUuidList);
        List<OpinionRuleIncludeItemDto> dtos = BeanUtils.copyCollection(list, OpinionRuleIncludeItemDto.class);
        List<WfOpinionRuleItemEntity> itemEntityList = wfOpinionRuleItemService
                .getWfOpinionRuleItemListByOpinionRuleUuids(ruleUuidList);
        Map<String, List<WfOpinionRuleItemEntity>> ruleItemMap = new HashMap<>();
        for (WfOpinionRuleItemEntity item : itemEntityList) {
            List<WfOpinionRuleItemEntity> itemEntities = ruleItemMap.get(item.getOpinionRuleUuid());
            if (itemEntities == null) {
                itemEntities = new ArrayList<>();
            }
            itemEntities.add(item);
            ruleItemMap.put(item.getOpinionRuleUuid(), itemEntities);
        }
        for (OpinionRuleIncludeItemDto dto : dtos) {
            dto.setOpinionRuleItemEntitys(ruleItemMap.get(dto.getUuid()));
        }
        return dtos;
    }
}
