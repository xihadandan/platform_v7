package com.wellsoft.pt.app.feishu.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.okhttp.Headers;
import com.lark.oapi.okhttp.OkHttpClient;
import com.lark.oapi.okhttp.Request;
import com.lark.oapi.okhttp.Response;
import com.lark.oapi.service.auth.v3.model.*;
import com.lark.oapi.service.authen.v1.model.*;
import com.lark.oapi.service.contact.v3.enums.ChildrenDepartmentDepartmentIdTypeEnum;
import com.lark.oapi.service.contact.v3.enums.ChildrenDepartmentUserIdTypeEnum;
import com.lark.oapi.service.contact.v3.enums.FindByDepartmentUserDepartmentIdTypeEnum;
import com.lark.oapi.service.contact.v3.enums.FindByDepartmentUserUserIdTypeEnum;
import com.lark.oapi.service.contact.v3.model.*;
import com.lark.oapi.service.im.v1.model.*;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.app.feishu.model.*;
import com.wellsoft.pt.app.feishu.support.FeishuMessage;
import com.wellsoft.pt.app.feishu.support.response.BotInfoResponse;
import com.wellsoft.pt.app.feishu.support.response.TenantAccessTokenResponse;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 飞书api工具类
 */
public class FeishuApiUtils {

    private static final Logger logger = LoggerFactory.getLogger(FeishuApiUtils.class);

    private static Map<String, com.lark.oapi.ws.Client> wsClientMap = Maps.newHashMap();

    private static Map<String, Client> clientMap = Maps.newHashMap();

    private static Map<String, String> appIdSecretMap = Maps.newHashMap();

    private static Map<String, BotInfo> botInfoMap = Maps.newHashMap();

    private static Client getClient(String appId, String appSecret, String baseUrl) {
        if (appIdSecretMap.containsKey(appId)) {
            String existSecret = appIdSecretMap.get(appId);
            if (StringUtils.equals(existSecret, appSecret)) {
                Client client = clientMap.get(appId + appSecret);
                if (client != null) {
                    return client;
                }
            } else {
                clientMap.remove(appId + existSecret);
            }
        }

        // 构建client
        Client.Builder builder = Client.newBuilder(appId, appSecret);
        if (StringUtils.isNotBlank(baseUrl)) {
            builder.openBaseUrl(baseUrl);
        }
        Client client = builder.build();
        clientMap.put(appId + appSecret, client);
        appIdSecretMap.put(appId, appSecret);
        return client;
    }

    private static Client getClient(FeishuConfigVo feishuConfigVo) {
        return getClient(feishuConfigVo.getAppId(), feishuConfigVo.getAppSecret(), feishuConfigVo.getServiceUri());
    }

    public static com.lark.oapi.ws.Client createWsClient(FeishuConfigVo feishuConfigVo, EventDispatcher eventDispatcher) {
        String appId = feishuConfigVo.getAppId();
        String appSecret = feishuConfigVo.getAppSecret();
        if (wsClientMap.containsKey(appId) && StringUtils.equals(appIdSecretMap.get(appId), appSecret)) {
            return wsClientMap.get(feishuConfigVo.getAppId());
        } else if (wsClientMap.containsKey(appId)) {
            wsClientMap.remove(appId);
        }

        com.lark.oapi.ws.Client cli = new com.lark.oapi.ws.Client.Builder(appId, appSecret)
                .eventHandler(eventDispatcher)
                .build();
        cli.start();
        wsClientMap.put(appId, cli);
        appIdSecretMap.put(appId, appSecret);
        return cli;
    }

