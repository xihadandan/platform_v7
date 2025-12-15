/*
 * @(#)2019年4月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月7日.1	zhulh		2019年4月7日		Create
 * </pre>
 * @date 2019年4月7日
 */
public class IexportDataRecordUtils {

    /**
     * @param iexportData
     * @return
     */
    public static IexportDataRecord getRecord(IexportData iexportData) {
        IexportDataRecordService iexportDataMetaDataService = ApplicationContextHolder
                .getBean(IexportDataRecordService.class);
        return iexportDataMetaDataService.getRecord(iexportData);
    }

}
