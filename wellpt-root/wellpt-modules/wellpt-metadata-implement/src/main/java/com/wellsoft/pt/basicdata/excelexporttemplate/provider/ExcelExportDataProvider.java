/*
 * @(#)2015-1-4 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.provider;

import java.util.List;
import java.util.Map;

/**
 * Description: excel导出数据组装接口
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
public interface ExcelExportDataProvider {

    public List<Map<String, String>> getExportDataArray(Map<String, Object> exparms);

    public String[] getExportTitleArray();

    public String getModuleId();

    public String getModuleName();
}
