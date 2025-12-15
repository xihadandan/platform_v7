/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.dao.TaskIdentityDao;
import com.wellsoft.pt.bpm.engine.delegation.DelegationExecutor;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
@Service
public class IdentityServiceImpl extends AbstractJpaServiceImpl<TaskIdentity, TaskIdentityDao, String>
        implements IdentityService {
    private static final String REMOVE_BY_TASK_INST_UUID = "update TaskIdentity t set t.suspensionState = 2, t.modifier = :modifier, t.modifyTime = :modifyTime where t.taskInstUuid = :taskInstUuid and t.suspensionState <> 2";

    private static final String GET_TODO_BY_TASK_INST_UUID = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.suspensionState = 0 order by sortOrder asc";

    private static final String GET_TODO_BY_FLOW_INST_UUID = "from TaskIdentity t where exists (select 1 from TaskInstance tt where tt.uuid = t.taskInstUuid and tt.flowInstance.uuid = :flowInstUuid) and t.suspensionState = 0 order by sortOrder asc";

    private static final String GET_TODO_BY_ORDERS_BY_TASK_INST_UUID = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.suspensionState = 0 and sortOrder is not null order by sortOrder asc";

    private static final String REMOVE_BY_TASK_INST_UUID_AND_USER_ID = "update TaskIdentity t set t.suspensionState = 2, t.modifier = :modifier, t.modifyTime = :modifyTime where t.taskInstUuid = :taskInstUuid and t.userId = :userId and t.suspensionState <> 2";

    private static final String REMOVE_WITHOUT_SORT_ORDER_BY_TASK_INST_UUID = "update TaskIdentity t set t.suspensionState = 2, t.modifier = :modifier, t.modifyTime = :modifyTime where t.taskInstUuid = :taskInstUuid and t.sortOrder is null and t.sourceTaskIdentityUuid is not null and t.suspensionState <> 2";

    private static final String GET_TODO_USERS = "select t.userId from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.suspensionState = 0";

    private static final String GET_IDENTITY_USERS = "select t.userId from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.suspensionState = 0";

    // private static final String COUNT_TASK_IDENTITY =
    // "select count(*) from TaskIdentity t where t.sourceTaskIdentityUuid =
    // :sourceTaskIdentityUuid and t.suspensionState = 0";

    private static final String COUNT_LAST_TASK_IDENTITY = "select count(*) from TaskIdentity t where t.sourceTaskIdentityUuid = :sourceTaskIdentityUuid and t.suspensionState in (0, 1)";

    private static final String GET_TODO_BY_TASK_INST_UUID_AND_USER_ID = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.userId = :userId and t.suspensionState = 0";

    private static final String GET_TODO_BY_TASK_INST_UUID_AND_SIDS = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.userId in (:sids) and t.suspensionState = 0 order by t.todoType desc";

    private static final String GET_TODO_TYPE_FOR_SUSPENSION = "select distinct t.todoType from TaskIdentity t where t.sourceTaskIdentityUuid = :sourceTaskIdentityUuid and t.todoType in (2, 3, 5)";

    private static final String GET_TODO_SUBMIT_BY_USER_IDS = "from TaskIdentity t where t.userId in :userIds and t.suspensionState = 0";

    private static final String GET_TODO_SUBMIT_TASK_INST_UUID_BY_USER_ID = "select t.taskInstUuid from TaskIdentity t where t.userId = :userId and t.suspensionState = 0";

    private static final String COUNT_AVAILABLE_BY_TASK_INST_UUID_AND_USER_ID = "select count(*) from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.userId = :userId and t.suspensionState in (0, 1)";

    // private static final String GET_BY_SOURCE_TASK_IDENTITY_UUID =
    // "from TaskIdentity t where t.sourceTaskIdentityUuid = :sourceTaskIdentityUuid
    // and t.suspensionState = 0";

    private static final String GET_BY_FLOW_INST_UUID = "from TaskIdentity t1 where exists (select uuid from TaskInstance t2 where t1.taskInstUuid = t2.uuid and t2.flowInstance.uuid = :flowInstUuid)";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskIdentity t1 where exists (select uuid from TaskInstance t2 where t1.taskInstUuid = t2.uuid and t2.flowInstance.uuid = :flowInstUuid)";

    private static final String GET_BY_TASK_INST_UUID = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid";

    private static final String GET_BY_TASK_INST_UUID_AND_OWNER_ID = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.ownerId = :ownerId";

    private static final String GET_BY_TASK_INST_UUID_AND_SUSPENSION_STATE = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.suspensionState = :suspensionState";

    private static final String GET_BY_TASK_INST_UUID_AND_USER_ID_AND_TODO_TYPE = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.userId = :userId and t.todoType = :todoType and t.suspensionState = :suspensionState";

    private static final String GET_BY_USER_ID_AND_TODO_TYPE = "from TaskIdentity t where t.userId = :userId and t.todoType = :todoType";

    @Autowired
    private AclTaskService aclTaskService;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private DelegationExecutor delegationExecutor;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#get(java.lang.String)
     */
    @Override
    public TaskIdentity get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#get(java.util.Collection)
     */
    @Override
    public List<TaskIdentity> get(Collection<String> uuids) {
        return listByUuids(Lists.newArrayList(uuids));
    }

    /**
     * 挂起TaskIdentity，不移除待办权限
     *
     * @param taskIdentity taskIdentity
     */
    @Override
    @Transactional
    public void suspenseTodoWithNotRemoveTodoPermission(TaskIdentity taskIdentity) {
        taskIdentity.setSuspensionState(SuspensionState.SUSPEND.getState());
        save(taskIdentity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#suspenseTodo(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void suspenseTodo(TaskIdentity taskIdentity) {
        taskIdentity.setSuspensionState(SuspensionState.SUSPEND.getState());
        save(taskIdentity);
        if (IdPrefix.startsUser(taskIdentity.getUserId())) {
            aclTaskService.removePermission(taskIdentity.getTaskInstUuid(), AclPermission.TODO,
                    taskIdentity.getUserId());
        }
        this.taskInstanceService.flushSession();
        this.taskInstanceService.clearSession();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#addTodo(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void addTodo(TaskIdentity identity) {
        this.dao.save(identity);
        aclTaskService.addPermission(identity.getTaskInstUuid(), AclPermission.TODO,
                identity.getUserId());
    }

    @Override
    @Transactional
    public void addTodo(TaskIdentity identity, String doneUserId) {
        this.dao.save(identity);
        aclTaskService.addPermission(identity.getTaskInstUuid(), AclPermission.TODO, identity.getUserId(), doneUserId);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void removeTodo(TaskIdentity identity) {
        identity.setSuspensionState(SuspensionState.DELETED.getState());
        this.dao.save(identity);
        // 发送已办事件
        String taskInstUuid = identity.getTaskInstUuid();
//        String flowInstUUid = taskInstanceService.getFlowInstUUidByTaskInstUuid(taskInstUuid);
//        ApplicationContextHolder
//                .publishEvent(new WorkDoneEvent(this, identity.getUserId(), flowInstUUid, taskInstUuid));
        aclTaskService.removePermission(taskInstUuid, AclPermission.TODO, identity.getUserId());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void removeTodoByTaskInstUuid(String taskInstUuid) {
        List<TaskIdentity> taskIdentities = getTodoByTaskInstUuid(taskInstUuid);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("modifier", SpringSecurityUtils.getCurrentUserId());
        values.put("modifyTime", Calendar.getInstance().getTime());
        this.dao.updateByHQL(REMOVE_BY_TASK_INST_UUID, values);

        // 删除所有待办权限
        aclTaskService.removePermission(taskInstUuid, AclPermission.TODO);
//        List<AclSid> getAclSid = aclService.getSid(TaskInstance.class, taskInstUuid, AclPermission.TODO);
//        for (AclSid aclSid : getAclSid) {
//            aclService.removePermission(taskInstUuid, AclPermission.TODO, aclSid.getSid());
//        }
//        // 发送已办事件
//        String flowInstUUid = taskInstanceService.getFlowInstUUidByTaskInstUuid(taskInstUuid);
//        for (TaskIdentity identity : taskIdentities) {
//            ApplicationContextHolder
//                    .publishEvent(new WorkDoneEvent(this, identity.getUserId(), flowInstUUid, taskInstUuid));
//        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#removeTodoByTaskInstUuidAndUserId(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeTodoByTaskInstUuidAndUserId(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        values.put("modifier", SpringSecurityUtils.getCurrentUserId());
        values.put("modifyTime", Calendar.getInstance().getTime());
        this.dao.updateByHQL(REMOVE_BY_TASK_INST_UUID_AND_USER_ID, values);
//        String flowInstUUid = taskInstanceService.getFlowInstUUidByTaskInstUuid(taskInstUuid);
//        ApplicationContextHolder.publishEvent(new WorkDoneEvent(this, userId, flowInstUUid, taskInstUuid));
        aclTaskService.removePermission(taskInstUuid, AclPermission.TODO, userId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#removeTodoWithoutSortOrderByTaskInstUuid(java.lang.String)
     */
    @Override
    public void removeTodoWithoutSortOrderByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskInstUuid", taskInstUuid);
        values.put("modifier", SpringSecurityUtils.getCurrentUserId());
        values.put("modifyTime", Calendar.getInstance().getTime());
        this.dao.updateByHQL(REMOVE_WITHOUT_SORT_ORDER_BY_TASK_INST_UUID, values);
    }

    public List<TaskIdentity> getTodoByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_TODO_BY_FLOW_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskIdentity> getTodoByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listByHQL(GET_TODO_BY_TASK_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getByOrdersByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskIdentity> getByOrdersByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listByHQL(GET_TODO_BY_ORDERS_BY_TASK_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoUserIds(java.lang.String)
     */
    @Override
    public List<String> getTodoUserIds(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listCharSequenceByHQL(GET_IDENTITY_USERS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restore(java.util.List)
     */
    @Override
    @Transactional
    public void restore(List<TaskIdentity> taskIdentities) {
        Set<String> taskInstUuids = new HashSet<String>();
        for (TaskIdentity taskIdentity : taskIdentities) {
            TaskIdentity identity = this.dao.getOne(taskIdentity.getUuid());
            identity.setSuspensionState(taskIdentity.getSuspensionState());
            this.dao.save(identity);

            // 待办还原待办人员
            if (Integer.valueOf(SuspensionState.NORMAL.getState()).equals(taskIdentity.getSuspensionState())) {
                String taskInstUuid = identity.getTaskInstUuid();
                String todoUserId = identity.getUserId();
                // 添加权限
                aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
                // 标记未阅
                readMarkerService.markNew(taskInstUuid, todoUserId);

                taskInstUuids.add(taskInstUuid);
            }
        }

        // 更新环节的待办人员列表
        for (String taskInstUuid : taskInstUuids) {
            updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreTodoSumit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreTodoSumit(TaskIdentity historyTaskIdentity) {
        // 主流程提交到子流程指定环节，不对允许撤回
        if (historyTaskIdentity == null) {
            return false;
        }

        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 任务办理所有者ID
        example.setOwnerId(historyTaskIdentity.getOwnerId());
        // 源待办UUID
        example.setSourceTaskIdentityUuid(historyTaskIdentity.getSourceTaskIdentityUuid());
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreTodoSumit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreTodoSumit(TaskIdentity historyTaskIdentity) {
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        if (IdPrefix.startsUser(todoUserId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        } else if (aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, todoUserId)) {
            aclTaskService.removeUserDoneMarker(taskInstUuid, SpringSecurityUtils.getCurrentUserId());
        } else {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        }
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreTodoCounterSignSubmit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreTodoCounterSignSubmit(TaskIdentity historyTaskIdentity) {
        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 任务办理所有者ID
        example.setOwnerId(historyTaskIdentity.getOwnerId());
        // 源待办UUID
        example.setSourceTaskIdentityUuid(historyTaskIdentity.getSourceTaskIdentityUuid());
        if (this.dao.countByEntity(example) > 0) {
            return true;
        }

        // 会签返回后，办理人还没办理可撤回
        if (StringUtils.isNotBlank(historyTaskIdentity.getSourceTaskIdentityUuid())) {
            TaskIdentity taskIdentity = this.dao.getOne(historyTaskIdentity.getSourceTaskIdentityUuid());
            return taskIdentity.getSuspensionState().equals(SuspensionState.NORMAL.getState());
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreTodoCounterSignSubmit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreTodoCounterSignSubmit(TaskIdentity historyTaskIdentity) {
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        if (IdPrefix.startsUser(todoUserId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        } else if (aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, todoUserId)) {
            aclTaskService.removeUserDoneMarker(taskInstUuid, SpringSecurityUtils.getCurrentUserId());
        } else {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        }
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 挂起会签发起人
        TaskIdentity sourceIdentity = this.dao.getOne(historyTaskIdentity.getSourceTaskIdentityUuid());
        sourceIdentity.setSuspensionState(SuspensionState.SUSPEND.getState());
        this.dao.save(sourceIdentity);
        String sourceTaskInstUuid = sourceIdentity.getTaskInstUuid();
        String sourceTodoUserId = sourceIdentity.getUserId();
        // 添加权限
        aclTaskService.removePermission(sourceTaskInstUuid, AclPermission.TODO, sourceTodoUserId);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreTodoTransferSubmit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreTodoTransferSubmit(TaskIdentity historyTaskIdentity) {
        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 任务办理所有者ID
        example.setOwnerId(historyTaskIdentity.getOwnerId());
        // 源待办UUID
        example.setSourceTaskIdentityUuid(historyTaskIdentity.getSourceTaskIdentityUuid());
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreTodoTransferSubmit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreTodoTransferSubmit(TaskIdentity historyTaskIdentity) {
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        if (IdPrefix.startsUser(todoUserId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        } else if (aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, todoUserId)) {
            aclTaskService.removeUserDoneMarker(taskInstUuid, SpringSecurityUtils.getCurrentUserId());
        } else {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        }
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreTodoDelegationSubmit(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreTodoDelegationSubmit(TaskIdentity historyTaskIdentity) {
        if (historyTaskIdentity == null) {
            return false;
        }

        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 任务挂起状态
        example.setSuspensionState(SuspensionState.NORMAL.getState());
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreTodoDelegationSubmit(java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreTodoDelegationSubmit(String userId, TaskIdentity historyTaskIdentity) {
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        // 当委托人撤回受托人的办理时，更新userId为委托人本身
        identity.setUserId(userId);
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        if (IdPrefix.startsUser(todoUserId)) {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        } else if (aclTaskService.hasPermission(taskInstUuid, AclPermission.TODO, todoUserId)) {
            aclTaskService.removeUserDoneMarker(taskInstUuid, SpringSecurityUtils.getCurrentUserId());
        } else {
            aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        }
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreTransfer(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreTransfer(TaskIdentity historyTaskIdentity) {
        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 源待办UUID
        example.setSourceTaskIdentityUuid(historyTaskIdentity.getUuid());
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreTransfer(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreTransfer(TaskIdentity historyTaskIdentity) {
        // 恢复原待办人员
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 删除转办出去的人员
        deleteSubTaskIdentity(identity, WorkFlowTodoType.Transfer);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreDelegation(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreDelegation(TaskIdentity sourceTaskIdentity) {
        // 恢复原待办人员
        TaskIdentity identity = this.dao.getOne(sourceTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isAllowedRestoreCounterSign(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    public boolean isAllowedRestoreCounterSign(TaskIdentity historyTaskIdentity) {
        TaskIdentity example = new TaskIdentity();
        // 任务实例UUID
        example.setTaskInstUuid(historyTaskIdentity.getTaskInstUuid());
        // 源待办UUID
        example.setSourceTaskIdentityUuid(historyTaskIdentity.getUuid());
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#restoreCounterSign(com.wellsoft.pt.bpm.engine.entity.TaskIdentity)
     */
    @Override
    @Transactional
    public void restoreCounterSign(TaskIdentity historyTaskIdentity) {
        // 恢复原待办人员
        TaskIdentity identity = this.dao.getOne(historyTaskIdentity.getUuid());
        identity.setSuspensionState(SuspensionState.NORMAL.getState());
        this.dao.save(identity);

        String taskInstUuid = identity.getTaskInstUuid();
        String todoUserId = identity.getUserId();
        // 添加权限
        aclTaskService.addPermission(taskInstUuid, AclPermission.TODO, todoUserId);
        // 标记未阅
        readMarkerService.markNew(taskInstUuid, todoUserId);

        // 删除会签出去的人员
        deleteSubTaskIdentity(identity, WorkFlowTodoType.CounterSign);

        // 更新环节的待办人员列表
        updateTaskIdentity(this.taskInstanceService.getOne(taskInstUuid));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#updateTaskIdentity(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    @Transactional
    public void updateTaskIdentity(TaskInstance taskInstance) {
        this.updateTaskIdentity(taskInstance, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#updateTaskIdentity(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public void updateTaskIdentity(TaskInstance taskInstance, TaskData taskData) {
        String taskInstUuid = taskInstance.getUuid();
        Set<String> todoUserIdSet = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
        for (AclTaskEntry aclSid : aclSids) {
            todoUserIdSet.add(aclSid.getSid());
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        List<String> todoUserSids = this.dao.listCharSequenceByHQL(GET_TODO_USERS, values);
        Set<String> todoUserIds = Sets.newLinkedHashSet();
        Set<String> todoUserNames = Sets.newLinkedHashSet();
        for (String todoUserSid : todoUserSids) {
            if (!todoUserIdSet.contains(todoUserSid)) {
                continue;
            }
            String todoUserName = IdentityResolverStrategy.resolveAsName(todoUserSid);
            if (StringUtils.isBlank(todoUserName)) {
                todoUserName = todoUserSid;
            }
            todoUserIds.add(todoUserSid);
            todoUserNames.add(todoUserName);
        }
        taskInstance.setTodoUserId(StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()));
        taskInstance.setTodoUserName(StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue()));

        // 同步表单映射字段值
        List<String> mappingFieldNames = new ArrayList<String>();
        List<Object> mappingFieldValues = new ArrayList<Object>();
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_ID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_NAME.getValue());
        mappingFieldValues.add(taskInstance.getTodoUserId());
        mappingFieldValues.add(taskInstance.getTodoUserName());
        syncTaskFieldMapping(mappingFieldNames, mappingFieldValues, taskInstance, taskData);
        this.taskInstanceService.flushSession();
        this.taskInstanceService.clearSession();
        this.taskInstanceService.save(taskInstance);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#updateTaskIdentityFromPermission(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    @Transactional
    public void updateTaskIdentityFromPermission(TaskInstance taskInstance, TaskData taskData) {
        String taskInstUuid = taskInstance.getUuid();
        Set<String> todoUserIdSet = Sets.newLinkedHashSet();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
        for (AclTaskEntry aclSid : aclSids) {
            todoUserIdSet.add(aclSid.getSid());
        }
        Set<String> todoUserIds = Sets.newLinkedHashSet();
        Set<String> todoUserNames = Sets.newLinkedHashSet();
        for (AclTaskEntry aclSid : aclSids) {
            String todoUserSid = aclSid.getSid();
            String todoUserName = IdentityResolverStrategy.resolveAsName(todoUserSid);
            if (StringUtils.isBlank(todoUserName)) {
                todoUserName = todoUserSid;
            }
            todoUserIds.add(todoUserSid);
            todoUserNames.add(todoUserName);
        }
        taskInstance.setTodoUserId(StringUtils.join(todoUserIds, Separator.SEMICOLON.getValue()));
        taskInstance.setTodoUserName(StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue()));

        // 同步表单映射字段值
        List<String> mappingFieldNames = new ArrayList<String>();
        List<Object> mappingFieldValues = new ArrayList<Object>();
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_ID.getValue());
        mappingFieldNames.add(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_NAME.getValue());
        mappingFieldValues.add(taskInstance.getTodoUserId());
        mappingFieldValues.add(taskInstance.getTodoUserName());
        syncTaskFieldMapping(mappingFieldNames, mappingFieldValues, taskInstance, taskData);
        this.taskInstanceService.flushSession();
        this.taskInstanceService.clearSession();
        this.taskInstanceService.save(taskInstance);
    }

    /**
     * 如何描述该方法
     *
     * @param mappingNames
     * @param mappingValues
     * @param taskInstance
     */
    private void syncTaskFieldMapping(List<String> mappingNames, List<Object> mappingValues, TaskInstance taskInstance,
                                      TaskData taskData) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        DyFormData dyFormData = null;
        if (taskData != null) {
            dyFormData = taskData.getDyFormData(dataUuid);
        }
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        // 同步表单流程号映射字段值
        boolean hasUpdateMappingField = false;
        for (int index = 0; index < mappingNames.size(); index++) {
            String mappingName = mappingNames.get(index);
            Object mappingValue = mappingValues.get(index);
            if (dyFormData != null && dyFormData.hasFieldMappingName(mappingName)) {
                dyFormData.setFieldValueByMappingName(mappingName, mappingValue);
                hasUpdateMappingField = true;
            }
        }
        if (hasUpdateMappingField) {
            if (taskData == null) {
                dyFormFacade.saveFormData(dyFormData);
            } else {
                taskData.setDyFormData(dataUuid, dyFormData);
                taskData.addUpdatedDyFormData(dataUuid, dyFormData);
            }
        }
    }

    /**
     * @param taskIdentity
     */
    private void deleteSubTaskIdentity(TaskIdentity taskIdentity, Integer todoType) {
        TaskIdentity example = new TaskIdentity();
        example.setSourceTaskIdentityUuid(taskIdentity.getUuid());
        List<TaskIdentity> toDeleteTaskIdentities = this.dao.listByEntity(example);

        for (TaskIdentity toDeleteTaskIdentity : toDeleteTaskIdentities) {
            // 忽略非同类型操作的子操作
            if (!todoType.equals(toDeleteTaskIdentity.getTodoType())) {
                // 委托子操作
                if (WorkFlowTodoType.Delegation.equals(toDeleteTaskIdentity.getTodoType())) {
                    delegationExecutor.cancelDelegationByTaskIdentityUuid(toDeleteTaskIdentity.getUuid());
                } else {
                    continue;
                }
            }
            deleteSubTaskIdentity(toDeleteTaskIdentity, todoType);

            toDeleteTaskIdentity.setSuspensionState(SuspensionState.DELETED.getState());
            this.dao.save(toDeleteTaskIdentity);

            String taskInstUuid = toDeleteTaskIdentity.getTaskInstUuid();
            String toDeleteUserId = toDeleteTaskIdentity.getUserId();
            // 删除待办权限
            aclTaskService.removePermission(taskInstUuid, AclPermission.TODO, toDeleteUserId);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#isLastTodoTaskIdentityBySourceTaskIdentityUuid(java.lang.String)
     */
    @Override
    public boolean isLastTodoTaskIdentityBySourceTaskIdentityUuid(String sourceTaskIdentityUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("sourceTaskIdentityUuid", sourceTaskIdentityUuid);
        Long count = this.dao.getNumberByHQL(COUNT_LAST_TASK_IDENTITY, values);
        return count <= 1;
    }

    @Override
    public boolean isLastTodoTaskIdentityBySourceTaskIdentityUuid(String sourceTaskIdentityUuid, String creator, Integer todoType) {
        String hql = COUNT_LAST_TASK_IDENTITY + " and t.creator = :creator and t.todoType = :todoType";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("sourceTaskIdentityUuid", sourceTaskIdentityUuid);
        values.put("creator", creator);
        values.put("todoType", todoType);
        Long count = this.dao.getNumberByHQL(hql, values);
        return count <= 1;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoByTaskInstUuidAndUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskIdentity> getTodoByTaskInstUuidAndUserId(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        return this.dao.listByHQL(GET_TODO_BY_TASK_INST_UUID_AND_USER_ID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoByTaskInstUuidAndUserDetails(java.lang.String, com.wellsoft.pt.security.core.userdetails.UserDetails)
     */
    @Override
    public List<TaskIdentity> getTodoByTaskInstUuidAndUserDetails(String taskInstUuid, UserDetails userDetails) {
        List<String> sids = PermissionGranularityUtils.getSids(userDetails);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("sids", sids);
        return this.dao.listByHQL(GET_TODO_BY_TASK_INST_UUID_AND_SIDS, values);
    }

    @Override
    public List<TaskIdentity> getTodoByTaskInstUuidAndUserIds(String taskInstUuid, List<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("sids", userIds);
        return this.dao.listByHQL(GET_TODO_BY_TASK_INST_UUID_AND_SIDS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoTypeForSuspension(java.lang.String)
     */
    @Override
    public Integer getTodoTypeForSuspension(String sourceTaskIdentityUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("sourceTaskIdentityUuid", sourceTaskIdentityUuid);
        return this.dao.getNumberByHQL(GET_TODO_TYPE_FOR_SUSPENSION, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoSubmitByUserId(java.lang.String)
     */
    @Override
    public List<TaskIdentity> getTodoSubmitByUserSids(List<String> sids) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", sids);
        return this.dao.listByHQL(GET_TODO_SUBMIT_BY_USER_IDS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#listTodoSubmitTaskInstUuidByUserId(java.lang.String)
     */
    @Override
    public List<String> listTodoSubmitTaskInstUuidByUserId(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        return this.dao.listCharSequenceByHQL(GET_TODO_SUBMIT_TASK_INST_UUID_BY_USER_ID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#countAvailableByTaskInstUuidAndUserId(java.lang.String, java.lang.String)
     */
    @Override
    public Long countAvailableByTaskInstUuidAndUserId(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        return this.dao.getNumberByHQL(COUNT_AVAILABLE_BY_TASK_INST_UUID_AND_USER_ID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskIdentity> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    @Override
    public List<TaskIdentity> getByTaskInstUuidAndUserIds(String taskInstUuid, List<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userIds", userIds);
        String hql = GET_BY_TASK_INST_UUID + " and t.userId in (:userIds)";
        return this.dao.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskIdentity> getByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.listByHQL(GET_BY_TASK_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getByTaskInstUuidAndOwnerId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskIdentity> getByTaskInstUuidAndOwnerId(String taskInstUuid, String ownerId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("ownerId", ownerId);
        return this.dao.listByHQL(GET_BY_TASK_INST_UUID_AND_OWNER_ID, values);
    }

    /**
     * @param taskInstUuid
     * @param suspensionState
     * @return
     */
    @Override
    public List<TaskIdentity> getByTaskInstUuidAndSuspensionState(String taskInstUuid, int suspensionState) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("suspensionState", suspensionState);
        return this.dao.listByHQL(GET_BY_TASK_INST_UUID_AND_SUSPENSION_STATE, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.IdentityService#getTodoByTaskInstUuidAndUserIdAndTodoType(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public List<TaskIdentity> getTodoByTaskInstUuidAndUserIdAndTodoType(String taskInstUuid, String userId,
                                                                        Integer todoType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        values.put("todoType", todoType);
        values.put("suspensionState", SuspensionState.NORMAL.getState());
        return this.dao.listByHQL(GET_BY_TASK_INST_UUID_AND_USER_ID_AND_TODO_TYPE, values);
    }

    @Override
    public List<TaskIdentity> getTodoByTaskInstUuidAndTodoType(String taskInstUuid, Integer todoType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("todoType", todoType);
        values.put("suspensionState", SuspensionState.NORMAL.getState());
        String hql = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.todoType = :todoType and t.suspensionState = :suspensionState";
        return this.dao.listByHQL(hql, values);
    }

    @Override
    public List<TaskIdentity> getTodoByUserIdAndTodoType(String userId, Integer todoType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("todoType", todoType);
        return this.dao.listByHQL(GET_BY_USER_ID_AND_TODO_TYPE, values);
    }

    @Override
    public List<TaskIdentity> listBySourceTaskIdentityUuid(String sourceTaskIdentityUuid) {
        String hql = "from TaskIdentity t where t.sourceTaskIdentityUuid = :sourceTaskIdentityUuid";
        Map<String, Object> values = new HashMap<>();
        values.put("sourceTaskIdentityUuid", sourceTaskIdentityUuid);
        return this.dao.listByHQL(hql, values);
    }

    @Override
    public List<TaskIdentity> listBySourceTaskIdentityUuidsAndTodoType(List<String> sourceTaskIdentityUuids, Integer todoType) {
        String hql = "from TaskIdentity t where t.sourceTaskIdentityUuid in :sourceTaskIdentityUuids and t.todoType = :todoType";
        Map<String, Object> values = new HashMap<>();
        values.put("sourceTaskIdentityUuids", sourceTaskIdentityUuids);
        values.put("todoType", todoType);
        return this.dao.listByHQL(hql, values);
    }

    @Override
    public Long countOrderByTaskInstUuid(String taskInstUuid) {
        String hql = "from TaskIdentity t where t.taskInstUuid = :taskInstUuid and t.sortOrder is not null";
        Map<String, Object> values = new HashMap<>();
        values.put("taskInstUuid", taskInstUuid);
        return this.dao.countByHQL(hql, values);
    }

    @Override
    @Transactional
    public void updateOverdueStateByTaskInstUuid(Integer overdueState, String taskInstUuid) {
        String hql = "update TaskIdentity t set t.overdueState = :overdueState where t.taskInstUuid = :taskInstUuid and t.suspensionState = :suspensionState";
        Map<String, Object> values = new HashMap<>();
        values.put("overdueState", overdueState);
        values.put("taskInstUuid", taskInstUuid);
        values.put("suspensionState", SuspensionState.NORMAL.getState());
        this.dao.updateByHQL(hql, values);
        this.dao.flushSession();
    }

}
