/*
 * @(#)10/14/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;

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
 * 10/14/22.1	zhulh		10/14/22		Create
 * </pre>
 * @date 10/14/22
 */
public class ItemDataQuerySqlHelper {
    private BizItemDefinitionEntity itemDefinitionEntity;

    private List<String> itemCodes;

    // 事项表名
    private String tableName;

    // 材料表名
    private String materialTableName;

    // 工作办理时限表名
    private String timeLimitTableName;

    // 包含事项从表
    private String includeItemTableName;

    // 互斥事项从表
    private String mutexItemTableName;

    // 关联事项从表
    private String relateItemTableName;

    public ItemDataQuerySqlHelper(BizItemDefinitionEntity itemDefinitionEntity, List<String> itemCodes) {
        this.itemDefinitionEntity = itemDefinitionEntity;
        this.itemCodes = itemCodes;

        parse();
    }

    private void parse() {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        String formId = itemDefinitionEntity.getFormId();
        String materialSubformId = itemDefinitionEntity.getMaterialSubformId();
        String timeLimitSubformId = itemDefinitionEntity.getTimeLimitSubformId();
        String includeItemSubformId = itemDefinitionEntity.getIncludeItemSubformId();
        String mutexItemSubformId = itemDefinitionEntity.getMutexItemSubformId();
        String relateItemSubformId = itemDefinitionEntity.getRelateItemSubformId();
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(formId);
        DyFormFormDefinition materialFormDefinition = dyFormFacade.getFormDefinitionById(materialSubformId);
        DyFormFormDefinition timeLimitFormDefinition = dyFormFacade.getFormDefinitionById(timeLimitSubformId);
        DyFormFormDefinition includeItemFormDefinition = dyFormFacade.getFormDefinitionById(includeItemSubformId);
        DyFormFormDefinition mutexItemFormDefinition = dyFormFacade.getFormDefinitionById(mutexItemSubformId);
        DyFormFormDefinition relateItemFormDefinition = dyFormFacade.getFormDefinitionById(relateItemSubformId);
        this.tableName = dyFormFormDefinition.getTableName();
        this.materialTableName = materialFormDefinition != null ? materialFormDefinition.getTableName() : StringUtils.EMPTY;
        this.timeLimitTableName = timeLimitFormDefinition != null ? timeLimitFormDefinition.getTableName() : StringUtils.EMPTY;
        this.includeItemTableName = includeItemFormDefinition != null ? includeItemFormDefinition.getTableName() : StringUtils.EMPTY;
        this.mutexItemTableName = mutexItemFormDefinition != null ? mutexItemFormDefinition.getTableName() : StringUtils.EMPTY;
        this.relateItemTableName = relateItemFormDefinition != null ? relateItemFormDefinition.getTableName() : StringUtils.EMPTY;
    }

