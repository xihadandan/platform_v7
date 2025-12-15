package com.wellsoft.pt.app.dingtalk.constants;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;

/**
 * Description: 钉钉APIs
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
public abstract class DingtalkApis {

    /**
     * 花名册字段信息：证件号码
     */
    public static final String SMART_WORK_HRM_CERT_NO = "sys02-certNo";
    /**
     * access_token获取接口
     */
    public static final String URI_ACCESS_TOKEN;
    /**
     * jsapi_ticket获取接口
     */
    public static final String URI_JSAPI_TICKET;
    /**
     * 钉钉免密登录获取临时授权码接口
     */
    public static final String URI_SNS_AUTHORIZE;
    /**
     * 免密登录二维码地址 ：https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6/jfDu3
     */
    public static final String URI_SNS_LOGIN_QR;
    /**
     * 免密登入Oauth2地址：https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6/Q23rX
     */
    public static final String URI_SNS_LOGIN_OAUTH2;
    /**
     * 临时授权码获取授权用户信息接口
     */
    public static final String URI_GETUSERINFO_BYCODE;
    /**
     * 获取通讯录权限范围
     */
    public static final String URI_AUTH_SCOPES;
    /**
     * 获取用户详情接口
     */
    public static final String URI_USER_GET;
    /**
     * 获取部门列表接口
     */
    public static final String URI_DEPARTMENT_LIST;
    /**
     * 获取部门详情接口
     */
    public static final String URI_DEPARTMENT_GET;
    /**
     * 部门用户列表
     */
    public static final String URI_USER_SIMPLELIST;
    /**
     * 部门用户列表详情
     */
    public static final String URI_USER_LISTBYPAGE;
    /**
     * 获取部门用户userid列表
     */
    public static final String URI_GET_DEPT_MEMBER;
    /**
     * 工作通知消息接口
     */
    public static final String URI_MESSAGE_CORPCONVERSATION;
    /**
     * 发起代办接口
     */
    public static final String URI_WORKRECORD_ADD;
    /**
     * 更新代办接口
     */
    public static final String URI_WORKRECORD_UPDATE;
    /**
     * 注册业务事件回调接口
     */
    public static final String URI_REGISTER_CALL_BACK;
    /**
     * 查询事件回调接口
     */
    public static final String URI_GET_CALL_BACK;
    /**
     * 更新事件回调接口
     */
    public static final String URI_UPDATE_CALL_BACK;
    /**
     * 删除事件回调接口
     */
    public static final String URI_DELETE_CALL_BACK;
    /**
     * 获取回调失败的结果
     */
    public static final String URI_GET_CALL_BACK_FAILED_RESULT;
    /**
     * 获取角色列表
     */
    public static final String URI_GET_ROLE_LIST;
    /**
     * 获取角色下的员工列表
     */
    public static final String URI_GET_ROLE_SIMPLELIST;
    /**
     * 获取角色详情
     */
    public static final String URI_GET_ROLE_GETROLE;
    /**
     * 获取员工花名册字段信息
     */
    public static final String URI_GET_EMPLOYEE_LIST;
    private static DingtalkConfig dingtalkConfig = null;

    static {
        dingtalkConfig = ApplicationContextHolder.getBean(DingtalkConfig.class);
        URI_ACCESS_TOKEN = dingtalkConfig.getBaseUri() + "/gettoken";
        URI_JSAPI_TICKET = dingtalkConfig.getBaseUri() + "/get_jsapi_ticket";
        URI_SNS_AUTHORIZE = dingtalkConfig.getBaseUri() + "/connect/oauth2/sns_authorize?appid=%s&response_type=code&scope=snsapi_auth&state=STATE&redirect_uri=%s";
        URI_SNS_LOGIN_QR = dingtalkConfig.getBaseUri() + "/connect/qrconnect?appid=%s&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=%s";
        URI_SNS_LOGIN_OAUTH2 = dingtalkConfig.getBaseUri() + "/connect/oauth2/sns_authorize?appid=%s&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=%s";
        URI_GETUSERINFO_BYCODE = dingtalkConfig.getBaseUri() + "/sns/getuserinfo_bycode";
        URI_AUTH_SCOPES = dingtalkConfig.getBaseUri() + "/auth/scopes";
        URI_USER_GET = dingtalkConfig.getBaseUri() + "/user/get";
        URI_DEPARTMENT_LIST = dingtalkConfig.getBaseUri() + "/department/list";
        URI_DEPARTMENT_GET = dingtalkConfig.getBaseUri() + "/department/get";
        URI_USER_SIMPLELIST = dingtalkConfig.getBaseUri() + "/user/simplelist?access_token=%s&department_id=%d";
        URI_USER_LISTBYPAGE = dingtalkConfig.getBaseUri() + "/user/listbypage";
        URI_GET_DEPT_MEMBER = dingtalkConfig.getBaseUri() + "/user/getDeptMember";
        URI_MESSAGE_CORPCONVERSATION = dingtalkConfig.getBaseUri() + "/topapi/message/corpconversation/asyncsend_v2?access_token=%s";
        URI_WORKRECORD_ADD = dingtalkConfig.getBaseUri() + "/topapi/workrecord/add";
        URI_WORKRECORD_UPDATE = dingtalkConfig.getBaseUri() + "/topapi/workrecord/update";
        URI_REGISTER_CALL_BACK = dingtalkConfig.getBaseUri() + "/call_back/register_call_back";
        URI_GET_CALL_BACK = dingtalkConfig.getBaseUri() + "/call_back/get_call_back";
        URI_UPDATE_CALL_BACK = dingtalkConfig.getBaseUri() + "/call_back/update_call_back";
        URI_DELETE_CALL_BACK = dingtalkConfig.getBaseUri() + "/call_back/delete_call_back";
        URI_GET_CALL_BACK_FAILED_RESULT = dingtalkConfig.getBaseUri() + "/call_back/get_call_back_failed_result";
        URI_GET_ROLE_LIST = dingtalkConfig.getBaseUri() + "/topapi/role/list";
        URI_GET_ROLE_SIMPLELIST = dingtalkConfig.getBaseUri() + "/topapi/role/simplelist";
        URI_GET_ROLE_GETROLE = dingtalkConfig.getBaseUri() + "/topapi/role/getrole";
        URI_GET_EMPLOYEE_LIST = dingtalkConfig.getBaseUri() + "/topapi/smartwork/hrm/employee/v2/list";
    }

}
