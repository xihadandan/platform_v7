/*
 * @(#)2014-9-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.request.TaskSubmitRequest;
import com.wellsoft.pt.api.response.TaskSubmitResponse;

import java.util.ArrayList;
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
 * 2014-9-24.1	zhulh		2014-9-24		Create
 * </pre>
 * @date 2014-9-24
 */
public class ClientTest5TaskSubmit {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress,
                "T001", "ldx", "0");

        // 11、任务提交
        System.out.println("任务提交");
        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> ids1 = new ArrayList<String>();
        ids1.add("U0010000001");
        // taskUsers.put("T807", ids1);

        List<String> ids2 = new ArrayList<String>();
        ids2.add("U0010000001");
        ids2.add("U0010000095");
        taskUsers.put("T621", ids2);

        TaskSubmitRequest taskSubmitRequest = new TaskSubmitRequest();
        taskSubmitRequest.setUuid("d6743afe-fc01-4531-a986-30d78e4a23d5");
        // taskSubmitRequest.setTaskUsers(taskUsers);
        TaskSubmitResponse taskSubmitResponse = wellptClient
                .execute(taskSubmitRequest);
        System.out.println(taskSubmitResponse);
        if (taskSubmitResponse.getCode().equals("-8")) {
            System.out
                    .println(WellptJsonParser.object2Json(taskSubmitResponse));
        }

    }
}
