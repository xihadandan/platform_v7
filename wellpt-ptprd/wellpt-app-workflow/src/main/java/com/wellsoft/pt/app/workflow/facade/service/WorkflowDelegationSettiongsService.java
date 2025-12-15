/*
 * @(#)Aug 4, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowDelegationSettingsDto;
import com.wellsoft.pt.bpm.engine.dto.WfCommonDelegationSettingDto;

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
 * Aug 4, 2017.1	zhulh		Aug 4, 2017		Create
 * </pre>
 * @date Aug 4, 2017
 */
public interface WorkflowDelegationSettiongsService extends BaseService {

    /**
     * @param uuid
     * @return
     */
    FlowDelegationSettingsDto getBean(String uuid);

    /**
     * @param uuid
     * @return
     */
    ResultMessage saveBean(FlowDelegationSettingsDto flowDelegationSettingsDto);

    /**
     * 从指定类型开始异步加载树形结点，维护时不需要考虑ACL权限
     *
     * @param id
     * @return
     */
    List<TreeNode> getContentAsTreeAsync(String uuid);

    /**
     * 激活
     *
     * @param uuids
     */
    void active(Collection<String> uuids);

    /**
     * 终止
     *
     * @param uuids
     */
    void deactive(Collection<String> uuids);

    /**
     * @param uuids
     */
    void deleteAll(Collection<String> uuids);

    /**
     * 委托生效
     *
     * @param uuid
     */
    void delegationActive(String uuid);

    /**
     * 委托拒绝
     *
     * @param uuid
     */
    void delegationRefuse(String uuid);

    /**
     * 保存常用委托设置
     *
     * @param commonDelegationSettingDto
     * @return
     */
    Long saveCommonBean(WfCommonDelegationSettingDto commonDelegationSettingDto);

    /**
     * 根据用户ID获取常用工作委托设置
     *
     * @param userId
     * @param pagingInfo
     * @return
     */
    List<WfCommonDelegationSettingDto> listCommonByUserId(String userId, PagingInfo pagingInfo);

    /**
     * 根据常用工作委托设置UUID删除常用工作委托设置
     *
     * @param uuid
     */
    void deleteCommon(Long uuid);
}
