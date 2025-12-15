/*
 * @(#)2017年10月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support.factory;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.mapper.DataFactory;
import com.wellsoft.pt.basicdata.mapper.MapperException;
import com.wellsoft.pt.basicdata.mapper.support.AliasToCaseInsensitiveMapResultTransformer;
import com.wellsoft.pt.basicdata.mapper.support.BaseDataFactory;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.hibernate.Query;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月12日.1	zhongzh		2017年10月12日		Create
 * </pre>
 * @date 2017年10月12日
 */
public class SqlDataFactory extends BaseDataFactory implements DataFactory {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.dozer.BeanFactory#createBean(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
        return new CaseInsensitiveMap();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.mapper.DataFactory#createData(java.lang.Object, java.lang.Class, java.lang.String)
     */
    @Override
    public Object createData(Object dest, Class<?> destClass, String targetDataId) {
        if (targetDataId == null) {
            throw new MapperException("SqlDataFactory targetDataId is not null");
        }
        targetDataId = targetDataId.toLowerCase();
        if (targetDataId.startsWith("select ")) {
            UniversalDao dao = ApplicationContextHolder.getBean("universalDao", UniversalDao.class);
            Query query = dao.getSession().createSQLQuery(targetDataId);
            query.setResultTransformer(AliasToCaseInsensitiveMapResultTransformer.INSTANCE);
            @SuppressWarnings("unchecked")
            List<Object> lists = query.setMaxResults(1).list();
            return lists.isEmpty() ? null : lists.get(0);
        }
        return null;
    }
}
