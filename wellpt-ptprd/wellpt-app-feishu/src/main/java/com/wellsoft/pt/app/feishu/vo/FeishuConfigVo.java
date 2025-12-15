package com.wellsoft.pt.app.feishu.vo;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@ApiModel("飞书配置信息")
public class FeishuConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("飞书配置UUID")
    private Long uuid;

    @ApiModelProperty("归属系统")
    private String system;

    @ApiModelProperty("归属租户")
    private String tenant;

    @ApiModelProperty("飞书应用的App ID")
    private String appId;

    @ApiModelProperty("飞书应用的App Secret")
    private String appSecret;

    @ApiModelProperty("飞书应用的服务URL")
    private String serviceUri;

    @ApiModelProperty("飞书应用的重定向URL")
    private String redirectUri;

    @ApiModelProperty("飞书应用的移动端URL")
    private String mobileAppUri;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("配置JSON")
    private FeishuConfiguration configuration;

    @ApiModelProperty("飞书同步配置json对象")
    private FeishuSyncConfVo syncConf;
    @ApiModelProperty("飞书事件配置json对象")
    private List<FeishuEventConfVo> eventConfs;
    @ApiModelProperty("飞书审批配置json对象")
    private FeishuApprovalConfVo approvalConf;

    /**
     * @return the uuid
     */
    public Long getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the appSecret
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * @param appSecret 要设置的appSecret
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * @return the serviceUri
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * @param serviceUri 要设置的serviceUri
     */
    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    /**
     * @return the redirectUri
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * @param redirectUri 要设置的redirectUri
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * @return the mobileAppUri
     */
    public String getMobileAppUri() {
        return mobileAppUri;
    }

    /**
     * @param mobileAppUri 要设置的mobileAppUri
     */
    public void setMobileAppUri(String mobileAppUri) {
        this.mobileAppUri = mobileAppUri;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the configuration
     */
    public FeishuConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(FeishuConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the syncConf
     */
    public FeishuSyncConfVo getSyncConf() {
        return syncConf;
    }

    /**
     * @param syncConf 要设置的syncConf
     */
    public void setSyncConf(FeishuSyncConfVo syncConf) {
        this.syncConf = syncConf;
    }

    /**
     * @return the eventConfs
     */
    public List<FeishuEventConfVo> getEventConfs() {
        return eventConfs;
    }

    /**
     * @param eventConfs 要设置的eventConfs
     */
    public void setEventConfs(List<FeishuEventConfVo> eventConfs) {
        this.eventConfs = eventConfs;
    }

    /**
     * @return the approvalConf
     */
    public FeishuApprovalConfVo getApprovalConf() {
        return approvalConf;
    }

    /**
     * @param approvalConf 要设置的approvalConf
     */
    public void setApprovalConf(FeishuApprovalConfVo approvalConf) {
        this.approvalConf = approvalConf;
    }

    public static FeishuConfigVo create(FeishuConfigEntity entity) {
        FeishuConfigVo vo = new FeishuConfigVo();
        if (entity != null) {
            vo.setUuid(entity.getUuid());
            vo.setAppId(entity.getAppId());
            vo.setAppSecret(entity.getAppSecret());
            vo.setServiceUri(entity.getServiceUri());
            vo.setRedirectUri(entity.getRedirectUri());
            vo.setMobileAppUri(entity.getMobileAppUri());
            vo.setEnabled(entity.getEnabled());
            vo.setSystem(entity.getSystem());
            vo.setTenant(entity.getTenant());
            if (StringUtils.isNotBlank(entity.getDefinitionJson())) {
                vo.setConfiguration(JsonUtils.json2Object(entity.getDefinitionJson(), FeishuConfiguration.class));
            }
//            vo.setOrgUuid(entity.getOrgUuid());
//            vo.setOrgName(entity.getOrgName());
//            FeishuSyncConfVo syncConfVo = JSON.parseObject(entity.getSyncConfJson(), FeishuSyncConfVo.class);
//            List<FeishuEventConfVo> eventConfVos = JSON.parseArray(entity.getEventConfJson(), FeishuEventConfVo.class);
//            FeishuApprovalConfVo approvalConfVo = JSON.parseObject(entity.getApprovalConfJson(), FeishuApprovalConfVo.class);
//            vo.setSyncConf(syncConfVo);
//            vo.setEventConfs(eventConfVos);
//            vo.setApprovalConf(approvalConfVo);
            return vo;
        }
        vo.setSyncConf(FeishuSyncConfVo.getDefaultSyncConfVo());
        vo.setApprovalConf(FeishuApprovalConfVo.getDefaultApprovalConfVo());
        vo.setEventConfs(FeishuEventConfVo.getDefaultEventConfVos());
        return vo;
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FeishuConfiguration extends BaseObject {
        private Boolean enabledSyncOrg;

        private String orgUuid;

        private FeishuOrgSyncOption orgSyncOption;

        private List<String> orgSyncEvents;

        // 消息推送
        private boolean enabledPushMsg;

        // 待办消息内容模板,todo按流程待办消息配置的模板、custom统一指定
        private String todoMsgTemplateType;

        // 待办消息模板ID
        private String todoMsgTemplateId;

        // 事项群聊
        private FeishuGroupChat groupChat;

        /**
         * @return the enabledSyncOrg
         */
        public Boolean getEnabledSyncOrg() {
            return enabledSyncOrg;
        }

        /**
         * @param enabledSyncOrg 要设置的enabledSyncOrg
         */
        public void setEnabledSyncOrg(Boolean enabledSyncOrg) {
            this.enabledSyncOrg = enabledSyncOrg;
        }

        /**
         * @return the orgUuid
         */
        public String getOrgUuid() {
            return orgUuid;
        }

        /**
         * @param orgUuid 要设置的orgUuid
         */
        public void setOrgUuid(String orgUuid) {
            this.orgUuid = orgUuid;
        }

        /**
         * @return the orgSyncOption
         */
        public FeishuOrgSyncOption getOrgSyncOption() {
            return orgSyncOption;
        }

        /**
         * @param orgSyncOption 要设置的orgSyncOption
         */
        public void setOrgSyncOption(FeishuOrgSyncOption orgSyncOption) {
            this.orgSyncOption = orgSyncOption;
        }

        /**
         * @return the orgSyncEvents
         */
        public List<String> getOrgSyncEvents() {
            return orgSyncEvents;
        }

        /**
         * @param orgSyncEvents 要设置的orgSyncEvents
         */
        public void setOrgSyncEvents(List<String> orgSyncEvents) {
            this.orgSyncEvents = orgSyncEvents;
        }

        /**
         * @return the enabledPushMsg
         */
        public boolean isEnabledPushMsg() {
            return enabledPushMsg;
        }

        /**
         * @param enabledPushMsg 要设置的enabledPushMsg
         */
        public void setEnabledPushMsg(boolean enabledPushMsg) {
            this.enabledPushMsg = enabledPushMsg;
        }

        /**
         * @return the todoMsgTemplateType
         */
        public String getTodoMsgTemplateType() {
            return todoMsgTemplateType;
        }

        /**
         * @param todoMsgTemplateType 要设置的todoMsgTemplateType
         */
        public void setTodoMsgTemplateType(String todoMsgTemplateType) {
            this.todoMsgTemplateType = todoMsgTemplateType;
        }

        /**
         * @return the todoMsgTemplateId
         */
        public String getTodoMsgTemplateId() {
            return todoMsgTemplateId;
        }

        /**
         * @param todoMsgTemplateId 要设置的todoMsgTemplateId
         */
        public void setTodoMsgTemplateId(String todoMsgTemplateId) {
            this.todoMsgTemplateId = todoMsgTemplateId;
        }

        /**
         * @return the groupChat
         */
        public FeishuGroupChat getGroupChat() {
            return groupChat;
        }

        /**
         * @param groupChat 要设置的groupChat
         */
        public void setGroupChat(FeishuGroupChat groupChat) {
            this.groupChat = groupChat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FeishuOrgSyncOption extends BaseObject {

        private static final long serialVersionUID = 2849974253527265962L;
        @ApiModelProperty("部门是否同步")
        private boolean dept;
        @ApiModelProperty("用户姓名是否同步")
        private boolean userName;
        @ApiModelProperty("用户头像是否同步")
        private boolean userAvatar;
        @ApiModelProperty("用户性别是否同步")
        private boolean userGender;
        @ApiModelProperty("用户手机号码是否同步")
        private boolean userMobile;
        @ApiModelProperty("用户邮箱是否同步")
        private boolean userEmail;
        @ApiModelProperty("用户工号是否同步")
        private boolean userNo;
        @ApiModelProperty("用户备注是否同步")
        private boolean userRemark;

        /**
         * @return the dept
         */
        public boolean isDept() {
            return dept;
        }

        /**
         * @param dept 要设置的dept
         */
        public void setDept(boolean dept) {
            this.dept = dept;
        }

        /**
         * @return the userName
         */
        public boolean isUserName() {
            return userName;
        }

        /**
         * @param userName 要设置的userName
         */
        public void setUserName(boolean userName) {
            this.userName = userName;
        }

        /**
         * @return the userAvatar
         */
        public boolean isUserAvatar() {
            return userAvatar;
        }

        /**
         * @param userAvatar 要设置的userAvatar
         */
        public void setUserAvatar(boolean userAvatar) {
            this.userAvatar = userAvatar;
        }

        /**
         * @return the userGender
         */
        public boolean isUserGender() {
            return userGender;
        }

        /**
         * @param userGender 要设置的userGender
         */
        public void setUserGender(boolean userGender) {
            this.userGender = userGender;
        }

        /**
         * @return the userMobile
         */
        public boolean isUserMobile() {
            return userMobile;
        }

        /**
         * @param userMobile 要设置的userMobile
         */
        public void setUserMobile(boolean userMobile) {
            this.userMobile = userMobile;
        }

        /**
         * @return the userEmail
         */
        public boolean isUserEmail() {
            return userEmail;
        }

        /**
         * @param userEmail 要设置的userEmail
         */
        public void setUserEmail(boolean userEmail) {
            this.userEmail = userEmail;
        }

        /**
         * @return the userNo
         */
        public boolean isUserNo() {
            return userNo;
        }

        /**
         * @param userNo 要设置的userNo
         */
        public void setUserNo(boolean userNo) {
            this.userNo = userNo;
        }

        /**
         * @return the userRemark
         */
        public boolean isUserRemark() {
            return userRemark;
        }

        /**
         * @param userRemark 要设置的userRemark
         */
        public void setUserRemark(boolean userRemark) {
            this.userRemark = userRemark;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class FeishuGroupChat extends BaseObject {
        private static final long serialVersionUID = -4573053773454539642L;

        private boolean enabled;

        private String scope;

        private List<String> values;

        // multiUser按环节多人办理且每个人都要办理、collaboration按协作环节
        private List<String> startModes;

        // none群聊记录不带入流程、text群聊记录带入流程(不包含附件)、textAndFile群聊记录带入流程(包含附件)
        private String saveContentMode;

        // 事项群聊解散，taskEnd群聊结束立即解散、flowEnd流程结束时解散
        private String deleteMode;

        // 自动提交，none不自动提交、@_all@所有人时自动提交
        private String autoSubmitMode;

        /**
         * @return the enabled
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * @param enabled 要设置的enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * @return the scope
         */
        public String getScope() {
            return scope;
        }

        /**
         * @param scope 要设置的scope
         */
        public void setScope(String scope) {
            this.scope = scope;
        }

        /**
         * @return the values
         */
        public List<String> getValues() {
            return values;
        }

        /**
         * @param values 要设置的values
         */
        public void setValues(List<String> values) {
            this.values = values;
        }

        /**
         * @return the startModes
         */
        public List<String> getStartModes() {
            return startModes;
        }

        /**
         * @param startModes 要设置的startModes
         */
        public void setStartModes(List<String> startModes) {
            this.startModes = startModes;
        }

        /**
         * @return the saveContentMode
         */
        public String getSaveContentMode() {
            return saveContentMode;
        }

        /**
         * @param saveContentMode 要设置的saveContentMode
         */
        public void setSaveContentMode(String saveContentMode) {
            this.saveContentMode = saveContentMode;
        }

        /**
         * @return the deleteMode
         */
        public String getDeleteMode() {
            return deleteMode;
        }

        /**
         * @param deleteMode 要设置的deleteMode
         */
        public void setDeleteMode(String deleteMode) {
            this.deleteMode = deleteMode;
        }

        /**
         * @return the autoSubmitMode
         */
        public String getAutoSubmitMode() {
            return autoSubmitMode;
        }

        /**
         * @param autoSubmitMode 要设置的autoSubmitMode
         */
        public void setAutoSubmitMode(String autoSubmitMode) {
            this.autoSubmitMode = autoSubmitMode;
        }
    }
}
