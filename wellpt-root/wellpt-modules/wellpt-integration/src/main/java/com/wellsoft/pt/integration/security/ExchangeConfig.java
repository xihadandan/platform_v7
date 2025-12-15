package com.wellsoft.pt.integration.security;

import com.wellsoft.context.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ExchangeConfig {
    public static final String EXCHANGE_BUSINESS_TYPE = "SSGL";

    public static final String EXCHANGE_MESSAGE_SSZT = "EXCHANGE_RECEIVE_SSZT";

    public static final String EXCHANGE_MESSAGE_XZXK = "EXCHANGE_RECEIVE_XZXK";

    public static final String EXCHANGE_MESSAGE_OTHER = "EXCHANGE_RECEIVE_OTHER";

    public static final String EXCHANGE_MESSAGE_CANCEL_SSZT = "EXCHANGE_CANCEL_SSZT";

    public static final String EXCHANGE_MESSAGE_CANCEL_XZXK = "EXCHANGE_CANCEL_XZXK";

    public static final String EXCHANGE_MESSAGE_CANCEL_OTHER = "EXCHANGE_CANCEL_OTHER";

    public static final String EXCHANGE_JCR_MODLE_NAME = "EXCHANGE_UPLOAD_FILE";

    public static final String EXCHANGE_ZCH = "ZCH";

    public static final String EXCHANGE_ZTMC = "ZTMC";

    public static final String EXCHANGE_FDDBR = "FDDBR";

    public static final String EXCHANGE_JYCS = "JYCS";

    public static final String EXCHANGE_ZTLX = "ZTLX";

    public static final String DATA_ITEM_DATA_ID = "DATA_ID";// 统一查询号
    public static final String DATA_ITEM_REC_VER = "REC_VER";// 数据版本号

    // 商事主体登记信息
    public static final String TYPE_ID_SSXX_ZTDJ = "004140203SZ";
    public static final String SSXX_XGXK_FORM_NAME = "uf_ssxx_xgxk";
    public static final String SSXX_ZTDJ_ZCH = "ZCH";// 注册号
    public static final String SSXX_ZTDJ_ZTMC = "ZTMC";// 商事主体名称
    public static final String SSXX_ZTDJ_LSZTMC = "LSZTMC";// 隶属商事主体名称
    public static final String SSXX_ZTDJ_FDDBR = "FDDBR";// 法定代表人(负责人)
    public static final String SSXX_ZTDJ_FDDBRZWDM = "FDDBRZWDM";// 法定代表人职务代码
    public static final String SSXX_ZTDJ_FDDBRZWMC = "FDDBRZWMC";// 法定代表人职务名称
    public static final String SSXX_ZTDJ_FDDBRZJHM = "FDDBRZJHM";// 法定代表人证件号码
    public static final String SSXX_ZTDJ_JYHW = "JYHW";// 经营范围
    public static final String SSXX_ZTDJ_XKJYHW = "XKJYHW";// 许可经营范围
    public static final String SSXX_ZTDJ_ZS = "ZS";// 住所
    public static final String SSXX_ZTDJ_LXDH = "LXDH";// 联系电话
    public static final String SSXX_ZTDJ_LLRXX = "LLRXX";// 联络人
    public static final String SSXX_ZTDJ_RJCZE = "RJCZE";// 认缴出资额
    public static final String SSXX_ZTDJ_RJCZEDW = "RJCZEDW";// 认缴出资额单位及币种
    public static final String SSXX_ZTDJ_SJCZE = "SJCZE";// 实缴出资额
    public static final String SSXX_ZTDJ_SJCZEDW = "SJCZEDW";// 实缴出资额单位及币种
    public static final String SSXX_ZTDJ_ZTLX = "ZTLX";// 商事主体类型
    public static final String SSXX_ZTDJ_ZTLXDM = "ZTLXDM";// 商事主体类型代码
    public static final String SSXX_ZTDJ_CLRQ = "CLRQ";// 成立日期
    public static final String SSXX_ZTDJ_YYQX = "YYQX";// 营业期限
    public static final String SSXX_ZTDJ_HZRQ = "HZRQ";// 核准日期
    public static final String SSXX_ZTDJ_DJJG = "DJJG";// 登记机关
    public static final String SSXX_ZTDJ_DJJGDM = "DJJGDM";// 登记机关代码
    public static final String SSXX_ZTDJ_NBQK = "NBQK";// 年报情况
    public static final String SSXX_ZTDJ_ZTZT = "ZTZT";// 主体状态
    public static final String SSXX_ZTDJ_ZTZTDM = "ZTZTDM";// 主体状态代码
    public static final String SSXX_ZTDJ_ZC = "ZC";// 章程
    public static final String SSXX_ZTDJ_QTWJ = "QTWJ";// 其他文件
    // 商事登记_主体的经营场所
    public static final String USER_FORM_SSXX_JYCS = "uf_ssxx_jycs"; // 经营场所
    public static final String SSXX_JYCS_JYCS = "JYCS"; // 经营场所
    // 商事登记_主体的股东信息
    public static final String USER_FORM_SSXX_GDXX = "uf_ssxx_gdxx";// 股东信息
    public static final String SSXX_GDXX_GDMC = "GDMC"; // 股东名称
    public static final String SSXX_GDXX_GDLX = "GDLX"; // 股东类型
    public static final String SSXX_GDXX_GDLXDM = "GDLXDM"; // 股东类型代码
    public static final String SSXX_GDXX_GB = "GB"; // 国别
    public static final String SSXX_GDXX_GBDM = "GBDM"; // 国别代码
    public static final String SSXX_GDXX_RJCZE = "RJCZE"; // 认缴出资额
    public static final String SSXX_GDXX_RJCZEDW = "RJCZEDW"; // 认缴出资额单位及币种
    public static final String SSXX_GDXX_SJCZE = "SJCZE"; // 实缴出资额
    public static final String SSXX_GDXX_SJCZEDW = "SJCZEDW"; // 实缴出资额单位及币种
    public static final String SSXX_GDXX_CZBL = "CZBL"; // 出资比例
    // 商事登记_主体组织机构信息
    public static final String USER_FORM_SSXX_ZZJG = "uf_ssxx_zzjg";// 主体组织机构信息
    public static final String SSXX_ZZJG_XM = "XM"; // 姓名
    public static final String SSXX_ZZJG_ZW = "ZW"; // 职务
    public static final String SSXX_ZZJG_ZWDM = "ZWDM"; // 职务代码
    // 商事登记_主体分支机构信息
    public static final String USER_FORM_SSXX_FZJG = "uf_ssxx_fzjg";// 主体分支机构信息
    public static final String SSXX_FZJG_MC = "MC"; // 名称
    public static final String SSXX_FZJG_JYCS = "JYCS"; // 经营场所
    public static final String SSXX_FZJG_FZR = "FZR"; // 负责人
    public static final String SSXX_FZJG_BZ = "BZ"; // 备注
    // 商事登记_相关许可单位
    public static final String USER_FORM_SSXX_XGXK = "uf_ssxx_xgxk";// 相关许可单位
    public static final String SSXX_XGXK_XKJYXMMC = "XKJYXMMC"; // 许可经营项目名称
    public static final String SSXX_XGXK_XKJYXMDM = "XKJYXMDM"; // 许可经营项目代码
    public static final String SSXX_XGXK_DWMC = "DWMC"; // 单位名称
    public static final String SSXX_XGXK_DWDM = "DWDM"; // 单位代码
    // 商事登记_主体清算信息
    public static final String USER_FORM_SSXX_QSXX = "uf_ssxx_qsxx";// 主体清算信息
    public static final String SSXX_QSXX_QSZFZR = "QSZFZR"; // 清算组负责人
    public static final String SSXX_QSXX_QSZRY = "QSZRY"; // 清算组人员姓名
    public static final String SSXX_QSXX_QSZDZ = "QSZDZ"; // 清算组联系地址
    public static final String SSXX_QSXX_QSZDH = "QSZDH"; // 清算组联系电话
    public static final String SSXX_QSXX_QSZBARQ = "QSZBARQ"; // 清算组备案时间

    // 商事行政许可信息
    public static final String TYPE_ID_SSXX_XZXK = "000000000XK";
    // 二期 商事行政许可 调整成6种阶段
    public static final String TYPE_ID_SSXX_XZXK_XK = "000000000XKXK"; // 许可
    public static final String TYPE_ID_SSXX_XZXK_YX = "000000000XKYX"; // 延续
    public static final String TYPE_ID_SSXX_XZXK_BG = "000000000XKBG"; // 补办
    public static final String TYPE_ID_SSXX_XZXK_FH = "000000000XKFH"; // 复核
    public static final String TYPE_ID_SSXX_XZXK_YSBB = "000000000XKYSBB"; // 遗失补办
    public static final String TYPE_ID_SSXX_XZXK_ZX = "000000000XKZX"; // 注销

    public static final String SSXX_XZXK_ZCH = "ZCH";// 注册号
    public static final String SSXX_XZXK_ZTMC = "ZTMC";// 企业名称
    public static final String SSXX_XZXK_FDDBR = "FDDBR";// 法定代表人
    public static final String SSXX_XZXK_ZTLX = "ZTLX";// 企业类型
    public static final String SSXX_XZXK_JYCS = "JYCS";// 经营场所
    public static final String SSXX_XZXK_BZDWDM = "BZDWDM";// 颁证单位代码
    public static final String SSXX_XZXK_BZDWMC = "BZDWMC";// 颁证单位名称
    public static final String SSXX_XZXK_ZJH = "ZJH";// 证件号
    public static final String SSXX_XZXK_ZZMC = "ZZMC";// 证照名称
    public static final String SSXX_XZXK_XKFW = "XKFW";// 许可范围
    public static final String SSXX_XZXK_GXRQ = "GXRQ";// 更新日期
    public static final String SSXX_XZXK_BZRQ = "BZRQ";// 颁证日期
    public static final String SSXX_XZXK_XQGXRQ = "XQGXRQ";// 下次更新日期
    public static final String SSXX_XZXK_JGMC = "JGMC";// 机构名称
    public static final String SSXX_XZXK_DJZT = "DJZT";// 登记状态
    public static final String SSXX_XZXK_XGWJ = "XGWJ";// 相关文件

    // 商事行政处罚信息
    public static final String TYPE_ID_SSXX_XZCF = "000000000CF";
    public static final String SSXX_XZCF_ZCH = "ZCH";// 注册号
    public static final String SSXX_XZCF_ZTMC = "ZTMC";// 企业名称
    public static final String SSXX_XZCF_FDDBR = "FDDBR";// 法定代表人
    public static final String SSXX_XZCF_ZTLX = "ZTLX";// 企业类型
    public static final String SSXX_XZCF_JYCS = "JYCS";// 经营场所
    public static final String SSXX_XZCF_DSR = "DSR";// 当事人
    public static final String SSXX_XZCF_WHXWDX = "WHXWDX";// 违法行为定性
    public static final String SSXX_XZCF_CFYJ = "CFYJ";// 处罚依据
    public static final String SSXX_XZCF_CFZL = "CFZL";// 处罚种类
    public static final String SSXX_XZCF_CFDJJGDM = "CFDJJGDM";// 处罚登记机关代码
    public static final String SSXX_XZCF_CFDJJGMC = "CFDJJGMC";// 处罚登记机关名称
    public static final String SSXX_XZCF_CFRQ = "CFRQ";// 处罚日期
    public static final String SSXX_XZCF_XGWJ = "XGWJ";// 相关文件

    // 商事主体荣誉信息
    public static final String TYPE_ID_SSXX_QDRY = "000000000RY";
    public static final String SSXX_QDRY_ZCH = "ZCH";// 注册号
    public static final String SSXX_QDRY_ZTMC = "ZTMC";// 企业名称
    public static final String SSXX_QDRY_FDDBR = "FDDBR";// 法定代表人
    public static final String SSXX_QDRY_ZTLX = "ZTLX";// 企业类型
    public static final String SSXX_QDRY_JYCS = "JYCS";// 经营场所
    public static final String SSXX_QDRY_MC = "MC";// 荣誉名称
    public static final String SSXX_QDRY_SYDWDM = "SYDWDM";// 授予单位代码
    public static final String SSXX_QDRY_SYDWMC = "SYDWMC";// 授予单位名称
    public static final String SSXX_QDRY_SYRQ = "SYRQ";// 授予日期
    public static final String SSXX_QDRY_YXRQ = "YXRQ";// 有效日期
    public static final String SSXX_QDRY_XGWJ = "XGWJ";// 相关文件

    // 商事主体资质信息
    public static final String TYPE_ID_SSXX_QDZZ = "000000000ZZ";
    public static final String SSXX_QDZZ_ZCH = "ZCH";// 注册号
    public static final String SSXX_QDZZ_ZTMC = "ZTMC";// 企业名称
    public static final String SSXX_QDZZ_FDDBR = "FDDBR";// 法定代表人
    public static final String SSXX_QDZZ_ZTLX = "ZTLX";// 企业类型
    public static final String SSXX_QDZZ_JYCS = "JYCS";// 经营场所
    public static final String SSXX_QDZZ_MC = "MC";// 资质名称
    public static final String SSXX_QDZZ_SYDWDM = "SYDWDM";// 授予单位代码
    public static final String SSXX_QDZZ_SYDWMC = "SYDWMC";// 授予单位名称
    public static final String SSXX_QDZZ_QDRQ = "QDRQ";// 取得日期
    public static final String SSXX_QDZZ_YXRQ = "YXRQ";// 有效日期
    public static final String SSXX_QDZZ_XGWJ = "XGWJ";// 相关文件

    // 商事主体年报备案信息
    public static final String TYPE_ID_SSXX_NBBA = "004140203NB";
    public static final String SSXX_NBBA_ZCH = "ZCH";// 注册号
    public static final String SSXX_NBBA_ZTMC = "ZTMC";// 企业名称
    public static final String SSXX_NBBA_FDDBR = "FDDBR";// 法定代表人
    public static final String SSXX_NBBA_ZTLX = "ZTLX";// 企业类型
    public static final String SSXX_NBBA_JYCS = "JYCS";// 经营场所
    public static final String SSXX_NBBA_ND = "ND";// 年度
    public static final String SSXX_NBBA_MC = "MC";// 名称
    public static final String SSXX_NBBA_RJRQ = "RJRQ";// 提交日期
    public static final String SSXX_NBBA_BGSM = "BGSM";// 变更说明
    public static final String SSXX_NBBA_XGWJ = "XGWJ";// 相关文件

    // 行政许可过程 （数据类型）
    public static final String SPGC_SL_TYPEID = "000000000SL"; // 受理
    public static final String SPGC_SH_TYPEID = "000000000SH"; // 审批过程
    public static final String SH_SPHJDM_CB = "SPHJMC_CB"; // 承办
    public static final String SH_SPHJDM_SH = "SPHJMC_SH"; // 审核
    public static final String SH_SPHJDM_PZ = "SPHJMC_PZ"; // 批准
    public static final String SPGC_BJ_TYPEID = "000000000BJ"; // 办结
    public static final String SPGC_BJGZ_TYPEID = "000000000BG"; // 补交告知
    public static final String SPGC_BJSL_TYPEID = "000000000BS"; // 补交受理
    public static final String SPGC_CXKS_TYPEID = "000000000TK"; // 特别程序开始
    public static final String SPGC_CXJS_TYPEID = "000000000TJ"; // 特别程序结束

    public static final Integer EXCHANGE_RETRANSMISSIONNUM = 10;
    public static final Integer EXCHANGE_INTERVER = 120;

    public static final String SOURCETYPE_ENTITY = "entity";
    public static final String SOURCETYPE_TABLE = "table";

    public static final Integer SYNCHRONOUSINTERVAL = 60;

    public static final Integer REPESTNUMBYONE = 10;// 定时推送数据，1个周期推送数量

    public static final String SOURCE_PLATFORM = "palatform";

    public static final String SOURCE_EXTERNAL = "external";

    public static final String SYNTABLE = "TIG_TABLE_DATA";

    public static final String SYNCOLUMN = "TIG_COLUMN_DATA";

    public static final String FOLDERTABLE = "REPO_FOLDER";

    public static final String FILETABLE = "REPO_FILE";

    public static final String ISSYNBACKFIELD = "IS_SYN_BACK";

    public static final String DONOTTIGDATA = Config
            .getValue(
                    "exchange.trigger.table.blacklist",
                    "BM_;CALENDAR_EMPLOY_DEFINITION;CD_RTX;CMS_PAGE_SORT;DYTABLE_;IS_DEL_DATA_LOG;IS_DATA_OPERATION_LOG;IS_DX_;IS_EXCHANGE_DATA;IS_EXCHANGE_DATA_BATCH;IS_EXCHANGE_DATA_CALLBACK;IS_EXCHANGE_DATA_FILE_UPLOAD;IS_EXCHANGE_DATA_LOG;IS_EXCHANGE_DATA_MONITOR;IS_EXCHANGE_DATA_REPEST;IS_EXCHANGE_DATA_REPLY;IS_EXCHANGE_DATA_ROUTE;IS_EXCHANGE_SEND_MONITOR;LOG_USER_OPERATION;MAIL_;MEETING_SUMMARY;ORG_USER_LOGIN_LOG;PSI_;SCHEDULE_;TIG_;USERFORM_;WE_;CD_READ_MARKER;TMP_ACL_;MSG_MAS_CONFIG;IS_SYN_DATA_LOG;TASK_JOB_DETAILS;MSG_MESSAGE_INBOX;MSG_MESSAGE_OUTBOX;REPO_FOLDER_OPERATE_LOG;WF_TASK_TIMER_LOG;T_BUSISYSATTR;T_BUSISYSINFO;T_RESOURCEATTR;T_RESOURCEINFO");

    public static final String NEWAPASOUT = "NEWAPASOUT";

    public static final String NEWAPASIN = "NEWAPASIN";
    /**
     * responseMap
     */
    public static final Map<Integer, String> responseMap = new HashMap<Integer, String>();

    static {
        responseMap.put(0, "成功");
        responseMap.put(1, "xml格式错误");
        responseMap.put(2, "业务数据不满足");
        responseMap.put(3, "系统异常");
        responseMap.put(4, "数据重复");
    }
}
