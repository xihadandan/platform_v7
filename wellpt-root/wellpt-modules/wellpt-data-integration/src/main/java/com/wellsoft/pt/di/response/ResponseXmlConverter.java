package com.wellsoft.pt.di.response;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.lang.StringUtils;

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
public class ResponseXmlConverter extends JavaBeanConverter {

    public ResponseXmlConverter(Mapper mapper) {
        super(mapper, Response.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        Response response = (Response) source;
        writer.startNode("CODE");
        writer.setValue(response.getCode() + "");
        writer.endNode();
        writer.startNode("MSG");
        writer.setValue(StringUtils.stripToEmpty(response.getMsg()));
        writer.endNode();

        writer.startNode("BODY");
        Object body = response.getResponseBody();
        if (body == null) {
            writer.setValue("");
        } else {
            if (!(body instanceof String)
                    && !(body instanceof Number)
                    && !(body instanceof Boolean)
                    && !(body instanceof Character)) {//非基础类型
                context.convertAnother(body);
            } else {
                writer.setValue(response.getResponseBody().toString());
            }
        }
        writer.endNode();

    }


}
