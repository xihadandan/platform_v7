/*
 * @(#)2013-5-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.validation.service.impl;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.jpa.util.DaoUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-30.1	rzhu		2013-5-30		Create
 * </pre>
 * @date 2013-5-30
 */
@Service
@Transactional
public class CommonValidateServiceImpl extends BaseServiceImpl implements CommonValidateService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.CommonValidateService#checkExists(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkExists(String checkType, String fieldName, String fieldValue) {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Class<?> entityClass = entityClasses.get(checkType);
        BeanWrapper beanWrapper = new BeanWrapperImpl(entityClass);
        beanWrapper.setPropertyValue(fieldName, fieldValue);
        IdEntity entity = (IdEntity) beanWrapper.getWrappedInstance();
        CommonEntity commonEntity = entityClass.getAnnotation(CommonEntity.class);
        if (commonEntity != null) {
            return DaoUtils.isExists("tenantDao", entity, fieldName);
        }
        return DaoUtils.isExists("userDao", entity, fieldName);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.CommonValidateService#checkUnique(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkUnique(String uuid, String checkType, String fieldName, String fieldValue) {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Class<?> entityClass = entityClasses.get(checkType);
        BeanWrapper beanWrapper = new BeanWrapperImpl(entityClass);
        beanWrapper.setPropertyValue(fieldName, fieldValue);
        IdEntity entity = (IdEntity) beanWrapper.getWrappedInstance();
        String hql = "select count(*) from " + entity.getClass().getCanonicalName() + " where uuid != :uuid and "
                + fieldName + " = :fieldValue";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        values.put("fieldValue", beanWrapper.getPropertyValue(fieldName));
        CommonEntity commonEntity = entityClass.getAnnotation(CommonEntity.class);
        if (commonEntity != null) {
            return ((Long) ApplicationContextHolder.getBean("tenantDao", HibernateDao.class).findUnique(hql, values)) == 0;
        }
        return ((Long) ApplicationContextHolder.getBean("userDao", HibernateDao.class).findUnique(hql, values)) == 0;
    }

}
