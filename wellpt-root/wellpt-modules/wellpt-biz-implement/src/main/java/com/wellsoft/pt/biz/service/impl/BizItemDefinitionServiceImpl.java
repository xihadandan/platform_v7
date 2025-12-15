/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.biz.dao.BizItemDefinitionDao;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.query.ItemIncludeItemQueryItem;
import com.wellsoft.pt.biz.query.ItemInfoQueryItem;
import com.wellsoft.pt.biz.query.ItemMaterialQueryItem;
import com.wellsoft.pt.biz.query.ItemTimeLimitQueryItem;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.support.*;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Service
public class BizItemDefinitionServiceImpl extends AbstractJpaServiceImpl<BizItemDefinitionEntity, BizItemDefinitionDao, String> implements BizItemDefinitionService {

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 根据业务ID列表获取业务流程定义数量
     *
     * @param businessIds
     * @return
     */
    @Override
    public Long countByBusinessIds(List<String> businessIds) {
        String hql = "from BizItemDefinitionEntity t where t.businessId in(:businessIds)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("businessIds", businessIds);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * 根据业务流程定义UUID，获取相同业务的事项定义列表
     *
     * @param processDefUuid
     * @return
     */
    @Override
    public List<BizItemDefinitionEntity> listByProcessDefUuid(String processDefUuid) {
        String hql = "from BizItemDefinitionEntity t1 where t1.businessId in(select t2.businessId from " +
                "BizProcessDefinitionEntity t2 where t2.uuid = :processDefUuid)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processDefUuid", processDefUuid);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * 查询事项数据
     *
     * @param processDefUuid
     * @param values
     * @param pagingInfo
     * @return
     */
    @Override
    public List<QueryItem> queryItemData(String processDefUuid, Map<String, Object> values, PagingInfo pagingInfo) {
        List<BizItemDefinitionEntity> bizItemDefinitionEntities = listByProcessDefUuid(processDefUuid);
        if (CollectionUtils.isEmpty(bizItemDefinitionEntities)) {
            return Collections.emptyList();
        }
        String sql = generateItemDataQuerySql(bizItemDefinitionEntities);
        Map<String, Object> params = Maps.newHashMap();
        if (values != null) {
            params.putAll(values);
        }
        params.put("sql", sql);
        return this.dao.listQueryItemByNameSQLQuery("bizItemDataQuery", params, pagingInfo);
    }

    /**
     * 生成查询事项数据的sql
     *
     * @param bizItemDefinitionEntities
     * @return
     */
    private String generateItemDataQuerySql(List<BizItemDefinitionEntity> bizItemDefinitionEntities) {
        StringBuilder sqlBuilder = new StringBuilder();
        Iterator<BizItemDefinitionEntity> it = bizItemDefinitionEntities.iterator();
        while (it.hasNext()) {
            BizItemDefinitionEntity entity = it.next();
            String formId = entity.getFormId();
            if (StringUtils.isBlank(formId)) {
                continue;
            }
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(formId);
            String tableName = dyFormFormDefinition.getTableName();
            String itemNameField = entity.getItemNameField();
            String itemCodeField = entity.getItemCodeField();
            String itemType = entity.getType();
            String itemDefName = entity.getName();
            String itemDefId = entity.getId();
            String itemDefUuid = entity.getUuid();
            if (sqlBuilder.length() > 0) {
                sqlBuilder.append(" union all ");
            }
            sqlBuilder.append(String.format("select uuid as uuid, uuid as item_uuid, %s as item_name, %s as item_code, " +
                            "'%s' as item_type, '%s' as item_def_name, '%s' as item_def_id, '%s' as item_def_uuid from %s",
                    itemNameField, itemCodeField, itemType, itemDefName, itemDefId, itemDefUuid, tableName));
        }
        return sqlBuilder.toString();
    }

    /**
     * 根据事项定义ID获取事项定义
     *
     * @param id
     * @return
     */
    @Override
    public BizItemDefinitionEntity getById(String id) {
        Assert.hasText(id, "事项定义ID不能为空！");

        List<BizItemDefinitionEntity> entities = this.dao.listByFieldEqValue("id", id);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.get(0);
    }

    /**
     * 根据业务ID获取事项定义
     *
     * @param businessId
     * @return
     */
    @Override
    public List<BizItemDefinitionEntity> listByBusinessId(String businessId) {
        Assert.hasText(businessId, "业务ID不能为空！");

        String hql = "from BizItemDefinitionEntity t1 where t1.businessId = :businessId order by t1.type asc, t1.code asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("businessId", businessId);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasText(id, "事项定义ID不能为空！");

        BizItemDefinitionEntity entity = new BizItemDefinitionEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity);
    }

