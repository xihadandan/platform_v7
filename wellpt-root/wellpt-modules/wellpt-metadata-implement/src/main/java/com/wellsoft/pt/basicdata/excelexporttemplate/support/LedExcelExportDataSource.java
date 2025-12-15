/*
 * @(#)2015-1-5 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import com.wellsoft.pt.basicdata.excelexporttemplate.provider.AbstractExcelExportDataProvider;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
@Component
public class LedExcelExportDataSource extends AbstractExcelExportDataProvider {

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
        return "ledExcelExportDataSource";
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
        return "led替换灯数据导出接口";
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
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String dataUuids = (String) exparms.get("dataUuid");
        String[] dataUuidArray = dataUuids.split(",");
        String formUuid = "4aa3f81b-7417-4d2f-bfe8-ecb51d737a90";
        /*
         * DyFormData data = new DyFormData(); for (String dataUuid :
         * dataUuidArray) { data = dyFormApiFacade.getDyFormData(formUuid,
         * dataUuid); Map<String, Object> dataMap =
         * data.getFormDataByFormUuidAndDataUuid(formUuid, dataUuid);
         * Map<String, String> map = new HashMap<String, String>(); for (String
         * key : dataMap.keySet()) { if (dataMap.get(key) != null) {
         * map.put(key, String.valueOf(dataMap.get(key))); } else { map.put(key,
         * ""); } } list.add(map); }
         */
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
        String formUuid = "4aa3f81b-7417-4d2f-bfe8-ecb51d737a90";
        List<DyformFieldDefinition> list = dyFormApiFacade.getFieldDefinitions(formUuid);
        String[] titleArray = new String[200];
        int i = 0;
        for (DyformFieldDefinition fd : list) {
            String titleName = fd.getDisplayName();
            titleArray[i] = titleName;
            i++;
        }
        return titleArray;
    }

}
