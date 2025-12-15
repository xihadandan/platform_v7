package com.wellsoft.pt.dyform.implement.definition.dto;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumSubformFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubformFieldDefinition implements DyformSubformFieldDefinition {
    private static Logger logger = LoggerFactory.getLogger(SubformFieldDefinition.class);
    private String name;
    private FormDefinitionHandler formDefinitionHandler;
    private String formUuidOfSubform;
    private FormDefinitionHandler subformFormDefinitionHandler;

    public SubformFieldDefinition(String fieldName, FormDefinitionHandler formDefinitionHandler,
                                  String formUuidOfSubform) {
        this(fieldName, formDefinitionHandler, formUuidOfSubform, null);
    }

    public SubformFieldDefinition(String fieldName, FormDefinitionHandler formDefinitionHandler,
                                  String formUuidOfSubform, FormDefinitionHandler subformFormDefinitionHandler) {
        this.name = fieldName;
        this.formDefinitionHandler = formDefinitionHandler;
        this.formUuidOfSubform = formUuidOfSubform;
        this.subformFormDefinitionHandler = subformFormDefinitionHandler;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.formDefinitionHandler.getSubformFieldPropertyOfStringType(this.formUuidOfSubform, this.name,
                EnumSubformFieldPropertyName.displayName);
    }

    public String getInputMode() {
        return this.loadSubformFormDefinitionHandler() == null ? null : this.loadSubformFormDefinitionHandler()
                .getFieldPropertyOfStringType(this.name, EnumFieldPropertyName.inputMode);
    }

    public FormDefinitionHandler loadSubformFormDefinitionHandler() {
        if (subformFormDefinitionHandler == null) {
            DyFormFormDefinition df = ((DyFormFacade) ApplicationContextHolder.getBean(DyFormFacade.class))
                    .getFormDefinition(formUuidOfSubform);
            if (df != null) {
                try {
                    this.subformFormDefinitionHandler = new FormDefinitionHandler(df.getDefinitionJson(),
                            df.getFormType(), df.getName(), df.getpFormUuid());
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return subformFormDefinitionHandler;
    }

}
