/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessItemInstanceDao;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import com.wellsoft.pt.timer.support.WorkTimePeriod;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizProcessItemInstanceService extends JpaService<BizProcessItemInstanceEntity, BizProcessItemInstanceDao, String> {
    /**
     * 根据业务事项定义UUID列表获取业务事项实例数量
     *
     * @param itemDefUuids
     * @return
     */
    Long countByItemDefUuids(List<String> itemDefUuids);


    /**
     * 根据过程节点UUID列表获取对应业务事项实例数量
     *
     * @param processNodeInstUuids
     * @return
     */
    Map<String, Long> getCountMapByProcessNodeInstUuids(List<String> processNodeInstUuids);

    /**
     * 业务事项保存为草稿
     *
     * @param itemDataDto
     * @param dyFormData
     * @return
     */
    String saveAsDraft(BizProcessItemDataDto itemDataDto, DyFormData dyFormData);

    /**
     * 更新事项实例
     *
     * @param uuid
     * @param dyFormData
     */
    void updateByUUid(String uuid, DyFormData dyFormData);

    /**
     * 完成事项办件
     *
     * @param uuid
     */
    void completeByUuid(String uuid);

    /**
     * 获取事项实例的工作时间段信息
     *
     * @return
     */
    WorkTimePeriod getWorkTimePeriodByUuid(String uuid);

    /**
     * 获取事项实例的计时器工作时间信息
     *
     * @param uuid
     * @return
     */
    TimerWorkTime getTimerWorkTimeByUuid(String uuid);

    /**
     * 根据过程节点实例UUID获取业务事项实例列表
     *
     * @param processNodeInstUuid
     * @return
     */
    List<BizProcessItemInstanceEntity> listByProcessNodeInstUuid(String processNodeInstUuid);

    /**
     * 根据所属事项实例UUID获取业务事项实例列表
     *
     * @param belongItemInstUuid
     * @return
     */
    List<BizProcessItemInstanceEntity> listByBelongItemInstUuid(String belongItemInstUuid);

    /**
     * 根据上级事项实例UUID获取业务事项实例列表
     *
     * @param parentItemInstUuid
     * @return
     */
    List<BizProcessItemInstanceEntity> listByParentItemInstUuid(String parentItemInstUuid);

    /**
     * 根据业务流程实例UUID列表获取业务事项实例列表
     *
     * @param processInstUuids
     * @return
     */
    List<BizProcessItemInstanceEntity> listByProcessInstUuids(List<String> processInstUuids);

    /**
     * 更新计时器数据
     *
     * @param itemInstanceEntity
     * @param tsTimerDto
     */
    void updateTimerData(BizProcessItemInstanceEntity itemInstanceEntity, TsTimerDto tsTimerDto);

//    /**
//     * 保存子事项实例为草稿
//     *
//     * @param parentItemInstanceEntity
//     * @param dispenseEntity
//     * @param dyFormData
//     * @param dispenseFormConfig
//     * @return
//     */
//    String saveSubItemAsDraft(BizProcessItemInstanceEntity parentItemInstanceEntity,
//                              BizProcessItemInstanceDispenseEntity dispenseEntity, DyFormData dyFormData, ProcessItemConfig.DispenseFormConfig dispenseFormConfig);

    /**
     * 根据事项编号、业务流程实例UUID，判断是否完成里程碑
     *
     * @param itemCode
     * @param processInstUuid
     * @return
     */
    boolean isCompleteMilestone(String itemCode, String processInstUuid);

    /**
     * 根据计时器UUID获取业务事项实例
     *
     * @param timerUuid
     * @return
     */
    BizProcessItemInstanceEntity getByTimerUuid(String timerUuid);

    /**
     * 根据业务流程定义ID、事项ID列表获取相应的事项办理状态
     *
     * @param processDefId
     * @param itemIds
     * @return
     */
    Map<String, String> listItemStatesByProcessDefIdAndItemIds(String processDefId, List<String> itemIds);

    /**
     * 根据事项ID列表、业务流程实例UUID，判断对应的事项是否都完成
     *
     * @param itemIds
     * @param processInstUuid
     * @return
     */
    boolean isCompleteByItemIdsAndProcessInstUuid(List<String> itemIds, String processInstUuid);

    /**
     * 根据事项ID、业务主体ID，获取最新的业务事项实例
     *
     * @param itemId
     * @param entityId
     * @return
     */
    BizProcessItemInstanceEntity getByItemIdAndEntityId(String itemId, String entityId);

    /**
     * 变更业务事项办件状态
     *
     * @param event
     * @param stateConfig
     * @param processItemFormConfig
     * @param processItemConfig
     * @param parser
     */
    void changeItemInstanceState(Event event, StateDefinition.StateConfig stateConfig,
                                 ProcessItemConfig.ProcessItemFormConfig processItemFormConfig,
                                 ProcessItemConfig processItemConfig, ProcessDefinitionJsonParser parser);

}
