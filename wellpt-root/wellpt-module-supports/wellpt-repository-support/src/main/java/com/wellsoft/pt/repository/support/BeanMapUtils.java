package com.wellsoft.pt.repository.support;

import com.wellsoft.pt.repository.entity.mongo.Id;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * JavaBean and map converter.
 */
public final class BeanMapUtils {
    private static final Logger LOG = Logger.getLogger(BeanMapUtils.class);

    /**
     * Converts a map to a JavaBean.
     *
     * @param type type to convert
     * @param map  map to convert
     * @return JavaBean converted
     * @throws IntrospectionException    failed to get class fields
     * @throws IllegalAccessException    failed to instant JavaBean
     * @throws InstantiationException    failed to instant JavaBean
     * @throws InvocationTargetException failed to call setters
     */
    public static final Object toBean(Class<?> type, Map<String, ? extends Object> map)
            throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        Object obj = type.newInstance();
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (map.containsKey(propertyName)) {
                Object value = map.get(propertyName);
                Object[] args = new Object[1];
                args[0] = value;
                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    /**
     * Converts a JavaBean to a map.
     *
     * @param bean JavaBean to convert
     * @param cfg
     * @return map converted
     * @throws IntrospectionException    failed to get class fields
     * @throws IllegalAccessException    failed to instant JavaBean
     * @throws InvocationTargetException failed to call setters
     */
    public static final Map<String, Object> toMap(Object bean, MapConfig cfg)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if (cfg == null)
            cfg = new MapConfig();
        Map<String, Object> returnMap = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];

            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {

                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                Class clazz = descriptor.getPropertyType();
                String packageName = clazz.getPackage().getName();

                if (packageName.indexOf(".proxy") != -1//代理对象的信息
                        || cfg.fliter.apply(clazz, propertyName, result)//是不是过滤的属性
                ) {
                    continue;
                }
                if (result != null) {
                    if (Serializable.class.isAssignableFrom(clazz)) {//内置数据类型包括String类型
                        returnMap.put(propertyName, result);
                    } else if (Collection.class.isAssignableFrom(clazz)) {//判断是否是集合
                        Collection c = (Collection) result;
                        Iterator it = c.iterator();
                        List<Map<String, Object>> subs = new ArrayList<Map<String, Object>>();
                        boolean isSerializable = false;
                        while (it.hasNext()) {
                            Object obj = it.next();
                            if (isPrimitive(obj.getClass())) {//内置数据类型包括String类型
                                isSerializable = true;
                                break;
                            } else {
                                subs.add(toMap(obj, cfg));
                            }
                        }
                        if (isSerializable) {
                            returnMap.put(propertyName, result);
                        } else {
                            returnMap.put(propertyName, subs);
                        }
                    } else {
                        if ((result instanceof Id)) {//mongodb的ObjectId
                            Id id = (Id) result;
                            if (id.get$oid() != null) {
                                returnMap.put(propertyName, new ObjectId(id.get$oid()));
                            } else {
                                returnMap.put(propertyName, null);
                            }

                        } else {
                            returnMap.put(propertyName, toMap(result, cfg));
                        }

                    }

                } else {
                    if (Collection.class.isAssignableFrom(clazz)) {
                        returnMap.put(propertyName, new ArrayList());
                    } else {
                        returnMap.put(propertyName, null);
                    }

                }
            }
        }
        return returnMap;
    }

    public static boolean isPrimitive(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if (clz == String.class || clz == Date.class) {
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(isPrimitive(Date.class));
    }

}
