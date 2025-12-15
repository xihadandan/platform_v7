package com.wellsoft.pt.dyform.implement.definition.dto;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumSubformPropertyName;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SubformFormDefinition implements DyformSubformFormDefinition {
    private static Logger logger = LoggerFactory.getLogger(SubformFormDefinition.class);
    private final List<DyformSubformFieldDefinition> subformFieldDefinitions;
    private FormDefinitionHandler ddJson;
    private String formUuidOfSubform;

    public SubformFormDefinition(String formUuidOfSubform, FormDefinitionHandler ddJson) {
        this.formUuidOfSubform = formUuidOfSubform;
        this.ddJson = ddJson;
        subformFieldDefinitions = new ArrayList<DyformSubformFieldDefinition>();
        List<String> fieldNames = this.getFieldNames();

        FormDefinitionHandler subformFormDefinitionHandler = null;
        DyFormFormDefinition df = ((DyFormFacade) ApplicationContextHolder.getBean(DyFormFacade.class))
                .getFormDefinition(formUuidOfSubform);
        if (df != null) {
            try {
                subformFormDefinitionHandler = new FormDefinitionHandler(df.getDefinitionJson(),
                        df.getFormType(), df.getName(), df.getpFormUuid());
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        for (String fieldName : fieldNames) {
            subformFieldDefinitions.add(new SubformFieldDefinition(fieldName, this.ddJson, this.formUuidOfSubform, subformFormDefinitionHandler));
        }
    }

    public String getFormUuid() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.formUuid);
    }

    public void setFormUuid(String formUuid) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.formUuid, formUuid);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getOuterId() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.outerId);
    }

    public void setOuterId(String outerId) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.outerId, outerId);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getName() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.name);
    }

    public void setName(String name) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.name, name);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getDisplayName() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.displayName);
    }

    public void setDisplayName(String displayName) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.displayName, displayName);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getIsGroupShowTitle() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.isGroupShowTitle);
    }

    public void setIsGroupShowTitle(String isGroupShowTitle) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.isGroupShowTitle,
                    isGroupShowTitle);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getGroupShowTitle() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.groupShowTitle);
    }

    public void setGroupShowTitle(String groupShowTitle) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.groupShowTitle, groupShowTitle);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getIsGroupColumnShow() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.isGroupColumnShow);
    }

    public void setIsGroupColumnShow(String isGroupColumnShow) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.isGroupColumnShow,
                    isGroupColumnShow);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getSubformApplyTableId() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform,
                EnumSubformPropertyName.subformApplyTableId);
    }

    public void setSubformApplyTableId(String subformApplyTableId) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.subformApplyTableId,
                    subformApplyTableId);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getSubrRelationDataDefiantion() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform,
                EnumSubformPropertyName.subrRelationDataDefiantion);
    }

    public void setSubrRelationDataDefiantion(String subrRelationDataDefiantion) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.subrRelationDataDefiantion,
                    subrRelationDataDefiantion);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getTableOpen() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.tableOpen);
    }

    public void setTableOpen(String tableOpen) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.tableOpen, tableOpen);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getEditMode() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.editMode);
    }

    public void setEditMode(String editMode) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.editMode, editMode);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getHideButtons() {
        return this.ddJson.getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.hideButtons);
    }

    public void setHideButtons(String hideButtons) {
        try {
            this.ddJson.addSubformProperty(formUuidOfSubform, EnumSubformPropertyName.hideButtons, hideButtons);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public List<String> getFieldNames() {
        try {
            return this.ddJson.getFieldNamesOfSubform(formUuidOfSubform);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    public List<DyformSubformFieldDefinition> getSubformFieldDefinitions() {
        return subformFieldDefinitions;
    }

}
