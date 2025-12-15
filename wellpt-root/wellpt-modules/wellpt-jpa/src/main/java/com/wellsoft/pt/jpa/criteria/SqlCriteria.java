/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.support.NamedParameterStatement;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月26日.1	xiem		2016年10月26日		Create
 * </pre>
 * @date 2016年10月26日
 */
public class SqlCriteria extends AbstractNativeCriteria {
    private final String sql;

    public SqlCriteria(NativeDao nativeDao, String sql) {
        super(nativeDao);
        this.sql = sql;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        return this.nativeDao.query(getQuerySql(), queryParams, this.pagingInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        String querySql = getQuerySql();
        try {
            querySql = TemplateEngineFactory.getDefaultTemplateEngine().process(querySql, queryParams);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return this.nativeDao.query(querySql, queryParams, itemClass, this.pagingInfo);
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public String getAlais() {
        return StringUtils.EMPTY;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public long count() {
        Number count = (Number) this.nativeDao.findUnique(getCountSql(), queryParams);
        return count.longValue();
    }

    @Override
    protected String getFromSql() {
        return " from (" + getSql() + ") this_ ";
    }

    public String getSql() {
        return sql;
    }

    @Override
    protected CriteriaMetadata initCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Connection connection = null;
        NamedParameterStatement namedParameterStatement = null;
        JdbcConnectionAccess jdbcConnectionAccess = null;
        jdbcConnectionAccess = ((SessionImplementor) getSession()).getJdbcConnectionAccess();

        try {
            connection = jdbcConnectionAccess.obtainConnection();
            namedParameterStatement = new NamedParameterStatement(
                    connection, sql);
            Set<String> mapKeys = this.queryParams.keySet();

            for (String k : mapKeys) {
                if (sql.indexOf(Separator.COLON.getValue() + k) != -1)
                    namedParameterStatement.setObject(k, this.queryParams.get(k));
            }
            ResultSet rs = namedParameterStatement.executeQuery();
            ResultSetMetaData data = rs.getMetaData();
            int numberOfColumns = data.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                criteriaMetadata.add(data.getColumnName(i), data.getColumnName(i),
                        data.getColumnTypeName(i));
            }
            rs.close();
            namedParameterStatement.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
        return criteriaMetadata;
    }


}
