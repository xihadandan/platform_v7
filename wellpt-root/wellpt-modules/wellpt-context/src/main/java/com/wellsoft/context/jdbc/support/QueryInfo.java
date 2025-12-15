/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-1.1	zhulh		2013-2-1		Create
 * </pre>
 * @date 2013-2-1
 */
public class QueryInfo {
    private String sfId;
    private String serviceName;
    private String queryType;
    private List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>(0);
    private PagingInfo pagingInfo;
    private String orderBy;

    /**
     * @return the sfId
     */
    public String getSfId() {
        return sfId;
    }

    /**
     * @param sfId 要设置的sfId
     */
    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName 要设置的serviceName
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the queryType
     */
    public String getQueryType() {
        return queryType;
    }

    /**
     * @param queryType 要设置的queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    /**
     * @return the propertyFilters
     */
    public List<PropertyFilter> getPropertyFilters() {
        return propertyFilters;
    }

    /**
     * @param propertyFilters 要设置的propertyFilters
     */
    public void setPropertyFilters(List<PropertyFilter> propertyFilters) {
        this.propertyFilters = propertyFilters;
    }

    /**
     * @return the pagingInfo
     */
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    /**
     * @param pagingInfo 要设置的pagingInfo
     */
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy 要设置的orderBy
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
