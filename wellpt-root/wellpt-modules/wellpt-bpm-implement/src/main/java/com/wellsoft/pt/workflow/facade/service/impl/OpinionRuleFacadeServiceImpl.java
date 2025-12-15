/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.OpinionRuleDto;
import com.wellsoft.pt.workflow.dto.OpinionRuleIncludeItemDto;
import com.wellsoft.pt.workflow.dto.SaveOpinionRuleDto;
import com.wellsoft.pt.workflow.entity.OpinionRuleEntity;
import com.wellsoft.pt.workflow.entity.WfOpinionRuleItemEntity;
import com.wellsoft.pt.workflow.enums.ItemConditionEnum;
import com.wellsoft.pt.workflow.facade.service.OpinionRuleFacadeService;
import com.wellsoft.pt.workflow.facade.service.WfOpinionRuleItemFacadeService;
import com.wellsoft.pt.workflow.service.OpinionRuleService;
import com.wellsoft.pt.workflow.service.WfOpinionCheckSetService;
import com.wellsoft.pt.workflow.service.WfOpinionRuleItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据库表UF_OPINION_RULE的门面服务实现类
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
public class OpinionRuleFacadeServiceImpl extends AbstractApiFacade implements OpinionRuleFacadeService {

    private static final String SYSTEM_UNIT_ID = "S0000000000";
    @Autowired
    private OpinionRuleService opinionRuleService;
    @Autowired
    private WfOpinionCheckSetService wfOpinionCheckSetService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private WfOpinionRuleItemFacadeService wfOpinionRuleItemFacadeService;
    @Autowired
    private WfOpinionRuleItemService wfOpinionRuleItemService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    @Transactional
    public void saveOpinionRule(SaveOpinionRuleDto saveOpinionRuleDto) {
        List<WfOpinionRuleItemEntity> objects = (List<WfOpinionRuleItemEntity>) JSONArray
                .parseArray(saveOpinionRuleDto.getOpinionRuleItems(), WfOpinionRuleItemEntity.class);
        OpinionRuleEntity opinionRuleEntity = null;
        if (StringUtils.isNotBlank(saveOpinionRuleDto.getUuid())) {
            opinionRuleEntity = opinionRuleService.getOne(saveOpinionRuleDto.getUuid());
        } else {
            opinionRuleEntity = new OpinionRuleEntity();
            saveOpinionRuleDto.setSystem(RequestSystemContextPathResolver.system());
            saveOpinionRuleDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }


        BeanUtils.copyProperties(saveOpinionRuleDto, opinionRuleEntity);
        opinionRuleEntity.setOpinionRuleItem(getOpinionRuleItemStr(objects));
        opinionRuleService.save(opinionRuleEntity);

        // 先删除旧的，再新增
        wfOpinionRuleItemFacadeService.deleteByOpinionRuleUuid(opinionRuleEntity.getUuid());
        wfOpinionRuleItemFacadeService.saveOpinionRuleItems(opinionRuleEntity.getUuid(), objects);


        List<AppDefElementI18nEntity> i18nEntities = Lists.newArrayList();
        if (MapUtils.isNotEmpty(saveOpinionRuleDto.getI18n())) {
            appDefElementI18nService.deleteAllI18n(null, opinionRuleEntity.getUuid()
                    , new BigDecimal("1.0"), IexportType.FLowOpinionRule);
            Set<Map.Entry<String, Map<String, String>>> entrySet = saveOpinionRuleDto.getI18n().entrySet();
            for (Map.Entry<String, Map<String, String>> map : entrySet) {
                String locale = map.getKey();
                Map<String, String> props = map.getValue();
                Set<Map.Entry<String, String>> propEntrySet = props.entrySet();
                for (Map.Entry<String, String> prop : propEntrySet) {
                    AppDefElementI18nEntity entity = new AppDefElementI18nEntity();
                    entity.setCode(prop.getKey());
                    entity.setVersion(new BigDecimal("1.0"));
                    entity.setDefId(opinionRuleEntity.getUuid());
                    entity.setApplyTo(IexportType.FLowOpinionRule);
                    entity.setContent(prop.getValue());
                    entity.setLocale(locale);
                    i18nEntities.add(entity);
                }
            }
        }
        appDefElementI18nService.saveAll(i18nEntities);


    }

