/*
 * @(#)2013-12-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

import java.util.Properties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	zhulh		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
public abstract class AbstractXADataSource implements XADataSource {
    protected String jndiName;

    protected Properties driverProperties = new Properties();

    /**
     * @return the jndiName
     */
    @Override
    public String getJndiName() {
        return jndiName;
    }

    /**
     * @return the driverProperties
     */
    @Override
    public Properties getDriverProperties() {
        return driverProperties;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.XADataSource#getMaxPoolSize()
     */
    @Override
    public int getMaxPoolSize() {
        return 550;
    }

}
