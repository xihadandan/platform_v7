/*
 * @(#)4/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service.impl;

import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import com.google.common.collect.Lists;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkConfigEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkEventCallbackFacadeService;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkOrgSyncFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkConfigService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkEventHoler;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkEventUtils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import shade.com.alibaba.fastjson2.JSONArray;
import shade.com.alibaba.fastjson2.JSONObject;

import java.util.Iterator;
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
 * 4/21/25.1	    zhulh		4/21/25		    Create
 * </pre>
 * @date 4/21/25
 */
@Service
public class DingtalkEventCallbackFacadeServiceImpl implements DingtalkEventCallbackFacadeService, ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DingtalkConfigService dingtalkConfigService;

    @Autowired
    private DingtalkOrgSyncFacadeService dingtalkOrgSyncFacadeService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        GenericEventListener listener = getEventListener();

        List<DingtalkConfigEntity> dingtalkConfigEntities = dingtalkConfigService.listAll();
        dingtalkConfigEntities.forEach(configEntity -> {
            if (BooleanUtils.isNotTrue(configEntity.getEnabled())) {
                return;
            }

            try {
                DingtalkApiV2Utils.createWsClient(configEntity.getClientId(), configEntity.getClientSecret(), listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private GenericEventListener getEventListener() {
        return new GenericEventListener() {
            private Boolean processing = false;

            public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
                try {
                    // 添加事件
                    addEvent(event);
                    synchronized (processing) {
                        if (processing) {
                            return EventAckStatus.SUCCESS;
                        } else {
                            processing = true;
                        }
                    }

                    // 处理事件
                    processEvent();
                    synchronized (processing) {
                        processing = false;
                    }

                    // 是否还存在事件并处理
                    if (hasEvent()) {
                        processEvent();
                    }

                    // 消费成功
                    return EventAckStatus.SUCCESS;
                } catch (Exception e) {
                    // 消费失败
                    logger.error(e.getMessage(), e);
                    processing = false;
                    return EventAckStatus.LATER;
                }
            }

            private void addEvent(GenericOpenDingTalkEvent event) {
                DingtalkEventUtils.addEvent(event);
            }

            private boolean hasEvent() {
                return CollectionUtils.isNotEmpty(DingtalkEventUtils.getEvents());
            }

            private void processEvent() {
                List<GenericOpenDingTalkEvent> events = Lists.newArrayList(DingtalkEventUtils.getEvents());
                Iterator<GenericOpenDingTalkEvent> iterator = events.iterator();
                while (iterator.hasNext()) {
                    GenericOpenDingTalkEvent event = iterator.next();
                    doProcessEvent(event);
                    DingtalkEventUtils.removeEvent(event);
                }

                if (CollectionUtils.isNotEmpty(DingtalkEventUtils.getEvents())) {
                    processEvent();
                }
            }
        };
    }

    private void doProcessEvent(GenericOpenDingTalkEvent event) {
        if (DingtalkEventUtils.existsEvent(event.getEventId())) {
            return;
        }

        String appId = event.getEventUnifiedAppId();
        DingtalkConfigEntity configEntity = dingtalkConfigService.getByAppId(appId);
        if (configEntity == null || BooleanUtils.isNotTrue(configEntity.getEnabled())) {
            logger.error("钉钉接入配置不存在或未启用");
        }

        DingtalkConfigVo dingtalkConfigVo = DingtalkConfigVo.fromEntity(configEntity);

        try {
            DingtalkEventUtils.addEvent(event.getEventId());
            RequestSystemContextPathResolver.setSystem(configEntity.getSystem());
            IgnoreLoginUtils.login(configEntity.getTenant(), configEntity.getCreator());

            DingtalkEventHoler.create(event);
            // 事件类型
            String eventType = event.getEventType();
            if (!isRegistryEvent(dingtalkConfigVo, eventType)) {
                DingtalkEventHoler.error("应用配置没有监听事件");
                return;
            }

            //处理事件
            switch (eventType) {
                // 通讯录用户增加
                case "user_add_org":
                    handleUserCreated(event, dingtalkConfigVo);
                    break;
                // 通讯录用户更改
                case "user_modify_org":
                    handleUserUpdated(event, dingtalkConfigVo);
                    break;
                // 通讯录用户离职
                case "user_leave_org":
                    handleUserDeleted(event, dingtalkConfigVo);
                    break;
                // 加入企业后用户激活
                case "user_active_org":
                    handleUserActived(event, dingtalkConfigVo);
                    break;
                // 通讯录企业部门创建
                case "org_dept_create":
                    handleDepartmentCreated(event, dingtalkConfigVo);
                    break;
                // 通讯录企业部门修改
                case "org_dept_modify":
                    handleDepartmentUpdated(event, dingtalkConfigVo);
                    break;
                // 通讯录企业部门删除
                case "org_dept_remove":
                    handleDepartmentDeleted(event, dingtalkConfigVo);
                    break;
            }
        } catch (Exception e) {
            DingtalkEventUtils.removeEvent(event.getEventId());
            DingtalkEventHoler.error(e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            DingtalkEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private boolean isRegistryEvent(DingtalkConfigVo dingtalkConfigVo, String eventType) {
        List<String> orgSyncEvents = dingtalkConfigVo.getConfiguration().getOrgSyncEvents();
        return CollectionUtils.isNotEmpty(orgSyncEvents) && orgSyncEvents.contains(eventType);
    }

    private void handleUserCreated(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("userId");
        jsonArray.forEach(userid -> {
            OapiV2UserGetResponse.UserGetResponse userGetResponse = DingtalkApiV2Utils.getUserByUserid(String.valueOf(userid), dingtalkConfigVo);
            List<Long> deptIdList = userGetResponse.getDeptIdList();
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                deptIdList.forEach(deptId -> {
                    createParentDepartmentIfAbsent(deptId, dingtalkConfigVo);
                });
            }
            dingtalkOrgSyncFacadeService.createUser(userGetResponse, dingtalkConfigVo);
        });
    }

    private void handleUserUpdated(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
//        // 事件唯一Id
//        String eventId = event.getEventId();
//        // 事件类型
//        String eventType = event.getEventType();
//        // 事件产生时间
//        Long bornTime = event.getEventBornTime();
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("userId");
        jsonArray.forEach(userid -> {
            OapiV2UserGetResponse.UserGetResponse userGetResponse = DingtalkApiV2Utils.getUserByUserid(String.valueOf(userid), dingtalkConfigVo);
            List<Long> deptIdList = userGetResponse.getDeptIdList();
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                deptIdList.forEach(deptId -> {
                    createParentDepartmentIfAbsent(deptId, dingtalkConfigVo);
                });
            }
            dingtalkOrgSyncFacadeService.updateUser(userGetResponse, dingtalkConfigVo);
        });
    }

    private void handleUserDeleted(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("userId");
        jsonArray.forEach(userid -> {
            //  OapiV2UserGetResponse.UserGetResponse userGetResponse = DingtalkApiV2Utils.getUserByUserid(String.valueOf(userid), dingtalkConfigVo);
            dingtalkOrgSyncFacadeService.deleteUser(String.valueOf(userid), dingtalkConfigVo);
        });
    }

    private void handleUserActived(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("userId");
        jsonArray.forEach(userId -> {
            //  OapiV2UserGetResponse.UserGetResponse userGetResponse = DingtalkApiV2Utils.getUserByUserid(String.valueOf(userid), dingtalkConfigVo);
            dingtalkOrgSyncFacadeService.activeUser(String.valueOf(userId), dingtalkConfigVo);
        });
    }

    private void handleDepartmentCreated(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("deptId");
        jsonArray.forEach(deptId -> {
            if (DingtalkEventUtils.isAbsentDepartmentId(deptId)) {
                DingtalkEventUtils.removeAbsentDepartmentId(deptId);
                return;
            }
            OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse = DingtalkApiV2Utils.getDepartmentByDeptId(Long.valueOf(deptId.toString()), dingtalkConfigVo);
            createParentDepartmentIfAbsent(deptGetResponse.getParentId(), dingtalkConfigVo);
            if (!dingtalkOrgSyncFacadeService.isExistsDepartment(deptGetResponse.getDeptId())) {
                dingtalkOrgSyncFacadeService.createDepartment(deptGetResponse, dingtalkConfigVo);
            } else {
                DingtalkEventHoler.success("createdDingtalkDept", dingtalkOrgSyncFacadeService.getDingtalkDeptById(deptGetResponse.getDeptId()));
            }
        });
    }

    private void createParentDepartmentIfAbsent(Long parentId, DingtalkConfigVo dingtalkConfigVo) {
        if (Long.valueOf(1).equals(parentId)) {
            return;
        }
        if (dingtalkOrgSyncFacadeService.isExistsDepartment(parentId)) {
            return;
        }

        OapiV2DepartmentGetResponse.DeptGetResponse parentDeptGetResponse = DingtalkApiV2Utils.getDepartmentByDeptId(parentId, dingtalkConfigVo);
        if (parentDeptGetResponse == null) {
            return;
        }

        createParentDepartmentIfAbsent(parentDeptGetResponse.getParentId(), dingtalkConfigVo);
        dingtalkOrgSyncFacadeService.createDepartment(parentDeptGetResponse, dingtalkConfigVo);
        DingtalkEventUtils.addAbsentDepartmentId(parentDeptGetResponse.getDeptId());
    }

    private void handleDepartmentUpdated(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("deptId");
        jsonArray.forEach(deptId -> {
            OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse = DingtalkApiV2Utils.getDepartmentByDeptId(Long.valueOf(deptId.toString()), dingtalkConfigVo);
            dingtalkOrgSyncFacadeService.updateDepartment(deptGetResponse, dingtalkConfigVo);
        });
    }

    private void handleDepartmentDeleted(GenericOpenDingTalkEvent event, DingtalkConfigVo dingtalkConfigVo) {
        // 获取事件体
        JSONObject bizData = event.getData();
        JSONArray jsonArray = bizData.getJSONArray("deptId");
        jsonArray.forEach(deptId -> {
            // OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse = DingtalkApiV2Utils.getDepartmentByDeptId(Long.valueOf(deptId), dingtalkConfigVo);
            dingtalkOrgSyncFacadeService.deleteDepartment(Long.valueOf(deptId.toString()), dingtalkConfigVo);
        });
    }

    @Override
    public void updateWsClient(DingtalkConfigEntity entity) {
        try {
            if (BooleanUtils.isNotTrue(entity.getEnabled())) {
                DingtalkApiV2Utils.removeWsClient(entity.getClientId());
            } else {
                DingtalkApiV2Utils.createWsClient(entity.getClientId(), entity.getClientSecret(), getEventListener());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
