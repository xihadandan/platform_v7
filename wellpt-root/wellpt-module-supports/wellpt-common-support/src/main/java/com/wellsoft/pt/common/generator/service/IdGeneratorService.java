/*
 * @(#)2013-6-23 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.generator.service;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-23.1	rzhu		2013-6-23		Create
 * </pre>
 * @date 2013-6-23
 */
public interface IdGeneratorService {
    <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern);

    <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, String prefix);

    String generate(String tableName, String pattern);

    <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, boolean checkUnique);

    <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern, String prefix,
                                              boolean checkUnique);

    String generate(String tableName, String pattern, boolean checkUnique);

    String getBySysDate();
}
