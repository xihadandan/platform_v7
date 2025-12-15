/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.internal.suport.FormDataUtils;
import com.wellsoft.pt.api.request.FlowFormDataSaveRequest;
import com.wellsoft.pt.api.response.FlowFormDataSaveResponse;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
@Service(ApiServiceName.FLOW_FORM_DATA_SAVE)
@Transactional
public class FlowFormDataSaveServiceImpl extends BaseServiceImpl implements WellptService<FlowFormDataSaveRequest> {

    @Autowired
    private FlowService flowService;

    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowFormDataSaveRequest saveRequest) {
        String flowInstUuid = saveRequest.getFlowInstUuid();

        if (StringUtils.isBlank(flowInstUuid)) {
            throw new WorkFlowException("流程实例UUID不能为空!");
        }

        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        String formUuid = flowInstance.getFormUuid();
        String dataUuid = flowInstance.getDataUuid();

        DyFormData dyFormData = FormDataUtils.merge2DyformData(saveRequest.getFormData(), formUuid, dataUuid);
        dataUuid = dyFormFacade.saveFormData(dyFormData);
        if (StringUtils.isBlank(flowInstance.getDataUuid())) {
            Set<TaskInstance> taskInstances = flowInstance.getTaskInstances();
            for (TaskInstance taskInstance : taskInstances) {
                if (StringUtils.isNotBlank(flowInstance.getFormUuid())
                        && flowInstance.getFormUuid().equals(taskInstance.getFormUuid())) {
                    taskInstance.setDataUuid(dataUuid);
                    this.dao.save(taskInstance);
                }
            }

            flowInstance.setDataUuid(dataUuid);
            flowService.saveFlowInstance(flowInstance);
        }

        FlowFormDataSaveResponse saveResponse = new FlowFormDataSaveResponse();
        return saveResponse;
    }

}
