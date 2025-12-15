/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowInstanceDraftQueryRequest;
import com.wellsoft.pt.api.request.FlowInstanceQueryRequest;
import com.wellsoft.pt.api.response.FlowInstanceDraftQueryResponse;
import com.wellsoft.pt.api.response.FlowInstanceQueryResponse;

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
public class ClientTestFlowInstanceQuery {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 查询流程实例
        FlowInstanceQueryRequest flowInstanceQueryRequest = new FlowInstanceQueryRequest();
        // flowInstanceQueryRequest.setTitle("test");
        FlowInstanceQueryResponse flowInstanceQueryResponse = wellptClient.execute(flowInstanceQueryRequest);
        System.out.println(flowInstanceQueryResponse);
        System.out.println(JsonUtils.object2Json(flowInstanceQueryResponse));

        FlowInstanceDraftQueryRequest flowInstanceDraftQueryRequest = new FlowInstanceDraftQueryRequest();
        FlowInstanceDraftQueryResponse flowInstanceDraftQueryResponse = wellptClient
                .execute(flowInstanceDraftQueryRequest);
        System.out.println(flowInstanceDraftQueryResponse);
        System.out.println(JsonUtils.object2Json(flowInstanceDraftQueryResponse));
    }

}
