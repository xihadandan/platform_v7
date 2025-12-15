/*
 * @(#)2014-3-18 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2014-3-18.1	zhulh		2014-3-18		Create
 * </pre>
 * @date 2014-3-18
 */
public class XZSPBIZ {

    // 业务单位登记窗口
    public static final String BIZ_ROLE_DJCK = "BUSINESS_SENDER";
    // 业务单位部门窗口
    public static final String BIZ_ROLE_BMCK = "BUSINESS_OWNER";
    // 业务单位发件窗口
    public static final String BIZ_ROLE_FJCK = "BUSINESS_RECIPIENT";

    // 内部申报
    public static final String APPLY_TYPE_INTERNAL = "1";
    // 网上申报
    public static final String APPLY_TYPE_INTENET = "2";
    // 网上内部申报
    // public static final String APPLY_TYPE_INTENET_INTERNAL = "3";
    // 发送信息按钮
    public static final String BTN_ID_SEND_MSG = "B013001091";
    // 修改按钮 显示打印按钮，隐藏发送按钮
    public static final String BTN_ID_MODIFY = "B013001011";
    // 窗口登记
    public static final String TASK_ID_CKDJ = "T_CKDJ";
    // 受理核准
    public static final String TASK_ID_SLHZ = "T_SLHZ";
    // 补件
    public static final String TASK_ID_BJ = "T_BJ";
    // 补件登记
    public static final String TASK_ID_BJDJ = "T_BJDJ";
    // 补件登记确认(用于市区联动及网上申报)
    public static final String TASK_ID_BJDJQR = "T_BJDJQR";
    // 退件
    public static final String TASK_ID_TJ = "T_TJ";
    // 逾期退件
    public static final String TASK_ID_YQTJ = "T_YQTJ";
    // 审批-承办
    public static final String TASK_ID_SPCB = "T_SPCB";
    // 审核
    public static final String TASK_ID_SH = "T_SH";
    // 批准
    public static final String TASK_ID_PZ = "T_PZ";
    // 过程补件
    public static final String TASK_ID_GCBJ = "T_GCBJ";
    // 过程补件登记
    public static final String TASK_ID_GCBJDJ = "T_GCBJDJ";
    // 过程补件登记确认(用于市区联动及网上申报)
    public static final String TASK_ID_GCBJDJQR = "T_GCBJDJQR";
    // 过程补件核准
    public static final String TASK_ID_GCBJHZ = "T_GCBJHZ";
    // 过程退件
    public static final String TASK_ID_GCTJ = "T_GCTJ";
    // 特别程序开始
    public static final String TASK_ID_TSCXKS = "T_TSCXKS";
    // 特别程序结束
    public static final String TASK_ID_TSCXJS = "T_TSCXJS";
    // 出文
    public static final String TASK_ID_CW = "T_CW";
    // 出文确认
    public static final String TASK_ID_CWQR = "T_CWQR";
    // 窗口发件
    public static final String TASK_ID_CKFJ = "T_CKFJ";
    // 统一登记
    public static final String TASK_ID_TYDJ = "T_CKDJ";
    // 送串联审批
    public static final String TASK_ID_SCLSP = "S_SCLSP";
    // 市级单位ID
    public static final String UNIT_ID_CENTER = "426605496";
    // 统一登记单位ID 426605496(市局) 058396802(海沧)
    public static final String UNIT_ID_TYDJ = Config.getValue("center.unit.id");
    // 中央对接开关
    public static final String ZYDJ_OPEN = Config.getValue("zydj.open");
    // 内外网标识
    public static final String HOME_NETWORK = Config.getValue("home.network");
    // 重点项目工程(1、重点工程 2、非重点工程 3、未知)
    public static final Integer PROJECT_PRIORITY_1 = 1;
    public static final Integer PROJECT_PRIORITY_2 = 2;
    public static final Integer PROJECT_PRIORITY_3 = 3;
    // 事项定义->面向用户
    // 个人
    public static final String FACING_USER_PERSONAL = "1";
    // 单位
    public static final String FACING_USER_UNIT = "2";
    // 个人和单位
    public static final String FACING_USER_PERSONAL_AND_UNIT = "3";
    // 出文文件
    public static final String FILES_CW = "CWWJ";
    // 过程退件文件
    public static final String FILES_GCTJ = "GCTJWJ";
    // 过程补件文件
    public static final String FILES_GCBJ = "GCBJWJ";
    // 转报文件
    public static final String FILES_ZB = "ZBWJ";
    // 特别程序文件
    public static final String FILES_TBCX = "SQFJ";
    // 受理意见汇总文件
    public static final String FILES_SLYJHZWJ = "SLYJHZWJ";
    // 出文意见汇总文件
    public static final String FILES_CWYJHZWJ = "CWYJHZWJ";
    /*********************************** 办件结果开始 ***********************************/
    // 办结结果代码
    // 出证办结(正常产生证照、批文的办结)
    public static final String BJJG_CZBJ = "0";
    // 退回办结(退回或驳回申请的办结）
    public static final String BJJG_THBJ = "1";
    // 作废办结(指业务处理上无效的纪录)
    public static final String BJJG_ZHBJ = "2";
    // 删除办结(指录入错误、操作错误等技术上的无效纪录)
    public static final String BJJG_SCBJ = "3";
    // 转报办结(指转报其他单位或上级单位的办结情况)
    public static final String BJJG_ZBBJ = "4";
    // 补交不来办结(指出现补交告知时，通知之后，申请人长期不来补交材料的办结)
    public static final String BJJG_BJBLBJ = "5";
    // 办结(除以上所述情况外的办结)
    public static final String BJJG_BJ = "6";
    /*********************************** 数据类型ID开始 ***********************************/
    // 审批事项库(SX_LIB)
    public static final String TYPE_ID_MATTERS_LIB = "SX_LIB";
    // 审批事项库(SX_LIST)1.2文档
    public static final String SX_LIST = "SX_LIST";
    // 审批事项库详情
    public static final String TYPE_ENTITY_LIB = "SX_ENTITY";
    // 审批结果库(ZZ_LIB)
    public static final String TYPE_ID_LICENSE_LIB = "ZZ_LIB";
    // 申报材料库(CL_LIB)
    public static final String TYPE_ID_MATERIAL_LIB = "CL_LIB";
    // 申报人库(SBR_LIB)
    public static final String TYPE_ID_PERSON_LIB = "SBR_LIB";
    // 单位库(DW_LIB)
    public static final String TYPE_ID_UNIT_LIB = "DW_LIB";
    // 项目库(建设项目、企业设立照后并联审批项目等)(XM_LIB)
    public static final String TYPE_ID_PROJECT_LIB = "XM_LIB";
    // 收费库(SF_LIB)
    public static final String TYPE_ID_CHARGE_LIB = "SF_LIB";
    // 办件库(BJ_LIB)
    public static final String TYPE_ID_BAN_JIAN_LIB = "BJ_LIB";
    // 是否有办件数据更新(BJ_LIB_EXTENT)
    public static final String TYPE_ID_BAN_JIAN_LIB_EXTENT = "BJ_LIB_EXTENT";
    // 前期意见(QQYJ_LIB)
    public static final String TYPE_ID_QQYJ_LIB = "QQYJ_LIB";
    // 初审意见请求
    public static final String TYPE_ID_QQYJSEND_LIB = "SPGC_QQYJNG";
    /*********************************** 办件结果结束 ***********************************/
    // 初审意见反馈
    public static final String TYPE_ID_CQYJFK_LIB = "SPGC_QQYJFK";
    // 前期意见(QQYJFK_LIB)
    public static final String TYPE_ID_QQYJFK_LIB = "QQYJFK_LIB";
    // 文书库(HZD_LIB)
    public static final String TYPE_ID_RECEIPT_LIB = "HZD_LIB";
    // 并联事项部门(HZD_LIB)
    public static final String TYPE_ID_BINLIAN_LIB = "BL_LIB";
    // 办件查询
    // 办件库
    public static final String TYPE_ID_BJ_LIST = "BJ_LIST";
    // 项目下办件
    public static final String TYPE_ID_XM_BJ_LIST = "XM_BJ_LIST";
    // 办件详情
    public static final String TYPE_ID_BJ_ENTITY = "BJ_ENTITY";
    // 证照库
    public static final String TYPE_ID_ZZ_LIST = "ZZ_LIST";
    // 申报者
    public static final String TYPE_ID_SBZ_LIST = "SBZ_LIST";
    // 项目列表
    public static final String TYPE_ID_XM_LIST = "XM_LIST";
    // 项目详情
    public static final String TYPE_ID_XM_ENTITY = "XM_ENTITY";
    // 市区联动
    // 办件汇总
    public static final String TYPE_ID_SQLD_BJHZ = "SQLD_BJHZ";
    // 并联办理汇总
    public static final String TYPE_ID_SQLD_BLBLHZ = "SQLD_BLBLHZ";
    // 办理过程
    public static final String TYPE_ID_SQLD_BLGC = "SQLD_BLGC";
    // 行政审批过程业务数据类型
    // 登记
    public static final String SPGC_DJ = "SPGC_DJ";
    // 受理
    public static final String SPGC_SL = "SPGC_SL";
    // 受理_新
    public static final String SPGC_SL_NEW = "SPGC_SL_NEW";
    // 承办
    public static final String SPGC_CB = "SPGC_CB";
    // 审核
    public static final String SPGC_SH = "SPGC_SH";
    // 批准
    public static final String SPGC_PZ = "SPGC_PZ";
    // 出文
    public static final String SPGC_CW = "SPGC_CW";
    // 新增出文
    public static final String SPGC_NCW = "SPGC_NCW";
    // 新增过补告知
    public static final String SPGC_GCBZ = "SPGC_GCBZ";
    // 新增特别程序开始
    public static final String SPGC_TBCXKS = "SPGC_TBCXKS";
    // 新增不予许可
    public static final String SPGC_BYXK = "SPGC_BYXK";
    // 出文确认
    public static final String SPGC_CWQR = "SPGC_CWQR";
    // 出文确认退回
    public static final String SPGC_CWQRTH = "SPGC_CWQRTH";
    // 取件
    public static final String SPGC_QJ = "SPGC_QJ";
    // 补件告知
    public static final String SPGC_BJGZ = "SPGC_BJGZ";
    // 补件确认
    public static final String SPGC_BJQR = "SPGC_BJQR";
    // 补件确认退回
    public static final String SPGC_BJQRTH = "SPGC_BJQRTH";
    // 补件登记
    public static final String SPGC_BJDJ = "SPGC_BJDJ";
    // 退件
    public static final String SPGC_TJ = "SPGC_TJ";
    // 退件确认
    public static final String SPGC_TJQR = "SPGC_TJQR";
    // 退件确认退回
    public static final String SPGC_TJQRTH = "SPGC_TJQRTH";
    // 过程补件告知
    public static final String SPGC_GCBJGZ = "SPGC_GCBJGZ";
    // 过程补件确认
    public static final String SPGC_GCBJQR = "SPGC_GCBJQR";
    // 过程补件确认退回
    public static final String SPGC_GCBJQRTH = "SPGC_GCBJQRTH";
    // 过程补件登记
    public static final String SPGC_GCBJDJ = "SPGC_GCBJDJ";
    // 过程补件直接出文
    public static final String SPGC_GCBJZJCW = "SPGC_GCBJZJCW";
    // 过程补件直接出文确认
    public static final String SPGC_GCBJZJCWQR = "SPGC_GCBJZJCWQR";
    // 过程补件直接出文确认退回
    public static final String SPGC_GCBJZJCWQRTH = "SPGC_GCBJZJCWQRTH";
    // 过程收件
    public static final String SPGC_GCSJ = "SPGC_GCSJ";
    // 过程退件
    public static final String SPGC_GCTJ = "SPGC_GCTJ";
    // 过程退件确认
    public static final String SPGC_GCTJQR = "SPGC_GCTJQR";
    // 过程退件确认退回
    public static final String SPGC_GCTJQRTH = "SPGC_GCTJQRTH";
    // 特别程序开始
    public static final String SPGC_TSCXKS = "SPGC_TSCXKS";
    // 特别程序确认
    public static final String SPGC_TSCXQR = "SPGC_TSCXQR";
    // 特别程序确认退回
    public static final String SPGC_TSCXQRTH = "SPGC_TSCXQRTH";
    // 特别程序结束
    public static final String SPGC_TSCXJS = "SPGC_TSCXJS";
    // 特别程序直接出文
    public static final String SPGC_TSCXZJCW = "SPGC_TSCXZJCW";
    // 特别程序直接出文确认
    public static final String SPGC_TSCXZJCWQR = "SPGC_TSCXZJCWQR";
    // 特别程序直接出文确认退回
    public static final String SPGC_TSCXZJCWQRTH = "SPGC_TSCXZJCWQRTH";
    // 可研批复->初审意见告知函
    public static final String SPGC_KYPF_CSYJ = "SPGC_KYPF_CSYJ";
    // 可研批复->联合技术指导
    public static final String SPGC_KYPF_JSZD = "SPGC_KYPF_JSZD";
    // 可研批复->征求意见
    public static final String SPGC_KYPF_ZQYJ = "SPGC_KYPF_ZQYJ";
    // 管理员撤回
    public static final String SPGC_CANCEL = "SPGC_CANCEL";
    // 退回（审核、批准环节退回）
    public static final String SPGC_ROLLBACK = "SPGC_ROLLBACK";
    // 退回（审核、批准环节退回）
    public static final String SPGC_NOTICE = "SPGC_NOTICE";
    // 撤件申请
    public static final String SPGC_CJSQ = "SPGC_CJSQ";
    // 撤件审核
    public static final String SPGC_CJSH = "SPGC_CJSH";
    // 办件收回
    public static final String SPGC_BJSH = "SPGC_BJSH";
    // 网上申报 -草稿
    public static final String SPGC_CG = "SPGC_CG";
    // 网上申报 -待预审
    public static final String SPGC_DYS = "SPGC_DYS";
    // 网上申报 -退回
    public static final String SPGC_TH = "SPGC_TH";
    // 网上申报 -登记确认退回
    public static final String SPGC_DJQRTH = "SPGC_DJQRTH";
    // 网上申报 -补件登记确认退回
    public static final String SPGC_BJDJQRTH = "SPGC_BJDJQRTH";
    // 网上申报 -过程补件登记确认退回
    public static final String SPGC_GCBJDJQRTH = "SPGC_GCBJDJQRTH";
    // 网上申报 -并联件登记
    public static final String SPGC_BLDJ = "SPGC_BLDJ";
    // 网上申报
    public static final String WSSB_ID = "WSSB";
    // 网上申报 -消息通知
    public static final String WSSB_XXTZ = "WSSB_XXTZ";
    // 网上申报 -消息通知新
    public static final String WSSB_SPGC_DJSH = "SPGC_DJSH";
    // 网上申报 - 用户注册
    public static final String WSSB_YHZC = "WSSB_YHZC";
    // 网上申报 - 审批过程
    public static final String WSSB_SPGC = "SPGC";
    // 网上申报 -项目审核通过
    public static final String WSSB_XZSP_TG = "XZSP_TG";
    // 网上申报 -项目审核退回
    public static final String WSSB_XZSP_TH = "XZSP_TH";
    // 中介信息对接
    public static final String ZJPJ_XXFK = "PJXX";
    /*******************************网上预审开始******************************/
    // 网上预审 - 草稿
    public static final String SPGC_WSYS_CG = "SPGC_WSYS_CG";
    // 网上预审 - 待预审
    public static final String SPGC_WSYS_DYS = "SPGC_WSYS_DYS";
    // 网上预审 - 部门通过
    public static final String SPGC_WSYS_TG = "SPGC_WSYS_TG";
    // 网上预审 - 部门无需验收
    public static final String SPGC_WSYS_WXYS = "SPGC_WSYS_WXYS";
    // 网上预审 - 部门补正
    public static final String SPGC_WSYS_BZ = "SPGC_WSYS_BZ";
    /*******************************网上预约开始******************************/
    // 网上预约_申请信息从表字段 - 事项
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_SX = "SX";
    // 网上预约_申请信息从表字段 - 预审情况代码
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_YSQKDM = "YSQKDM";
    // 网上预约_申请信息从表字段 - 批次真实值
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_PCZSZ = "PCZSZ";
    // 网上预约_申请信息从表字段 - 验收部门真实值
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_YSBMZSZ = "YSBMZSZ";
    // 网上预约_申请信息从表字段 - 参验人员1
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_CYRY1 = "CYRY1";
    // 网上预约_申请信息从表字段 - 联系电话1
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_LXDH1 = "LXDH1";
    // 网上预约_申请信息从表字段 - 参验人员2
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_CYRY2 = "CYRY2";
    // 网上预约_申请信息从表字段 - 联系电话2
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_LXDH2 = "LXDH2";
    // 网上预约_申请信息从表字段 - 参验人员3
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_CYRY3 = "CYRY3";
    // 网上预约_申请信息从表字段 - 联系电话3
    public static final String UF_XZSP_WSYYSQD_SQXX_FIELD_LXDH3 = "LXDH3";
    // 网上预约字段 - 预约批次真实值
    public static final String UF_XZSP_WSYYSQD_FIELD_YYPCZSZ = "YYPCZSZ";
    // 网上预约字段 - 所属行业真实值
    public static final String UF_XZSP_WSYYSQD_FIELD_SSXYZSZ = "SSXYZSZ";
    // 网上预约字段 - 所属行业显示值
    public static final String UF_XZSP_WSYYSQD_FIELD_SSXYXSZ = "SSXYXSZ";
    /*******************************网上预审结束******************************/
    // 网上预约字段 - 已反馈单位代码
    public static final String UF_XZSP_WSYYSQD_FIELD_YFKDWDM = "YFKDWDM";
    // 网上预约字段 - 项目地址
    public static final String UF_XZSP_WSYYSQD_FIELD_XMDZ = "XMDZ";
    // 网上预约字段 - 窗口申报时间
    public static final String UF_XZSP_WSYYSQD_FIELD_CKSBSJ = "CKSBSJ";
    // 网上预约字段 - 窗口申报开始日期
    public static final String UF_XZSP_WSYYSQD_FIELD_CKSBKSRQ = "CKSBKSRQ";
    // 网上预约字段 - 窗口申报截止日期
    public static final String UF_XZSP_WSYYSQD_FIELD_CKSBJZRQ = "CKSBJZRQ";
    // 网上预约字段 - 现场验收时间
    public static final String UF_XZSP_WSYYSQD_FIELD_XCYSSJ = "XCYSSJ";
    // 网上预约字段 - 现场验收日期
    public static final String UF_XZSP_WSYYSQD_FIELD_XCYSRQ = "XCYSRQ";
    // 网上预约字段 - 状态
    public static final String UF_XZSP_WSYYSQD_FIELD_ZT = "ZT";
    // 网上预约字段 - 状态代码
    public static final String UF_XZSP_WSYYSQD_FIELD_ZTDM = "ZTDM";
    // 网上预约字段 - 是否加入黑名单
    public static final String UF_XZSP_WSYYSQD_FIELD_SFJRHMD = "SFJRHMD";
    // 网上预约字段 - 牵头部门真实值
    public static final String UF_XZSP_WSYYSQD_FIELD_QTBMZSZ = "QTBMZSZ";
    // 网上预约字段 - 牵头联系人
    public static final String UF_XZSP_WSYYSQD_FIELD_QTLXR = "QTLXR";
    // 网上预约字段 - 联系人电话
    public static final String UF_XZSP_WSYYSQD_FIELD_LXRDH = "LXRDH";
    // 网上预约字段 - 乘车方式显示值
    public static final String UF_XZSP_WSYYSQD_FIELD_CCFSXSZ = "CCFSXSZ";
    // 网上预约字段 - 乘车地点
    public static final String UF_XZSP_WSYYSQD_FIELD_CCDD = "CCDD";
    // 网上预约字段 - 乘车时间
    public static final String UF_XZSP_WSYYSQD_FIELD_CCSJ = "CCSJ";
    // 网上预约字段 - 预约号
    public static final String UF_XZSP_WSYYSQD_FIELD_YYH = "YYH";
    // 网上预约字段 - 项目名称
    public static final String UF_XZSP_WSYYSQD_FIELD_XMMC = "XMMC";
    // 网上预约字段 - 项目编号
    public static final String UF_XZSP_WSYYSQD_FIELD_XMBH = "XMBH";
    // 网上预约字段 - 项目长编号
    public static final String UF_XZSP_WSYYSQD_FIELD_XMCBH = "XMCBH";
    // 网上预约字段 - 联系人
    public static final String UF_XZSP_WSYYSQD_FIELD_LXR = "LXR";
    // 网上预约字段 - 手机
    public static final String UF_XZSP_WSYYSQD_FIELD_SJ = "SJ";
    // 网上预约_办理过程从表字段 - 单位名称
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_DWMC = "dwmc";
    // 网上预约_办理过程从表字段 - 人员名称
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_RYMC = "rymc";
    // 网上预约_办理过程从表字段 - 人员代码
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_RYDM = "rydm";
    // 网上预约_办理过程从表字段 - 操作时间
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_CZSJ = "czsj";
    // 网上预约_办理过程从表字段 - 操作名称
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_CZMC = "czmc";
    // 网上预约_办理过程从表字段 - 内容
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_NR = "nr";
    // 网上预约_办理过程从表字段 - 创建时间
    public static final String UF_XZSP_WSYYSQD_BLGC_FIELD_CREATE_TIME = "create_time";
    // 网上预约_黑名单从表字段 - 操作时间
    public static final String UF_XZSP_WSYYSQD_HMD_FIELD_CZSJ = "CZSJ";
    // 网上预约_黑名单从表字段 - 原因
    public static final String UF_XZSP_WSYYSQD_HMD_FIELD_CZYY = "CZYY";
    // 网上预约单
    public static final String UF_XZSP_WSYYSQD = "uf_xzsp_wsyysqd";
    // 网上预约单_申请信息
    public static final String UF_XZSP_WSYYSQD_SQXX = "uf_xzsp_wsyysqd_sqxx";
    // 网上预约单_办理过程
    public static final String UF_XZSP_WSYYSQD_GLGC = "uf_xzsp_wsyysqd_blgc";
    // 网上预约单_黑名单
    public static final String UF_XZSP_WSYYSQD_HMD = "uf_xzsp_wsyysqd_hmd";
    // 网上预约 - 预约号流水号定义
    public static final String WSYS_BOOKING_CODE = "WSYS_BOOKING_CODE";
    // 网上预约 - 业主预约
    public static final String SPGC_WSYY_YY = "SPGC_WSYY_YY";
    // 网上预约 - 变更预约时间
    public static final String SPGC_WSYY_BGYYSJ = "SPGC_WSYY_BGYYSJ";
    // 网上预约 - 取消预约
    public static final String SPGC_WSYY_QXYY = "SPGC_WSYY_QXYY";
    // 网上预约 - 牵头部门取消预约
    public static final String SPGC_WSYY_QTBMQXYY = "SPGC_WSYY_QTBMQXYY";
    // 网上预约 - 反馈参验人员
    public static final String SPGC_WSYY_FKCYRY = "SPGC_WSYY_FKCYRY";
    // 网上预约 - 验收安排确认
    public static final String SPGC_WSYY_YSAPQR = "SPGC_WSYY_YSAPQR";
    // 网上预约 - 解冻
    public static final String SPGC_WSYY_JD = "SPGC_WSYY_JD";
    // // 网上预约 - 业主预约
    // public static final String SPGC_WSYY_YY = "SPGC_WSYY_YY";
    // // 网上预约 - 业主预约
    // public static final String SPGC_WSYY_YY = "SPGC_WSYY_YY";
    // 业主预约
    public static final String TASK_ID_T_YZYY = "T_YZYY";
    // 参验部门 反馈参验人员
    public static final String TASK_ID_T_FKCYRY = "T_FKCYRY";
    // 牵头部门验收安排确认
    public static final String TASK_ID_T_YSAPQR = "T_YSAPQR";
    /*******************************网上预约结束******************************/

