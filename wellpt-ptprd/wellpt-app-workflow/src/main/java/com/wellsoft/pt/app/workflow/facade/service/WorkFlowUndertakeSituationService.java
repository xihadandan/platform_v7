/*
 * @(#)2019年3月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service;

import com.wellsoft.pt.app.workflow.dto.UndertakeSituationActionData;
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
 * 2019年3月22日.1	zhulh		2019年3月22日		Create
 * </pre>
 * @date 2019年3月22日
 */
public interface WorkFlowUndertakeSituationService {

    /**
     * @param undertakeSituationActionData
     */
    void startBranchTask(UndertakeSituationActionData undertakeSituationActionData);

    /**
     * @param undertakeSituationActionData
     */
    void startSubFlow(UndertakeSituationActionData undertakeSituationActionData);

    /**
     * @param botRuleUuid
     * @param childDyformData
     * @param belongToFlowInstUuid
     */
    void mergeChildFormData2MainFlowFormData(String botRuleUuid, DyFormData childDyformData, String belongToFlowInstUuid);

}
