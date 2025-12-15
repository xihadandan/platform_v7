/*
 * @(#)2014-11-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.test;

import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
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
 * 2014-11-7.1	zhulh		2014-11-7		Create
 * </pre>
 * @date 2014-11-7
 */
public class JsonTest {

    /**
     * 如何描述该方法
     *
     * @param args
     */
    public static void main(String[] args) {
        //		WellptResponse r = new WellptResponse();
        //		r.setCode("0");
        //		r.setMsg("\u6210\u529f");
        //		r.setSuccess(true);
        //		System.out.println(WellptJsonParser.object2Json(r));
        //		String responseBody = WellptJsonParser.object2Json(r);
        String responseBody = "﻿{\"code\":\"0\",\"msg\":\"\\u6210\\u529f\",\"success\":true}";
        String a = "{\"code\":\"0\",\"success\":true,\"msg\":\"\\u6210\\u529f\"}";
        TaskOperateProcessPostResponse response = WellptJsonParser.json2Object(responseBody,
                TaskOperateProcessPostResponse.class);
        System.out.println(responseBody);
        //		System.out.println(response.getCode() + " " + response.getMsg() + " " + response.isSuccess());
        //		try {
        //			JSONObject json = new JSONObject(responseBody);
        //			if (!json.has("code") || !json.has("msg") || !json.has("success")) {
        //				json.put("code", "-3");
        //				json.put("msg", "服务器返回无法识别的信息：" + responseBody);
        //				json.put("success", false);
        //				responseBody = json.toString();
        //			}
        //			System.out.println(json.toString());
        //		} catch (JSONException e) {
        //			e.printStackTrace();
        //		}
    }

}
