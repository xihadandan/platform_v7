/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.service.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmWebmailBean;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.webservice.WmWebmailApiServiceName;
import com.wellsoft.pt.webmail.webservice.request.WmWebmailInboxGetRequest;
import com.wellsoft.pt.webmail.webservice.response.WmWebmailInboxGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 收件箱获取Web服务Impl
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
@Service(WmWebmailApiServiceName.WM_WEBMAIL_INBOX_GET)
@Transactional(readOnly = true)
public class WmWebmailInboxGetWebServiceImpl implements WellptService<WmWebmailInboxGetRequest> {

    @Autowired
    private WmWebmailService wmWebmailService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(WmWebmailInboxGetRequest request) {
        String mailboxUuid = request.getUuid();
        String userId = SpringSecurityUtils.getCurrentUserId();
        WmWebmailBean bean = wmWebmailService.getWmWebmailBean(userId, mailboxUuid);

        WmWebmailInboxGetResponse response = new WmWebmailInboxGetResponse();
        response.setData(bean);
        return response;
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.api.facade.WellptService#getRequestClass()
     */
    // @Override
    // public Class<? extends WellptRequest<?>> getRequestClass() {
    // return WmWebmailInboxGetRequest.class;
    // }

}
