/*
 * @(#)2014-2-23 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 行政审批常量
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-23.1	zhulh		2014-2-23		Create
 * </pre>
 * @date 2014-2-23
 */
public class XZSPConstants {
    /***************************** 行政审批业务类型ID开始 *****************************/
    // 业务类型ID
    public static final String BUSINESS_TYPE_ID = "XZSP";
    public static final String BUSINESS_TYPE_NAME = "行政审批";

    public static final String APP_SHORT_NAME = "建管系统";

    public static final String FLOW_DEF_ID_XZSP_TEMPLATE = "XZSP_TEMPLATE";
    public static final String FLOW_DEF_ID_XZSP_PARALLEL_TEMPLATE = "XZSP_PARALLEL_TEMPLATE";
    public static final String FLOW_DEF_ID_XZSP_KYPF_CSYJ = "XZSP_KYPF_CSYJ";
    public static final String FLOW_DEF_ID_XZSP_KYPF_JSZD = "XZSP_KYPF_JSZD";
    public static final String FLOW_DEF_ID_XZSP_SGXKJD_ZQYJ = "XZSP_SGXKJD_ZQYJ";
    public static final Map<String, String> flowDefMap = new HashMap<String, String>();
    /***************************** 行政审批自定义的参数开始 *****************************/
    // 行政审批子流程分发器
    public static final String CUSTOM_RT_DISPATCHER_FLOW_RESOLVER = "custom_rt_dispatcherFlowResolver";
    /***************************** 行政审批业务类型ID结束 *****************************/
    // 行政审批业务单位通讯录人员自定义解析类
    public static final String CUSTOM_RT_BIZ_ROLE_IDENTITY_RESOLVER = "custom_rt_bizRoleIdentityResolver";
    // 行政审批业务短信消息发送调度类
    public static final String CUSTOM_RT_MESSAGE_SENDER_DISPATCHER = "custom_rt_messageSenderDispatcher";
    // 表单动态按钮过滤器
    public static final String CUSTOM_RT_DYNAMIC_BUTTON_FILTER = "custom_rt_dynamic_button_filter";
    // 自定义事项流程模板
    public static final String CUSTOM_RT_FRONT_FLOWID = "custom_rt_frontFlowid";
    // 主事项UUID
    public static final String CUSTOM_RT_MATTERS_UUID = "custom_rt_mattersUuid";
    // 主事项CODE
    public static final String CUSTOM_RT_MATTERS_CODE = "custom_rt_mattersCode";
    // 并联事项UUID
    public static final String CUSTOM_RT_PARALLEL_MATTERS_UUID = "custom_rt_parallelMattersUuid";
    // 并联事项表单定义UUID
    public static final String CUSTOM_RT_PARALLEL_FORM_UUID = "custom_rt_parallelFormUuid";
    // 并联事项数据UUID
    public static final String CUSTOM_RT_PARALLEL_DATA_UUID = "custom_rt_parallelDataUuid";
    // 项目UUID
    public static final String CUSTOM_RT_PROJECT_UUID = "custom_rt_projectUuid";
    // 事项申请表
    public static final String CUSTOM_RT_APPLY_FORMUUID = "custom_rt_applyFormUuid";
    // 事项申请单数据
    public static final String CUSTOM_RT_APPLY_DATAUUID = "custom_rt_applyDataUuid";
    // 批文要素单（不直接保存表单数据）
    public static final String CUSTOM_RT_PWYS_FORMUUID = "custom_rt_approFormUuid";
    // 附加JAVASCRIPT脚本
    public static final String CUSTOM_RT_SCRIPT_URL = "custom_rt_script_url";
    // 是否为并联件
    public static final String CUSTOM_RT_IS_PARALLEL = "custom_rt_is_parallel";
    // 是否为市区联动并联件
    public static final String CUSTOM_RT_IS_SQLD_PARALLEL = "custom_rt_is_sqld_parallel";
    // 市区联动登记用户ID
    public static final String CUSTOM_RT_SQLD_DJ_USERID = "custom_rt_sqld_dj_userid";
    // 市区联动登记用户名称
    public static final String CUSTOM_RT_SQLD_DJ_USERNAME = "custom_rt_sqld_dj_username";
    // 是否选择办理时限
    // public static final String CUSTOM_RT_IS_SELECT_WORKDAY =
    // "custom_rt_is_select_workday";
    // 过程补件登记已经处理
    public static final String CUSTOM_RT_IS_GCBJ_DONE = "custom_rt_is_gcbj_done";
    // 是否显示工程图列表
    public static final String CUSTOM_RT_SHOW_ENGINEERING_DRAWING = "custom_rt_show_engineering_drawing";
    // 报送中央对接
    public static final String CUSTOM_RT_UNION_REGION = "custom_rt_unionRegion";
    // 报送通用软件
    public static final String CUSTOM_RT_UNION_TYRJ = "custom_rt_union_tyrj";
    // 申报来源流程代码 4：网上预约
    public static final String CUSTOM_RT_APPLY_FROM_FLOW = "custom_rt_applyFromFlow";
    // 并联JAVASCRIPT脚本
    public static final String PARALLEL_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;";
    // 串联JAVASCRIPT脚本
    public static final String SERIES_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;";
    // 可研批复JAVASCRIPT脚本
    public static final String KYPF_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_kypf.js;";
    // 工程规划许可阶段
    public static final String GCGHXKJD_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_gcghxkjd.js;";
    // 施工许可阶段
    public static final String SGXKJD_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_sgxkjd.js;";
    // 用地规划许可阶段（建筑类）
    public static final String YDGHXKJD_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_ydgfxkjd.js;";
    // 用地规划许可阶段（多规合一）
    public static final String YDGHXKJDDGHY_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_ydgfxkjddghy.js;";
    // 施工图审查阶段
    public static final String SGTSCJD_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_constants.js;/resources/app/js/xzsp/xzsp_workflow.js;/resources/app/js/xzsp/xzsp_workflow_sgtscjd.js;";
    // 前期意见需加载js
    public static final String QQYJ_SCRIPT_URL = "/resources/app/js/xzsp/xzsp_workflow_qqyj.js;";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    // 行政审批_督查视图ID
    public static final String XZSPY_DC_TJ = "XZSPY_DC_TJ";
    /******************************* 事项类型开始 *******************************/
    // 并联事项
    public static final String MATTERS_TYPE_PARALLEL = "2";
    /***************************** 行政审批自定义的参数结束 *****************************/
    // 串联事项
    public static final String MATTERS_TYPE_SERIES = "1";
    public static final String MATTERS_TYPE_SERIES_NAME = "串联";
    /******************************* 事项类型结束 *******************************/
    public static final String _N_1 = "-1";
    /******************************* 事项类型结束 *******************************/
    public static final String _1 = "1";
    public static final String _0 = "0";
    /******************************* 办件类型开始 *******************************/
    // 即办件
    public static final String BJ_TYPE_JBJ = "1";
    /******************************* 事项类型结束 *******************************/
    // 承诺件
    public static final String BJ_TYPE_CNJ = "2";
    // 联办件
    public static final String BJ_TYPE_LBJ = "3";
    /******************************* 套打模板开始 *******************************/
    // 打印模板ID
    // 申请材料接收凭证-登记窗口
    public static final String PRINT_TEMPLATE_CKDJ_RECEPTION = "XZSP_DJ_RECEPTION";
    /******************************* 办件类型结束 *******************************/
    // 申请材料接收凭证-建设项目用地规划许可（财政投融资项目）
    public static final String PRINT_TEMPLATE_CKDJ_RECEPTION_SG = "XZSP_DJ_RECEPTION_SG";
    // 申请材料接收凭证-多规合一（汇）
    public static final String PRINT_TEMPLATE_CKDJ_RECEPTION_YDH = "XZSP_DJ_RECEPTION_YDH";
    // 申请材料接收凭证-网上申报
    public static final String PRINT_TEMPLATE_WSSB_RECEPTION = "XZSP_WSSB_RECEPTION";
    // (收件)受理决定书
    public static final String PRINT_TEMPLATE_SL_DECISION = "XZSP_SL_DECISION";
    // (收件)受理决定书-建设项目用地规划许可（财政投融资项目）
    public static final String PRINT_TEMPLATE_SL_DECISION_SG = "XZSP_SL_DECISION_SG";
    // (收件)受理决定书-多规合一（汇）
    public static final String PRINT_TEMPLATE_SL_DECISION_YDH = "XZSP_SL_DECISION_YDH";
    // 补正材料通知书
    public static final String PRINT_TEMPLATE_BJ_NOTIFICATION = "XZSP_BJ_NOTIFICATION";
    // 补正材料通知书-多规合一（汇）
    public static final String PRINT_TEMPLATE_BJ_NOTIFICATION_YDH = "XZSP_BJ_NOTIFICATION_YDH";
    // 补正材料接收凭证
    public static final String PRINT_TEMPLATE_BJ_RECEPTION = "XZSP_BJ_RECEPTION";
    // 补正材料接收凭证（网上申报）
    public static final String PRINT_TEMPLATE_WSSB_BJ_RECEPTION = "XZSP_WSSB_BJ_RECEPTION";
    // (过程收件)受理决定书
    public static final String PRINT_TEMPLATE_GCSL_DECISION = "XZSP_GCSL_DECISION";
    // 审查阶段补正材料通知书
    public static final String PRINT_TEMPLATE_GCBJ_NOTIFICATION = "XZSP_GCBJ_NOTIFICATION";
    // 审查阶段补正材料接收凭证
    public static final String PRINT_TEMPLATE_GCBJ_RECEPTION = "XZSP_GCBJ_RECEPTION";
    // 审查阶段补正材料接收凭证（网上申报）
    public static final String PRINT_TEMPLATE_WSSB_GCBJ_RECEPTION = "XZSP_WSSB_GCBJ_RECEPTION";
    // 不予受理告知书
    public static final String PRINT_TEMPLATE_TJ_NOTIFICATION = "XZSP_TJ_NOTIFICATION";
    // 不予行政许可决定书
    public static final String PRINT_TEMPLATE_GCTJ_DECISION = "XZSP_GCTJ_DECISION";
    // 特别程序告知书
    public static final String PRINT_TEMPLATE_ZB_NOTIFICATION = "XZSP_ZB_NOTIFICATION";
    // 送达回证范本
    public static final String PRINT_TEMPLATE_SDHZ_TEMPLATE = "XZSP_SDHZ_TEMPLATE";
    // 送达回证（正式出文）
    public static final String PRINT_TEMPLATE_SDHZ_CWQR_TEMPLATE = "XZSP_SDHZ_CWQR_TEMPLATE";
    // 送达回证（不予行政许可决定书）
    public static final String PRINT_TEMPLATE_SDHZ_GCTJQR_TEMPLATE = "XZSP_SDHZ_GCTJQR_TEMPLATE";
    // 初审、联合指导意见汇总表
    public static final String PRINT_TEMPLATE_CS_LHZDYJ = "XZSP_CS_LHZDYJ";
    // 联合指导意见汇总表
    public static final String PRINT_TEMPLATE_LHZDYJ = "XZSP_LHZDYJ";
    // 征求意见汇总表
    public static final String PRINT_TEMPLATE_QZYJ = "XZSP_ZQYJHZB";
    // 前期意见--初审意见
    public static final String PRINT_TEMPLATE_CSYJ = "XZSP_CSYJHZB";
    // 前期意见--技术指导
    public static final String PRINT_TEMPLATE_JSZD = "XZSP_LHJSZD";
    // 前期意见--征求意见
    public static final String PRINT_TEMPLATE_ZQYJ = "XZSP_ZQYJHZB";
    // 项目卡
    public static final String PRINT_TEMPLATE_PROJECT_CARD = "XZSP_PROJECT_CARD";
    // 整改意见汇总表
    public static final String PRINT_TEMPLATE_ZGYJHZ = "XZSP_ZGYJHZ";
    // 现场验收安排情况表
    public static final String PRINT_TEMPLATE_SL_XCYSAPQK = "XZSP_SL_XCYSAPQK";
    // 补正材料通知书
    public static final String PRINT_TEMPLATE_DYSQB = "XZSP_DYSQB";
    // 文书名称编号
    // 申请材料接收凭证-登记窗口
    public static final String PRINT_TEMPLATE_CODE_CKDJ_RECEPTION = "01";
    // (收件)受理决定书
    public static final String PRINT_TEMPLATE_CODE_SL_DECISION = "04";
    // 补正材料通知书
    public static final String PRINT_TEMPLATE_CODE_BJ_NOTIFICATION = "02";
    // 补正材料接收凭证
    public static final String PRINT_TEMPLATE_CODE_BJ_RECEPTION = "05";
    // (过程收件)受理决定书
    public static final String PRINT_TEMPLATE_CODE_GCSL_DECISION = "10";
    // 审查阶段补正材料通知书
    public static final String PRINT_TEMPLATE_CODE_GCBJ_NOTIFICATION = "06";
    // 审查阶段补正材料接收凭证
    public static final String PRINT_TEMPLATE_CODE_GCBJ_RECEPTION = "11";
    // 不予受理告知书
    public static final String PRINT_TEMPLATE_CODE_TJ_NOTIFICATION = "03";
    // 不予行政许可决定书
    public static final String PRINT_TEMPLATE_CODE_GCTJ_DECISION = "07";
    // 特别程序告知书
    public static final String PRINT_TEMPLATE_CODE_ZB_NOTIFICATION = "08";
    // 送达回证
    public static final String PRINT_TEMPLATE_CODE_SDHZ_TEMPLATE = "09";
    // （网上申报）申请材料接收凭证
    public static final String PRINT_TEMPLATE_CODE_WSSB_RECEPTION = "12";
    // （网上申报）补正材料接收凭证
    public static final String PRINT_TEMPLATE_CODE_WSSB_BJ_RECEPTION = "13";
    // （网上申报）审查阶段补正材料接收凭证
    public static final String PRINT_TEMPLATE_CODE_WSSB_GCBJ_RECEPTION = "14";
    // 现场验收汇总表
    public static final String PRINT_TEMPLATE_CODE_XCYSAPQK_RECEPTION = "21";
    /******************************* 行政审批库ID开始 *******************************/
    // 事项库ID
    public static final String MATTERS_LIB_ID = "MATTERS_LIB";
    /******************************* 套打模板结束 *******************************/
    // 证照库ID
    public static final String LICENSE_LIB_ID = "LICENSE_LIB";
    // 材料库ID
    public static final String MATERIAL_LIB_ID = "MATERIAL_LIB";
    // 回执单库ID
    public static final String RECEIPT_LIB_ID = "RECEIPT_LIB";
    // 市民库ID
    public static final String RESIDENTS_LIB_ID = "RESIDENTS_LIB";
    // 单位库ID
    public static final String UNIT_LIB_ID = "UNIT_LIB";
    // 项目库ID
    public static final String PROJECT_LIB_ID = "PROJECT_LIB";
    // 黑名单库ID
    public static final String BLACKLIST_LIB_ID = "BLACKLIST_LIB";
    // 收费库ID
    public static final String CHARGE_LIB_ID = "CHARGE_LIB";
    /*************************** 行政审批库动态表单定义表名开始 **************************/
    // 行政审批_事项定义
    public static final String MATTERS_LIB_TABLE_NAME = "uf_xzsp_matters";
    /******************************* 行政审批库ID结束 *******************************/
    // 行政审批_事项定义_工作日
    public static final String MATTERS_LIB_WORKDAY_TABLE_NAME = "uf_xzsp_matters_gzr";
    // 行政审批_事项定义_应提交申请材料
    public static final String MATTERS_LIB_DEF_STUFF_TABLE_NAME = "uf_xzsp_matters_def_stuff";
    // 行政审批_证照定义
    public static final String LICENSE_LIB_TABLE_NAME = "uf_xzsp_license_def";
    // 行政审批_材料定义
    public static final String MATERIAL_LIB_TABLE_NAME = "uf_xzsp_material_def";
    // 行政审批_回执单
    public static final String RECEIPT_LIB_TABLE_NAME = "uf_xzsp_receipt_definition";
    // 行政审批_申报人
    public static final String RESIDENTS_LIB_TABLE_NAME = "uf_xzsp_sbr_black";
    // 行政审批_单位库
    public static final String UNIT_LIB_TABLE_NAME = "uf_xzsp_dwk_black";
    // 行政审批_项目库
    public static final String PROJECT_LIB_TABLE_NAME = "uf_xzsp_xmk_black";
    // 行政审批_黑名单
    public static final String BLACKLIST_LIB_TABLE_NAME = "uf_xzsp_black";
    // 行政审批_收费定义
    public static final String CHARGE_LIB_TABLE_NAME = "uf_xzsp_charge";
    // 行政审批_办件申请
    public static final String BANJIAN_LIB_TABLE_NAME = "uf_xzsp_bjsq";
    // 行政审批_办件申请单_初审意见告知函
    public static final String BANJIAN_CSYJ_TABLE_NAME = "uf_xzsp_subbjsqd_csyj";
    // 行政审批_办件申请单_联合技术指导
    public static final String BANJIAN_LHJSZD_TABLE_NAME = "uf_xzsp_subbjsqd_lhjszd";
    // 行政审批_办件申请单_征求意见
    public static final String BANJIAN_ZQYJ_TABLE_NAME = "uf_xzsp_subbjsqd_zqyj";
    // 行政审批_政府信息公开
    public static final String GOVERNMENT_INFORMATION_TABLE_NAME = "uf_xzsp_zfxxgk";
    // 行政审批_项目库_合并项目
    public static final String PROJECT_LIB_HBXM_TABLE_NAME = "uf_xzsp_xmk_black_hbxm";
    /*************************** 行政审批库动态表单字段名开始 **************************/
    // 事项定义动态表单字段名
    // 事项名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_SXMC = "SXMC";
    /*************************** 行政审批库动态表单定义表名结束 **************************/
    // 面向用户
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_FACING_USER = "MXYH";
    // 所属部门单位
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_BELONGING_UNIT = "SSBMDW";
    // 所属部门单位名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_BELONGING_DEPT = "SSBMDWMC";
    // 所属部门单位代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_BELONGING_CODE = "SSBMDWDM";
    // 窗口登记人员
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_WINDOW_PERSON = "QKDJRY";
    // 窗口登记人员ID
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_WINDOW_PERSON_ID = "CKDJRYDM";
    // 受理核准人员
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_ACCEPT_PERSON = "SLHZRY";
    // 受理核准人员ID
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_ACCEPT_PERSON_ID = "SLHZRYDM";
    // 前置事项
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QZSX = "QZSX";
    // 前置事项代码(UUID)
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QZSXDM = "QZSXDM";
    // 前置事项编号
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QZSXBH = "QZSXBH";
    // 关联的流程ID
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_GUANLIAN_PROCESS = "GLSPLCID";
    // 所属阶段
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_PROJECT_PROCESS = "SSJDDM";
    // 事项编号
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_MATTERS_CODE = "SXBH";
    // 是否显示工程图列表
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_SHOW_ENGINEERING_DRAWING = "XSGCTLB";
    // 是否显示工程图列表代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_SHOW_ENGINEERING_DRAWING_CODE = "XSGCTLBDM";
    // 是否显示工程图列表
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_MATTERS_NAME = "SXMC";
    // 市级共享事项
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_SJGXSX = "SJGXSX";
    // 区级共享事项
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QJGXSX = "QJGXSX";
    // 事项编码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_SXBM = "SXBM";
    // 启用名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QY = "QY";
    // 启用JSON
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QY_ = "QY_";
    // 启用代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QYDM = "QYDM";
    // 牵头部门单位
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QTBMDW = "QTBMDW";
    // 牵头部门单位名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QTBMDWMC = "QTBMDWMC";
    // 牵头部门单位代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_QTBMDWDM = "QTBMDWDM";
    // 分批验收申请审批期限
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_FPYSSQSPQX = "FPYSSQSPQX";
    // 开通网上预审
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYS = "KTWSYS";
    // 开通网上预审名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYSMC = "KTWSYSMC";
    // 开通网上预审代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYSDM = "KTWSYSDM";
    // 网上预审期限
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_WSYSQX = "WSYSQX";
    // 开通网上预约
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYY = "KTWSYY";
    // 开通网上预约名称
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYYMC = "KTWSYYMC";
    // 开通网上预约代码
    public static final String MATTERS_LIB_MATTERS_FORM_FIELD_KTWSYYDM = "KTWSYYDM";
    // 事项定义应提交申请材料动态表单字段名
    // 材料名称
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_NAME = "CLMC";
    // 材料类型名称
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_TYPE_NAME = "CLLXMC";
    // 材料类型代码
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_TYPE_CODE = "CLLXDM";
    // 材料编号
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_NO = "CLBH";
    // 材料说明
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_EXPLAIN = "CLSM";
    // 材料性质
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_NATURE = "CLXZ";
    // 应交纸质数量
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_PAPER_NUMBER = "YJZZSL";
    // 应交电子数量
    public static final String MATTERS_LIB_APPLY_MATERIAL_FORM_FIELD_ELECTRONIC_NUMBER = "YJDZSL";
    // 材料类型数据字典
    public static final String MATTERS_LIB_APPLY_MATERIAL_TYPE_DATA_DICTIONARY = "XZSP_MATERIAL_NAME";
    // 项目挂钩
    public static final String MATTERS_LIB_APPLY_MATERIAL_NAME_ASSOCIATED_WITH_PROJECT = "associated_with_project";
    // 1表示与项目挂钩
    public static final String MATTERS_LIB_APPLY_MATERIAL_VALUE_ASSOCIATED_WITH_PROJECT = "1";
    // 事项定义工作日动态表单字段名
    // 工作日
    public static final String MATTERS_LIB_WORKDAY_FORM_FIELD_WORK_DAY = "GZR";
    // 备注
    public static final String MATTERS_LIB_WORKDAY_FORM_FIELD_REMARK = "BZ";
    // 排序
    // public static final String MATTERS_LIB_WORKDAY_FORM_FIELD_SORT =
    // "work_day_sort";
    // 事项类型
    public static final String MATTERS_LIB_WORKDAY_FORM_FIELD_TYPE = "LX";
    // 证照库定义动态表单字段名 LICENSE_LIB_ID
    // 证照UUID
    public static final String LICENSE_LIB_FORM_FIELD_UUID = IdEntity.UUID;
    // 业务流水号
    public static final String LICENSE_LIB_FORM_FIELD_YWLSH = "YWLSH";
    // 证照名称
    public static final String LICENSE_LIB_FORM_FIELD_NAME = "ZZMC";
    // 证照类型
    public static final String LICENSE_LIB_FORM_FIELD_TYPE = "ZZLX";
    // 证照代码
    public static final String LICENSE_LIB_FORM_FIELD_CODE = "ZZDM";
    // 证件号
    public static final String LICENSE_LIB_FORM_FIELD_SERIAL_NO = "ZZH";
    // 审批结果类型
    public static final String LICENSE_LIB_FORM_FIELD_APPROVAL_TYPE = "SPJGLX";
    // 颁证单位名称
    public static final String LICENSE_LIB_FORM_FIELD_UNIT_NAME = "BZDWMC";
    // 颁证单位组织代码
    public static final String LICENSE_LIB_FORM_FIELD_UNIT_ID = "BZDWDM";
    // 证照所有者编号
    public static final String LICENSE_LIB_FORM_FIELD_OWNER_NO = "ZZSYZBH";
    // 证照所有者名称
    public static final String LICENSE_LIB_FORM_FIELD_OWNER_NAME = "ZZSYZMC";
    // 核准时间
    public static final String LICENSE_LIB_FORM_FIELD_APPROVAL_TIME = "HZSJ";
    // 更新日期
    public static final String LICENSE_LIB_FORM_FIELD_UPDATE_TIME = "GXRQ";
    // 下次更新日期
    public static final String LICENSE_LIB_FORM_FIELD_NEXT_UPDATE_TIME = "XCGXRQ";
    // 有效开始时间
    public static final String LICENSE_LIB_FORM_FIELD_START_TIME = "YXKSSJ";
    // 有效截止时间
    public static final String LICENSE_LIB_FORM_FIELD_END_TIME = "YXJZSJ";
    // 证照有效性标识
    public static final String LICENSE_LIB_FORM_FIELD_EFFECTIVE = "YXXBS";
    // 证照附件
    public static final String LICENSE_LIB_FORM_FIELD_FILE_NODE_UUID = "ZZFJ";
    // 应用类别
    public static final String LICENSE_LIB_FORM_FIELD_APPLICATION_TYPE = "YYLB";
    // 建设项目编号
    public static final String LICENSE_LIB_FORM_FIELD_JSXMDM = "JSXMDM";
    // 建设项目名称
    public static final String LICENSE_LIB_FORM_FIELD_JSXMMC = "JSXMMC";
    // 批文要素数据
    public static final String LICENSE_LIB_FORM_FIELD_PWYS = "PWYS";
    // 批文要素数据
    public static final String LICENSE_LIB_FORM_FIELD_SSSXDM = "SSSXDM";
    /**
     * 引用批文调用 2016-02-26 start
     */
    // 项目长编号
    public static final String LICENSE_LIB_FORM_FIELD_XMCBH = "XMCBH";
    // 所属事项名称
    public static final String LICENSE_LIB_FORM_FIELD_SSSXMC = "SSSXMC";
    // 所属事项编号
    public static final String LICENSE_LIB_FORM_FIELD_SSSXBH = "SSSXBH";
    // 批文要素formuuid
    public static final String LICENSE_LIB_FORM_FIELD_PWYSDUUID = "PWYSDUUID";
    /**
     * 引用批文调用 2016-02-26 end
     */

