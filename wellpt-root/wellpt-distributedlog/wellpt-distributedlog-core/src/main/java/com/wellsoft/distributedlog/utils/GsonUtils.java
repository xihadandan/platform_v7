package com.wellsoft.distributedlog.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public class GsonUtils {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmssSSS").create();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}
