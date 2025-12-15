package com.wellsoft.context.util.encode;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Jackson的简单封装.
 *
 * @author lilin
 */
public class JsonBinder {

    private static Logger logger = LoggerFactory.getLogger(JsonBinder.class);

    private ObjectMapper mapper;

    /**
     * 创建JsonBinder
     *
     * @param inclusion 查看{@link Inclusion}
     */
    @SuppressWarnings("deprecation")
    public JsonBinder(Inclusion inclusion) {
        mapper = new ObjectMapper();
        // 设置输出包含的属性
        mapper.getSerializationConfig().setSerializationInclusion(inclusion);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.getDeserializationConfig()
                .set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);
    }

    /**
     * 创建输出全部属性到Json字符串的Binder.
     *
     * @return {@link JsonBinder}
     */
    public static JsonBinder buildNormalBinder() {
        return new JsonBinder(Inclusion.ALWAYS);
    }

    /**
     * 创建只输出非空属性到Json字符串的Binder.
     *
     * @return {@link JsonBinder}
     */
    public static JsonBinder buildNonNullBinder() {
        return new JsonBinder(Inclusion.NON_NULL);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Binder.
     *
     * @return {@link JsonBinder}
     */
    public static JsonBinder buildNonDefaultBinder() {
        return new JsonBinder(Inclusion.NON_DEFAULT);
    }

    /**
     * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
     * <p>
     * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句: List<MyBean> beanList =
     * binder.getMapper().readValue(listString, new
     * TypeReference<List<MyBean>>() {});
     *
     * @param jsonString json字符串
     * @param clazz      对象类定义
     * @return 返回实体
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
     *
     * @param object 转换实体
     * @return 转换后json
     */
    public String toJson(Object object) {

        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 设置转换日期类型的format pattern,如果不设置默认打印Timestamp毫秒数.
     *
     * @param pattern 日期格式
     */
    public void setDateFormat(String pattern) {
        if (StringUtils.isNotBlank(pattern)) {
            DateFormat df = new SimpleDateFormat(pattern);
            mapper.getSerializationConfig().setDateFormat(df);
            mapper.getDeserializationConfig().setDateFormat(df);
        }
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     *
     * @return {@link ObjectMapper}
     */
    public ObjectMapper getMapper() {
        return mapper;
    }
}
