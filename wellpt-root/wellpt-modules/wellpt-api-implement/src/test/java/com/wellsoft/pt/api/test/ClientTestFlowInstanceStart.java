/*
 * @(#)2019年8月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowInstanceStartRequest;
import com.wellsoft.pt.api.response.FlowInstanceStartResponse;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;

import java.util.HashMap;
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
 * 2019年8月12日.1	zhulh		2019年8月12日		Create
 * </pre>
 * @date 2019年8月12日
 */
public class ClientTestFlowInstanceStart {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 启动流程实例
        FlowInstanceStartRequest flowInstanceStartRequest = new FlowInstanceStartRequest();
        InteractionTaskData interactionTaskData = new InteractionTaskData();
        flowInstanceStartRequest.setFlowDefinitionId("test2");
        flowInstanceStartRequest.setToTaskId(FlowService.AUTO_SUBMIT);
        // 提交环节
        //        Map<String, String> toTaskIds = Maps.newHashMap();
        //        toTaskIds.put("T330", "T865");
        // interactionTaskData.setToTaskIds(toTaskIds);
        // 环节办理人
        Map<String, List<String>> taskUsers = Maps.newHashMap();
        List<String> userIds = Lists.newArrayList();
        userIds.add("U0000000059");
        taskUsers.put("T038", userIds);
        interactionTaskData.setTaskUsers(taskUsers);
        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("test_job_show", "aaa");
        flowInstanceStartRequest.setFormData(formData);
        flowInstanceStartRequest.setInteractionTaskData(interactionTaskData);
        FlowInstanceStartResponse flowInstanceStartResponse = wellptClient.execute(flowInstanceStartRequest);
        System.out.println(flowInstanceStartResponse.getMsg());
        System.out.println(JsonUtils.object2Json(flowInstanceStartResponse));
    }

}
