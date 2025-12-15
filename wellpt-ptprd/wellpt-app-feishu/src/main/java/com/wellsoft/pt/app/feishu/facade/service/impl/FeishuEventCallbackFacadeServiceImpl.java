/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.facade.service.impl;

import com.google.common.collect.Lists;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.contact.ContactService;
import com.lark.oapi.service.contact.v3.model.*;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.*;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.pt.app.feishu.entity.FeishuConfigEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.app.feishu.facade.service.FeishuEventCallbackFacadeService;
import com.wellsoft.pt.app.feishu.facade.service.FeishuOrgSyncFacadeService;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuDeptService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.service.FeishuWorkRecordService;
import com.wellsoft.pt.app.feishu.support.FeishuEventHoler;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.utils.FeishuEventUtils;
import com.wellsoft.pt.app.feishu.utils.FeishuGroupChatUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
@Service
public class FeishuEventCallbackFacadeServiceImpl implements FeishuEventCallbackFacadeService, ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private FeishuDeptService feishuDeptService;

    @Autowired
    private FeishuWorkRecordService feishuWorkRecordService;

    @Autowired
    private FeishuOrgSyncFacadeService feishuOrgSyncFacadeService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("workService")
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    private Object deptLock = new Object();

    @Override
    public void createWsClient(FeishuConfigVo feishuConfigVo) {
        if (BooleanUtils.isNotTrue(feishuConfigVo.getEnabled())) {
            return;
        }

        FeishuApiUtils.createWsClient(feishuConfigVo, getEventDispatcher());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        EventDispatcher eventDispatcher = getEventDispatcher();
        List<FeishuConfigEntity> feishuConfigEntities = feishuConfigService.listAll();
        feishuConfigEntities.forEach(feishuConfigEntity -> {
            if (BooleanUtils.isNotTrue(feishuConfigEntity.getEnabled())) {
                return;
            }
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            FeishuApiUtils.createWsClient(feishuConfigVo, eventDispatcher);
        });
    }

    private EventDispatcher getEventDispatcher() {
        return EventDispatcher.newBuilder("", "")
                .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                    @Override
                    public void handle(P2MessageReceiveV1 event) throws Exception {
                        scheduledExecutorService.execute(() -> handleP2MessageReceiveV1(event));
                    }
                })
                .onP2DepartmentCreatedV3(new ContactService.P2DepartmentCreatedV3Handler() {
                    @Override
                    public void handle(P2DepartmentCreatedV3 p2DepartmentCreatedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2DepartmentCreatedV3.getHeader().getEventId())) {
                            return;
                        }

                        // handleP2DepartmentCreatedV3(p2DepartmentCreatedV3);
                        scheduledExecutorService.execute(() -> handleP2DepartmentCreatedV3(p2DepartmentCreatedV3));
                    }
                })
                .onP2DepartmentDeletedV3(new ContactService.P2DepartmentDeletedV3Handler() {
                    @Override
                    public void handle(P2DepartmentDeletedV3 p2DepartmentDeletedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2DepartmentDeletedV3.getHeader().getEventId())) {
                            return;
                        }
                        handleP2DepartmentDeletedV3(p2DepartmentDeletedV3);
                        // scheduledExecutorService.execute(() -> handleP2DepartmentDeletedV3(p2DepartmentDeletedV3));
                    }
                })
                .onP2DepartmentUpdatedV3(new ContactService.P2DepartmentUpdatedV3Handler() {
                    @Override
                    public void handle(P2DepartmentUpdatedV3 p2DepartmentUpdatedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2DepartmentUpdatedV3.getHeader().getEventId())) {
                            return;
                        }
                        handleP2DepartmentUpdatedV3(p2DepartmentUpdatedV3);
                        // scheduledExecutorService.execute(() -> handleP2DepartmentUpdatedV3(p2DepartmentUpdatedV3));
                    }
                })
                .onP2UserCreatedV3(new ContactService.P2UserCreatedV3Handler() {
                    @Override
                    public void handle(P2UserCreatedV3 p2UserCreatedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2UserCreatedV3.getHeader().getEventId())) {
                            return;
                        }
                        // handleP2UserCreatedV3(p2UserCreatedV3);
                        scheduledExecutorService.execute(() -> handleP2UserCreatedV3(p2UserCreatedV3));
                    }
                })
                .onP2UserDeletedV3(new ContactService.P2UserDeletedV3Handler() {
                    @Override
                    public void handle(P2UserDeletedV3 p2UserDeletedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2UserDeletedV3.getHeader().getEventId())) {
                            return;
                        }
                        handleP2UserDeletedV3(p2UserDeletedV3);
                    }
                })
                .onP2UserUpdatedV3(new ContactService.P2UserUpdatedV3Handler() {
                    @Override
                    public void handle(P2UserUpdatedV3 p2UserUpdatedV3) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2UserUpdatedV3.getHeader().getEventId())) {
                            return;
                        }
                        handleP2UserUpdatedV3(p2UserUpdatedV3);
                    }
                }).onP2ChatDisbandedV1(new ImService.P2ChatDisbandedV1Handler() {

                    @Override
                    public void handle(P2ChatDisbandedV1 p2ChatDisbandedV1) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2ChatDisbandedV1.getHeader().getEventId())) {
                            return;
                        }
                        handleP2ChatDisbandedV1(p2ChatDisbandedV1);
                    }
                }).onP2ChatMemberUserAddedV1(new ImService.P2ChatMemberUserAddedV1Handler() {
                    @Override
                    public void handle(P2ChatMemberUserAddedV1 p2ChatMemberUserAddedV1) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2ChatMemberUserAddedV1.getHeader().getEventId())) {
                            return;
                        }
                        handleP2ChatMemberUserAddedV1(p2ChatMemberUserAddedV1);
                    }
                }).onP2ChatMemberUserDeletedV1(new ImService.P2ChatMemberUserDeletedV1Handler() {
                    @Override
                    public void handle(P2ChatMemberUserDeletedV1 p2ChatMemberUserDeletedV1) throws Exception {
                        if (FeishuEventUtils.existsEvent(p2ChatMemberUserDeletedV1.getHeader().getEventId())) {
                            return;
                        }
                        handleP2ChatMemberUserDeletedV1(p2ChatMemberUserDeletedV1);
                    }
                })
