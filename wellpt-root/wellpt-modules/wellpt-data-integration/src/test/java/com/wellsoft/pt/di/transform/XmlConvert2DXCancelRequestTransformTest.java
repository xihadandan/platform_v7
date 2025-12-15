package com.wellsoft.pt.di.transform;

import com.wellsoft.pt.integration.request.DXCancelRequest;
import org.junit.Assert;
import org.junit.Test;

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
public class XmlConvert2DXCancelRequestTransformTest {

    @Test
    public void transform() throws Exception {
        DXCancelRequest request = new XmlConvert2DXCancelRequestTransform().transform(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "\n" +
                        "<item>\n" +
                        "  <BATCHID>b709b5be-8ae9-4e88-96c5-63ccc0c84f2d</BATCHID>\n" +
                        "  <DATAID>e33c6059-b759-4714-9683-20f9f104afcd</DATAID>\n" +
                        "  <RECVER>0</RECVER>\n" +
                        "  <FROMID>xxxx</FROMID>\n" +
                        "  <unitId>U111111111111</unitId>\n" +
                        "  <MSG>测试注销请求信息</MSG>\n" +
                        "</item>");
        Assert.assertEquals("b709b5be-8ae9-4e88-96c5-63ccc0c84f2d", request.getBatchId());
    }
}