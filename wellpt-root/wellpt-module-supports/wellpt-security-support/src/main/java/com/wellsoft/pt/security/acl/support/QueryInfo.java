/*
 * @(#)2012-11-1 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.support;

import com.wellsoft.context.jdbc.support.Page;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: ACL查询条件信息类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-1.1	zhulh		2012-11-1		Create
 * </pre>
 * @date 2012-11-1
 */
public class QueryInfo<T> {
    /**
     * 查询字段HQL语句，只对QueryItem有效
     */
    private String selectionHql;
    /**
     * 条件HQL语句
     */
    private String whereHql;
    /**
     * 条件参数
     */
    private Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
    /**
     * 分页信息
     */
    private Page<T> page = new Page<T>();
    /**
     * 排序信息
     */
    private Map<String, String> orderby = new LinkedHashMap<String, String>();

    /**
     * @return the selectionHql
     */
    public String getSelectionHql() {
        return selectionHql;
    }

    /**
     * @param selectionHql 要设置的selectionHql
     */
    public void setSelectionHql(String selectionHql) {
        this.selectionHql = selectionHql;
    }

    /**
     * @return the whereHql
     */
    public String getWhereHql() {
        return whereHql;
    }

    /**
     * @param whereHql 要设置的whereHql
     */
    public void setWhereHql(String whereHql) {
        this.whereHql = whereHql;
    }

    /**
     * @return the queryParams
     */
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    /**
     * 以key-value的形式增加参数值
     *
     * @param paramName
     * @param value
     * @return
     */
    public QueryInfo<T> addQueryParams(String paramName, Object value) {
        this.queryParams.put(paramName, value);
        return this;
    }

    /**
     * @return the page
     */
    public Page<T> getPage() {
        return page;
    }

    /**
     * @param page 要设置的page
     */
    public void setPage(Page<T> page) {
        this.page = page;
    }

    /**
     * @return the orderby
     */
    public Map<String, String> getOrderby() {
        return orderby;
    }

    /**
     * 以key-value的形式设置排序信息
     *
     * @param fieldName
     * @param order
     * @return
     */
    public QueryInfo<T> addOrderby(String fieldName, String order) {
        this.orderby.put(fieldName, order);
        return this;
    }

    /**
     * 添加一个orderBy如：name asc
     *
     * @param orderBy
     */
    public void addOrderby(String orderBy) {
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderBys = orderBy.split(" ");
            if (orderBys.length == 1) {
                addOrderby(orderBys[0], "asc");
            } else {
                addOrderby(orderBys[0], orderBys[1]);
            }
        }
    }

}
