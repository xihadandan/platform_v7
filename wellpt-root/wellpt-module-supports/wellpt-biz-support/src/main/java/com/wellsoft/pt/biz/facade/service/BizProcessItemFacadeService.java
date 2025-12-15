/*
 * @(#)10/13/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.dto.BizProcessItemDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessItemOperationDto;
import com.wellsoft.pt.biz.dto.BizWorkflowProcessItemDataDto;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
import com.wellsoft.pt.biz.support.ItemMaterial;
import com.wellsoft.pt.biz.support.ItemTimeLimit;
import com.wellsoft.pt.biz.support.WorkflowIntegrationParams;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

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
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
public interface BizProcessItemFacadeService extends Facade {

    /**
     * 创建事项办件实例数据
     *
     * @param processDefId
     * @param processItemIds
     * @return
     */
    BizProcessItemDataDto newItemById(String processDefId, String processItemIds);


    /**
     * 根据业务事项实例UUID获取事项办件实例数据
     *
     * @param itemInstUuid
     * @return
     */
    BizProcessItemDataDto getItemByUuid(String itemInstUuid);

    /**
     * 获取业务事项实例数据
     *
     * @param itemDataRequestParamDto
     * @return
     */
    BizProcessItemDataDto getItemData(BizProcessItemDataRequestParamDto itemDataRequestParamDto);

    /**
     * 获取流程集成工作数据
     *
     * @param processDefId
     * @param processItemIds
     * @param workBean
     * @return
     */
    WorkBean getWorkData(String processDefId, String processItemIds, WorkBean workBean);

    /**
     * 获取流程业务集成实例
     *
     * @param itemInstUuid
     * @return
     */
    BizBusinessIntegrationEntity getWorkflowBusinessIntegrationByItemInstUuid(String itemInstUuid);
    
    /**
     * 保存业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    String save(BizProcessItemDataDto itemDataDto);

    /**
     * 提交业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    String submit(BizProcessItemDataDto itemDataDto);

    /**
     * 发起业务事项
     *
     * @param itemDataDto
     * @param workflowIntegrationParams
     */
    String startItemInstance(BizProcessItemDataDto itemDataDto, WorkflowIntegrationParams workflowIntegrationParams);

    /**
     * 从流程单据提交业务事项实例数据
     *
     * @param flowInstUuid
     * @param itemDataDto
     * @return
     */
    SubmitResult startFromWorkflow(String flowInstUuid, BizWorkflowProcessItemDataDto itemDataDto);

    /**
     * 启动计时器
     *
     * @param itemDataDto
     * @return
     */
    String startTimer(BizProcessItemDataDto itemDataDto);

    /**
     * 暂停计时器
     *
     * @param itemDataDto
     * @return
     */
    String pauseTimer(BizProcessItemDataDto itemDataDto);

    /**
     * 恢复计时器
     *
     * @param itemDataDto
     * @return
     */
    String resumeTimer(BizProcessItemDataDto itemDataDto);

    /**
     * 挂起业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    String suspend(BizProcessItemDataDto itemDataDto);

    /**
     * 恢复业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    String resume(BizProcessItemDataDto itemDataDto);

    /**
     * 撤销业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    String cancel(BizProcessItemDataDto itemDataDto);

    /**
     * 根据流程数据，撤销业务事项实例
     *
     * @param workData
     * @return
     */
    String cancelByWorkData(WorkBean workData);

    /**
     * 根据流程数据，撤销发起的业务事项实例
     *
     * @param workData
     * @return
     */
    String cancelOtherByWorkData(WorkBean workData);

    /**
     * 根据业务事项实例UUID，重新开始办结的业务事项实例
     *
     * @param itemInstUuid
     * @param newItemRelationEntity
     * @return
     */
    void restartByItemInstUuid(String itemInstUuid, BizNewItemRelationEntity newItemRelationEntity);

    /**
     * 完成业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    String complete(BizProcessItemDataDto itemDataDto);

    /**
     * 根据业务流程定义ID、事项ID列表获取相应的事项办理状态
     *
     * @param processDefId
     * @param itemIds
     * @return
     */
    Map<String, String> listItemStatesByProcessDefIdAndItemIds(String processDefId, List<String> itemIds);

    /**
     * 根据业务事项实例UUID获取业务事项操作列表
     *
     * @param itemInstUuid
     * @return
     */
    List<BizProcessItemOperationDto> listProcessItemOperationByUuid(String itemInstUuid);

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形材料
     *
     * @param itemId
     * @param processDefId
     * @param values
     * @return
     */
    List<ItemMaterial> listItemSituationMaterial(String itemId, String processDefId, Map<String, Object> values);

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形时限
     *
     * @param itemId
     * @param processDefId
     * @param values
     * @return
     */
    List<ItemTimeLimit> listItemSituationTimeLimit(String itemId, String processDefId, Map<String, Object> values);

}
