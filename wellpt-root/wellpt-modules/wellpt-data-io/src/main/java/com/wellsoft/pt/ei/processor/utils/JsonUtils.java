package com.wellsoft.pt.ei.processor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @Auther: yt
 * @Date: 2021/10/22 14:11
 * @Description:
 */
public class JsonUtils {

    public static SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteNullStringAsEmpty
    };


    public static String toJsonStr(Object object) {
        String jsonStr = JSON.toJSONString(object, features);
        return jsonStr;
    }


}
