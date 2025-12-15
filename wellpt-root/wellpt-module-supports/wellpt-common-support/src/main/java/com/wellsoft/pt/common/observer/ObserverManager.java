package com.wellsoft.pt.common.observer;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 观察者管理器<br/><br/>
 * <a href="http://www.cnblogs.com/BeyondAnyTime/archive/2012/07/15/2592113.html">《观察者模板参考资料》</a>
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-4-26.1	hongjz		2017-4-26		Create
 * </pre>
 * @date 2017-4-26
 */
public class ObserverManager {
    static Logger logger = Logger.getLogger(ObserverManager.class);
    @SuppressWarnings("rawtypes")
    private static Map<Class, List<ObserverImplClassDescriptor>> observerMap = new ConcurrentHashMap<Class, List<ObserverImplClassDescriptor>>();

    /**
     * 为观察者基类添加实现类
     *
     * @param observerBaseClazz           观察者基类
     * @param observerImplClassDescriptor
     */
    @SuppressWarnings({"unchecked"})
    public static void register(Class<?> observerBaseClazz, ObserverImplClassDescriptor observerImplClassDescriptor) {
        List<ObserverImplClassDescriptor> observerImplClasses = null;
        if (observerMap.containsKey(observerBaseClazz)) {
            observerImplClasses = observerMap.get(observerBaseClazz);
        } else {
            observerImplClasses = Collections.synchronizedList(new ArrayList<ObserverImplClassDescriptor>());
            observerMap.put(observerBaseClazz, observerImplClasses);
        }
        Class<?> observerImplClazz = observerImplClassDescriptor.getObserverImplClazz();
        if (!observerBaseClazz.isAssignableFrom(observerImplClazz)) {
            String msg = "观察者实现类[" + observerImplClazz.getName() + "]并没有实现基类[" + observerBaseClazz.getName() + "]";
            logger.debug(msg);
            throw new RuntimeException(msg);
        }
        observerImplClasses.add(observerImplClassDescriptor);

        logger.debug("为观察者基类:[" + observerBaseClazz.getName() + "]添加了一个实现类:[" + observerImplClazz.getName()
                + "]，该实现类的作用为[" + observerImplClassDescriptor.getPurposeRemark() + "]");

    }

    /**
     * 获取观察者基类的所有实现类描述器(包含了实现类及作用)
     *
     * @param observerBaseClazz
     * @return
     */
    public static List<ObserverImplClassDescriptor> getObserverImplClassDescriptors(Class<?> observerBaseClazz) {
        List<ObserverImplClassDescriptor> observerImplClasses = observerMap.get(observerBaseClazz);
        List<ObserverImplClassDescriptor> observerImplClassesTmpList = new ArrayList<ObserverImplClassDescriptor>();
        if (observerImplClasses == null) {
            return observerImplClassesTmpList;
        } else {
            Collections.copy(observerImplClassesTmpList, observerImplClasses);
            return observerImplClassesTmpList;
        }
    }

    /**
     * 获取观察者基类的所有实现类
     *
     * @param observerBaseClazz
     * @return
     */
    public static List<Class<?>> getObserverImplClasses(Class<?> observerBaseClazz) {
        List<ObserverImplClassDescriptor> descriptors = getObserverImplClassDescriptors(observerBaseClazz);
        List<Class<?>> observerImplClasses = new ArrayList<Class<?>>();
        for (ObserverImplClassDescriptor descriptor : descriptors) {
            observerImplClasses.add(descriptor.getObserverImplClazz());
        }
        return observerImplClasses;
    }

    /**
     * 触发观察者基类所有实现类的方法
     *
     * @param observerBaseClazz
     * @param methodName
     * @param arguments
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static void invokeMethodForAllObserver(Class<?> observerBaseClazz, String methodName, Object... args)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        List<ObserverImplClassDescriptor> observerImplClassDescriptors = getObserverImplClassDescriptors(observerBaseClazz);

        for (ObserverImplClassDescriptor descriptor : observerImplClassDescriptors) {
            invokeMethod(descriptor, methodName, args);
        }

    }

    /**
     * 触发观察者基类实现类的方法
     *
     * @param observerBaseClazz
     * @param methodName
     * @param arguments
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invokeMethod(ObserverImplClassDescriptor descriptor, String methodName, Object... args)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {

        Class<?>[] parameterTypes = new Class<?>[args.length];

        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }

        String descriptorStr = "观察者[" + descriptor.getPurposeRemark() + "]实现类:["
                + descriptor.getObserverImplClazz().getName() + "]";
        descriptorStr = descriptorStr + "触发方法[" + methodName + "(" + StringUtils.join(parameterTypes, ", ") + ")]";
        logger.debug(descriptorStr + ", 开始处理....");
        @SuppressWarnings("unchecked")
        Object observerInstance = descriptor.getObserverImplInstance();
        if (observerInstance == null) {
            observerInstance = ApplicationContextHolder.getBean(descriptor.getObserverImplClazz());
            descriptor.setObserverImplInstance(observerInstance);
        }

        Method method = observerInstance.getClass().getMethod(methodName, parameterTypes);
        Object result = method.invoke(observerInstance, args);
        logger.debug(descriptorStr + ", 处理完毕....");
        return result;
    }

}
