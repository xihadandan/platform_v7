/*
 * @(#)Mar 3, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.dms.core.support.DataType;
import org.apache.commons.lang.StringUtils;

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
 * Mar 3, 2017.1	zhulh		Mar 3, 2017		Create
 * </pre>
 * @date Mar 3, 2017
 */
public class ConfigurationBuilder {

    private String dataType;
    private String dataStoreId;

    /**
     * @param dataStoreId
     * @return
     */
    public ConfigurationBuilder setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
        this.dataType = DataType.DATA_STORE.getId();
        return this;
    }

    public Configuration buildFromWidgetDefinition(String dmsId) {
        Configuration configuration = null;
        if (StringUtils.isBlank(dmsId)) {
            return buildEmptyConfiguration();
        }
        if (dmsId.startsWith("wDataManagementViewer") || dmsId.startsWith("wMobileDataManagementViewer")) {
            configuration = WidgetDefinitionUtils.getWidgetConfigurationById(
                    DmsDataManagementViewerConfiguration.class, dmsId);
        }
        if (dmsId.startsWith("wFileManager") || dmsId.startsWith("wMobileFileManager")) {
            configuration = WidgetDefinitionUtils.getWidgetConfigurationById(DmsFileManagerConfiguration.class, dmsId);
        }
        if (dmsId.startsWith("wDocExchanger")) {
            configuration = WidgetDefinitionUtils.getWidgetConfigurationById(DmsDocExchangeManagerConfiguration.class,
                    dmsId);
        }
        return configuration;
    }

    /**
     * @return
     */
    public Configuration buildDataStoreConfiguration() {
        DmsDataManagementViewerConfiguration dmsConfiguration = new DmsDataManagementViewerConfiguration();

        Store store = new Store();
        store.setDataType(dataType);
        store.setDataStoreId(dataStoreId);

        Document document = new Document();
        List<Button> buttons = new ArrayList<Button>();
        document.setButtons(buttons);

        dmsConfiguration.setStore(store);
        dmsConfiguration.setDocument(document);

        return dmsConfiguration;
    }

    /**
     * @return
     */
    public Configuration buildEmptyConfiguration() {
        DmsDataManagementViewerConfiguration dmsConfiguration = new DmsDataManagementViewerConfiguration();
        Store store = new Store();
        Document document = new Document();
        dmsConfiguration.setStore(store);
        dmsConfiguration.setDocument(document);
        return dmsConfiguration;
    }

}