    // 企业设立->待审批
    public static final String SPGC_QYSL_DSP = "待审批";
    public static final Map<String, String> spgcMap = new HashMap<String, String>();
    public static final Map<String, String> taskCodeMap = new HashMap<String, String>();
    /*********************************** 需并联消息通知的审批过程开始  ***********************************/
    public static final Map<String, String> parallelNoticeActionTypeMap = new HashMap<String, String>();
    /********************************* 可研批复相关单位开始 *********************************/
    public static final Map<String, String> kypfCsyjUnitMap = new HashMap<String, String>();
    public static final Map<String, String> kypfjszdUnitMap = new HashMap<String, String>();
    public static final List<String> kypfMattersCode = new ArrayList<String>();
    // 工程规划许可阶段
    public static final List<String> gcghxkjdMattersCode = new ArrayList<String>();
    // 施工许可阶段
    public static final List<String> sgxkjdMattersCode = new ArrayList<String>();
    // 用地规划许可阶段（建筑类）
    public static final List<String> ydghxkMattersCode = new ArrayList<String>();
    // 用地规划许可阶段（多规合一）
    public static final List<String> ydghxkdghyMattersCode = new ArrayList<String>();
    // 用地规划许可阶段（多规合一）
    public static final Map<String, Integer> ydghxkcsyjMattersCnbjr = new HashMap<String, Integer>();
    // 用地规划许可阶段（多规合一）
    public static final Map<String, String> ydghxkcsyjFrontMatters = new HashMap<String, String>();
    // 施工图审查阶段
    public static final List<String> sgtscjdMattersCode = new ArrayList<String>();
    // 前期意见系统参数配置_用地规划许可阶段并联事项编号
    public static final String qqyj_ydghxkjd_matters_code = "qqyj_ydghxkjd_matters_code";
    // 前期意见系统参数配置_用地规划许可阶段(多规合一)并联事项编号
    public static final String qqyj_ydghxkjd_dghy_matters_code = "qqyj_ydghxkjd_dghy_matters_code";
    // 前期意见系统参数配置_工程规划许可阶段并联事项编号
    public static final String qqyj_gcghxkjd_matters_code = "qqyj_gcghxkjd_matters_code";
    // 前期意见系统参数配置_可研批复阶段并联事项编号
    public static final String qqyj_kypfjd_matters_code = "qqyj_kypfjd_matters_code";

