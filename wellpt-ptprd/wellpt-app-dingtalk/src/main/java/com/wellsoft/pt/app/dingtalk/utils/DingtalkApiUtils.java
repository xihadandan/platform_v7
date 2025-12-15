package com.wellsoft.pt.app.dingtalk.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList;
import com.dingtalk.api.request.OapiWorkrecordAddRequest.FormItemVo;
import com.dingtalk.api.response.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.constants.Constants;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkApis;
import com.wellsoft.pt.app.dingtalk.entity.WorkRecord;
import com.wellsoft.pt.app.dingtalk.enums.EnumDingtalkMsgType;
import com.wellsoft.pt.app.dingtalk.service.WorkRecordService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Description: 钉钉接口API工具类
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
@Deprecated
public abstract class DingtalkApiUtils {

    private static Logger logger = LoggerFactory.getLogger(DingtalkApiUtils.class);

    private static LoadingCache<String, Object> tokenCache = null;

    private static WorkRecordService workRecordService = null;

    private static DingtalkConfig dingtalkConfig = null;

    static {
        dingtalkConfig = ApplicationContextHolder.getBean(DingtalkConfig.class);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        tokenCache = cacheBuilder.maximumSize(16).expireAfterWrite(7200 - 60, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws ApiException {
                        if (Constants.DINGTALK_JSAPI_TICKET.equals(key)) {
                            DefaultDingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_JSAPI_TICKET);
                            OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
                            req.setTopHttpMethod("GET");
                            OapiGetJsapiTicketResponse rsp = client.execute(req, getAccessToken());
                            JSONObject jb = DingtalkUtils.checkResult(DingtalkUtils.getJsonResult(rsp.getBody()));
                            return jb.getString("ticket"); // ticket;
                        } else if (Constants.DINGTALK_ACCESS_TOKEN.equals(key)) {
                            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_ACCESS_TOKEN);
                            OapiGettokenRequest req = new OapiGettokenRequest();
                            req.setAppkey(dingtalkConfig.getAppKey());
                            req.setAppsecret(dingtalkConfig.getAppSecret());
                            req.setHttpMethod("GET");
                            OapiGettokenResponse rsp = client.execute(req);
                            JSONObject jb = DingtalkUtils.checkResult(DingtalkUtils.getJsonResult(rsp.getBody()));
                            return jb.getString("access_token"); // token;
                        }
                        throw new RuntimeException("");
                    }
                });
        workRecordService = ApplicationContextHolder.getBean(WorkRecordService.class);
    }

    /**
     * 获取access_token
     * 2小时有效时间
     *
     * @return
     */
    public static String getAccessToken() {
        try {
            return (String) tokenCache.get(Constants.DINGTALK_ACCESS_TOKEN);
        } catch (ExecutionException ex) {
            // 二次重试
            return (String) tokenCache.getUnchecked(Constants.DINGTALK_ACCESS_TOKEN);
        }
    }

    /**
     * 获取jsapi_ticket
     * 2小时有效时间
     *
     * @return
     */
    public static String getJsapiTicket() {
        try {
            return (String) tokenCache.get(Constants.DINGTALK_JSAPI_TICKET);
        } catch (ExecutionException ex) {
            // 二次重试
            return (String) tokenCache.getUnchecked(Constants.DINGTALK_JSAPI_TICKET);
        }
    }

    /**
     * 过期access_token
     *
     * @return
     */
    public static void invalidateAccessToken() {
        tokenCache.invalidate(Constants.DINGTALK_ACCESS_TOKEN);
    }

    /**
     * 过期所有缓存对象
     */
    public static void invalidateAllToken() {
        tokenCache.invalidateAll();
    }

    public static JSONObject getUserInfoByCode(String code) {
        try {
            DefaultDingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GETUSERINFO_BYCODE);
            OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
            req.setTmpAuthCode(code);
            OapiSnsGetuserinfoBycodeResponse response = client.execute(req, dingtalkConfig.getSnsAppId(),
                    dingtalkConfig.getSnsAppSecret());
            return DingtalkUtils.getJsonResult(response.getBody());
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 注册回调监听事件
     *
     * @param backTags
     * @param accessToken
     */
    public static JSONObject registerCallBack(List<String> backTags, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_REGISTER_CALL_BACK);
            OapiCallBackRegisterCallBackRequest req = new OapiCallBackRegisterCallBackRequest();
            req.setCallBackTag(backTags);
            req.setToken(dingtalkConfig.getEventCallBackToken());
            req.setAesKey(dingtalkConfig.getAppAesKey());
            req.setUrl(dingtalkConfig.getCorpDomainUri() + "/mobile/pt/dingtalk/callback");
            OapiCallBackRegisterCallBackResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 查询事件回调接口
     *
     * @param accessToken
     */
    public static JSONObject getCallBack(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_CALL_BACK);
            OapiCallBackGetCallBackRequest request = new OapiCallBackGetCallBackRequest();
            request.setHttpMethod("GET");
            OapiCallBackGetCallBackResponse rsp = client.execute(request, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 更新事件回调接口
     *
     * @param backTags
     * @param accessToken
     */
    public static JSONObject updateCallBack(List<String> backTags, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_UPDATE_CALL_BACK);
            OapiCallBackUpdateCallBackRequest request = new OapiCallBackUpdateCallBackRequest();
            request.setUrl(dingtalkConfig.getCorpDomainUri() + "/mobile/pt/dingtalk/callback");
            request.setAesKey(dingtalkConfig.getAppAesKey());
            request.setToken(dingtalkConfig.getEventCallBackToken());
            request.setCallBackTag(backTags);
            OapiCallBackUpdateCallBackResponse rsp = client.execute(request, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 删除事件回调接口
     *
     * @param accessToken
     */
    public static JSONObject deleteCallBack(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_DELETE_CALL_BACK);
            OapiCallBackDeleteCallBackRequest request = new OapiCallBackDeleteCallBackRequest();
            request.setHttpMethod("GET");
            OapiCallBackDeleteCallBackResponse rsp = client.execute(request, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取回调失败的结果
     *
     * @param accessToken
     */
    public static JSONObject getCallBackFailedResult(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_CALL_BACK_FAILED_RESULT);
            OapiCallBackGetCallBackFailedResultRequest request = new OapiCallBackGetCallBackFailedResultRequest();
            request.setHttpMethod("GET");
            OapiCallBackGetCallBackFailedResultResponse rsp = client.execute(request, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取钉钉用户详情
     *
     * @param userId
     * @param accessToken
     */
    public static JSONObject getDingTalkUserInfo(String userId, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_USER_GET);
            OapiUserGetRequest req = new OapiUserGetRequest();
            req.setUserid(userId);
            req.setHttpMethod("GET");
            OapiUserGetResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取通讯录权限范围
     *
     * @param accessToken
     */
    public static JSONObject getDingTalkAuthScopes(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_AUTH_SCOPES);
            OapiAuthScopesRequest req = new OapiAuthScopesRequest();
            req.setHttpMethod("GET");
            OapiAuthScopesResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取钉钉部门列表
     *
     * @param accessToken
     */
    public static JSONObject getDingTalkDeptList(String accessToken, String deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_DEPARTMENT_LIST);
            OapiDepartmentListRequest req = new OapiDepartmentListRequest();
            req.setHttpMethod("GET");
            if (StringUtils.isNotBlank(deptId)) {
                req.setId(deptId);
            }
            OapiDepartmentListResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取钉钉部门详情
     *
     * @param userId
     * @param accessToken
     */
    public static JSONObject getDingTalkDeptInfo(String deptId, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_DEPARTMENT_GET);
            OapiDepartmentGetRequest req = new OapiDepartmentGetRequest();
            req.setId(deptId);
            req.setHttpMethod("GET");
            OapiDepartmentGetResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 获取部门用户详情数据
     *
     * @param deptId
     * @param accessToken
     * @return
     */
    public static JSONObject getDingTalkUserListbypage(String deptId, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_USER_LISTBYPAGE);
            OapiUserListbypageRequest req = new OapiUserListbypageRequest();
            req.setDepartmentId(Long.parseLong(deptId));
            req.setOffset(0L);
            req.setSize(100L);
            req.setHttpMethod("GET");
            OapiUserListbypageResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取部门用户userid列表
     *
     * @param deptId
     * @param accessToken
     * @return
     */
    public static JSONObject getDeptMember(String deptId, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_DEPT_MEMBER);
            OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
            req.setDeptId(deptId);
            req.setHttpMethod("GET");
            OapiUserGetDeptMemberResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 执行待办推送
     */
    public static void executeWorkRecord() {
        PagingInfo page = new PagingInfo(0, 10); // 每次10条
        WorkRecordService workRecordService = ApplicationContextHolder.getBean(WorkRecordService.class);
        // 查询状态1优先
        List<WorkRecord> list = workRecordService.listByNameHQLQueryAndPage("getWorkRecordByPage", null, page);
        while (list != null && false == list.isEmpty()) {
            for (WorkRecord record : list) {
                int status = record.getRecordStatus();
                if (status == WorkRecord.RECORD_STATUS_1 || status == WorkRecord.RECORD_STATUS_3) {
                    JSONArray items = JSONArray.fromObject(record.getFormItemList());
                    List<FormItemVo> formitems = (List<FormItemVo>) JSONArray.toCollection(items, FormItemVo.class);
                    DingtalkApiUtils.addWorkRecord(record.getBizId(), record.getDingUserId(), record.getUserName(),
                            DingtalkApiUtils.getAccessToken(), record.getTitle(), record.getUrl(), formitems);
                } else if (status == WorkRecord.RECORD_STATUS_2 || status == WorkRecord.RECORD_STATUS_4) {
                    DingtalkApiUtils.updateWorkRecord(record.getBizId(), record.getDingUserId(),
                            DingtalkApiUtils.getAccessToken());
                }
            }
            list = workRecordService.listByNameHQLQueryAndPage("getWorkRecordByPage", null, page);
        }
    }

    /**
     * 发起钉钉待办
     *
     * @param bizId        平台业务id
     * @param userId       钉钉用户id
     * @param userName     用户名称
     * @param accessToken
     * @param title        待办标题
     * @param url          待办跳转url
     * @param formItemList 待办具体内容
     * @return
     */
    public static JSONObject addWorkRecord(String bizId, String userId, String userName, String accessToken,
                                           String title, String url, List<FormItemVo> formItemList) {
        try {
            Date createTime = new Date();
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_WORKRECORD_ADD);
            OapiWorkrecordAddRequest req = new OapiWorkrecordAddRequest();
            req.setUrl(url);
            req.setTitle(title);
            req.setUserid(userId);
            req.setFormItemList(formItemList);
            req.setTimestamp(createTime.getTime());
            req.setCreateTime(createTime.getTime());
            OapiWorkrecordAddResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            WorkRecord record = workRecordService.getWorkRecordByCondition(bizId, userId, null);
            if (record == null) {
                record = new WorkRecord();
                record.setBizId(bizId);
                record.setDingUserId(userId);
            }
            int errcode = jb.getInt("errcode");
            if (errcode == 0) {
                String recordId = jb.getString("record_id");
                record.setDingRecordId(recordId);
                record.setRecordStatus(WorkRecord.RECORD_STATUS_0);// 发起待办/待处理
            } else {
                String errmsg = "";
                if (errcode == 88) {
                    errcode = jb.getInt("sub_code");
                    errmsg = jb.getString("sub_msg");
                } else {
                    errmsg = jb.getString("errmsg");
                }
                record.setRecordStatus(WorkRecord.RECORD_STATUS_1); // 发起待办/推送异常
                record.setErrMsg(errmsg);
                record.setErrCode("" + errcode);
            }
            record.setOperateTime(new Date());
            record.setTitle(title);
            record.setUrl(url);
            record.setUserName(userName);
            record.setFormItemList(JsonUtils.object2Json(formItemList));
            workRecordService.save(record);
            return jb;
        } catch (ApiException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    public static JSONObject addWorkRecord2(String bizId, String userId, String userName, String accessToken,
                                            String title, String url, List<FormItemVo> formItemList) {
        WorkRecord record = workRecordService.getWorkRecordByCondition(bizId, userId, null);
        if (record == null) {
            record = new WorkRecord();
            record.setBizId(bizId);
            record.setDingUserId(userId);
        }
        record.setTitle(title);
        record.setUrl(url);
        record.setUserName(userName);
        record.setRecordStatus(WorkRecord.RECORD_STATUS_3); // 发起待办/待推送
        record.setFormItemList(JsonUtils.object2Json(formItemList));
        workRecordService.save(record);
        return null;
    }

    /**
     * 更新钉钉待办
     *
     * @param bizId       平台业务id
     * @param userId      钉钉用户id
     * @param accessToken
     * @return
     */
    public static JSONObject updateWorkRecord(String bizId, String userId, String accessToken) {
        try {
            WorkRecord record = workRecordService.getWorkRecordByCondition(bizId, userId, null);
            if (record == null) {
                // 记录不存在(1、多次删除，2、)
                logger.info("updateWorkRecord bizId[" + bizId + "]userId[" + userId + "] not found record");
                return null;
            } else if (StringUtils.isBlank(record.getDingRecordId())) {
                // 还未发送成功，直接删除（1、参数错误，2、）
                workRecordService.delete(record.getUuid());
                logger.warn("updateWorkRecord bizId[" + bizId + "]userId[" + userId + "] not found recordId");
                return null;
            }
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_WORKRECORD_UPDATE);
            OapiWorkrecordUpdateRequest req = new OapiWorkrecordUpdateRequest();
            req.setUserid(userId);
            req.setRecordId(record.getDingRecordId());
            OapiWorkrecordUpdateResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            int errcode = jb.getInt("errcode");
            if (errcode == 0) { // 更新成功，直接删除
                workRecordService.delete(record.getUuid());
                return jb;
            }
            String errmsg = "";
            if (errcode == 88) {
                errcode = jb.getInt("sub_code");
                errmsg = jb.getString("sub_msg");
            } else {
                errmsg = jb.getString("errmsg");
            }
            record.setOperateTime(new Date());
            record.setRecordStatus(WorkRecord.RECORD_STATUS_2); // 更新待办/推送异常
            record.setErrCode("" + errcode);
            record.setErrMsg(errmsg);
            workRecordService.save(record);
            return jb;
        } catch (ApiException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    public static JSONObject updateWorkRecord2(String bizId, String userId, String accessToken) {
        WorkRecord record = workRecordService.getWorkRecordByCondition(bizId, userId, null);
        if (record == null) {
            // 记录不存在(1、多次删除，2、)
            logger.info("updateWorkRecord bizId[" + bizId + "]userId[" + userId + "] not found record");
            return null;
        } else if (StringUtils.isBlank(record.getDingRecordId())) {
            // 还未发送成功，直接删除（1、参数错误，2、）
            workRecordService.delete(record.getUuid());
            logger.warn("updateWorkRecord bizId[" + bizId + "]userId[" + userId + "] not found recordId");
            return null;
        }
        record.setCreateTime(new Date());
        record.setOperateTime(null);
        record.setRecordStatus(WorkRecord.RECORD_STATUS_4); // 更新待办/待推送
        workRecordService.save(record);
        return null;
    }

    /**
     * 工作通知消息
     *
     * @param useridList     多个以英文逗号“,”隔开
     * @param msgType        消息类型
     * @param msgTitle       消息标题，msgType为“link”时使用
     * @param msgContent     消息内容
     * @param mediaId        媒介id msgType为“image、file”时使用
     * @param messageUrl     消息链接，msgType为“link”时使用
     * @param picUrl         图片链接，msgType为“link”时使用
     * @param btnOrientation msgType为“action_card”时使用
     * @param dtBtnJsonList  msgType为“action_card”时使用
     * @param accessToken
     * @return
     */
    public static JSONObject sendDingtalkMsg(String useridList, String msgType, String msgTitle, String msgContent,
                                             String mediaId, String messageUrl, String picUrl, String btnOrientation,
                                             List<Map<String, String>> dtBtnJsonList, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkUtils.uriFormat(
                    DingtalkApis.URI_MESSAGE_CORPCONVERSATION, accessToken));
            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setUseridList(useridList);
            request.setAgentId(Long.parseLong(dingtalkConfig.getAgentId()));
            request.setToAllUser(false);
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            switch (msgType) {
                case "text":
                    msg.setMsgtype(EnumDingtalkMsgType.text.getValue());
                    msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
                    msg.getText().setContent(msgContent);
                    request.setMsg(msg);
                    break;
                case "image":
                    msg.setMsgtype(EnumDingtalkMsgType.image.getValue());
                    msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
                    msg.getImage().setMediaId(mediaId);
                    request.setMsg(msg);
                    break;
                case "file":
                    msg.setMsgtype(EnumDingtalkMsgType.file.getValue());
                    msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
                    msg.getFile().setMediaId(mediaId);
                    request.setMsg(msg);
                    break;
                case "link":
                    msg.setMsgtype(EnumDingtalkMsgType.link.getValue());
                    msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
                    msg.getLink().setTitle(msgTitle);
                    msg.getLink().setText(msgContent);
                    msg.getLink().setMessageUrl(messageUrl);
                    msg.getLink().setPicUrl(picUrl);
                    request.setMsg(msg);
                    break;
                case "markdown":
                    msg.setMsgtype(EnumDingtalkMsgType.markdown.getValue());
                    msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
                    msg.getMarkdown().setText("##### text");
                    msg.getMarkdown().setTitle("### Title");
                    request.setMsg(msg);
                    break;
                case "oa":
                    msg.setMsgtype(EnumDingtalkMsgType.oa.getValue());
                    msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
                    msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
                    msg.getOa().getHead().setText("head");
                    msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
                    msg.getOa().getBody().setContent("xxx");
                    request.setMsg(msg);
                    break;
                case "action_card":
                    msg.setMsgtype(EnumDingtalkMsgType.action_card.getValue());
                    msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
                    msg.getActionCard().setTitle(msgTitle);
                    msg.getActionCard().setMarkdown(msgContent);
                    if (dtBtnJsonList == null || dtBtnJsonList.isEmpty()) {
                        // 没有按钮
                    } else if (dtBtnJsonList.size() == 1) {
                        msg.getActionCard().setSingleUrl(dtBtnJsonList.get(0).get("url"));
                        msg.getActionCard().setSingleTitle(dtBtnJsonList.get(0).get("title"));
                    } else {
                        msg.getActionCard().setBtnOrientation(btnOrientation == null ? "0" : btnOrientation);
                        List<BtnJsonList> btnJsonList = Lists.newArrayList();
                        for (Map<String, String> dtBtnJson : dtBtnJsonList) {
                            BtnJsonList btnJson = new BtnJsonList();
                            btnJson.setTitle(dtBtnJson.get("title"));
                            btnJson.setActionUrl(dtBtnJson.get("url"));
                            btnJsonList.add(btnJson);
                        }
                        msg.getActionCard().setBtnJsonList(btnJsonList);
                    }
                    request.setMsg(msg);
                    break;
                default:
                    break;
            }
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(response.getBody());
            DingtalkUtils.checkResult(jb);
            return jb;
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取角色列表
     *
     * @param all
     * @param accessToken
     * @return
     */
    public static JSONObject getRoleList(boolean all, String accessToken) {
        boolean hasMore = false;
        JSONObject roleResult = null;
        long size = 200L, offset = 0;
        try {
            do {
                DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_ROLE_LIST);
                OapiRoleListRequest req = new OapiRoleListRequest();
                req.setSize(size);
                req.setOffset(offset);
                OapiRoleListResponse rsp = client.execute(req, accessToken);
                JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
                JSONObject resultObj = jb.optJSONObject("result");
                if (null != resultObj && false == JSONUtils.isNull(resultObj)) {
                    hasMore = resultObj.getBoolean("hasMore");
                }
                if (roleResult == null) {
                    roleResult = jb;
                } else {
                    offset += size;
                    JSONObject resultObj2 = roleResult.getJSONObject("result");
                    resultObj2.put("hasMore", hasMore);
                    resultObj2.getJSONArray("list").addAll(resultObj.getJSONArray("list"));
                }
            } while (all && hasMore);
        } catch (ApiException ex) {
            roleResult = null;
            logger.warn(ex.getMessage(), ex);
        }
        return roleResult;
    }

    /**
     * 获取角色下的员工列表
     *
     * @param roleId
     * @param all
     * @param accessToken
     * @return
     */
    public static JSONObject getRoleSimpleList(long roleId, boolean all, String accessToken) {
        boolean hasMore = false;
        JSONObject roleResult = null;
        long size = 200L, offset = 0;
        try {
            do {
                DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_ROLE_SIMPLELIST);
                OapiRoleSimplelistRequest req = new OapiRoleSimplelistRequest();
                req.setSize(size);
                req.setOffset(offset);
                req.setRoleId(roleId);
                OapiRoleSimplelistResponse rsp = client.execute(req, accessToken);
                JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
                JSONObject resultObj = jb.optJSONObject("result");
                if (null != resultObj && false == JSONUtils.isNull(resultObj)) {
                    hasMore = resultObj.getBoolean("hasMore");
                }
                if (roleResult == null) {
                    roleResult = jb;
                } else {
                    offset += size;
                    JSONObject resultObj2 = roleResult.getJSONObject("result");
                    resultObj2.put("hasMore", hasMore);
                    resultObj2.getJSONArray("list").addAll(resultObj.getJSONArray("list"));
                }
            } while (all && hasMore);
        } catch (ApiException ex) {
            roleResult = null;
            logger.warn(ex.getMessage(), ex);
        }
        return roleResult;
    }

    /**
     * 获取角色详情
     *
     * @param roleId
     * @param accessToken
     * @return
     */
    public static JSONObject getRole(long roleId, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_ROLE_GETROLE);
            OapiRoleGetroleRequest req = new OapiRoleGetroleRequest();
            req.setRoleId(roleId);
            OapiRoleGetroleResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取员工花名册字段信息
     *
     * @param userIdList      员工的ID列表，多个用,隔开
     * @param fieldFilterList 需要获取的花名册字段信息，建议按需获取，如果为空，则获取所有的花名册字段。多个用,隔开
     *                        花名册字段信息详见：https://open.dingtalk.com/document/orgapp-server/roster-custom-field-business-code
     *                        示例：sys02-certNo	证件号码
     * @param accessToken
     * @return
     */
    public static JSONObject getEmployeeList(String userIdList, String fieldFilterList, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(DingtalkApis.URI_GET_EMPLOYEE_LIST);
            OapiSmartworkHrmEmployeeListRequest req = new OapiSmartworkHrmEmployeeListRequest();
            req.setUseridList(userIdList);
            req.setAgentid(Long.valueOf(dingtalkConfig.getAgentId()));
            if (StringUtils.isNotBlank(fieldFilterList)) {
                req.setFieldFilterList(fieldFilterList);
            }
            OapiSmartworkHrmEmployeeListResponse rsp = client.execute(req, accessToken);
            JSONObject jb = DingtalkUtils.getJsonResult(rsp.getBody());
            return jb;
        } catch (ApiException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

}
