package com.wellsoft.pt.di.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/13    chenq		2019/8/13		Create
 * </pre>
 */
public class GsonUtils {
    private static Gson PRETTY_GSON = null;
    private static Gson GSON = null;

    static {
        PRETTY_GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        GSON = new GsonBuilder().disableHtmlEscaping().create();
    }


    public static String toJson(Object obj, boolean prettyPrinting) {
        return prettyPrinting ? PRETTY_GSON.toJson(obj) : GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

}