    /*********************************** 数据类型ID结束 ***********************************/
    // 前期意见系统参数配置_施工许可阶段并联事项编号
    public static final String qqyj_sgxkjd_matters_code = "qqyj_sgxkjd_matters_code";
    // 前期意见系统参数配置_施工图审查阶段并联事项编号
    public static final String qqyj_sgtscjd_matters_code = "qqyj_sgtscjd_matters_code";
    /*********************************** 需并联消息通知的审批过程结束  ***********************************/
    // 前期意见系统参数配置_初审意见单位ID
    public static final String qqyj_csyj_unitId = "qqyj_csyj_unitId";
    // 前期意见系统参数配置_技术指导单位ID
    public static final String qqyj_jszd_unitId = "qqyj_jszd_unitId";
    // 前期意见系统参数配置_规划设计条件核定事项编号
    public static final String qqyj_csyj_matters_code_ghsjtjhd = "qqyj_csyj_matters_code_ghsjtjhd";
    // 前期意见系统参数配置_规划设计条件核定工作日
    public static final String qqyj_csyj_gzr_ghsjtjhd = "qqyj_csyj_gzr_ghsjtjhd";
    // 前期意见系统参数配置_建设项目选址许可事项编号
    public static final String qqyj_csyj_matters_code_jsxmxzxk = "qqyj_csyj_matters_code_jsxmxzxk";
    // 前期意见系统参数配置_建设项目选址许可工作日
    public static final String qqyj_csyj_gzr_jsxmxzxk = "qqyj_csyj_gzr_jsxmxzxk";
    // 前期意见系统参数配置_政府投资项目建议书审批事项编号
    public static final String qqyj_csyj_matters_code_zftzxmjys = "qqyj_csyj_matters_code_zftzxmjys";
    // 前期意见系统参数配置_政府投资项目建议书审批工作日
    public static final String qqyj_csyj_gzr_zftzxmjys = "qqyj_csyj_gzr_zftzxmjys";
    // 前期意见系统参数配置_建设项目用地预审事项编号
    public static final String qqyj_csyj_matters_code_jsxmydys = "qqyj_csyj_matters_code_jsxmydys";
    // 前期意见系统参数配置_建设项目用地预审工作日
    public static final String qqyj_csyj_gzr_jsxmydys = "qqyj_csyj_gzr_jsxmydys";
    // 前期意见系统参数配置_建设项目用地规划许可（财政投融资项目）事项编号
    public static final String qqyj_csyj_matters_code_jsxmydghxk = "qqyj_csyj_matters_code_jsxmydghxk";
    // 前期意见系统参数配置_建设项目用地规划许可（财政投融资项目）工作日
    public static final String qqyj_csyj_gzr_jsxmydghxk = "qqyj_csyj_gzr_jsxmydghxk";
    /*********************************** 三规合一开始 ***********************************/
    public static final String AUGURIT_SXBH_CZTR = "350200004139456A160100";// 建设项目用地规划许可（财政投融资项目）
    public static final String AUGURIT_SXBH_GKCR = "350200004139456A160200";// 建设项目用地规划许可（公开出让用地备案类项目）
    public static final String AUGURIT_SXBH_CCRFGC = "350200004138824A050000";// 拆除人防工程及配套设施(人防)
    public static final String AUGURIT_SXBH_JSXMJG = "350200004139501A090000";// 建设项目竣工环境保护验收许可（环保）
    public static final String AUGURIT_SXBH_CYBJYS = "35020000413820XN040000";// 产业布局预审（发改）
    public static final String AUGURIT_SXBH_CZXTRZJSXMJSSH = "350200426602287N040000";// 财政性投融资建设项目结算审核（财审中心）
    public static final String AUGURIT_SXBH_BBJSYD = "35020000413935XN010000";// 补办建设用地（国土局）
    public static final String BJ_MATTERIAL_FORMID = XZSPConstants.APPLY_MATTERS_MATERIAL_TABLE_ID;// 办件申请单应该提交材料从表
    public static final String BJ_PROJECTREPO_FORMID = XZSPConstants.PROJECT_LIB_TABLE_NAME;// 项目库
    public static final String BJ_BJCL_FORMID = XZSPConstants.APPLY_MATTERS_BUJIAN_MATERIAL_TABLE_ID;// 行政审批_办件申请单从表_补件材料
    public static final String BJ_FJCL_FORMID = XZSPConstants.APPLY_MATTERS_ADDITION_MATERIAL_TABLE_ID;// 行政审批_办件申请单从表_附加材料
    public static final String BJ_GCT_FORMID = XZSPConstants.APPLY_MATTERS_ENGINEERING_DRAWING_TABLE_ID;// 行政审批_办件申请单从表_工程图
    public static final String BJ_MATTERIAL_FIELDNAME = "DJFJ";// 办件申请单应该提交材料从表
    public static final String BJ_MAINTABLE_FIELDNAME = "CWWJ";// 办件单主表出文文件字段
    public static final String AUGURIT_UNIT_CODE = "004139456";// 规划委组织机构代码
    /*********************************** 上报电子监察开始 ***********************************/
    public static final String T_EX_XZXK_WANGSHANGSHOULI = "T_EX_XZXK_WANGSHANGSHOULI";// 网上受理（登记）
    public static final String T_EX_XZXK_SHOULI = "T_EX_XZXK_SHOULI";// 受理（收件）
    /********************************* 可研批复相关单位结束 *********************************/
    public static final String T_EX_XZXK_SHENPIGUOCHENG = "T_EX_XZXK_SHENPIGUOCHENG";// 审批过程（承办、审核、批准）
    public static final String T_EX_XZXK_BUJIAOGAOZHI = "T_EX_XZXK_BUJIAOGAOZHI";// 补交告知（补件、过程补件）
    public static final String T_EX_XZXK_BUJIAOSHOULI = "T_EX_XZXK_BUJIAOSHOULI";// 补交受理（过程收件）
    public static final String T_EX_XZXK_TEBIECHENGXUSHENQING = "T_EX_XZXK_TEBIECHENGXUSHENQING";// 特别程序申请
    public static final String T_EX_XZXK_TEBIECHENGXUJIEGUO = "T_EX_XZXK_TEBIECHENGXUJIEGUO";// 特别程序结果
    public static final String T_EX_XZXK_WENSHUZHIZUO = "T_EX_XZXK_WENSHUZHIZUO";// 文书制作
    public static final String T_EX_XZXK_SONGDA = "T_EX_XZXK_SONGDA";// 送达（取件）
    public static final String T_EX_XZXK_BANJIE = "T_EX_XZXK_BANJIE";// 办结
    public static final String ZYDJ_XZQH_SJ = "350200";
    public static final String ZYDJ_XZQH_HC = "350205";
    public static final String ZYDJ_XZQH_XA = "350213";
    public static final String ZYDJ_XZQH_JM = "350211";
    public static final String ZYDJ_XZQH_TA = "350212";
    public static final String ZYDJ_XZQH_SM = "350203";
    public static final String ZYDJ_XZQH_HL = "350206";
    /*********************************** 三规合一结束 ***********************************/
    public static final String SJ_AREA_CODE = "426605496";
    public static final String HC_AREA_CODE = "058396802";
    public static final String XA_AREA_CODE = "594966565";
    public static final String JM_AREA_CODE = "079360585";
    public static final String TA_AREA_CODE = "78419632X";
    public static final String SM_AREA_CODE = "079378080";
    public static final String HL_AREA_CODE = "065853266";
    /*********************************** 上报电子监察结束 ***********************************/
    // 中央对接开关
    public static final String ZYDJ_ON_OFF = "on";
    /************************************市区联动对接操作***************************************/
    public static final String SQLD_ADD = "ADD";
    public static final String SQLD_DELETE = "DELETE";
    public static final Map<String, String> XZFQMAP = new HashMap<String, String>();
    /************************************办件操作撤件***************************************/
    // 1、撤件申请 2、撤件登记 3、撤件登记通过4、撤件登记不通过 5、撤件审核通过 6、撤件审核不通过
    public static final Integer BANJIAN_BACK_STATUS_1 = 1;
    public static final Integer BANJIAN_BACK_STATUS_2 = 2;
    public static final Integer BANJIAN_BACK_STATUS_3 = 3;
    public static final Integer BANJIAN_BACK_STATUS_4 = 4;
    public static final Integer BANJIAN_BACK_STATUS_5 = 5;
    public static final Integer BANJIAN_BACK_STATUS_6 = 6;
    // 撤回
    public static final String CJ_ACTIONTYPE_CH = "CH";
    // 撤件申请
    public static final String CJ_ACTIONTYPE_CJSQ = "CJSQ";
    // 撤件登记
    public static final String CJ_ACTIONTYPE_CJDJ = "CJDJ";
    // 撤件登记不通过
    public static final String CJ_ACTIONTYPE_CJDJBTG = "CJDJBTG";
    // 撤件审核通过
    public static final String CJ_ACTIONTYPE_CJSHTG = "CJSHTG";
    // 撤件审核不通过
    public static final String CJ_ACTIONTYPE_CJSHBTG = "CJSHBTG";
    // 网上申报人员
    // public static final String ROLE_INTENET = "2";
    // 管理员角色
    public static String ROLE_ADMIN = "ROLE_ADMIN";
    // 登记窗口角色
    public static String ROLE_DJCK = "ROLE_XZSP_DJCK";
    // 部门窗口角色
    public static String ROLE_BMCK = "ROLE_XZSP_BMCK";
    // 发件窗口角色
    public static String ROLE_FJCK = "ROLE_XZSP_FJCK";
    // 中心人员角色
    public static String ROLE_ZXRY = "ROLE_XZSP_ZXRY";
    // 业务管理人员角色
    public static String ROLE_YWGL = "ROLE_XZSP_YWGL";
    // 网上申报人员角色
    public static String ROLE_INTERNET = "ROLE_XZSP_INTERNET";
    // 自助申报人员角色
    public static String ROLE_ZZSB = "ROLE_XZSP_ZZSBRY";
    // 行政审批-规划局项目生成测试人员
    public static String ROLE_GHCS = "ROLE_XZSP_GHJ_TEST";
    // 行政审批-督查人员
    public static String ROLE_DCRY = "ROLE_XZSP_DC";
    // 行政审批-项目库角色
    public static String ROLE_XMKRY = "ROLE_XZSP_XMKRY";
    // 行政审批-文书删除角色
    public static String ROLE_XZSP_WSSC = "ROLE_XZSP_WSSC";
    // 行政审批-策划项目
    public static String ROLE_PLAN_PROJECT = "ROLE_PLAN_PROJECT";

