/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TableCriteria extends AbstractNativeCriteria {
    private final String tableName;

    public TableCriteria(NativeDao nativeDao, String tableName) {
        super(nativeDao);
        this.tableName = tableName.toUpperCase();
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
        String countSql = getCountSql();
        try {
            countSql = TemplateEngineFactory.getDefaultTemplateEngine().process(countSql, queryParams);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        Number count = (Number) this.nativeDao.findUnique(countSql, queryParams);
        return count.longValue();
    }

    @Override
    protected String getFromSql() {
        return " from " + getTableName() + " " + getAlais() + " ";
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    protected CriteriaMetadata initCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", tableName);
        SessionFactory sessionFactory = this.nativeDao.getSessionFactory();
        if (sessionFactory != null) {
            String tableSchema = ((SessionFactoryImpl) sessionFactory).getProperties().getProperty("tableSchema");
            if (StringUtils.isNotBlank(tableSchema)) {
                params.put("tableSchema", tableSchema);
            }
        }
        List<QueryItem> columns = this.nativeDao.namedQuery("queryTableColumnMetadata", params, QueryItem.class);
        for (QueryItem column : columns) {
            String columnName = column.getString("columnName");
            criteriaMetadata.add(columnName, columnName, column.getString("comments"), column.getString("dataType"));
        }
        return criteriaMetadata;
    }

}
