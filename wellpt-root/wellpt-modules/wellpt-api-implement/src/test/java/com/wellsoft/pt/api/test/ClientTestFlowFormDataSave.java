/*
 * @(#)2019年8月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowFormDataSaveRequest;
import com.wellsoft.pt.api.response.FlowFormDataSaveResponse;

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
public class ClientTestFlowFormDataSave {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://127.0.0.1:8080/webservices/wellpt/rest/service";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0X2lubmVyIiwiYXVkIjoi5rWL6K-V5YaF6YOo57O757ufIiwiZXhwIjoxNTk2ODUyODQwLCJ1bml0IjoiUzAwMDAwMDAwNzAiLCJzeXN0ZW1jb2RlIjoidGVzdF9pbm5lciIsInN5c3RlbW5hbWUiOiLmtYvor5XlhoXpg6jns7vnu58iLCJhdXRob3JpemVhcGlzIjpbIi9hcGkvKioiXSwidW5hdXRob3JpemVhcGlzIjpbXX0.k5I0jZweIbHMcJCJXw0FM5LMQ_tJpFzr77rqGUCnXqM";
        WellptClient wellptClient = new BearerTokenBasedWellptClient(baseAddress, "T001", "adm_pt", token);

        // 保存流程表单数据
        FlowFormDataSaveRequest flowFormDataSaveRequest = new FlowFormDataSaveRequest();
        flowFormDataSaveRequest.setFlowInstUuid("0fe04ba5-ecc3-4885-98b2-fa94238d3a7f");
        Map<String, Object> formData = Maps.newHashMap();
        formData.put("test_job_show", "abc");
        flowFormDataSaveRequest.setFormData(formData);
        FlowFormDataSaveResponse flowFormDataSaveResponse = wellptClient.execute(flowFormDataSaveRequest);
        System.out.println(flowFormDataSaveResponse);
        System.out.println(JsonUtils.object2Json(flowFormDataSaveResponse));
    }

}