    static {
        spgcMap.put(SPGC_DJ, SPGC_DJ);
        spgcMap.put(SPGC_SL, SPGC_SL);
        spgcMap.put(SPGC_SL_NEW, SPGC_SL_NEW);
        spgcMap.put(SPGC_CB, SPGC_CB);
        spgcMap.put(SPGC_SH, SPGC_SH);
        spgcMap.put(SPGC_PZ, SPGC_PZ);
        spgcMap.put(SPGC_CW, SPGC_CW);
        spgcMap.put(SPGC_CWQR, SPGC_CWQR);
        spgcMap.put(SPGC_CWQRTH, SPGC_CWQRTH);
        spgcMap.put(SPGC_QJ, SPGC_QJ);
        spgcMap.put(SPGC_BJGZ, SPGC_BJGZ);
        spgcMap.put(SPGC_BJQR, SPGC_BJQR);
        spgcMap.put(SPGC_BJQRTH, SPGC_BJQRTH);
        spgcMap.put(SPGC_BJDJ, SPGC_BJDJ);
        spgcMap.put(SPGC_TJ, SPGC_TJ);
        spgcMap.put(SPGC_TJQR, SPGC_TJQR);
        spgcMap.put(SPGC_TJQRTH, SPGC_TJQRTH);
        spgcMap.put(SPGC_GCBJGZ, SPGC_GCBJGZ);
        spgcMap.put(SPGC_GCBJQR, SPGC_GCBJQR);
        spgcMap.put(SPGC_GCBJQRTH, SPGC_GCBJQRTH);
        spgcMap.put(SPGC_GCBJDJ, SPGC_GCBJDJ);
        spgcMap.put(SPGC_GCBJZJCW, SPGC_GCBJZJCW);
        spgcMap.put(SPGC_GCBJZJCWQR, SPGC_GCBJZJCWQR);
        spgcMap.put(SPGC_GCBJZJCWQRTH, SPGC_GCBJZJCWQRTH);
        spgcMap.put(SPGC_GCSJ, SPGC_GCSJ);
        spgcMap.put(SPGC_GCTJ, SPGC_GCTJ);
        spgcMap.put(SPGC_GCTJQR, SPGC_GCTJQR);
        spgcMap.put(SPGC_GCTJQRTH, SPGC_GCTJQRTH);
        spgcMap.put(SPGC_TSCXKS, SPGC_TSCXKS);
        spgcMap.put(SPGC_TSCXQR, SPGC_TSCXQR);
        spgcMap.put(SPGC_TSCXQRTH, SPGC_TSCXQRTH);
        spgcMap.put(SPGC_TSCXJS, SPGC_TSCXJS);
        spgcMap.put(SPGC_TSCXZJCW, SPGC_TSCXZJCW);
        spgcMap.put(SPGC_TSCXZJCWQR, SPGC_TSCXZJCWQR);
        spgcMap.put(SPGC_TSCXZJCWQRTH, SPGC_TSCXZJCWQRTH);
        spgcMap.put(SPGC_ROLLBACK, SPGC_ROLLBACK);
        spgcMap.put(SPGC_BJDJQRTH, SPGC_BJDJQRTH);
        spgcMap.put(SPGC_GCBJDJQRTH, SPGC_GCBJDJQRTH);
        spgcMap.put(SPGC_NOTICE, SPGC_NOTICE);

        // 管理员撤回
        spgcMap.put(SPGC_CANCEL, SPGC_CANCEL);
        spgcMap.put(WorkFlowOperation.GOTO_TASK, WorkFlowOperation.GOTO_TASK);

        spgcMap.put(SPGC_KYPF_CSYJ, SPGC_KYPF_CSYJ);
        spgcMap.put(SPGC_KYPF_JSZD, SPGC_KYPF_JSZD);
        spgcMap.put(SPGC_KYPF_ZQYJ, SPGC_KYPF_ZQYJ);

        // 网上申报
        spgcMap.put(WSSB_XXTZ, WSSB_XXTZ);

        // 市区联动
        spgcMap.put(XZSPBIZ.TYPE_ID_LICENSE_LIB, XZSPBIZ.TYPE_ID_LICENSE_LIB);
    }

