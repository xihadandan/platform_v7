/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowInstanceStartRequest;
import com.wellsoft.pt.api.response.FlowInstanceStartResponse;

import java.util.*;

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
public class ClientTest2 {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "xzsp", "0");

        // 1、获取流程定义
        // System.out.println("获取流程定义");
        // FlowDefinitionGetRequest flowDefinitionGetRequest = new
        // FlowDefinitionGetRequest();
        // flowDefinitionGetRequest.setFlowDefinitionId("XZSP_TEMPLATE");
        // FlowDefinitionGetResponse response =
        // wellptClient.execute(flowDefinitionGetRequest);
        // System.out.println(response);

        // 1、获取流程定义
        // System.out.println("获取流程定义");
        // FlowDefinitionDetailGetReqeust flowDefinitionDetailGetReqeust = new
        // FlowDefinitionDetailGetReqeust();
        // flowDefinitionDetailGetReqeust.setFlowDefinitionId("XZSP_TEMPLATE");
        // FlowDefinitionDetailGetResponse response =
        // wellptClient.execute(flowDefinitionDetailGetReqeust);
        // System.out.println(response);

        // 2、流程定义查询
        // System.out.println("流程定义查询");
        // FlowDefinitionQueryRequest flowDefinitionQueryRequest = new
        // FlowDefinitionQueryRequest();
        // //flowDefinitionQueryRequest.setFlowDefinitionName("测试");
        // FlowDefinitionQueryResponse flowDefinitionQueryResponse =
        // wellptClient.execute(flowDefinitionQueryRequest);
        // System.out.println(flowDefinitionQueryResponse);

        // 3、流程实例查询
        // System.out.println("流程实例查询");
        // FlowInstanceQueryRequest flowInstanceQueryRequest = new
        // FlowInstanceQueryRequest();
        // flowInstanceQueryRequest.setFlowDefinitionId("XZSP_TEMPLATE");
        // FlowInstanceQueryResponse flowInstanceQueryResponse =
        // wellptClient.execute(flowInstanceQueryRequest);
        // System.out.println(flowInstanceQueryResponse);
        //
        // 4、启动流程实例
        // System.out.println("启动流程实例");
        FlowInstanceStartRequest flowInstanceStartRequest = new FlowInstanceStartRequest();
        flowInstanceStartRequest.setFlowDefinitionId("XZSP_TEMPLATE");
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("item_name", "a");
        formData.put("item_url", "2");
        formData.put("item_id", new Date());
        List<Map<String, Object>> s = new ArrayList<Map<String, Object>>();
        Map<String, Object> subformData = new HashMap<String, Object>();
        subformData.put("item_name", "a");
        subformData.put("item_url", "2");
        subformData.put("item_id", new Date());
        s.add(subformData);

        Map<String, Object> subformData2 = new HashMap<String, Object>();
        subformData2.put("item_name", "a");
        subformData2.put("item_url", "2");
        subformData2.put("item_id", new Date());
        s.add(subformData2);
        formData.put("uf_xzsp_subbjsqd_cad", s);
        flowInstanceStartRequest.setFormData(formData);
        FlowInstanceStartResponse flowInstanceStartResponse = wellptClient.execute(flowInstanceStartRequest);
        System.out.println(flowInstanceStartResponse);

        // // 5、结束流程实例
        // FlowInstanceEndRequest flowInstanceEndRequest = new
        // FlowInstanceEndRequest();
        // flowInstanceEndRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // FlowInstanceEndResponse flowInstanceEndResponse =
        // wellptClient.execute(flowInstanceEndRequest);
        // System.out.println(flowInstanceEndResponse);

        // // 5、任务撤回
        // System.out.println("任务撤回");
        // TaskCancelRequest taskCancelRequest = new TaskCancelRequest();
        // taskCancelRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskCancelResponse taskCancelResponse =
        // wellptClient.execute(taskCancelRequest);
        // System.out.println(taskCancelResponse);
        //
        // // 6、任务会签
        // System.out.println("任务会签");
        // TaskCounterSignRequest counterSignRequest = new
        // TaskCounterSignRequest();
        // counterSignRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // counterSignRequest.setTaskUsers(Arrays.asList(new String[] {
        // "T001000000032" }));
        // TaskCounterSignResponse counterSignResponse =
        // wellptClient.execute(counterSignRequest);
        // System.out.println(counterSignResponse);
        //
        // //7、获取任务详细信息
        // System.out.println("获取任务详细信息");
        // TaskDetailGetRequest detailGetRequest = new TaskDetailGetRequest();
        // detailGetRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskDetailGetResponse detailGetResponse =
        // wellptClient.execute(detailGetRequest);
        // System.out.println(detailGetResponse);
        //
        // // 8、获取任务信息
        // System.out.println("获取任务信息");
        // TaskGetRequest taskGetRequest = new TaskGetRequest();
        // taskGetRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskGetResponse taskGetResponse =
        // wellptClient.execute(taskGetRequest);
        // System.out.println(taskGetResponse);
        //
        // 9、任务查询
        // System.out.println("任务查询");
        // TaskQueryRequest taskQueryRequest = new TaskQueryRequest();
        // taskQueryRequest.setId("T_CWQR");
        // taskQueryRequest.setName("确认");
        // taskQueryRequest
        // .setFlowInstUuid("00a4a8dd-fd6c-4e48-9c5c-3e0ee0707d12");
        // TaskQueryResponse taskQueryResponse = wellptClient
        // .execute(taskQueryRequest);
        // System.out.println(taskQueryResponse);

        // // 10、任务退回
        // System.out.println("任务退回");
        // TaskRollbackRequest taskRollbackRequest = new TaskRollbackRequest();
        // taskRollbackRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskRollbackResponse taskRollbackResponse =
        // wellptClient.execute(taskRollbackRequest);
        // System.out.println(taskRollbackResponse);
        //
        // 11、任务提交
        // System.out.println("任务提交");
        // TaskSubmitRequest taskSubmitRequest = new TaskSubmitRequest();
        // taskSubmitRequest.setUuid(taskQueryResponse.getDataList().get(0)
        // .getUuid());
        // TaskSubmitResponse taskSubmitResponse = wellptClient
        // .execute(taskSubmitRequest);
        // System.out.println(taskSubmitResponse);

        // // 12、任务转办
        // System.out.println("任务转办");
        // TaskTransferRequest taskTransferRequest = new TaskTransferRequest();
        // taskTransferRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskTransferResponse taskTransferResponse =
        // wellptClient.execute(taskTransferRequest);
        // System.out.println(taskTransferResponse);
        // // 13、暂停任务
        // System.out.println("暂停任务");
        // TaskSuspendRequest taskSuspendRequest = new TaskSuspendRequest();
        // taskSuspendRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskSuspendResponse taskSuspendResponse =
        // wellptClient.execute(taskSuspendRequest);
        // System.out.println(taskSuspendResponse);
        //
        // // 14、恢复任务
        // System.out.println("恢复任务");
        // TaskResumeRequest taskResumeRequest = new TaskResumeRequest();
        // taskResumeRequest.setUuid("6eaf4017-9718-4479-a5ba-4fa79bb544db");
        // TaskResumeResponse taskResumeResponse =
        // wellptClient.execute(taskResumeRequest);
        // System.out.println(taskResumeResponse);
    }

}
