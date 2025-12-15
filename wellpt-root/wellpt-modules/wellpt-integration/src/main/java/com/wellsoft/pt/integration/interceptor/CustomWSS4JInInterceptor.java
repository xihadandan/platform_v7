/*
 * @(#)2014-1-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.XMLUtils;
import org.apache.wss4j.dom.WSSecurityEngineResult;
import org.w3c.dom.Element;

import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
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
 * 2014-1-13.1	zhulh		2014-1-13		Create
 * </pre>
 * @date 2014-1-13
 */
public class CustomWSS4JInInterceptor extends WSS4JInInterceptor {
    public static final String CUSTOM_DECRYPT_RESULT = "custom.wss4j.decrypt.result";

    public CustomWSS4JInInterceptor() {
        super();
    }

    public CustomWSS4JInInterceptor(boolean ignore) {
        super(ignore);
    }

    public CustomWSS4JInInterceptor(Map<String, Object> properties) {
        super(properties);
    }

    /**
     * 存储解密后的SOAP内容到消息上下文中
     * <p>
     * (non-Javadoc)
     *
     * @see org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor#doResults(org.apache.cxf.binding.soap.SoapMessage, java.lang.String, javax.xml.soap.SOAPMessage, java.util.Vector, boolean)
     */
    @Override
    protected void doResults(SoapMessage msg, String actor, Element soapHeader, Element soapBody,
                             List<WSSecurityEngineResult> wsResult) throws SOAPException, XMLStreamException, WSSecurityException {
        super.doResults(msg, actor, soapHeader, soapBody, wsResult);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XMLUtils.ElementToStream(soapBody.getOwnerDocument().getDocumentElement(), baos);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        msg.put(CUSTOM_DECRYPT_RESULT, baos);
    }
}
