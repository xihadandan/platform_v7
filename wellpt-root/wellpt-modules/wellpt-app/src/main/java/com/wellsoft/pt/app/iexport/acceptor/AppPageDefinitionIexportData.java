/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
public class AppPageDefinitionIexportData extends IexportData {

    public AppPageDefinition appPageDefinition;

    /**
     * @param appProduct
     */
    public AppPageDefinitionIexportData(AppPageDefinition appPageDefinition) {
        this.appPageDefinition = appPageDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return appPageDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getName()
     */
    @Override
    public String getName() {
        return "页面定义: " + appPageDefinition.getName() + "(" + appPageDefinition.getVersion() + ")";
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppPageDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return appPageDefinition.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, appPageDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        // 1、页面组件
        AppWidgetDefinition example = new AppWidgetDefinition();
        example.setAppPageUuid(this.appPageDefinition.getUuid());
        AppWidgetDefinitionService appWidgetDefinitionService = ApplicationContextHolder
                .getBean(AppWidgetDefinitionService.class);
        List<AppWidgetDefinition> appWidgetDefinitions = appWidgetDefinitionService.findByExample(example);
        // 按时间升序
        Collections.sort(appWidgetDefinitions, IdEntityComparators.CREATE_TIME_ASC);
        // 按组件定义包含关系升序
        Collections.sort(appWidgetDefinitions, new Comparator<AppWidgetDefinition>() {

            @Override
            public int compare(AppWidgetDefinition o1, AppWidgetDefinition o2) {
                if (StringUtils.contains(o1.getDefinitionJson(), o2.getDefinitionJson())) {
                    return 1;
                } else if (StringUtils.contains(o2.getDefinitionJson(), o1.getDefinitionJson())) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        for (AppWidgetDefinition appWidgetDefinition : appWidgetDefinitions) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppWidgetDefinition).getData(
                    appWidgetDefinition.getUuid()));
        }
        // 2、页面引用资源
        AppPageResourceService appPageResourceService = ApplicationContextHolder.getBean(AppPageResourceService.class);
        AppPageResourceEntity entity = new AppPageResourceEntity();
        entity.setAppPageUuid(this.appPageDefinition.getUuid());
        List<AppPageResourceEntity> appPageResourceEntities = appPageResourceService.listByEntity(entity);
        for (AppPageResourceEntity appPageResourceEntity : appPageResourceEntities) {
            if (StringUtils.isNotBlank(appPageResourceEntity.getAppFunctionUuid())) {
                dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppFunction).getData(
                        appPageResourceEntity.getAppFunctionUuid()));
            }
        }
        return dependencies;
    }

}
