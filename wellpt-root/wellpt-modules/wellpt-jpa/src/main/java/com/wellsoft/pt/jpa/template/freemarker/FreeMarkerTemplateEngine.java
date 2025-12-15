/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.template.AbstractTemplateEngine;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Description: 模板引擎接口的freemarker实现
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
@Component
public class FreeMarkerTemplateEngine extends AbstractTemplateEngine {
    public static final String IS_NOT_BLANK_METHOD = "isNotBlank";

    @Autowired(required = false)
    private List<CustomFreemarkerTemplateSharedVariable> sharedVariables;

    /**
     * @return the configuration
     */
    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setClassicCompatible(true);
        configuration.setStrictSyntaxMode(false);
        configuration.setObjectWrapper(new CustomBeanWrapper());
        if (sharedVariables != null) {
            checkDuplicateVariable();
            for (CustomFreemarkerTemplateSharedVariable sharedVariable : sharedVariables) {
                configuration.setSharedVariable(sharedVariable.getName(), sharedVariable.getValue());
            }
        }
        return configuration;
    }

    /**
     * 变量名重复检测
     */
    private void checkDuplicateVariable() {
        Set<String> existsVariables = new HashSet<String>();
        for (CustomFreemarkerTemplateSharedVariable sharedVariable : sharedVariables) {
            String sharedVariableName = sharedVariable.getName();
            if (!existsVariables.contains(sharedVariableName)) {
                existsVariables.add(sharedVariableName);
            } else {
                throw new RuntimeException("FreeMarker共享变量[" + sharedVariableName + "]已存在！");
            }
        }
    }

    /**
     * 1、数据对象是实体类，将对象及属性名放入Map中；
     * 2、数据对象是Map，复制原来的Map数据；
     * 3、数据对象是集合，直接放入key为list的Map中。
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.core.template.TemplateEngine#process(java.lang.String, java.lang.Object)
     */

    private String process(String source, Object data, Configuration config) throws Exception {
        if (StringUtils.isBlank(source)) {
            return source;
        }

        // 获取模板
        StringReader reader = new StringReader(source);
        Template template = new Template(UUID.randomUUID() + ".tpl", reader, config);

        // 将对象转换成Map数据
        Map<String, Object> rootMap = convert(data);

        // 合并数据输出
        StringWriter out = new StringWriter();
        Environment env = template.createProcessingEnvironment(rootMap, out, null);
        env.process();

        return out.toString();
    }

    @Override
    public String process(String source, Object data) throws Exception {
        Configuration config = getConfiguration();
        return this.process(source, data, config);
    }

    @Override
    public String process(String source, Object data, ObjectWrapper beanWrapper) throws Exception {
        if (beanWrapper != null) {
            Configuration config = getConfiguration();
            config.setObjectWrapper(beanWrapper);
            return this.process(source, data, config);

        }
        return this.process(source, data);
    }

    /**
     * 将对象转换成key-value的数据
     *
     * @param data
     * @return
     */
    private Map<String, Object> convert(Object data) {
        Map<String, Object> root = new HashMap<String, Object>();
        // 数据对象是实体类，将对象及属性名放入Map中
        if (data instanceof IdEntity) {
            root.put(StringUtils.lowerCase(data.getClass().getSimpleName()), data);
            BeanWrapperImpl wrapper = new BeanWrapperImpl(data);
            PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                // 相同属性，以第一个为准
                if (!root.containsKey(propertyName)) {
                    root.put(propertyName, wrapper.getPropertyValue(propertyName));
                }
            }
        } else if (data instanceof Map) {// 数据对象是Map，复制原来的Map数据
            for (Iterator<?> entryIter = ((Map<?, ?>) data).entrySet().iterator(); entryIter.hasNext(); ) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryIter.next();
                root.put(entry.getKey().toString(), entry.getValue());
            }
        } else if (data instanceof Collection) {// 数据对象是集合，直接放入key为list的Map中
            root.put("list", data);
        }
        return root;
    }

}
