/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.selective.provider.impl.SqlQuerySelectiveDataProvider;
import com.wellsoft.pt.basicdata.selective.service.SqlQuerySelectiveDataService;
import com.wellsoft.pt.basicdata.selective.support.DataItem;
import com.wellsoft.pt.basicdata.selective.support.DataItemConstants;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import com.wellsoft.pt.basicdata.selective.support.SqlQuerySelectiveData;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
@Service
@Transactional(readOnly = true)
public class SqlQuerySelectiveDataServiceImpl extends BaseServiceImpl implements
        SqlQuerySelectiveDataService {

    @Autowired
    private List<SqlQuerySelectiveData> sqlQuerySelectiveDatas;

    private Map<String, SqlQuerySelectiveData> configKeyMap = Maps.newConcurrentMap();


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.selective.service.SqlQuerySelectiveDataService#get(java.lang.String)
     */
    @Override
    public SelectiveData get(String configKey) {
        if (configKeyMap.isEmpty()) {
            initConfigKeyMap();
        }

        SqlQuerySelectiveData sqlQuerySelectiveData = configKeyMap.get(configKey);
        if (sqlQuerySelectiveData == null) {
            return sqlQuerySelectiveData;
        }

        String sessionFactoryId = sqlQuerySelectiveData.getSessionFactoryId();
        NativeDao queryDao = this.getNativeDao(sessionFactoryId);
        String sql = sqlQuerySelectiveData.getQuerySql();
        // 去掉最后 的分号
        if (StringUtils.isNotBlank(sql)) {
            sql = StringUtils.trim(sql);
            if (sql.endsWith(Separator.SEMICOLON.getValue())) {
                sql = sql.substring(0, sql.length() - 1);
            }
        }
        List<QueryItem> queryItems = queryDao.query(sql, null, QueryItem.class);
        SelectiveData selectiveData = new SelectiveData();
        selectiveData.setConfigKey(configKey);
        selectiveData.setProvider(SqlQuerySelectiveDataProvider.class.getSimpleName());
        selectiveData.setItems(queryItems);
        selectiveData.setItemLabel(sqlQuerySelectiveData.getItemLabel());
        selectiveData.setItemValue(sqlQuerySelectiveData.getItemValue());
        selectiveData.setCacheable(sqlQuerySelectiveData.isCacheable());
        selectiveData.setCacheName(sqlQuerySelectiveData.getCacheName());
        // 转化为DataItem
        if (DataItemConstants.LABEL.equals(sqlQuerySelectiveData.getItemLabel())
                && DataItemConstants.VALUE.equals(sqlQuerySelectiveData.getItemValue())) {
            List<DataItem> items = new ArrayList<DataItem>();
            for (QueryItem queryItem : queryItems) {
                DataItem item = new DataItem();
                if (sqlQuerySelectiveData.getLabelRender() != null) {
                    item.setLabel(sqlQuerySelectiveData.getLabelRender().render(
                            queryItem.getString(DataItemConstants.LABEL),
                            queryItem.getString(DataItemConstants.VALUE)));
                } else {
                    item.setLabel(queryItem.getString(DataItemConstants.LABEL));
                }
                if (sqlQuerySelectiveData.getValueRender() != null) {
                    item.setValue(sqlQuerySelectiveData.getValueRender().render(
                            queryItem.getString(DataItemConstants.LABEL),
                            queryItem.getString(DataItemConstants.VALUE)));
                } else {
                    item.setValue(queryItem.getString(DataItemConstants.VALUE));
                }
                items.add(item);
            }
            selectiveData.setItems(items);
        }

        return selectiveData;
    }

    /**
     *
     */
    private void initConfigKeyMap() {
        for (SqlQuerySelectiveData sqlQuerySelectiveData : sqlQuerySelectiveDatas) {
            configKeyMap.put(sqlQuerySelectiveData.getConfigKey(), sqlQuerySelectiveData);
        }
    }

}
