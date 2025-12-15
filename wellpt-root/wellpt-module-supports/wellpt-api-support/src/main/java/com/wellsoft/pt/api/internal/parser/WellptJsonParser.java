/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.parser;

import com.wellsoft.pt.api.WellptResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class WellptJsonParser<T extends WellptResponse> implements WellptParser<T> {
    protected static Logger LOG = LoggerFactory.getLogger(WellptJsonParser.class);
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    private Class<T> responseClass;

    /**
     * @param responseClass
     */
    public WellptJsonParser(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    public static String object2Json(Object object) {
        StringWriter writer = new StringWriter();
        try {
            objectMapper.writeValue(writer, object);
        } catch (JsonGenerationException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        } catch (JsonMappingException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }
        return writer.toString();
    }

    /**
     * @param src
     * @param cls
     */
    public static <T extends Object> T json2Object(String src, Class<T> cls) {
        try {
            return objectMapper.readValue(src, cls);
        } catch (JsonParseException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        } catch (JsonMappingException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.internal.parser.WellptParser#getResponseClass()
     */
    @Override
    public Class<T> getResponseClass() {
        return responseClass;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.internal.parser.WellptParser#parse(java.lang.String)
     */
    @Override
    public T parse(String rsp) {
        try {
            return objectMapper.readValue(rsp.getBytes("UTF-8"), responseClass);
        } catch (Exception e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }

        return null;
    }
}
