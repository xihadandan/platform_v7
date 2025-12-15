/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

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
public class AppProductIexportData extends IexportData {

    public AppProduct appProduct;

    /**
     * @param appProduct
     */
    public AppProductIexportData(AppProduct appProduct) {
        this.appProduct = appProduct;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return appProduct.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getName()
     */
    @Override
    public String getName() {
        return appProduct.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppProduct;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return appProduct.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, appProduct);
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
        List<AppProductIntegration> appProductIntegrations = appProductIntegrationService
                .getTopByProductUuid(appProduct.getUuid());
        for (AppProductIntegration appProductIntegration : appProductIntegrations) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.AppProductIntegration).getData(
                    appProductIntegration.getUuid()));
        }
        return dependencies;
    }

}
