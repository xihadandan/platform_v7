/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

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
public class NamedQueryCriteria extends AbstractNativeCriteria {
    private final String sqlName;

    public NamedQueryCriteria(NativeDao nativeDao, String sqlName) {
        super(nativeDao);
        this.sqlName = sqlName;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#list()
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        return this.nativeDao.query(getQuerySql(), queryParams, this.pagingInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#list(java.lang.Class)
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
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.Criteria#count()
     */
    @Override
    public long count() {
        BigDecimal count = (BigDecimal) this.nativeDao.findUnique(getCountSql(), queryParams);
        return count.longValue();
    }

    @Override
    protected String getQuerySql() {
        return super.getQuerySql();
    }

    @Override
    protected String getCountSql() {
        return super.getCountSql();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.AbstractCriteria#getAlais()
     */
    @Override
    public String getAlais() {
        return StringUtils.EMPTY;
    }

    @Override
    protected String getFromSql() {
        String sql = getDynamicNamedQueryString(this.getSessionFactory(), this.getSqlName(), this.getQueryParams());
        return " from (" + sql + ") tfrom";
    }

    @Override
    protected CriteriaMetadata initCriteriaMetadata() {
        String sql = getDynamicNamedQueryString(this.getSessionFactory(), this.getSqlName(), this.getQueryParams());
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Connection connection = null;
        PreparedStatement pState = null;
        JdbcConnectionAccess jdbcConnectionAccess = null;
        jdbcConnectionAccess = ((SessionImplementor) getSession()).getJdbcConnectionAccess();
        try {
            connection = jdbcConnectionAccess.obtainConnection();
            pState = connection.prepareStatement(sql);
            ResultSet rs = pState.executeQuery();
            ResultSetMetaData data = rs.getMetaData();
            int numberOfColumns = data.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                criteriaMetadata.add(data.getColumnName(i), data.getColumnName(i), data.getColumnTypeName(i));
            }
            rs.close();
            pState.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException("SQL命名查询不能内置带参数!");
        } finally {
            try {
                if (pState != null && !pState.isClosed()) {
                    pState.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
        return criteriaMetadata;
    }

    public String getSqlName() {
        return sqlName;
    }

}
