/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
public class AppProductIntegrationIexportData extends IexportData {

    public AppProductIntegration appProductIntegration;

    /**
     * @param appProductIntegration
     */
    public AppProductIntegrationIexportData(AppProductIntegration appProductIntegration) {
        this.appProductIntegration = appProductIntegration;
    }

    public AppProductIntegration getAppProductIntegration() {
        return appProductIntegration;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return appProductIntegration.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getName()
     */
    @Override
    public String getName() {
        return AppType.getName(Integer.valueOf(appProductIntegration.getDataType())) + ": "
                + appProductIntegration.getDataName();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppProductIntegration;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return appProductIntegration.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, appProductIntegration);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder
                .getBean(AppProductIntegrationService.class);
        AppPageDefinitionService appPageDefinitionService = ApplicationContextHolder
                .getBean(AppPageDefinitionService.class);
        String appPageUuid = appProductIntegration.getAppPageUuid();
        String dataUuid = appProductIntegration.getDataUuid();
        Integer appType = Integer.valueOf(appProductIntegration.getDataType());

        // 集成信息配置的页面
        if (StringUtils.isNotBlank(appPageUuid)) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppPageDefinition).getData(
                    appPageUuid));
        }

        // 集成信息下的页面
        AppPageDefinition example = new AppPageDefinition();
        example.setAppPiUuid(appProductIntegration.getUuid());
        List<AppPageDefinition> appPageDefinitions = appPageDefinitionService.findByExample(example, "version desc");
        for (AppPageDefinition appPageDefinition : appPageDefinitions) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppPageDefinition).getData(
                    appPageDefinition.getUuid()));
        }

        // 集成信息本身数据依赖
        if (AppType.SYSTEM.equals(appType)) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppSystem).getData(dataUuid));
        }
        if (AppType.MODULE.equals(appType)) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppModule).getData(dataUuid));
        }
        if (AppType.APPLICATION.equals(appType)) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppApplication).getData(dataUuid));
        }
        if (AppType.FUNCTION.equals(appType)) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppFunction).getData(dataUuid));
        }

        // 集成信息子结点
        String uuid = appProductIntegration.getUuid();
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService.getChildren(uuid);
        for (AppProductIntegration child : appProductIntegrations) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppProductIntegration).getData(
                    child.getUuid()));
        }
        return dependencies;
    }

}
