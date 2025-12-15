package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.WorkHourRequest;
import com.wellsoft.pt.api.response.WorkHourResponse;

import java.util.Calendar;
import java.util.Date;

public class WorkHourTest {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {

        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");

        WorkHourRequest wr = new WorkHourRequest(WorkHourRequest.METHOD_WORKTIME);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 10);
        wr.setParm(c.getTime(), new Date());

        WorkHourResponse response = wellptClient.execute(wr);
        System.out.println(response.getpMap());

        WorkHourRequest wr1 = new WorkHourRequest(WorkHourRequest.METHOD_POINTTIME);
        wr1.setParm(new Date(), -10, WorkHourRequest.TYPE_HOUR);

        WorkHourResponse response1 = wellptClient.execute(wr1);
        System.out.println(response1.getpMap());
    }
}
