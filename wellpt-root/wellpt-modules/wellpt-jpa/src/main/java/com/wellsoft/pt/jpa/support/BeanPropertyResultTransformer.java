/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import org.apache.commons.io.IOUtils;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.util.StringUtils;

import javax.sql.rowset.serial.SerialClob;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-7.1	zhulh		2014-7-7		Create
 * </pre>
 * @date 2014-7-7
 */
public class BeanPropertyResultTransformer extends AliasedTupleSubsetResultTransformer {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7357529342782352374L;
    // IMPL NOTE : due to the delayed population of setters (setters cached
    // for performance), we really cannot properly define equality for
    // this transformer

    private static Logger LOG = LoggerFactory.getLogger(BeanPropertyResultTransformer.class);

    /**
     * The class we are mapping to
     */
    private Class<?> mappedClass;

    /**
     * Map of the fields we provide mapping for
     */
    private Map<String, PropertyDescriptor> mappedFields;

    /**
     * Set of bean properties we provide mapping for
     */
    private Map<String, String> mappedProperties;

    public BeanPropertyResultTransformer(Class<?> resultClass) {
        if (this.mappedClass == null) {
            initialize(resultClass);
        } else {
            if (!this.mappedClass.equals(resultClass)) {
                throw new InvalidDataAccessApiUsageException(
                        "The mapped class can not be reassigned to map to "
                                + resultClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    public static Object getResultSetValue(Object value, Class<?> requiredType) {
        Object newValue = value;
        if (value instanceof BigDecimal) {
            BigDecimal decimalValue = (BigDecimal) value;
            if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
                return decimalValue.shortValue();
            } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
                return decimalValue.intValue();
            } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
                return decimalValue.longValue();
            } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
                return decimalValue.floatValue();
            } else if (double.class.equals(requiredType) || Double.class.equals(requiredType)
                    || Number.class.equals(requiredType)) {
                return decimalValue.doubleValue();
            } else if ((boolean.class.equals(requiredType) || Boolean.class.equals(requiredType))
                    && decimalValue != null) {
                return Integer.valueOf(1).equals(decimalValue.intValue());
            } else if (Enum.class.isAssignableFrom(requiredType) && value != null) {
                try {
                    return requiredType.getEnumConstants()[((BigDecimal) value).intValue()];
                } catch (Exception e) {
                    return null;
                }
            }
        }
        if (value instanceof Clob && String.class.isAssignableFrom(requiredType)) {
            try {
                return IOUtils.toString(((Clob) value).getCharacterStream());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        if (Clob.class.isAssignableFrom(requiredType) && value instanceof String) {
            try {
                return new SerialClob(((String) value).toCharArray());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        if (value instanceof Integer && requiredType.isAssignableFrom(Boolean.class) && ("1".equals(
                value.toString()) || "0".equals(value.toString()))) {
            return value.toString();
        }

        return newValue;
    }

    /**
     * Initialize the mapping metadata for the given class.
     *
     * @param mappedClass the mapped class.
     */
    protected void initialize(Class<?> resultClass) {
        this.mappedClass = resultClass;
        this.mappedFields = new HashMap<String, PropertyDescriptor>();
        this.mappedProperties = new HashMap<String, String>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                String propertyName = pd.getName();
                this.mappedFields.put(propertyName.toLowerCase(), pd);
                this.mappedProperties.put(propertyName.toLowerCase(), propertyName);
                String underscoredName = underscoreName(propertyName);
                if (!propertyName.toLowerCase().equals(underscoredName)) {
                    this.mappedFields.put(underscoredName, pd);
                    this.mappedProperties.put(underscoredName, propertyName);
                }
            }
        }
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     *
     * @param name the string containing original name
     * @return the converted name
     */
    private String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1).toLowerCase());
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = s.toLowerCase();
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see org.hibernate.transform.TupleSubsetResultTransformer#isTransformedValueATupleElement(java.lang.String[], int)
     */
    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object mappedObject = BeanUtils.instantiate(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i].toLowerCase();
            PropertyDescriptor pd = mappedFields.get(alias);
            if (pd == null) {
                continue;
            }
            Object value = getResultSetValue(tuple[i], pd.getPropertyType());
            String propertyName = this.mappedProperties.get(alias);
            bw.setPropertyValue(propertyName, value);
        }
        return mappedObject;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mappedClass == null) ? 0 : mappedClass.hashCode());
        result = prime * result + ((mappedProperties == null) ? 0 : mappedProperties.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BeanPropertyResultTransformer other = (BeanPropertyResultTransformer) obj;
        if (mappedClass == null) {
            if (other.mappedClass != null)
                return false;
        } else if (!mappedClass.equals(other.mappedClass))
            return false;
        if (mappedProperties == null) {
            if (other.mappedProperties != null)
                return false;
        } else if (!mappedProperties.equals(other.mappedProperties))
            return false;
        return true;
    }

}
