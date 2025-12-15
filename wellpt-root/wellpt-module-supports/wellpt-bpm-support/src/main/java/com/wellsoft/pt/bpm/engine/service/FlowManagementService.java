/*
 * @(#)2015-4-9 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.entity.FlowManagement;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-9.1	zhulh		2015-4-9		Create
 * </pre>
 * @date 2015-4-9
 */
public interface FlowManagementService extends BaseService {

    /**
     * @param type
     * @param sids
     * @param flowDefUuid
     */
    void create(Integer type, Collection<String> orgIds, String flowDefUuid);

    void save(FlowManagement flowManagement);

    void remove(String flowDefUuid, Integer type);

    /**
     * @param type
     * @param orgIds
     * @param flowDefUuid
     */
    void remove(Integer type, List<String> orgIds, String flowDefUuid);

    /**
     * @param type
     * @param orgIds
     * @param flowDefId
     */
    void removeByFlowDefId(Integer type, List<String> orgIds, String flowDefId);

    /**
     * @param taskInstUuid
     * @param type
     * @param userId
     * @return
     */
    boolean hasPermission(String userId, String taskInstUuid, Integer type);

    /**
     * @param userId
     * @param taskInstUuid
     * @return
     */
    boolean hasPermission(String userId, String taskInstUuid);

    void upgrade(String flowDefUuid);

    /**
     * 如何描述该方法
     *
     * @param type
     * @param orgIds
     * @param flowDefUuid
     */
    void add(Integer type, List<String> orgIds, String flowDefUuid);

    /**
     * 如何描述该方法
     *
     * @param type
     * @param orgIds
     * @param flowDefId
     */
    void addByFlowDefId(Integer type, List<String> orgIds, String flowDefId);

    /**
     * @param userId
     * @param flowDefUuid
     * @return
     */
    List<Integer> listManagementPermission(String userId, String flowDefUuid);
}
