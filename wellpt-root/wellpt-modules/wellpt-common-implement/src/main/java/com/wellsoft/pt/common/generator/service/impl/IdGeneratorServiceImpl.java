/*
 * @(#)2013-6-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.generator.service.impl;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.common.generator.dao.IdGeneratorDao;
import com.wellsoft.pt.common.generator.entity.IdGenerator;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.DaoUtils;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-23.1	rzhu		2013-6-23		Create
 * </pre>
 * @date 2013-6-23
 */
@Service
@Transactional
public class IdGeneratorServiceImpl extends BaseServiceImpl implements IdGeneratorService {

    @Autowired
    private IdGeneratorDao idGeneratorDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.Class, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern) {
        return generate(entityClass, pattern, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.Class, java.lang.String, boolean)
     */
    @Override
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, boolean checkUnique) {
        Annotation annotation = entityClass.getAnnotation(CommonEntity.class);
        if (annotation != null) {
            throw new RuntimeException("实体类[" + entityClass.getCanonicalName() + "]不是租户库中的类");
        }

        if (checkUnique) {
            return createUnique(entityClass.getCanonicalName(), pattern, StringUtils.EMPTY);
        }
        return createNoUnique(entityClass.getCanonicalName(), pattern, StringUtils.EMPTY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.Class, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, String prefix,
                                                     boolean checkUnique) {
        Annotation annotation = entityClass.getAnnotation(CommonEntity.class);
        if (annotation != null) {
            throw new RuntimeException("实体类[" + entityClass.getCanonicalName() + "]不是租户库中的类");
        }

        if (checkUnique) {
            return createUnique(entityClass.getCanonicalName(), pattern, prefix);
        }
        return createNoUnique(entityClass.getCanonicalName(), pattern, prefix);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.String, java.lang.String)
     */
    @Override
    public String generate(String tableName, String pattern) {
        return generate(tableName, pattern, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#generate(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public String generate(String tableName, String pattern, boolean checkUnique) {
        if (checkUnique) {
            return createUnique(tableName, pattern, StringUtils.EMPTY);
        }
        return createNoUnique(tableName, pattern, StringUtils.EMPTY);
    }

    /**
     * @param entityClass
     * @param pattern
     * @return
     */
    private <ENTITY> String createUnique(String entityClassName, String pattern, String prefix) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        String tenantId = userDetail.getTenantId();
        long nextPointer = 1l;
        IdGenerator currentGenerator = getCurrentGenerator(entityClassName, userDetail.getTenantId());
        if (currentGenerator != null) {
            nextPointer = currentGenerator.getPointer();
            nextPointer++;
        }
        try {
            Object cls = Class.forName(entityClassName).newInstance();
            IdEntity queryModel = (IdEntity) DaoUtils.instance(cls.getClass());
            BeanWrapper ebw = PropertyAccessorFactory.forBeanPropertyAccess(queryModel);
            if (ebw.isWritableProperty("id")) {
                ebw.setPropertyValue("id", prefix + decimalFormat.format(nextPointer));
                while (DaoUtils.isExists("idGeneratorDao", queryModel, "id")) {
                    nextPointer++;
                    ebw.setPropertyValue("id", prefix + decimalFormat.format(nextPointer));
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        IdGenerator idGenerator = currentGenerator;
        if (idGenerator == null) {
            idGenerator = new IdGenerator();
        }
        idGenerator.setEntityClassName(entityClassName);
        idGenerator.setPointer(nextPointer);
        idGenerator.setTenantId(tenantId);
        idGeneratorDao.save(idGenerator);
        return prefix + decimalFormat.format(idGenerator.getPointer());
    }

    /**
     * @param entityClassName
     * @param tenantId
     * @return
     */
    private IdGenerator getCurrentGenerator(String entityClassName, String tenantId) {
        IdGenerator idGenerator = new IdGenerator();
        idGenerator.setEntityClassName(entityClassName);
        idGenerator.setTenantId(tenantId);
        List<IdGenerator> generators = idGeneratorDao.findByExample(idGenerator);
        if (generators.size() > 1) {
            throw new RuntimeException("数据错误，" + entityClassName + "存在多个ID生成器！");
        }
        if (CollectionUtils.isEmpty(generators)) {
            return null;
        }
        return generators.get(0);
    }

    /**
     * @param entityClass
     * @param pattern
     * @return
     */
    private <ENTITY> String createNoUnique(String entityClassName, String pattern, String prefix) {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        String tenantId = userDetail.getTenantId();
        IdGenerator idGenerator = getCurrentGenerator(entityClassName, tenantId);
        if (idGenerator == null) {
            idGenerator = new IdGenerator();
            idGenerator.setEntityClassName(entityClassName);
            idGenerator.setPointer(1l);
            idGenerator.setTenantId(tenantId);
        } else {
            idGenerator.setPointer(idGenerator.getPointer() + 1);
        }
        idGeneratorDao.save(idGenerator);
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return prefix + decimalFormat.format(idGenerator.getPointer());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.generator.service.IdGeneratorService#getBySysDate()
     */
    @Override
    @Transactional(readOnly = true)
    public String getBySysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = this.dao.getSysDate();
        return dateFormat.format(date);
    }

}
