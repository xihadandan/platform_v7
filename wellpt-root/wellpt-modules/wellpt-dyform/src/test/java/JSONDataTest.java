import org.json.JSONException;
import org.json.JSONObject;

/*
 * @(#)2020年2月11日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月11日.1	wangrf		2020年2月11日		Create
 * </pre>
 * @date 2020年2月11日
 */
public class JSONDataTest {

    @org.junit.Test
    public void testJSON() throws JSONException {
        //        if (1 == 1) {
        String jsonData = "{\"uuid\":\"1fc6a3c516854ade96d8338f97ce61b3\",\"formUuid\":\"f33102e1-5459-4c90-a5b6-983cb04a02d2\",\"fieldName\":\"t1\",\"fieldValue\":\"1\"}";
        //        }
        JSONObject obj = new JSONObject(jsonData);
        System.out.println(obj.get("uuid"));

    }

    @org.junit.Test
    public void testZZ() {
        String s = "123,456;135,578;12,46";
        ;
        String[] array = s.split(",|;");
        for (String a : array) {
            System.out.println(a);
        }

    }

}