    @Override
    public void delete(List<String> uuids) {
        for (String uuid : uuids) {
            opinionRuleService.delete(uuid);
        }

    }

    @Override
    public Boolean isReferencedByOpinionRuleUuids(List<String> opinionRuleUuids) {

        return wfOpinionCheckSetService.isReferencedByOpinionRuleUuids(opinionRuleUuids);
    }

    @Override
    public List<OpinionRuleDto> getCurrentUserBelongOpinionRuleList(String flowId) {
        List<String> systemUintIds = new ArrayList<>();
        if (StringUtils.isNotBlank(flowId)) {
            FlowDefinition flowDefinition = flowDefinitionService.getById(flowId);
            if (flowDefinition == null) {
                throw new BusinessException("此流程不存在");
            }
            if (SYSTEM_UNIT_ID.equals(flowDefinition.getSystemUnitId())
                    || StringUtils.isBlank(flowDefinition.getSystemUnitId())) {
                // 超管创建的流程,或者是没有单位的流程
                systemUintIds.add(SYSTEM_UNIT_ID);
            } else {
                systemUintIds.add(SYSTEM_UNIT_ID);
                systemUintIds.add(SpringSecurityUtils.getCurrentUserUnitId());
            }
        } else {
            // 未保存的流程
            if (SYSTEM_UNIT_ID.equals(SpringSecurityUtils.getCurrentUserUnitId())) {
                // 操作当前未保存的流程用户是超管
                systemUintIds.add(SYSTEM_UNIT_ID);
            } else {
                systemUintIds.add(SYSTEM_UNIT_ID);
                systemUintIds.add(SpringSecurityUtils.getCurrentUserUnitId());
            }
        }
        List<OpinionRuleEntity> opinionRuleEntities = opinionRuleService
                .getOpinionRuleListBySystemUintIds(systemUintIds);
        return BeanUtils.copyCollection(opinionRuleEntities, OpinionRuleDto.class);
    }

    @Override
    public OpinionRuleIncludeItemDto getOpinionRuleDetail(String uuid) {
        OpinionRuleEntity opinionRuleEntity = opinionRuleService.getOne(uuid);
        OpinionRuleIncludeItemDto opinionRuleIncludeItemDto = new OpinionRuleIncludeItemDto();
        BeanUtils.copyProperties(opinionRuleEntity, opinionRuleIncludeItemDto);
        List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(uuid, null, new BigDecimal(1), IexportType.FLowOpinionRule);
        if (CollectionUtils.isNotEmpty(i18nEntities)) {
            for (AppDefElementI18nEntity i : i18nEntities) {
                if (!opinionRuleIncludeItemDto.getI18n().containsKey(i.getLocale())) {
                    opinionRuleIncludeItemDto.getI18n().put(i.getLocale(), Maps.newHashMap());
                }
                opinionRuleIncludeItemDto.getI18n().get(i.getLocale()).put(i.getCode(), i.getContent());
            }
        }
        List<WfOpinionRuleItemEntity> wfOpinionRuleItemEntities = wfOpinionRuleItemService
                .getWfOpinionRuleItemList(uuid);
        opinionRuleIncludeItemDto.setOpinionRuleItemEntitys(wfOpinionRuleItemEntities);
        return opinionRuleIncludeItemDto;
    }

    /**
     * 封装校验项的值
     *
     * @param itemDtos
     * @return java.lang.String
     **/
    private String getOpinionRuleItemStr(List<WfOpinionRuleItemEntity> itemDtos) {
        StringBuilder stringBuilder = new StringBuilder();
        for (WfOpinionRuleItemEntity item : itemDtos) {
            stringBuilder.append(" [" + item.getItemName() + "] ");
            stringBuilder.append(ItemConditionEnum.getByValue(item.getItemCondition()).getName() + " ");
            if (StringUtils.isNotBlank(item.getItemValue())) {
                stringBuilder.append(item.getItemValue() + ";");
            } else {
                stringBuilder.append("");
            }

        }
        return stringBuilder.toString();
    }
}
