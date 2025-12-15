/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.excel.ExcelImportDataReport;
import com.wellsoft.context.util.excel.ExcelImportRule;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dms.bean.DmsDataImportBean;

import java.io.InputStream;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
public interface DmsDataImportService extends BaseService {
    /**
     * 数据导入
     *
     * @param fileId
     * @param dmsDataImportBean
     * @return
     */
    ResultMessage importFormRepoFile(String fileId, DmsDataImportBean dmsDataImportBean);


    /**
     * 根据自定义的导入监听器执行导入逻辑
     *
     * @param className
     * @param fileId
     * @return
     */
    ResultMessage importByListener(String fileId, String className, ExcelImportRule excelImportRule);


    ExcelImportDataReport importByListener(InputStream inputStream, String className, ExcelImportRule excelImportRule) throws Exception;

    ResultMessage importByListener(String fileId, String className);


    /**
     * 生成导入数据的数据报告
     *
     * @param fileId
     * @param type
     * @return
     */
    ResultMessage generateImportDataReportExcel(String fileId, Integer type,
                                                ExcelImportDataReport report);

}