    /**
     * 获取查询事项信息的SQL信息
     *
     * @param itemCode
     * @return
     */
    public ItemDataSqlInfo getQueryItemInfoSqlInfoByItemCode(String itemCode) {
        String itemNameField = itemDefinitionEntity.getItemNameField();
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        if (StringUtils.isBlank(itemNameField) || StringUtils.isBlank(itemCodeField)) {
            return null;
        }
        String sql = String.format("select uuid as item_uuid, %s as item_name, %s as item_code from %s t where t.%s = :itemCode",
                itemNameField, itemCodeField, tableName, itemCodeField);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    /**
     * 获取查询事项材料的SQL信息
     *
     * @param itemCode
     * @return
     */
    public ItemDataSqlInfo getQueryItemMaterialSqlInfoByItemCode(String itemCode) {
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        String materialNameField = itemDefinitionEntity.getMaterialNameField();
        String materialCodeField = itemDefinitionEntity.getMaterialCodeField();
        String materialRequiredField = itemDefinitionEntity.getMaterialRequiredField();
        if (StringUtils.isBlank(materialRequiredField)) {
            materialRequiredField = "\'0\'";
        }
        if (StringUtils.isBlank(itemCodeField) || StringUtils.isBlank(materialNameField) || StringUtils.isBlank(materialCodeField)) {
            return null;
        }
        String sql = String.format("select %s as material_name , %s as material_code, '%s' as material_code_field, %s as material_required from %s t1 " +
                        "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                        "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                        "order by t2.sort_order asc",
                materialNameField, materialCodeField, materialCodeField, materialRequiredField, materialTableName, materialTableName, tableName, itemCodeField);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    /**
     * 获取查询事项办理时限的SQL信息
     *
     * @param itemCode
     * @return
     */
    public ItemDataSqlInfo getQueryItemTimeLimitSqlInfoByItemCode(String itemCode) {
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        String timeLimitField = itemDefinitionEntity.getTimeLimitField();
        if (StringUtils.isBlank(itemCodeField) || StringUtils.isBlank(timeLimitField)) {
            return null;
        }
        String sql = String.format("select %s as time_limit from %s t1 " +
                        "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                        "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                        "order by t2.sort_order asc",
                timeLimitField, timeLimitTableName, timeLimitTableName, tableName, itemCodeField);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    public ItemDataSqlInfo getQueryItemIncludeItemSqlInfoByItemCode(String itemCode) {
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        String includeItemNameField = itemDefinitionEntity.getIncludeItemNameField();
        String includeItemCodeField = itemDefinitionEntity.getIncludeItemCodeField();
        String frontItemCodeField = itemDefinitionEntity.getFrontItemCodeField();
        if (StringUtils.isBlank(itemCodeField) || StringUtils.isBlank(includeItemNameField) || StringUtils.isBlank(includeItemCodeField)) {
            return null;
        }
        String sql = null;
        if (StringUtils.isNotBlank(frontItemCodeField)) {
            sql = String.format("select %s as item_name, %s as item_code, %s as front_item_code from %s t1 " +
                            "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                            "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                            "order by t2.sort_order asc",
                    includeItemNameField, includeItemCodeField, frontItemCodeField, includeItemTableName, includeItemTableName, tableName, itemCodeField);
        } else {
            sql = String.format("select %s as item_name, %s as item_code from %s t1 " +
                            "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                            "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                            "order by t2.sort_order asc",
                    includeItemNameField, includeItemCodeField, includeItemTableName, includeItemTableName, tableName, itemCodeField);
        }

        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    public ItemDataSqlInfo getQueryItemMutexItemSqlInfoByItemCode(String itemCode) {
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        String mutexItemCodeField = itemDefinitionEntity.getMutexItemCodeField();
        if (StringUtils.isBlank(itemCodeField) || StringUtils.isBlank(mutexItemCodeField)) {
            return null;
        }
        String sql = String.format("select %s as mutex_item_code from %s t1 " +
                        "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                        "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                        "order by t2.sort_order asc",
                mutexItemCodeField, mutexItemTableName, mutexItemTableName, tableName, itemCodeField);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    public ItemDataSqlInfo getQueryItemRelateItemSqlInfoByItemCode(String itemCode) {
        String itemCodeField = itemDefinitionEntity.getItemCodeField();
        String relateItemCodeField = itemDefinitionEntity.getRelateItemCodeField();
        if (StringUtils.isBlank(itemCodeField) || StringUtils.isBlank(relateItemCodeField)) {
            return null;
        }
        String sql = String.format("select %s as relate_item_code from %s t1 " +
                        "inner join %s_rl t2 on t1.uuid = t2.data_uuid " +
                        "where t2.mainform_data_uuid in(select uuid as item_uuid from %s t where t.%s = :itemCode) " +
                        "order by t2.sort_order asc",
                relateItemCodeField, relateItemTableName, relateItemTableName, tableName, itemCodeField);
        Map<String, Object> values = Maps.newHashMap();
        values.put("itemCode", itemCode);
        return new ItemDataSqlInfo(sql, values);
    }

    public static class ItemDataSqlInfo {
        private String sql;
        private Map<String, Object> values;

        public ItemDataSqlInfo(String sql, Map<String, Object> values) {
            this.sql = sql;
            this.values = values;
        }

        /**
         * @return the sql
         */
        public String getSql() {
            return sql;
        }


        /**
         * @return the values
         */
        public Map<String, Object> getValues() {
            return values;
        }

    }

}
