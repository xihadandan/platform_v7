/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowAclSid;
import com.wellsoft.pt.bpm.engine.exception.IdentityNotFlowPermissionException;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 流程权限判断实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
@Component
public class DefaultFlowPermissionEvaluator implements FlowPermissionEvaluator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AclService aclService;


    @Autowired
    private AclTaskService aclTaskService;

    //    @Autowired
    //    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator#hasPermission(com.wellsoft.pt.bpm.engine.entity.FlowInstance, java.util.List, org.springframework.security.acls.model.Permission)
     */
    @Override
    public boolean hasPermission(FlowDefinition flowDefinition, String taskId, Collection<String> userIds,
                                 Permission wfTodo) {
        List<AclSid> aclSids = aclService.getSid(flowDefinition, wfTodo);
        for (AclSid aclSid : aclSids) {
            // 基于流程参与者群组所有参与权限的SID
            if (WorkFlowAclSid.ROLE_FLOW_ALL_USER.name().equals(aclSid.getSid())) {
                return true;
            }

            // 基于流程参与者群组的SID
            String flowUserSid = WorkFlowAclSid.GROUP_FLOW_USER.name() + flowDefinition.getUuid();
            if (flowUserSid.equals(aclSid.getSid())) {
                for (String member : userIds) {
                    if (!aclService.hasMember(flowUserSid, member, ModuleID.WORKFLOW.getValue())) {
                        Map<String, Object> variables = new HashMap<String, Object>();
                        variables.put("taskId", taskId);
                        if (member.startsWith(IdPrefix.USER.getValue())) {
                            String userName = workflowOrgService.getNameById(member);
                            variables.put("msg", "用户[" + userName + "]没有权限参与流程！");
                            throw new IdentityNotFlowPermissionException(variables);
                        } else {
                            variables.put("msg", "ID为[" + member + "]的用户没有权限参与流程！");
                            throw new IdentityNotFlowPermissionException(variables);
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator#hasPermission(com.wellsoft.pt.security.core.userdetails.UserDetails, java.lang.String, org.springframework.security.acls.model.Permission)
     */
    @Override
    public boolean hasPermission(UserDetails userDetails, String objectIdIdentity, Permission permission) {
        TaskInstance taskInstance = taskInstanceService.get(objectIdIdentity);
        if (taskInstance != null) {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            if (isOnlyUseAccessPermissionProvider(flowDelegate)) {
                List<Permission> permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (containsPermisson(permission, permissions)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                List<Permission> permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (containsPermisson(permission, permissions)) {
                    return true;
                }
            }
        }
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(permission);
        List<String> sids = PermissionGranularityUtils.getSids(userDetails);
        return aclTaskService.isGranted(objectIdIdentity, permissions, sids);
    }

    @Override
    public boolean hasPermission(UserDetails userDetails, String objectIdIdentity, Integer[] masks) {
        TaskInstance taskInstance = taskInstanceService.get(objectIdIdentity);
        if (taskInstance != null) {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            if (isOnlyUseAccessPermissionProvider(flowDelegate)) {
                List<Permission> permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (containsAnyPermisson(masks, permissions)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                List<Permission> permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (containsAnyPermisson(masks, permissions)) {
                    return true;
                }
            }
        }
        List<String> sids = PermissionGranularityUtils.getSids(userDetails);
        return aclTaskService.isGranted(objectIdIdentity, masks, sids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator#hasAnyPermission(com.wellsoft.pt.security.core.userdetails.UserDetails, java.lang.String)
     */
    @Override
    public boolean hasAnyPermission(UserDetails userDetails, String objectIdIdentity) {
        TaskInstance taskInstance = taskInstanceService.get(objectIdIdentity);
        if (taskInstance != null) {
            List<Permission> permissions = Lists.newArrayList();
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            if (isOnlyUseAccessPermissionProvider(flowDelegate)) {
                permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (CollectionUtils.isNotEmpty(permissions)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                permissions = getByFlowAccessPermissionProvider(taskInstance, flowDelegate);
                if (CollectionUtils.isNotEmpty(permissions)) {
                    return true;
                }
            }
            permissions = getRuntimeFlowAccessPermission(taskInstance, taskInstance.getFlowInstance().getUuid());
            if (CollectionUtils.isNotEmpty(permissions)) {
                return true;
            }
        }
        List<String> sids = PermissionGranularityUtils.getSids(userDetails);
        return aclTaskService.isGranted(objectIdIdentity, sids);
    }

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    @Override
    public List<Permission> getFlowAccessPermission(String taskInstUuid, String flowInstUuid, String flowDefUuid) {
        Set<Permission> permissions = Sets.newHashSet();
        TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
        if (taskInstance != null) {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
            permissions.addAll(getByFlowAccessPermissionProvider(taskInstance, flowDelegate));
            permissions.addAll(getRuntimeFlowAccessPermission(taskInstance, flowInstUuid));
        }
        return Lists.newArrayList(permissions);
    }

    /**
     * @param taskInstance
     * @param flowInstUuid
     * @return
     */
    private List<Permission> getRuntimeFlowAccessPermission(TaskInstance taskInstance, String flowInstUuid) {
        List<Permission> permissions = Lists.newArrayList();
        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, "flowAccessPermissionProvider");
        if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
            List<String> flowAccessPermissionProviders = Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue()));
            for (String flowAccessPermissionProvider : flowAccessPermissionProviders) {
                FlowAccessPermissionProvider accessPermissionProvider = ApplicationContextHolder.getBean(flowAccessPermissionProvider, FlowAccessPermissionProvider.class);
                if (accessPermissionProvider != null) {
                    permissions.addAll(accessPermissionProvider.provide(taskInstance, taskInstance.getFlowDefinition()));
                }
            }
        }
        return permissions;
    }

    /**
     * @param permission
     * @param permissions
     * @return
     */
    private boolean containsPermisson(Permission permission, List<Permission> permissions) {
        return permissions.contains(permission);
    }

    /**
     * @param masks
     * @param permissions
     * @return
     */
    private boolean containsAnyPermisson(Integer[] masks, List<Permission> permissions) {
        for (Integer mask : masks) {
            for (Permission permission : permissions) {
                if (mask.equals(permission.getMask())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param taskInstance
     * @param flowDelegate
     * @return
     */
    private List<Permission> getByFlowAccessPermissionProvider(TaskInstance taskInstance, FlowDelegate flowDelegate) {
        List<Permission> permissions = Lists.newArrayList();
        String accessPermissionProvider = flowDelegate.getFlow().getProperty().getAccessPermissionProvider();
        if (StringUtils.isNotBlank(accessPermissionProvider)) {
            List<String> accessPermissionProviders = Arrays.asList(StringUtils.split(accessPermissionProvider, Separator.SEMICOLON.getValue()));
            accessPermissionProviders.forEach(provider -> {
                try {
                    FlowAccessPermissionProvider flowAccessPermissionProvider = ApplicationContextHolder.getBean(provider, FlowAccessPermissionProvider.class);
                    List<Permission> aclPermissions = flowAccessPermissionProvider.provide(taskInstance,
                            taskInstance.getFlowDefinition());
                    if (CollectionUtils.isNotEmpty(aclPermissions)) {
                        permissions.addAll(aclPermissions);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });
        }
        return permissions;
    }

    /**
     * @param flowDelegate
     * @return
     */
    private boolean isOnlyUseAccessPermissionProvider(FlowDelegate flowDelegate) {
        PropertyElement propertyElement = flowDelegate.getFlow().getProperty();
        if (FlowPermissionEvaluatorContext.isEnableContextHolder()) {
            FlowPermissionEvaluatorContext.setOnlyUseAccessPermissionProvider(propertyElement.getIsOnlyUseAccessPermissionProvider());
        }
        return propertyElement.getIsOnlyUseAccessPermissionProvider();
    }

}
