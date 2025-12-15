/*
 * @(#)2015-1-5 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import com.wellsoft.pt.basicdata.excelexporttemplate.provider.AbstractExcelExportDataProvider;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2015-1-5.1	wubin		2015-1-5		Create
 * </pre>
 * @date 2015-1-5
 */
public class CopyOfLedExcelExportDataSource extends AbstractExcelExportDataProvider {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.ExcelExportDataProvider#getModuleId()
     */
    @Override
    public String getModuleId() {
        return "ledReplaceExportDataSource222";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.ExcelExportDataProvider#getModuleName()
     */
    @Override
    public String getModuleName() {
        // TODO Auto-generated method stub
        return "led替换灯数据导出接口222";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.AbstractExcelExportDataProvider#getExportDataArray()
     */
    @Override
    public List<Map<String, String>> getExportDataArray(Map<String, Object> exparms) {
        String formUuid = "4aa3f81b-7417-4d2f-bfe8-ecb51d737a90";
        String dataUuid = "46c5bdb68a4741abada11368f8482584";
        DyFormData data = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        Map<String, Object> dataMap = data.getFormDataByFormUuidAndDataUuid(formUuid, dataUuid);
        for (String key : dataMap.keySet()) {
            map.put(key, String.valueOf(dataMap.get(key)));
        }
        list.add(map);
        return list;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.provider.AbstractExcelExportDataProvider#getExportTitleArray()
     */
    @Override
    public String[] getExportTitleArray() {
        // TODO Auto-generated method stub
        return super.getExportTitleArray();
    }

}
