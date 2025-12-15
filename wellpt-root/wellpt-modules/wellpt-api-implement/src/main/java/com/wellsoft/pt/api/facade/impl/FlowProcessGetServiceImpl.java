/*
 * @(#)2014-9-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.FlowProcessGetRequest;
import com.wellsoft.pt.api.response.FlowProcessGetResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.workflow.work.bean.WorkProcessBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 获取流程办理过程
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-28.1	zhulh		2014-9-28		Create
 * </pre>
 * @date 2014-9-28
 */
@Service(ApiServiceName.FLOW_PROCESS_GET)
public class FlowProcessGetServiceImpl extends BaseServiceImpl implements WellptService<FlowProcessGetRequest> {

    @Autowired
    private WorkService workService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FlowProcessGetRequest processGetRequest) {
        String flowInstUuid = processGetRequest.getFlowInstUuid();
        List<WorkProcessBean> workProcessBeans = workService.getWorkProcess(flowInstUuid);

        FlowProcessGetResponse processGetResponse = new FlowProcessGetResponse();
        processGetResponse.setDataList(workProcessBeans);

        return processGetResponse;
    }

}
