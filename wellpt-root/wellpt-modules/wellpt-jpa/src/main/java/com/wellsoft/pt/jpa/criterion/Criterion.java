/*
 * @(#)2016年10月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criterion;

import com.wellsoft.pt.jpa.criteria.Criteria;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月24日.1	xiem		2016年10月24日		Create
 * </pre>
 * @date 2016年10月24日
 */
public interface Criterion {
    /**
     * 生成sql方法
     *
     * @return sql/hql
     */
    public String toSqlString(Criteria criteria);

    public org.hibernate.criterion.Criterion toHibernateCriterion();

}
