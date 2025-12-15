/*
 * @(#)2014-6-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.basicdata.excelexporttemplate.bean.ExcelExportDefinitionBean;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * 2014-6-17.1	wubin		2014-6-17		Create
 * </pre>
 * @date 2014-6-17
 */
public interface ExcelExportRuleService {

    /**
     * 获得数据导出接口的list
     *
     * @param s
     * @param id
     * @return
     */
    public List getDataSourceList(String s);

    /**
     * 通过uuid获取excel导出规则的bean
     *
     * @param uuid
     * @return
     */
    public ExcelExportDefinitionBean getBeanByUuid(String uuid);

    public void saveBean(ExcelExportDefinitionBean bean);

    /**
     * 删除一条导出规则
     *
     * @param uuid
     */
    public void remove(String uuid);

    /**
     * 删除多条的导出规则
     *
     * @param uuids
     */
    public void removeAll(String[] uuids);

    /**
     * JQgridExcel导出规则列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 根据excel导出模版获得导出文件
     *
     * @param id
     * @return
     */
    public String generateExcelFile(DyViewQueryInfoNew dyViewQueryInfoNew, String id, String data,
                                    HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取所有的导出模版规则
     *
     * @return
     */
    public List<ExcelExportDefinition> getExcelExportRule();

    public ExcelExportDefinition getExcelExportDefinition(String uuid);

    public String generateXmlFile(List<Map<String, String>> dataList, String[] titleArray, HttpServletRequest request,
                                  HttpServletResponse response);

}
