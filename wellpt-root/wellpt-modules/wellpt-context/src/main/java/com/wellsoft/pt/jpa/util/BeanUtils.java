/*
 * @(#)BeanUtils.java 2012-10-16 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.util;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Description: 对象属性复制工具类
 *
 * @author zhulh
 * @date 2012-10-16
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-16.1	zhulh		2012-10-16		Create
 * 2012-11-16.1	zhulh		2012-11-16		改为spring的BeanUtils
 * </pre>
 */

/**
 * zyguo 后面需要把这些属性复制的方法全部改用methodAccess方式来实现，目前还没有改造
 * 不用 org.springframework.beans.BeanUtils 和 apache的BeanUtils ，是因为这两个的效率太低
 * 所以用 ReflectASM  来实现,
 * 参考文档：
 * https://blog.csdn.net/liaodehong/article/details/50379351
 * https://www.cnblogs.com/Frank-Hao/p/5839096.html
 */
public class BeanUtils {
    // 用来做缓存，从而提高效率
    public static Map<String, MethodAccess> methodAccessMap = new HashMap<String, MethodAccess>();

    /**
     * 忽略的属性集合
     */
    private static HashMap<String, Set<String>> ignorePropertyMap = new HashMap<String, Set<String>>();

    /**
     * 属性拷贝
     *
     * @param source 原始对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getIgnoreProperties(source, target));
    }

    /**
     * 属性拷贝（只拷贝参考类拥有的属性）
     *
     * @param source   原始对象
     * @param target   目标对象
     * @param editable 属性参考类
     */
    public static void copyProperties(Object source, Object target, Class<?> editable) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, editable);
    }

    /**
     * 属性拷贝(忽略某些属性)
     *
     * @param source           原始对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性
     */
    public static void copyProperties(Object source, Object target, String[] ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                getIgnoreProperties(source, target, ignoreProperties));
    }

    public static void copyProperties(Object source, Object target, String[] ignoreProperties, Class[] ignorePropertyOrAnnotationClass) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();

        PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (value != null && ArrayUtils.isNotEmpty(ignorePropertyOrAnnotationClass)) {
                                boolean ignored = false;
                                for (Class ignoreClazz : ignorePropertyOrAnnotationClass) {
                                    if (ignoreClazz.isAssignableFrom(value.getClass()) || AnnotationUtils.findAnnotation(readMethod, ignoreClazz) != null) {
                                        ignored = true;
                                        break;
                                    }
                                }
                                if (ignored) {
                                    continue;
                                }
                            }
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * 属性拷贝(忽略指定的属性，并且忽略IdEntity的6大基本属性)
     *
     * @param source           原始对象
     * @param target           目标对象
     * @param ignoreProperties 忽略的属性
     */
    public static void copyPropertiesExcludeBaseField(Object source, Object target, String[] ignoreProperties) {
        String[] excludeFields = ignoreProperties == null ? new String[]{} : ignoreProperties;
        excludeFields = ArrayUtils.addAll(excludeFields, IdEntity.BASE_FIELDS);
        org.springframework.beans.BeanUtils.copyProperties(source, target,
                getIgnoreProperties(source, target, excludeFields));
    }

    /**
     * 集合的对象的属性一一对应拷贝到返回的集合中
     *
     * @param sources     原始数据集合
     * @param targetClass 拷贝的目标对象类
     * @return 目标对象集合
     */
    @SuppressWarnings("unchecked")
    public static <ENTITY extends IdEntity, COLLECTION extends Collection<ENTITY>> COLLECTION convertCollection(
            COLLECTION sources, Class<ENTITY> targetClass) {
        COLLECTION targets = null;
        try {
            if (Set.class.isAssignableFrom(sources.getClass())) {
                targets = (COLLECTION) new LinkedHashSet<ENTITY>();
            } else {
                targets = (COLLECTION) new ArrayList<ENTITY>();
            }
            Iterator<ENTITY> it = sources.iterator();
            while (it.hasNext()) {
                ENTITY target = targetClass.newInstance();
                BeanUtils.copyProperties(it.next(), target);
                targets.add(target);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return targets;
    }

    /**
     * 集合的对象的属性一一对应拷贝到返回的集合中
     *
     * @param sources     原始数据集合
     * @param targetClass 拷贝的目标对象类
     * @return 目标对象集合
     */
    @SuppressWarnings("unchecked")
    public static <SOURCE extends Object, TARGET extends Object, SOURCECOLLECTION extends Collection<SOURCE>, TARGETCOLLECTION extends Collection<TARGET>> TARGETCOLLECTION copyCollection(
            SOURCECOLLECTION sources, Class<TARGET> targetClass) {
        Collection<Object> targets = null;
        try {
            if (Set.class.isAssignableFrom(sources.getClass())) {
                targets = new LinkedHashSet<Object>();
            } else {
                targets = new ArrayList<Object>();
            }
            Iterator<IdEntity> it = (Iterator<IdEntity>) sources.iterator();
            while (it.hasNext()) {
                TARGET target = targetClass.newInstance();
                BeanUtils.copyProperties(it.next(), target);
                targets.add(target);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (TARGETCOLLECTION) targets;
    }

    private static String[] getIgnoreProperties(Object source, Object target, String... ignoreProperties) {
        String key = target.getClass().getCanonicalName();
        if (!ignorePropertyMap.containsKey(key)) {
            setIgnoreProperties(target);
        }

        Set<String> returnSet = new HashSet<String>(0);
        Set<String> propertySet = ignorePropertyMap.get(key);
        if (propertySet != null && !propertySet.isEmpty()) {
            returnSet.addAll(propertySet);
        }

        // 目标IdEntity的创建人/时间不为空时，忽略掉，不进行属性复制
        if (target instanceof IdEntity) {
            IdEntity targetIdEntity = (IdEntity) target;
            if (targetIdEntity.getCreator() != null) {
                returnSet.add(IdEntity.CREATOR);
            }
            if (targetIdEntity.getCreateTime() != null) {
                returnSet.add(IdEntity.CREATE_TIME);
            }
        }
        if (target instanceof TenantEntity) {
            TenantEntity targetIdEntity = (TenantEntity) target;
            if (targetIdEntity.getSystemUnitId() != null) {
                returnSet.add(TenantEntity.SYSTEM_UNIT_ID);
            }
        }

        for (String ignore : ignoreProperties) {
            returnSet.add(ignore);
        }
        return returnSet.toArray(new String[0]);
    }

    /**
     * 如何描述该方法
     *
     * @param target
     */
    private static void setIgnoreProperties(Object target) {
        Class<?> cls = target.getClass();
        while (IdEntity.class.isAssignableFrom(cls)) {
            String key = target.getClass().getCanonicalName();
            if (!ignorePropertyMap.containsKey(key)) {
                ignorePropertyMap.put(key, new HashSet<String>());
            }
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                UnCloneable unCloneable = field.getAnnotation(UnCloneable.class);
                if (unCloneable != null) {
                    ignorePropertyMap.get(key).add(field.getName());
                }
            }

            cls = cls.getSuperclass();
        }
    }

    public static MethodAccess getMethodAccess(Object obj) {
        String className = obj.getClass().getName();
        MethodAccess methodAccess = methodAccessMap.get(className);
        if (methodAccess == null) {
            methodAccess = MethodAccess.get(obj.getClass());
            methodAccessMap.put(className, methodAccess);
        }
        return methodAccess;
    }

    /**
     * @param source 要拷贝的对象
     * @return
     * @Description <p>获取到对象中属性为null的属性名  </P>
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        result = ArrayUtils.addAll(result, IdEntity.BASE_FIELDS);
        return emptyNames.toArray(result);
    }

}
