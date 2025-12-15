package com.wellsoft.pt.di.transform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.wellsoft.context.util.xml.converter.MapFormateConverter;
import com.wellsoft.pt.integration.request.DXCallbackRequest;

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
public class DXCallbackRequestConvert2XmlTransform extends

        AbstractDataTransform<DXCallbackRequest, String> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "数据交换回调请求转为XML字符串_v1.0";
    }


    @Override
    public String transform(DXCallbackRequest o) throws Exception {
        xStream.alias("item", DXCallbackRequest.class);
        xStream.aliasField("BATCHID", DXCallbackRequest.class, "batchId");
        xStream.aliasField("CODE", DXCallbackRequest.class, "code");
        xStream.aliasField("MSG", DXCallbackRequest.class, "msg");
        xStream.aliasField("UNITID", DXCallbackRequest.class, "unitId");
        xStream.aliasField("PARAMS", DXCallbackRequest.class, "params");
        xStream.registerConverter(new MapFormateConverter(null, true, false));

        return xStream.toXML(o);
    }


}
