/*
 * @(#)2020年5月30日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade;

import com.google.common.collect.Lists;
import com.wellsoft.context.web.process.ProcessUtils;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkInfo;
import com.wellsoft.pt.app.dingtalk.entity.*;
import com.wellsoft.pt.app.dingtalk.service.*;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkUtils;
import com.wellsoft.pt.app.dingtalk.utils.EventTypeUtils;
import com.wellsoft.pt.security.util.SecurityConfigUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月30日.1	zhongzh		2020年5月30日		Create
 * </pre>
 * @date 2020年5月30日
 */
@Component
@Deprecated
public class DingtalkOrgSyncApi {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private EventCallBackService eventCallBackService;

    @Autowired
    private MultiOrgDingDeptService multiOrgDingDeptService;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

    @Autowired
    private MultiOrgDingRoleService multiOrgDingRoleService;

    @Autowired
    private MultiOrgSyncLogService multiOrgSyncLogService;

    @Autowired
    private MultiOrgSyncDeptLogService multiOrgSyncDeptLogService;

    @Autowired
    private MultiOrgSyncUserLogService multiOrgSyncUserLogService;

    @Autowired
    private MultiOrgSyncUserWorkLogService multiOrgSyncUserWorkLogService;

    private ConcurrentHashMap<String, Object> paramMap = new ConcurrentHashMap<>();

