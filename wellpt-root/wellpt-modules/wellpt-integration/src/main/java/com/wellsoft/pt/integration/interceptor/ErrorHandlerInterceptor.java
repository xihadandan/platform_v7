/*
 * @(#)2013-11-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-26.1	zhulh		2013-11-26		Create
 * </pre>
 * @date 2013-11-26
 */
public class ErrorHandlerInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
    public ErrorHandlerInterceptor() {
        super(Phase.WRITE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Object code = message.get(Message.RESPONSE_CODE);
        System.out.println(code);
        message.put(Message.RESPONSE_CODE, 200);
        Exception e = message.getContent(Exception.class);
        Fault fault = new Fault(e);
        System.out.println(fault);
        //		WebServiceResponse response = new WebServiceResponse();
        //		response.setMsg("错误码111");
        //		//		Response response = Response
        //		//				.status(Response.Status.BAD_REQUEST)
        //		//				.entity(new ErrorDetail(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST
        //		//						.toString())).build();
        //		message.setContent(WebServiceResponse.class, response);
    }
}
