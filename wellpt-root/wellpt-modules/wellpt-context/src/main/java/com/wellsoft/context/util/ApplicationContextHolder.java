package com.wellsoft.context.util;

import com.wellsoft.context.util.groovy.GroovyUseable;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

@GroovyUseable
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static ApplicationContextHolder applicationContextHolder;
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    public static ApplicationContextHolder getInstance() {
        if (applicationContextHolder == null) {
            applicationContextHolder = (ApplicationContextHolder) getBean("applicationContextHolder");
        }
        return applicationContextHolder;
    }

    private static void setAppContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
        ApplicationContextHolder.defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.setAppContext(applicationContext);
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return getApplicationContext().getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static boolean containsBean(String beanName) {
        return getApplicationContext().containsBean(beanName);
    }

    public static void publishEvent(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }

    public static DefaultListableBeanFactory defaultListableBeanFactory() {
        return ApplicationContextHolder.defaultListableBeanFactory;
    }

}
