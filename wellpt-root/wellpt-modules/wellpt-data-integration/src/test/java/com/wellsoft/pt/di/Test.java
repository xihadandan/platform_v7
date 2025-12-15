package com.wellsoft.pt.di;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.wellsoft.context.util.xml.XmlBuilder;
import com.wellsoft.pt.di.request.DIRequest;
import com.wellsoft.pt.di.request.RequestDataItem;
import com.wellsoft.pt.di.request.RequestStream;
import org.apache.cxf.jaxrs.ext.multipart.InputStreamDataSource;

import javax.activation.DataHandler;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Map;

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
public class Test {


    @org.junit.Test
    public void test() {
        String x = "<code>1</code>";
        XStream xStream = new XStream(new JDomDriver());
        String result = xStream.toXML(x);
        Object z = new XmlBuilder().toBean(result);
        System.out.println(result);
    }

    @org.junit.Test
    public void test2() throws Exception {
        DIRequest request1 = new DIRequest();
        request1.getHeader().put("jjjjj", "jjjjjjjjjjjjjjjjjjjj");
        RequestDataItem dataItem = new RequestDataItem();
        Map<String, Object> dataItemMap = Maps.newHashMap();
        dataItemMap.put("code", 1);
        dataItemMap.put("name", "测试");
        dataItemMap.put("number", new BigDecimal(11111));
        dataItemMap.put("boolean", true);
        dataItem.setItemList(Lists.newArrayList(dataItemMap));
        RequestStream requestStream = new RequestStream();

        DataHandler dataHandler = new DataHandler(
                new InputStreamDataSource(new FileInputStream("D:/test/bing/bing.html"), "text"));
        requestStream.setDataHandler(dataHandler);
        requestStream.setFileName(dataHandler.getName());
        dataItem.getStreamingDatas().add(requestStream);
        request1.setBody(dataItem);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return DataHandler.class.isAssignableFrom(
                        aClass) || InputStream.class.isAssignableFrom(
                        aClass) || OutputStream.class.isAssignableFrom(aClass);
            }
        });
        gsonBuilder.setPrettyPrinting();

        XStream xStream = new XStream(new JettisonMappedXmlDriver());
        Stopwatch timer = Stopwatch.createStarted();
        String result = xStream.toXML(request1);

        System.out.println(result);
        System.out.println(timer.stop());
        Object obj = xStream.fromXML(result);
        System.out.println();
    }
}
