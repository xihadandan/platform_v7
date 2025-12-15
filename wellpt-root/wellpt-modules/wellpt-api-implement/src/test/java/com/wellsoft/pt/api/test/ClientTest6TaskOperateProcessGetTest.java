/*
 * @(#)2014-9-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.request.TaskOperateProcessPostRequest;
import com.wellsoft.pt.api.response.TaskOperateProcessPostResponse;

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
public class ClientTest6TaskOperateProcessGetTest {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        TaskOperateProcessPostRequest request = new TaskOperateProcessPostRequest();

        System.out.println(WellptJsonParser.object2Json(request));

        System.out.println(WellptJsonParser
                .object2Json(new TaskOperateProcessPostResponse()));
    }
}
