package com.wellsoft.pt.di.component.sql;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractScheduledPollConsumer;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import org.apache.camel.Processor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.activation.DataHandler;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/23    chenq		2019/8/23		Create
 * </pre>
 */
public class SqlConsumer extends AbstractScheduledPollConsumer<SqlEndpoint> {

    JdbcTemplate jdbcTemplate;

    public SqlConsumer(
            SqlEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        Map<String, Object> dsProperties = Maps.newHashMap();
        dsProperties.put("allowLocalTransactions", true);
        String dataSourceId = DynamicHibernateSessionFactoryRegistry.buildDataSource(
                ApplicationContextHolder.defaultListableBeanFactory(), super.endpoint.getJdbcUrl(),
                super.endpoint.getUser(), super.endpoint.getPassword(), dsProperties);
        DataSource dataSource = (DataSource) ApplicationContextHolder.getBean(dataSourceId);
        jdbcTemplate = new JdbcTemplate(dataSource);

    }

    @Override
    protected Object body() {
        try {
            String sql = super.endpoint.getSql();
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.error("数据库执行SQL异常：", e);
        }
        return null;

    }

    @Override
    protected Map<String, Object> headers() {
        return null;
    }

    @Override
    public Map<String, Object> properties() {
        return null;
    }

    @Override
    protected Map<String, DataHandler> attachments() {
        return null;
    }
}
