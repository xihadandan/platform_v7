/*
 * @(#)2017年10月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper;

/**
 * Description: 如何描述该类
 * 根据mapId(classa://:?beanFactory=DyformDataFactory>>classb://:?beanFactory=DyformDataFactory)生成schema
 * 1、POJO生成字段
 * 2、JSON或MAP指定JSON SCHEMA文件或内容
 * 3、DyFormData根据formUuid取得字段
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年10月13日.1	zhongzh		2017年10月13日		Create
 * </pre>
 * @Reference https://github.com/joelittlejohn/jsonschema2pojo/wiki/Reference
 * @date 2017年10月13日
 */
public interface MapperSchema {

    /**
     * 如何描述serialVersionUID
     */

    public Object getSchemaA();

    public Object getSchemaB();
}