    @Override
    public long countByEntity(BizItemDefinitionEntity entity) {
        return this.dao.countByEntity(entity);
    }

    /**
     * @param processDefUuid
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemData> listItemDataByProcessDefUuidAndItemCode(String processDefUuid, String... itemCode) {
        Assert.hasText(processDefUuid, "业务流程UUID不能为空！");
        Assert.notEmpty(itemCode, "事项编码不能为空！");

        List<BizItemDefinitionEntity> bizItemDefinitionEntities = listByProcessDefUuid(processDefUuid);
        String sql = generateItemDataQuerySql(bizItemDefinitionEntities);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        values.put("sql", "select * from (" + sql + ") t where t.item_code in(:itemCode)");
        List<ItemInfoQueryItem> itemInfoQueryItems = this.dao.listItemByNameSQLQuery("bizItemDataQuery",
                ItemInfoQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));
        return BeanUtils.copyCollection(itemInfoQueryItems, ItemData.class);
    }

    /**
     * @param businessId
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemData> listItemDataByBusinessIdAndItemCode(String businessId, String... itemCode) {
        Assert.hasText(businessId, "业务ID不能为空！");
        Assert.notEmpty(itemCode, "事项编码不能为空！");

        List<BizItemDefinitionEntity> bizItemDefinitionEntities = listByBusinessId(businessId);
        String sql = generateItemDataQuerySql(bizItemDefinitionEntities);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        values.put("sql", "select * from (" + sql + ") t where t.item_code in(:itemCode)");
        List<ItemInfoQueryItem> itemInfoQueryItems = this.dao.listItemByNameSQLQuery("bizItemDataQuery",
                ItemInfoQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));
        return BeanUtils.copyCollection(itemInfoQueryItems, ItemData.class);
    }

    /**
     * 根据事项编码获取事项办理材料
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemMaterial> listMaterialDataByItemCode(String id, String itemCode) {
        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
        ItemDataQuerySqlHelper helper = new ItemDataQuerySqlHelper(itemDefinitionEntity, Lists.<String>newArrayList(itemCode));
        return listMaterialData(itemCode, helper);
    }

    private List<ItemMaterial> listMaterialData(String itemCode, ItemDataQuerySqlHelper helper) {
        // 办理时限
        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemMaterialSqlInfoByItemCode(itemCode);
        if (sqlInfo == null) {
            return Collections.emptyList();
        }
        Map<String, Object> values = sqlInfo.getValues();
        values.put("sql", sqlInfo.getSql());
        List<ItemMaterialQueryItem> itemMaterialQueryItems = this.dao.listItemByNameSQLQuery("bizItemLimitTimeQuery",
                ItemMaterialQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));

        return BeanUtils.copyCollection(itemMaterialQueryItems, ItemMaterial.class);
    }

    /**
     * 根据事项编码获取事项办理时限
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemTimeLimit> listTimeLimitDataByItemCode(String id, String itemCode) {
        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
        ItemDataQuerySqlHelper helper = new ItemDataQuerySqlHelper(itemDefinitionEntity, Lists.<String>newArrayList(itemCode));
        return listTimeLimitData(itemCode, helper);
    }

    private List<ItemTimeLimit> listTimeLimitData(String itemCode, ItemDataQuerySqlHelper helper) {
        // 办理时限
        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemTimeLimitSqlInfoByItemCode(itemCode);
        if (sqlInfo == null) {
            return Collections.emptyList();
        }
        Map<String, Object> values = sqlInfo.getValues();
        values.put("sql", sqlInfo.getSql());
        List<ItemTimeLimitQueryItem> itemLimitTimeQueryItems = this.dao.listItemByNameSQLQuery("bizItemLimitTimeQuery",
                ItemTimeLimitQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));

        return BeanUtils.copyCollection(itemLimitTimeQueryItems, ItemTimeLimit.class);
    }


    /**
     * 根据事项编码获取事项包含的事项
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<ItemIncludeItem> listIncludeItemDataByItemCode(String id, String itemCode) {
        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
        ItemDataQuerySqlHelper helper = new ItemDataQuerySqlHelper(itemDefinitionEntity, Lists.<String>newArrayList(itemCode));
        return listIncludeItemData(itemCode, helper);
    }

    private List<ItemIncludeItem> listIncludeItemData(String itemCode, ItemDataQuerySqlHelper helper) {
        // 包含事项从表
        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemIncludeItemSqlInfoByItemCode(itemCode);
        if (sqlInfo == null) {
            return Collections.emptyList();
        }
        Map<String, Object> values = sqlInfo.getValues();
        values.put("sql", sqlInfo.getSql());
        List<ItemIncludeItemQueryItem> itemIncludeItemQueryItems = this.dao.listItemByNameSQLQuery("bizItemIncludeItemQuery",
                ItemIncludeItemQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));

        return BeanUtils.copyCollection(itemIncludeItemQueryItems, ItemIncludeItem.class);
    }

    private ItemInfoQueryItem getItemInfoByItemCode(String itemCode, ItemDataQuerySqlHelper helper) {
        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemInfoSqlInfoByItemCode(itemCode);
        if (sqlInfo == null) {
            return null;
        }
        Map<String, Object> values = sqlInfo.getValues();
        values.put("sql", sqlInfo.getSql());
        List<ItemInfoQueryItem> itemInfoQueryItems = this.dao.listItemByNameSQLQuery("bizItemInfoQuery",
                ItemInfoQueryItem.class, values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(itemInfoQueryItems)) {
            return itemInfoQueryItems.get(0);
        }
        return null;
    }

    /**
     * 获取事项表单主表数据
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public Map<String, Object> getItemFormDataOfMainform(String id, String itemCode) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(itemCode)) {
            return null;
        }

        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
        if (itemDefinitionEntity == null) {
            return null;
        }

        String itemFormId = itemDefinitionEntity.getFormId();
        String itemCodeField = itemDefinitionEntity.getItemCodeField();

        String formUuid = dyFormFacade.getFormUuidById(itemFormId);
        String dataUuid = queryItemDataUuid(formUuid, itemCode, itemCodeField);

        if (StringUtils.isBlank(dataUuid)) {
            return null;
        }

        return dyFormFacade.getFormDataOfMainform(formUuid, dataUuid);
    }

    /**
     * 获取事项表单材料
     *
     * @param id
     * @param itemCode
     * @return
     */
    @Override
    public List<DyFormData> getItemDyFormDataOfMaterials(String id, String itemCode) {
        List<DyFormData> materials = Lists.newArrayListWithCapacity(0);
        if (StringUtils.isBlank(id) || StringUtils.isBlank(itemCode)) {
            return materials;
        }

        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
        if (itemDefinitionEntity == null || StringUtils.isBlank(itemDefinitionEntity.getMaterialSubformId())) {
            return materials;
        }

        String itemFormId = itemDefinitionEntity.getFormId();
        String itemCodeField = itemDefinitionEntity.getItemCodeField();

        String formUuid = dyFormFacade.getFormUuidById(itemFormId);
        String dataUuid = queryItemDataUuid(formUuid, itemCode, itemCodeField);

        if (StringUtils.isBlank(dataUuid)) {
            return materials;
        }

        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        List<DyFormData> materialList = dyFormData.getDyformDatasByFormId(itemDefinitionEntity.getMaterialSubformId());

        return materialList != null ? materialList : materials;
    }

