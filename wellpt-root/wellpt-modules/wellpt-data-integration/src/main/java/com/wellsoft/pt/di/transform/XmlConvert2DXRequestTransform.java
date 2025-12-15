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
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.support.DXDataItem;
import com.wellsoft.pt.integration.support.InputStreamDataSource;
import com.wellsoft.pt.integration.support.StreamingData;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;

import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
public class XmlConvert2DXRequestTransform extends AbstractDataTransform<String, DXRequest> {
    XStream xStream = new XStream(new Dom4JDriver());


    @Override
    public String name() {
        return "XML字符串转为数据交换发送请求对象_v1.0";
    }


    @Override
    public DXRequest transform(String o) throws Exception {
        xStream.alias("item", DXRequest.class);

        //解析DXRequest对象
        xStream.registerConverter(new Converter() {
            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {
            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                DXRequest result = new DXRequest();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    String nodeName = reader.getNodeName();
                    if ("FROM".equals(nodeName)) {
                        result.setFrom(reader.getValue());
                    } else if ("TO".equals(nodeName)) {
                        result.setTo(reader.getValue());
                    } else if ("CC".equals(nodeName)) {
                        result.setCc(reader.getValue());
                    } else if ("BCC".equals(nodeName)) {
                        result.setBcc(reader.getValue());
                    } else if ("BATCHID".equals(nodeName)) {
                        result.setBatchId(reader.getValue());
                    } else if ("TYPEID".equals(nodeName)) {
                        result.setTypeId(reader.getValue());
                    } else if ("DATALIST".equals(nodeName)) {
                        //转向解析DXDataItem对象
                        result.setDataList(
                                (List<DXDataItem>) context.convertAnother(reader.getValue(),
                                        DXDataItem.class));
                    }
                    reader.moveUp();
                }

                return result;
            }

            @Override
            public boolean canConvert(Class type) {
                return DXRequest.class.isAssignableFrom(type);
            }
        });


        //解析DXDataItem对象
        xStream.registerConverter(new Converter() {

            @Override
            public boolean canConvert(Class type) {
                return DXDataItem.class.isAssignableFrom(type);
            }

            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {

            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                List<DXDataItem> itemList = Lists.newArrayList();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    DXDataItem dxDataItem = new DXDataItem();
                    if ("item".equals(reader.getNodeName())) {


                        while (reader.hasMoreChildren()) {
                            reader.moveDown();
                            String node = reader.getNodeName();
                            if ("DATAID".equals(node)) {
                                dxDataItem.setDataId(reader.getValue());
                            } else if ("RECVER".equals(node)) {
                                dxDataItem.setRecVer(StringUtils.isNotBlank(
                                        reader.getValue()) ? Integer.parseInt(
                                        reader.getValue()) : 0);
                            } else if ("PARAMS".equals(node)) {
                                //转向解析Map对象
                                dxDataItem.setParams(
                                        (Map<String, String>) context.convertAnother(
                                                reader.getValue(), Map.class));
                            } else if ("TEXT".equals(node)) {
                                dxDataItem.setText(StringUtils.trim(reader.getValue()));
                            } else if ("STREAMINGDATAS".equals(node)) {
                                //转向解析StreamingData对象
                                dxDataItem.setStreamingDatas(
                                        (List<StreamingData>) context.convertAnother(
                                                reader.getValue(),
                                                StreamingData.class));
                            }

                            reader.moveUp();

                        }


                    }

                    reader.moveUp();
                    itemList.add(dxDataItem);

                }

                return itemList;
            }
        });

        //解析Map对象
        xStream.registerConverter(new Converter() {
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

        //解析StreamingData对象
        xStream.registerConverter(new Converter() {
            @Override
            public void marshal(Object source, HierarchicalStreamWriter writer,
                                MarshallingContext context) {

            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

                List<StreamingData> itemList = Lists.newArrayList();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    StreamingData streamingData = new StreamingData();
                    if ("item".equals(reader.getNodeName())) {
                        while (reader.hasMoreChildren()) {
                            reader.moveDown();
                            String node = reader.getNodeName();
                            if ("DATAHANDLER".equals(node)) {

                                try {
                                    BASE64Decoder decoder = new BASE64Decoder();
                                    byte[] bytes = decoder.decodeBuffer(reader.getValue());
                                    InputStream in = new ByteArrayInputStream(bytes);
                                    streamingData.setDataHandler(
                                            new DataHandler(new InputStreamDataSource(in,
                                                    "octet-stream")));
                                } catch (Exception e) {
                                    logger.error("二级制码解析流异常：", e);
                                    throw new RuntimeException(e);
                                }

                            } else if ("FILENAME".equals(node)) {
                                streamingData.setFileName(reader.getValue());
                            }

                            reader.moveUp();

                        }


                    }
                    reader.moveUp();
                    itemList.add(streamingData);

                }

                return itemList;
            }

            @Override
            public boolean canConvert(Class type) {
                return StreamingData.class.isAssignableFrom(type);
            }
        });


        return (DXRequest) xStream.fromXML(o);
    }


}
