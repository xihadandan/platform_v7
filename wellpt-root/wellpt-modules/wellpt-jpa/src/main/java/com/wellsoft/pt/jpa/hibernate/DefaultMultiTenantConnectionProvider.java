/*
 * @(#)2012-12-1 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-1.1	lilin		2012-12-1		Create
 * </pre>
 * @date 2012-12-1
 */
@Component
public class DefaultMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider {
    private static final long serialVersionUID = -8571258027565913445L;

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return ConnectionProviderBuilder.buildConnectionProvider();
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return ConnectionProviderBuilder.buildConnectionProvider(tenantIdentifier);
    }
}
