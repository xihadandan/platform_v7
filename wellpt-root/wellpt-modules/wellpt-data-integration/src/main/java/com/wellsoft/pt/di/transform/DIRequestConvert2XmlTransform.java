package com.wellsoft.pt.di.transform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wellsoft.context.util.xml.converter.DataHandlerBase64Converter;
import com.wellsoft.context.util.xml.converter.DateFormateConverter;
import com.wellsoft.context.util.xml.converter.MapFormateConverter;
import com.wellsoft.pt.di.request.DIRequest;
import com.wellsoft.pt.di.request.RequestDataItemXmlConverter;
import com.wellsoft.pt.di.request.RequestStreamXmlConverter;
import com.wellsoft.pt.di.request.RequestXmlConverter;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/7    chenq		2019/8/7		Create
 * </pre>
 */
public class DIRequestConvert2XmlTransform extends AbstractDataTransform<DIRequest, String> {


    @Override
    public String name() {
        return "数据交换请求转XML字符串_v2.0";
    }


    @Override
    public String transform(DIRequest o) throws Exception {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("DATALIST", List.class);
        xStream.alias("REQUEST", DIRequest.class);
        xStream.registerConverter(new DateFormateConverter("yyyy-MM-dd HH:mm:ss"));
        xStream.alias("ITEM", Map.class);
        xStream.registerConverter(new RequestXmlConverter(null));
        xStream.registerConverter(new MapFormateConverter(null, true, true));
        xStream.registerConverter(new DataHandlerBase64Converter());
        xStream.registerConverter(new RequestDataItemXmlConverter(null));
        xStream.registerConverter(new RequestStreamXmlConverter(null));
        return xStream.toXML(o);
    }


}
