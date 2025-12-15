/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.ImportIexportService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class IexportDataProviderFactory {

    public static IexportDataProvider getDataProvider(String type) {
        return ApplicationContextHolder.getBean(ImportIexportService.class).getIexportDataProvider(type);
    }

    public static IexportData getIexportData(String type, String uuid) {
        return getDataProvider(type).getData(uuid);
    }

}
