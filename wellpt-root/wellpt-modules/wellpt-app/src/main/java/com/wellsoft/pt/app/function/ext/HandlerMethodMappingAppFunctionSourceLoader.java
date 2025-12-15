/*
 * @(#)Sep 6, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.context.util.security.HandlerMappingUtils;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 方法注解请求映射处理类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-06.1	zhulh		2016-09-06		Create
 * </pre>
 * @date 2016-09-06
 */
@Service
@Transactional(readOnly = true)
public class HandlerMethodMappingAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    private static final String KEY_PATTERNS = "patterns";

    private static final String KEY_REQUEST_URI = "requestURI";

    private static final String LOGIC_OR = "||";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.Controller;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        HttpServletRequest request = JsonDataServicesContextHolder.getRequest();
        WebApplicationContext wac = RequestContextUtils.getWebApplicationContext(request);
        Map<String, HandlerMapping> handerMappings = wac.getBeansOfType(HandlerMapping.class);
        for (Entry<String, HandlerMapping> entry : handerMappings.entrySet()) {
            HandlerMapping handlerMapping = entry.getValue();
            // 方法注解请求映射处理类
            if (AbstractHandlerMethodMapping.class.isAssignableFrom(handlerMapping.getClass())) {
                appFunctionSources
                        .addAll(getHandlerMethodMappingFunctionSources(
                                (AbstractHandlerMethodMapping<RequestMappingInfo>) handlerMapping));
            }
        }
        return appFunctionSources;
    }

    /**
     * 方法注解请求映射处理类
     *
     * @param handlerMapping
     * @return
     */
    private Collection<? extends AppFunctionSource> getHandlerMethodMappingFunctionSources(
            AbstractHandlerMethodMapping<RequestMappingInfo> handlerMapping) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
        for (Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod value = entry.getValue();
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            String requestURI = info.getPatternsCondition().toString();
            String beanName = value.getBean().toString();
            String methodName = value.getMethod().getName();

            String controllerService = StringUtils.uncapitalize(beanName + "." + methodName);
            String uuid = DigestUtils.md5Hex(controllerService);
            String fullName = ClassUtils.getQualifiedName(
                    value.getBean().getClass()) + "." + methodName;
            String name = "Web控制器功能_" + controllerService;
            String id = controllerService;
            String code = controllerService.hashCode() + StringUtils.EMPTY;
            String action = info.getMethodsCondition().toString();
            String category = getAppFunctionType();
            String remark = com.wellsoft.pt.jpa.util.ClassUtils.getClassMethodDescriptions().get(
                    value.getBeanType().getCanonicalName() + "." + methodName);
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put(KEY_PATTERNS, patterns);
            extras.put(KEY_REQUEST_URI, requestURI);
            appFunctionSources.add(
                    new SimpleAppFunctionSource(uuid, fullName, name, id, code, action, null,
                            category,
                            false, category, false, extras, remark));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getObjectIdentities(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        Map<String, Object> extras = appFunctionSource.getExtras();
        Collection<String> patterns = (Collection<String>) extras.get(KEY_PATTERNS);
        if (CollectionUtils.isEmpty(patterns)) {
            return addFromRequestURI(appFunctionSource);
        }

        List<Object> objectIdentities = new ArrayList<Object>();
        for (String url : patterns) {
            addRequestMappingUrl(objectIdentities, url);
        }
        return objectIdentities;
    }

    /**
     * @param appFunctionSource
     * @return
     */
    private Collection<Object> addFromRequestURI(AppFunctionSource appFunctionSource) {
        Map<String, Object> extras = appFunctionSource.getExtras();
        String requestURI = ObjectUtils.toString(extras.get(KEY_REQUEST_URI), StringUtils.EMPTY);
        if (StringUtils.isBlank(requestURI)) {
            return super.getObjectIdentities(appFunctionSource);
        }

        requestURI = requestURI.substring(1, requestURI.length() - 1);
        if (StringUtils.isBlank(requestURI)) {
            return super.getObjectIdentities(appFunctionSource);
        }

        List<Object> objectIdentities = new ArrayList<Object>();
        if (requestURI.indexOf(LOGIC_OR) != -1) {
            String[] urls = StringUtils.split(requestURI, LOGIC_OR);
            for (String url : urls) {
                addRequestMappingUrl(objectIdentities, url);
            }
        } else {
            addRequestMappingUrl(objectIdentities, requestURI);
        }
        return objectIdentities;
    }

    /**
     * @param objectIdentities
     * @param url
     */
    private void addRequestMappingUrl(List<Object> objectIdentities, String url) {
        String tempUrl1 = StringUtils.trim(url);
        String tempUrl2 = HandlerMappingUtils.getRequestMappingUrl(tempUrl1);
        if (!StringUtils.equals(tempUrl1, tempUrl2)) {
            objectIdentities.add(tempUrl1);
        }
        objectIdentities.add(tempUrl2);
    }

}
