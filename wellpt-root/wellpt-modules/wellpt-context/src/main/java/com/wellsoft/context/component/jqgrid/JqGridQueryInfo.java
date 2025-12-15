/*
 * @(#)2012-12-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.jqgrid;

/**
 * Description: JqGridQueryInfo.java
 *
 * @author zhulh
 * @date 2012-12-20
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-20.1		zhulh		2012-12-20		Create
 * </pre>
 */
public class JqGridQueryInfo {
    private String _search;
    private String nd;
    private Integer rows;
    private Integer page;
    private String sidx;
    private String sord;

    // Tree Grid
    private String nodeid;
    private String parentid;
    private Integer n_level;

    private String sfId;
    private String serviceName;
    private String queryType;

    public String get_search() {
        return _search;
    }

    public void set_search(String _search) {
        this._search = _search;
    }

    public Integer getN_level() {
        return n_level;
    }

    public void setN_level(Integer n_level) {
        this.n_level = n_level;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }

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

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

}
