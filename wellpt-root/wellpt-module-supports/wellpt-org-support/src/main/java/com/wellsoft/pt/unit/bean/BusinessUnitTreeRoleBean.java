/*
 * @(#)2014-10-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.bean;

import com.wellsoft.pt.unit.entity.BusinessUnitTreeRole;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-13.1	zhulh		2014-10-13		Create
 * </pre>
 * @date 2014-10-13
 */
public class BusinessUnitTreeRoleBean extends BusinessUnitTreeRole {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1067741562662797569L;

    // jqGrid的行标识
    private String id;

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
