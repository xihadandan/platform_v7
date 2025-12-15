/*
 * @(#)2020年12月29日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.profiles.dev;

import com.wellsoft.context.profile.OnDevProfileCondition;
import com.wellsoft.pt.WebMvcConfiguration;
import com.wellsoft.pt.jpa.generator.CustomAnnotationBeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Description: spring mvc开发环境配置
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
@EnableWebMvc
@Configuration
@Conditional(OnDevProfileCondition.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "com.wellsoft,springfox", lazyInit = true, useDefaultFilters = false, nameGenerator = CustomAnnotationBeanNameGenerator.class, includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)})
public class WebMvcDevConfiguration extends WebMvcConfiguration {
}
