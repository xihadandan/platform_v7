/*
 * @(#)2014-7-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao.impl;

import javax.annotation.PostConstruct;

/**
 * Description: 公共库原生HQL的DAO接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-9.1	zhulh		2014-7-9		Create
 * </pre>
 * @date 2014-7-9
 */
//@Repository
//@Scope(value = "prototype")
public class CommonNativeDaoImpl extends NativeDaoImpl {

    @Override
    @PostConstruct
    public void init() {
        // sessionFactory = (SessionFactory) ApplicationContextHolder.getBean(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
    }

}