    // 材料库定义动态表单字段名
    // 材料UUID
    public static final String MATERIAL_LIB_FORM_FIELD_UUID = "UUID";
    // 材料所在的业务流水号
    public static final String MATERIAL_LIB_FORM_FIELD_YWLSH = "YWLSH";
    // 材料所有者编号
    public static final String MATERIAL_LIB_FORM_FIELD_OWNER_NO = "CLSYZBH";
    // 材料所有者名称
    public static final String MATERIAL_LIB_FORM_FIELD_OWNER_NAME = "CLSYZMC";
    // 材料所属项目编号
    public static final String MATERIAL_LIB_FORM_FIELD_PROJECT_NO = "CLSSXMBH";
    // 材料名称
    public static final String MATERIAL_LIB_FORM_FIELD_NAME = "CLMC";
    // 材料编号
    public static final String MATERIAL_LIB_FORM_FIELD_CODE = "CLBH";
    // 材料类型名称
    public static final String MATERIAL_LIB_FORM_FIELD_TYPE_NAME = "CLLXMC";
    // 材料类型代码
    public static final String MATERIAL_LIB_FORM_FIELD_TYPE_CODE = "CLLXDM";
    // 材料提交时间
    public static final String MATERIAL_LIB_FORM_FIELD_SUBMIT_TIME = "CLTJSJ";
    // 材料有效性标识
    public static final String MATERIAL_LIB_FORM_FIELD_EFFECTIVE = "CLYXXBS";
    // 附件
    public static final String MATERIAL_LIB_FORM_FIELD_FILE_NODE_UUID = "CLFJ";
    // 应用类别
    public static final String MATERIAL_LIB_FORM_FIELD_APPLICATION_TYPE = "YYLB";
    // 项目单位所有者代码
    public static final String PROJECT_XMDWSYZDM = "XMDWSYZDM";
    // 项目库定义动态表单字段名
    public static final String PROJECT_LIB_FORM_FIELD_NAME = "XMMC";
    // 项目编号
    public static final String PROJECT_LIB_FORM_FIELD_BH = "XMBH";
    // 项目短编号
    public static final String PROJECT_LIB_FORM_FIELD_DBH = "XMDBH";
    // 上一级项目编号
    public static final String PROJECT_LIB_FORM_FIELD_SJXMBH = "SJXMBH";
    // 上一级项目名称
    public static final String PROJECT_LIB_FORM_FIELD_SJXMMC = "SJXMMC";
    // 项目建设单位名称
    public static final String PROJECT_LIB_FORM_FIELD_CONSTRUCTION_UNIT_NAME = "XMJSDWMC";
    // 项目建设单位代码
    public static final String PROJECT_LIB_FORM_FIELD_CONSTRUCTION_UNIT_ID = "XMJSDWDM";
    // 重点工程
    public static final String PROJECT_LIB_FORM_FIELD_ZDGC = "ZDGC";
    // 项目地址
    public static final String PROJECT_LIB_FORM_FIELD_CONSTRUCTION_ADDRESS = "XMDZ";
    // 单位法人代表
    public static final String PROJECT_LIB_FORM_FIELD_CONSTRUCTION_DWFRDB = "DWFRDB";
    // 联系人姓名
    public static final String PROJECT_LIB_FORM_FIELD_CONTACTS_NAME = "LXRXM";
    // 联系人证件号
    public static final String PROJECT_LIB_FORM_FIELD_CONTACTS_ID = "LXRZJH";
    // 联系人手机号码
    public static final String PROJECT_LIB_FORM_FIELD_CONTACTS_MOBILE = "LXRSJHM";
    // 联系人电话号码
    public static final String PROJECT_LIB_FORM_FIELD_CONTACTS_PHONE = "LXRDHHM";
    // 资金来源
    public static final String PROJECT_LIB_FORM_FIELD_ZJLY = "ZJLY";
    // 项目性质
    public static final String PROJECT_LIB_FORM_FIELD_XMXZ = "XMXZ";
    // 数据UUID
    public static final String PROJECT_LIB_FORM_FIELD_UUID = IdEntity.UUID;
    // 组织机构代码
    public static final String PROJECT_LIB_FORM_FIELD_ZZJGDM = "ZZJGDM";
    // 组织机构代码流水号
    public static final String PROJECT_LIB_FORM_FIELD_ZZJGDM_SERIALNUM = "XZSP_PROJECT_ZZJGDM";
    // 组织机构代码UUID
    public static final String PROJECT_LIB_FORM_FIELD_UNIT_UUID = "UNITUUID";
    // 施工单位名称
    public static final String PROJECT_LIB_FORM_FIELD_SGDWMC = "SGDWMC";
    // 是否策划项目 1-策划项目，其他值都不是策划项目
    public static final String PROJECT_LIB_FORM_FIELD_SFCHXM = "SFCHXM";
    // 前期计划（发改第几期）
    public static final String PROJECT_LIB_FORM_FIELD_QQJH = "QQJH";
    // 策划项目状态
    public static final String PROJECT_LIB_FORM_FIELD_CHXMZT = "CHXMZT";
    // 项目所属阶段
    public static final String PROJECT_LIB_FORM_FIELD_XMSSJD = "XMSSJD";
    // 项目所属阶段（原）
    public static final String PROJECT_LIB_FORM_FIELD_XMSSJDY = "XMSSJDY";
    // 项目所属阶段代码
    public static final String PROJECT_LIB_FORM_FIELD_XMSSJDDM = "XMSSJDDM";
    // 项目所属阶段名称
    public static final String PROJECT_LIB_FORM_FIELD_XMSSJDMC = "XMSSJDMC";
    // 项目经历阶段
    public static final String PROJECT_LIB_FORM_FIELD_XMJLJD = "XMJLJD";
    // 项目所处阶段
    public static final String PROJECT_LIB_FORM_FIELD_XMSCJD = "XMSCJD";
    // 项目代码
    public static final String PROJECT_LIB_FORM_FIELD_XMDM = "XMDM";
    // 关联项目编号
    public static final String PROJECT_LIB_FORM_FIELD_GLXMBH = "GLXMBH";
    // 合并项目标识
    public static final String PROJECT_LIB_FORM_FIELD_HBXMBS = "HBXMBS";
    // 合并项目从表_是否合并标识
    public static final String PROJECT_LIB_HBXM_FORM_FIELD_SFHB = "SFHB";
    // 项目创建时间
    public static final String PROJECT_LIB_HBXM_FORM_FIELD_CREATE_TIME = "createTime";
    // 回执单定义动态表单字段名
    // 回执单名称
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_NAME = "HZDMC";
    // 回执单编号
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_CODE = "HZDBH";
    // 回执编号
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_SERIAL_NO = "HZBH";
    // 回执时间
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_REPLY_TIME = "HZSJ";
    // 办件单号
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_DO_DOCUMENT_ID = "BJDH";
    // 项目名称
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_PROJECT_NAME = "XMMC";
    // 项目短编号
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_SHORT_PROJECT_CODE = "XMBH";
    // 事项名称
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_MATTERS_NAME = "SXMC";
    // 事项编号
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_MATTERS_Code = "SXBH";
    // 打印模板ID
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_PRINT_TEMPLATE_ID = "DYMBID";
    // 打印者ID
    public static final String RECEIPT_LIB_FORM_FIELD_RECEIPTS_PRINTOR_ID = "DYZID";
    // 回执单附件
    public static final String RECEIPT_LIB_FORM_FIELD_FILE_NODE_UUID = "HZFJ";
    // 流程实例UUID
    public static final String RECEIPT_LIB_FORM_FIELD_FLOW_INST_UUID = "LCSLUUID";
    // 申报人库
    // 姓名
    public static final String RESIDENTS_LIB_FORM_FIELD_NAME = "XM";
    // 性别
    public static final String RESIDENTS_LIB_FORM_FIELD_SEX = "XB";
    // 证件号
    public static final String RESIDENTS_LIB_FORM_FIELD_ID = "ZJH";
    // 手机号
    public static final String RESIDENTS_LIB_FORM_FIELD_MOBILE = "SJH";
    // 联系电话
    public static final String RESIDENTS_LIB_FORM_FIELD_TELEPHONE = "LXDH";
    // 电子邮件
    public static final String RESIDENTS_LIB_FORM_FIELD_EMAIL = "DZYJ";
    // 单位库
    public static final String UNIT_DWMC = "DWMC";
    public static final String UNIT_ZZJGDM = "ZZJGDM";
    /*************************** 行政审批事项申请动态表单开始 **************************/
    // 行政审批_办件申请单从表_应提交材料
    public static final String APPLY_MATTERS_MATERIAL_TABLE_ID = "uf_xzsp_subbjsqd";

