package com.wellsoft.pt.di.request;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/14    chenq		2019/8/14		Create
 * </pre>
 */
public class RequestXmlConverter extends JavaBeanConverter {

    public RequestXmlConverter(Mapper mapper) {
        super(mapper, DIRequest.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        DIRequest request = (DIRequest) source;
        RequestHeader requestHeader = request.getHeader();
        Set<String> hks = requestHeader.keySet();
        for (String k : hks) {
            writer.startNode(k.toUpperCase());
            writer.setValue((String) requestHeader.get(k));
            writer.endNode();
        }

        Object body = request.getBody();
        writer.startNode("BODY");
        context.convertAnother(body);
        writer.endNode();
    }


}
