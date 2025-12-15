/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;
import com.wellsoft.pt.workflow.facade.service.WfOpinionRuleItemFacadeService;
import com.wellsoft.pt.workflow.service.WfOpinionRuleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 数据库表WF_OPINION_RULE_ITEM的门面服务实现类
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
public class WfOpinionRuleItemFacadeServiceImpl extends AbstractApiFacade implements WfOpinionRuleItemFacadeService {

    @Autowired
    private WfOpinionRuleItemService wfOpinionRuleItemService;

    @Override
    @Transactional
    public void saveOpinionRuleItems(String opinionRuleUuid, List<WfOpinionRuleItemEntity> opinionRuleItemEntities) {
        for (WfOpinionRuleItemEntity opinionRuleItemEntity : opinionRuleItemEntities) {
            opinionRuleItemEntity.setOpinionRuleUuid(opinionRuleUuid);
            opinionRuleItemEntity.setItemValue(opinionRuleItemEntity.getItemValue().trim());
            wfOpinionRuleItemService.save(opinionRuleItemEntity);
        }
    }

    @Override
    public List<WfOpinionRuleItemDto> getWfOpinionRuleItemList(String opinionRuleUuid) {
        List<WfOpinionRuleItemEntity> opinionRuleItemList = wfOpinionRuleItemService
                .getWfOpinionRuleItemList(opinionRuleUuid);
        List<WfOpinionRuleItemDto> opinionRuleItemDtos = BeanUtils.copyCollection(opinionRuleItemList,
                WfOpinionRuleItemDto.class);
        return opinionRuleItemDtos;
    }

    @Override
    public List<WfOpinionRuleItemDto> getWfOpinionRuleItemListByOpinionRuleUuids(List<String> opinionRuleUuids) {
        List<WfOpinionRuleItemEntity> opinionRuleItemList = wfOpinionRuleItemService
                .getWfOpinionRuleItemListByOpinionRuleUuids(opinionRuleUuids);
        List<WfOpinionRuleItemDto> opinionRuleItemDtos = BeanUtils.copyCollection(opinionRuleItemList,
                WfOpinionRuleItemDto.class);
        return opinionRuleItemDtos;
    }

    @Override
    public void deleteByOpinionRuleUuid(String opinionRuleUuid) {
        wfOpinionRuleItemService.deleteByOpinionRuleUuid(opinionRuleUuid);
    }
}
