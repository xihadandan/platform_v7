/*
 * @(#)UTF8StringHttpMessageConverter.java 2012-10-15 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.converter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: HTTP字符串UTF-8编码转换类
 *
 * @author zhulh
 * @date 2012-10-15
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-15.1	zhulh		2012-10-15		Create
 * </pre>
 */
public class UTF8StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {
    // public static final Charset DEFAULT_CHARSET =
    // Charset.forName("ISO-8859-1");
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final List<Charset> availableCharsets;

    private boolean writeAcceptCharset = false;

    public UTF8StringHttpMessageConverter() {
        super(new MediaType("text", "plain", DEFAULT_CHARSET), MediaType.ALL);
        this.availableCharsets = new ArrayList<Charset>(Charset.availableCharsets().values());
    }

    /**
     * Indicates whether the {@code Accept-Charset} should be written to any
     * outgoing request.
     * <p>
     * Default is {@code true}.
     */
    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected String readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException {
        Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return FileCopyUtils.copyToString(new InputStreamReader(inputMessage.getBody(), charset));
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#getContentLength(java.lang.Object, org.springframework.http.MediaType)
     */
    @Override
    protected Long getContentLength(String s, MediaType contentType) {
        Charset charset = getContentTypeCharset(contentType);
        try {
            return (long) s.getBytes(charset.name()).length;
        } catch (UnsupportedEncodingException ex) {
            logger.error(ExceptionUtils.getStackTrace(ex));
            // should not occur
            throw new InternalError(ex.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal(java.lang.Object, org.springframework.http.HttpOutputMessage)
     */
    @Override
    protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException {
        if (writeAcceptCharset) {
            outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());
        }
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        FileCopyUtils.copy(s, new OutputStreamWriter(outputMessage.getBody(), charset));
    }

    /**
     * Return the list of supported {@link Charset}.
     * <p>
     * <p>
     * By default, returns {@link Charset#availableCharsets()}. Can be
     * overridden in subclasses.
     *
     * @return the list of accepted charsets
     */
    protected List<Charset> getAcceptedCharsets() {
        return this.availableCharsets;
    }

    /**
     * Get the content type charset
     *
     * @param contentType
     * @return
     */
    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            return contentType.getCharSet();
        } else {
            return DEFAULT_CHARSET;
        }
    }
}