/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.criteria;

import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.ConfigurationBuilder;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.dao.NativeDao;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
public class DmsDyformCriteria extends TableCriteria {

    private boolean enableVersioning;

    /**
     * @param nativeDao
     * @param dataStoreParams
     * @param dataStoreConfiguration
     */
    public DmsDyformCriteria(NativeDao nativeDao, DataStoreParams dataStoreParams,
                             DataStoreConfiguration dataStoreConfiguration) {
        super(nativeDao, dataStoreConfiguration.getTableName());
        this.enableVersioning = true;
        String dmsId = (String) dataStoreParams.getProxy().getExtras().get("dms_id");
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Configuration configuration = builder.buildFromWidgetDefinition(dmsId);
        enableVersioning = configuration.isEnableVersioning();
    }

    @Override
    protected String getFromSql() {
        if (enableVersioning) {
            return super.getFromSql() + " left join dms_data_version v on this_.uuid = v.data_uuid ";
        }
        return super.getFromSql();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.AbstractCriteria#getWhereSql()
     */
    @Override
    protected String getWhereSql() {
        if (enableVersioning) {
            addQueryParams("is_latest_version", true);
            return super.getWhereSql() + " and (v.is_latest_version = :is_latest_version or v.version is null) ";
        }
        return super.getWhereSql();
    }

}
