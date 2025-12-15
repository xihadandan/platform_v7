/*
 * @(#)2019年8月26日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月26日.1	zhulh		2019年8月26日		Create
 * </pre>
 * @date 2019年8月26日
 */
public class FormRepositoryContextFactory {

    /**
     * @param formUuid
     * @return
     */
    public static FormRepositoryContext getByFormUuid(String formUuid) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        FormDefinitionHandler formDefinitionHandler = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid))
                .doGetFormDefinitionHandler();
        if (StringUtils.isBlank(formDefinitionHandler.getPformUuid())) {
            return new FormRepositoryContext(formDefinitionHandler);
        } else {
            return new FormRepositoryContext(formDefinitionHandler.doGetPformFormDefinition()
                    .doGetFormDefinitionHandler());
        }
    }

    /**
     * @param formId
     * @return
     */
    public static FormRepositoryContext getByFormId(String formId) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        FormDefinitionHandler formDefinitionHandler = ((FormDefinition) dyFormFacade.getFormDefinitionById(formId))
                .doGetFormDefinitionHandler();
        if (StringUtils.isBlank(formDefinitionHandler.getPformUuid())) {
            return new FormRepositoryContext(formDefinitionHandler);
        } else {
            return new FormRepositoryContext(formDefinitionHandler.doGetPformFormDefinition()
                    .doGetFormDefinitionHandler());
        }
    }

    /**
     * @param tblName
     * @return
     */
    public static FormRepositoryContext getByTableName(String tblName) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        String formUuid = DyformCacheUtils.getFormUuidByTlbName(tblName);
        FormDefinitionHandler formDefinitionHandler = null;
        if (StringUtils.isNotBlank(formUuid)) {
            DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            formDefinitionHandler = ((FormDefinition) formFormDefinition).doGetFormDefinitionHandler();
        } else {
            List<DyFormFormDefinition> dyFormFormDefinitions = dyFormFacade.getFormDefinitionsByTblName(tblName);
            if (CollectionUtils.isNotEmpty(dyFormFormDefinitions)) {
                formDefinitionHandler = ((FormDefinition) dyFormFormDefinitions.get(0))
                        .doGetFormDefinitionHandler();
                DyformCacheUtils.setFormUuidByTlbName(tblName, formDefinitionHandler.getFormUuid());
            }
        }

        if (formDefinitionHandler != null) {
            if (StringUtils.isBlank(formDefinitionHandler.getPformUuid())) {
                return new FormRepositoryContext(formDefinitionHandler);
            } else {
                return new FormRepositoryContext(formDefinitionHandler.doGetPformFormDefinition()
                        .doGetFormDefinitionHandler());
            }
        }

        return null;
    }

}
