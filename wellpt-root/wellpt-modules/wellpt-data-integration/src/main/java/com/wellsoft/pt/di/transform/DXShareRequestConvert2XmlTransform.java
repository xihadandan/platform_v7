package com.wellsoft.pt.di.transform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.wellsoft.context.util.xml.converter.MapFormateConverter;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.support.Condition;

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
public class DXShareRequestConvert2XmlTransform extends AbstractDataTransform<Object, String> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "数据交换共享查询请求转为XML字符串_v1.0";
    }


    @Override
    public String transform(Object o) throws Exception {
        //定义DXRequest的xml
        xStream.alias("item", ShareRequest.class);
        xStream.aliasField("TYPEID", ShareRequest.class, "typeId");
        xStream.aliasField("CURRENTPAGE", ShareRequest.class, "currentPage");
        xStream.aliasField("PAGESIZE", ShareRequest.class, "pageSize");
        xStream.aliasField("UNITID", ShareRequest.class, "unitId");
        xStream.aliasField("PARAMS", ShareRequest.class, "params");
        xStream.aliasField("CONDITION", ShareRequest.class, "conditions");
        xStream.alias("item", Condition.class);
        xStream.aliasField("KEY", Condition.class, "key");
        xStream.aliasField("VALUE", Condition.class, "value");
        xStream.aliasField("OPERATOR", Condition.class, "operator");
        xStream.registerConverter(new MapFormateConverter(null, false, false));
        return xStream.toXML(o);
    }

}
