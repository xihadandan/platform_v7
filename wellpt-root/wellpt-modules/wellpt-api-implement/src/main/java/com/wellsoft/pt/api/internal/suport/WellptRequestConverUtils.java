/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.internal.suport;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.domain.RequestParam;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.request.FileUploadRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-11.1	zhulh		2014-8-11		Create
 * </pre>
 * @date 2014-8-11
 */
public class WellptRequestConverUtils {

    private static Logger logger = LoggerFactory.getLogger(WellptRequestConverUtils.class);

    @SuppressWarnings("unchecked")
    public static WellptRequest<?> convert(WellptService service, RequestParam requestParam, Attachment attachment) {
        WellptRequest<?> wellptRequest = null;
        try {
            String requestBody = requestParam.getJson();
            JSONObject jsonObject = new JSONObject(requestBody);
            Method method = getDoServiceMethod(service);
            Class<?> requestParameterType = method.getParameterTypes()[0];
            Class<? extends WellptRequest<?>> requestCls = null;
            if (WellptRequest.class.isAssignableFrom(requestParameterType)) {
                requestCls = (Class<? extends WellptRequest<?>>) requestParameterType;
            } else {
                String apiServiceName = jsonObject.getString("apiServiceName");
                requestCls = RequestResponseClassMappingUtils.getRequestClass(apiServiceName);
            }
            wellptRequest = WellptJsonParser.json2Object(requestBody, requestCls);
            if (FileUploadRequest.class.isAssignableFrom(wellptRequest.getClass())) {
                FileUploadRequest fileUploadApiRequest = (FileUploadRequest) wellptRequest;
                if (attachment != null) {
                    try {
                        fileUploadApiRequest.setInputStream(attachment.getDataHandler().getInputStream());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return wellptRequest;
    }

    /**
     * @param service
     * @return
     */
    private static Method getDoServiceMethod(WellptService service) {
        Method[] methods = ClassUtils.getUserClass(service.getClass()).getMethods();
        for (Method method : methods) {
            if (StringUtils.equals("doService", method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && WellptRequest.class.isAssignableFrom(parameterTypes[0])
                        && !WellptRequest.class.equals(parameterTypes[0])) {
                    return method;
                }
            }
        }
        return null;
    }
}
