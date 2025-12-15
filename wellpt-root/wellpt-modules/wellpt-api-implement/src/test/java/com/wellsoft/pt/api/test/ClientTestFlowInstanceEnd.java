/*
 * @(#)2019年8月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowInstanceEndRequest;
import com.wellsoft.pt.api.response.FlowInstanceEndResponse;

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
public class ClientTestFlowInstanceEnd {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 结束流程实例
        FlowInstanceEndRequest flowInstanceEndRequest = new FlowInstanceEndRequest();
        flowInstanceEndRequest.setUuid("11c4bc69-c186-494c-8f04-e6a37c8e2330");
        FlowInstanceEndResponse flowInstanceEndResponse = wellptClient.execute(flowInstanceEndRequest);
        System.out.println(flowInstanceEndResponse.getMsg());
        System.out.println(JsonUtils.object2Json(flowInstanceEndResponse));
    }

}
