/*
 * @(#)2013-1-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.generator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Description: 自定义的spring bean名字生成器，将名字以"Impl"为结尾的去掉
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-16.1	zhulh		2013-1-16		Create
 * </pre>
 * @date 2013-1-16
 */
public class CustomAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.annotation.AnnotationBeanNameGenerator#generateBeanName(org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.BeanDefinitionRegistry)
     */
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanName = super.generateBeanName(definition, registry);
        return beanName.endsWith("Impl") ? beanName.substring(0, beanName.length() - 4) : beanName;
    }

}
