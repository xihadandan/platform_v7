/*
 * @(#)2014-10-29 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.service.impl;

import com.wellsoft.pt.api.domain.RequestParam;
import com.wellsoft.pt.api.service.WellptSoapWebService;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-29.1	zhulh		2014-10-29		Create
 * </pre>
 * @date 2014-10-29
 */
@WebService
@SOAPBinding(style = Style.RPC)
public class WellptSoapWebServiceImpl extends WellptWebServiceImpl implements WellptSoapWebService {

    @Resource
    private WebServiceContext context;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.service.WellptSoapWebService#execute(com.wellsoft.pt.api.domain.RequestParam)
     */
    @Override
    @WebMethod
    public String execute(RequestParam param) {
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;
        if (context != null) {
            httpServletRequest = (HttpServletRequest) context.getMessageContext().get(
                    AbstractHTTPDestination.HTTP_REQUEST);
            httpServletResponse = (HttpServletResponse) context.getMessageContext().get(
                    AbstractHTTPDestination.HTTP_RESPONSE);
        }
        return super.execute(param, null, httpServletRequest, httpServletResponse);
    }

}
