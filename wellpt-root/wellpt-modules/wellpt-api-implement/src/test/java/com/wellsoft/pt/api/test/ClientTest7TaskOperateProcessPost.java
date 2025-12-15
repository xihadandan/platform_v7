/*
 * @(#)2014-9-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.request.TaskOperateProcessPostRequest;
import com.wellsoft.pt.api.response.TaskOperateProcessPostResponse;

/**
 * Description: 如何描述该类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-25.1	Asus		2014-9-25		Create
 * </pre>
 * @date 2014-9-25
 */
public class ClientTest7TaskOperateProcessPost {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        String baseAddress = "http://localhost:8080/wellpt-web/webservices/wellpt/rest/service";
        WellptClient wellptClient = new DefaultWellptClient(baseAddress,
                "T001", "ldx", "0");

        TaskOperateProcessPostRequest postRequest = new TaskOperateProcessPostRequest();
        TaskOperateProcessPostResponse postResponse = wellptClient
                .execute(postRequest);
        System.out.println(postResponse);
    }

}
