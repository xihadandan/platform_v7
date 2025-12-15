/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template;

import com.wellsoft.context.jdbc.entity.IdEntity;
import freemarker.template.ObjectWrapper;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * Description: 模板引擎
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
public interface TemplateEngine {
    /**
     * 合并字符模板与数据对象输出结果
     *
     * @param source
     * @param data
     * @return
     * @throws Exception
     */
    public String process(String source, Object data) throws Exception;

    /**
     * 合并字符模板与数据对象输出结果, 可指定占位符处理
     *
     * @param source
     * @param data
     * @return
     * @throws Exception
     */
    public String process(String source, Object data, ObjectWrapper beanWrapper) throws Exception;

    /**
     * 将${variable}的变量形式转化为${variable!}
     *
     * @param source
     * @return
     */
    public String decorateSource(String source);

    /**
     * 合并实体集合、动态表单及默认常量数据
     *
     * @param entities
     * @param dytableMap
     * @param addDefaultConstant 是否添加默认常量变量
     * @param replaceExistKey    多个实体是否替换存在的属性变量
     * @return
     */
    public <ENTITY extends IdEntity> Map<Object, Object> mergeDataAsMap(Collection<ENTITY> entities, Map dytableMap,
                                                                        Map<String, Object> extraData, boolean addDefaultConstant, boolean replaceExistKey);

    /**
     * @param packageCls
     * @param templateName
     * @return
     */
    public String getTemplateAsString(Class<?> packageCls, String templateName);

    public abstract void addDefaultConstant(Map<Object, Object> root);

    public abstract void addDefaultConstantFillFormat(Map<Object, Object> root);

    void addDefaultConstant(Map<String, Object> root, Calendar calendar);

    void addDefaultConstantFillFormat(Map<String, Object> root, Calendar calendar);
}
