/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.entity.WmMailbox;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import com.wellsoft.pt.webmail.webservice.WmWebmailApiServiceName;
import com.wellsoft.pt.webmail.webservice.request.WmWebmailOutboxQueryRequest;
import com.wellsoft.pt.webmail.webservice.response.WmWebmailInboxQueryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 发件箱查询Web服务Impl
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
@Service(WmWebmailApiServiceName.WM_WEBMAIL_OUTBOX_QUERY)
@Transactional(readOnly = true)
public class WmWebmailOutboxQueryWebServiceImpl extends BaseServiceImpl implements WellptService<WmWebmailOutboxQueryRequest> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(WmWebmailOutboxQueryRequest request) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(request.getPageNo());
        pagingInfo.setPageSize(request.getPageSize());

        String userId = SpringSecurityUtils.getCurrentUserId();
        String queryValue = request.getQueryValue();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("status", WmWebmailConstants.STATUS_SEND_SUCCESS);
        values.put("queryValue", queryValue);
        List<WmMailbox> wmMailboxs = this.dao.namedQuery("wmWebmailOutboxQuery", values, WmMailbox.class, pagingInfo);

        WmWebmailInboxQueryResponse response = new WmWebmailInboxQueryResponse();
        response.setDataList(wmMailboxs);
        response.setTotal(pagingInfo.getTotalCount());
        response.setStart(pagingInfo.getFirst());
        response.setSize(wmMailboxs.size());
        return response;
    }

    /**
     * (non-Javadoc)
     * @see com.wellsoft.pt.api.facade.WellptService#getRequestClass()
     */
    // @Override
    // public Class<? extends WellptRequest<?>> getRequestClass() {
    // return WmWebmailOutboxQueryRequest.class;
    // }

}
