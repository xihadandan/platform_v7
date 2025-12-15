package com.wellsoft.context.web.converter;


import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.Assert;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/26    chenq		2019/11/26		Create
 * </pre>
 */
public class MappingCodehausJacksonHttpMessageConverter extends
        AbstractGenericHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final MediaType TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
    protected ObjectMapper objectMapper;
    private String jsonPrefix;
    private Boolean prettyPrint;

    private PrettyPrinter ssePrettyPrinter;


    public MappingCodehausJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        init(objectMapper);
    }

    public MappingCodehausJacksonHttpMessageConverter(ObjectMapper objectMapper,
                                                      MediaType supportedMediaType) {
        super(supportedMediaType);
        init(objectMapper);
    }

    public MappingCodehausJacksonHttpMessageConverter(ObjectMapper objectMapper,
                                                      MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
        init(objectMapper);
    }

    protected void init(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setDefaultCharset(DEFAULT_CHARSET);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultPrettyPrinter.Lf2SpacesIndenter());
        this.ssePrettyPrinter = prettyPrinter;
    }

    /**
     * Return the underlying {@code ObjectMapper} for this view.
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    /**
     * Set the {@code ObjectMapper} for this view.
     * If not set, a default {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further
     * control of the JSON serialization process. For example, an extended
     * {@link com.fasterxml.jackson.databind.ser.SerializerFactory}
     * can be configured that provides custom serializers for specific types.
     * The other option for refining the serialization process is to use Jackson's
     * provided annotations on the types to be serialized, in which case a
     * custom-configured ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        configurePrettyPrint();
    }

    /**
     * Whether to use the {@link DefaultPrettyPrinter} when writing JSON.
     * This is a shortcut for setting up an {@code ObjectMapper} as follows:
     * <pre class="code">
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
     * converter.setObjectMapper(mapper);
     * </pre>
     */
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        }
    }


    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        if (!canRead(mediaType)) {
            return false;
        }
        JavaType javaType = getJavaType(type, contextClass);
        AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canDeserialize(javaType)) {
            return true;
        }
        logWarningIfNecessary(type, causeRef.get());
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!canWrite(mediaType)) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        if (this.objectMapper.canSerialize(clazz)) {
            return true;
        }
        logWarningIfNecessary(clazz, causeRef.get());
        return false;
    }

    /**
     * Determine whether to log the given exception coming from a
     * {@link ObjectMapper#canDeserialize} / {@link ObjectMapper#canSerialize} check.
     *
     * @param type  the class that Jackson tested for (de-)serializability
     * @param cause the Jackson-thrown exception to evaluate
     *              (typically a {@link JsonMappingException})
     * @since 4.3
     */
    protected void logWarningIfNecessary(Type type, Throwable cause) {
        if (cause == null) {
            return;
        }

        // Do not log warning for serializer not found (note: different message wording on Jackson 2.9)
        boolean debugLevel = (cause instanceof JsonMappingException &&
                (cause.getMessage().startsWith("Can not find") || cause.getMessage().startsWith(
                        "Cannot find")));

        if (debugLevel ? logger.isDebugEnabled() : logger.isWarnEnabled()) {
            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") +
                    "serialization for type [" + type + "]";
            if (debugLevel) {
                logger.debug(msg, cause);
            } else if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(),
                    ex);
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator =
                this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(),
                        encoding);

        // A workaround for JsonGenerators not applying serialization features
        // https://github.com/FasterXML/jackson-databind/issues/12
        if (this.objectMapper.getSerializationConfig().isEnabled(
                SerializationConfig.Feature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(),
                    ex);
        }
    }

    @Override
    protected void writeInternal(Object object, Type type,
                                 HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        JsonGenerator generator = this.objectMapper.getJsonFactory().createJsonGenerator(
                outputMessage.getBody(), encoding);
        try {
            writePrefix(generator, object);

            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;

            if (type != null && value != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = getJavaType(type, null);
            }
            ObjectWriter objectWriter = (serializationView != null ?
                    this.objectMapper.writerWithView(
                            serializationView) : this.objectMapper.writer());

            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.withType(javaType);
            }
            SerializationConfig config = this.objectMapper.getSerializationConfig();
            if (contentType != null && contentType.isCompatibleWith(TEXT_EVENT_STREAM) &&
                    config.isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.withPrettyPrinter(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, value);
            writeSuffix(generator, object);
            generator.flush();
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException(
                    "Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("deprecation")
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
        String jsonpFunction =
                (object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
        if (jsonpFunction != null) {
            generator.writeRaw("/**/");
            generator.writeRaw(jsonpFunction + "(");
        }
    }

    @SuppressWarnings("deprecation")
    protected void writeSuffix(JsonGenerator generator,
                               Object object) throws IOException {
        String jsonpFunction =
                (object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getJsonpFunction() : null);
        if (jsonpFunction != null) {
            generator.writeRaw(");");
        }
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        return (contextClass != null) ?
                TypeFactory.type(type, TypeFactory.type(contextClass)) :
                TypeFactory.type(type);
    }

    private ResolvableType resolveVariable(TypeVariable<?> typeVariable,
                                           ResolvableType contextType) {
        ResolvableType resolvedType;
        if (contextType.hasGenerics()) {
            resolvedType = ResolvableType.forType(typeVariable, contextType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }

        ResolvableType superType = contextType.getSuperType();
        if (superType != ResolvableType.NONE) {
            resolvedType = resolveVariable(typeVariable, superType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        for (ResolvableType ifc : contextType.getInterfaces()) {
            resolvedType = resolveVariable(typeVariable, ifc);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        return ResolvableType.NONE;
    }

    /**
     * Determine the JSON encoding to use for the given content type.
     *
     * @param contentType the media type as requested by the caller
     * @return the JSON encoding to use (never {@code null})
     */
    protected JsonEncoding getJsonEncoding(MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            Charset charset = contentType.getCharset();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

    @Override
    protected MediaType getDefaultContentType(Object object) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }
        return super.getDefaultContentType(object);
    }

    @Override
    protected Long getContentLength(Object object, MediaType contentType) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }
        return super.getContentLength(object, contentType);
    }

}