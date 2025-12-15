/*
 * @(#)2013-12-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.facade.impl;

import com.wellsoft.pt.bm.facade.SelfPublicityAsyncWebService;
import com.wellsoft.pt.bm.service.SelfPublicityAsyncService;
import com.wellsoft.pt.integration.response.Response;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-9.1	zhulh		2013-12-9		Create
 * </pre>
 * @date 2013-12-9
 */
@WebService
@SOAPBinding(style = Style.RPC)
public class SelfPublicityAsyncWebServiceImpl extends BaseServiceImpl implements SelfPublicityAsyncWebService {

    private SelfPublicityAsyncService selfPublicityAsyncService;

    /**
     * @return the selfPublicityAsyncService
     */
    public SelfPublicityAsyncService getSelfPublicityAsyncService() {
        return selfPublicityAsyncService;
    }

    /**
     * @param selfPublicityAsyncService 要设置的selfPublicityAsyncService
     */
    public void setSelfPublicityAsyncService(SelfPublicityAsyncService selfPublicityAsyncService) {
        this.selfPublicityAsyncService = selfPublicityAsyncService;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.facade.SelfPublicityAsyncWebService#asyncAttach(java.lang.String)
     */
    @Override
    public Response asyncAttach(String uuid) {
        Response response = new Response();
        try {
            String tenantId = TenantContextHolder.getTenantId();
            IgnoreLoginUtils.login(tenantId, tenantId);

            selfPublicityAsyncService.asyncAttach(uuid);
            response.setCode(1);
            response.setMsg("success!");
        } catch (Exception e) {
            logger.info(e.getMessage());
            response.setCode(-1);
            response.setMsg(e.getMessage());
        } finally {
            IgnoreLoginUtils.logout();
        }
        return response;
    }

}
