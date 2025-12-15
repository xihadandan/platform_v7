/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import org.apache.http.entity.mime.content.StringBody;

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
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public interface WellptClient {

    <T extends WellptResponse> T execute(WellptRequest<T> request);

    String getResponseBody();

    Map<String, String> addRequestParam(String key, String value);

    Map<String, StringBody> addRequestPart(String key, String value);

}
