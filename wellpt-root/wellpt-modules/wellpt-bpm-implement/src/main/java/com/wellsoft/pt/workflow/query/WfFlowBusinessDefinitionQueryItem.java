/*
 * @(#)11/16/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/16/22.1	zhulh		11/16/22		Create
 * </pre>
 * @date 11/16/22
 */
public class WfFlowBusinessDefinitionQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = -2696111996687640542L;

    private String name;

    private String id;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }
}
