/*
 * @(#)9/29/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.biz.dto.BizItemDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionItemIncludeItemDto;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.enums.EnumBizTimeLimitType;
import com.wellsoft.pt.biz.facade.service.BizItemDefinitionFacadeService;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.support.ItemData;
import com.wellsoft.pt.biz.support.ItemIncludeItem;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
 * 9/29/22.1	zhulh		9/29/22		Create
 * </pre>
 * @date 9/29/22
 */
@Service
public class BizItemDefinitionFacadeServiceImpl extends AbstractApiFacade implements BizItemDefinitionFacadeService {

    @Autowired
    private BizItemDefinitionService bizItemDefinitionService;

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 根据业务事项UUID获取业务事项定义
     *
     * @param uuid
     * @return
     */
    @Override
    public BizItemDefinitionDto getDto(String uuid) {
        BizItemDefinitionDto dto = new BizItemDefinitionDto();
        BizItemDefinitionEntity entity = bizItemDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据业务事项ID获取业务事项定义
     *
     * @param id
     * @return
     */
    @Override
    public BizItemDefinitionDto getDtoById(String id) {
        BizItemDefinitionDto dto = new BizItemDefinitionDto();
        BizItemDefinitionEntity entity = bizItemDefinitionService.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据业务ID获取事项定义
     *
     * @param businessId
     * @return
     */
    @Override
    public List<BizItemDefinitionDto> listByBusinessId(String businessId) {
        List<BizItemDefinitionEntity> entities = bizItemDefinitionService.listByBusinessId(businessId);
        return BeanUtils.copyCollection(entities, BizItemDefinitionDto.class);
    }

    /**
     * 保存业务事项定义
     *
     * @param dto
     */
    @Override
    public void saveDto(BizItemDefinitionDto dto) {
        BizItemDefinitionEntity entity = new BizItemDefinitionEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = bizItemDefinitionService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.bizItemDefinitionService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的事项定义!", dto.getId()));
            }
        }
        BeanUtils.copyProperties(dto, entity, IdEntity.BASE_FIELDS);
        bizItemDefinitionService.save(entity);
    }

    /**
     * 根据业务事项定义UUID列表删除业务事项定义
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }

        // 判断业务事项定义是否被使用
        if (isUsed(uuids)) {
            throw new BusinessException("业务事项定义已存在业务事项实例，无法删除！");
        }
        bizItemDefinitionService.deleteByUuids(uuids);
    }

    /**
     * 获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listDyFormDefinitionSelectData(Select2QueryInfo queryInfo) {
        List<DyFormFormDefinition> dyFormFormDefinitions = dyFormFacade.listDyFormDefinitionBasicInfo();
        return new Select2QueryData(dyFormFormDefinitions, "id", "name");
    }

    /**
     * 根据表单ID获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData getDyFormDefinitionSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] formIds = queryInfo.getIds();
        List<DyFormFormDefinition> dyFormFormDefinitions = Lists.newArrayList();
        for (String formId : formIds) {
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(formId);
            dyFormFormDefinitions.add(dyFormFormDefinition);
        }
        return new Select2QueryData(dyFormFormDefinitions, "id", "name");
    }

    /**
     * 查询事项数据
     *
     * @param processDefUuid
     * @param context
     * @return
     */
    @Override
    public List<QueryItem> queryItemData(String processDefUuid, QueryContext context) {
        Map<String, Object> values = context.getQueryParams();
        values.put("whereSql", context.getWhereSqlString());
        values.put("orderBy", context.getOrderString());
        List<QueryItem> queryItems = bizItemDefinitionService.queryItemData(processDefUuid, values, context.getPagingInfo());
        return queryItems;
    }

    /**
     * 获取事项数据的办理时限列表
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listTimeLimitDataSelectData(Select2QueryInfo queryInfo) {
        String itemDefId = queryInfo.getOtherParams("itemDefId");
        String itemCode = queryInfo.getOtherParams("itemCode");
        String timeLimitTypeString = queryInfo.getOtherParams("timeLimitType");
        Integer timeLimitType = EnumBizTimeLimitType.WorkingDay.getValue();
        if (StringUtils.isNotBlank(timeLimitTypeString)) {
            timeLimitType = Integer.valueOf(timeLimitTypeString);
        }
        List<ItemTimeLimit> timeLimitList = bizItemDefinitionService.listTimeLimitDataByItemCode(itemDefId, itemCode);
        List<Select2DataBean> select2DataBeans = Lists.newArrayList();
        for (ItemTimeLimit timeLimit : timeLimitList) {
            Select2DataBean dataBean = new Select2DataBean(timeLimit.getTimeLimit() + StringUtils.EMPTY,
                    timeLimit.getTimeLimit() + "个" + EnumBizTimeLimitType.getNameByValue(timeLimitType));
            select2DataBeans.add(dataBean);
        }
        return new Select2QueryData(select2DataBeans);
    }

    /**
     * 根据办理时限事项数据的办理时限列表
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData getTimeLimitDataSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }

    /**
     * 根据事项编码获取事项包含的事项
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<BizProcessDefinitionItemIncludeItemDto> listIncludeItemDataByItemCode(String id, String itemCode) {
        BizItemDefinitionEntity itemDefinitionEntity = bizItemDefinitionService.getById(id);
        List<ItemIncludeItem> includeItems = bizItemDefinitionService.listIncludeItemDataByItemCode(id, itemCode);
        if (CollectionUtils.isEmpty(includeItems)) {
            return Collections.emptyList();
        }
        List<BizProcessDefinitionItemIncludeItemDto> includeItemDtos = BeanUtils.copyCollection(includeItems, BizProcessDefinitionItemIncludeItemDto.class);
        List<String> itemCodes = Lists.newArrayList();
        includeItemDtos.forEach(item -> itemCodes.add(item.getItemCode()));

        List<ItemData> itemDatas = bizItemDefinitionService.listItemDataByBusinessIdAndItemCode(itemDefinitionEntity.getBusinessId(),
                itemCodes.toArray(new String[0]));
        Map<String, ItemData> itemDataMap = ConvertUtils.convertElementToMap(itemDatas, "itemCode");
        includeItemDtos.forEach(item -> {
            ItemData itemData = itemDataMap.get(item.getItemCode());
            if (itemData == null) {
                return;
            }
            item.setItemDefName(itemData.getItemDefName());
            item.setItemDefId(itemData.getItemDefId());
            item.setItemType(itemData.getItemType());
        });
        return includeItemDtos;
    }

    /**
     * 根据事项编码获取事项办理时限配置
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemTimeLimit> listTimeLimitDataByItemCode(String id, String itemCode) {
        return bizItemDefinitionService.listTimeLimitDataByItemCode(id, itemCode);
    }

    /**
     * 根据事项编码获取事项材料配置
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemMaterial> listMaterialDataByItemCode(String id, String itemCode) {
        return bizItemDefinitionService.listMaterialDataByItemCode(id, itemCode);
    }

    /**
     * 业务事项定义是否被使用
     *
     * @param uuids
     * @return
     */
    private boolean isUsed(List<String> uuids) {
        return bizProcessItemInstanceService.countByItemDefUuids(uuids) > 0;
    }

}
