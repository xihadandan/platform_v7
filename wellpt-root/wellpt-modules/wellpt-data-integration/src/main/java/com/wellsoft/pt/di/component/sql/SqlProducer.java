package com.wellsoft.pt.di.component.sql;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractProducer;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import org.apache.commons.lang.StringUtils;
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
public class SqlProducer extends AbstractProducer<SqlEndpoint> {

    JdbcTemplate jdbcTemplate;

    public SqlProducer(SqlEndpoint endpoint) {
        super(endpoint);
        Map<String, Object> dsProperties = Maps.newHashMap();
        dsProperties.put("allowLocalTransactions", true);
        String dataSourceId = DynamicHibernateSessionFactoryRegistry.buildDataSource(
                ApplicationContextHolder.defaultListableBeanFactory(), super.endpoint.getJdbcUrl(),
                super.endpoint.getUser(), super.endpoint.getPassword(), dsProperties);
        DataSource dataSource = (DataSource) ApplicationContextHolder.getBean(dataSourceId);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void action(Object body, Map<String, Object> headers, Map<String, Object> properties,
                          Map<String, DataHandler> attachments) throws Exception {
        if (body != null && String.class.isAssignableFrom(body.getClass())
                && StringUtils.isNotBlank((String) body)) {
            jdbcTemplate.execute((String) body);
        } else {
            throw new RuntimeException("数据交换-SQL只接收可执行SQL语句");
        }
    }
}
