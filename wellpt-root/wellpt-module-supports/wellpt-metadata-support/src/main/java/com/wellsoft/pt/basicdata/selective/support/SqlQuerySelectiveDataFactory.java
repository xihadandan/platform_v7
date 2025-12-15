package com.wellsoft.pt.basicdata.selective.support;

import com.wellsoft.context.config.Config;
import org.springframework.beans.factory.FactoryBean;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/15    chenq		2019/11/15		Create
 * </pre>
 */
public class SqlQuerySelectiveDataFactory implements FactoryBean<SqlQuerySelectiveData> {

    private String sessionFactoryId = Config.DEFAULT_SESSION_FACTORY_BEAN_NAME;

    private String itemLabel = "label";

    private String itemValue = "value";

    private boolean cacheable = true;

    private String querySql = null;

    private String configKey = null;

    private String cacheName = null;

    private SqlQuerySelectiveData.SqlSelectDataRender valueRender = null;

    private SqlQuerySelectiveData.SqlSelectDataRender labelRender = null;

    @Override
    public SqlQuerySelectiveData getObject() throws Exception {
        SqlQuerySelectiveData sqlQuerySelectiveData = new SqlQuerySelectiveData();
        sqlQuerySelectiveData.setQuerySql(querySql);
        sqlQuerySelectiveData.setSessionFactoryId(sessionFactoryId);
        sqlQuerySelectiveData.setCacheable(cacheable);
        sqlQuerySelectiveData.setItemLabel(itemLabel);
        sqlQuerySelectiveData.setItemValue(itemValue);
        sqlQuerySelectiveData.setConfigKey(configKey);
        sqlQuerySelectiveData.setCacheName(cacheName);
        sqlQuerySelectiveData.setValueRender(valueRender);
        sqlQuerySelectiveData.setLabelRender(labelRender);
        return sqlQuerySelectiveData;
    }

    @Override
    public Class<?> getObjectType() {
        return SqlQuerySelectiveData.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getSessionFactoryId() {
        return sessionFactoryId;
    }

    public void setSessionFactoryId(String sessionFactoryId) {
        this.sessionFactoryId = sessionFactoryId;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public SqlQuerySelectiveData.SqlSelectDataRender getValueRender() {
        return valueRender;
    }

    public void setValueRender(
            SqlQuerySelectiveData.SqlSelectDataRender valueRender) {
        this.valueRender = valueRender;
    }

    public SqlQuerySelectiveData.SqlSelectDataRender getLabelRender() {
        return labelRender;
    }

    public void setLabelRender(
            SqlQuerySelectiveData.SqlSelectDataRender labelRender) {
        this.labelRender = labelRender;
    }
}
