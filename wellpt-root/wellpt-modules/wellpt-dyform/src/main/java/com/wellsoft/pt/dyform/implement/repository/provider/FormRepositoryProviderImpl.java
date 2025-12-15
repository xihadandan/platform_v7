/*
 * @(#)2019年8月20日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.provider;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.implement.repository.*;
import com.wellsoft.pt.dyform.implement.repository.enums.FormRepositoryModeEnum;
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
 * 2019年8月20日.1	zhulh		2019年8月20日		Create
 * </pre>
 * @date 2019年8月20日
 */
@Component
public class FormRepositoryProviderImpl implements FormRepositoryProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepositoryProvider#provide(com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler)
     */
    @Override
    public FormRepository provide(FormRepositoryContext formRepositoryContext) {
        String repositoryMode = formRepositoryContext.getRepositoryMode();
        FormRepositoryModeEnum repositoryModeEnum = FormRepositoryModeEnum.value2Enum(repositoryMode);
        FormRepository formRepository = null;
        switch (repositoryModeEnum) {
            case Dyform:
                break;
            case UserTable:
                formRepository = ApplicationContextHolder.getBean("userTableFormRepository", UserTableFormRepository.class);
                break;
            case SoapWebservice:
                formRepository = ApplicationContextHolder.getBean("soapWebserviceFormRepository",
                        SoapWebserviceFormRepository.class);
                break;
            case RestfulApi:
                formRepository = ApplicationContextHolder.getBean("restApiFormRepository", RestApiFormRepository.class);
                break;
            case CustomInterface:
                formRepository = ApplicationContextHolder.getBean("customInterfaceFormRepository",
                        CustomInterfaceFormRepository.class);
                break;
            default:
                break;
        }
        return formRepository;
    }

}
