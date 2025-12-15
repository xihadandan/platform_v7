package com.wellsoft.pt.di.transform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.support.Condition;

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
public class XmlConvert2DXShareRequestTransform extends
        AbstractDataTransform<String, ShareRequest> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "XML字符串转为数据交换共享查询请求_v1.0";
    }


    @Override
    public ShareRequest transform(String o) throws Exception {
        //定义DXRequest的xml
        xStream.alias("item", ShareRequest.class);
        xStream.aliasField("TYPEID", ShareRequest.class, "typeId");
        xStream.aliasField("CURRENTPAGE", ShareRequest.class, "currentPage");
        xStream.aliasField("PAGESIZE", ShareRequest.class, "pageSize");
        xStream.aliasField("UNITID", ShareRequest.class, "unitId");
        xStream.aliasField("PARAMS", ShareRequest.class, "params");
        xStream.aliasField("CONDITION", ShareRequest.class, "conditions");
        //解析conditions
        xStream.registerLocalConverter(ShareRequest.class, "conditions", new Converter() {
            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {

            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                List<Condition> conditionList = Lists.newArrayList();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    if ("item".equals(reader.getNodeName())) {
                        Condition condition = new Condition();
                        while (reader.hasMoreChildren()) {
                            reader.moveDown();
                            if ("KEY".equals(reader.getNodeName())) {
                                condition.setKey(reader.getValue());
                            } else if ("VALUE".equals(reader.getNodeName())) {
                                condition.setValue(reader.getValue());
                            } else if ("OPERATOR".equals(reader.getNodeName())) {
                                condition.setOperator(reader.getValue());
                            }
                            reader.moveUp();
                        }
                        conditionList.add(condition);

                    }
                    reader.moveUp();

                }

                return conditionList;
            }

            @Override
            public boolean canConvert(Class type) {
                return true;
            }
        });


        //解析Map对象
        xStream.registerLocalConverter(ShareRequest.class, "params", new Converter() {
            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {

            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                Map<String, String> map = Maps.newHashMap();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    map.put(reader.getNodeName(), reader.getValue());
                    reader.moveUp();
                }

                return map;
            }

            @Override
            public boolean canConvert(Class type) {
                return Map.class.isAssignableFrom(type);
            }
        });

        return (ShareRequest) xStream.fromXML(o);
    }


}
