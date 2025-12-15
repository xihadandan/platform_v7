/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.export;

import com.wellsoft.pt.api.entity.ApiOutSystemConfigEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 消息模板
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 *  2016-1-18.1	 linz		 2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
public class ApiOutSystemConfigIexportData extends IexportData {
    public ApiOutSystemConfigEntity configEntity;

    public ApiOutSystemConfigIexportData(ApiOutSystemConfigEntity configEntity) {
        this.configEntity = configEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return configEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "API对接系统配置：" + configEntity.getSystemName();
    }

    @Override
    public Integer getRecVer() {
        return configEntity.getRecVer();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.ApiSysConfig;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this,
                configEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        return new ArrayList<IexportData>();
    }

}