    /**
     * @param formUuid
     * @param itemCodeValue
     * @param itemCodeField
     * @return
     */
    private String queryItemDataUuid(String formUuid, String itemCodeValue, String itemCodeField) {
        String dataUuid = null;
        if (JpaEntity.UUID.equals(itemCodeField)) {
            dataUuid = itemCodeValue;
        } else {
            Map<String, Object> params = Maps.newHashMap();
            params.put(itemCodeField, itemCodeValue);
            // 根据业务主体ID查询对应的表单数据UUID
            List<String> dataUuids = dyFormFacade.queryUniqueForFields(formUuid, params, null);
            if (CollectionUtils.isNotEmpty(dataUuids)) {
                dataUuid = dataUuids.get(0);
            }
        }
        return dataUuid;
    }

    /**
     * 根据事项编码获取事项包含的互斥事项
     *
     * @param id
     * @param itemCode
     * @return
     */
//    @Override
//    public List<ItemMutexItem> listMutexItemDataByItemCode(String id, String itemCode) {
//        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
//        ItemDataQuerySqlHelper helper = new ItemDataQuerySqlHelper(itemDefinitionEntity, Lists.<String>newArrayList(itemCode));
//        return listMutexItemData(itemCode, helper);
//    }

//    private List<ItemMutexItem> listMutexItemData(String itemCode, ItemDataQuerySqlHelper helper) {
//        // 互斥事项从表
//        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemMutexItemSqlInfoByItemCode(itemCode);
//        if (sqlInfo == null) {
//            return Collections.emptyList();
//        }
//        Map<String, Object> values = sqlInfo.getValues();
//        values.put("sql", sqlInfo.getSql());
//        List<ItemMutexItemQueryItem> itemMutexItemQueryItems = this.dao.listItemByNameSQLQuery("bizItemMutexItemQuery",
//                ItemMutexItemQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));
//
//        return BeanUtils.copyCollection(itemMutexItemQueryItems, ItemMutexItem.class);
//    }

