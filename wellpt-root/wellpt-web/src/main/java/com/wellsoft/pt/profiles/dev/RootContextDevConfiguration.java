/*
 * @(#)2020年12月29日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.profiles.dev;

import com.wellsoft.context.profile.OnDevProfileCondition;
import com.wellsoft.pt.RootContextConfiguration;
import com.wellsoft.pt.jpa.generator.CustomAnnotationBeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

/**
 * Description: spring 上下文开发环境配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月29日.1	zhulh		2020年12月29日		Create
 * </pre>
 * @date 2020年12月29日
 */
@Configuration
@Conditional(OnDevProfileCondition.class)
@ComponentScan(basePackages = "com.wellsoft", lazyInit = true, nameGenerator = CustomAnnotationBeanNameGenerator.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Lazy(false)
public class RootContextDevConfiguration extends RootContextConfiguration {

}
