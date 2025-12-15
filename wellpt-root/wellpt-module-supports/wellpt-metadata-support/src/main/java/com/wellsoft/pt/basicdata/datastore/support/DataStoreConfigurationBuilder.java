/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public class DataStoreConfigurationBuilder {

    public static DataStoreConfiguration buildFromDataStoreId(String dataStoreId) {
        CdDataStoreDefinitionService cdDataStoreDefinitionService = ApplicationContextHolder
                .getBean(CdDataStoreDefinitionService.class);
        CdDataStoreDefinition dataStoreDefinition = cdDataStoreDefinitionService.getBeanById(dataStoreId);
        if (dataStoreDefinition == null) {
            return null;
        }
        DataStoreConfiguration dataStoreConfiguration = new DataStoreConfiguration();
        BeanUtils.copyProperties(dataStoreDefinition, dataStoreConfiguration);
        return dataStoreConfiguration;
    }

}
