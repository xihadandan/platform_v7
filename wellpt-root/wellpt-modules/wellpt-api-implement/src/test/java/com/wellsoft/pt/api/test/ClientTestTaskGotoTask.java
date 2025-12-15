/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.TaskGotoTaskRequest;
import com.wellsoft.pt.api.response.TaskGotoTaskResponse;

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
 * 2019年8月13日.1	zhulh		2019年8月13日		Create
 * </pre>
 * @date 2019年8月13日
 */
public class ClientTestTaskGotoTask {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 环节特送环节
        TaskGotoTaskRequest taskGotoTaskRequest = new TaskGotoTaskRequest();
        taskGotoTaskRequest.setUuid("5b93c2f1-0073-4b31-ad10-963bf03261e4");
        taskGotoTaskRequest.setGotoTaskId("T864");
        taskGotoTaskRequest.setOpinionName("同意");
        taskGotoTaskRequest.setOpinionValue("1");
        taskGotoTaskRequest.setOpinionText("特送环节测试");
        List<String> userIds = Lists.newArrayList();
        userIds.add("U0000000076");
        Map<String, List<String>> taskUsers = Maps.newHashMap();
        taskUsers.put("T864", userIds);
        taskGotoTaskRequest.setTaskUsers(taskUsers);
        TaskGotoTaskResponse taskGotoTaskResponse = wellptClient.execute(taskGotoTaskRequest);
        System.out.println(taskGotoTaskResponse);
        System.out.println(JsonUtils.object2Json(taskGotoTaskResponse));
    }

}
