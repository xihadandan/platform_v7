package com.wellsoft.pt;

import com.wellsoft.pt.jpa.datasource.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 实现动态数据源，根据AbstractRoutingDataSource路由到不同数据源中
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2025/1/14    Create
 * </pre>
 * @date 2025/1/14
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> dataSourceMap;


    public DynamicDataSource(DataSource defaultDataSource, Map<Object, Object> targetDataSources) {
        this.dataSourceMap = targetDataSources;
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(targetDataSources);
    }

    /**
     * 是否存在数据源
     *
     * @param key 数据源标识
     * @return
     */
    public boolean existDataSource(String key) {
        return dataSourceMap.containsKey(key);
    }

    /**
     * 动态添加数据源的方法
     *
     * @param key        数据源key
     * @param dataSource 数据源
     */
    public void addDataSource(String key, DataSource dataSource) {
        dataSourceMap.put(key, dataSource);
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }
}
