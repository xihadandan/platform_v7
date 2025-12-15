package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.ScheduleGetDayCountRequest;
import com.wellsoft.pt.api.response.ScheduleGetDayCountResponse;

public class ScheduleTest {
    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {

        //String baseAddress = "http://oa.well-soft.com:9100/webservices/wellpt/rest/service";
        String baseAddress = "http://localhost:8080/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");

        //		//======ScheduleQueryByDate========有数据返回但格式解析错误
        //		ScheduleQueryByDateRequest wr = new ScheduleQueryByDateRequest();
        //		wr.setStrDate("2015-2-25");
        //		wr.setEndDate("2015-2-25");
        //		ScheduleQueryByDateResponse response = wellptClient.execute(wr);
        //		//System.out.println("ScheduleQueryByDate");
        //		System.out.println(response.getMsg() + ":" + response.getSchedule());

        //		//		// ======ScheduleGetDayCount========ok
        ScheduleGetDayCountRequest wr1 = new ScheduleGetDayCountRequest();
        wr1.setStrDate("2015-1-1");
        wr1.setEndDate("2015-3-1");
        ScheduleGetDayCountResponse r1 = wellptClient.execute(wr1);
        System.out.println("ScheduleGetDayCount");
        System.out.println(r1.getMsg() + ":" + r1.getData());
        //
        //		// ======ScheduleMySchedulesRequest======== 有数据返回但格式解析错误
        //		ScheduleMySchedulesRequest w3 = new ScheduleMySchedulesRequest();
        //		ScheduleMySchedulesResponse r3 = wellptClient.execute(w3);
        //		System.out.println("ScheduleMySchedulesRequest");
        //		System.out.println(r3.getMsg() + ":" + r3.getSchedule());
        //
        //======ScheduleTagGetRequest========ok
        //		ScheduleTagGetRequest w4 = new ScheduleTagGetRequest();
        //		ScheduleTagGetResponse r4 = wellptClient.execute(w4);
        //		System.out.println("ScheduleTagGetRequest");
        //		System.out.println(r4.getMsg() + ":" + r4.getTags());

        // ======ScheduleModify========

        //===ScheduleAddRequest
        //		ScheduleAddRequest w5 = new ScheduleAddRequest();
        //		w5.setName("testtest");
        //		w5.setEndDate("2015-3-4");
        //		w5.setAddress("lllllllll");
        //
        //		ScheduleAddResponse r5 = wellptClient.execute(w5);
        //		System.out.println("ScheduleAddRequest");
        //		System.out.println(r5.getMsg() + ":");

        //		ScheduleDeleteRequest w6 = new ScheduleDeleteRequest();
        //		w6.setId("72dbe809-55ca-4041-80fa-57ebfa483b23");
        //		ScheduleDeleteResponse r6 = wellptClient.execute(w6);
        //		System.out.println(r6.getMsg() + ":");
    }
}
