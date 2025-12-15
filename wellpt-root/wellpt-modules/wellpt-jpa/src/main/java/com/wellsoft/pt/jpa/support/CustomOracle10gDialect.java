/*
 * @(#)2014-6-6 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-6.1	zhulh		2014-6-6		Create
 * </pre>
 * @date 2014-6-6
 */
public class CustomOracle10gDialect extends Oracle10gDialect {

    /**
     * 如何描述该构造方法
     */
    public CustomOracle10gDialect() {
        super();
        registerHibernateType(-101, StandardBasicTypes.TIMESTAMP.getName());
        registerHibernateType(-9, StandardBasicTypes.STRING.getName());
    }

}
