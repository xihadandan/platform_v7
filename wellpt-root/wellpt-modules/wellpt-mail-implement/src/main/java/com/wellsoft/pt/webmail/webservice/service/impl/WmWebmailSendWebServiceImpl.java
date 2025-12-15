/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.service.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.webmail.facade.api.service.impl.MailClientApiFacadeImpl;
import com.wellsoft.pt.webmail.webservice.WmWebmailApiServiceName;
import com.wellsoft.pt.webmail.webservice.request.WmWebmailSendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 发送Web服务Impl
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
@Service(WmWebmailApiServiceName.WM_WEBMAIL_SEND)
@Transactional
public class WmWebmailSendWebServiceImpl implements WellptService<WmWebmailSendRequest> {

    @Autowired
    private MailClientApiFacadeImpl mailClientApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(WmWebmailSendRequest wellptRequest) {
        return null;
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.api.facade.WellptService#getRequestClass()
     */
    // @Override
    // public Class<? extends WellptRequest<?>> getRequestClass() {
    // return WmWebmailSendRequest.class;
    // }

}
