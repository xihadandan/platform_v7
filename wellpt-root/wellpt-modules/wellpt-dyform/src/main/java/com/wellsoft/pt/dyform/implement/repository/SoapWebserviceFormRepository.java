/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.wellsoft.pt.api.BearerTokenBasedWellptSoapClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.service.WellptSoapWebService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
@Component
public class SoapWebserviceFormRepository extends RestApiFormRepository {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.RestApiFormRepository#getServiceClient(com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    protected WellptClient getServiceClient(FormRepositoryContext repositoryContext) {
        JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
        soapFactoryBean.setAddress(repositoryContext.getServiceUrl());
        soapFactoryBean.setServiceClass(WellptSoapWebService.class);
        Object o = soapFactoryBean.create();
        WellptSoapWebService service = (WellptSoapWebService) o;
        return new BearerTokenBasedWellptSoapClient(service, repositoryContext.getServiceToken());
    }

}
