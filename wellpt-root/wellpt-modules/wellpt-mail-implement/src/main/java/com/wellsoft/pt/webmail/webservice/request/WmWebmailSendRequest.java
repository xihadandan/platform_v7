/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.webmail.webservice.WmWebmailApiServiceName;
import com.wellsoft.pt.webmail.webservice.response.WmWebmailSendResponse;

/**
 * Description: 发送请求
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
public class WmWebmailSendRequest extends WellptRequest<WmWebmailSendResponse> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return WmWebmailApiServiceName.WM_WEBMAIL_SEND;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<WmWebmailSendResponse> getResponseClass() {
        return WmWebmailSendResponse.class;
    }

}
