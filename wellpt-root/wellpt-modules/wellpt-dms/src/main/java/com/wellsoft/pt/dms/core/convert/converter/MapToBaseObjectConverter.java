/*
 * @(#)Feb 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.convert.converter;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 19, 2017.1	zhulh		Feb 19, 2017		Create
 * </pre>
 * @date Feb 19, 2017
 */
public class MapToBaseObjectConverter implements ConditionalGenericConverter {

    private static final Map<TypeDescriptor, Map<String, Object>> typeMap = new ConcurrentHashMap<TypeDescriptor, Map<String, Object>>();
    private final ConversionService conversionService;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public MapToBaseObjectConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
     */
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Map.class, BaseObject.class));
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        try {
            BeanWrapper wrapper = new BeanWrapperImpl(targetType.getType().newInstance());
            Map<Object, Object> sourceMap = (Map<Object, Object>) source;
            Map<String, Object> map = getTargetTypePropertyDescriptor(wrapper, targetType);
            for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
                Object sourceKey = QueryItem.getKey((String) entry.getKey());
                Object sourceValue = entry.getValue();
                if (map.containsKey(sourceKey)) {
                    PropertyDescriptor propertyDescriptor = ((PropertyDescriptor) map.get(sourceKey));
                    String propertyName = propertyDescriptor.getName();
                    Object convertValue = null;
                    Class<?> propertyType = propertyDescriptor.getPropertyType();
                    if (propertyType.isAssignableFrom(Date.class)) {
                        convertValue = DateUtils.parse((String) sourceValue);
                    } else {
                        convertValue = conversionService.convert(sourceValue, propertyType);
                    }
                    wrapper.setPropertyValue(propertyName, convertValue);
                }
            }
            return wrapper.getWrappedInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param targetType
     * @return
     */
    private Map<String, Object> getTargetTypePropertyDescriptor(BeanWrapper wrapper, TypeDescriptor targetType) {
        if (!typeMap.containsKey(targetType)) {
            PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
            Map<String, Object> map = new QueryItem();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                map.put(propertyName, propertyDescriptor);
            }
            typeMap.put(targetType, map);
        }
        return typeMap.get(targetType);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
     */
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return true;
    }

}
