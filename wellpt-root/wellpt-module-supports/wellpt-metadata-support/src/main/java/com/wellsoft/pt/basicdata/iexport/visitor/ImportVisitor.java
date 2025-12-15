/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.visitor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description: 生成导出的数据
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
public class ImportVisitor implements Visitor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> newVerUuids;
    private boolean newVer;

    public ImportVisitor(List<String> newVerUuids, boolean newVer) {
        this.newVerUuids = newVerUuids;
        this.newVer = newVer;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.visitor.Visitor#visit(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData)
     */
    @Override
    public void visit(IexportData iexportData) {
        String type = iexportData.getType();
        try {
            //			IexportDataRecordService iexportDataRecordService = ApplicationContextHolder
            //					.getBean(IexportDataRecordService.class);
            //			iexportDataRecordService.storeRecord(iexportData);
            IexportDataProviderFactory.getDataProvider(type).storeData(iexportData, newVer);
            // 记录日志
            //			DataImportLogService dataImportLogService = ApplicationContextHolder.getBean(DataImportLogService.class);
            //			dataImportLogService.log(iexportData);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

}
