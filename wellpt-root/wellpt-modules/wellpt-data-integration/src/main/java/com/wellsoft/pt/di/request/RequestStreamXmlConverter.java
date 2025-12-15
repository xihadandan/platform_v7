package com.wellsoft.pt.di.request;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import javax.activation.DataHandler;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
public class RequestStreamXmlConverter extends JavaBeanConverter {

    public RequestStreamXmlConverter(Mapper mapper) {
        super(mapper, RequestStream.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        RequestStream item = (RequestStream) source;

        DataHandler dataHandler = item.getDataHandler();
        if (dataHandler != null) {
            context.convertAnother(dataHandler);
        }

    }
}
