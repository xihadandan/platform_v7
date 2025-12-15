/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
public class AppSystemIexportData extends IexportData {

    public AppSystem appSystem;

    /**
     * @param appSystem
     */
    public AppSystemIexportData(AppSystem appSystem) {
        this.appSystem = appSystem;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return appSystem.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getName()
     */
    @Override
    public String getName() {
        return "系统定义: " + appSystem.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppSystem;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return appSystem.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, appSystem);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }

}