    /**
     * 获取飞书部门+用户数据
     *
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static List<DepartmentNode> departments(String appId, String appSecret, String baseUrl) {
        try {
            Client client = getClient(appId, appSecret, baseUrl);// Client.newBuilder(appId, appSecret).build()
            String accessToken = createAccessToken(appId, appSecret, baseUrl);
            // 存储所有部门信息
            List<Department> allDepartments = new ArrayList<>();
            // 初始分页参数
            String pageToken = null;
            boolean hasMore = true;
            // 分页获取所有部门数据
            while (hasMore) {
//                ListDepartmentReq.Builder reqBuilder = ListDepartmentReq.newBuilder()
//                        .userIdType(ListDepartmentUserIdTypeEnum.OPEN_ID) // 用户 ID 类型
//                        .departmentIdType(ListDepartmentDepartmentIdTypeEnum.OPEN_DEPARTMENT_ID) // 部门 ID 类型
//                        .parentDepartmentId("0") // 根部门 ID 为 "0"
//                        .fetchChild(true) // 递归获取子部门
//                        .pageSize(50); // 每页返回的部门数量

                ChildrenDepartmentReq req = ChildrenDepartmentReq.newBuilder()
                        .departmentId("0")
                        .userIdType(ChildrenDepartmentUserIdTypeEnum.OPEN_ID)
                        .departmentIdType(ChildrenDepartmentDepartmentIdTypeEnum.OPEN_DEPARTMENT_ID)
                        .fetchChild(true)
                        .pageSize(50)
                        .build();

                if (pageToken != null) {
                    req.setPageToken(pageToken);
                }

                // 发起请求
                ChildrenDepartmentResp resp = client.contact().v3().department().children(req, RequestOptions.newBuilder()
                        .userAccessToken(accessToken)
                        .build());
                //  ListDepartmentResp resp = client.contact().department().list(reqBuilder.build(), RequestOptions.newBuilder().build());
                if (!resp.success()) {
                    logger.error("同步飞书部门数据失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                    String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                            resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false)
                                    .toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                    throw new BusinessException("同步飞书通讯录获取数据失败: " + errorMsg);
                }
                allDepartments.addAll(Arrays.asList(resp.getData().getItems()));
                hasMore = resp.getData().getHasMore();
                pageToken = resp.getData().getPageToken();
            }
            // 构建部门树形结构
            Map<String, List<Department>> departmentMap = allDepartments.stream()
                    .collect(Collectors.groupingBy(Department::getParentDepartmentId));
            List<DepartmentNode> departmentTree = buildDepartmentTree("0", departmentMap, client, accessToken);
            return departmentTree;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("同步飞书通讯录失败: ", e);
        }
    }


    // 递归构建部门树
    private static List<DepartmentNode> buildDepartmentTree(String parentId, Map<String, List<Department>> departmentMap,
                                                            Client client, String accessToken) throws Exception {
        List<DepartmentNode> result = new ArrayList<>();
        List<Department> children = departmentMap.getOrDefault(parentId, Collections.emptyList());
        for (Department department : children) {
            DepartmentNode node = new DepartmentNode();
            node.setDepartment(department);
            // 获取部门下的用户数据
            if (department.getMemberCount() != null && department.getMemberCount() > 0) {
                List<User> users = fetchUsersByDepartment(department.getOpenDepartmentId(), client, accessToken);
                node.setUsers(users);
            }
            // 递归获取子部门
            node.setChildren(buildDepartmentTree(department.getOpenDepartmentId(), departmentMap, client, accessToken));
            result.add(node);
        }
        return result;
    }

    // 获取部门下的用户数据
    private static List<User> fetchUsersByDepartment(String departmentId, Client client, String accessToken) throws Exception {
        List<User> users = new ArrayList<>();
        String pageToken = null;
        boolean hasMore = true;
        while (hasMore) {
//            ListUserReq.Builder reqBuilder = ListUserReq.newBuilder()
//                    .departmentId(departmentId) // 部门 ID
//                    .departmentIdType(ListUserDepartmentIdTypeEnum.OPEN_DEPARTMENT_ID) // 部门 ID 类型
//                    .userIdType(ListUserUserIdTypeEnum.OPEN_ID) // 用户 ID 类型
//                    .pageSize(50); // 每页返回的用户数量
//            if (pageToken != null) {
//                reqBuilder.pageToken(pageToken);
//            }
//            ListUserResp resp = client.contact().user().list(reqBuilder.build(), RequestOptions.newBuilder().build());
//            if (resp.getCode() != 0) {
//                logger.error("同步飞书用户数据失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
//                throw new RuntimeException("同步飞书用户数据失败");
//            }
//            users.addAll(Arrays.asList(resp.getData().getItems()));
//            hasMore = resp.getData().getHasMore();
//            pageToken = resp.getData().getPageToken();
            // 创建请求对象
            FindByDepartmentUserReq req = FindByDepartmentUserReq.newBuilder()
                    .userIdType(FindByDepartmentUserUserIdTypeEnum.OPEN_ID)
                    .departmentIdType(FindByDepartmentUserDepartmentIdTypeEnum.OPEN_DEPARTMENT_ID)
                    .departmentId(departmentId)
                    .pageSize(50)
                    .build();

            if (pageToken != null) {
                req.setPageToken(pageToken);
            }

            // 发起请求
            FindByDepartmentUserResp resp = client.contact().v3().user().findByDepartment(req, RequestOptions.newBuilder()
                    .userAccessToken(accessToken)
                    .build());
            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("同步飞书用户数据失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                throw new BusinessException("同步飞书用户数据失败: " + errorMsg);
            }

            User[] items = resp.getData().getItems();
            if (items != null) {
                users.addAll(Arrays.asList(items));
            }
            hasMore = resp.getData().getHasMore();
            pageToken = resp.getData().getPageToken();
        }
        return users;
    }

    // 打印部门树
    public static void printDepartmentTree(List<DepartmentNode> tree, int level) {
        for (DepartmentNode node : tree) {
            String prefix = new String(new char[level]).replace("\0", "  ");
            System.out.println(prefix + "部门名称：" + node.getDepartment().getName());
            System.out.println(prefix + "部门 ID：" + node.getDepartment().getOpenDepartmentId());
            System.out.println(prefix + "成员数量：" + node.getDepartment().getMemberCount());
            System.out.println(prefix + "成员信息：");
            if (CollectionUtils.isNotEmpty(node.getUsers())) {
                node.getUsers().forEach(user -> System.out.println(String.format("%s", JSON.toJSONString(user))));
            }
            System.out.println(prefix + "----------");
            printDepartmentTree(node.getChildren(), level + 1);
        }
    }

    public static Department getDepartmentByDeptId(String departmentId, FeishuConfigVo feishuConfigVo) {
        try {
            Client client = getClient(feishuConfigVo);
            // 创建请求对象
            GetDepartmentReq req = GetDepartmentReq.newBuilder()
                    .userIdType("open_id")
                    .departmentIdType("open_department_id")
                    .departmentId(departmentId)
                    .build();

            // 发起请求
            GetDepartmentResp resp = client.contact().v3().department().get(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("获取部门信息失败，错误码：{}，错误信息：{}", resp.getCode(), errorMsg);
                return null;
            }

            // 业务数据处理
            return resp.getData().getDepartment();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String createAccessToken(String appId, String appSecret, String baseUrl) {
        try {
            // 构建client
//            Client.Builder builder = Client.newBuilder(appId, appSecret);
//            if (StringUtils.isNotBlank(baseUrl)) {
//                builder.openBaseUrl(baseUrl);
//            }
            Client client = getClient(appId, appSecret, baseUrl);// builder.build();

            // 创建请求对象
            InternalTenantAccessTokenReq req = InternalTenantAccessTokenReq.newBuilder()
                    .internalTenantAccessTokenReqBody(InternalTenantAccessTokenReqBody.newBuilder()
                            .appId(appId)
                            .appSecret(appSecret)
                            .build())
                    .build();

            // 发起请求
            InternalTenantAccessTokenResp resp = client.auth().v3().tenantAccessToken().internal(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("获取应用访问凭证失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                throw new RuntimeException("获取应用访问凭证失败: " + errorMsg);
            }

            // 业务数据处理
            TenantAccessTokenResponse accessTokenResponse = JsonUtils.json2Object(new String(resp.getRawResponse().getBody()), TenantAccessTokenResponse.class);
            return accessTokenResponse.getTenantAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createAccessToken(String appId, String appSecret) {
        return createAccessToken(appId, appSecret, "https://open.feishu.cn");
    }

    public static void sendTextMessage(String subject, List<FeishuWorkRecordEntity> entities, FeishuConfigVo feishuConfigVo) {
        // 构建client
        Client client = getClient(feishuConfigVo);// Client.newBuilder(appId, appSecret).build();
        entities.forEach(workRecord -> {
            try {
                TextMessage textMessage = null;
                String content = workRecord.getContent();
                if (StringUtils.isNotBlank(content)) {
                    textMessage = JsonUtils.json2Object(content, TextMessage.class);
                } else {
                    textMessage = new TextMessage();
                    textMessage.setSubject(subject);
                    textMessage.setTitle(workRecord.getTitle());
                    textMessage.setUrl(workRecord.getUrl());
                }
                // 创建请求对象
                CreateMessageReq req = CreateMessageReq.newBuilder()
                        .receiveIdType("open_id")
                        .createMessageReqBody(CreateMessageReqBody.newBuilder()
                                .receiveId(workRecord.getOpenId())
                                .msgType("interactive")
                                .content(buildCardContent(textMessage, feishuConfigVo))
                                .build())
                        .build();

                // 发起请求
                CreateMessageResp resp = client.im().v1().message().create(req);

                // 处理服务端错误
                if (!resp.success()) {
                    String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                            resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                    logger.error("发送飞书消息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                    // throw new RuntimeException("发送飞书消息: " + errorMsg);
                    workRecord.setErrMsg(errorMsg);
                    return;
                }

                // 业务数据处理
                CreateMessageRespBody data = resp.getData();
                if (data != null) {
                    workRecord.setState(FeishuWorkRecordEntity.State.Sent);
                    workRecord.setMessageId(data.getMessageId());
                    workRecord.setChatId(data.getChatId());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public static void sendTextMessage(String subject, FeishuWorkRecordEntity workRecord, String openId, String msg,
                                       FeishuConfigVo feishuConfigVo) {
        // 构建client
        Client client = getClient(feishuConfigVo);// Client.newBuilder(appId, appSecret).build();
        try {
            TextMessage textMessage = new TextMessage();
            textMessage.setSubject(subject);
            textMessage.setTitle(workRecord.getTitle());
            textMessage.setUrl(workRecord.getUrl());
            textMessage.setText(msg);
            // 创建请求对象
            CreateMessageReq req = CreateMessageReq.newBuilder()
                    .receiveIdType("open_id")
                    .createMessageReqBody(CreateMessageReqBody.newBuilder()
                            .receiveId(openId)
                            .msgType("interactive")
                            .content(buildCardContent(textMessage, feishuConfigVo))
                            .build())
                    .build();

            // 发起请求
            CreateMessageResp resp = client.im().v1().message().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("发送飞书消息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                // throw new RuntimeException("发送飞书消息: " + errorMsg);
                workRecord.setErrMsg(errorMsg);
                return;
            }

            // 业务数据处理
            CreateMessageRespBody data = resp.getData();
            if (data != null) {
                workRecord.setState(FeishuWorkRecordEntity.State.Sent);
                workRecord.setMessageId(data.getMessageId());
                workRecord.setChatId(data.getChatId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String buildCardContent(TextMessage textMessage, FeishuConfigVo feishuConfigVo) {
        CardMessage cardMessage = new CardMessage();
        String subject = textMessage.getSubject();
        String title = textMessage.getTitle();
        String text = textMessage.getText();
        String url = textMessage.getUrl();
        cardMessage.setTextHeader(subject);
        if (StringUtils.isNotBlank(title)) {
            String mobileUrl = null;
            if (StringUtils.isNotBlank(feishuConfigVo.getMobileAppUri())) {
                mobileUrl = getMobileUrlByPcUrl(url, feishuConfigVo.getMobileAppUri(), feishuConfigVo.getSystem());
            }
            if (StringUtils.isBlank(mobileUrl)) {
                mobileUrl = url;
            }
            cardMessage.addLinkElement(title, url, mobileUrl, feishuConfigVo);
        }
        if (StringUtils.isNotBlank(text)) {
            cardMessage.addDivElement(text);
        }
        return JsonUtils.object2Json(cardMessage);
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
        String url = String.format("%s/sys/%s/_/workflow/work/view/work?taskInstUuid=%s&flowInstUuid=%s&source=feishu&_requestCode=%s",
                uri, system, taskInstUuid, flowInstUuid, System.currentTimeMillis());
        return url;
    }

    private static String buildContent(TextMessage textMessage) {
        Map<String, Object> content = Maps.newHashMap();
        String text = textMessage.getText();
        String subject = textMessage.getSubject();
        String title = textMessage.getTitle();
        String url = textMessage.getUrl();
        if (StringUtils.isNotBlank(text)) {
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            sb.append("\n").append(text);
            content.put("text", sb.toString());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(subject).append(": ").append("[").append(title).append("]").append("(").append(url).append(")");
            content.put("text", sb.toString());
        }
        return JsonUtils.object2Json(content);
    }

    public static void cancelMessage(List<FeishuWorkRecordEntity> workRecordEntities, String appId, String appSecret,
                                     String serviceUri) {
        // 构建client
        Client client = getClient(appId, appSecret, serviceUri);// Client.newBuilder(appId, appSecret).build();

        workRecordEntities.forEach(workRecord -> {
            try {
                // 创建请求对象
                DeleteMessageReq req = DeleteMessageReq.newBuilder()
                        .messageId(workRecord.getMessageId())
                        .build();

                // 发起请求
                DeleteMessageResp resp = client.im().v1().message().delete(req);

                // 处理服务端错误
                if (!resp.success()) {
                    String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                            resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                    logger.error("撤销飞书消息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                    workRecord.setErrMsg(errorMsg);
                    return;
                }

                // 业务数据处理
                workRecord.setState(FeishuWorkRecordEntity.State.Cancelled);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    public static void createChat(FeishuWorkRecordEntity feishuWorkRecord, FeishuConfigVo feishuConfigVo) {
        try {
            String groupName = feishuWorkRecord.getTitle();
            String content = feishuWorkRecord.getContent();
            if (StringUtils.isNotBlank(content)) {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("groupName")) {
                    groupName = jsonObject.getString("groupName");
                }
            }
            String i18nTitle = StringUtils.replace(groupName, "群聊: ", "");
            String ownerId = feishuWorkRecord.getOpenOwnerId();
            String[] openIds = StringUtils.split(feishuWorkRecord.getOpenId(), Separator.SEMICOLON.getValue());
            String appId = feishuConfigVo.getAppId();
            String appSecret = feishuConfigVo.getAppSecret();
            // 构建client
            Client client = getClient(appId, appSecret, feishuConfigVo.getServiceUri());// Client.newBuilder(appId, appSecret).build();

            // 创建请求对象
            CreateChatReq req = CreateChatReq.newBuilder()
                    .userIdType("open_id")
                    .setBotManager(false)
                    .createChatReqBody(CreateChatReqBody.newBuilder()
                            .name(groupName)
                            // .description("测试群描述")
                            .i18nNames(I18nNames.newBuilder()
                                    .zhCn(groupName)
                                    .enUs("group chat: " + i18nTitle)
                                    .jaJp("グループチャット: " + i18nTitle)
                                    .build())
                            .ownerId(ownerId)
                            .userIdList(openIds)
                            .botIdList(new String[]{})
                            .groupMessageType("chat")
                            .chatMode("group")
                            .chatType("private")
                            .joinMessageVisibility("all_members")
                            .leaveMessageVisibility("all_members")
                            .membershipApproval("no_approval_required")
                            .restrictedModeSetting(RestrictedModeSetting.newBuilder()
                                    .status(false)
                                    .screenshotHasPermissionSetting("all_members")
                                    .downloadHasPermissionSetting("all_members")
                                    .messageHasPermissionSetting("all_members")
                                    .build())
                            .urgentSetting("all_members")
                            .videoConferenceSetting("all_members")
                            .editPermission("all_members")
                            .hideMemberCountSetting("all_members")
                            .build())
                    .build();

            // 发起请求
            CreateChatResp resp = client.im().v1().chat().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("创建飞书群聊失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                feishuWorkRecord.setErrMsg(errorMsg);
                return;
            }

            // 业务数据处理
            CreateChatRespBody data = resp.getData();
            if (data != null) {
                feishuWorkRecord.setChatId(data.getChatId());
                feishuWorkRecord.setState(FeishuWorkRecordEntity.State.Sent);
                createViewWorkChatMenu(feishuWorkRecord, feishuConfigVo);
            }
            // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void createViewWorkChatMenu(FeishuWorkRecordEntity feishuWorkRecord, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);
            String chatId = feishuWorkRecord.getChatId();
            String pcUrl = feishuWorkRecord.getUrl();
            String mobileUrl = pcUrl;
            if (StringUtils.isNotBlank(feishuConfigVo.getMobileAppUri())) {
                mobileUrl = getMobileUrlByPcUrl(pcUrl, getUri(feishuConfigVo.getMobileAppUri()), feishuConfigVo.getSystem());
            }

            // 创建请求对象
            CreateChatMenuTreeReq req = CreateChatMenuTreeReq.newBuilder()
                    .chatId(chatId)
                    .createChatMenuTreeReqBody(CreateChatMenuTreeReqBody.newBuilder()
                            .menuTree(ChatMenuTree.newBuilder()
                                    .chatMenuTopLevels(new ChatMenuTopLevel[]{
                                            ChatMenuTopLevel.newBuilder()
                                                    .chatMenuItem(ChatMenuItem.newBuilder()
                                                            .actionType("REDIRECT_LINK")
                                                            .redirectLink(ChatMenuItemRedirectLink.newBuilder()
                                                                    .commonUrl(pcUrl)
                                                                    .iosUrl(mobileUrl)
                                                                    .androidUrl(mobileUrl)
                                                                    .pcUrl(pcUrl)
                                                                    .webUrl(pcUrl)
                                                                    .build())
                                                            .name("查看流程")
                                                            .i18nNames(I18nNames.newBuilder()
                                                                    .zhCn("查看流程")
                                                                    .enUs("View work")
                                                                    .jaJp("プロセスの表示")
                                                                    .build())
                                                            .build())
                                                    .build()
                                    })
                                    .build())
                            .build())
                    .build();

            // 发起请求
            CreateChatMenuTreeResp resp = client.im().v1().chatMenuTree().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("创建飞书群聊菜单失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void sendCreateChatMessage(String subject, FeishuWorkRecordEntity workRecord, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            TextMessage textMessage = new TextMessage();
            textMessage.setSubject(subject);
            textMessage.setTitle(workRecord.getTitle());
            textMessage.setUrl(workRecord.getUrl());

            // 创建请求对象
            CreateMessageReq req = CreateMessageReq.newBuilder()
                    .receiveIdType("chat_id")
                    .createMessageReqBody(CreateMessageReqBody.newBuilder()
                            .receiveId(workRecord.getChatId())
                            .msgType("interactive")
                            .content(buildCardContent(textMessage, feishuConfigVo))
                            .build())
                    .build();

            // 发起请求
            CreateMessageResp resp = client.im().v1().message().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("发送飞书群聊消息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return;
            }

            // 业务数据处理
            // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void cancelGroupChat(FeishuWorkRecordEntity feishuWorkRecord, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 创建请求对象
            DeleteChatReq req = DeleteChatReq.newBuilder()
                    .chatId(feishuWorkRecord.getChatId())
                    .build();

            // 发起请求
            DeleteChatResp resp = client.im().v1().chat().delete(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("飞书解散群聊失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                feishuWorkRecord.setErrMsg(errorMsg);
                return;
            }

            // 业务数据处理
            feishuWorkRecord.setState(FeishuWorkRecordEntity.State.Cancelled);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void sendUserChatMessage(UserDetails userDetails, String actionName, String opinionText, FeishuWorkRecordEntity workRecord, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

//            TextMessage textMessage = new TextMessage();
//            textMessage.setSubject(userDetails.getUserName() + " 已提交");
//            textMessage.setTitle(workRecord.getTitle());
//            textMessage.setUrl(workRecord.getUrl());

            Map<String, String> content = Maps.newHashMap();
            String text = userDetails.getUserName() + " 已" + actionName;
            if (StringUtils.isNotBlank(opinionText)) {
                text += ": " + opinionText;
            }
            content.put("text", text);

            // 创建请求对象
            CreateMessageReq req = CreateMessageReq.newBuilder()
                    .receiveIdType("chat_id")
                    .createMessageReqBody(CreateMessageReqBody.newBuilder()
                            .receiveId(workRecord.getChatId())
                            .msgType("text")
                            .content(JsonUtils.object2Json(content))
                            .build())
                    .build();

            // 发起请求
            CreateMessageResp resp = client.im().v1().message().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("发送飞书群聊消息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return;
            }

            // 业务数据处理
            // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Message> listChatMessage(FeishuWorkRecordEntity workRecord, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 群聊消息列表
            List<Message> messages = new ArrayList<>();
            // 初始分页参数
            String pageToken = null;
            boolean hasMore = true;
            // 分页获取所有部门数据
            while (hasMore) {
                // 创建请求对象
                ListMessageReq req = ListMessageReq.newBuilder()
                        .containerIdType("chat")
                        .containerId(workRecord.getChatId())
                        .sortType("ByCreateTimeAsc")
                        .pageSize(50)
                        .build();

                // 发起请求
                ListMessageResp resp = client.im().v1().message().list(req);

                if (pageToken != null) {
                    req.setPageToken(pageToken);
                }

                // 处理服务端错误
                if (!resp.success()) {
                    String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                            resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                    logger.error("同步飞书用户数据失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                    return Collections.emptyList();
                }

                // 业务数据处理
                messages.addAll(Arrays.asList(resp.getData().getItems()));
                hasMore = resp.getData().getHasMore();
                pageToken = resp.getData().getPageToken();
                // System.out.println(Jsons.DEFAULT.toJson(resp.getData()));
            }
            return messages;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public static String getBotName(String botId, FeishuConfigVo feishuConfigVo) {
        BotInfo botInfo = botInfoMap.get(feishuConfigVo.getAppId());
        if (botInfo != null) {
            return botInfo.getAppName();
        }

        String accessToken = createAccessToken(feishuConfigVo.getAppId(), feishuConfigVo.getAppSecret(), feishuConfigVo.getServiceUri());
        OkHttpClient client = new OkHttpClient();

        // 创建Headers对象并添加请求头
        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + accessToken)
                .build();

        String url = "https://open.feishu.cn/open-apis/bot/v3/info";
        if (StringUtils.isNotBlank(feishuConfigVo.getServiceUri())) {
            url = feishuConfigVo.getServiceUri() + "/open-apis/bot/v3/info";
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 请求成功
            if (response.isSuccessful()) {
                BotInfoResponse botInfoResponse = JsonUtils.json2Object(response.body().string(), BotInfoResponse.class);
                botInfoMap.put(feishuConfigVo.getAppId(), botInfoResponse.getBot());
                return botInfoResponse.getBot().getAppName();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return botId;
    }

    public static List<String> downloadResource(List<FeishuMessage> feishuMessages, FeishuConfigVo feishuConfigVo,
                                                MongoFileService mongoFileService) {
        List<FileMessage> fileMessages = feishuMessages.stream()
                .filter(message -> (message instanceof FileMessage) && StringUtils.isNotBlank(((FileMessage) message).getType()))
                .flatMap(message -> Stream.of((FileMessage) message))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fileMessages)) {
            return Collections.emptyList();
        }

        List<String> fileIds = Lists.newArrayList();

        // 构建client
        Client client = getClient(feishuConfigVo);

        fileMessages.forEach(message -> {
            try {
                // 创建请求对象
                GetMessageResourceReq req = GetMessageResourceReq.newBuilder()
                        .messageId(message.getMessageId())
                        .fileKey(message.getFileKey())
                        .type(message.getType())
                        .build();

                // 发起请求
                GetMessageResourceResp resp = client.im().v1().messageResource().get(req);

                // 处理服务端错误
                if (!resp.success()) {
                    String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                            resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                    logger.error("下载飞书文件失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                    return;
                }

                // 业务数据处理
                ByteArrayInputStream inputStream = new ByteArrayInputStream(resp.getData().toByteArray());
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(message.getFileName(), inputStream);
                fileIds.add(mongoFileEntity.getId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        return fileIds;
    }

    public static String getAppAccessToken(FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 创建请求对象
            InternalAppAccessTokenReq req = InternalAppAccessTokenReq.newBuilder()
                    .internalAppAccessTokenReqBody(InternalAppAccessTokenReqBody.newBuilder()
                            .appId(feishuConfigVo.getAppId())
                            .appSecret(feishuConfigVo.getAppSecret())
                            .build())
                    .build();

            // 发起请求
            InternalAppAccessTokenResp resp = client.auth().v3().appAccessToken().internal(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("获取飞书应用访问令牌失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return StringUtils.EMPTY;
            }

            // 业务数据处理
            TenantAccessTokenResponse accessTokenResponse = JsonUtils.json2Object(new String(resp.getRawResponse().getBody()), TenantAccessTokenResponse.class);
            return accessTokenResponse.getTenantAccessToken();
        } catch (Exception e) {
            logger.error("获取飞书应用访问令牌失败，错误信息：{}", e.getMessage());
        }
        return null;
    }

    public static CreateOidcAccessTokenRespBody createOidcAccessToken(String appAccessToken, String authorizationCode, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 创建请求对象
            CreateOidcAccessTokenReq req = CreateOidcAccessTokenReq.newBuilder()
                    .createOidcAccessTokenReqBody(CreateOidcAccessTokenReqBody.newBuilder()
                            .grantType("authorization_code")
                            .code(authorizationCode)
                            .build())
                    .build();

            // 发起请求
            CreateOidcAccessTokenResp resp = client.authen().v1().oidcAccessToken().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("获取飞书用户访问令牌失败，错误码：{}，错误信息：{}", resp.getCode(), errorMsg);
                return null;
            }

            // 业务数据处理
            return resp.getData();
        } catch (Exception e) {
            logger.error("获取飞书用户访问令牌失败，错误信息：{}", e.getMessage());
        }
        return null;
    }

    public static GetUserInfoRespBody getUserInfo(String userAccessToken, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 创建请求对象
            // 发起请求
            GetUserInfoResp resp = client.authen().v1().userInfo().get(RequestOptions.newBuilder()
                    .userAccessToken(userAccessToken)
                    .build());

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("获取飞书用户信息失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return null;
            }

            // 业务数据处理
            return resp.getData();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void addChatMembers(FeishuWorkRecordEntity workRecord, Map<String, String> openIdMap, FeishuConfigVo feishuConfigVo) {
        try {
            // 构建client
            Client client = getClient(feishuConfigVo);

            // 创建请求对象
            CreateChatMembersReq req = CreateChatMembersReq.newBuilder()
                    .chatId(workRecord.getChatId())
                    .memberIdType("open_id")
                    .createChatMembersReqBody(CreateChatMembersReqBody.newBuilder()
                            .idList(openIdMap.keySet().toArray(new String[0]))
                            .build())
                    .build();

            // 发起请求
            CreateChatMembersResp resp = client.im().v1().chatMembers().create(req);

            // 处理服务端错误
            if (!resp.success()) {
                String errorMsg = String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                        resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8))));
                logger.error("添加飞书群聊成员失败，错误码：{}，错误信息：{}", resp.getCode(), resp.getMsg());
                return;
            }

            // 业务数据处理
            List<String> oaUserIds = Lists.newArrayList(StringUtils.split(workRecord.getOaUserId(), Separator.SEMICOLON.getValue()));
            List<String> openIds = Lists.newArrayList(StringUtils.split(workRecord.getOpenId(), Separator.SEMICOLON.getValue()));
            oaUserIds.addAll(openIdMap.values());
            openIds.addAll(openIdMap.keySet());
            workRecord.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
            workRecord.setOpenId(StringUtils.join(openIds, Separator.SEMICOLON.getValue()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

//    /**
//     * @param args
//     */
//    public static void main(String[] args) {
//        FeishuWorkRecordEntity workRecord = new FeishuWorkRecordEntity();
//        workRecord.setChatId("oc_411ab91efad6890937fe992e672736ee");
//        FeishuConfigVo feishuConfigVo = new FeishuConfigVo();
//        feishuConfigVo.setAppId("cli_a7539bf6c7b3d013");
//        feishuConfigVo.setAppSecret("4qTirjcnymc1phi4JbvkNhXnOsJad2db");
//        List<Message> messages = listChatMessage(workRecord, feishuConfigVo);
//        FeishuMessageParser messageParser = new FeishuMessageParser();
//        messages.forEach(message -> {
//            Sender sender = message.getSender();
//            if (StringUtils.isBlank(sender.getId())) {
//                return;
//            }
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(Long.parseLong(message.getCreateTime()));
//            String senderName = sender.getId();
//            // 获取机器人名称
//            if ("app".equals(sender.getSenderType())) {
//                senderName = getBotName(sender.getId(), feishuConfigVo);
//            } else {
//            }
//
//            System.out.println(senderName + " " + DateUtils.formatDateTime(calendar.getTime()));
//            //System.out.println(messageParser.parse(message).getTextMessage());
//            System.out.println();
//        });
//    }

}
