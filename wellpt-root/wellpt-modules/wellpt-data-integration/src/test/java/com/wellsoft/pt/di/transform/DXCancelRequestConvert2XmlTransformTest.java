package com.wellsoft.pt.di.transform;

import com.wellsoft.pt.integration.request.DXCancelRequest;
import org.junit.Assert;
import org.junit.Test;

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
public class DXCancelRequestConvert2XmlTransformTest {

    @Test
    public void transform() throws Exception {
        DXCancelRequest request = new DXCancelRequest();
        request.setBatchId(UUID.randomUUID().toString());
        request.setDataId(UUID.randomUUID().toString());
        request.setFromId("xxxx");
        request.setMsg("测试注销请求信息");
        request.setRecVer(0);
        request.setUnitId("U111111111111");

        String xml = new DXCancelRequestConvert2XmlTransform().transform(request);
        Assert.assertNotNull(xml);
    }
}