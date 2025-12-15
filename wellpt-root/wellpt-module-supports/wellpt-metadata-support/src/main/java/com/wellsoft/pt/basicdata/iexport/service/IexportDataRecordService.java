/*
 * @(#)2019年4月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord;
import com.wellsoft.pt.basicdata.iexport.suport.InsertedRecords.InsertedRecord;

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
 * 2019年4月7日.1	zhulh		2019年4月7日		Create
 * </pre>
 * @date 2019年4月7日
 */
public interface IexportDataRecordService extends BaseService {

    /**
     * @param iexportData
     * @return
     */
    IexportDataRecord getRecord(IexportData iexportData);

    /**
     * @param iexportData
     */
    void storeRecord(IexportData iexportData);

    /**
     * @param insertedRecords
     */
    void deleteInsertedRecords(List<InsertedRecord> insertedRecords);

}
