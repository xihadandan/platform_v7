package com.wellsoft.pt.di.transform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.wellsoft.pt.integration.request.DXCancelRequest;

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
public class DXCancelRequestConvert2XmlTransform extends
        AbstractDataTransform<DXCancelRequest, String> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "数据交换注销请求转为XML字符串_v1.0";
    }


    @Override
    public String transform(DXCancelRequest o) throws Exception {
        xStream.alias("item", DXCancelRequest.class);
        xStream.aliasField("BATCHID", DXCancelRequest.class, "batchId");
        xStream.aliasField("DATAID", DXCancelRequest.class, "dataId");
        xStream.aliasField("FROMID", DXCancelRequest.class, "fromId");
        xStream.aliasField("MSG", DXCancelRequest.class, "msg");
        xStream.aliasField("RECVER", DXCancelRequest.class, "recVer");
        xStream.aliasField("UNITID", DXCancelRequest.class, "unitId");

        return xStream.toXML(o);
    }

}
