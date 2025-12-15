/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.MessageCancelRequest;
import com.wellsoft.pt.api.response.MessageCancelResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.message.facade.service.impl.MessageClientApiFacadeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-15.1	tony		2014-10-15		Create
 * </pre>
 * @date 2014-10-15
 */
@Service(ApiServiceName.MESSAGE_CANCEL)
@Transactional
public class MessageCancelServiceImpl extends BaseServiceImpl implements WellptService<MessageCancelRequest> {

    @Autowired(required = false)
    public MessageClientApiFacadeImpl messageClientApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(MessageCancelRequest cancelRequest) {
        String datauuid = cancelRequest.getDataUuid();
        messageClientApiFacade.cancelMessage(datauuid);
        MessageCancelResponse response = new MessageCancelResponse();
        return response;
    }

}
