package com.wellsoft.pt.di.transform;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.wellsoft.context.util.xml.XmlConverUtils;
import com.wellsoft.context.util.xml.converter.DateFormateConverter;
import com.wellsoft.context.util.xml.converter.MapFormateConverter;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.support.DXDataItem;
import com.wellsoft.pt.integration.support.StreamingData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
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
public class DXRequestConvert2XmlTransform extends AbstractDataTransform<Object, String> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "数据交换发送请求转为XML字符串_v1.0";
    }


    @Override
    public String transform(Object o) throws Exception {
        //定义DXRequest的xml
        xStream.alias("item", DXRequest.class);
        xStream.aliasField("FROM", DXRequest.class, "from");
        xStream.aliasField("TO", DXRequest.class, "to");
        xStream.aliasField("CC", DXRequest.class, "cc");
        xStream.aliasField("BCC", DXRequest.class, "bcc");
        xStream.aliasField("TYPEID", DXRequest.class, "typeId");
        xStream.aliasField("BATCHID", DXRequest.class, "batchId");
        xStream.aliasField("DATALIST", DXRequest.class, "dataList");

        //定义DXDataItem的xml
        xStream.alias("item", DXDataItem.class);
        xStream.aliasField("DATAID", DXDataItem.class, "dataId");
        xStream.aliasField("RECVER", DXDataItem.class, "recVer");
        xStream.aliasField("PARAMS", DXDataItem.class, "params");
        xStream.aliasField("TEXT", DXDataItem.class, "text");
        xStream.alias("item", Map.class);
        //text字段转为xml输出
        xStream.registerLocalConverter(DXDataItem.class, "text", new Converter() {
            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {
                String xml = (String) source;
                try {
                    if (StringUtils.isNotBlank(xml)) {
                        List list = XmlConverUtils.xmltoList("<root>" + xml + "</root>");
                        if (list != null)
                            context.convertAnother(list);
                    }
                } catch (Exception e) {
                    logger.warn("格式化输出text的xml结构失败：", e);
                }
                writer.setValue(xml);

            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                return null;
            }

            @Override
            public boolean canConvert(Class type) {
                return true;
            }
        });


        xStream.aliasField("STREAMINGDATAS", DXDataItem.class, "streamingDatas");

        //定义StreamingData的xml
        xStream.alias("item", StreamingData.class);
        xStream.aliasField("DATAID", DXDataItem.class, "dataId");

        xStream.aliasField("STREAMINGDATAS", DXDataItem.class, "streamingDatas");
        xStream.registerLocalConverter(DXDataItem.class, "streamingDatas",
                new StreamingDataXmlConverter());
        xStream.registerConverter(new MapFormateConverter(null, true, false));
        xStream.registerConverter(new DateFormateConverter("yyyy-MM-dd HH:mm:ss"));
        return xStream.toXML(o);
    }


    class StreamingDataXmlConverter implements Converter {
        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer,
                            MarshallingContext context) {
            List<StreamingData> streamingDataList = (List<StreamingData>) source;
            if (CollectionUtils.isNotEmpty(streamingDataList)) {
                for (StreamingData data : streamingDataList) {
                    writer.startNode("item");
                    context.convertAnother(data.getDataHandler(),
                            new DataHandlerXmlConverter());

                    writer.endNode();
                }

            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }

        @Override
        public boolean canConvert(Class type) {
            return true;
        }

        class DataHandlerXmlConverter implements Converter {

            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {
                DataHandler dataHandler = (DataHandler) source;

                try {
                    writer.startNode("DATAHANDLER");
                    BASE64Encoder encoder = new BASE64Encoder();
                    writer.setValue(
                            encoder.encode(IOUtils.toByteArray(
                                    dataHandler.getInputStream())));
                    writer.endNode();
                    writer.startNode("FILENAME");
                    writer.setValue(dataHandler.getName());
                    writer.endNode();
                } catch (Exception e) {
                }
            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                return null;
            }

            @Override
            public boolean canConvert(Class type) {
                return DataHandler.class.isAssignableFrom(type);
            }
        }

    }


}
