/*
 * @(#)2019年8月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.support;

import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月28日.1	zhulh		2019年8月28日		Create
 * </pre>
 * @date 2019年8月28日
 */
public class UserTableFormDataQueryInfo extends FormDataQueryInfo {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6021481539846366914L;

    private String tableName;

    /**
     * @param formId
     */
    public UserTableFormDataQueryInfo(String tableName, FormDataQueryInfo formDataQueryInfo) {
        super(formDataQueryInfo.getFormId());
        this.tableName = tableName;
        setFormDataQueryInfo(formDataQueryInfo);
    }

    /**
     * @param formDataQueryInfo
     */
    private void setFormDataQueryInfo(FormDataQueryInfo formDataQueryInfo) {
        this.setDistinct(formDataQueryInfo.isDistinct());
        this.setProjection(formDataQueryInfo.getProjection());
        this.setConditions(formDataQueryInfo.getConditions());
        this.setQueryParams(formDataQueryInfo.getQueryParams());
        this.setGroupBy(formDataQueryInfo.getGroupBy());
        this.setHaving(formDataQueryInfo.getHaving());
        this.setOrders(formDataQueryInfo.getOrders());
        this.setPagingInfo(formDataQueryInfo.getPagingInfo());
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

}
