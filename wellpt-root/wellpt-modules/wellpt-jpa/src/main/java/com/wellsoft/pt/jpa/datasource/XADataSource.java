/*
 * @(#)2013-12-4 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import com.wellsoft.pt.mt.entity.Tenant;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-4.1	zhulh		2013-12-4		Create
 * </pre>
 * @date 2013-12-4
 */
public interface XADataSource extends DataSource {

    public static final String TRANSACTION_READ_COMMITTED = "READ_COMMITTED";

    String getJndiName();

    String getXaDatasourceClass();

    int getMaxPoolSize();

    XADataSource buildInternal(Tenant tenant);

    /**
     * 是否调用java.sql.Connection#isValid();做连接校验
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.XADataSource#isEnableJdbc4ConnectionTest()
     */
    boolean isEnableJdbc4ConnectionTest();

    boolean isTestQueryEnable();

    String getTestQuery();

    int getMaxIdleTime();

}
