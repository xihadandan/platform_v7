/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowInstanceStartRequest;
import com.wellsoft.pt.api.request.TaskQueryRequest;
import com.wellsoft.pt.api.request.TaskSubmitRequest;
import com.wellsoft.pt.api.response.FlowInstanceStartResponse;
import com.wellsoft.pt.api.response.TaskQueryResponse;
import com.wellsoft.pt.api.response.TaskSubmitResponse;

import java.util.Date;
import java.util.HashMap;
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
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class ClientTest9 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String baseAddress = "http://localhost:8080/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");

        // 4、启动流程实例
        FlowInstanceStartRequest flowInstanceStartRequest = new FlowInstanceStartRequest();
        flowInstanceStartRequest.setFlowDefinitionId("TEMPORARY_JOB_TEMPLATE");
        flowInstanceStartRequest.setToTaskId("T411");
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("item_name", "a");
        formData.put("item_url", "2");
        formData.put("item_id", new Date());
        FlowInstanceStartResponse flowInstanceStartResponse = wellptClient.execute(flowInstanceStartRequest);
        System.out.println(flowInstanceStartResponse);

        Map<String, String> result = (Map<String, String>) flowInstanceStartResponse.getData();
        String flowInstUuid = result.get("flowInstUuid");

        // 9、任务查询
        System.out.println("任务查询");
        TaskQueryRequest taskQueryRequest = new TaskQueryRequest();
        taskQueryRequest.setFlowInstUuid(flowInstUuid);
        TaskQueryResponse taskQueryResponse = wellptClient.execute(taskQueryRequest);
        System.out.println(taskQueryResponse);

        // 11、任务提交
        System.out.println("任务提交");
        TaskSubmitRequest taskSubmitRequest = new TaskSubmitRequest();
        taskSubmitRequest.setUuid(taskQueryResponse.getDataList().get(0).getUuid());
        TaskSubmitResponse taskSubmitResponse = wellptClient.execute(taskSubmitRequest);
        System.out.println(taskSubmitResponse);
    }

}
