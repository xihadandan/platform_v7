package com.wellsoft.pt.di.component.frontdb;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.di.component.AbstractScheduledPollConsumer;
import com.wellsoft.pt.jpa.dao.SessionOperationHibernateDao;
import com.wellsoft.pt.jpa.hibernate4.DynamicHibernateSessionFactoryRegistry;
import org.apache.camel.Processor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.activation.DataHandler;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/26    chenq		2019/8/26		Create
 * </pre>
 */
public class FrontDatabaseConsumer extends AbstractScheduledPollConsumer<FrontDatabaseEndpoint> {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    SessionOperationHibernateDao dao;

    public FrontDatabaseConsumer(
            FrontDatabaseEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        Map<String, Object> dsProperties = Maps.newHashMap();
        dsProperties.put("allowLocalTransactions", true);
        dsProperties.put("ignnoreEntity", true);
        dsProperties.put("ignnoreMapping", true);
        String dataSourceId = DynamicHibernateSessionFactoryRegistry.buildDataSource(
                ApplicationContextHolder.defaultListableBeanFactory(),
                endpoint.getJdbcUrl(), endpoint.getUser(), endpoint.getPassword(), dsProperties);
        NamedParameterJdbcDaoSupport daoSupport = new NamedParameterJdbcDaoSupport();
        daoSupport.setDataSource((DataSource) ApplicationContextHolder.getBean(dataSourceId));
        namedParameterJdbcTemplate = daoSupport.getNamedParameterJdbcTemplate();

        //前置库数据源
        Map<String, Object> jdbcProperties = Maps.newHashMap();
        jdbcProperties.put("url", endpoint.getJdbcUrl());
        jdbcProperties.put("username", endpoint.getUser());
        jdbcProperties.put("password", endpoint.getPassword());
        String sessionFId = DynamicHibernateSessionFactoryRegistry.registry(
                ApplicationContextHolder.defaultListableBeanFactory(), jdbcProperties);
        dao = (SessionOperationHibernateDao) ApplicationContextHolder.getBean(
                "DynamicDAO_" + sessionFId);

    }

    @Override
    protected List body() {
        Map<String, Object> params = Maps.newHashMap();
        //params.put("direction", endpoint.getNetworkType());//前置库是单向传输，所有消费的数据只要根据状态位判断
        params.put("dataStatus", endpoint.getSyncType());
        //TODO:同步中的状态时间太长的数据也需要纳入（服务器宕机导致数据问题）
        List<String> list = dao.queryStrings(
                "select uuid from front_db_data where data_status=:dataStatus " +
                        "order by create_time asc ",
                params,
                new PagingInfo(0, endpoint.getLimit()));

        if (list.isEmpty()) {
            return null;
        }

        return list;


    }


    @Override
    protected Map<String, Object> headers() {
        return null;
    }

    @Override
    public Map<String, Object> properties() {
        Map<String, Object> props = Maps.newHashMap();
        props.put("DAO", dao);
        return props;
    }

    @Override
    protected Map<String, DataHandler> attachments() {
        return null;
    }
}
