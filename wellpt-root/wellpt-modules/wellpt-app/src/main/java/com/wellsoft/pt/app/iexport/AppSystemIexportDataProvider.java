/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.iexport.acceptor.AppSystemIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service
@Transactional(readOnly = true)
public class AppSystemIexportDataProvider extends AbstractIexportDataProvider<AppSystem, String> {
    static {
        TableMetaData.register(IexportType.AppSystem, "系统", AppSystem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppSystem;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppSystem appSystem = this.dao.get(AppSystem.class, uuid);
        if (appSystem == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的系统依赖关系", "系统定义", uuid);
        }
        return new AppSystemIexportData(appSystem);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 系统ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppSystem), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(AppSystem.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppSystem appSystem) {
        return new AppSystemIexportData(appSystem).getName();
    }


    @Override
    public Map<String, List<AppSystem>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppSystem> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppSystem.class);
        Map<String, List<AppSystem>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            for (AppSystem appSystem : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appSystem.getUuid();
                this.putParentMap(map, appSystem, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
