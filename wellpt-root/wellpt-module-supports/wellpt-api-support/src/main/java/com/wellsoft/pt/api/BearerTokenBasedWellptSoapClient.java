/*
 * @(#)2019年9月2日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import com.wellsoft.pt.api.domain.RequestParam;
import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.internal.parser.WellptParser;
import com.wellsoft.pt.api.service.WellptSoapWebService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.http.entity.mime.content.StringBody;

import java.util.Arrays;
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
 * 2019年9月2日.1	zhulh		2019年9月2日		Create
 * </pre>
 * @date 2019年9月2日
 */
public class BearerTokenBasedWellptSoapClient implements WellptClient {

    private String accessToken;

    private WellptSoapWebService service;

    private String responseBody;

    /**
     * @param service
     */
    public BearerTokenBasedWellptSoapClient(WellptSoapWebService service, String accessToken) {
        super();
        this.service = service;
        this.accessToken = accessToken;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#execute(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public <T extends WellptResponse> T execute(WellptRequest<T> request) {
        RequestParam param = new RequestParam();
        param.setTenantId(SpringSecurityUtils.getCurrentTenantId());
        param.setUsername(SpringSecurityUtils.getCurrentUserId());
        param.setJson(WellptJsonParser.object2Json(request));

        // 添加HTTP请求头
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Authorization", Arrays.asList("Bearer " + accessToken));
        Client proxy = ClientProxy.getClient(service);
        proxy.getRequestContext().put(Message.PROTOCOL_HEADERS, headers);
        responseBody = service.execute(param);
        WellptParser<T> parser = new WellptJsonParser<T>(request.getResponseClass());
        return parser.parse(responseBody);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#getResponseBody()
     */
    @Override
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#addRequestParam(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, String> addRequestParam(String key, String value) {
        throw new UnsupportedOperationException();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#addRequestPart(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, StringBody> addRequestPart(String key, String value) {
        throw new UnsupportedOperationException();
    }

}