    /**
     * 同步钉钉回调失败列表（先入库，后执行executeCallBack）
     *
     * @return
     */
    public JSONObject syncFailedList() {
        JSONObject jb = DingtalkApiUtils.getCallBackFailedResult(DingtalkApiUtils.getAccessToken());
        JSONArray failedArray = jb.optJSONArray("failed_list");
        if (false == CollectionUtils.isEmpty(failedArray)) {
            List<EventCallBack> entities = Lists.newArrayList();
            for (int i = 0; i < failedArray.size(); i++) {
                JSONObject failEvent = failedArray.getJSONObject(i);
                String callbackTag = failEvent.optString("call_back_tag");
                String eventTime = failEvent.optString("event_time");
                JSONObject callbackData = failEvent.getJSONObject(callbackTag);
                String userId = callbackData.optString("userid"); // 用户信息
                String deptId = callbackData.optString("deptid"); // 部门信息
                String corpId = callbackData.optString("corpid");
                EventCallBack eventCallBack = new EventCallBack();
                eventCallBack.setEventType(callbackTag);
                // 此处的eventTime为时间戳，需要转换格式
                eventCallBack.setDingTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(eventTime))));
                eventCallBack.setDingUserId(userId);
                eventCallBack.setDingDeptId(deptId);
                eventCallBack.setDingCorpId(corpId);
                eventCallBack.setStatus(EventCallBack.CALLBACK_STATUS_0); // 默认为0，未处理
                // eventCallBack.setOptTime(new Date());
                eventCallBack.setSyncContent(EventTypeUtils.eventType2SyncContent(callbackTag));
                entities.add(eventCallBack);
            }
            eventCallBackService.saveAll(entities);
        }
        return jb;
    }

    /**
     * 定时处理钉钉回调事件（失败列表）
     */
    public void executeCallBack() {
        List<EventCallBack> listEventCallBack = eventCallBackService.listEventCallBack();
        if (CollectionUtils.isEmpty(listEventCallBack)) {
            return;
        }
        for (EventCallBack event : listEventCallBack) {
            executeCallBackEvent(event);
        }
    }

    /**
     * 执行钉钉回调事件
     *
     * @param event
     */
    public void executeCallBackEvent(EventCallBack event) {
        try {
            String eventType = event.getEventType();
            switch (eventType) {
                case "user_add_org":// 通讯录用户增加
                case "user_modify_org":// 通讯录用户更改
                    event.setSyncContent(DingtalkInfo.SYNC_CONTENT_USER + "、" + DingtalkInfo.SYNC_CONTENT_USER_WORK);
                    iteratorUserList(JSONArray.fromObject(event.getDingUserId()), eventType, event.getUuid());
                    break;
                case "user_leave_org":// 通讯录用户离职
                case "user_active_org":// 加入企业后用户激活
                case "org_admin_add":// 通讯录用户被设为管理员
                case "org_admin_remove":// 通讯录用户被取消设置管理员
                    event.setSyncContent(DingtalkInfo.SYNC_CONTENT_USER);
                    iteratorUserList(JSONArray.fromObject(event.getDingUserId()), eventType, event.getUuid());
                    break;
                case "org_dept_create":// 通讯录企业部门创建
                case "org_dept_modify":// 通讯录企业部门修改
                case "org_dept_remove":// 通讯录企业部门删除
                    // 需要在组织同步设置那边，开启部门同步的开关才能执行部门相关的业务事件回调
                    String deptSyncSwitch = dingtalkConfig.getDeptSyncSwitch();
                    if (!DingtalkInfo.SYNC_SWITCH_OPEN.equals(deptSyncSwitch)) {
                        return;
                    }

                    event.setSyncContent(DingtalkInfo.SYNC_CONTENT_DEPT);
                    iteratorDeptList(JSONArray.fromObject(event.getDingDeptId()), eventType, event.getUuid());
                    break;
                case "org_remove":// 企业被解散
                    break;
                case "check_url"://
                    break;
                case "bpms_task_change"://
                    break;
                case "bpms_instance_change"://
                    break;
                case "label_user_change"://
                    iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                    break;
                case "label_conf_add"://
                    iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                    break;
                case "label_conf_modify"://
                    iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                    break;
                case "label_conf_del"://
                    iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                    break;
                default: //
                    break;
            }
            if (multiOrgSyncDeptLogService.isExistsErrorData(event.getUuid()) || multiOrgSyncUserLogService.isExistsErrorData(event.getUuid()) || multiOrgSyncUserWorkLogService.isExistsErrorData(event.getUuid())) {
                event.setStatus(EventCallBack.CALLBACK_STATUS_2);
            } else {
                event.setStatus(EventCallBack.CALLBACK_STATUS_1);
            }
        } catch (Exception ex) {
            event.setStatus(EventCallBack.CALLBACK_STATUS_2);
            event.setRemark(ex.getMessage());
        }
        event.setOptTime(new Date());
        eventCallBackService.save(event);
    }

    public Integer syncOrgFromDingtalk() {
        SecurityConfigUtils.disableSecurityConfigUpdatedEvent();
        MultiOrgSyncLog log = saveOrgSyncLog();

        paramMap.put("logId", log.getUuid());
//			syncRoleFromDingtalk();// 同步角色
        JSONObject authResult = DingtalkApiUtils.getDingTalkAuthScopes(DingtalkApiUtils.getAccessToken());
        DingtalkUtils.checkResult(authResult);// 结果检查
        JSONObject authOrgScopes = authResult.getJSONObject("auth_org_scopes");
        JSONArray authedDepts = authOrgScopes.getJSONArray("authed_dept");
        if (null == authedDepts || JSONUtils.isNull(authedDepts) || authedDepts.size() == 0) {
            // 默认为根部门
            authedDepts = new JSONArray();
            //根部门ID为1 钉钉的要求
            authedDepts.add(1);
        }
        for (int index = 0; index < authedDepts.size(); index++) {
            long rootDeptId = authedDepts.getLong(index);
            syncOrgDeptFormDingtalk(rootDeptId);
        }

        updateOrgSyncLog(log);
        SecurityConfigUtils.removeSecurityConfigUpdatedEvent();
        SecurityConfigUtils.publishSecurityConfigUpdatedEvent(this);
        return log.getSyncStatus();
    }

    private void updateOrgSyncLog(MultiOrgSyncLog log) {
        // 判断一批次的日志是否全部成功，全部成功则同步成功，否则同步异常
        String logId = log.getUuid();
        if (multiOrgSyncDeptLogService.isExistsErrorData(logId) || multiOrgSyncUserLogService.isExistsErrorData(logId) || multiOrgSyncUserWorkLogService.isExistsErrorData(logId)) {
            log.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
        } else {
            log.setSyncStatus(DingtalkInfo.SYNC_STATUS_SUCCESS);
        }
        multiOrgSyncLogService.update(log);
    }

    private MultiOrgSyncLog saveOrgSyncLog() {
        String deptSyncSwitch = dingtalkConfig.getDeptSyncSwitch();
        String workinfoSyncSwitch = dingtalkConfig.getWorkinfoSyncSwitch();

        StringBuffer syncContent = new StringBuffer();
        if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(deptSyncSwitch)) {
            syncContent.append(DingtalkInfo.SYNC_CONTENT_DEPT + "、" + DingtalkInfo.SYNC_CONTENT_USER);
        } else {
            syncContent.append(DingtalkInfo.SYNC_CONTENT_USER);
        }

        if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(workinfoSyncSwitch)) {
            syncContent.append("、" + DingtalkInfo.SYNC_CONTENT_USER_WORK);
        }

        MultiOrgSyncLog log = new MultiOrgSyncLog();
        log.setSyncTime(new Date());
        log.setOperator(SpringSecurityUtils.getCurrentUserId());
        log.setOperatorName(SpringSecurityUtils.getCurrentUserName());
        log.setSyncContent(syncContent.toString());
