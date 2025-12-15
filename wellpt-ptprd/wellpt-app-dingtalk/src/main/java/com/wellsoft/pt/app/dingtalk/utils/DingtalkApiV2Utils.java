/*
 * @(#)4/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.utils;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalktodo_1_0.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkClient;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.taobao.api.ApiException;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkTodoTaskEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.model.ActionCardMessage;
import com.wellsoft.pt.app.dingtalk.model.TodoTaskMessage;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkDepartmentVo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/17/25.1	    zhulh		4/17/25		    Create
 * </pre>
 * @date 4/17/25
 */
public class DingtalkApiV2Utils {
    private static final Logger LOG = LoggerFactory.getLogger(DingtalkApiV2Utils.class);

    private static Map<String, AccessToken> ACCESS_TOKEN_MAP = Maps.newConcurrentMap();

    private static Map<String, DingTalkClient> DING_TALK_CLIENT_MP = new WeakHashMap<>();

    private static Map<String, OpenDingTalkClient> WS_CLIENT_MAP = Maps.newHashMap();

    private static DingTalkClient getDingTalkClient(String apiUrl) {
        DingTalkClient client = DING_TALK_CLIENT_MP.get(apiUrl);
        if (client == null) {
            client = new DefaultDingTalkClient(apiUrl);
        }
        return client;
    }

