var DingtalkConfig = {
    "public String getOrgVersionId()": "获取组织版本ID",
    "public String getOrgVersionName()": "获取组织版本名称",
    "public String getSystemUnitId()": "获取平台系统单位ID",
    "public String getSystemUnitName()": "获取平台系统单位名称",
    "public String getAgentId()": "获取企业应用ID",
    "public String getAppKey()": "微应用key",
    "public String getAppSecret()": "微应用秘钥",
    "public String getEventCallBackToken()": "通讯录回调事件的token",
    "public String getAppAesKey()": "通讯录回调事件的数据加密密钥",
    "public String getSnsAppId()": "免密登录应用授权ID",
    "public String getSnsAppSecret()": "免密登录应用授权秘钥",
    "public String getCorpId()": "企业ID",
    "public String getCorpDomainUri()": "企业回调域名",
    "public String getBaseUri()": "钉钉私有云域名",
};

var DingtalkApiUtils = {
    "public static String getAccessToken()": "获取access_token",
    "public static String getJsapiTicket()": "获取jsapi_ticket",
    "public static JSONObject getUserInfoByCode(String code)": "获取用户授权信息",
    "public static JSONObject registerCallBack(List<String> backTags, String accessToken)": "注册回调监听事件",
    "public static JSONObject getCallBack(String accessToken)": "查询事件回调接口",
    "public static JSONObject updateCallBack(List<String> backTags, String accessToken)": "更新事件回调接口",
    "public static JSONObject deleteCallBack(String accessToken)": "删除事件回调接口",
    "public static JSONObject getCallBackFailedResult(String accessToken)": "获取回调失败的结果",
    "public static JSONObject getDingTalkUserInfo(String userId, String accessToken)": "获取钉钉用户详情",
    "public static JSONObject getDingTalkAuthScopes(String accessToken)": "获取通讯录权限范围",
    "public static JSONObject getDingTalkDeptList(String accessToken, String deptId)": "获取钉钉部门列表",
    "public static JSONObject getDingTalkDeptInfo(String deptId, String accessToken)": "获取钉钉部门详情",
    "public static JSONObject getDingTalkUserListbypage(String deptId, String accessToken)": "获取部门用户详情数据",
    "public static JSONObject getDeptMember(String deptId, String accessToken)": "获取部门用户userid列表",
    "public static JSONObject addWorkRecord(String bizId, String userId, String userName, String accessToken,String title, String url, List<FormItemVo> formItemList)": "发起钉钉待办",
    "public static JSONObject addWorkRecord2(String bizId, String userId, String userName, String accessToken,String title, String url, List<FormItemVo> formItemList)": "保存钉钉待办任务",
    "public static JSONObject updateWorkRecord(String bizId, String userId, String accessToken)": "更新钉钉待办",
    "public static JSONObject updateWorkRecord2(String bizId, String userId, String accessToken)": "更新钉钉待办任务",
    "public static JSONObject sendDingtalkMsg(String useridList, String msgType, String msgTitle, String msgContent,String mediaId, String messageUrl, String picUrl, String btnOrientation,List<Map<String, String>> dtBtnJsonList, String accessToken)": "工作通知消息",
    "public static JSONObject getRoleList(boolean all, String accessToken)": "获取角色列表",
    "public static JSONObject getRoleSimpleList(long roleId, boolean all, String accessToken)": "获取角色下的员工列表",
    "public static JSONObject getRole(long roleId, String accessToken)": "获取角色详情"
};

var MultiOrgDingDeptService = {
    "public MultiOrgDingDept getByDingDeptId(String dingDeptId)": "根据钉钉部门ID获取钉钉部门详情",
    "public MultiOrgDingDept getByPtDeptId(String ptDeptId)": "根据平台部门ID获取钉钉部门详情",
}

var MultiOrgDingRoleService = {
    "public MultiOrgDingRole getByDingRoleId(String dingRoleId)": "根据钉钉角色ID获取钉钉角色详情",
    "public MultiOrgDingRole getByRoleId(String roleId)": "根据平台角色ID获取钉钉角色详情",
}

var MultiOrgDingUserService = {
    "public MultiOrgDingUser getByPtUserId(String ptUserId)": "根据平台用户ID获取钉钉用户详情",
    "public MultiOrgDingUser getByDingUserId(String dingUserId)": "根据钉钉用户ID获取钉钉用户详情",
    "public MultiOrgDingUser getByUnionId(String unionId)": "根据钉钉UnionId获取钉钉用户详情",
}