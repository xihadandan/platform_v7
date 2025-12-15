/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
public class CustomInterfaceFormRepository extends AbstractFormRepository {

    @Autowired
    private Map<String, CustomFormRepository> customFormRepositoryMap;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfMainform(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public Map<String, Object> getDataOfMainform(String formId, String dataUuid, FormRepositoryContext repositoryContext) {
        return customFormRepositoryMap.get(repositoryContext.getCustomInterface()).getDataOfMainform(formId, dataUuid,
                repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfSubform(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<Map<String, Object>> getDataOfSubform(String subformId, String mainformId, String dataUuidOfMainform,
                                                      FormRepositoryContext repositoryContext) {
        return customFormRepositoryMap.get(repositoryContext.getCustomInterface()).getDataOfSubform(subformId,
                mainformId, dataUuidOfMainform, repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#saveFormData(com.wellsoft.pt.dyform.implement.repository.FormData, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public String saveFormData(FormData formData, FormRepositoryContext repositoryContext) {
        return customFormRepositoryMap.get(repositoryContext.getCustomInterface()).saveFormData(formData,
                repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public FormDataQueryData query(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        return customFormRepositoryMap.get(repositoryContext.getCustomInterface()).query(queryInfo, repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#count(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public long count(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        return customFormRepositoryMap.get(repositoryContext.getCustomInterface()).count(queryInfo, repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteFormData(java.lang.String, java.lang.String, boolean, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteFormData(String formId, String dataUuid, boolean cascade, FormRepositoryContext repositoryContext) {
        customFormRepositoryMap.get(repositoryContext.getCustomInterface()).deleteFormData(formId, dataUuid, cascade,
                repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String formId, String dataUuid, FormRepositoryContext repositoryContext) {
        customFormRepositoryMap.get(repositoryContext.getCustomInterface()).deleteSubformData(formId, dataUuid,
                repositoryContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String formId, String dataUuid, String subformId,
                                  FormRepositoryContext repositoryContext) {
        customFormRepositoryMap.get(repositoryContext.getCustomInterface()).deleteSubformData(formId, dataUuid,
                subformId, repositoryContext);
    }

}
