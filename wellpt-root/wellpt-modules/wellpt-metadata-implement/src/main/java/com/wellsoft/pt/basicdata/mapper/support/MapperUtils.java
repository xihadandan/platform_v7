/*
 * @(#)2017年10月14日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.mapper.support;

import java.net.URI;
import java.net.URISyntaxException;

import static com.wellsoft.pt.basicdata.mapper.support.MapperContants.*;

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
public abstract class MapperUtils {

    public final static URI buildBeanUri(String schema, String beanFactory, String factoryBeanId) {
        try {
            StringBuilder sb = new StringBuilder(schema);
            sb.append("://host:?q=bean");
            if (beanFactory != null) {
                sb.append("&").append(BEAN_FACTORY).append("=").append(beanFactory);
            }
            if (factoryBeanId != null) {
                sb.append("&").append(FACTORY_BEAN_ID).append("=").append(factoryBeanId);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
        }
        return null;
    }

    ;

    public final static URI buildDataUri(String schema, String dataFactory, String factoryDataId) {
        try {
            StringBuilder sb = new StringBuilder(schema);
            sb.append("://host:?q=data");
            if (dataFactory != null) {
                sb.append("&").append(DATA_FACTORY).append("=").append(dataFactory);
            }
            if (factoryDataId != null) {
                sb.append("&").append(FACTORY_DATA_ID).append("=").append(factoryDataId);
            }
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
        }
        return null;
    }

    ;

    public final static String buildMapId(String srcSchema, String dataFactory, String factoryDataId,
                                          String destSchema, String beanFactory, String factoryBeanId) {
        StringBuilder sb = new StringBuilder(srcSchema);
        sb.append("://host:?q=data");
        if (dataFactory != null) {
            sb.append("&").append(DATA_FACTORY).append("=").append(dataFactory);
        }
        if (factoryDataId != null) {
            sb.append("&").append(FACTORY_DATA_ID).append("=").append(factoryDataId);
        }
        sb.append(">>").append(destSchema);
        sb.append("://host:?q=bean");
        if (beanFactory != null) {
            sb.append("&").append(BEAN_FACTORY).append("=").append(beanFactory);
        }
        if (factoryBeanId != null) {
            sb.append("&").append(FACTORY_BEAN_ID).append("=").append(factoryBeanId);
        }
        return sb.toString();
    }

    ;
}
