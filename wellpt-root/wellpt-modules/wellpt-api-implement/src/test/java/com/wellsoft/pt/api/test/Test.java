package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.FlowFormDataSaveRequest;
import com.wellsoft.pt.api.response.FlowFormDataSaveResponse;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        //
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");
        FlowFormDataSaveRequest flowFormDataSaveRequest = new FlowFormDataSaveRequest();
        flowFormDataSaveRequest.setFlowInstUuid("c6a97eca-dfc0-4419-9043-2046722d7d32");

        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put("fjlb", "a98a0caf807c43dd85a6707bb7a38b4b");
        flowFormDataSaveRequest.setFormData(formData);
        FlowFormDataSaveResponse flowFormDataSaveResponse = wellptClient.execute(flowFormDataSaveRequest);
        System.out.println(flowFormDataSaveResponse);
    }
}