    /*************************** 行政审批库动态表单字段名结束 **************************/
    // 行政审批_办件申请单从表_补件材料
    public static final String APPLY_MATTERS_BUJIAN_MATERIAL_TABLE_ID = "uf_xzsp_subbjsqd_bj";
    // 行政审批_办件申请单从表_附加材料
    public static final String APPLY_MATTERS_ADDITION_MATERIAL_TABLE_ID = "uf_xzsp_subcllist_add";
    // 行政审批_办件申请单从表_工程图
    public static final String APPLY_MATTERS_ENGINEERING_DRAWING_TABLE_ID = "uf_xzsp_subbjsqd_cad";
    // 行政审批_办件申请单从表_引用批文
    public static final String APPLY_MATTERS_YYPWSJ_DRAWING_TABLE_ID = "uf_xzsp_yypw";
    /*************************** 行政审批事项前期意见表单开始 **************************/
    // 行政审批_办件申请单从表_初审意见
    public static final String APPLY_MATTERS_SUBBJSQD_CSYJ_TABLE_ID = "uf_xzsp_subbjsqd_csyj";
    /*************************** 行政审批事项申请动态表单结束 **************************/
    // 行政审批_办件申请单从表_技术指导
    public static final String APPLY_MATTERS_SUBBJSQD_JSZD_TABLE_ID = "uf_xzsp_subbjsqd_lhjszd";
    // 行政审批_办件申请单从表_征求意见
    public static final String APPLY_MATTERS_SUBBJSQD_ZQYJ_TABLE_ID = "uf_xzsp_subbjsqd_zqyj";
    /*************************** 行政审批事项申请动态表单字段名开始 **************************/
    // 事项申请字段
    // 省网申报号
    public static final String APPLY_MATTERS_FORM_FIELD_SWSBH = "SWSBH";
    /*************************** 行政审批事项前期意见表单结束 **************************/
    /********************************************************************/
    // 流水号
    public static final String APPLY_MATTERS_FORM_FIELD_SERIAL_NUMBER = "LSH";
    // 密码
    public static final String APPLY_MATTERS_FORM_FIELD_PASSWORD = "MM";
    // 项目编号
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_CODE = "XMBH";
    // 项目短编号
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_SHORT_CODE = "XMDBH";
    // 项目名称
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_NAME = "XMMC";
    // 中心项目UUID
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_UUID = "XMUUID";
    // 地址
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_ADDRESS = "DZ";
    // 单位法人代表
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_FRDB = "FRDB";
    // 申请人ID(个人身份证号，企事业组织机构代码)
    public static final String APPLY_MATTERS_FORM_FIELD_APPLICANT_ID = "SQRBH";
    // 申请人名称
    public static final String APPLY_MATTERS_FORM_FIELD_APPLICANT_NAME = "SQRMC";
    // 申请人联系方式
    public static final String APPLY_MATTERS_FORM_FIELD_APPLICANT_LXFS = "SQRLXFS";
    // 申请人联系电话
    public static final String APPLY_MATTERS_FORM_FIELD_APPLICANT_LXDH = "SQRLXDH";
    // 申请单位ID
    public static final String APPLY_MATTERS_FORM_FIELD_UNIT_ID = "SQDWDM";
    // 申请单位名称
    public static final String APPLY_MATTERS_FORM_FIELD_UNIT_NAME = "SQDWMC";
    // 申报来源名称
    public static final String APPLY_MATTERS_FORM_FIELD_SBLYMC = "SBLYMC";
    // 材料所属项目ID
    public static final String APPLY_MATTERS_FORM_FIELD_PROJECT_ID = "item_matters_id";
    // 受理单位
    public static final String APPLY_MATTERS_FORM_FIELD_RECEIVING_UNIT = "SLDW";
    // 受理单位名称
    public static final String APPLY_MATTERS_FORM_FIELD_RECEIVING_UNIT_NAME = "SLDWMC";
    // 受理单位代码
    public static final String APPLY_MATTERS_FORM_FIELD_RECEIVING_UNIT_CODE = "SLDWDM";
    // 审批目录标识
    public static final String APPLY_MATTERS_FORM_FIELD_SPMLBS = "SPMLBS";
    // 接件日期
    public static final String APPLY_MATTERS_FORM_FIELD_PICK_UP_DATE = "JJRQ";
    // 收件日期
    public static final String APPLY_MATTERS_FORM_FIELD_PROMISE_RECEIVE_DATE = "SJRQ";
    // 承诺办结日期
    public static final String APPLY_MATTERS_FORM_FIELD_PROMISE_DEADLINE = "CNBJRQ";
    // 承诺办理时限
    public static final String APPLY_MATTERS_FORM_FIELD_PROMISE_TIME_LIMIT = "CNBJR";
    // 承诺办理时限（分钟）
    public static final String APPLY_MATTERS_FORM_FIELD_PROMISE_FZ_TIME_LIMIT = "CNSXFZ";
    // 最新办理时限
    public static final String APPLY_MATTERS_FORM_FIELD_PROMISE_NEWEST_TIME_LIMIT = "ZXBJR";
    // 事项名称
    public static final String APPLY_MATTERS_FORM_FIELD_MATTERS_NAME = "SXMC";
    // 事项编号
    public static final String APPLY_MATTERS_FORM_FIELD_MATTERS_CODE = "SXBH";
    // 办件类型
    public static final String APPLY_MATTERS_FORM_FIELD_BJLX = "BJLX";
    // 是否计时
    public static final String APPLY_MATTERS_FORM_FIELD_SFJS = "SFJS";
    // 是否计时代码
    public static final String APPLY_MATTERS_FORM_FIELD_SFJSMC = "SFJSMC";
    // 是否计时名称
    public static final String APPLY_MATTERS_FORM_FIELD_SFJSDM = "SFJSDM";
    // 最新业务操作
    public static final String APPLY_MATTERS_FORM_FIELD_ZXYWCZ = "ZXYWCZ";
    // 办件状态
    public static final String APPLY_MATTERS_FORM_FIELD_BLZT = "BJZT";
    // 市区联动办件来源
    public static final String APPLY_MATTERS_FORM_FIELD_SQLDBJLY = "SQLDBJLY";
    // 取件标识
    public static final String APPLY_MATTERS_FORM_FIELD_QJBS = "QJBS";
    // 取件标识代码
    public static final String APPLY_MATTERS_FORM_FIELD_QJBSDM = "QJBSDM";
    // 逾期理由
    public static final String APPLY_MATTERS_FORM_FIELD_YQLY = "YQLY";
    // 办结结果
    public static final String APPLY_MATTERS_FORM_FIELD_BJJG = "BJJG";
    // 取件时间
    public static final String APPLY_MATTERS_FORM_FIELD_QJSJ = "QJSJ";
    // 取件备注
    public static final String APPLY_MATTERS_FORM_FIELD_QJBZ = "BZ";
    // 申报来源
    public static final String APPLY_MATTERS_FORM_FIELD_SBLY = "SBLY";
    // 申报来源代码
    public static final String APPLY_MATTERS_FORM_FIELD_SBLYDM = "SBLYDM";
    // 受理意见汇总
    public static final String APPLY_MATTERS_FORM_FIELD_SLYJHZ = "SLYJHZ";
    // 出文意见汇总
    public static final String APPLY_MATTERS_FORM_FIELD_CWYJHZ = "CWYJHZ";
    // 受理意见汇总文件
    public static final String APPLY_MATTERS_FORM_FIELD_SLYJHZWJ = "SLYJHZWJ";
    // 出文意见汇总文件
    public static final String APPLY_MATTERS_FORM_FIELD_CWYJHZWJ = "CWYJHZWJ";
    // 施工单位名称
    public static final String APPLY_MATTERS_FORM_FIELD_SGDWMC = "SGDWMC";
    // 是否策划项目
    public static final String APPLY_MATTERS_FORM_FIELD_SFCHXM = "SFCHXM";
    // 策划项目状态
    public static final String APPLY_MATTERS_FORM_FIELD_CHXMZT = "CHXMZT";
    // 事项备注
    public static final String APPLY_MATTERS_FORM_FIELD_SXBZ = "SXBZ";
    // 登记窗口备注
    public static final String APPLY_MATTERS_FORM_FIELD_DJCKBZ = "DJCKBZ";
    // 排队号
    public static final String APPLY_MATTERS_FORM_FIELD_PDH = "PDH";
    // 最新申请备注
    public static final String APPLY_MATTERS_FORM_FIELD_ZXSQBZ = "ZXSQBZ";
    // 最新申请时间
    public static final String APPLY_MATTERS_FORM_FIELD_ZXSQSJ = "ZXSQSJ";
    // 最新到期时间
    public static final String APPLY_MATTERS_FORM_FIELD_ZXDQSJ = "ZXDQSJ";
    // 最新剩余时间
    public static final String APPLY_MATTERS_FORM_FIELD_ZXSYSJ = "ZXSYSJ";
    // 预审件状态
    public static final String APPLY_MATTERS_FORM_FIELD_YSJZT = "YSJZT";
    // 最新预审时间
    public static final String APPLY_MATTERS_FORM_FIELD_ZXYSSJ = "ZXYSSJ";
    // 最新预审备注
    public static final String APPLY_MATTERS_FORM_FIELD_ZXYSBZ = "ZXYSBZ";
    // 网上预审受理单位
    public static final String APPLY_MATTERS_FORM_FIELD_WSYSSLDW = "WSYSSLDW";
    // 是否拟稿退回
    public static final String APPLY_MATTERS_FORM_FIELD_SFNGTH = "SFNGTH";
    // 应提交的材料字段
    // 数据UUID
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_UUID = IdEntity.UUID;
    // 材料名称
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_NAME = "CLMC";
    // 材料性质
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_NATURE = "CLXZ";
    // 材料类型
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_TYPE = "CLLX";
    // 材料类型名称
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_TYPE_NAME = "CLLXMC";
    // 材料类型代码
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_TYPE_CODE = "CLLXDM";
    // 应交纸质数量
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_PAPER_NUMBER = "YJZZSL";
    // 实交纸质数量
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_FINALLY_PAPER_NUMBER = "SJZZSL";
    // 应交电子数量
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_ELECTRONIC_NUMBER = "YJDZSL";
    // 实交电子数量
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_FINALLY_ELECTRONIC_NUMBER = "SJDZSL";
    // 递交附件
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_FILE_NODE_UUID = "DJFJ";
    // 所属事项ID
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_MATTERS_NO = "SSSXBH";
    // 所属事项名称
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_MATTERS_NAME = "SSSXMC";
    // 是否补件材料
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_IS_BUJIAN = "SFBJCL";
    // 补件原因
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_REASON = "BJYY";
    // 提交时间(创建时间)
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_CREATE_TIME = "create_time";
    // 是否申报者提供
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_CREATE_IS_APPLICANT_APPLY = "SFSBZTG";
    // 排序编号
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_SORT_ORDER = "SORTORDER";
    // 审批目录应交材料标识号
    public static final String APPLY_MATTERS_MATERIAL_FORM_FIELD_YJCLBSH = "YJCLBSH";
    // 补件材料清单
    // 数据UUID
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_UUID = IdEntity.UUID;
    // 材料名称
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_NAME = "CLMC";
    // 材料性质
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_NATURE = "CLXZ";
    // 材料类型
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_TYPE = "CLLX";
    // 材料类型代码
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_TYPE_NAME = "CLLXMC";
    // 材料类型代码
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_TYPE_CODE = "CLLXDM";
    // 所属事项名称
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_MATTERS_NAME = "SSSXMC";
    // 所属事项编号
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_MATTERS_NO = "SSSXBH";
    // 应交纸质数量
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_PAPER_NUMBER = "YJZZSL";
    // 实交纸质数量
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_FINALLY_PAPER_NUMBER = "SJZZSL";
    // 应交电子数量
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_ELECTRONIC_NUMBER = "YJDZSL";
    // 实交电子数量
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_FINALLY_ELECTRONIC_NUMBER = "SJDZSL";
    // 递交附件
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_FILE_NODE_UUID = "DJFJ";
    // 补件原因
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_REASON = "BJYY";
    // 补件状态
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_STATUS = "BJZT";
    // 补件来源
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_SOURCE = "BJLY";
    // 材料编号
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_BH = "BH";
    // 补件材料创建时间
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_CREATE_TIME = "create_time";
    // 材料来源代码
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_CLLYDM = "CLLYDM";
    // 文档类型
    public static final String BJCL_MATTERS_MATERIAL_FORM_FIELD_BUJIAN_WDLX = "WDLX";
    // 附加材料清单
    // 材料名称
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_CLMC = "CLMC";
    // 材料来源(1补件附加、2过补附加)
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_CLLY = "CLLY";
    // 材料状态(-1已删除、1正常)
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_CLZT = "CLZT";
    // 上传时间
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_SCSJ = "SCSJ";
    // 实交纸质数量
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_FINALLY_PAPER_NUMBER = "SJZZSL";
    // 应交电子数量
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_ELECTRONIC_NUMBER = "YJDZSL";
    // 上传人
    public static final String FJCL_MATTERS_MATERIAL_FORM_FIELD_SCR = "SCR";
    // 初审意见告知函
    // 办理单位
    public static final String CSYJ_MATTERS_FORM_FIELD_DEPARTMENT = "BLDW";
    // 办理单位名称
    public static final String CSYJ_MATTERS_FORM_FIELD_DEPARTMENT_NAME = "BLDWMC";
    // 办理单位代码
    public static final String CSYJ_MATTERS_FORM_FIELD_DEPARTMENT_CODE = "BLDWDM";
    // 初审意见
    public static final String CSYJ_MATTERS_FORM_FIELD_CSYJ = "CSYJ";
    // 办理时间
    public static final String CSYJ_MATTERS_FORM_FIELD_OPERATE_TIME = "BLSJ";
    // 联合技术指导
    // 办理单位
    public static final String LHJSZD = "BLDW";
    // 办理单位名称
    public static final String LHJSZD_MATTERS_FORM_FIELD_DEPARTMENT_NAME = "BLDWMC";
    // 办理单位代码
    public static final String LHJSZD_MATTERS_FORM_FIELD_DEPARTMENT_CODE = "BLDWDM";
    // 指导意见
    public static final String LHJSZD_MATTERS_FORM_FIELD_CSYJ = "ZDYJ";
    // 办理时间
    public static final String LHJSZD_MATTERS_FORM_FIELD_OPERATE_TIME = "BLSJ";
    // 征求意见
    public static final String ZQYJ_MATTERS_FORM_FIELD_DEPARTMENT = "BLDW";
    // 办理单位名称
    public static final String ZQYJ_MATTERS_FORM_FIELD_DEPARTMENT_NAME = "BLDWMC";
    // 办理单位代码
    public static final String ZQYJ_MATTERS_FORM_FIELD_DEPARTMENT_CODE = "BLDWDM";
    // 征求意见
    public static final String ZQYJ_MATTERS_FORM_FIELD_ZQYJ = "ZQYJ";
    // 办理时间
    public static final String ZQYJ_MATTERS_FORM_FIELD_OPERATE_TIME = "BLSJ";
    /******************************************* 行政审批数据字典开始 ***********************************************/
    // 项目进程
    public static final String XZSP_DATADICTIONARY_PROJECT_PROCESS = "XZSP_PROJECT_PROCESS";
    /*************************** 行政审批事项申请动态表单字段名结束 **************************/
    // 项目进程_建设项目联合审批
    public static final String XZSP_PROJECT_PROCESS_JSXM = "XZSP_PROJECT_PROCESS_JSXM";
    // 项目进程_默认展示
    public static final String XZSP_PROJECT_PROCESS_DEFAULT = "XZSP_PROJECT_PROCESS_ELGG_CZXTRZXM_JZL";
    // 项目进程_默认展示名称
    public static final String XZSP_PROJECT_PROCESS_DEFAULT_NAME = "财政性投融资项目（建筑类）";
    // 项目进程_非公开出让用地项目（建筑类）
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_JZL = "XZSP_PROJECT_PROCESS_JSXMCZTRZXM_JZL";
    // 项目进程_建设项目财政投融资项目（线性工程）
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_XXGC = "XZSP_PROJECT_PROCESS_JSXMCZTRZXM_XXGC";
    // 项目进程_建设项目（公开出让用地备案类）
    public static final String XZSP_PROJECT_PROCESS_JSXMGKCRYDBA = "XZSP_PROJECT_PROCESS_JSXMGKCRYDBA";
    // 项目进程_建设项目联合审批_其他阶段代码
    public static final String XZSP_DATADICTIONARY_PROJECT_PROCESS_JSXM_OTHER = "主流程外事项";
    // 项目进程_非公开出让用地项目（建筑类）名称
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_JZL_NAME = "非公开出让用地项目（建筑类）";
    // 项目进程_建设项目财政投融资项目（线性工程）名称
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_XXGC_NAME = "建设项目财政投融资项目（线性工程）";
    // 项目进程_建设项目（公开出让用地备案类）名称
    public static final String XZSP_PROJECT_PROCESS_JSXMGKCRYDBA_NAME = "建设项目（公开出让用地备案类）";
    // 项目进程_非公开出让用地项目（建筑类）_可研批复及工程规划许可阶段代码
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_JZL_KY_DM = "015014002002";
    // 项目进程_建设项目财政投融资项目（线性工程）_可研批复及工程规划许可阶段代码
    public static final String XZSP_PROJECT_PROCESS_JSXMCZTRZXM_XXGC_KY_DM = "015014003002";
    // 项目进程_建设工程施工图设计文件审查 事项编号
    public static final String XZSP_PROJECT_PROCESS_JSGCSGTSJWJSC = "350200426605381A070000";
    // 项目进程_施工图审查合格书备案 事项编号
    public static final String XZSP_PROJECT_PROCESS_SGTSCHGSBA = "350200004138453E380000";
    // 第一阶段代码_建筑类（旧）
    public static final String OLD_FIRST_PHASE_CODE_JZL = "015014002001";
    // 第一阶段代码_线性工程（旧）
    public static final String OLD_FIRST_PHASE_CODE_XXGC = "015014003001";
    // 第一阶段代码_备案类（旧）
    public static final String OLD_FIRST_PHASE_CODE_BAL = "015014004001";
    // 第二阶段代码_建筑类（旧）
    public static final String OLD_SECOND_PHASE_CODE_JZL = "015014002002";
    // 第二阶段代码_线性工程（旧）
    public static final String OLD_SECOND_PHASE_CODE_XXGC = "015014003002";
    // 第二阶段代码_备案类（旧）
    public static final String OLD_SECOND_PHASE_CODE_BAL = "015014004002";
    // 第三阶段代码_建筑类（旧）
    public static final String OLD_THIRD_PHASE_CODE_JZL = "015014002003";
    // 第三阶段代码_线性工程（旧）
    public static final String OLD_THIRD_PHASE_CODE_XXGC = "015014003003";
    // 第三阶段代码_备案类（旧）
    public static final String OLD_THIRD_PHASE_CODE_BAL = "015014004003";
    // 第四阶段代码_建筑类（旧）
    public static final String OLD_FORTH_PHASE_CODE_JZL = "015014002004";
    // 第四阶段代码_线性工程（旧）
    public static final String OLD_FORTH_PHASE_CODE_XXGC = "015014003004";
    // 第四阶段代码_备案类（旧）
    public static final String OLD_FORTH_PHASE_CODE_BAL = "015014004004";
    // 第五阶段代码_建筑类（旧）
    public static final String OLD_FIFTH_PHASE_CODE_JZL = "015014002005";
    // 第五阶段代码_线性工程（旧）
    public static final String OLD_FIFTH_PHASE_CODE_XXGC = "015014003005";
    // 第五阶段代码_备案类（旧）
    public static final String OLD_FIFTH_PHASE_CODE_BAL = "015014004005";
    /******************************************* 行政审批流水号开始 ***********************************************/
    // 项目编号
    public static final String SEIALNUMBER_XZSP_PROJECT_CODE = "XZSP_PROJECT_CODE";
    /******************************************* 行政审批数据字典结束 ***********************************************/
    // 子项目编号
    public static final String SEIALNUMBER_XZSP_PROJECT_CHILD_CODE = "XZSP_PROJECT_CHILD_CODE";
    // 办件单号
    public static final String SEIALNUMBER_XZSP_BANJIAN_CODE = "XZSP_BANJIAN_SERIAL_NUMBER";
    // 回执单库文书编号
    public static final String SEIALNUMBER_XZSP_RECEIPT_LIB_DOCUMENT_CODE = "XZSP_RECEIPT_LIB_DOCUMENT_CODE";
    // 材料流水号
    public static final String SEIALNUMBER_XZSP_MATERIAL_CODE = "XZSP_MATERIAL_YJCL_CODE";
    /******************************************* 项目单的状态开始 ***********************************************/
    public static final String XZSP_PROJECT_STATUS_ROUGH = "1";
    /******************************************* 行政审批流水号结束 ***********************************************/
    public static final String XZSP_PROJECT_STATUS_CHECK = "2";
    public static final String XZSP_PROJECT_STATUS_BACK = "3";
    public static final String XZSP_PROJECT_STATUS_PASS = "4";
    public static final String XZSP_PROJECT_STATUS_ZZSB_ROUGH = "5";
    public static final String XZSP_PROJECT_STATUS_ZZSB_CHECK = "6";
    /******************************************* 项目单值开始 ***********************************************/
    // 资金来源_财政
    public static final String XZSP_PROJECT_ZJLY_CZ = "1";
    // 资金来源_非财政
    public static final String XZSP_PROJECT_ZJLY_FCZ = "2";
    // 资金来源_未知
    public static final String XZSP_PROJECT_ZJLY_WZ = "3";
    // 项目性质_市政道路
    public static final String XZSP_PROJECT_XMXZ_SZDL = "7";
    /******************************************* 中央对接时间 ***********************************************/
    // 中央对接时间
    public static final String XZSP_ZYDJ_SBDATE = Config.getValue("zydj.sbdate");
    /******************************************* 网上预约开始 ***********************************************/
    // 并联事项编号
    public static final String XZSP_WSYY_BLSXBH = "BLSXBH";
    // 预约号
    public static final String BOOKING_FORM_FIELD_YYH = "YYH";
    // 状态
    public static final String BOOKING_FORM_FIELD_ZT = "ZT";
    // 状态代码
    public static final String BOOKING_FORM_FIELD_ZTDM = "ZTDM";
    // 现场验收日期
    public static final String BOOKING_FORM_FIELD_XCYSRQ = "XCYSRQ";
    // 网上预约市区联动来源
    public static final String BOOKING_FORM_WSYYSQLDLY = "WSYYSQLDLY";

    static {
        flowDefMap.put(FLOW_DEF_ID_XZSP_TEMPLATE, "行政审批串联流程模板");
        flowDefMap.put(FLOW_DEF_ID_XZSP_KYPF_CSYJ, "初审意见告知函");
        flowDefMap.put(FLOW_DEF_ID_XZSP_KYPF_JSZD, "联合技术指导意见");
        flowDefMap.put(FLOW_DEF_ID_XZSP_SGXKJD_ZQYJ, "征求意见");
    }
    /******************************************* 网上预约结束 ***********************************************/
}
