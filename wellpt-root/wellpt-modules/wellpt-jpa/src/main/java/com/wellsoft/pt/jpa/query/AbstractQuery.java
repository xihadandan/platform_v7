/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.query;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
public abstract class AbstractQuery<T extends Query<?, ?>, U> extends BaseServiceImpl implements Query<T, U> {
    protected int firstResult;

    protected int maxResults;

    protected Map<String, Object> values = new HashMap<String, Object>();

    private StringBuilder orderBy = new StringBuilder();

    /**
     * @return the firstResult
     */
    public int getFirstResult() {
        return firstResult;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#setFirstResult(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T setFirstResult(int firstResult) {
        this.firstResult = firstResult;
        return (T) this;
    }

    /**
     * @return the maxResults
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#setMaxResults(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T addParameter(String key, Object value) {
        if (key != null && key.endsWith("Like")) {
            values.put("likeQuery", "true");
        }
        values.put(key, value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T addParameterList(String key, Object object) {
        if (!values.containsKey(key)) {
            values.put(key, new ArrayList<Object>());
        }
        List<Object> list = (List<Object>) values.get(key);
        list.add(object);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected T removeParameter(String key) {
        values.remove(key);
        return (T) this;
    }

    /**
     * @param fileName
     * @param order
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T addOrderBy(String fileName, String order) {
        if (orderBy.length() > 0) {
            orderBy.append(", ");
        }
        orderBy.append(fileName + " " + order);

        addParameter("orderBy", orderBy.toString());
        return (T) this;
    }

    /**
     * @param values
     */
    public void setProperties(Map<String, Object> properties) {
        values.putAll(properties);
    }

}
