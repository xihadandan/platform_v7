/*
 * @(#)2015-1-4 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.provider;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-4.1	wubin		2015-1-4		Create
 * </pre>
 * @date 2015-1-4
 */
public abstract class AbstractExcelExportDataProvider implements ExcelExportDataProvider {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.ExcelExportDataProvider#getExportDataArray()
     */
    @Override
    public List<Map<String, String>> getExportDataArray(Map<String, Object> exparms) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.ExcelExportDataProvider#getExportTitleArray()
     */
    @Override
    public String[] getExportTitleArray() {
        // TODO Auto-generated method stub
        return null;
    }

}