//		log.setSyncStatus(DingtalkInfo.SYNC_STATUS_SUCCESS);
        multiOrgSyncLogService.save(log);
        return log;
    }

    private MultiOrgSyncLog saveOrgSyncLog(String syncContent) {
        MultiOrgSyncLog log = new MultiOrgSyncLog();
        log.setSyncTime(new Date());
        log.setOperator(SpringSecurityUtils.getCurrentUserId());
        log.setOperatorName(SpringSecurityUtils.getCurrentUserName());
        log.setSyncContent(syncContent);
        log.setSyncStatus(DingtalkInfo.SYNC_STATUS_SUCCESS);
        multiOrgSyncLogService.save(log);
        return log;
    }

    public void syncRoleFromDingtalk() {
        JSONObject result = DingtalkApiUtils.getRoleList(true, DingtalkApiUtils.getAccessToken());
        DingtalkUtils.checkResult(result);// 结果检查
        JSONObject resultObj = result.getJSONObject("result");
        JSONArray groupList = resultObj.getJSONArray("list");
        iteratorGroupList(groupList);
    }

    /**
     * 如何描述该方法
     *
     * @param rootDeptId
     */
    private void syncOrgDeptFormDingtalk(Long rootDeptId) {
        ProcessUtils.start("拉取部门");
        JSONObject deptResult = DingtalkApiUtils.getDingTalkDeptList(DingtalkApiUtils.getAccessToken(),
                rootDeptId == null ? null : String.valueOf(rootDeptId));
        DingtalkUtils.checkResult(deptResult);
        if (null == deptResult || JSONUtils.isNull(deptResult)) {
            logger.info("syncDeptFromDingtalk deptResult null");
            return;
        }
        JSONArray deptArray = deptResult.optJSONArray("department");
        if (null == deptArray || JSONUtils.isNull(deptArray)) {
            logger.info("syncDeptFromDingtalk deptArray null");
            return;
        }
        if (null != rootDeptId) {
            JSONObject deptInfoResult = DingtalkApiUtils.getDingTalkDeptInfo(String.valueOf(rootDeptId),
                    DingtalkApiUtils.getAccessToken());
            DingtalkUtils.checkResult(deptInfoResult);
            deptArray.add(deptInfoResult);// 追加根部门
        }
        Map<String, Object> rootDept = null;
        List<Map<String, Object>> deptList = (List<Map<String, Object>>) JSONArray.toCollection(deptArray, Map.class);
        List<String> newDeptIdList = new ArrayList<>();
        for (int i = 0; i < deptList.size(); i++) {
            Map<String, Object> dept = deptList.get(i);
            // 根部门
            long deptId = Long.parseLong(dept.get("id").toString());
            newDeptIdList.add(dept.get("id").toString());
            if (deptId == rootDeptId) {
                rootDept = dept;
                rootDept.put("isRoot", true);
            }
            // 处理父子关系
            List<Map<String, Object>> children = Lists.newArrayList();
            for (int j = 0; j < deptList.size(); j++) {
                Map<String, Object> deptChild = deptList.get(j);
                Object parendId = deptChild.get("parentid");
                if (parendId != null && deptId == Long.parseLong(parendId.toString())) {
                    children.add(deptChild);
                }
            }
            dept.put("children", children);
        }
        JSONObject rootDeptObj = JSONObject.fromObject(rootDept);

        // 需要组织同步设置中，部门同步开关打开，才进行部门同步
        String deptSyncSwitch = dingtalkConfig.getDeptSyncSwitch();
        if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(deptSyncSwitch)) {
            ProcessUtils.start(deptArray.size(), "同步部门");
            String logId = paramMap.get("logId").toString();
            rootDeptObj.put("logId", logId);
            iteratorDeptTree(rootDeptObj, 1);// 同步部门

            // 比对获取到的钉钉部门列表，与原先的表中的部门列表进行对比，如果不存在，则钉钉上该部门已删除
            List<MultiOrgDingDept> dingDepts = multiOrgDingDeptService.getAllDingDepts();
            List<String> oldDeptIdList = dingDepts.stream().map(MultiOrgDingDept::getId).collect(Collectors.toList());
//			for (MultiOrgDingDept dept : dingDepts) {
//				oldDeptIdList.add(dept.getId());
//			}
            // 取差集，得到删除的部门ID
            oldDeptIdList.removeAll(newDeptIdList);

            for (String deptId : oldDeptIdList) {
                // 同步删除
                multiOrgDingDeptService.deleteDeptFromDingtalk(deptId, logId);
            }
        }

        ProcessUtils.start(deptArray.size(), "同步用户");
        iteratorUserTree(rootDeptObj, new ArrayList<String>());// 同步用户
    }

    /**
     * 同步部门路径
     *
     * @param deptId
     */
    public synchronized void iteratortDeptPath(String deptId) {
        JSONObject deptObj = DingtalkApiUtils.getDingTalkDeptInfo(deptId, DingtalkApiUtils.getAccessToken());
        if (deptObj != null) {
            String parentId = deptObj.optString("parentid");
            if (StringUtils.isBlank(deptId) || StringUtils.equals("0", deptId) || StringUtils.equals("1", deptId)) {
                // 忽略根部门
            } else {
                MultiOrgDingDept multiOrgDingDept = multiOrgDingDeptService.getByDingDeptId(parentId);
                if (multiOrgDingDept == null || StringUtils.isBlank(multiOrgDingDept.getEleId())) {
                    iteratortDeptPath(parentId);// 向上递归,确保父节点存在
                }
            }
            multiOrgDingDeptService.saveAndUpdateDeptFromDingtalk(deptObj, 0, true);
        }
    }

    /**
     * 同步部门树
     *
     * @param deptObj
     * @param order
     */
    public synchronized void iteratorDeptTree(JSONObject deptObj, int order) {
        ProcessUtils.increase(1, deptObj.optString("name"));
        multiOrgDingDeptService.saveAndUpdateDeptFromDingtalk(deptObj, order, false, deptObj.getString("logId"));
        // 保存children节点
        JSONArray children = deptObj.optJSONArray("children");
        if (null == children || JSONUtils.isNull(children)) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            JSONObject iteratorDept = children.getJSONObject(i);
            if (null != iteratorDept) {
                iteratorDept.put("logId", paramMap.get("logId"));
                iteratorDeptTree(iteratorDept, i);
            }
        }
    }

    /**
     * 同步部门列表
     *
     * @param deptIds
     * @param eventType
     */
    public synchronized void iteratorDeptList(JSONArray deptIds, String eventType, String uuid) {
        if (null == deptIds || JSONUtils.isNull(deptIds)) {
            return;
        }
        for (int i = 0; i < deptIds.size(); i++) {
            String deptId = deptIds.getString(i);
            switch (eventType) {
                case "org_dept_create":// 通讯录企业部门创建
                case "org_dept_modify":// 通讯录企业部门修改
                    JSONObject deptObj = DingtalkApiUtils.getDingTalkDeptInfo(deptId, DingtalkApiUtils.getAccessToken()); // 调取钉钉接口获取部门详情
                    multiOrgDingDeptService.saveAndUpdateDeptFromDingtalk(deptObj, i, true, uuid);
                    break;
                case "org_dept_remove":// 通讯录企业部门删除
                    multiOrgDingDeptService.deleteDeptFromDingtalk(deptId, uuid);
                    break;
                default: //
                    break;
            }
        }
    }

    /**
     * 同步用户列表
     *
     * @param userIds
     * @param eventType
     */
    public synchronized void iteratorUserList(JSONArray userIds, String eventType, String uuid) {
        if (null == userIds || JSONUtils.isNull(userIds)) {
            return;
        }
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.getString(i);
            switch (eventType) {
                case "user_add_org":// 通讯录用户增加
                case "user_modify_org":// 通讯录用户更改
                    JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(userId, DingtalkApiUtils.getAccessToken());
                    if (60121 == userObj.getInt("errcode")) {
                        MultiOrgSyncUserLog log = new MultiOrgSyncUserLog();
                        log.setLogId(uuid);
                        log.setUserId(userId);
                        if (eventType.equals("user_add_org")) {
                            log.setOperationName(DingtalkInfo.SYNC_OPERATION_ADD);
                        } else if (eventType.equals("user_modify_org")) {
                            log.setOperationName(DingtalkInfo.SYNC_OPERATION_MOD);
                        }
                        log.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
                        log.setRemark(DingtalkInfo.SYNC_USER_ERROR_NO_EXISTS);
                        multiOrgSyncUserLogService.save(log);
                    } else {
                        multiOrgDingUserService.saveAndUpdateUserFromDingtalk(userObj, uuid, true);
                    }
                    break;
                case "user_leave_org":// 通讯录用户离职
                    MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByDingUserId(userId);
                    multiOrgDingUserService.deleteUserFromDingtalk(userId);
                    saveSyncUserLog(uuid, DingtalkInfo.SYNC_OPERATION_DEL, multiOrgDingUser);
                    break;
                case "user_active_org":// 加入企业后用户激活
                case "org_admin_add":// 通讯录用户被设为管理员
                case "org_admin_remove":// 通讯录用户被取消设置管理员
                    userObj = DingtalkApiUtils.getDingTalkUserInfo(userId, DingtalkApiUtils.getAccessToken());
                    multiOrgDingUserService.saveAndUpdateUserFromDingtalk(userObj, uuid, false);
                    break;
                default: //
                    break;
            }
        }
    }

    /**
     * 同步部门归属用户
     *
     * @param deptObj
     * @param ignore
     */
    public synchronized void iteratorUserTree(JSONObject deptObj, List<String> ignore) {
        // 保存用户列表
        iteratorUserListByDeptId(deptObj.getLong("id") + "", ignore);
        // 保存children节点
        JSONArray children = deptObj.optJSONArray("children");
        if (null == children || JSONUtils.isNull(children)) {
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            JSONObject iteratorDept = children.getJSONObject(i);
            iteratorUserTree(iteratorDept, ignore);
        }
    }

    /**
     * 根据部门ID同步用户列表（不包括已删除的人员）
     *
     * @param deptId
     */
    public synchronized void iteratorUserListByDeptId(String deptId, String logId) {
//		iteratorUserListByDeptId(deptId, null);
        JSONObject jb = DingtalkApiUtils.getDeptMember(deptId, DingtalkApiUtils.getAccessToken());
        JSONArray userIds = jb.optJSONArray("userIds");
        if (null == userIds || JSONUtils.isNull(userIds)) {
            return;
        }
        ProcessUtils.start(userIds.size(), "同步部门[" + deptId + "]用户");
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.getString(i);
            JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(userId, DingtalkApiUtils.getAccessToken());
            ProcessUtils.increase(1, userObj.optString("name"));
            multiOrgDingUserService.saveAndUpdateUserFromDingtalk(userObj, logId, true);
        }
    }

    public synchronized void iteratorUserListByDeptId(String deptId, List<String> ignore) {
        JSONObject jb = DingtalkApiUtils.getDeptMember(deptId, DingtalkApiUtils.getAccessToken());
        JSONArray userIds = jb.optJSONArray("userIds");
        if (null == userIds || JSONUtils.isNull(userIds)) {
            return;
        }
        ProcessUtils.start(userIds.size(), "同步部门[" + deptId + "]用户");
        List<String> newUserIds = Lists.newArrayList();
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.getString(i);
            newUserIds.add(userId);
            if (ignore != null) {
                if (ignore.contains(userId)) {
                    continue;// 人员在多部门，忽略更新
                } else {
                    ignore.add(userId);
                }
            }
            JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(userId, DingtalkApiUtils.getAccessToken());
            ProcessUtils.increase(1, userObj.optString("name"));
            multiOrgDingUserService.saveAndUpdateUserFromDingtalk(userObj, paramMap.get("logId").toString(), true);
        }

        // 对钉钉已删除的用户，进行禁用
        List<String> oldUserIds = multiOrgDingUserService.getDingIdsByDeptId(deptId);
        oldUserIds.removeAll(newUserIds);
        for (String userId : oldUserIds) {
            // 该部门下没有某个人，可能是换部门了。因此需要调用钉钉接口查询是否存在，确认不存在后才可删除
            JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(userId, DingtalkApiUtils.getAccessToken());
            int errorCode = userObj.getInt("errcode");
            if (60121 == errorCode) {
                // 找不到该用户
                MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByDingUserId(userId);
                if (null != multiOrgDingUser) {
                    multiOrgDingUserService.deleteUserFromDingtalk(userId);
                    saveSyncUserLog(paramMap.get("logId").toString(), DingtalkInfo.SYNC_OPERATION_DEL, multiOrgDingUser);
                }
            }
        }
    }

    private void saveSyncUserLog(String logId, String operationName, MultiOrgDingUser multiOrgDingUser) {
        MultiOrgSyncUserLog log = new MultiOrgSyncUserLog();
        log.setLogId(logId);
        log.setUserId(multiOrgDingUser.getDing_userId());
        log.setName(multiOrgDingUser.getName());
        log.setOperationName(operationName);
        log.setMobile(multiOrgDingUser.getMobile());
        log.setSyncStatus(DingtalkInfo.SYNC_STATUS_SUCCESS);
        log.setRemark(StringUtils.EMPTY);
        multiOrgSyncUserLogService.save(log);
    }

    public synchronized void iteratorGroupList(JSONArray groupList) {
        if (null == groupList || JSONUtils.isNull(groupList)) {
            return;
        }
        for (int i = 0; i < groupList.size(); i++) {
            JSONObject groupObj = groupList.getJSONObject(i);
            String groupId = groupObj.getString("groupId");
            String groupName = groupObj.getString("name");
            JSONArray roleList = groupObj.getJSONArray("roles");
            iteratorRoleList(groupId, groupName, roleList);
        }
    }

    public void iteratorRoleList(JSONArray roleIdList, String eventType) {
        if (null == roleIdList || JSONUtils.isNull(roleIdList)) {
            return;
        }
        JSONObject resultObj = null, roleObj = null;
        String accessToken = DingtalkApiUtils.getAccessToken();
        for (int i = 0; i < roleIdList.size(); i++) {
            String roleId = roleIdList.getString(i);
            switch (eventType) {
                case "label_conf_add"://
                case "label_conf_modify"://
                    resultObj = DingtalkApiUtils.getRole(Long.parseLong(roleId), accessToken);
                    roleObj = resultObj.getJSONObject("role");
                    roleObj.put("id", roleId);
                    //	roleObj.put("groupId", groupId);
                    //	roleObj.put("groupName", groupName);
                    multiOrgDingRoleService.saveAndUpdateRoleFromDingtalk(roleObj);
                    break;
                case "label_conf_del"://
                    multiOrgDingRoleService.deleteRoleFromDingtalk(roleId);
                    break;
                case "label_user_change"://
                    resultObj = DingtalkApiUtils.getRole(Long.parseLong(roleId), accessToken);
                    roleObj = resultObj.getJSONObject("role");
                    roleObj.put("id", roleId);
                    JSONObject userResultObj = DingtalkApiUtils
                            .getRoleSimpleList(Long.parseLong(roleId), true, accessToken);
                    roleObj.put("users", userResultObj.getJSONObject("result").getJSONArray("list"));
                    multiOrgDingRoleService.saveAndUpdateRoleFromDingtalk(roleObj);
                    break;
                default: //
                    break;
            }
        }
    }

    public void iteratorRoleList(String groupId, String groupName, JSONArray roleList) {
        if (null == roleList || JSONUtils.isNull(roleList)) {
            return;
        }
        for (int i = 0; i < roleList.size(); i++) {
            JSONObject roleObj = roleList.getJSONObject(i);
            roleObj.put("groupId", groupId);
            roleObj.put("groupName", groupName);
            multiOrgDingRoleService.saveAndUpdateRoleFromDingtalk(roleObj);
        }
    }

    /**
     * 单条异常数据重新同步
     *
     * @param logId
     * @param type
     */
    public Integer syncOneData(String logId, String type) {
        switch (type) {
            case DingtalkInfo.SYNC_DEPT_AGAIN:
                if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(dingtalkConfig.getDeptSyncSwitch())) {
                    MultiOrgSyncDeptLog log = multiOrgSyncDeptLogService.getOne(logId);
                    if (null != log && DingtalkInfo.SYNC_STATUS_ERROR.intValue() == log.getSyncStatus().intValue()) {
                        // 当部门同步开关开启  且  日志同步状态为异常时，同步部门数据（部门的单条数据需要同步部门的人员、人员和职位关系的信息，不包括删除的人员）
                        MultiOrgSyncLog multiOrgSyncLog = null;
                        if (DingtalkInfo.SYNC_OPERATION_DEL.equals(log.getOperationName())) {
                            multiOrgSyncLog = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_DEPT);
                        } else {
                            multiOrgSyncLog = saveOrgSyncLog();
                        }

                        try {
                            if (DingtalkInfo.SYNC_OPERATION_DEL.equals(log.getOperationName())) {
                                // 同步删除
                                multiOrgDingDeptService.deleteDeptFromDingtalk(log.getDeptId(), multiOrgSyncLog.getUuid());
                            } else {
                                JSONObject deptObj = DingtalkApiUtils.getDingTalkDeptInfo(log.getDeptId(), DingtalkApiUtils.getAccessToken());
                                deptObj.put("logId", multiOrgSyncLog.getUuid());
                                multiOrgDingDeptService.saveAndUpdateDeptFromDingtalk(deptObj, 1, false, multiOrgSyncLog.getUuid());
                                iteratorUserListByDeptId(log.getDeptId(), multiOrgSyncLog.getUuid());
                            }
                            updateOrgSyncLog(multiOrgSyncLog);
                        } catch (Exception e) {
                            logger.error("重新同步部门异常：" + e.getMessage(), e);
                            multiOrgSyncLog.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
                            multiOrgSyncLogService.update(multiOrgSyncLog);
                            return DingtalkInfo.SYNC_STATUS_ERROR;
                        }
                        return multiOrgSyncLog.getSyncStatus();
                    }
                }
                return DingtalkInfo.SYNC_STATUS_SUCCESS;
            case DingtalkInfo.SYNC_USER_AGAIN:
                MultiOrgSyncUserLog log = multiOrgSyncUserLogService.getOne(logId);
                if (null != log && DingtalkInfo.SYNC_STATUS_ERROR.intValue() == log.getSyncStatus().intValue()) {
                    MultiOrgSyncLog multiOrgSyncLog = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_USER);
                    try {
                        JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(log.getUserId(), DingtalkApiUtils.getAccessToken());
                        int errCode = userObj.getInt("errcode");
                        if (60121 == errCode) {
                            // 钉钉上人员不存在（删除）
                            MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByDingUserId(log.getUserId());
                            multiOrgDingUserService.deleteUserFromDingtalk(log.getUserId());
                            saveSyncUserLog(multiOrgSyncLog.getUuid(), DingtalkInfo.SYNC_OPERATION_DEL, multiOrgDingUser);
                        } else {
                            multiOrgDingUserService.saveAndUpdateUserFromDingtalk(userObj, multiOrgSyncLog.getUuid(), false);
                        }
                        updateOrgSyncLog(multiOrgSyncLog);
                    } catch (Exception e) {
                        logger.error("重新同步人员异常：" + e.getMessage(), e);
                        multiOrgSyncLog.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
                        multiOrgSyncLogService.update(multiOrgSyncLog);
                        return DingtalkInfo.SYNC_STATUS_ERROR;
                    }
                    return multiOrgSyncLog.getSyncStatus();
                }
                return DingtalkInfo.SYNC_STATUS_SUCCESS;
            case DingtalkInfo.SYNC_USER_WORK_AGAIN:
                if (DingtalkInfo.SYNC_SWITCH_OPEN.equals(dingtalkConfig.getWorkinfoSyncSwitch())) {
                    MultiOrgSyncUserWorkLog userWorkLog = multiOrgSyncUserWorkLogService.getOne(logId);
                    if (null != userWorkLog && DingtalkInfo.SYNC_STATUS_ERROR.intValue() == userWorkLog.getSyncStatus().intValue()) {
                        MultiOrgSyncLog multiOrgSyncLog = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_USER_WORK);
                        try {
                            JSONObject userObj = DingtalkApiUtils.getDingTalkUserInfo(userWorkLog.getUserId(), DingtalkApiUtils.getAccessToken());
                            multiOrgDingUserService.saveAndUpdateUserWork(userObj, multiOrgSyncLog.getUuid(), userWorkLog.getDeptId(), userWorkLog.getJobName());

                            updateOrgSyncLog(multiOrgSyncLog);
                        } catch (Exception e) {
                            logger.error("重新同步人员和职位关系异常：" + e.getMessage(), e);
                            multiOrgSyncLog.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
                            multiOrgSyncLogService.update(multiOrgSyncLog);
                            return DingtalkInfo.SYNC_STATUS_ERROR;
                        }
                        return multiOrgSyncLog.getSyncStatus();
                    }
                }
                return DingtalkInfo.SYNC_STATUS_SUCCESS;
            default:
                break;
        }
        return DingtalkInfo.SYNC_STATUS_ERROR;
    }

    /**
     * 业务事件 重新同步  同步后生成组织同步日志
     */
    public Integer syncEventData(String logId) {
        MultiOrgSyncLog log = null;
        EventCallBack event = eventCallBackService.getOne(logId);
        try {
            if (null != event && DingtalkInfo.SYNC_STATUS_ERROR.intValue() == event.getStatus()) {
                String eventType = event.getEventType();
                switch (eventType) {
                    case "user_add_org":// 通讯录用户增加
                    case "user_modify_org":// 通讯录用户更改
                        log = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_USER + "、" + DingtalkInfo.SYNC_CONTENT_USER_WORK);
                        iteratorUserList(JSONArray.fromObject(event.getDingUserId()), eventType, log.getUuid());
                        break;
                    case "user_leave_org":// 通讯录用户离职
                    case "user_active_org":// 加入企业后用户激活
                    case "org_admin_add":// 通讯录用户被设为管理员
                    case "org_admin_remove":// 通讯录用户被取消设置管理员
                        log = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_USER);
                        iteratorUserList(JSONArray.fromObject(event.getDingUserId()), eventType, log.getUuid());
                        break;
                    case "org_dept_create":// 通讯录企业部门创建
                    case "org_dept_modify":// 通讯录企业部门修改
                    case "org_dept_remove":// 通讯录企业部门删除
                        log = saveOrgSyncLog(DingtalkInfo.SYNC_CONTENT_DEPT);
                        iteratorDeptList(JSONArray.fromObject(event.getDingDeptId()), eventType, log.getUuid());
                        break;
                    case "org_remove":// 企业被解散
                        break;
                    case "check_url"://
                        break;
                    case "bpms_task_change"://
                        break;
                    case "bpms_instance_change"://
                        break;
                    case "label_user_change"://
                        iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                        break;
                    case "label_conf_add"://
                        iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                        break;
                    case "label_conf_modify"://
                        iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                        break;
                    case "label_conf_del"://
                        iteratorRoleList(JSONArray.fromObject(event.getDingLabelId()), eventType);
                        break;
                    default: //
                        break;
                }
                updateOrgSyncLog(log);
                event.setStatus(log.getSyncStatus());
            }
        } catch (Exception ex) {
            if (null != log) {
                log.setSyncStatus(DingtalkInfo.SYNC_STATUS_ERROR);
                multiOrgSyncLogService.save(log);
            }
            event.setStatus(EventCallBack.CALLBACK_STATUS_2);
            logger.error("业务事件重新同步异常：" + ex.getMessage(), ex);
        } finally {
            eventCallBackService.save(event);
        }
        if (null != log) {
            return log.getSyncStatus();
        } else {
            return DingtalkInfo.SYNC_STATUS_ERROR;
        }
    }

}
