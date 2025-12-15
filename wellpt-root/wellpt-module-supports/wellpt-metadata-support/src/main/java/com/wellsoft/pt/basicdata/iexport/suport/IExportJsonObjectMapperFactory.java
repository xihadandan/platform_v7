package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.util.ReflectUtil;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonFilter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.hibernate.annotations.Cascade;

import javax.persistence.Transient;
import javax.sql.rowset.serial.SerialClob;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.text.SimpleDateFormat;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月14日   chenq	 Create
 * </pre>
 */
public class IExportJsonObjectMapperFactory {

    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule customModule = new SimpleModule("simpleModule", Version.unknownVersion());
        customModule.addSerializer(Clob.class, new ToStringSerializer() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                try {
                    SerialClob serialClob = (SerialClob) value;
                    if (serialClob != null) {
                        serialClob.getCharacterStream();
                        jgen.writeString(IOUtils.toString(serialClob.getCharacterStream()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        customModule.addDeserializer(Clob.class, new StdScalarDeserializer<SerialClob>(SerialClob.class) {
            @Override
            public SerialClob deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                try {
                    SerialClob clob = new SerialClob(jp.getTextCharacters());
                    return clob;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        customModule.setMixInAnnotation(JpaEntity.class, EntityIgnoreFilterMixIn.class);
        objectMapper.registerModule(customModule);
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("entityIgnoreFilter", new SimpleBeanPropertyFilter.FilterExceptFilter(null) {
            @Override
            public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider provider, BeanPropertyWriter writer) throws Exception {
                Method method = (Method) ReflectUtil.getField(writer, "_accessorMethod");
                if (method.getDeclaredAnnotation(Transient.class) != null
                        || method.getDeclaredAnnotation(Cascade.class) != null) {
                    // 对于实体类对象，非持久化字段与懒加载相关属性对象不予序列化
                    return;
                }
                writer.serializeAsField(bean, jgen, provider);
            }
        });
        objectMapper.setFilters(filterProvider);
        return objectMapper;
    }

    @JsonFilter("entityIgnoreFilter")
    interface EntityIgnoreFilterMixIn {
    }
}
