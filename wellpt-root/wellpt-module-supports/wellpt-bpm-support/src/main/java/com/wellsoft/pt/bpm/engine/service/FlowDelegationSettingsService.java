/*
 * @(#)2018年8月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.FlowDelegationSettingsDao;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2018年8月14日.1	zhulh		2018年8月14日		Create
 * </pre>
 * @date 2018年8月14日
 */
public interface FlowDelegationSettingsService
        extends JpaService<FlowDelegationSettings, FlowDelegationSettingsDao, String> {

    /**
     * 根据委托人ID列表获取委托设置
     *
     * @param userIds
     * @return
     */
    List<FlowDelegationSettings> getByUserIds(List<String> userIds);

    /**
     * 根据委托人ID列表、归属系统，获取委托设置
     *
     * @param userIds
     * @param system
     * @return
     */
    List<FlowDelegationSettings> getByUserIdsAndSystem(List<String> userIds, String system);

    /**
     * 根据委托人ID列表获取委托设置(没有委托终止的数据)
     *
     * @param userIds
     * @return
     */
    List<FlowDelegationSettings> getNotDeactiveByUserIds(List<String> userIds);

    /**
     * 委托终止
     *
     * @param dutyAgentUuid
     */
    void deactive(String uuid);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<FlowDelegationSettings> getOverdueSettings();

    /**
     * 如何描述该方法
     *
     * @param delegationSettings
     * @return
     */
    List<FlowDelegationSettings> getForCheckTheSameDelegationSettings(FlowDelegationSettings delegationSettings);

}
