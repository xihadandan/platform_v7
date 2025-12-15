package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.SecurityLoginRequest;
import com.wellsoft.pt.api.response.SecurityLoginResponse;

public class SecurityLoginTest {
    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {

        //String baseAddress = "http://oa.well-soft.com:9100/webservices/wellpt/rest/service";
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, "T001", "ldx", "0");

        SecurityLoginRequest w5 = new SecurityLoginRequest();
        w5.setDeviceId("");
        w5.setPassword("0");
        w5.setUsername("ldx");
        SecurityLoginResponse r5 = wellptClient.execute(w5);
        System.out.println("SecurityLoginRequest");
        System.out.println(r5.getMsg() + ":" + r5.getSenderDic());
    }
}
