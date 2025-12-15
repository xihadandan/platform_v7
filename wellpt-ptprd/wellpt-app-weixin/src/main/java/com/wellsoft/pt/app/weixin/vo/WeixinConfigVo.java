/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.vo;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinConfigEntity;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
public class WeixinConfigVo extends BaseObject {

    // 配置UUID
    private Long uuid;

    private String creator;

    private String system;

    private String tenant;

    // 应用的ID
    private String appId;

    // 应用的凭证密钥
    private String appSecret;

    // 企业ID
    private String corpId;

    // 企业回调域名
    private String corpDomainUri;

    // 应用的移动端URL
    private String mobileAppUri;

    // 是否启用
    private Boolean enabled;

    // 配置JSON
    private WeixinConfiguration configuration;

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
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
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
     * @return the corpId
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * @param corpId 要设置的corpId
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the corpDomainUri
     */
    public String getCorpDomainUri() {
        return corpDomainUri;
    }

    /**
     * @param corpDomainUri 要设置的corpDomainUri
     */
    public void setCorpDomainUri(String corpDomainUri) {
        this.corpDomainUri = corpDomainUri;
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
    public WeixinConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(WeixinConfiguration configuration) {
        this.configuration = configuration;
    }

    public static WeixinConfigVo fromEntity(WeixinConfigEntity entity) {
        WeixinConfigVo vo = new WeixinConfigVo();
        if (entity != null) {
            BeanUtils.copyProperties(entity, vo);
            if (StringUtils.isNotBlank(entity.getDefinitionJson())) {
                vo.setConfiguration(JsonUtils.json2Object(entity.getDefinitionJson(), WeixinConfiguration.class));
            }
        }
        return vo;
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class WeixinConfiguration extends BaseObject {
        private Boolean enabledSyncOrg;

        private String orgUuid;

        private WeixinOrgSyncOption orgSyncOption;

        private List<String> orgSyncEvents;

        // 消息推送
        private boolean enabledPushMsg;

        // 待办消息内容模板,todo按流程待办消息配置的模板、custom统一指定
        private String todoMsgTemplateType;

        // 待办消息模板ID
        private String todoMsgTemplateId;

        // 事项群聊
        private WeixinGroupChat groupChat;

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
        public WeixinOrgSyncOption getOrgSyncOption() {
            return orgSyncOption;
        }

        /**
         * @param orgSyncOption 要设置的orgSyncOption
         */
        public void setOrgSyncOption(WeixinOrgSyncOption orgSyncOption) {
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
        public WeixinGroupChat getGroupChat() {
            return groupChat;
        }

        /**
         * @param groupChat 要设置的groupChat
         */
        public void setGroupChat(WeixinGroupChat groupChat) {
            this.groupChat = groupChat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class WeixinOrgSyncOption extends BaseObject {

        private static final long serialVersionUID = 2849974253527265962L;
        // 部门是否同步
        private boolean dept;
        // 用户是否同步
        private boolean user;
        // 用户姓名是否同步
        private boolean userName;
        // 用户头像是否同步
        private boolean userAvatar;
        // 用户手机号码是否同步
        private boolean userMobile;
        // 用户分机号是否同步
        private boolean userTelephone;
        // 用户邮箱是否同步
        private boolean userEmail;
        // 用户工号是否同步
        private boolean userNo;
        // 用户备注是否同步
        private boolean userRemark;

        // 同步事件token
        private String eventToken;

        // 同步事件AESKey
        private String eventAesKey;

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
         * @return the user
         */
        public boolean isUser() {
            return user;
        }

        /**
         * @param user 要设置的user
         */
        public void setUser(boolean user) {
            this.user = user;
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
         * @return the userTelephone
         */
        public boolean isUserTelephone() {
            return userTelephone;
        }

        /**
         * @param userTelephone 要设置的userTelephone
         */
        public void setUserTelephone(boolean userTelephone) {
            this.userTelephone = userTelephone;
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

        /**
         * @return the eventToken
         */
        public String getEventToken() {
            return eventToken;
        }

        /**
         * @param eventToken 要设置的eventToken
         */
        public void setEventToken(String eventToken) {
            this.eventToken = eventToken;
        }

        /**
         * @return the eventAesKey
         */
        public String getEventAesKey() {
            return eventAesKey;
        }

        /**
         * @param eventAesKey 要设置的eventAesKey
         */
        public void setEventAesKey(String eventAesKey) {
            this.eventAesKey = eventAesKey;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class WeixinGroupChat extends BaseObject {
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