    static {
        taskCodeMap.put(SPGC_DJ, "02");
        taskCodeMap.put(SPGC_SL, "03");
        taskCodeMap.put(SPGC_SL_NEW, "03");
        taskCodeMap.put(SPGC_CB, "04");
        taskCodeMap.put(SPGC_SH, "05");
        taskCodeMap.put(SPGC_PZ, "06");
        taskCodeMap.put(SPGC_CW, "07");
        taskCodeMap.put(SPGC_CWQR, "08");
        taskCodeMap.put(SPGC_CWQRTH, "23");
        taskCodeMap.put(SPGC_QJ, "09");
        taskCodeMap.put(SPGC_BJGZ, "10");
        taskCodeMap.put(SPGC_BJQR, "11");
        taskCodeMap.put(SPGC_BJDJ, "24");
        taskCodeMap.put(SPGC_TJ, "12");
        taskCodeMap.put(SPGC_TJQR, "13");
        taskCodeMap.put(SPGC_GCBJGZ, "14");
        taskCodeMap.put(SPGC_GCBJQR, "15");
        taskCodeMap.put(SPGC_GCBJDJ, "25");
        taskCodeMap.put(SPGC_GCBJZJCW, "17");
        taskCodeMap.put(SPGC_GCSJ, "16");
        taskCodeMap.put(SPGC_GCTJ, "18");
        taskCodeMap.put(SPGC_GCTJQR, "19");
        taskCodeMap.put(SPGC_TSCXKS, "20");
        taskCodeMap.put(SPGC_TSCXQR, "26");
        taskCodeMap.put(SPGC_TSCXJS, "21");
        taskCodeMap.put(SPGC_TSCXZJCW, "22");
    }

