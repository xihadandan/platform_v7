/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceProfile;
import com.wellsoft.pt.basicdata.datasource.iexport.acceptor.DataSourceProfileIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:外部数据源
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-18.1	linz		2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
@Service
@Transactional(readOnly = true)
public class DataSourceProfileIexportDataProvider extends AbstractIexportDataProvider<DataSourceProfile, String> {
    static {
        TableMetaData.register(IexportType.DataSourceProfile, "外部数据源", DataSourceProfile.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataSourceProfile;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        DataSourceProfile dataSourceProfile = this.dao.get(DataSourceProfile.class, uuid);
        if (dataSourceProfile == null) {
            return new ErrorDataIexportData(IexportType.DataSourceProfile, "找不到对应的外部数据源依赖关系,可能已经被删除", "外部数据源", uuid);
        }
        return new DataSourceProfileIexportData(dataSourceProfile);
    }

    @Override
    public String getTreeName(DataSourceProfile dataSourceProfile) {
        return new DataSourceProfileIexportData(dataSourceProfile).getName();
    }

    @Override
    public Map<String, List<DataSourceProfile>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DataSourceProfile> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DataSourceProfile.class);
        Map<String, List<DataSourceProfile>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.DataSourceDefinition)) {
            for (DataSourceProfile dataSourceProfile : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataSourceProfile.getUuid();
                this.putParentMap(map, dataSourceProfile, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }

        return map;
    }
}
