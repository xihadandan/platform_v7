package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.request.TaskSubmitRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientTest3 {
    public static void main(String[] args) {
        String json = "{'uuid':'d6743afe-fc01-4531-a986-30d78e4a23d5','toTaskId':'','taskUsers':'{'T562':['U0010000565','U0010000054']}','operator':'ldx','operateTime':'2014-09-25 02:50:52','opinionName':'','opinionValue':'','opinionText':'','formData':null,'apiServiceName':'workflow.task.submit'}";
        // TaskSubmitRequest wellptRequest = WellptJsonParser.json2Object(json,
        // TaskSubmitRequest.class);
        // System.out.println(wellptRequest);

        TaskSubmitRequest request = new TaskSubmitRequest();
        request.setUuid("2dcc070e-b0f9-4362-8f3f-88c50e98f2a7");
        request.setToTaskId("");
//		request.setOperator("ldx");
//		request.setOperateTime("2014-09-24 03:58:55");
        request.setOpinionName("");
        request.setOpinionValue("");
        request.setOpinionText("");
        request.setFormData(null);

        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> ids1 = new ArrayList<String>();
        ids1.add("U0010000001");

        List<String> ids2 = new ArrayList<String>();
        ids2.add("U0010000001");
        ids2.add("U0010000095");
        taskUsers.put("T621", ids2);
        request.setTaskUsers(taskUsers);

        String jsonString = WellptJsonParser.object2Json(request);
        System.out.println(jsonString);
        System.out.println(WellptJsonParser.json2Object(json,
                TaskSubmitRequest.class).getTaskUsers());
    }
}