    static {
        parallelNoticeActionTypeMap.put(SPGC_SL, SPGC_SL);
        parallelNoticeActionTypeMap.put(SPGC_SL_NEW, SPGC_SL_NEW);
        parallelNoticeActionTypeMap.put(SPGC_BJQR, SPGC_BJQR);
        parallelNoticeActionTypeMap.put(SPGC_TJQR, SPGC_TJQR);
        parallelNoticeActionTypeMap.put(SPGC_GCSJ, SPGC_GCSJ);
        parallelNoticeActionTypeMap.put(SPGC_GCBJQR, SPGC_GCBJQR);
        parallelNoticeActionTypeMap.put(SPGC_GCTJQR, SPGC_GCTJQR);
        parallelNoticeActionTypeMap.put(SPGC_TSCXQR, SPGC_TSCXQR);
        parallelNoticeActionTypeMap.put(SPGC_CWQR, SPGC_CWQR);
        parallelNoticeActionTypeMap.put(SPGC_TSCXZJCWQR, SPGC_TSCXZJCWQR);
        parallelNoticeActionTypeMap.put(SPGC_GCBJZJCWQR, SPGC_GCBJZJCWQR);
        parallelNoticeActionTypeMap.put(SPGC_CB, SPGC_CB);
    }

    static {
        kypfCsyjUnitMap.put("004139501", "环保局");
        kypfCsyjUnitMap.put("004142022", "水利局");
        kypfCsyjUnitMap.put("004138250", "经发局");
        kypfCsyjUnitMap.put("004139704", "海洋渔业局");
        kypfCsyjUnitMap.put("004138453", "建设局");
        kypfCsyjUnitMap.put("004139755", "交通局");
        kypfCsyjUnitMap.put("004136562", "港口局");

        kypfjszdUnitMap.put("678282419", "国家电网");
        kypfjszdUnitMap.put("737896235", "水务集团");
        kypfjszdUnitMap.put("761758158", "通信管理局");
        kypfjszdUnitMap.put("79805412X", "华润燃气");
        kypfjszdUnitMap.put("010248", "消防");

        kypfMattersCode.add("350200BL0102");
        kypfMattersCode.add("350200BL0202");

        gcghxkjdMattersCode.add("350200BL0302");

        // 施工许可阶段（建筑类）
        sgxkjdMattersCode.add("350200BL0104");
        // 施工许可阶段（线性工程）
        sgxkjdMattersCode.add("350200BL0204");
        // 施工许可阶段（公开出让用地备案类项目）
        sgxkjdMattersCode.add("350200BL0304");

        // 用地规划许可阶段（建筑类）
        ydghxkMattersCode.add("350200BL0101");

        // 用地规划许可阶段（多规合一）
        ydghxkdghyMattersCode.add("350200BL0106");
        ydghxkdghyMattersCode.add("350200BL0107");

        // 前置
        ydghxkcsyjMattersCnbjr.put("350200004139456N020000", 3);// 厦门市规划委员会>规划设计条件核定
        ydghxkcsyjMattersCnbjr.put("350200004139456A010000", 3);// 厦门市规划委员会>建设项目选址许可
        ydghxkcsyjMattersCnbjr.put("35020000413820XN110000", 3);// 厦门市发展和改革委员会>政府投资项目建议书审批
        ydghxkcsyjMattersCnbjr.put("35020000413935XN090000", 7); // 厦门市国土资源与房产管理局>建设项目用地预审
        ydghxkcsyjMattersCnbjr.put("350200004139456A160100", 3);// 厦门市规划委员会>建设项目用地规划许可（财政投融资项目）

        // 施工图审查阶段
        sgtscjdMattersCode.add("350200BL0103");

        // 网上预审
        spgcMap.put(SPGC_WSYS_CG, SPGC_WSYS_CG);
        spgcMap.put(SPGC_WSYS_DYS, SPGC_WSYS_DYS);
    }

    static {
        XZFQMAP.put(SJ_AREA_CODE, ZYDJ_XZQH_SJ);
        XZFQMAP.put(HC_AREA_CODE, ZYDJ_XZQH_HC);
        XZFQMAP.put(XA_AREA_CODE, ZYDJ_XZQH_XA);
        XZFQMAP.put(JM_AREA_CODE, ZYDJ_XZQH_JM);
        XZFQMAP.put(TA_AREA_CODE, ZYDJ_XZQH_TA);
        XZFQMAP.put(SM_AREA_CODE, ZYDJ_XZQH_SM);
        XZFQMAP.put(HL_AREA_CODE, ZYDJ_XZQH_HL);
    }
}
