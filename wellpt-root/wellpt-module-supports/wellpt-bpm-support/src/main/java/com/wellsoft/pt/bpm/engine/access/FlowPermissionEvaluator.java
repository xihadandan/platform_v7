/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.security.acls.model.Permission;

import java.util.Collection;
import java.util.List;

/**
 * Description: 流程权限判断接口
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
public interface FlowPermissionEvaluator {
    /**
     * 判某用户对流程是否有参与者权限
     *
     * @param flowInstance
     * @param userIds
     * @param wfTodo
     */
    public boolean hasPermission(FlowDefinition flowDefinition, String taskId, Collection<String> userIds,
                                 Permission wfTodo);

    /**
     * 判断会话用户是否有指定的权限
     *
     * @param userDetails
     * @param objectIdIdentity
     * @param permission
     * @return
     */
    public boolean hasPermission(UserDetails userDetails, String objectIdIdentity, Permission permission);

    public boolean hasPermission(UserDetails userDetails, String objectIdIdentity, Integer[] masks);

    /**
     * 判断会话用户是否有任意一个权限
     *
     * @param userDetails
     * @param objectIdIdentity
     * @return
     */
    public boolean hasAnyPermission(UserDetails userDetails, String objectIdIdentity);

    /**
     * 获取流程数据鉴权接口返回的权限
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    List<Permission> getFlowAccessPermission(String taskInstUuid, String flowInstUuid, String flowDefUuid);

}
