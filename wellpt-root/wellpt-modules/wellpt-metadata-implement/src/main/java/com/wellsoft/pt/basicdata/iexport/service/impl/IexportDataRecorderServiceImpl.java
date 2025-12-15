/*
 * @(#)2019年4月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecorderService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord;
import com.wellsoft.pt.basicdata.iexport.suport.InsertedRecords;
import com.wellsoft.pt.basicdata.iexport.suport.InsertedRecords.InsertedRecord;
import com.wellsoft.pt.basicdata.iexport.suport.UpdatedRecords;
import com.wellsoft.pt.basicdata.iexport.suport.UpdatedRecords.UpdatedRecord;
import org.springframework.stereotype.Service;

/**
 * Description: 数据记录器服务实现
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月16日.1	zhulh		2019年4月16日		Create
 * </pre>
 * @date 2019年4月16日
 */
@Service
public class IexportDataRecorderServiceImpl implements IexportDataRecorderService {

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecorderService#recordUpdate(IexportDataRecord)
     */
    @Override
    public void recordUpdate(IexportDataRecord record) {
        UpdatedRecords updatedRecords = IexportContextHolder.getUpdatedRecords();
        if (updatedRecords == null) {
            updatedRecords = new UpdatedRecords();
            IexportContextHolder.setUpdatedRecords(updatedRecords);
        }

        UpdatedRecord updatedRecord = new UpdatedRecord();
        updatedRecord.setTableName(record.getTableName());
        updatedRecord.setPrimaryKeyName(record.getPrimaryKeyName());
        updatedRecord.setPrimaryKeyValue(record.getPrimaryKeyValue());
        updatedRecords.getUpdatedRecords().add(updatedRecord);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataRecorderService#recordInsert(IexportDataRecord)
     */
    @Override
    public void recordInsert(IexportDataRecord record) {
        InsertedRecords insertedRecords = IexportContextHolder.getInsertedRecords();
        if (insertedRecords == null) {
            insertedRecords = new InsertedRecords();
            IexportContextHolder.setInsertedRecords(insertedRecords);
        }

        InsertedRecord insertedRecord = new InsertedRecord();
        insertedRecord.setTableName(record.getTableName());
        insertedRecord.setPrimaryKeyName(record.getPrimaryKeyName());
        insertedRecord.setPrimaryKeyValue(record.getPrimaryKeyValue());
        insertedRecords.getInsertedRecords().add(insertedRecord);
    }

}
