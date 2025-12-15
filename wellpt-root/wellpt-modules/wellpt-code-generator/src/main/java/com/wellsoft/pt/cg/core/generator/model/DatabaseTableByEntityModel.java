/*
 * @(#)2015年8月10日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.EntitySource;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.CustomSchemaUpdate;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.SessionFactoryImpl;

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
 * 2015年8月10日.1	zhulh		2015年8月10日		Create
 * </pre>
 * @date 2015年8月10日
 */
public class DatabaseTableByEntityModel extends AbstractModel {

    private static final int MODELCODE = Type.OUTPUTTYPE_DATABASE_TABLE;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#getCode()
     */
    @Override
    public int getCode() {
        return MODELCODE;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Model#work(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public void work(Context context) {
        EntitySource source = (EntitySource) context.getSource();
        Configuration cfg = new Configuration();
        cfg.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
        for (Class<?> clazz : source.getClazzs()) {
            cfg.addAnnotatedClass(clazz);
        }
        Properties properties = new Properties();
        properties.put(Environment.HBM2DDL_AUTO, "update");
        cfg.setProperties(properties);
        CustomSchemaUpdate schemaUpdate = new CustomSchemaUpdate(
                ((SessionFactoryImpl) getSessionFactory()).getServiceRegistry(), cfg, null);
        schemaUpdate.execute(true, true);
    }

    /**
     * @return
     */
    private SessionFactory getSessionFactory() {
        return SessionFactoryUtils.getMultiTenantSessionFactory();
    }

}
