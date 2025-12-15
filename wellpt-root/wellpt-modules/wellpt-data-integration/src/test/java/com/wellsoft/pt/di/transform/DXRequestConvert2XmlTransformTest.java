package com.wellsoft.pt.di.transform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.integration.request.DXRequest;
import com.wellsoft.pt.integration.support.DXDataItem;
import com.wellsoft.pt.integration.support.StreamingData;
import org.junit.Assert;
import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.io.File;
import java.util.UUID;

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
public class DXRequestConvert2XmlTransformTest {

    @Test
    public void transform() throws Exception {

        DXRequest dxRequest = new DXRequest();
        dxRequest.setFrom("厦门行政服务中心");
        dxRequest.setTo("中央行政服务中心");
        dxRequest.setCc("国土OA中心");
        dxRequest.setBcc("国家保密局");
        dxRequest.setTypeId("1");
        dxRequest.setBatchId(UUID.randomUUID().toString());
        DXDataItem dataItem = new DXDataItem();
        dataItem.setDataId(dxRequest.getBatchId());
        dataItem.setRecVer(0);
        dataItem.setText("<item><BLHJ>1111</BLHJ></item><item><BLHJ>2222</BLHJ></item>");
        dataItem.setParams(Maps.<String, String>newHashMap());
        dataItem.getParams().put("key1", "value1");
        dataItem.setStreamingDatas(Lists.<StreamingData>newArrayList());
        StreamingData data = new StreamingData();
        data.setDataHandler(new DataHandler(
                new FileDataSource(new File("C:\\Windows\\System32\\drivers\\etc\\hosts"))));
        dataItem.getStreamingDatas().add(data);
        dxRequest.setDataList(Lists.<DXDataItem>newArrayList());
        dxRequest.getDataList().add(dataItem);

        String xml = new DXRequestConvert2XmlTransform().transform(dxRequest);
        Assert.assertNotNull(xml);

    }
}