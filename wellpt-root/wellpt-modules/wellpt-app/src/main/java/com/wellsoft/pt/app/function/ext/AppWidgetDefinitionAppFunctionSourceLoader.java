/*
 * @(#)Sep 6, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 组件定义
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
public class AppWidgetDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppFunctionService appFunctionService;

    @Autowired
    private AppFunctionSourceManager appFunctionSourceManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.AppWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getAppFunctionSourceByUuid(java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.get(uuid);
        if (appWidgetDefinition != null) {
            appFunctionSources.add(convert2AppFunctionSource(appWidgetDefinition));
            // 组件功能元素
            appFunctionSources.addAll(convertWidgetFunctionElement2AppFunctionSource(appWidgetDefinition));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<AppWidgetDefinition> appWidgetDefinitions = appProductIntegrationMgr.getAllAppWidgetDefinition();
        for (AppWidgetDefinition appWidgetDefinition : appWidgetDefinitions) {
            appFunctionSources.add(convert2AppFunctionSource(appWidgetDefinition));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        AppWidgetDefinition appWidgetDefinition = (AppWidgetDefinition) item;
        String uuid = appWidgetDefinition.getUuid();
        String fullName = appWidgetDefinition.getName() + "_" + appWidgetDefinition.getTitle();
        String name = "组件定义_" + fullName;
        String id = appWidgetDefinition.getId();
        String code = id.hashCode() + StringUtils.EMPTY;
        String category = getAppFunctionType();
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(IdEntity.UUID, uuid);
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, true,
                extras);
    }

    /**
     * @param appWidgetDefinition
     * @return
     */
    private Collection<? extends AppFunctionSource> convertWidgetFunctionElement2AppFunctionSource(
            AppWidgetDefinition appWidgetDefinition) {
        List<AppFunctionSource> appFunctionSources = Lists.newArrayList();
        // 根据组件定义实例化组件对象
        Widget widget = WidgetDefinitionUtils.parseWidget(appWidgetDefinition.getDefinitionJson());
        // 获取组件配置的功能元素
        WidgetConfiguration widgetConfiguration = widget.getConfiguration();
        List<FunctionElement> functionElements = widgetConfiguration.getFunctionElements();
        for (FunctionElement functionElement : functionElements) {
            appFunctionSources.addAll(convertFunctionElement2AppFunctionSource(functionElement, appWidgetDefinition));
        }
        return appFunctionSources;
    }

    /**
     * @param functionElement
     * @param appWidgetDefinition
     * @return
     */
    private Collection<AppFunctionSource> convertFunctionElement2AppFunctionSource(FunctionElement functionElement,
                                                                                   AppWidgetDefinition appWidgetDefinition) {
        List<AppFunctionSource> appFunctionSources = Lists.newArrayList();
        String entityUuid = functionElement.getUuid();
        String appFunctionType = functionElement.getFunctionType();
        boolean isRef = functionElement.isRef();
        // 1、组件引用的平台资源功能
        if (isRef && StringUtils.isNotBlank(entityUuid) && StringUtils.isNotBlank(appFunctionType)) {
            appFunctionSources.addAll(appFunctionSourceManager.getAppFunctionSources(entityUuid, appFunctionType));
        } else {
            // 2、组件元素功能
            String uuid = DigestUtils.md5Hex(functionElement.getUuid() + appWidgetDefinition.getId());
            String tile = StringUtils.isNotBlank(appWidgetDefinition.getTitle()) ? appWidgetDefinition.getTitle() : appWidgetDefinition.getName();
            String fullName = tile + "_" + functionElement.getName();
            String name = "组件功能元素_" + fullName;
            String id = functionElement.getId();
            String code = functionElement.getCode();
            String category = AppFunctionType.AppWidgetFunctionElement;
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put(IdEntity.UUID, uuid);
            AppFunctionSource source = new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null,
                    category, false, category, true, extras);
            appFunctionSources.add(source);
        }
        return appFunctionSources;
    }

}
