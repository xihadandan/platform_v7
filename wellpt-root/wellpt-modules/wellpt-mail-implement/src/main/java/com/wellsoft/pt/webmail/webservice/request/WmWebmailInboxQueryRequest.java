/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.request;

import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.webmail.webservice.WmWebmailApiServiceName;
import com.wellsoft.pt.webmail.webservice.response.WmWebmailInboxQueryResponse;

/**
 * Description: 收件箱查询请求
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月18日.1	zhulh		2016年7月18日		Create
 * </pre>
 * @date 2016年7月18日
 */
public class WmWebmailInboxQueryRequest extends WellptQueryRequest<WmWebmailInboxQueryResponse> {

    private String queryValue;

    /**
     * @return the queryValue
     */
    public String getQueryValue() {
        return queryValue;
    }

    /**
     * @param queryValue 要设置的queryValue
     */
    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return WmWebmailApiServiceName.WM_WEBMAIL_INBOX_QUERY;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<WmWebmailInboxQueryResponse> getResponseClass() {
        return WmWebmailInboxQueryResponse.class;
    }

}
