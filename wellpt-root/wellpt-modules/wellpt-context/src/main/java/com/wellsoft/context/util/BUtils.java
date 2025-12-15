package com.wellsoft.context.util;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;

// 所有常用的工具类集合在这里, 方便使用，因为有一些名字太长了，用起来不方便
public class BUtils {
    public static String DEF_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // 驼峰转下划线
    public static String camel2underline(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    // 下划线转驼峰
    public static String underline2camel(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    // 是否空字符串
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    // 是否非空字符串
    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    // 是否空集
    public static boolean isEmpty(Collection<?> objs) {
        return CollectionUtils.isEmpty(objs);
    }

    // 是否非空集
    public static boolean isNotEmpty(Collection<?> objs) {
        return CollectionUtils.isNotEmpty(objs);
    }

    // 日期转str
    public static String date2str(Date d) {
        return DateUtil.getFormatDate(d, DEF_FORMAT);
    }

    // str转日期
    public static Date str2date(String str) {
        return DateUtil.getFormatDateByStr(str, DEF_FORMAT);
    }

    // 首字母大写
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    // 对象转json字符串
    public static String obj2json(Object obj) {
        return JsonUtils.object2Gson(obj);
    }

    // json字符串转对象
    public static <T> T json2obj(String json, Class<T> clz) {
        return JsonUtils.gson2Object(json, clz);
    }
}
