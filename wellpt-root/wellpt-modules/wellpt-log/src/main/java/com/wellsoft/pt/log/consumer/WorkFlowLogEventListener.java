/*
 * @(#)2021年1月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.consumer;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.service.BusinessOperationLogService;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月8日.1	zhongzh		2021年1月8日		Create
 * </pre>
 * @date 2021年1月8日
 */
// @Component
public class WorkFlowLogEventListener {

    @Autowired
    private WorkService workService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BusinessOperationLogService logBusinessOperationService;

    @Transactional
    // @RocketMqListener(body = LogEvent.class, tags = LogEvent.TAGS_LOG)
    public void handle(LogEvent event) {
        BusinessOperationLog source = event.getSource();
        if (null == source) {
            return;
        }
        logBusinessOperationService.saveLog(source);
        Map<String, Object> detail = event.getParams();
        if (null == detail) {
            return;
        }
        String dyformDataJson = (String) detail.get("dyform");
        if (StringUtils.isBlank(dyformDataJson)) {
            return;
        }
        String flowInstUuid = (String) detail.get("flowInstUuid");
        String taskInstUuid = (String) detail.get("taskInstUuid");
        DyFormData dyFormData = dyFormFacade.parseDyformData(dyformDataJson);
        System.out.println(dyFormData);
        System.out.println(flowInstUuid);
        System.out.println(taskInstUuid);
    }

}
