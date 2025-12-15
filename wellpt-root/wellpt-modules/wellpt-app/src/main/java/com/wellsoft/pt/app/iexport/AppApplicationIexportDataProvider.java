/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.app.iexport.acceptor.AppApplicationIexportData;
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
public class AppApplicationIexportDataProvider extends AbstractIexportDataProvider<AppApplication, String> {
    static {
        TableMetaData.register(IexportType.AppApplication, "应用", AppApplication.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppApplication;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppApplication appApplication = this.dao.get(AppApplication.class, uuid);
        if (appApplication == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的应用依赖关系", "应用定义", uuid);
        }
        return new AppApplicationIexportData(appApplication);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 应用ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppApplication), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(AppApplication.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppApplication appApplication) {
        return new AppApplicationIexportData(appApplication).getName();
    }

    @Override
    public Map<String, List<AppApplication>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppApplication> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppApplication.class);
        Map<String, List<AppApplication>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            for (AppApplication appApplication : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appApplication.getUuid();
                this.putParentMap(map, appApplication, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