    public static com.aliyun.dingtalkoauth2_1_0.Client createClient(String clientId, String clientSecret, String baseUrl) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        if (StringUtils.isNotBlank(baseUrl)) {
            URL url = new URL(baseUrl);
            config.endpoint = url.getHost();
            config.protocol = url.getProtocol();
        }
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    public static com.aliyun.dingtalkrobot_1_0.Client createRobotClient(String clientId, String clientSecret, String baseUrl) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        if (StringUtils.isNotBlank(baseUrl)) {
            URL url = new URL(baseUrl);
            config.endpoint = url.getHost();
            config.protocol = url.getProtocol();
        }
        return new com.aliyun.dingtalkrobot_1_0.Client(config);
    }

    public static com.aliyun.dingtalktodo_1_0.Client createTodoClient(DingtalkConfigVo dingtalkConfigVo) throws Exception {
        String baseUrl = dingtalkConfigVo.getServiceUri();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        if (StringUtils.isNotBlank(baseUrl)) {
            URL url = new URL(baseUrl);
            config.endpoint = url.getHost();
            config.protocol = url.getProtocol();
        }
        return new com.aliyun.dingtalktodo_1_0.Client(config);
    }

    private static com.aliyun.dingtalkoauth2_1_0.Client createAuthClient(DingtalkConfigVo dingtalkConfigVo) throws Exception {
        String baseUrl = dingtalkConfigVo.getServiceUri();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        if (StringUtils.isNotBlank(baseUrl)) {
            URL url = new URL(baseUrl);
            config.endpoint = url.getHost();
            config.protocol = url.getProtocol();
        }
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    private static com.aliyun.dingtalkcontact_1_0.Client createContactClient(DingtalkConfigVo dingtalkConfigVo) throws Exception {
        String baseUrl = dingtalkConfigVo.getServiceUri();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        if (StringUtils.isNotBlank(baseUrl)) {
            URL url = new URL(baseUrl);
            config.endpoint = url.getHost();
            config.protocol = url.getProtocol();
        }
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }

    public static String createAccessToken(DingtalkConfigVo dingtalkConfigVo) {
        return createAccessToken(dingtalkConfigVo.getClientId(), dingtalkConfigVo.getClientSecret(), dingtalkConfigVo.getServiceUri());
    }

    public static String createAccessToken(String clientId, String clientSecret, String baseUrl) {
        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = createClient(clientId, clientSecret, baseUrl);
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest();
            getAccessTokenRequest.setAppKey(clientId);
            getAccessTokenRequest.setAppSecret(clientSecret);
            GetAccessTokenResponse getAccessTokenResponse = client.getAccessToken(getAccessTokenRequest);
            AccessToken accessToken = new AccessToken(getAccessTokenResponse.getBody().getAccessToken(),
                    getAccessTokenResponse.getBody().getExpireIn());
            ACCESS_TOKEN_MAP.put(clientId, accessToken);
            return getAccessTokenResponse.getBody().getAccessToken();
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        }
        return null;
    }

    public static String getAccessToken(DingtalkConfigVo dingtalkConfigVo) {
        AccessToken accessToken = ACCESS_TOKEN_MAP.get(dingtalkConfigVo.getClientId());
        if (accessToken == null || accessToken.isExpire()) {
            return createAccessToken(dingtalkConfigVo);
        }
        return accessToken.getToken();
    }

    public static List<DingtalkDepartmentVo> departments(DingtalkConfigVo dingtalkConfigVo) {
        try {
            List<OapiV2DepartmentListsubResponse.DeptBaseResponse> results = listDepartment(1L, dingtalkConfigVo);
            List<DingtalkDepartmentVo> departmentVos = buildDepartmentTree(results, dingtalkConfigVo);
            return departmentVos;
        } catch (ApiException e) {
            throw new BusinessException(e);
        }
    }

    private static List<DingtalkDepartmentVo> buildDepartmentTree(List<OapiV2DepartmentListsubResponse.DeptBaseResponse> departments,
                                                                  DingtalkConfigVo dingtalkConfigVo) throws ApiException {
        List<DingtalkDepartmentVo> departmentVos = Lists.newArrayList();
        for (OapiV2DepartmentListsubResponse.DeptBaseResponse department : departments) {
            DingtalkDepartmentVo departmentVo = new DingtalkDepartmentVo();
            departmentVo.setDepartment(department);
            List<OapiV2UserListResponse.ListUserResponse> users = listUser(department.getDeptId(), dingtalkConfigVo);
            departmentVo.setUsers(users);
            List<OapiV2DepartmentListsubResponse.DeptBaseResponse> subDepartments = listDepartment(department.getDeptId(), dingtalkConfigVo);
            departmentVo.setChildren(buildDepartmentTree(subDepartments, dingtalkConfigVo));
            departmentVos.add(departmentVo);
        }
        return departmentVos;
    }

    private static List<OapiV2DepartmentListsubResponse.DeptBaseResponse> listDepartment(Long deptId, DingtalkConfigVo dingtalkConfigVo) throws ApiException {
        DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsub");
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(deptId);
        OapiV2DepartmentListsubResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> results = rsp.getResult();
        return results;
    }

    private static List<OapiV2UserListResponse.ListUserResponse> listUser(Long deptId, DingtalkConfigVo dingtalkConfigVo) throws ApiException {
        DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");

        List<OapiV2UserListResponse.ListUserResponse> users = Lists.newArrayList();
        Boolean hasMore = true;
        Long cursor = 0L;
        while (BooleanUtils.isTrue(hasMore)) {
            OapiV2UserListRequest req = new OapiV2UserListRequest();
            req.setDeptId(deptId);
            req.setCursor(cursor);
            req.setSize(100L);
            OapiV2UserListResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
            OapiV2UserListResponse.PageResult pageResult = rsp.getResult();
            hasMore = pageResult.getHasMore();
            cursor = pageResult.getNextCursor();

            users.addAll(pageResult.getList());
        }

        return users;
    }

    public static void createWsClient(String clientId, String clientSecret, GenericEventListener listener) throws Exception {
        if (WS_CLIENT_MAP.containsKey(clientId)) {
            return;
        }

        OpenDingTalkClient client = OpenDingTalkStreamClientBuilder
                .custom()
                .credential(new AuthClientCredential(clientId, clientSecret))
                // 注册事件监听
                .registerAllEventListener(listener)
                // 注册机器人监听器
                .registerCallbackListener("/v1.0/im/bot/messages/get", robotMessage -> {
                    LOG.info("receive robotMessage, {}", robotMessage);
                    // 开发者根据自身业务需求，处理机器人回调
                    return new JSONObject();
                })
                .build();
        WS_CLIENT_MAP.put(clientId, client);
        client.start();
    }

    public static void removeWsClient(String clientId) throws Exception {
        OpenDingTalkClient client = WS_CLIENT_MAP.remove(clientId);
        if (client != null) {
            client.stop();
        }
    }

    public static OapiV2UserGetResponse.UserGetResponse getUserByUserid(String userid, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userid);
            OapiV2UserGetResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
            return rsp.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static OapiV2DepartmentGetResponse.DeptGetResponse getDepartmentByDeptId(Long deptId, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
            OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
            req.setDeptId(deptId);
            OapiV2DepartmentGetResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
            return rsp.getResult();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendTextMessage(String subject, List<DingtalkWorkRecordEntity> entities, DingtalkConfigVo dingtalkConfigVo) {
        DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        Long agentId = Long.valueOf(dingtalkConfigVo.getAgentId());
        entities.forEach(recordEntity -> {
            try {
                OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
                request.setAgentId(agentId);
                request.setUseridList(StringUtils.replace(recordEntity.getUserId(), ";", ","));
                OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();

                ActionCardMessage message = JsonUtils.json2Object(recordEntity.getContent(), ActionCardMessage.class);
                OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard = new OapiMessageCorpconversationAsyncsendV2Request.ActionCard();
                actionCard.setTitle(StringUtils.isNotBlank(message.getSubject()) ? message.getSubject() : subject);
                actionCard.setMarkdown(message.getMarkdown());
                actionCard.setSingleTitle(message.getSingleTitle());
                actionCard.setSingleUrl(message.getSingleUrl());

                msg.setMsgtype("action_card");
                msg.setActionCard(actionCard);
                request.setMsg(msg);
                OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    recordEntity.setState(DingtalkWorkRecordEntity.State.Sent);
                    recordEntity.setMsgTaskId(String.valueOf(rsp.getTaskId()));
                } else {
                    recordEntity.setErrMsg(rsp.getErrmsg());
                }
            } catch (ApiException e) {
                recordEntity.setErrMsg(e.getErrMsg());
                LOG.error(e.getErrMsg(), e);
            }
        });
    }

    /**
     * 获取工作通知消息发送结果
     * https://open.dingtalk.com/document/orgapp/gets-the-result-of-sending-messages-asynchronously-to-the-enterprise
     *
     * @param recordEntity
     * @param dingtalkConfigVo
     * @return
     */
    public static OapiMessageCorpconversationGetsendresultResponse.AsyncSendResult getSendTextMessageResult(DingtalkWorkRecordEntity recordEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/getsendresult");
            OapiMessageCorpconversationGetsendresultRequest req = new OapiMessageCorpconversationGetsendresultRequest();
            req.setAgentId(Long.valueOf(dingtalkConfigVo.getAgentId()));
            req.setTaskId(Long.valueOf(recordEntity.getMsgTaskId()));
            OapiMessageCorpconversationGetsendresultResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
            return rsp.getSendResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getUri(String redirectUri) {
        String uri = redirectUri;
        if (StringUtils.isBlank(uri)) {
            uri = "http://127.0.0.1:7001";
        } else if (StringUtils.endsWith(uri, "/")) {
            uri = redirectUri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    public static String getMobileUrlByPcUrl(String pcUrl, String mobileUri, String system) {
        String uri = getUri(mobileUri);
        String mobileUrl = String.format("%s/#/packages/_/pages/workflow/work_view?system=%s&%s",
                uri, system, StringUtils.substringAfter(pcUrl, "?"));
        return mobileUrl;
    }

    public static String getPcUrl(String taskInstUuid, String flowInstUuid, String system, String redirectUri) {
        String uri = getUri(redirectUri);
        String url = String.format("%s/sys/%s/_/workflow/work/view/work?taskInstUuid=%s&flowInstUuid=%s&source=dingtalk&_requestCode=%s",
                uri, system, taskInstUuid, flowInstUuid, System.currentTimeMillis());
        return url;
    }

    public static void cancelMessage(List<DingtalkWorkRecordEntity> workRecordEntities, DingtalkConfigVo dingtalkConfigVo) {
        DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/recall");
        Long agentId = Long.valueOf(dingtalkConfigVo.getAgentId());
        workRecordEntities.forEach(recordEntity -> {
            try {
                if (StringUtils.isBlank(recordEntity.getMsgTaskId())) {
                    return;
                }
                OapiMessageCorpconversationRecallRequest req = new OapiMessageCorpconversationRecallRequest();
                req.setAgentId(agentId);
                req.setMsgTaskId(Long.valueOf(recordEntity.getMsgTaskId()));
                OapiMessageCorpconversationRecallResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    recordEntity.setState(DingtalkWorkRecordEntity.State.Cancelled);
                } else {
                    recordEntity.setErrMsg(rsp.getErrmsg());
                }
            } catch (ApiException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    public static void createChat(DingtalkWorkRecordEntity recordEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfigVo.getConfiguration().getGroupChat();
            String groupName = recordEntity.getTitle();
            String content = recordEntity.getContent();
            if (StringUtils.isNotBlank(content)) {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("groupName")) {
                    groupName = jsonObject.getString("groupName");
                }
            }
            String ownerId = recordEntity.getOwnerId();

            // 创建场景群聊
            if (groupChat.isUseTemplate()) {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/im/chat/scenegroup/create");
                OapiImChatScenegroupCreateRequest req = new OapiImChatScenegroupCreateRequest();
                req.setOwnerUserId(ownerId);
                req.setUserIds(StringUtils.replace(recordEntity.getUserId(), ";", ","));
                req.setShowHistoryType(1L);
                req.setSearchable(1L);
                req.setValidationType(0L);
                req.setMentionAllAuthority(0L);
                req.setManagementType(1L);
                req.setChatBannedType(0L);
                req.setTitle(groupName);
                req.setTemplateId(groupChat.getTemplateId());
                OapiImChatScenegroupCreateResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    recordEntity.setChatId(rsp.getResult().getChatId());
                    recordEntity.setConversationId(rsp.getResult().getOpenConversationId());
                    recordEntity.setState(DingtalkWorkRecordEntity.State.Sent);
                } else {
                    recordEntity.setErrMsg(rsp.getErrmsg());
                }
            } else {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/chat/create");
                List<String> userIds = Arrays.asList(StringUtils.split(recordEntity.getUserId(), Separator.SEMICOLON.getValue()));

                OapiChatCreateRequest req = new OapiChatCreateRequest();
                req.setName(groupName);
                req.setOwner(ownerId);
                req.setUseridlist(userIds);
                req.setShowHistoryType(1L);
                req.setSearchable(1L);
                req.setValidationType(0L);
                req.setMentionAllAuthority(0L);
                req.setManagementType(1L);
                req.setChatBannedType(0L);
                OapiChatCreateResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    recordEntity.setChatId(rsp.getChatid());
                    recordEntity.setConversationId(rsp.getOpenConversationId());
                    recordEntity.setState(DingtalkWorkRecordEntity.State.Sent);
                } else {
                    recordEntity.setErrMsg(rsp.getErrmsg());
                }
            }
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            recordEntity.setErrMsg(e.getErrMsg());
        }
    }

    public static void sendCreateChatMessage(String subject, DingtalkWorkRecordEntity recordEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfigVo.getConfiguration().getGroupChat();
            if (groupChat.isUseTemplate()) {
                Map<String, Object> sampleActionCard = Maps.newHashMap();
                sampleActionCard.put("title", subject);
                sampleActionCard.put("markdown", "### " + subject + "\n" + recordEntity.getTitle());
                sampleActionCard.put("btn_orientation", "1");
                sampleActionCard.put("btn_title_1", "查看流程");
                sampleActionCard.put("action_url_1", getMobileUrlByPcUrl(recordEntity.getUrl(), dingtalkConfigVo.getMobileAppUri(), recordEntity.getSystem()));

                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/im/chat/scencegroup/message/send_v2");
                OapiImChatScencegroupMessageSendV2Request req = new OapiImChatScencegroupMessageSendV2Request();
                req.setTargetOpenConversationId(recordEntity.getConversationId());
                req.setMsgTemplateId("inner_app_template_action_card");
                // com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject().fluentPut("title", "测试").fluentPut("markdown_content", "# 测试内容 \n > 测试");
                req.setMsgParamMap(JsonUtils.object2Json(sampleActionCard));
                req.setRobotCode(groupChat.getRobotId());
                OapiImChatScencegroupMessageSendV2Response rsp = client.execute(req, createAccessToken(dingtalkConfigVo));
                LOG.info("sendCreateChatMessage rsp: {}", rsp.getBody());
//                com.aliyun.dingtalkrobot_1_0.Client client = createRobotClient(dingtalkConfigVo.getClientId(), dingtalkConfigVo.getClientSecret(), dingtalkConfigVo.getServiceUri());
//                com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders orgGroupSendHeaders = new com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders();
//                orgGroupSendHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
//                com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest orgGroupSendRequest = new com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest()
//                        .setMsgParam(JsonUtils.object2Json(sampleActionCard))
//                        .setMsgKey("sampleActionCard")
//                        .setRobotCode(groupChat.getRobotId())
//                        .setOpenConversationId(recordEntity.getConversationId());
//
//                client.orgGroupSendWithOptions(orgGroupSendRequest, orgGroupSendHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            } else {
                LOG.warn("not support send create chat message for dingtalk");
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            LOG.error(err.getMessage(), err);
        }
    }

    public static void sendUserChatMessage(UserDetails userDetails, String actionName, String opinionText, DingtalkWorkRecordEntity recordEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfigVo.getConfiguration().getGroupChat();
            if (groupChat.isUseTemplate()) {
                Map<String, String> sampleText = Maps.newHashMap();
                String text = userDetails.getUserName() + " 已" + actionName;
                if (StringUtils.isNotBlank(opinionText)) {
                    text += ": " + opinionText;
                }
                sampleText.put("content", text);

                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/im/chat/scencegroup/message/send_v2");
                OapiImChatScencegroupMessageSendV2Request req = new OapiImChatScencegroupMessageSendV2Request();
                req.setTargetOpenConversationId(recordEntity.getConversationId());
                req.setMsgTemplateId("inner_app_template_text");
                // com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject().fluentPut("title", "测试").fluentPut("markdown_content", "# 测试内容 \n > 测试");
                req.setMsgParamMap(JsonUtils.object2Json(sampleText));
                req.setRobotCode(groupChat.getRobotId());
                OapiImChatScencegroupMessageSendV2Response rsp = client.execute(req, createAccessToken(dingtalkConfigVo));
                LOG.info("sendUserChatMessage rsp: {}", rsp.getBody());
//                com.aliyun.dingtalkrobot_1_0.Client client = createRobotClient(dingtalkConfigVo.getClientId(), dingtalkConfigVo.getClientSecret(), dingtalkConfigVo.getServiceUri());
//                com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders orgGroupSendHeaders = new com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendHeaders();
//                orgGroupSendHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
//                com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest orgGroupSendRequest = new com.aliyun.dingtalkrobot_1_0.models.OrgGroupSendRequest()
//                        .setMsgParam(JsonUtils.object2Json(sampleText))
//                        .setMsgKey("sampleText")
//                        .setRobotCode(groupChat.getRobotId())
//                        .setOpenConversationId(recordEntity.getConversationId());
//
//                client.orgGroupSendWithOptions(orgGroupSendRequest, orgGroupSendHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            } else {
                LOG.warn("not support send create chat message for dingtalk");
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            LOG.error(err.getMessage(), err);
        }
    }

    public static void addChatMembers(DingtalkWorkRecordEntity workRecord, Map<String, String> userIdMap, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfigVo.getConfiguration().getGroupChat();
            if (groupChat.isUseTemplate()) {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/im/chat/scenegroup/member/add");
                OapiImChatScenegroupMemberAddRequest req = new OapiImChatScenegroupMemberAddRequest();
                req.setOpenConversationId(workRecord.getConversationId());
                req.setUserIds(StringUtils.join(userIdMap.keySet(), ","));
                OapiImChatScenegroupMemberAddResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    Set<String> oaUserIds = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(workRecord.getOaUserId(), Separator.SEMICOLON.getValue())));
                    Set<String> userIds = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(workRecord.getUserId(), Separator.SEMICOLON.getValue())));
                    oaUserIds.addAll(userIdMap.values());
                    userIds.addAll(userIdMap.keySet());
                    workRecord.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
                    workRecord.setUserId(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
                } else {
                    workRecord.setErrMsg(rsp.getErrmsg());
                }
            } else {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/chat/update");
                OapiChatUpdateRequest req = new OapiChatUpdateRequest();
                req.setChatid(workRecord.getChatId());
                req.setOwner(workRecord.getOwnerId());
                req.setOwnerType("emp");
                req.setAddUseridlist(Lists.newArrayList(userIdMap.keySet()));
                OapiChatUpdateResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    List<String> oaUserIds = Lists.newArrayList(StringUtils.split(workRecord.getOaUserId(), Separator.SEMICOLON.getValue()));
                    List<String> userIds = Lists.newArrayList(StringUtils.split(workRecord.getUserId(), Separator.SEMICOLON.getValue()));
                    oaUserIds.addAll(userIdMap.values());
                    userIds.addAll(userIdMap.keySet());
                    workRecord.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
                    workRecord.setUserId(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
                } else {
                    workRecord.setErrMsg(rsp.getErrmsg());
                }
            }
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            workRecord.setErrMsg(e.getMessage());
        }
    }

    public static void cancelGroupChat(DingtalkWorkRecordEntity workRecord, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfigVo.getConfiguration().getGroupChat();
            if (groupChat.isUseTemplate()) {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/im/chat/scenegroup/member/delete");
                OapiImChatScenegroupMemberDeleteRequest req = new OapiImChatScenegroupMemberDeleteRequest();
                req.setOpenConversationId(workRecord.getConversationId());
                req.setUserIds(StringUtils.replace(workRecord.getUserId(), ";", ","));
                OapiImChatScenegroupMemberDeleteResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    workRecord.setState(DingtalkWorkRecordEntity.State.Cancelled);
                } else {
                    workRecord.setErrMsg(rsp.getErrmsg());
                }
            } else {
                DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/chat/update");
                OapiChatUpdateRequest req = new OapiChatUpdateRequest();
                req.setChatid(workRecord.getChatId());
                req.setOwner(workRecord.getOwnerId());
                req.setOwnerType("emp");
                req.setDelUseridlist(Arrays.asList(StringUtils.split(workRecord.getUserId(), Separator.SEMICOLON.getValue())));
                OapiChatUpdateResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
                if (rsp.isSuccess()) {
                    workRecord.setState(DingtalkWorkRecordEntity.State.Cancelled);
                } else {
                    workRecord.setErrMsg(rsp.getErrmsg());
                }
            }
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            workRecord.setErrMsg(e.getMessage());
        }
    }

    public static void createTodoTask(DingtalkTodoTaskEntity taskEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            TodoTaskMessage todoTaskMessage = JsonUtils.json2Object(taskEntity.getContent(), TodoTaskMessage.class);
            String unionId = taskEntity.getOwnerUnionId();
            List<String> executorIds = Arrays.asList(StringUtils.split(taskEntity.getUserUnionId(), Separator.SEMICOLON.getValue()));

            com.aliyun.dingtalktodo_1_0.Client client = createTodoClient(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskHeaders createTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskHeaders();
            createTodoTaskHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest.CreateTodoTaskRequestDetailUrl detailUrl = new com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest.CreateTodoTaskRequestDetailUrl()
                    .setPcUrl(todoTaskMessage.getPcUrl())
                    .setAppUrl(todoTaskMessage.getAppUrl());
            com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest createTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest()
                    .setDetailUrl(detailUrl)
                    .setExecutorIds(executorIds)
                    .setSubject(todoTaskMessage.getSubject())
                    .setContentFieldList(getContentFieldList(todoTaskMessage));
            CreateTodoTaskResponse rsp = client.createTodoTaskWithOptions(unionId, createTodoTaskRequest, createTodoTaskHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            CreateTodoTaskResponseBody body = rsp.getBody();
            if (body != null && StringUtils.isNotBlank(body.getId())) {
                taskEntity.setDtTaskId(body.getId());
                taskEntity.setState(DingtalkTodoTaskEntity.State.Sent);
            } else {
                taskEntity.setErrMsg(rsp.toString());
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            taskEntity.setErrMsg(err.getMessage());
        }
    }

    private static List<CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList> getContentFieldList(TodoTaskMessage todoTaskMessage) {
        List<CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList> contentFieldList = Lists.newArrayList();
        LinkedHashMap<String, Object> contentFieldMap = todoTaskMessage.getContentFieldList();
        if (MapUtils.isNotEmpty(contentFieldMap)) {
            contentFieldMap.forEach((k, v) -> {
                CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList contentField = new CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList()
                        .setFieldKey(k)
                        .setFieldValue(Objects.toString(v, StringUtils.EMPTY));
                contentFieldList.add(contentField);
            });
        }
        return contentFieldList;
    }

    public static void updateTodoTask(DingtalkTodoTaskEntity taskEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            String unionId = taskEntity.getOwnerUnionId();
            List<String> executorIds = Arrays.asList(StringUtils.split(taskEntity.getUserUnionId(), Separator.SEMICOLON.getValue()));

            com.aliyun.dingtalktodo_1_0.Client client = createTodoClient(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskHeaders updateTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskHeaders();
            updateTodoTaskHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskRequest updateTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskRequest()
                    .setExecutorIds(executorIds)
                    .setSubject(taskEntity.getTitle());
            UpdateTodoTaskResponse rsp = client.updateTodoTaskWithOptions(unionId, taskEntity.getDtTaskId(), updateTodoTaskRequest, updateTodoTaskHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            if (!BooleanUtils.isTrue(rsp.getBody().getResult())) {
                taskEntity.setErrMsg(rsp.toString());
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            taskEntity.setErrMsg(err.getMessage());
        }
    }

    public static void completeTodoTask(DingtalkTodoTaskEntity taskEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            String unionId = taskEntity.getOwnerUnionId();

            com.aliyun.dingtalktodo_1_0.Client client = createTodoClient(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskHeaders updateTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskHeaders();
            updateTodoTaskHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskRequest updateTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskRequest()
                    .setDone(true)
                    .setSubject(taskEntity.getTitle());
            UpdateTodoTaskResponse rsp = client.updateTodoTaskWithOptions(unionId, taskEntity.getDtTaskId(), updateTodoTaskRequest, updateTodoTaskHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            if (BooleanUtils.isTrue(rsp.getBody().getResult())) {
                taskEntity.setState(DingtalkTodoTaskEntity.State.Completed);
            } else {
                taskEntity.setErrMsg(rsp.toString());
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            taskEntity.setErrMsg(err.getMessage());
        }
    }

    public static void cancelTodoTask(DingtalkTodoTaskEntity taskEntity, DingtalkConfigVo dingtalkConfigVo) {
        try {
            com.aliyun.dingtalktodo_1_0.Client client = createTodoClient(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskHeaders deleteTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskHeaders();
            deleteTodoTaskHeaders.xAcsDingtalkAccessToken = getAccessToken(dingtalkConfigVo);
            com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskRequest deleteTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskRequest();

            String unionId = taskEntity.getOwnerUnionId();
            String dtTaskId = taskEntity.getDtTaskId();
            DeleteTodoTaskResponse rsp = client.deleteTodoTaskWithOptions(unionId, dtTaskId, deleteTodoTaskRequest, deleteTodoTaskHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            DeleteTodoTaskResponseBody body = rsp.getBody();
            if (body != null && BooleanUtils.isTrue(rsp.getBody().getResult())) {
                taskEntity.setState(DingtalkTodoTaskEntity.State.Cancelled);
            } else {
                taskEntity.setErrMsg(rsp.toString());
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            taskEntity.setErrMsg(err.getMessage());
        }
    }

    public static String getUserAccessToken(String authCode, DingtalkConfigVo dingtalkConfigVo) {
        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = createAuthClient(dingtalkConfigVo);
            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                    // 应用基础信息-应用信息的AppKey,请务必替换为开发的应用AppKey
                    .setClientId(dingtalkConfigVo.getClientId())
                    // 应用基础信息-应用信息的AppSecret，,请务必替换为开发的应用AppSecret
                    .setClientSecret(dingtalkConfigVo.getClientSecret())
                    .setCode(authCode)
                    .setGrantType("authorization_code");
            GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
            // 获取用户个人token
            return getUserTokenResponse.getBody().getAccessToken();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static GetUserResponseBody getUserInfo(String accessToken, DingtalkConfigVo dingtalkConfigVo) {
        try {
            com.aliyun.dingtalkcontact_1_0.Client client = createContactClient(dingtalkConfigVo);
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = accessToken;
            // 获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
            GetUserResponse userResponse = client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            return userResponse.getBody();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static OapiV2UserGetuserinfoResponse.UserGetByCodeResponse getUserInfoByCode(String code, DingtalkConfigVo dingtalkConfigVo) {
        try {
            DingTalkClient client = getDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getuserinfo");
            OapiV2UserGetuserinfoRequest req = new OapiV2UserGetuserinfoRequest();
            req.setCode(code);
            OapiV2UserGetuserinfoResponse rsp = client.execute(req, getAccessToken(dingtalkConfigVo));
            return rsp.getResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }


    private static class AccessToken extends BaseObject {
        private String accessToken;
        private long expireIn;
        private Date expireTime;

        public AccessToken(String accessToken, long expireIn) {
            this.accessToken = accessToken;
            this.expireIn = expireIn;
            initExpireTime(expireIn);
        }

        private void initExpireTime(long expireIn) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, (int) expireIn - 1);
            this.expireTime = calendar.getTime();
        }

        public String getToken() {
            if (isExpire()) {
                return null;
            }
            return this.accessToken;
        }

        private boolean isExpire() {
            return expireTime.before(Calendar.getInstance().getTime());
        }
    }

}
