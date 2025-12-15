/*
 * @(#)2018年6月8日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.ApproveContentDto;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月8日.1	zhulh		2018年6月8日		Create
 * </pre>
 * @date 2018年6月8日
 */
public interface WorkFlowApproveService extends BaseService {

    /**
     * 送审批
     *
     * @param sendContent
     * @return
     */
    ResultMessage sendToApprove(ApproveContentDto approveContentDto, InteractionTaskData interactionTaskData);


    /**
     * 判断是否允许通过单据转换规则转换源表单数据
     *
     * @param sourceFormUuid
     * @param botRuleId
     * @param flowDefId
     * @return
     */
    boolean isAllowedConvertDyformDataByBotRuleId(String sourceFormUuid, String botRuleId, String flowDefId);

    /**
     * 通过单据转换规则转换源表单数据
     *
     * @param sourceFormUuid
     * @param sourceDataUuid
     * @param botRuleId
     * @return
     */
    DyFormData convertDyformDataByBotRuleId(String sourceFormUuid, String sourceDataUuid, String botRuleId);

}
