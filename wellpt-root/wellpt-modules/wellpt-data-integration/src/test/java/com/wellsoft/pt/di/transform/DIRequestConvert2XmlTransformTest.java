package com.wellsoft.pt.di.transform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.di.request.DIRequest;
import com.wellsoft.pt.di.request.RequestDataItem;
import com.wellsoft.pt.di.request.RequestHeader;
import com.wellsoft.pt.di.request.RequestStream;
import org.junit.Assert;
import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/20    chenq		2019/8/20		Create
 * </pre>
 */
public class DIRequestConvert2XmlTransformTest {

    @Test
    public void transform() throws Exception {

        List<Map<String, Object>> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", i);
            map.put("name", "xxxx" + i);
            Map<String, Object> map1 = Maps.newHashMap();
            map1.put("desc", "1111111");
            map.put("map", map1);


            list.add(map);
        }
        DIRequest request = new DIRequest();
        request.setHeader(new RequestHeader().from("1").to("2").type("test").batchId(
                UuidUtils.createUuid()));
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("BLRMC", "测试人员");
        map.put("DATETIME", new Date());
        HashMap<String, String> innerMap = Maps.newHashMap();
        innerMap.put("iiii", "xxxxxxxxxxxxx");
        map.put("inner", innerMap);

        List<Map<String, Object>> x = Lists.newArrayList();
        Map<String, Object> x1 = Maps.newHashMap();
        x1.put("name", "洛杉矶");
        DataHandler dataHandler = new DataHandler(new FileDataSource("D:/test/bing/bing.html"));
        x1.put("file", dataHandler);
        x.add(x1);
        map.put("listMap", x);


        request.setBody(map);


        DIRequest request1 = new DIRequest();
        RequestDataItem dataItem = new RequestDataItem();
        Map<String, Object> dataItemMap = Maps.newHashMap();
        dataItemMap.put("code", 1);
        dataItemMap.put("name", "测试");
        dataItemMap.put("number", new BigDecimal(11111));
        dataItemMap.put("boolean", true);
        dataItem.setItemList(Lists.newArrayList(dataItemMap));
        RequestStream requestStream = new RequestStream();
        requestStream.setDataHandler(dataHandler);
        requestStream.setFileName(dataHandler.getName());
        dataItem.getStreamingDatas().add(requestStream);
        request1.setBody(dataItem);
        Assert.assertNotNull(new DIRequestConvert2XmlTransform().transform(request1));
    }
}