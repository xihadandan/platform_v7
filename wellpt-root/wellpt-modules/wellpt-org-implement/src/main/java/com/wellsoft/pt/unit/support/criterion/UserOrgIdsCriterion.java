/*
 * @(#)Jun 5, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support.criterion;

import org.springframework.stereotype.Component;

/**
 * Description: 用户相关联的组织查询
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jun 5, 2017.1	zhulh		Jun 5, 2017		Create
 * </pre>
 * @date Jun 5, 2017
 */
@Component
public class UserOrgIdsCriterion extends AbstractUnitTreeCriterion {

    /**
     *
     */
    public UserOrgIdsCriterion() {
        super();
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     * @param ignoreCase
     */
    public UserOrgIdsCriterion(String columnIndex, Object value, String op, boolean ignoreCase) {
        super(columnIndex, value, op, ignoreCase);
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     */
    public UserOrgIdsCriterion(String columnIndex, Object value, String op) {
        super(columnIndex, value, op);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.support.criterion.AbstractUnitTreeCriterion#getOrgType()
     */
    @Override
    public String getOrgType() {
        return "UserOrgIds";
    }

}
