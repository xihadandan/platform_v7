/*
 * @(#)2014-4-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.reflection;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-2.1	zhulh		2014-4-2		Create
 * </pre>
 * @date 2014-4-2
 */
public class ServiceInvokeUtils {

    private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    public static Object invoke(String service, final Class<?>[] parameterTypes, final Object... args) {
        try {
            Service srv = getService(service, parameterTypes, args);
            return srv.getMethod().invoke(srv.getService(), args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isServiceExist(String service, final Class<?>[] parameterTypes, final Object... args) {
        try {
            getService(service, parameterTypes, args);
            return true;
        } catch (Exception e) {
            logger.info(new StringBuilder("service[").append(service).append("] not exist.").toString(), e);
            return false;
        }
    }

    public static Service getService(String service, final Class<?>[] parameterTypes, final Object... args)
            throws SecurityException, NoSuchMethodException {

        String[] services = StringUtils.split(service, ".");
        String serviceName = services[0];
        String methodName = services[1];
        Object serviceObject = ApplicationContextHolder.getBean(serviceName);
        Method method = serviceObject.getClass().getMethod(methodName, parameterTypes);
        ServiceInvokeUtils utils = new ServiceInvokeUtils();
        Service srv = utils.new Service();
        srv.setMethod(method);
        srv.setService(serviceObject);
        return srv;

    }

    class Service {
        private Object service;
        private Method method;

        public Object getService() {
            return service;
        }

        public void setService(Object service) {
            this.service = service;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }
}
