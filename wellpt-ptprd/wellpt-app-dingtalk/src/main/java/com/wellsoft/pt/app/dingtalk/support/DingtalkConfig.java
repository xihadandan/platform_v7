package com.wellsoft.pt.app.dingtalk.support;

import com.wellsoft.pt.basicdata.params.facade.ModuleConfig;
import org.springframework.stereotype.Component;

/**
 * Description: 钉钉模块配置
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月11日.1	zhongzh		2020年6月11日		Create
 * </pre>
 * @date 2020年6月11日
 */
@Component
@Deprecated
public class DingtalkConfig extends ModuleConfig {

    public DingtalkConfig() {
        super("dingtalk", "pt.app.dingtalk");
    }

    /**
     * 组织版本ID
     */
    //	public static final String ORG_VERSION_ID = ModuleConfig.getValue("org.org_version_id");
    //	public static final String ORG_VERSION_NAME = ModuleConfig.getValue("org.org_version_name");
    public String getOrgVersionId() {
        return getValue("org.org_version_id");
    }

    public String getOrgVersionName() {
        return getValue("org.org_version_name");
    }

    /**
     * 平台系统单位ID
     */
    //	public static final String SYSTEM_UNIT_ID = ModuleConfig.getValue("org.system_unit_id");
    //	public static final String SYSTEM_UNIT_NAME = ModuleConfig.getValue("org.system_unit_name");
    public String getSystemUnitId() {
        return getValue("org.system_unit_id");
    }

    public String getSystemUnitName() {
        return getValue("org.system_unit_name");
    }

    /**
     * 企业应用
     */
    //	public static final String AGENT_ID = ModuleConfig.getValue("agent_id");// "771445396";
    public String getAgentId() {
        return getValue("agent_id");
    }

    /**
     * 微应用key
     */
    //	public static final String APP_KEY = ModuleConfig.getValue("app_key");//"dingqlg6zwq3oiaepp53";
    public String getAppKey() {
        return getValue("app_key");
    }

    /**
     * 微应用秘钥
     */
    //	public static final String APP_SECRET = ModuleConfig.getValue("app_secret");//"SLytpbwT6oIQ3JpxgRTNmmYkzUW6F_FJCWVYjkiJ_6v6z4AaKhP1aat8FTFWmpHS";
    public String getAppSecret() {
        return getValue("app_secret");
    }

    /**
     * 通讯录回调事件的token
     */
    //	public static final String EVENT_CALL_BACK_TOKEN = ModuleConfig.getValue("event_call_back_token");//"6Ib4ZcBC";
    public String getEventCallBackToken() {
        return getValue("event_call_back_token");
    }

    /**
     * 通讯录回调事件的数据加密密钥
     */
    //	public static final String APP_AES_KEY = ModuleConfig.getValue("app_aes_key");//"SsGZ3O54Bou8rCho1yukSmehzHzleO5qPAJYCF5jsig";
    public String getAppAesKey() {
        return getValue("app_aes_key");
    }

    /**
     * 免密登录应用授权ID
     */
    //	public static final String SNS_APP_ID = ModuleConfig.getValue("sns_app_id");//"dingoatqql5whogyedauzj";
    public String getSnsAppId() {
        return getValue("sns_app_id");
    }

    /**
     * 免密登录应用授权秘钥
     */
    //	public static final String SNS_APP_SECRET = ModuleConfig.getValue("sns_app_secret");//"Ve7FHvJ6EcyR1EY69J_emyk3n2eJWAas6LTszqHTBMSUFvfZDzd55MiAyZREWNEU";
    public String getSnsAppSecret() {
        return getValue("sns_app_secret");
    }

    /**
     * 企业ID
     */
    //	public static final String CORP_ID = ModuleConfig.getValue("corp_id");//"dinge4fcddb96644fd9d35c2f4657eb6378f";
    public String getCorpId() {
        return getValue("corp_id");
    }

    /**
     * 企业回调域名
     */
    //	public static final String CORP_DOMAIN_URI = ModuleConfig.getValue("corp_domain_uri");//"http://oa.well-soft.com:11085";
    public String getCorpDomainUri() {
        return getValue("corp_domain_uri");
    }

    /**
     * 钉钉私有云域名
     *
     * @return
     */
    public String getBaseUri() {
        return getValue("base_uri");
    }

    /**
     * 待办推送模式
     *
     * @return
     */
    public String getPushMode() {
        return getValue("push.mode");
    }


    /**
     * 部门同步开关
     *
     * @return
     */
    public String getDeptSyncSwitch() {
        return getValue("org.sync.dept");
    }

    /**
     * 人员头像同步开关
     *
     * @return
     */
    public String getAvatarSyncSwitch() {
        return getValue("org.sync.user.photo");
    }

    /**
     * 人员手机同步开关
     *
     * @return
     */
    public String getMobileSyncSwitch() {
        return getValue("org.sync.user.mobile");
    }

    /**
     * 人员办公电话同步开关
     *
     * @return
     */
    public String getTelephoneSyncSwitch() {
        return getValue("org.sync.user.telephone");
    }

    /**
     * 人员邮箱同步开关
     *
     * @return
     */
    public String getEmailSyncSwitch() {
        return getValue("org.sync.user.email");
    }

    /**
     * 员工工号同步开关
     *
     * @return
     */
    public String getJobnoSyncSwitch() {
        return getValue("org.sync.user.jobno");
    }

    public String getIdnumberSyncSwitch() {
        return getValue("org.sync.user.idnumber");
    }

    /**
     * 人员备注同步开关
     *
     * @return
     */
    public String getRemarkSyncSwitch() {
        return getValue("org.sync.user.remark");
    }

    /**
     * 人员和职位的关系同步开关
     *
     * @return
     */
    public String getWorkinfoSyncSwitch() {
        return getValue("org.sync.workinfo");
    }


}
