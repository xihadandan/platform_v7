/*
 * @(#)2013-12-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.request;

import com.wellsoft.pt.integration.support.Condition;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-13.1	ruanhg		2014-6-13		Create
 * </pre>
 * @date 2014-6-13
 */
public class ShareRequest extends Request {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2593240346666842356L;

    // 数据类型ID
    private String typeId;

    // 数据ID
    private String unitId;

    private List<Condition> conditions;

    // 返回页面大小，当出入为‘-1’时，返回所有数据，当结果大于1000时将自动分页，pageSize=1000
    private Integer pageSize;

    // 源单位ID
    private Integer currentPage;

    //预留参数字段
    private Map<String, String> params;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
