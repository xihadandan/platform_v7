package com.wellsoft.pt.document.support;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.basicdata.view.support.CondSelectAskInfoNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.document.MongoLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MongoLogDataSource extends AbstractDataSourceProvider {
    private static final ThreadLocal<Map<String, Object>> queryMaps = new ThreadLocal<Map<String, Object>>();
    private static final String SHOW_TITLE_LIST[] = {"id", "moduleCode",
            "moduleName", "moduleId", "interfaceId", "interfaceTitle",
            "createTime", "creatorId", "creatorName", "creatorIp", "tenantId", "serverIP",
            "content"};
    @Autowired
    private MongoLogService mongoLogService;

    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();
        {
            DataSourceColumn field = null;
            for (int i = 0; i < SHOW_TITLE_LIST.length; i++) {
                field = new DataSourceColumn();
                field.setFieldName(SHOW_TITLE_LIST[i]);
                field.setColumnName(SHOW_TITLE_LIST[i]);
                field.setColumnAliase(SHOW_TITLE_LIST[i]);
                field.setTitleName(SHOW_TITLE_LIST[i]);
                dataSourceColumns.add(field);
            }
        }
        return dataSourceColumns;
    }

    public String getModuleId() {
        return "mongo_log";
    }

    public String getModuleName() {
        return "mongo业务日志";
    }

    @Override
    public String getWhereSqlForAll(DyViewQueryInfoNew dyViewQueryInfoNew,
                                    String whereSql) {
        List<CondSelectAskInfoNew> cdins = dyViewQueryInfoNew
                .getCondSelectList();
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != cdins && !cdins.isEmpty()) {
            for (CondSelectAskInfoNew condSelectAskInfoNew : cdins) {
                if (StringUtils.isNotBlank(condSelectAskInfoNew
                        .getSearchValue())) {
                    map.put(condSelectAskInfoNew.getSearchField(),
                            condSelectAskInfoNew.getSearchValue());
                }
            }
        }
        queryMaps.set(map);
        return null;

    }

    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumns,
                                 String whereHql, Map<String, Object> queryParams, String orderBy,
                                 PagingInfo pagingInfo) {
        queryParams.putAll(queryMaps.get());
        queryParams.put("pagingInfo", pagingInfo);
        return mongoLogService.findLog(queryParams);
    }
}
