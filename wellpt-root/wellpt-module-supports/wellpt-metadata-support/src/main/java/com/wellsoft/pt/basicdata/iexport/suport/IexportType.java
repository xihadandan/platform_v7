/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.util.BUtils;

import java.util.HashMap;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class IexportType {
    // 1.1、资源
    public static final String Resource = "resource";

    public static final String ResourceParent = "resourceParent";
    // 1.2、权限
    public static final String Privilege = "privilege";
    public static final String PrivilegeResource = "privilegeResource";
    // 1.3、角色
    public static final String Role = "role";

    // 2.1、数据字典
    public static final String DataDictionary = "dataDictionary";
    public static final String DataDictionaryParent = "dataDictionaryParent";

    public static final String CdDataDictionary = "cdDataDictionary";
    public static final String CdDataDictionaryCategory = "cdDataDictionaryCategory";
    public static final String CdDataDictionaryItem = "cdDataDictionaryItem";
    public static final String CdDataDictionaryItemAttribute = "cdDataDictionaryItemAttribute";

    // 3.1、 打印模板
    public static final String PrintTemplate = "printTemplate";
    public static final String PrintContents = "printContents";

    // 4.1、 流水号
    public static final String SerialNumber = "serialNumber";
    public static final String SnSerialNumberDefinition = "snSerialNumberDefinition";
    public static final String SnSerialNumberCategory = "snSerialNumberCategory";

    // 流程计时器配置
    public static final String TsTimerConfig = "tsTimerConfig";

    // 5.1、 系统表结构
    public static final String SystemTable = "systemTable";

    // 6.1、 界面
    public static final String CmsPage = "cmsPage";
    // 6.2、 导航
    public static final String CmsCategory = "cmsCategory";

    public static final String CmsCategoryParent = "cmsCategoryParent";
    // 6.3、 页面元素
    public static final String Module = "cmsModule";

    // 7.1、 数据源
    public static final String DataSourceDefinition = "dataSourceDefinition";

    // 7.2、 数据源列
    public static final String DataSourceColumn = "dataSourceColumn";
    // 7.3 、外部数据源
    public static final String DataSourceProfile = "dataSourceProfile";
    // 7.4 、数据仓库
    public static final String DataStoreDefinition = "dataStoreDefinition";

    // 8.1、 表单定义
    public static final String DyFormDefinition = "dyFormDefinition";
    // 8.2 、数据表单显示单据
    public static final String DyFormDisplayModel = "dyFormDisplayModel";

    // 8.3、多态 表单
    public static final String FormDefinition = "formDefinition";

    // 9.1、消息模板
    public static final String MessageTemplate = "messageTemplate";

    // 9.2、消息模板分类
    public static final String MessageClassify = "messageClassify";

    // 10.1、用户表
    public static final String User = "user";
    // 10.2、部门表
    public static final String Department = "department";
    public static final String DepartmentParent = "departmentParent";

    // 10.3、职务表
    public static final String Duty = "duty";
    // 10.4、职位表
    public static final String Job = "job";
    // 10.5、群组
    public static final String Group = "group";

    public static final String Organization = "organization";

    public static final String OrgGroup = "orgGroup";

    public static final String OrgElement = "orgElement";

    public static final String Unit = "unit";

    // 11.1、任务调度配置
    public static final String JobDetails = "jobDetails";

    // 11.2、视图定义表
    public static final String ViewDefinitionNew = "viewDefinitionNew";

    // 12.1、流程定义表
    public static final String FlowDefinition = "flowDefinition";
    // 12.2、流程分类
    public static final String FlowCategory = "flowCategory";
    // 12.3、信息格式
    public static final String FlowFormat = "flowFormat";
    // 12.4、流程定义XML
    public static final String FlowSchema = "flowSchema";
    // 12.5、流程业务定义
    public static final String FlowBusinessDefinition = "wfFlowBusinessDefinition";
    public static final String FLowOpinionRule = "wfOpinionRule";

    // 13.1 产品
    public static final String AppProduct = "appProduct";
    // 13.2 产品信息集成
    public static final String AppProductIntegration = "appProductIntegration";
    public static final String AppCategory = "appCategory";


    public static final String AppProductIntegrationParent = "appProductIntegrationParent";
    // 13.3 系统
    public static final String AppSystem = "appSystem";
    public static final String AppSystemLoginPageDef = "appSystemLoginPageDef";

    // 13.4 模块
    public static final String AppModule = "appModule";
    public static final String AppModuleResGroup = "appModuleResGroup";

    // 13.5 应用
    public static final String AppApplication = "appApplication";
    // 13.6 功能
    public static final String AppFunction = "appFunction";
    public static final String AppFunctionParent = "appFunctionParent";
    // 13.7 页面
    public static final String AppPageDefinition = "appPageDefinition";
    // 13.7 页面引用资源
    public static final String AppPageResource = "appPageResource";
    // 13.8 页面组件
    public static final String AppWidgetDefinition = "appWidgetDefinition";
    public static final String AppUserWidgetDef = "appUserWidgetDef";

    public static final String AppProdVersion = "appProdVersion";
    public static final String AppProdVersionSetting = "appProdVersionSetting";
    public static final String AppProdVersionLogin = "appProdVersionLogin";
    public static final String AppProdVersionParam = "appProdVersionParam";

    // 13.9 主题定义
    public static final String AppThemeDefinition = "appThemeDefinition";
    public static final String ThemePack = "themePack";
    public static final String ThemeSpecification = "themeSpecification";

    // 13.10 主题定义JSON
    public static final String AppThemeDefinitionJson = "appThemeDefinitionJson";

    // 14.1 数据导入规则
    public static final String ExcelImportRule = "excelImportRule";
    // 14.2 数据导出规则
    public static final String ExcelExportDefinition = "excelExportDefinition";

    // 15.1 ureport报表导出
    public static final String UreportFileRepository = "ureportFileRepository";
    // 16.1 单据转换规则导出
    public static final String BotRuleConf = "botRuleConf";

    public static final String DataModel = "dataModel";

    public static final String DataExchangeImpExpConf = "dataExchangeImpExpConf";

    public static final String DataIntegrationConfImpExp = "dataIntegrationConfImpExp";

    public static final String BotRuleObjRela = "botRuleObjRela";

    // 17.1 脚本定义
    public static final String CdScriptDefinition = "cdScriptDefinition";

    // 18.1 数据管理文件夹
    public static final String DmsFolder = "dmsFolder";
    // 18.2 数据管理文件夹操作权限
    public static final String DmsRole = "dmsRole";
    // 18.3 数据管理数据权限定义
    public static final String DmsDataPermissionDefinition = "dmsDataPermissionDefinition";

    public static final String ApiSysConfig = "apiOutSystemConfig";

    // 19、业务流程管理
    // 19.1 业务分类
    public static final String BizCategory = "bizCategory";
    // 19.2 业务
    public static final String BizBusiness = "bizBusiness";
    // 19.3 业务标签
    public static final String BizTag = "bizTag";
    // 19.4 事项定义
    public static final String BizItemDefinition = "bizItemDefinition";
    // 19.5 业务流程定义
    public static final String BizProcessDefinition = "bizProcessDefinition";
    // 19.6 业务流程装配
    public static final String BizProcessAssemble = "bizProcessAssemble";

    // 20. 文件
    public static final String LogicFileInfo = "logicFileInfo";

    public static final String CdImgCategory = "cdImgCategory";


    public static final String ApiLink = "apiLink";

    public static final String ApiOperation = "apiOperation";

    // error
    public static final String ErrorData = "errorData";

    // 外部导出需依赖上级对象的类型
    public static Map<String, String> dependencyParentTypes = new HashMap<String, String>();

    static {
        dependencyParentTypes.put(Resource, ResourceParent);
        dependencyParentTypes.put(DataDictionary, DataDictionaryParent);
        dependencyParentTypes.put(CmsCategory, CmsCategoryParent);
        dependencyParentTypes.put(Department, DepartmentParent);
        dependencyParentTypes.put(DyFormDefinition, FormDefinition);
        dependencyParentTypes.put(DyFormDisplayModel, FormDefinition);
    }

    /**
     * @param iexportType
     * @return
     */
    public static boolean hasDependencyParentType(String iexportType) {
        return dependencyParentTypes.containsKey(iexportType);
    }

    /**
     * @param iexportType
     * @return
     */
    public static String getDependencyParentType(String iexportType) {
        return dependencyParentTypes.get(iexportType);
    }

    /**
     * @param type
     * @return
     */
    public static String exportType2EntityName(String type) {
        String entityName = type;
        if (entityName.endsWith("Parent")) {
            entityName = entityName.replace("Parent", "");
        }
        entityName = BUtils.capitalize(entityName);
        if ("DataStoreDefinition".equals(entityName)) {
            entityName = "CdDataStoreDefinition";
        } else if ("DmsFolder".equals(entityName)) {
            entityName = "DmsFolderEntity";
        } else if ("BotRuleConf".equals(entityName)) {
            entityName = "BotRuleConfEntity";
        }
        return entityName;
    }

}
