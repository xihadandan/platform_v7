package com.wellsoft.pt.integration.interceptor;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.databinding.DataWriter;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.FaultOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.FaultInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.staxutils.W3CDOMStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.stream.XMLStreamWriter;

public class CustomFaultOutInterceptor extends FaultOutInterceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleMessage(Message message) throws Fault {
        Fault f = (Fault) message.getContent(Exception.class);
        if (f == null) {
            return;
        }
        // f.printStackTrace();
        Throwable cause = getRootCause(f.getCause());
        if (cause == null) {
            return;
        }
        // cause.printStackTrace();
        BindingOperationInfo bop = (BindingOperationInfo) message.getExchange().get(BindingOperationInfo.class);
        if (bop == null) {
            return;
        }
        FaultInfo fi = getFaultForClass(bop, cause.getClass());
        if ((cause instanceof Exception) && (fi != null)) {
            Exception ex = (Exception) cause;
            Object bean = getFaultBean(cause, fi, message);
            Service service = (Service) message.getExchange().get(Service.class);
            MessagePartInfo part = (MessagePartInfo) fi.getMessageParts().iterator().next();
            DataBinding db = service.getDataBinding();
            try {
                if (isDOMSupported(db)) {
                    DataWriter<Node> writer = db.createWriter(Node.class);
                    if (f.hasDetails()) {
                        writer.write(bean, part, f.getDetail());
                    } else {
                        writer.write(bean, part, f.getOrCreateDetail());
                        if (!(f.getDetail().hasChildNodes())) {
                            f.setDetail(null);
                        }
                    }
                } else if (f.hasDetails()) {
                    XMLStreamWriter xsw = new W3CDOMStreamWriter(f.getDetail());
                    DataWriter<XMLStreamWriter> writer = db.createWriter(XMLStreamWriter.class);
                    writer.write(bean, part, xsw);
                } else {
                    XMLStreamWriter xsw = new W3CDOMStreamWriter(f.getOrCreateDetail());
                    DataWriter<XMLStreamWriter> writer = db.createWriter(XMLStreamWriter.class);
                    writer.write(bean, part, xsw);
                    if (!(f.getDetail().hasChildNodes())) {
                        f.setDetail(null);
                    }
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append(" from service: [");
                buffer.append(ExceptionUtils.getStackTrace(ex));
                buffer.append("]");
                f.setMessage(buffer.toString());
                logger.error(buffer.toString());
            } catch (Exception fex) {
                logger.info("EXCEPTION_WHILE_WRITING_FAULT", fex);
            }
        } else {
            String config = (String) message.getContextualProperty("exceptionMessageCauseEnabled");
            if ((config != null) && (Boolean.valueOf(config).booleanValue())) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(" from service: [");
                buffer.append(ExceptionUtils.getStackTrace(cause));
                buffer.append("]");
                //buffer.append(cause.getMessage());
                f.setMessage(buffer.toString());
                logger.error(buffer.toString());
            }
        }
    }

    private boolean isDOMSupported(DataBinding db) {
        boolean supportsDOM = false;
        for (Class<?> c : db.getSupportedWriterFormats()) {
            if (c.equals(Node.class)) {
                supportsDOM = true;
            }
        }
        return supportsDOM;
    }

    private Throwable getRootCause(Throwable cause) {
        Throwable throwAble = cause;
        while (throwAble.getCause() != null) {
            throwAble = throwAble.getCause();
        }
        return throwAble;
    }

}
