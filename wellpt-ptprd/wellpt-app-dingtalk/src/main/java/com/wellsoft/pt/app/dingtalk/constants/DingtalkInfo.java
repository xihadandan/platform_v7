package com.wellsoft.pt.app.dingtalk.constants;

/**
 * Description: 钉钉应用信息
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
public class DingtalkInfo {

    /**
     * 同步状态：成功
     */
    public static final Integer SYNC_STATUS_SUCCESS = 1;
    /**
     * 同步状态：异常
     */
    public static final Integer SYNC_STATUS_ERROR = 2;

    /**
     * 同步开关：开启
     */
    public static final String SYNC_SWITCH_OPEN = "1";

    /**
     * 同步操作：新增
     */
    public static final String SYNC_OPERATION_ADD = "新增";

    /**
     * 同步操作：修改
     */
    public static final String SYNC_OPERATION_MOD = "修改";

    /**
     * 同步操作：删除
     */
    public static final String SYNC_OPERATION_DEL = "删除";

    /**
     * 部门同步异常：部门下存在人员
     */
    public static final String SYNC_DEPT_ERROR_DEPT_EXISTS_USER = "部门下存在人员";

    /**
     * 人员同步异常：存在重名人员
     */
    public static final String SYNC_USER_ERROR_DUPLICATE_NAME = "存在重名人员";

    /**
     * 人员同步异常：代码错误或异常
     */
    public static final String SYNC_USER_ERROR_SYS_ERROR = "代码错误或异常";

    /**
     * 人员同步异常：找不到该用户
     */
    public static final String SYNC_USER_ERROR_NO_EXISTS = "找不到该用户";

    /**
     * 人员和职位关系同步异常：部门未同步
     */
    public static final String SYNC_USER_WORK_ERROR_DEPT_NO_EXISTS = "职位所属的部门不存在";

    /**
     * 人员和职位关系同步：部门未挂载到组织树中
     */
    public static final String SYNC_USER_WORK_ERROR_DEPT_NO_AT_TREE = "部门未挂载到组织树中";

    /**
     * 同步日志详情 重新同步部门
     */
    public static final String SYNC_DEPT_AGAIN = "0";

    /**
     * 同步日志详情 重新同步人员
     */
    public static final String SYNC_USER_AGAIN = "1";

    /**
     * 同步日志详情 重新同步人员和职位关系
     */
    public static final String SYNC_USER_WORK_AGAIN = "2";

    public static final String SYNC_CONTENT_DEPT = "部门";

    public static final String SYNC_CONTENT_USER = "人员";

    public static final String SYNC_CONTENT_USER_WORK = "人员和职位的关系";

    /**
     * 多部门人员审核  待审核
     */
    public static final Integer MULTI_DEPTS_USER_WORK_UNAUDIT = 0;

    /**
     * 多部门人员审核  已审核
     */
    public static final Integer MULTI_DEPTS_USER_WORK_AUDITED = 1;

    //	public static final ModuleConfig ModuleConfig = new DingtalkConfig();

    /**
     * 平台系统单位ID
     */
    //	public static final String SYSTEM_UNIT_ID = ModuleConfig.getValue("org.system_unit_id");
    //	public static final String SYSTEM_UNIT_NAME = ModuleConfig.getValue("org.system_unit_name");

    /**
     * 组织版本ID
     */
    //	public static final String ORG_VERSION_ID = ModuleConfig.getValue("org.org_version_id");
    //	public static final String ORG_VERSION_NAME = ModuleConfig.getValue("org.org_version_name");

    /**
     * 企业应用
     */
    //	public static final String AGENT_ID = ModuleConfig.getValue("agent_id");// "771445396";

    /**
     * 微应用key
     */
    //	public static final String APP_KEY = ModuleConfig.getValue("app_key");//"dingqlg6zwq3oiaepp53";

    /**
     * 微应用秘钥
     */
    //	public static final String APP_SECRET = ModuleConfig.getValue("app_secret");//"SLytpbwT6oIQ3JpxgRTNmmYkzUW6F_FJCWVYjkiJ_6v6z4AaKhP1aat8FTFWmpHS";

    /**
     * 通讯录回调事件的token
     */
    //	public static final String EVENT_CALL_BACK_TOKEN = ModuleConfig.getValue("event_call_back_token");//"6Ib4ZcBC";

    /**
     * 通讯录回调事件的数据加密密钥
     */
    //	public static final String APP_AES_KEY = ModuleConfig.getValue("app_aes_key");//"SsGZ3O54Bou8rCho1yukSmehzHzleO5qPAJYCF5jsig";

    /**
     * 免密登录应用授权ID
     */
    //	public static final String SNS_APP_ID = ModuleConfig.getValue("sns_app_id");//"dingoatqql5whogyedauzj";

    /**
     * 免密登录应用授权秘钥
     */
    //	public static final String SNS_APP_SECRET = ModuleConfig.getValue("sns_app_secret");//"Ve7FHvJ6EcyR1EY69J_emyk3n2eJWAas6LTszqHTBMSUFvfZDzd55MiAyZREWNEU";

    /**
     * 企业ID
     */
    //	public static final String CORP_ID = ModuleConfig.getValue("corp_id");//"dinge4fcddb96644fd9d35c2f4657eb6378f";

    /**
     * 企业回调域名
     */
    //	public static final String CORP_DOMAIN_URI = ModuleConfig.getValue("corp_domain_uri");//"http://oa.well-soft.com:11085";

}
