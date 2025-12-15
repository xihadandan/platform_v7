/*
 * @(#)2014-8-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.bean;

import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-13.1	wubin		2014-8-13		Create
 * </pre>
 * @date 2014-8-13
 */
public class DataSourceDefinitionBean extends DataSourceDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2136434123226216066L;

    private Set<DataSourceColumnBean> dataSourceColumnBean = new LinkedHashSet<DataSourceColumnBean>();

    /**
     * @return the dataSourceColumnBean
     */
    public Set<DataSourceColumnBean> getDataSourceColumnBean() {
        return dataSourceColumnBean;
    }

    /**
     * @param dataSourceColumnBean 要设置的dataSourceColumnBean
     */
    public void setDataSourceColumnBean(Set<DataSourceColumnBean> dataSourceColumnBean) {
        this.dataSourceColumnBean = dataSourceColumnBean;
    }

}
