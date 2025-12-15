/*
 * @(#)2017年5月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support.criterion;

import com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月18日.1	zhulh		2017年5月18日		Create
 * </pre>
 * @date 2017年5月18日
 */
@Component
public class UnitTreeMyUnitCriterion extends AbstractUnitTreeCriterion {

    /**
     *
     */
    public UnitTreeMyUnitCriterion() {
        super();
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     * @param ignoreCase
     */
    public UnitTreeMyUnitCriterion(String columnIndex, Object value, String op, boolean ignoreCase) {
        super(columnIndex, value, op, ignoreCase);
    }

    /**
     * @param columnIndex
     * @param value
     * @param op
     */
    public UnitTreeMyUnitCriterion(String columnIndex, Object value, String op) {
        super(columnIndex, value, op);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.support.criterion.AbstractUnitTreeCriterion#getOrgType()
     */
    @Override
    public String getOrgType() {
        return UnitTreeServiceImpl.ID_MYUNIT;
    }

}
