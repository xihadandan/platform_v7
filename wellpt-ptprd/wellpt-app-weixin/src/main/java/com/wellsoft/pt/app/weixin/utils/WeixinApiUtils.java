/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinWorkRecordEntity;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.app.weixin.vo.WeixinDepartmentVo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
public class WeixinApiUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WeixinApiUtils.class);

    private static Map<String, AccessToken> ACCESS_TOKEN_MAP = Maps.newConcurrentMap();

    public static String getAccessToken(WeixinConfigVo weixinConfigVo) {
        String clientId = weixinConfigVo.getCorpId() + "_" + weixinConfigVo.getAppId();
        AccessToken accessToken = ACCESS_TOKEN_MAP.get(clientId);
        if (accessToken == null || accessToken.isExpire()) {
            return createAccessToken(weixinConfigVo);
        }
        return accessToken.getToken();
    }

    /**
     * @param weixinConfigVo
     * @return
     */
    private static String createAccessToken(WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
        HttpGet httpGet = new HttpGet(String.format(url, weixinConfigVo.getCorpId(), weixinConfigVo.getAppSecret()));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                String token = (String) responseMap.get("access_token");
                int expiresIn = (int) responseMap.get("expires_in");
                AccessToken accessToken = new AccessToken(token, expiresIn);
                String clientId = weixinConfigVo.getCorpId() + "_" + weixinConfigVo.getAppId();
                ACCESS_TOKEN_MAP.put(clientId, accessToken);
                return token;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<WeixinDepartmentVo> departments(WeixinConfigVo weixinConfigVo) {
        List<WeixinDepartmentVo.Department> departments = listDepartment(1, weixinConfigVo);
        List<WeixinDepartmentVo> departmentVos = buildDepartmentTree(departments, weixinConfigVo);
        return departmentVos;
    }

    private static List<WeixinDepartmentVo> buildDepartmentTree(List<WeixinDepartmentVo.Department> departments, WeixinConfigVo weixinConfigVo) {
        Map<Long, List<WeixinDepartmentVo.Department>> departmentGroups = Maps.newHashMap();
        departments.forEach(department -> {
            List<WeixinDepartmentVo.Department> list = departmentGroups.get(department.getParentId());
            if (list == null) {
                list = Lists.newArrayList();
                departmentGroups.put(department.getParentId(), list);
            }
            list.add(department);
        });
        List<WeixinDepartmentVo.Department> children = departmentGroups.get(0l);
        if (children == null) {
            children = Lists.newArrayListWithCapacity(0);
        }
        List<WeixinDepartmentVo> departmentVos = Lists.newArrayList();
        buildDepartmentTree(children, departmentVos, departmentGroups, weixinConfigVo);
        return departmentVos;
    }

    private static void buildDepartmentTree(List<WeixinDepartmentVo.Department> departments, List<WeixinDepartmentVo> departmentVos,
                                            Map<Long, List<WeixinDepartmentVo.Department>> departmentGroups, WeixinConfigVo weixinConfigVo) {
        for (WeixinDepartmentVo.Department department : departments) {
            WeixinDepartmentVo departmentVo = new WeixinDepartmentVo();
            departmentVo.setDepartment(department);
            departmentVo.setUsers(listUser(department.getId(), weixinConfigVo));

            List<WeixinDepartmentVo.Department> children = departmentGroups.get(department.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                List<WeixinDepartmentVo> departmentChidrenVos = Lists.newArrayList();
                buildDepartmentTree(children, departmentChidrenVos, departmentGroups, weixinConfigVo);
                departmentVo.setChildren(departmentChidrenVos);
            }
            departmentVos.add(departmentVo);
        }
    }

    private static List<WeixinDepartmentVo.User> listUser(Long deptId, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%d";
        HttpGet httpGet = new HttpGet(String.format(url, getAccessToken(weixinConfigVo), deptId));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                List<Object> userlist = (List<Object>) responseMap.get("userlist");
                List<WeixinDepartmentVo.User> users = list2Objects(userlist, WeixinDepartmentVo.User.class);
                return users;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private static List<WeixinDepartmentVo.Department> listDepartment(int parentId, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s";
        HttpGet httpGet = new HttpGet(String.format(url, getAccessToken(weixinConfigVo)));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                List<Object> department = (List<Object>) responseMap.get("department");
                List<WeixinDepartmentVo.Department> departments = list2Objects(department, WeixinDepartmentVo.Department.class);
                return departments;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private static <T extends BaseObject> List<T> list2Objects(List<Object> list, Class<T> cls) {
        List<T> objects = Lists.newArrayList();
        list.forEach(item -> {
            objects.add(JsonUtils.json2Object(JsonUtils.object2Json(item), cls));
        });
        return objects;
    }

    /**
     * @param userId
     * @param weixinConfigVo
     * @return
     */
    public static WeixinDepartmentVo.User getUserByUserId(String userId, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
        HttpGet httpGet = new HttpGet(String.format(url, getAccessToken(weixinConfigVo), userId));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                WeixinDepartmentVo.User user = JsonUtils.json2Object(responseBody, WeixinDepartmentVo.User.class);
                return user;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static WeixinDepartmentVo.Department getDepartmentById(String deptId, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/get?access_token=%s&id=%s";
        HttpGet httpGet = new HttpGet(String.format(url, getAccessToken(weixinConfigVo), deptId));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                Object departmentJson = responseMap.get("department");
                WeixinDepartmentVo.Department department = JsonUtils.json2Object(JsonUtils.object2Json(departmentJson), WeixinDepartmentVo.Department.class);
                return department;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static void sendTextMessage(String subject, List<WeixinWorkRecordEntity> workRecordEntities, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        workRecordEntities.forEach(recordEntity -> {
            try {
                Map<String, Object> data = Maps.newHashMap();
                data.put("touser", StringUtils.replace(recordEntity.getUserId(), ";", "|"));
                data.put("toparty", "");
                data.put("totag", "");
                data.put("msgtype", "textcard");
                data.put("agentid", Long.valueOf(weixinConfigVo.getAppId()));
                data.put("textcard", JsonUtils.json2Object(recordEntity.getContent(), Map.class));
                data.put("enable_id_trans", 0);
                data.put("enable_duplicate_check", 0);
                data.put("duplicate_check_interval", 1800);
                HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
                httpPost.setEntity(httpEntity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                InputStream inputStream = httpResponse.getEntity().getContent();
                String responseBody = IOUtils.toString(inputStream);
                Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
                Integer errorCode = (Integer) responseMap.get("errcode");
                if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                    String msgid = (String) responseMap.get("msgid");
                    recordEntity.setState(WeixinWorkRecordEntity.State.Sent);
                    recordEntity.setMessageId(msgid);
                } else {
                    recordEntity.setErrMsg(responseBody);
                }
            } catch (IOException e) {
                recordEntity.setErrMsg(e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        });
    }

    public static void cancelMessage(List<WeixinWorkRecordEntity> workRecordEntities, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/recall?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        workRecordEntities.forEach(recordEntity -> {
            try {
                Map<String, Object> data = Maps.newHashMap();
                data.put("msgid", recordEntity.getMessageId());
                HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
                httpPost.setEntity(httpEntity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                InputStream inputStream = httpResponse.getEntity().getContent();
                String responseBody = IOUtils.toString(inputStream);
                Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
                Integer errorCode = (Integer) responseMap.get("errcode");
                if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                    recordEntity.setState(WeixinWorkRecordEntity.State.Cancelled);
                } else {
                    recordEntity.setErrMsg(responseBody);
                }
            } catch (IOException e) {
                recordEntity.setErrMsg(e.getMessage());
                LOG.error(e.getMessage(), e);
            }
        });
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

    public static String getPcUrl(String taskInstUuid, String flowInstUuid, String system, String redirectUri) {
        String uri = getUri(redirectUri);
        String url = String.format("%s/sys/%s/_/workflow/work/view/work?taskInstUuid=%s&flowInstUuid=%s&source=weixin&_requestCode=%s",
                uri, system, taskInstUuid, flowInstUuid, System.currentTimeMillis());
        return url;
    }

    public static String getMobileUrlByPcUrl(String pcUrl, String mobileUri, String system) {
        String uri = getUri(mobileUri);
        String mobileUrl = String.format("%s/#/packages/_/pages/workflow/work_view?system=%s&%s",
                uri, system, StringUtils.substringAfter(pcUrl, "?"));
        return mobileUrl;
    }

    public static void createChat(WeixinWorkRecordEntity recordEntity, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/appchat/create?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        String groupName = recordEntity.getTitle();
        String content = recordEntity.getContent();
        if (StringUtils.isNotBlank(content)) {
            JSONObject jsonObject = new JSONObject(content);
            if (jsonObject.has("groupName")) {
                groupName = jsonObject.getString("groupName");
            }
        }
        String ownerId = recordEntity.getOwnerId();
        List<String> userIds = Lists.newArrayList(StringUtils.split(recordEntity.getUserId(), Separator.SEMICOLON.getValue()));
        if (StringUtils.isNotBlank(ownerId) && !userIds.contains(ownerId)) {
            userIds.add(ownerId);
        }
        try {
            Map<String, Object> data = Maps.newHashMap();
            data.put("name", groupName);
            data.put("owner", ownerId);
            data.put("userlist", userIds);
            HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                String chatId = (String) responseMap.get("chatid");
                recordEntity.setChatId(chatId);
                recordEntity.setState(WeixinWorkRecordEntity.State.Sent);
            } else {
                recordEntity.setErrMsg(responseBody);
            }
        } catch (IOException e) {
            recordEntity.setErrMsg(e.getMessage());
            LOG.error(e.getMessage(), e);
        }
    }

    public static void sendCreateChatMessage(String subject, WeixinWorkRecordEntity recordEntity, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        try {
            Map<String, Object> textCard = Maps.newHashMap();
            textCard.put("title", subject);
            textCard.put("description", recordEntity.getTitle());
            textCard.put("url", getMobileUrlByPcUrl(recordEntity.getUrl(), weixinConfigVo.getMobileAppUri(), recordEntity.getSystem()));
            textCard.put("btntxt", "查看流程");

            Map<String, Object> data = Maps.newHashMap();
            data.put("chatid", recordEntity.getChatId());
            data.put("msgtype", "textcard");
            data.put("textcard", textCard);
            data.put("safe", 0);
            HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                LOG.info("sendCreateChatMessage rsp: {}", responseBody);
            } else {
                LOG.error("sendCreateChatMessage rsp: {}", responseBody);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void sendUserChatMessage(UserDetails userDetails, String actionName, String opinionText, WeixinWorkRecordEntity recordEntity, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        try {
            Map<String, String> sampleText = Maps.newHashMap();
            String text = userDetails.getUserName() + " 已" + actionName;
            if (StringUtils.isNotBlank(opinionText)) {
                text += ": " + opinionText;
            }
            sampleText.put("content", text);

            Map<String, Object> data = Maps.newHashMap();
            data.put("chatid", recordEntity.getChatId());
            data.put("msgtype", "text");
            data.put("text", sampleText);
            data.put("safe", 0);
            HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                LOG.info("sendUserChatMessage rsp: {}", responseBody);
            } else {
                LOG.error("sendUserChatMessage rsp: {}", responseBody);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void addChatMembers(WeixinWorkRecordEntity recordEntity, Map<String, String> userIdMap, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/appchat/update?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        try {
            Map<String, Object> data = Maps.newHashMap();
            data.put("chatid", recordEntity.getChatId());
            data.put("add_user_list", userIdMap.keySet());
            HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                Set<String> oaUserIds = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(recordEntity.getOaUserId(), Separator.SEMICOLON.getValue())));
                Set<String> userIds = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(recordEntity.getUserId(), Separator.SEMICOLON.getValue())));
                oaUserIds.addAll(userIdMap.values());
                userIds.addAll(userIdMap.keySet());
                recordEntity.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
                recordEntity.setUserId(StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            } else {
                recordEntity.setErrMsg(responseBody);
                LOG.error("cancelGroupChat rsp: {}", responseBody);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void cancelGroupChat(WeixinWorkRecordEntity recordEntity, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/appchat/update?access_token=%s";
        HttpPost httpPost = new HttpPost(String.format(url, getAccessToken(weixinConfigVo)));
        try {
            Map<String, Object> data = Maps.newHashMap();
            data.put("chatid", recordEntity.getChatId());
            data.put("del_user_list", Arrays.asList(StringUtils.split(recordEntity.getUserId(), Separator.SEMICOLON.getValue())));
            HttpEntity httpEntity = new StringEntity(JsonUtils.object2Json(data), "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                recordEntity.setState(WeixinWorkRecordEntity.State.Cancelled);
            } else {
                recordEntity.setErrMsg(responseBody);
                LOG.error("cancelGroupChat rsp: {}", responseBody);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static String getUserIdByCode(String code, WeixinConfigVo weixinConfigVo) {
        HttpClient httpClient = HttpClients.createDefault();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s";
        HttpGet httpGet = new HttpGet(String.format(url, getAccessToken(weixinConfigVo), code));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String responseBody = IOUtils.toString(inputStream);
            Map<String, Object> responseMap = JsonUtils.json2Object(responseBody, Map.class);
            Integer errorCode = (Integer) responseMap.get("errcode");
            if (errorCode != null && Integer.valueOf(0).equals(errorCode)) {
                return (String) responseMap.get("userid");
            }
        } catch (IOException e) {
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
