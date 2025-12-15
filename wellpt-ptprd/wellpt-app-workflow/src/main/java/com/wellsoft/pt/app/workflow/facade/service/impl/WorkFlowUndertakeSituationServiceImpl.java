/*
 * @(#)2019年3月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.wellsoft.pt.app.workflow.dto.UndertakeSituationActionData;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowUndertakeSituationService;
import com.wellsoft.pt.bot.dto.BotRuleConfDto;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.facade.service.BotRuleConfFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月22日.1	zhulh		2019年3月22日		Create
 * </pre>
 * @date 2019年3月22日
 */
@Service
public class WorkFlowUndertakeSituationServiceImpl implements WorkFlowUndertakeSituationService {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private BotRuleConfFacadeService botRuleConfFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowUndertakeSituationService#startBranchTask(com.wellsoft.pt.app.workflow.dto.UndertakeSituationActionData)
     */
    @Override
    @Transactional
    public void startBranchTask(UndertakeSituationActionData undertakeSituationActionData) {
        String belongToTaskInstUuid = undertakeSituationActionData.getBelongToTaskInstUuid();
        String belongToFlowInstUuid = undertakeSituationActionData.getBelongToFlowInstUuid();
        String businessType = undertakeSituationActionData.getBusinessType();
        String businessRole = undertakeSituationActionData.getBusinessRole();
        String actionName = undertakeSituationActionData.getActionName();
        // 子表单数据转到数据库中
        mergeChildFormData2MainFlowFormData(undertakeSituationActionData.getBotRuleUuid(),
                undertakeSituationActionData.getChildDyformData(), belongToFlowInstUuid);

        // 发起分支流
        taskService.startBranchTask(belongToTaskInstUuid, belongToFlowInstUuid, businessType, businessRole, actionName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowUndertakeSituationService#startSubFlow(com.wellsoft.pt.app.workflow.dto.UndertakeSituationActionData)
     */
    @Override
    @Transactional
    public void startSubFlow(UndertakeSituationActionData undertakeSituationActionData) {
        String belongToTaskInstUuid = undertakeSituationActionData.getBelongToTaskInstUuid();
        String belongToFlowInstUuid = undertakeSituationActionData.getBelongToFlowInstUuid();
        String businessType = undertakeSituationActionData.getBusinessType();
        String businessRole = undertakeSituationActionData.getBusinessRole();
        String actionName = undertakeSituationActionData.getActionName();
        // 子表单数据转到数据库中
        mergeChildFormData2MainFlowFormData(undertakeSituationActionData.getBotRuleUuid(),
                undertakeSituationActionData.getChildDyformData(), belongToFlowInstUuid);

        // 发起子流程
        taskService.startSubFlow(belongToTaskInstUuid, belongToFlowInstUuid, businessType, businessRole, actionName);
    }

    /**
     * @param botRuleUuid
     * @param childDyformData
     * @param belongToFlowInstUuid
     */
    @Override
    public void mergeChildFormData2MainFlowFormData(String botRuleUuid, DyFormData childDyformData,
                                                    String belongToFlowInstUuid) {
        BotRuleConfDto botRuleConfDto = botRuleConfFacadeService.getBotRuleConfigDetail(botRuleUuid);
        FlowInstance flowInstance = flowService.getFlowInstance(belongToFlowInstUuid);
        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(childDyformData.getDataUuid(), childDyformData.getFormUuid(),
                childDyformData);
        froms.add(botFromParam);
        BotParam botParam = new BotParam(botRuleConfDto.getId(), froms);
        botParam.setTargetUuid(flowInstance.getDataUuid());
        BotResult botResult = botFacadeService.startBot(botParam);
    }

}
