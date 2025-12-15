/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.listener;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.support.DyformDefinitionFilter;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.event.FormFieldsRemovedEvent;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
@Component
public class FormFieldsRemovedListener extends WellptTransactionalEventListener<FormFieldsRemovedEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FormDefinitionService formDefinitionService;

    /**
     *
     */
    @Override
    public void onApplicationEvent(FormFieldsRemovedEvent event) {
        FormDefinitionHandler handler = event.getHandler();
        if (false == handler.isFormTypeAsMSTform()) {
            return;
        }
        final String mFormUuid = handler.getFormUuid();
        List<String> removedFieldNames = event.getRemovedFieldNames();
        DyformDefinitionFilter filter = new DyformDefinitionFilter() {

            @Override
            public boolean accept(DyFormFormDefinition dyFormFormDefinition) {
                FormDefinition formDefinition = (FormDefinition) dyFormFormDefinition;
                FormDefinitionHandler handler = formDefinition.doGetFormDefinitionHandler();
                return handler.getFormUuidsOfTemplate().contains(mFormUuid);
            }
        };
        for (String fieldName : removedFieldNames) {
            String displayName = handler.getFormDisplayName(fieldName);
            String warpperKey = "${" + displayName + "(dyform." + fieldName + ")}";
            onDyformTitlePropertyChange(warpperKey, filter);
        }
    }

    public void onDyformTitlePropertyChange(String removedProperty, DyformDefinitionFilter filter) {
        try {
            List<String> deletedFieldNames = Lists.newArrayList();
            List<FormDefinition> defs = formDefinitionService.getAllFormDefintions();
            for (FormDefinition formDefinition : defs) {
                String definitionJson = formDefinition.getDefinitionJson();
                if (null != filter && false == filter.accept(formDefinition)) {
                    continue;
                }
                JSONObject formDefinitionJSONObject = new JSONObject(definitionJson);
                String titleType = formDefinitionJSONObject.optString("titleType");
                String titleContent = formDefinitionJSONObject.optString("titleContent");
                if (StringUtils.equals("2", titleType) && StringUtils.contains(titleContent, removedProperty)) {
                    titleContent = titleContent.replace(removedProperty, "");
                    if (StringUtils.isBlank(titleContent)) {
                        // 未空，设置为默认
                        formDefinitionJSONObject.put("titleType", "1");
                    }
                    formDefinitionJSONObject.put("titleContent", titleContent);
                    formDefinition.setDefinitionJson(formDefinitionJSONObject.toString());
                    formDefinitionService.updateFormDefinitionAndFormTable(formDefinition, deletedFieldNames);
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

}
