/*
 * @(#)2016-09-13 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

/**
 * Description: 平台内置功能类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-13.1	zhulh		2016-09-13		Create
 * </pre>
 * @date 2016-09-13
 */
public interface AppFunctionType {
    // 1.1、资源
    public static final String Resource = IexportType.ResourceParent;
    // 1.2、权限
    public static final String Privilege = IexportType.Privilege;
    // 1.3、角色
    public static final String Role = IexportType.Role;

    // 2.1、数据字典
    public static final String DataDictionary = IexportType.DataDictionaryParent;
    public static final String CdDataDictionary = "cdDataDictionary";

    // 3.1、 打印模板
    public static final String PrintTemplate = IexportType.PrintTemplate;

    // 4.1、 流水号
    public static final String SerialNumber = IexportType.SerialNumber;
    public static final String SnSerialNumber = IexportType.SnSerialNumberDefinition;

    // 5.1、 系统表结构
    @Deprecated
    public static final String SystemTable = IexportType.SystemTable;
    // 5.2 数据导入规则
    public static final String ExcelImportRule = IexportType.ExcelImportRule;
    // 5.3 数据导出规则
    public static final String ExcelExportDefinition = IexportType.ExcelExportDefinition;

    // 6.1、 界面
    @Deprecated
    public static final String CmsPage = IexportType.CmsPage;
    // 6.2、 导航
    @Deprecated
    public static final String CmsCategory = IexportType.CmsCategoryParent;
    // 6.3、 页面元素
    @Deprecated
    public static final String CmsModule = IexportType.Module;

    // 7.1、 数据源
    @Deprecated
    public static final String DataSourceDefinition = IexportType.DataSourceDefinition;
    // 7.2、 数据仓库
    public static final String DataStoreDefinition = IexportType.DataStoreDefinition;

    // 8.1、 表单定义
    public static final String DyFormFormDefinition = IexportType.FormDefinition;

    // 9.1、消息模板
    public static final String MessageTemplate = IexportType.MessageTemplate;

    // 10.1、组织
    public static final String Group = IexportType.Group;

    // 11.1、任务调度配置
    public static final String JobDetails = IexportType.JobDetails;

    // 11.2、视图定义表
    @Deprecated
    public static final String ViewDefinitionNew = IexportType.ViewDefinitionNew;

    // 12.1、流程定义表
    public static final String FlowDefinition = IexportType.FlowDefinition;
    // 12.2、流程分类
    public static final String FlowCategory = IexportType.FlowCategory;
    // 12.3、信息格式
    public static final String FlowFormat = IexportType.FlowFormat;

    // 13.1 产品信息集成
    public static final String AppProductIntegration = IexportType.AppProductIntegration;
    // 13.2 页面定义
    public static final String AppPageDefinition = IexportType.AppPageDefinition;
    // 13.3 组件定义
    public static final String AppWidgetDefinition = IexportType.AppWidgetDefinition;
    // 13.4 组件功能元素
    public static final String AppWidgetFunctionElement = "AppWidgetFunctionElement";
    public static final String AppModule = IexportType.AppModule;

    // 14 平台功能
    // 14.1 JavaScript模块
    public static final String JavaScriptModule = "JavaScript";
    // 14.2 JavaScript模板
    public static final String JavaScriptTemplate = "JavaScriptTemplate";
    // 14.3 Web控制器
    public static final String Controller = "Controller";
    // 14.4 URL请求
    public static final String URL = "URL";
    // 14.5 菜单
    public static final String MENU = "MENU";
    // 14.6 按钮
    public static final String BUTTON = "BUTTON";
    // 14.7 门面服务方法
    public static final String FacedeService = "FacedeService";
    // 14.8 CSS模块
    public static final String CssFile = "CssFile";

    // 17.1 脚本定义
    public static final String CdScriptDefinition = IexportType.CdScriptDefinition;

    // 单据转换规则定义
    public static final String BotRuleConf = IexportType.BotRuleConf;

    // 数据交换定义
    public static final String DataExchangeImpExpConf = IexportType.DataExchangeImpExpConf;

    public static final String DataIntegrationConfImpExp = IexportType.DataIntegrationConfImpExp;

}
