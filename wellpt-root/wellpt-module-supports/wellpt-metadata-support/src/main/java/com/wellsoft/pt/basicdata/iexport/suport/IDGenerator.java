/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月10日.1	zhulh		2019年4月10日		Create
 * </pre>
 * @date 2019年4月10日
 */
public class IDGenerator implements IdentifierGenerator {

    private static final String ID_COLUMN_NAME = "id";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.suport.IdentifierGenerator#generate(com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord)
     */
    @Override
    public IexportDataIdentifier generate(IexportDataRecord iexportDataRecord) {
        String tableName = iexportDataRecord.getTableName();
        IexportDataColumn idDataColumn = iexportDataRecord.getDataColumn(ID_COLUMN_NAME);
        String pattern = idDataColumn.getValue() + "_000";
        IdGeneratorService idGeneratorService = ApplicationContextHolder.getBean(IdGeneratorService.class);
        String id = idGeneratorService.generate(tableName, pattern);
        IexportDataIdentifier identifier = new IexportDataIdentifier(id, id);
        return identifier;
    }

}