//                .onCustomizedEvent("这里填入你要自定义订阅的 event 的 key，例如 out_approval", new CustomEventHandler() {
//                    @Override
//                    public void handle(EventReq event) throws Exception {
//                        System.out.printf("[ onCustomizedEvent access ], type: message, data: %s\n", new String(event.getBody(), StandardCharsets.UTF_8));
//                    }
//                })
                .build();
    }


    /**
     * 员工入职
     *
     * @param p2UserCreatedV3
     */
    private void handleP2UserCreatedV3(P2UserCreatedV3 p2UserCreatedV3) {
        String appId = p2UserCreatedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2UserCreatedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2UserCreatedV3.getHeader(), Jsons.DEFAULT.toJson(p2UserCreatedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2UserCreatedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            synchronized (deptLock) {
                P2UserCreatedV3Data p2UserCreatedV3Data = p2UserCreatedV3.getEvent();
                String[] departmentIds = p2UserCreatedV3Data.getObject().getDepartmentIds();
                if (departmentIds != null) {
                    for (String departmentId : departmentIds) {
                        createParentDepartmentIfAbsent(departmentId, feishuConfigVo);
                    }
                }
                feishuOrgSyncFacadeService.createUser(p2UserCreatedV3Data.getObject(), feishuConfigVo);
            }
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2UserCreatedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void createParentDepartmentIfAbsent(String departmentId, FeishuConfigVo feishuConfigVo) {
        if (existsDepartment(departmentId)) {
            return;
        }

        Department department = FeishuApiUtils.getDepartmentByDeptId(departmentId, feishuConfigVo);
        if (department == null) {
            try {
                Thread.sleep(1000 * 10);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            department = FeishuApiUtils.getDepartmentByDeptId(departmentId, feishuConfigVo);
            if (department == null) {
                return;
            }
        }
        createParentDepartmentIfAbsent(department.getParentDepartmentId(), feishuConfigVo);
        feishuOrgSyncFacadeService.createDepartment(department, feishuConfigVo);
    }

    /**
     * 员工离职
     *
     * @param p2UserDeletedV3
     */
    private void handleP2UserDeletedV3(P2UserDeletedV3 p2UserDeletedV3) {
        String appId = p2UserDeletedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2UserDeletedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2UserDeletedV3.getHeader(), Jsons.DEFAULT.toJson(p2UserDeletedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2UserDeletedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            P2UserDeletedV3Data p2UserDeletedV3Data = p2UserDeletedV3.getEvent();
            feishuOrgSyncFacadeService.deleteUser(p2UserDeletedV3Data.getObject(), feishuConfigVo);
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2UserDeletedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    /**
     * 员工信息被修改
     *
     * @param p2UserUpdatedV3
     */
    private void handleP2UserUpdatedV3(P2UserUpdatedV3 p2UserUpdatedV3) {
        String appId = p2UserUpdatedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2UserUpdatedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2UserUpdatedV3.getHeader(), Jsons.DEFAULT.toJson(p2UserUpdatedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2UserUpdatedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            P2UserUpdatedV3Data p2UserUpdatedV3Data = p2UserUpdatedV3.getEvent();
            synchronized (deptLock) {
                String[] departmentIds = p2UserUpdatedV3Data.getObject().getDepartmentIds();
                if (departmentIds != null) {
                    for (String departmentId : departmentIds) {
                        createParentDepartmentIfAbsent(departmentId, feishuConfigVo);
                    }
                }
                feishuOrgSyncFacadeService.updateUser(p2UserUpdatedV3Data.getObject(), feishuConfigVo);
            }
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2UserUpdatedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void handleP2ChatDisbandedV1(P2ChatDisbandedV1 p2ChatDisbandedV1) {
        String appId = p2ChatDisbandedV1.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2ChatDisbandedV1.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2ChatDisbandedV1.getHeader(), Jsons.DEFAULT.toJson(p2ChatDisbandedV1.getEvent()));
            P2ChatDisbandedV1Data p2ChatDisbandedV1Data = p2ChatDisbandedV1.getEvent();
            feishuWorkRecordService.logicDeleteGroupChatByChatId(p2ChatDisbandedV1Data.getChatId());
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2ChatDisbandedV1.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void handleP2ChatMemberUserAddedV1(P2ChatMemberUserAddedV1 p2ChatMemberUserAddedV1) {
        String appId = p2ChatMemberUserAddedV1.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2ChatMemberUserAddedV1.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2ChatMemberUserAddedV1.getHeader(), Jsons.DEFAULT.toJson(p2ChatMemberUserAddedV1.getEvent()));
            P2ChatMemberUserAddedV1Data p2ChatMemberUserAddedV1Data = p2ChatMemberUserAddedV1.getEvent();
            feishuWorkRecordService.addGroupChatMembers(p2ChatMemberUserAddedV1Data);
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2ChatMemberUserAddedV1.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void handleP2ChatMemberUserDeletedV1(P2ChatMemberUserDeletedV1 p2ChatMemberUserDeletedV1) {
        String appId = p2ChatMemberUserDeletedV1.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2ChatMemberUserDeletedV1.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2ChatMemberUserDeletedV1.getHeader(), Jsons.DEFAULT.toJson(p2ChatMemberUserDeletedV1.getEvent()));
            P2ChatMemberUserDeletedV1Data p2ChatMemberUserDeletedV1Data = p2ChatMemberUserDeletedV1.getEvent();
            feishuWorkRecordService.deleteGroupChatMembers(p2ChatMemberUserDeletedV1Data);
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2ChatMemberUserDeletedV1.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    /**
     * 部门新建
     *
     * @param p2DepartmentCreatedV3
     */
    private void handleP2DepartmentCreatedV3(P2DepartmentCreatedV3 p2DepartmentCreatedV3) {
        String appId = p2DepartmentCreatedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2DepartmentCreatedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2DepartmentCreatedV3.getHeader(), Jsons.DEFAULT.toJson(p2DepartmentCreatedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2DepartmentCreatedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            synchronized (deptLock) {
                P2DepartmentCreatedV3Data p2DepartmentCreatedV3Data = p2DepartmentCreatedV3.getEvent();
                createParentDepartmentIfAbsent(p2DepartmentCreatedV3Data.getObject().getParentDepartmentId(), feishuConfigVo);
                Department department = FeishuApiUtils.getDepartmentByDeptId(p2DepartmentCreatedV3Data.getObject().getOpenDepartmentId(), feishuConfigVo);
                if (!existsDepartment(department.getOpenDepartmentId())) {
                    feishuOrgSyncFacadeService.createDepartment(department, feishuConfigVo);
                } else {
                    FeishuEventHoler.success("createdFeishuDept", feishuDeptService.getByOpenDepartmentId(department.getOpenDepartmentId()));
                }
            }
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2DepartmentCreatedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    /**
     * @param openDepartmentId
     * @return
     */
    private boolean existsDepartment(String openDepartmentId) {
        if ("0".equals(openDepartmentId)) {
            return true;
        }
        String orgElementId = feishuDeptService.getOrgElementIdByOpenDepartmentId(openDepartmentId);
        return StringUtils.isNotBlank(orgElementId);
    }

    private boolean isRegistryEvent(FeishuConfigVo feishuConfigVo, String eventType) {
        List<String> orgSyncEvents = feishuConfigVo.getConfiguration().getOrgSyncEvents();
        return CollectionUtils.isNotEmpty(orgSyncEvents) && orgSyncEvents.contains(eventType);
    }

    /**
     * 部门被删除
     *
     * @param p2DepartmentDeletedV3
     */
    private void handleP2DepartmentDeletedV3(P2DepartmentDeletedV3 p2DepartmentDeletedV3) {
        String appId = p2DepartmentDeletedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2DepartmentDeletedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2DepartmentDeletedV3.getHeader(), Jsons.DEFAULT.toJson(p2DepartmentDeletedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2DepartmentDeletedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            P2DepartmentDeletedV3Data p2DepartmentDeletedV3Data = p2DepartmentDeletedV3.getEvent();
            synchronized (deptLock) {
                feishuOrgSyncFacadeService.deleteDepartment(p2DepartmentDeletedV3Data.getObject(), feishuConfigVo);
            }
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2DepartmentDeletedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    /**
     * 部门信息变化
     *
     * @param p2DepartmentUpdatedV3
     */
    private void handleP2DepartmentUpdatedV3(P2DepartmentUpdatedV3 p2DepartmentUpdatedV3) {
        String appId = p2DepartmentUpdatedV3.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }

        try {
            FeishuEventUtils.addEvent(p2DepartmentUpdatedV3.getHeader().getEventId());
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());
            IgnoreLoginUtils.login(feishuConfigEntity.getTenant(), feishuConfigEntity.getCreator());

            FeishuEventHoler.create(p2DepartmentUpdatedV3.getHeader(), Jsons.DEFAULT.toJson(p2DepartmentUpdatedV3.getEvent()));
            FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
            if (!isRegistryEvent(feishuConfigVo, p2DepartmentUpdatedV3.getHeader().getEventType())) {
                FeishuEventHoler.error("应用配置没有监听事件");
                return;
            }

            // DepartmentStatus departmentStatus = p2DepartmentUpdatedV3.getEvent().getObject().getStatus();
            P2DepartmentUpdatedV3Data p2DepartmentUpdatedV3Data = p2DepartmentUpdatedV3.getEvent();
            synchronized (deptLock) {
                createParentDepartmentIfAbsent(p2DepartmentUpdatedV3Data.getObject().getParentDepartmentId(), feishuConfigVo);
                if (!feishuOrgSyncFacadeService.isDepartmentDeleted(p2DepartmentUpdatedV3Data.getObject().getOpenDepartmentId())) {
                    feishuOrgSyncFacadeService.updateDepartment(p2DepartmentUpdatedV3Data, feishuConfigVo);
                } else {
                    FeishuEventHoler.error("部门不存在");
                }
            }
        } catch (Exception e) {
            FeishuEventUtils.removeEvent(p2DepartmentUpdatedV3.getHeader().getEventId());
            FeishuEventHoler.error("执行异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void handleP2MessageReceiveV1(P2MessageReceiveV1 event) {
        P2MessageReceiveV1Data data = event.getEvent();
        if (!StringUtils.equals("user", data.getSender().getSenderType())
                || !StringUtils.equals("group", data.getMessage().getChatType())) {
            logger.error("不是用户或群组消息");
            return;
        }

        String appId = event.getHeader().getAppId();
        FeishuConfigEntity feishuConfigEntity = feishuConfigService.getByAppId(appId);
        if (feishuConfigEntity == null) {
            logger.error(String.format("应用配置ID[%s]不存在", appId));
            return;
        }
        FeishuWorkRecordEntity feishuWorkRecordEntity = feishuWorkRecordService.getGroupChatByChatId(data.getMessage().getChatId());
        if (feishuWorkRecordEntity == null) {
            logger.error("群聊不存在");
            return;
        }

        FeishuConfigVo feishuConfigVo = FeishuConfigVo.create(feishuConfigEntity);
        try {
            RequestSystemContextPathResolver.setSystem(feishuConfigEntity.getSystem());

            FeishuEventHoler.create(event.getHeader(), Jsons.DEFAULT.toJson(event.getEvent()));
            if (!FeishuGroupChatUtils.isAutoSubmit(feishuConfigVo)) {
                IgnoreLoginUtils.login(feishuWorkRecordEntity.getTenant(), feishuWorkRecordEntity.getCreator());
                FeishuEventHoler.success("应用配置自动提交未开启");
                return;
            }

            String oaUserId = feishuUserService.getOaUserIdByOpenIdAndConfigUuid(data.getSender().getSenderId().getOpenId(), feishuWorkRecordEntity.getConfigUuid());
            if (StringUtils.isBlank(oaUserId)) {
                FeishuEventHoler.success(String.format("飞书用户openId[%s]对就的平台用户ID不存在", data.getSender().getSenderId().getOpenId()));
                return;
            }
            IgnoreLoginUtils.login(feishuWorkRecordEntity.getTenant(), oaUserId);

            String content = data.getMessage().getContent();
            boolean autoSubmit = isAutoSubmitText(content);
            if (!autoSubmit) {
                FeishuEventHoler.success("消息内容不是自动提交");
                return;
            }

            String opinionText = getAutoSubmitOpinionText(content);
            String flowInstUuid = feishuWorkRecordEntity.getFlowInstUuid();
            String taskInstUuid = feishuWorkRecordEntity.getTaskInstUuid();
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            if (!taskService.hasTodoPermission(userDetails, taskInstUuid)) {
                FeishuEventHoler.success("用户无待办权限");
                return;
            }

            WorkBean workBean = workService.getTodo(taskInstUuid, flowInstUuid);
            workBean = workService.getWorkData(workBean);
            validateRequireFieldIsEmpty(workBean);

            workBean.setOpinionText(opinionText);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
            workBean.setActionType(WorkFlowOperation.SUBMIT);
            workService.submit(workBean);
            FeishuEventHoler.success("自动提交成功");
        } catch (Exception e) {
            String openId = event.getEvent().getSender().getSenderId().getOpenId();
            if (e instanceof BusinessException) {
                String msg = e.getMessage() + "，请打开流程详情提交。";
                FeishuApiUtils.sendTextMessage("流程自动提交失败", feishuWorkRecordEntity, openId, msg, feishuConfigVo);
                FeishuEventHoler.error("执行异常： " + e.getMessage());
            } else if (e instanceof JsonDataException) {
                JsonDataException exception = (JsonDataException) e;
                FeishuEventHoler.error("执行异常： " + exception.getErrorCode() + ", " + Jsons.DEFAULT.toJson(exception.getData()));
                String msg = "流程自动提交失败，需要选择办理人、流向等操作，请打开流程详情提交。";
                FeishuApiUtils.sendTextMessage("流程自动提交失败", feishuWorkRecordEntity, openId, msg, feishuConfigVo);
            } else {
                String msg = "执行异常： " + e.getMessage() + "，请打开流程详情提交。";
                FeishuApiUtils.sendTextMessage("流程自动提交失败", feishuWorkRecordEntity, openId, msg, feishuConfigVo);
                FeishuEventHoler.error("执行异常： " + e.getMessage());
            }
            logger.error(e.getMessage(), e);
        } finally {
            FeishuEventHoler.commit();
            IgnoreLoginUtils.logout();
            RequestSystemContextPathResolver.clear();
        }
    }

    private void validateRequireFieldIsEmpty(WorkBean workBean) {
        TaskForm taskForm = workService.getTaskForm(workBean);
        DyFormData dyFormData = workBean.getDyFormData();
        String formUuid = dyFormData.getFormUuid();
        List<String> emptyValueFields = new ArrayList<String>();
        // 主表判断
        List<String> requireFields = Lists.newArrayList();
        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        if (notNullFieldMap.containsKey(formUuid)) {
            requireFields.addAll(notNullFieldMap.get(formUuid));
        }
        for (String requireField : requireFields) {
            if (dyFormData.isFieldExist(requireField) && dyFormData.getFieldValue(requireField) == null
                    || StringUtils.isBlank(dyFormData.getFieldValue(requireField).toString())) {
                emptyValueFields.add(requireField);
            }
        }
        if (CollectionUtils.isNotEmpty(emptyValueFields)) {
            List<DyformFieldDefinition> dyformFields = dyFormFacade.getFieldDefinitions(dyFormData.getFormUuid());
            List<String> fieldNames = Lists.newArrayList();
            dyformFields.forEach(field -> {
                if (emptyValueFields.contains(field.getFieldName())) {
                    fieldNames.add(field.getDisplayName());
                }
            });
            throw new BusinessException(String.format("表单必填字段[%s]不能为空", StringUtils.join(fieldNames, ",")));
        }
    }

    private boolean isAutoSubmitText(String content) {
        if (!StringUtils.startsWith(content, "{")) {
            return false;
        }
        JSONObject jsonObject = new JSONObject(content);
        if (jsonObject.has("text")) {
            String text = jsonObject.optString("text");
            return StringUtils.contains(text, "@_all");
        }
        return false;
    }

    private String getAutoSubmitOpinionText(String content) {
        JSONObject jsonObject = new JSONObject(content);
        String text = StringUtils.trim(jsonObject.optString("text"));
        if (StringUtils.startsWith(text, "@_all")) {
            return StringUtils.trim(StringUtils.substringAfter(text, "@_all"));
        } else if (StringUtils.endsWith(text, "@_all")) {
            return StringUtils.trim(StringUtils.substringBefore(text, "@_all"));
        }
        return StringUtils.trim(StringUtils.replace(text, "@_all", StringUtils.EMPTY));
    }

}
