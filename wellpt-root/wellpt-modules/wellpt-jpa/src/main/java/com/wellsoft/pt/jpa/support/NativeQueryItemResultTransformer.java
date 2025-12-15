/*
 * @(#)2013-2-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-28.1	zhulh		2013-2-28		Create
 * </pre>
 * @date 2013-2-28
 */
public class NativeQueryItemResultTransformer extends AliasedTupleSubsetResultTransformer {

    public static final NativeQueryItemResultTransformer INSTANCE = new NativeQueryItemResultTransformer();
    /**
     *
     */
    private static final long serialVersionUID = 4195705427730178919L;

    private NativeQueryItemResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        QueryItem result = new QueryItem(tuple.length);
        if (aliases == null) {
            if (tuple[0] instanceof IdEntity) {
                BeanWrapper beanWrapper = new BeanWrapperImpl(tuple[0]);
                PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    String propertyName = propertyDescriptor.getName();
                    result.put(propertyName, beanWrapper.getPropertyValue(propertyName));
                }
            } else {
                throw new RuntimeException("query result transform error");
            }
        } else {
            for (int i = 0; i < tuple.length; i++) {
                String alias = aliases[i].toLowerCase();
                result.put(alias, tuple[i]);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.transform.TupleSubsetResultTransformer#
     * isTransformedValueATupleElement(java.lang.String[], int)
     */
    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
