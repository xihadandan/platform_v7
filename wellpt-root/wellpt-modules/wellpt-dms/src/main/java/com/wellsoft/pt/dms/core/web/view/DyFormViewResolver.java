/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.Store;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.web.View;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
@Component
public class DyFormViewResolver extends AbstractViewResolver {

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.ViewResolver#resolveView(com.wellsoft.pt.dms.core.context.ActionContext, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public View resolveView(ActionContext actionContext, HttpServletRequest request) {
        Configuration configuration = actionContext.getConfiguration();
        String formId = request.getParameter("formId");
        String formUuid = request.getParameter("formUuid");
        Store store = configuration.getStore(actionContext, request);
        String dataType = store.getDataType();
        String latestFormUuid = null;
        if (store.getUseDyFormLatestVersion()) {// 数据管理查看器配置的表单使用最新表单版本选项时，获取表单的最新版本UUID
            if (StringUtils.isNotBlank(formId)) {
                DyFormFormDefinition dyFormFormDefinition = dyFormApiFacade
                        .getFormDefinition(dyFormApiFacade.getFormUuidById(formId));
                DyFormFormDefinition latestDyFormDef = dyFormApiFacade
                        .getFormDefinitionOfMaxVersionById(dyFormFormDefinition.getId());
                latestFormUuid = latestDyFormDef.getUuid();
            } else if (StringUtils.isNotBlank(formUuid)) {
                DyFormFormDefinition dyFormFormDefinition = dyFormApiFacade.getFormDefinition(formUuid);
                DyFormFormDefinition latestDyFormDef = dyFormApiFacade
                        .getFormDefinitionOfMaxVersionById(dyFormFormDefinition.getId());
                latestFormUuid = latestDyFormDef.getUuid();
            } else if (StringUtils.isNotBlank(store.getFormUuid()) && isDyFormType(dataType)) {
                DyFormFormDefinition dyFormFormDefinition = dyFormApiFacade.getFormDefinition(store.getFormUuid());
                DyFormFormDefinition latestDyFormDef = dyFormApiFacade
                        .getFormDefinitionOfMaxVersionById(dyFormFormDefinition.getId());
                latestFormUuid = latestDyFormDef.getUuid();
            }
        }
        if (store.isUseCForm()) {
            String latestFormUuid2 = null;
            // 最新版本的扩展表单
            if (StringUtils.isNotBlank(latestFormUuid)) {
                latestFormUuid2 = dyFormApiFacade.getMaxExtFormUuidByFormUuid(latestFormUuid, "C");
            }
            // 默认版本的扩展表单
            if (StringUtils.isBlank(latestFormUuid2)) {
                if (StringUtils.isNotBlank(formId)) {
                    latestFormUuid2 = dyFormApiFacade
                            .getMaxExtFormUuidByFormUuid(dyFormApiFacade.getFormUuidById(formId), "C");
                } else if (StringUtils.isNotBlank(formUuid)) {
                    latestFormUuid2 = dyFormApiFacade.getMaxExtFormUuidByFormUuid(formUuid, "C");
                } else if (isDyFormType(dataType)) {
                    latestFormUuid2 = dyFormApiFacade.getMaxExtFormUuidByFormUuid(store.getFormUuid(), "C");
                }
            }
            if (StringUtils.isNotBlank(latestFormUuid2)) {
                latestFormUuid = latestFormUuid2;
            }
        }
        if (StringUtils.isNotBlank(formId)) {
            return new DyFormView(latestFormUuid == null ? dyFormApiFacade.getFormUuidById(formId) : latestFormUuid,
                    getIdValue(request));
        } else if (StringUtils.isNotBlank(formUuid)) {
            return new DyFormView(latestFormUuid == null ? formUuid : latestFormUuid, getIdValue(request));
        } else if (isDyFormType(dataType)) {
            String storeFormUuid = store.getFormUuid();
            return new DyFormView(latestFormUuid == null ? storeFormUuid : latestFormUuid, getIdValue(request));
        } else if (isDataStoreType(dataType)) {
            String dataStoreId = store.getDataStoreId();
            if (StringUtils.isBlank(dataStoreId)) {
                dataStoreId = getDataStoreId(request);
            }

            if (StringUtils.isBlank(dataStoreId)) {
                return null;
            }

            CdDataStoreDefinition cdDataStoreDefinition = cdDataStoreDefinitionService.getBeanById(dataStoreId);
            String tableName = cdDataStoreDefinition.getTableName();
            if (StringUtils.isBlank(tableName)) {
                return null;
            }

            DyFormFormDefinition dyFormDefinition = dyFormApiFacade
                    .getFormDefinitionOfMaxVersionByTblName(tableName.toLowerCase());
            if (dyFormDefinition == null) {
                return null;
            }
            return new DyFormView(dyFormDefinition.getUuid(), getIdValue(request));
        }
        return null;
    }

    /**
     * @param request
     * @return
     */
    private String getDataStoreId(HttpServletRequest request) {
        return request.getParameter("dataStoreId");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractViewResolver#getOrder()
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
