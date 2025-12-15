package com.wellsoft.pt.di.transform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.integration.request.ShareRequest;
import com.wellsoft.pt.integration.support.Condition;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;

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
public class DXShareRequestConvert2XmlTransformTest {

    @Test
    public void transform() throws Exception {

        ShareRequest request = new ShareRequest();
        request.setCurrentPage(1);
        request.setPageSize(100);
        request.setTypeId("xxx");
        request.setUnitId("U11111");
        Map<String, String> params = Maps.newHashMap();
        params.put("key1", "value1");
        request.setParams(params);
        List<Condition> conditionList = Lists.newArrayList();
        Condition condition = new Condition();
        condition.setKey("con_key");
        condition.setOperator("=");
        condition.setValue("11111");
        conditionList.add(condition);
        request.setConditions(conditionList);

        String xml = new DXShareRequestConvert2XmlTransform().transform(request);
        Assert.notNull(xml);

    }
}