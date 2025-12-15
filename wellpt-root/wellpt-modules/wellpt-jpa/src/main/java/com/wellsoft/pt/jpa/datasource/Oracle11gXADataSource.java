/*
 * @(#)2013-12-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.datasource;

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
public class Oracle11gXADataSource extends Oracle10gXADataSource {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.jdbc.datasource.Oracle10gXADataSource#getType()
     */
    @Override
    public String getType() {
        return DatabaseType.Oracle11g.getName();
    }

}
