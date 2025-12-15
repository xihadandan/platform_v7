/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月14日.1	zhongzh		2017年10月14日		Create
 * </pre>
 * @date 2017年10月14日
 */
public abstract class MapperContants {

    public final static String BEAN_FACTORY = "beanFactory";
    public final static String FACTORY_BEAN_ID = "factoryBeanId";
    public final static String DATA_FACTORY = "dataFactory";
    public final static String FACTORY_DATA_ID = "factoryDataId";

    public final static String MAPPING_XML_SCHEMA = "http://dozer.sourceforge.net/schema/beanmapping.xsd";
    public final static String MAPPING_JSON_SCHEMA = "http://dozer.sourceforge.net/schema/beanmapping.jsd";

    public static final String SCHEMA_MAP = "java.util.Map";
    public static final String SCHEMA_DYFORM = "com.wellsoft.pt.dyform.facade.dto.DyFormData";

    public final static String[] MAPPING_METHODS_DYFORM = new String[]{"getFieldValue", "setFieldValue"};
    public final static String[] MAPPING_METHODS_MAP = new String[]{"get", "put"};
    public final static String[] MAPPING_METHODS_DEFAULT = new String[]{null, null};

    public final static String dyformdata = "dyformdata";// 表单
    public final static String entity = "entity"; // 平台实体
    public final static String hql = "hql"; // hql查询
    public final static String sql = "sql"; // sql查询

}
