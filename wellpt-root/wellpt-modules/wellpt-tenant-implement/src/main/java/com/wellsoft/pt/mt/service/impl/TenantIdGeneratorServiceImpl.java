/*
 * @(#)2013-12-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.dao.TenantIdGeneratorDao;
import com.wellsoft.pt.mt.entity.TenantIdGenerator;
import com.wellsoft.pt.mt.service.TenantIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.text.DecimalFormat;

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
@Service
@Transactional
public class TenantIdGeneratorServiceImpl extends BaseServiceImpl implements TenantIdGeneratorService {
    @Autowired
    private TenantIdGeneratorDao tenantIdGeneratorDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.service.TenantIdGeneratorService#generate(java.lang.Class, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern) {
        Annotation annotation = entityClass.getAnnotation(CommonEntity.class);
        if (annotation == null) {
            throw new RuntimeException("实体类[" + entityClass.getCanonicalName() + "]不是公共库中的类");
        }

        String entityClassName = entityClass.getCanonicalName();
        TenantIdGenerator idGenerator = tenantIdGeneratorDao.get(entityClassName);
        if (idGenerator == null) {
            idGenerator = new TenantIdGenerator();
            idGenerator.setEntityClassName(entityClassName);
            idGenerator.setPointer(1l);
        } else {
            idGenerator.setPointer(idGenerator.getPointer() + 1);
        }
        tenantIdGeneratorDao.save(idGenerator);

        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(idGenerator.getPointer());
    }

}
