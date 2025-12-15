/*
 * @(#)2013-1-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.support;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-6.1	zhulh		2013-1-6		Create
 * </pre>
 * @date 2013-1-6
 */
public class TenantEntityAnnotatedClassesFactoryBean extends CommonEntityAnnotatedClassesFactoryBean {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.support.CommonEntityAnnotatedClassesFactoryBean#matchesFilter(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
     */
    @Override
    protected boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        if (ENTITY_TYPE_FILTER.match(reader, readerFactory) || COMMON_TYPE_FILTER.match(reader, readerFactory)) {
            return true;
        }
        return false;
    }

}
