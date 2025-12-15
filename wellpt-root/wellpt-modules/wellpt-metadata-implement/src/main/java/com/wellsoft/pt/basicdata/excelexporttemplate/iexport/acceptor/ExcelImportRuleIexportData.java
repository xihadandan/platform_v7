/*
 * @(#)2016-11-07 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.iexport.acceptor;

import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
 * 2016-11-07.1	zhulh		2016-11-07		Create
 * </pre>
 * @date 2016-11-07
 */
public class ExcelImportRuleIexportData extends IexportData {

    public ExcelImportRule excelImportRule;

    /**
     * @param excelImportRule
     */
    public ExcelImportRuleIexportData(ExcelImportRule excelImportRule) {
        this.excelImportRule = excelImportRule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return excelImportRule.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return "数据导入规则：" + excelImportRule.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.ExcelImportRule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return excelImportRule.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        List<String> fileUuids = new ArrayList<String>();
        fileUuids.add(this.excelImportRule.getFileUuid());
        return IexportDataResultSetUtils.mongoFileResultInputStream(this, excelImportRule, fileUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        return Collections.emptyList();
    }

}