    /**
     * 根据事项编码获取事项包含的关联事项
     *
     * @param id
     * @param itemCode
     * @return
     */
//    @Override
//    public List<ItemRelateItem> listRelateItemDataByItemCode(String id, String itemCode) {
//        BizItemDefinitionEntity itemDefinitionEntity = getById(id);
//        ItemDataQuerySqlHelper helper = new ItemDataQuerySqlHelper(itemDefinitionEntity, Lists.<String>newArrayList(itemCode));
//        return listRelateItemData(itemCode, helper);
//    }

//    private List<ItemRelateItem> listRelateItemData(String itemCode, ItemDataQuerySqlHelper helper) {
//        // 关联事项从表
//        ItemDataQuerySqlHelper.ItemDataSqlInfo sqlInfo = helper.getQueryItemRelateItemSqlInfoByItemCode(itemCode);
//        if (sqlInfo == null) {
//            return Collections.emptyList();
//        }
//        Map<String, Object> values = sqlInfo.getValues();
//        values.put("sql", sqlInfo.getSql());
//        List<ItemRelateItemQueryItem> itemRelateItemQueryItems = this.dao.listItemByNameSQLQuery("bizItemRelateItemQuery",
//                ItemRelateItemQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE));
//
//        return BeanUtils.copyCollection(itemRelateItemQueryItems, ItemRelateItem.class);
//    }

}
