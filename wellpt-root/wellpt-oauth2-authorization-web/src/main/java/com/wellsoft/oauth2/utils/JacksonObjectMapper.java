package com.wellsoft.oauth2.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class JacksonObjectMapper extends ObjectMapper {

    /**
     *
     */
    private static final long serialVersionUID = 6773224424978770429L;

    public JacksonObjectMapper() {
        super();

        this.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 设置为中国上海时区
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // //空值不序列化
        // this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        this.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        this.configure(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS, true);

        // 从JSON到java object
        // 没有匹配的属性名称时不作失败处理
        this.configure(MapperFeature.AUTO_DETECT_FIELDS, true);

        // 反序列化
        // 禁止遇到空原始类型时抛出异常，用默认值代替。
        this.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        // 禁止遇到未知（新）属性时报错，支持兼容扩展
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        this.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        // 序列化
        // 禁止序列化空值
        this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        this.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        // this.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
        // true);
        this.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);
        this.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        // 不包含空值属性(空值属性是否显示属性key)
        this.setSerializationInclusion(Include.NON_NULL);

        // this.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME,
        // true);
        // 是否缩放排列输出，默认false，有些场合为了便于排版阅读则需要对输出做缩放排列
        this.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

}
