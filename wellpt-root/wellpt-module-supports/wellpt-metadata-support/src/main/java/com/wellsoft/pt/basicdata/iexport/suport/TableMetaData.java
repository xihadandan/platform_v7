/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 导入导出数据库表数据外键关系
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public class TableMetaData {
    private static final Map<String, String> tableMap = new HashMap<String, String>();
    private static final Map<String, String> tableNameMap = new HashMap<String, String>();
    private static final Map<String, String> pkMap = new HashMap<String, String>();
    // 表关联，定义某一类数据内部的表关系，可结合数据返回的依赖关系进行使用
    private static final List<TableRelation> tableRelations = new ArrayList<TableRelation>();

    static {
        // 表关联，定义某一类数据内部的表关系，可结合数据返回的依赖关系进行使用
        // 权限资源关系，结合权限返回的依赖对象使用
        // 1.1 、资源权限关系
        tableRelations.add(new TableRelation(IexportType.Resource, "AUDIT_RESOURCE", "AUDIT_PRIVILEGE_RESOURCE",
                "RESOURCE_UUID"));
        // 1.2 、权限资源关系
        tableRelations.add(new TableRelation(IexportType.Privilege, "AUDIT_PRIVILEGE", "AUDIT_PRIVILEGE_RESOURCE",
                "PRIVILEGE_UUID"));

        // 5.1、系统表结构
        tableRelations.add(new TableRelation(IexportType.SystemTable, "CD_SYSTEM_TABLE_ENTITY",
                "CD_SYSTEM_TABLE_ENTITY_ATTR", "SYSTEM_TABLE_UUID"));
        tableRelations.add(new TableRelation(IexportType.SystemTable, "CD_SYSTEM_TABLE_ENTITY",
                "CD_SYSTEM_TABLE_RELATIONSHIP", "SYSTEM_TABLE_UUID"));

        /*
         * // 6.1 、页面组件关系 tableRelations.add(new
         * TableRelation(IexportType.CmsPage, getTableName(CmsPage.class),
         * getTableName(Widget.class), "PAGE_UUID"));
         */

        // 10.1、用户表
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_GROUP_USER", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_JOB", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_ROLE", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_DEPARTMENT_USER_JOB", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_LEADER", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_MINOR_JOB", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_PRIVILEGE", "USER_UUID"));
        tableRelations.add(new TableRelation(IexportType.User, "ORG_USER", "ORG_USER_PROPERTY", "USER_UUID"));
        // 部门
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPARTMENT_USER_JOB",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPT_PRINCIPAL",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPARTMENT_PRINCIPAL",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPARTMENT_ROLE",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_GROUP_DEPARTMENT",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPARTMENT_FUNCTION",
                "DEPARTMENT_UUID"));
        tableRelations.add(new TableRelation(IexportType.Department, "ORG_DEPARTMENT", "ORG_DEPARTMENT_PRIVILEGE",
                "DEPARTMENT_UUID"));
        // 10.4、职位表
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_USER_JOB", "JOB_UUID"));
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_JOB_FUNCTION", "JOB_UUID"));
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_GROUP_JOB", "JOB_UUID"));
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_JOB_ROLE", "JOB_UUID"));
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_JOB_PRIVILEGE", "JOB_UUID"));
        tableRelations.add(new TableRelation(IexportType.Job, "ORG_JOB", "ORG_JOB_LEADER", "JOB_UUID"));
        // 10.5、群组
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_USER", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_ROLE", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_DEPARTMENT", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_PRIVILEGE", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_DUTY", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_JOB", "GROUP_UUID"));
        tableRelations.add(new TableRelation(IexportType.Group, "ORG_GROUP", "ORG_GROUP_NESTED_GROUP", "GROUP_UUID"));
        // 职务
        tableRelations.add(new TableRelation(IexportType.Duty, "ORG_DUTY", "ORG_GROUP_DUTY", "DUTY_UUID"));
        tableRelations.add(new TableRelation(IexportType.Duty, "ORG_DUTY", "ORG_DUTY_ROLE", "DUTY_UUID"));

        // 角色
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_USER_ROLE", "ROLE_UUID"));
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_DEPARTMENT_ROLE", "ROLE_UUID"));
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_USER_ROLE", "ROLE_UUID"));
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_JOB_ROLE", "ROLE_UUID"));
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_DUTY_ROLE", "ROLE_UUID"));

        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "ORG_GROUP_ROLE", "ROLE_UUID"));
        // 角色权限
        tableRelations.add(new TableRelation(IexportType.Role, "AUDIT_ROLE", "AUDIT_ROLE_PRIVILEGE", "ROLE_UUID"));
        // 视图表关系，处理内部所有关系
        // 12.1、列定义
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_DEFINITION",
                "VIEW_COLUMN_DEFINITION", "VIEW_DEF_UUID"));
        // 12.2、按钮
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_DEFINITION",
                "VIEW_VIEW_CUSTOM_BUTTON", "VIEW_DEF_UUID"));
        // 12.3、查询定义
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_DEFINITION",
                "VIEW_SELECT_DEFINITION", "VIEW_DEF_UUID"));
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_SELECT_DEFINITION",
                "VIEW_FIELD_SELECT", "SELECT_DEF_UUID"));
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_SELECT_DEFINITION",
                "VIEW_SELECT_CONDITION_TYPE", "SELECT_DEF_UUID"));
        // 12.4、分页定义
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_DEFINITION", "VIEW_PAGE_DEFINITION",
                "VIEW_DEF_UUID"));
        // 12.5、列样式
        tableRelations.add(new TableRelation(IexportType.ViewDefinitionNew, "VIEW_DEFINITION",
                "VIEW_COLUMNCSS_DEFINITION", "VIEW_DEF_UUID"));

        // 数据字典表关系
        // 13.1、其他属性
        tableRelations.add(new TableRelation(IexportType.DataDictionary, "CD_DATA_DICT", "CD_DATA_DICT_ATTR",
                "DATA_DICT_UUID"));
        tableRelations.add(new TableRelation(IexportType.DataDictionaryParent, "CD_DATA_DICT", "CD_DATA_DICT_ATTR",
                "DATA_DICT_UUID"));

        // 14.1 数据导入规则
        tableRelations.add(new TableRelation(IexportType.ExcelImportRule, "CD_EXCEL_IMPORT_RULE",
                "CD_EXCEL_COLUMN_DEFINITION", "EXCEL_IMPORT_RULE_UUID"));
        // 14.2 数据导出规则
        tableRelations.add(new TableRelation(IexportType.ExcelExportDefinition, "EXCEL_EXPORT_DEFINITION",
                "EXCEL_EXPORT_column_DEFINITION", "EXCEL_EXPORT_DEFINITION_UUID"));

        //  单据转换规则
        tableRelations.add(new TableRelation(IexportType.BotRuleConf, "BOT_RULE_CONF", "BOT_RULE_OBJ_MAPPING",
                "RULE_CONF_UUID"));
        tableRelations.add(new TableRelation(IexportType.BotRuleConf, "BOT_RULE_CONF", "BOT_RULE_OBJ_RELA",
                "RULE_CONF_UUID"));
        tableRelations.add(new TableRelation(IexportType.BotRuleObjRela, "BOT_RULE_OBJ_RELA",
                "BOT_RULE_OBJ_RELA_MAPPING", "RULE_OBJ_RELA_UUID"));

        // 数据管理文件夹
        tableRelations.add(new TableRelation(IexportType.DmsFolder, "DMS_FOLDER", "DMS_FOLDER_CONFIGURATION",
                "FOLDER_UUID"));
        tableRelations.add(new TableRelation(IexportType.DmsFolder, "DMS_FOLDER", "DMS_OBJECT_IDENTITY",
                "OBJECT_ID_IDENTITY"));
        tableRelations.add(new TableRelation(IexportType.DmsFolder, "DMS_OBJECT_IDENTITY", "DMS_OBJECT_ASSIGN_ROLE",
                "OBJECT_IDENTITY_UUID"));
        tableRelations.add(new TableRelation(IexportType.DmsFolder, "DMS_OBJECT_ASSIGN_ROLE",
                "DMS_OBJECT_ASSIGN_ROLE_ITEM", "ASSIGN_ROLE_UUID"));
        // 数据管理文件操作权限
        tableRelations.add(new TableRelation(IexportType.DmsRole, "DMS_ROLE", "DMS_ROLE_ACTION", "ROLE_UUID"));

        // API对接系统配置
        tableRelations.add(new TableRelation(IexportType.ApiSysConfig, "API_OUT_SYSTEM_CONFIG",
                "API_OUT_SYSTEM_SERVICE_CONFIG", "SYSTEM_UUID"));
        tableRelations.add(new TableRelation(IexportType.ApiSysConfig, "API_OUT_SYSTEM_CONFIG", "API_AUTHORIZE_ITEM",
                "SYSTEM_UUID"));


        //数据交换配置
        tableRelations.add(new TableRelation(IexportType.DataIntegrationConfImpExp, "DI_CONFIG",
                "DX_IMP_EXP_ENDPOINT", "CONF_UUID"));
        tableRelations.add(new TableRelation(IexportType.DataIntegrationConfImpExp, "DI_CONFIG",
                "DI_DATA_PROCESSOR", "DI_CONFIG_UUID"));
        tableRelations.add(new TableRelation(IexportType.DataIntegrationConfImpExp, "DI_CONFIG",
                "DI_DATA_PRODUCER_ENDPOINT", "DI_CONFIG_UUID"));
        tableRelations.add(new TableRelation(IexportType.DataIntegrationConfImpExp, "DI_CONFIG",
                "DI_DATA_CONSUMER_ENDPOINT", "DI_CONFIG_UUID"));

        // 页面引用资源
        tableRelations.add(new TableRelation(IexportType.AppPageDefinition, "APP_PAGE_DEFINITION", "APP_PAGE_RESOURCE",
                "APP_PAGE_UUID"));
    }

    static {
        // 主键映射表，默认为UUID，多个以分号隔开

        // 1.1 、权限资源表主键
        pkMap.put("AUDIT_PRIVILEGE_RESOURCE", "PRIVILEGE_UUID;RESOURCE_UUID");
        pkMap.put("AUDIT_ROLE_PRIVILEGE", "ROLE_UUID;PRIVILEGE_UUID");
        // 10.1、用户群组关系
        pkMap.put("ORG_GROUP_USER", "USER_UUID;GROUP_UUID");
        pkMap.put("ORG_USER_ROLE", "USER_UUID;ROLE_UUID");
        pkMap.put("ORG_USER_PRIVILEGE", "USER_UUID;PRIVILEGE_UUID");
        pkMap.put("ORG_USER_JOB", "USER_UUID;JOB_UUID");
        pkMap.put("ORG_USER_LEADER", "USER_UUID;LEADER_UUID");
        pkMap.put("ORG_DEPARTMENT_USER_JOB", "USER_UUID;DEPARTMENT_UUID");
        pkMap.put("ORG_USER_MINOR_JOB", "USER_UUID;DEPUTY_UUID");
        pkMap.put("ORG_USER_PROPERTY", "USER_UUID;UUID");
        // JOB
        pkMap.put("ORG_JOB_FUNCTION", "JOB_UUID;FUNCTION_UUID");
        pkMap.put("ORG_GROUP_JOB", "JOB_UUID;GROUP_UUID");
        pkMap.put("ORG_JOB_ROLE", "JOB_UUID;ROLE_UUID");
        pkMap.put("ORG_JOB_PRIVILEGE", "JOB_UUID;PRIVILEGE_UUID");
        pkMap.put("ORG_JOB_LEADER", "JOB_UUID;LEADER_UUID");
        // GROUP
        pkMap.put("ORG_GROUP_ROLE", "GROUP_UUID;ROLE_UUID");
        pkMap.put("ORG_GROUP_DEPARTMENT", "GROUP_UUID;DEPARTMENT_UUID");
        pkMap.put("ORG_GROUP_JOB", "GROUP_UUID;JOB_UUID");
        pkMap.put("ORG_GROUP_DUTY", "GROUP_UUID;DUTY_UUID");
        pkMap.put("ORG_GROUP_PRIVILEGE", "GROUP_UUID;PRIVILEGE_UUID");
        pkMap.put("ORG_GROUP_NESTED_GROUP", "GROUP_UUID;NESTED_GROUP_UUID");
        // DUTY
        pkMap.put("ORG_DUTY_ROLE", "DUTY_UUID;ROLE_UUID");
        // DEPARTMENT
        pkMap.put("ORG_DEPT_PRINCIPAL", "DEPARTMENT_UUID;ORG_ID");
        pkMap.put("ORG_DEPARTMENT_PRINCIPAL", "DEPARTMENT_UUID;USER_UUID");
        pkMap.put("ORG_DEPARTMENT_ROLE", "DEPARTMENT_UUID;ROLE_UUID");
        pkMap.put("ORG_DEPARTMENT_PRIVILEGE", "DEPARTMENT_UUID;PRIVILEGE_UUID");
        pkMap.put("ORG_DEPARTMENT_FUNCTION", "DEPARTMENT_UUID;FUNCTION_UUID");
    }

    /**
     * 注册外部实现表
     *
     * @param type
     * @param tableName
     */
    public static void register(String type, String tableName) {
        tableMap.put(type, tableName);
    }

    /**
     * 获取名称集合 如何描述该方法
     *
     * @return
     */
    public static void register(String type, String tableName, Class<? extends Serializable> idEntity) {
        tableMap.put(type, getTableName(idEntity));
        tableNameMap.put(type, tableName);
    }

    /**
     * 获取表集合 如何描述该方法
     *
     * @return
     */
    public static Map<String, String> getTableMap() {
        return tableMap;
    }

    /**
     * 获取名称集合 如何描述该方法
     *
     * @return
     */
    public static Map<String, String> getTableNameMap() {
        return tableNameMap;
    }

    /**
     * @param cls
     * @return
     */
    private static String getTableName(Class<?> cls) {
        Table table = cls.getAnnotation(Table.class);
        return StringUtils.upperCase(table.name());
    }

    /**
     * @param type
     * @return
     */
    public static String getTableName(String type) {
        return tableMap.get(type);
    }

    /**
     * 根据主表名称，获取关联的从表
     *
     * @param tableName
     */
    public static List<TableRelation> getRelationTables(String group, String tableName) {
        List<TableRelation> list = new ArrayList<TableRelation>();
        for (TableRelation tableRelation : tableRelations) {
            if (tableRelation.getPrimaryTableName().equalsIgnoreCase(tableName)) {
                list.add(tableRelation);
            }
        }
        return list;
    }

    /**
     * 获取关联表对应关系
     *
     * @param slaveTableName
     * @param relationColumnName
     * @param name
     * @return
     */
    public static List<TableRelation> getTableRelations(String slaveTableName) {
        List<TableRelation> returnTableRelations = new ArrayList<TableRelation>();
        // 类型、表
        for (TableRelation tableRelation : tableRelations) {
            if (tableRelation.getSlaveTableName().equalsIgnoreCase(slaveTableName)) {
                returnTableRelations.add(tableRelation);
            }
        }
        return returnTableRelations;
    }

    /**
     * 获取关联表对应关系
     *
     * @param slaveTableName
     * @param relationColumnName
     * @param name
     * @return
     */
    public static List<TableRelation> getTableRelations(String slaveTableName, String columnName) {
        List<TableRelation> returnTableRelations = new ArrayList<TableRelation>();
        // 类型、表
        for (TableRelation tableRelation : tableRelations) {
            if (tableRelation.getSlaveTableName().equalsIgnoreCase(slaveTableName)
                    && tableRelation.getRelationColumnName().equalsIgnoreCase(columnName)) {
                returnTableRelations.add(tableRelation);
            }
        }
        return returnTableRelations;
    }

    public static String getPk(String tableName) {
        String pkName = pkMap.get(tableName);
        return StringUtils.isBlank(pkName) ? "UUID" : pkName;
    }

}
