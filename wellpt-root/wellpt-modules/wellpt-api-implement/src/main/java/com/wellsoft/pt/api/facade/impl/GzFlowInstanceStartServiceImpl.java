/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.internal.suport.FormDataUtils;
import com.wellsoft.pt.api.request.GzFlowInstanceStartRequest;
import com.wellsoft.pt.api.response.GzFlowInstanceStartResponse;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
@Service(ApiServiceName.GZ_FLOW_INSTANCE_START)
@Transactional
public class GzFlowInstanceStartServiceImpl extends BaseServiceImpl implements WellptService<GzFlowInstanceStartRequest> {

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
    public synchronized WellptResponse doService(GzFlowInstanceStartRequest startRequest) {
        String flowDefinitionId = null;
        String startUserId = null;
        String startUserName = null;
        String toTaskId = startRequest.getToTaskId();
        if (StringUtils.isBlank(toTaskId)) {
            toTaskId = FlowService.AS_DRAFT;
        }
        List<String> toTaskUsers = new ArrayList<String>();
        String formUuid = null;
        String dataUuid = null;

        // flowService.startByFlowDefId(flowDefId, startUserId, toTaskId,
        // toTaskUsers, formUuid, dataUuid);

        flowDefinitionId = startRequest.getFlowDefinitionId();
        startUserId = SpringSecurityUtils.getCurrentUserId();
        startUserName = SpringSecurityUtils.getCurrentUserName();
        if (!FlowService.AUTO_SUBMIT.equals(toTaskId)) {
            toTaskUsers.add(startUserId);
        }

        // 获取流程定义
        FlowDefinition flowDefinition = flowService.getFlowDefinitionById(flowDefinitionId);
        if (flowDefinition == null) {
            throw new WorkFlowException("找不到ID为[" + flowDefinitionId + "]的流程定义!");
        }
        TaskData taskData = new TaskData();
        taskData.setUserId(startUserId);
        taskData.setUserName(startUserName);

        taskData.setFormUuid(flowDefinition.getFormUuid());
        formUuid = flowDefinition.getFormUuid();

        DyFormData dyFormData = FormDataUtils.merge2DyformData(startRequest.getFormData(), formUuid, dataUuid);
        dataUuid = dyFormFacade.saveFormData(dyFormData);

        FlowInstance flowInstance = flowService.startByFlowDefId(flowDefinitionId, startUserId, toTaskId, toTaskUsers,
                formUuid, dataUuid);
        if (dyFormData.getFieldValue(WfGzDataConstant.SOURCE_TITLE) != null) {
            flowInstance.setTitle(dyFormData.getFieldValue(WfGzDataConstant.SOURCE_TITLE).toString());
            flowService.saveFlowInstance(flowInstance);
        }
        QueryItem data = new QueryItem();

        GzFlowInstanceStartResponse response = new GzFlowInstanceStartResponse();
        data.put("flowInstUuid", flowInstance.getUuid());
        data.put("formUuid", formUuid);
        data.put("dataUuid", dataUuid);
        data.put("createTime", flowInstance.getCreateTime());
        response.setData(data);

        return response;
    }
}
