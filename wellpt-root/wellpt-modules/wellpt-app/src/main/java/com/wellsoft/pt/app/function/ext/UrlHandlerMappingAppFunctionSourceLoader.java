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
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: URL请求映射处理类
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
public class UrlHandlerMappingAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.URL;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        HttpServletRequest request = JsonDataServicesContextHolder.getRequest();
        WebApplicationContext wac = RequestContextUtils.getWebApplicationContext(request);
        Map<String, HandlerMapping> handerMappings = wac.getBeansOfType(HandlerMapping.class);
        for (Entry<String, HandlerMapping> entry : handerMappings.entrySet()) {
            HandlerMapping handlerMapping = entry.getValue();
            // URL请求映射处理类
            if (AbstractUrlHandlerMapping.class.isAssignableFrom(handlerMapping.getClass())) {
                appFunctionSources
                        .addAll(getUrlHandlerMappingFunctionSources((AbstractUrlHandlerMapping) handlerMapping));
            }
        }
        return appFunctionSources;
    }

    /**
     * URL请求映射处理类
     *
     * @param handlerMapping
     * @return
     */
    private Collection<? extends AppFunctionSource> getUrlHandlerMappingFunctionSources(
            AbstractUrlHandlerMapping handlerMapping) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Map<String, Object> handlerMap = handlerMapping.getHandlerMap();
        for (Entry<String, Object> entry : handlerMap.entrySet()) {
            String url = entry.getKey();
            Object handler = entry.getValue();

            String uuid = DigestUtils.md5Hex(url);
            String fullName = url;
            String name = "URL请求功能_" + url;
            String id = url;
            String code = url.hashCode() + StringUtils.EMPTY;
            String category = getAppFunctionType();
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("urlHandler", handler.getClass().getCanonicalName());
            extras.put("url", url);
            appFunctionSources.add(new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                    false, category, false, extras));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getObjectIdentities(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        String url = appFunctionSource.getId();
        if (StringUtils.isBlank(url)) {
            return super.getObjectIdentities(appFunctionSource);
        }

        List<Object> objectIdentities = new ArrayList<Object>();
        String tempUrl1 = StringUtils.trim(url);
        String tempUrl2 = HandlerMappingUtils.getRequestMappingUrl(tempUrl1);
        if (!StringUtils.equals(tempUrl1, tempUrl2)) {
            objectIdentities.add(tempUrl1);
        }
        objectIdentities.add(tempUrl2);
        return objectIdentities;
    }

}
