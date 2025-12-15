/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.enums.FormRepositoryModeEnum;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfoBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
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
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
public class FormRepositoryContext {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private FormDefinitionHandler formDefinitionHandler;

    /**
     * @param formDefinitionHandler
     */
    public FormRepositoryContext(FormDefinitionHandler formDefinitionHandler) {
        super();
        this.formDefinitionHandler = formDefinitionHandler;
    }

    /**
     * @param key
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private static String getJsonString(String key, JSONObject jsonObject) throws JSONException {
        return jsonObject.optString(key);
    }

    /**
     * 返回表单ID
     *
     * @return
     */
    public String getFormId() {
        return formDefinitionHandler.getFormId();
    }

    /**
     * 根据表单定义UUID，返回表单ID
     *
     * @param formUuid
     * @return
     */
    public String getFormId(String formUuid) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        return dyFormFormDefinition.getId();
    }

    /**
     * @return
     */
    public String getFormUuid() {
        return formDefinitionHandler.getFormUuid();
    }

    /**
     * @return
     */
    public List<String> getSubformUuids() {
        return formDefinitionHandler.getFormUuidsOfSubform();
    }

    /**
     * 获取字段名
     *
     * @return
     */
    public List<String> getFieldNames() {
        List<String> fieldNames = Lists.newArrayList();
        if (formDefinitionHandler.isFormTypeAsVform() || formDefinitionHandler.isFormTypeAsMform()) {// 展现单据
            fieldNames.addAll(formDefinitionHandler.getFieldNamesOfMainformInPform());
        } else {
            fieldNames.addAll(formDefinitionHandler.getFieldNamesOfMaintable());
        }
        return fieldNames;
    }

    /**
     * 返回表单数据存储模式
     *
     * @return
     */
    public String getRepositoryMode() {
        try {
            JSONObject repositoryJsonObject = getRepositoryJsonObject();
            if (repositoryJsonObject != null) {
                return getJsonString("mode", repositoryJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return FormRepositoryModeEnum.Dyform.getValue();
    }

    /**
     * 返回用户表表名
     *
     * @return
     */
    public String getUserTableName() {
        return getUserTableName(formDefinitionHandler.getFormId());
    }

    /**
     * 返回用户表表名
     *
     * @param formId
     * @return
     */
    public String getUserTableName(String formId) {
        try {
            JSONObject repositoryJsonObject = getRepositoryJsonObject();
            if (repositoryJsonObject != null) {
                return getJsonString("userTableName", repositoryJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new RuntimeException("数据库表名不能为空！");
    }

    /**
     * @return
     */
    public String getRelationTableName() {
        return formDefinitionHandler.doGetRelationTblNameOfPform();
    }

    /**
     * 返回webservice/restful的服务地址
     *
     * @return
     */
    public String getServiceUrl() {
        try {
            JSONObject repositoryJsonObject = getRepositoryJsonObject();
            if (repositoryJsonObject != null) {
                return getJsonString("serviceUrl", repositoryJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new RuntimeException("服务地址不能为空！");
    }

    /**
     * @return
     */
    public String getServiceToken() {
        try {
            JSONObject repositoryJsonObject = getRepositoryJsonObject();
            if (repositoryJsonObject != null) {
                return getJsonString("serviceToken", repositoryJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new RuntimeException("Access Token不能为空！");
    }

    /**
     * 返回自定义接口实现类的bean实例名称
     *
     * @return
     */
    public String getCustomInterface() {
        try {
            JSONObject repositoryJsonObject = getRepositoryJsonObject();
            if (repositoryJsonObject != null) {
                return getJsonString("customInterface", repositoryJsonObject);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        throw new RuntimeException("存储接口不能为空！");
    }

    /**
     * @return
     * @throws JSONException
     */
    private JSONObject getRepositoryJsonObject() throws JSONException {
        JSONObject jsonObject = formDefinitionHandler.getFormDefinition();
        if (jsonObject.has("repository")) {
            return jsonObject.getJSONObject("repository");
        }
        return null;
    }

    /**
     * @param fieldName
     * @return
     */
    public boolean isDateTimeField(String fieldName) {
        try {
            return formDefinitionHandler.isInputModeEqDate(fieldName);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @param fieldName
     * @return
     */
    public String getDateTimeFieldPattern(String fieldName) {
        return formDefinitionHandler.getDateTimePatternByFieldName(fieldName);
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Date convertDateTimeFieldValue(String fieldName, String fieldValue) {
        String dbDataType = formDefinitionHandler.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
        String datePattern = formDefinitionHandler.getDateTimePatternByFieldName(fieldName);
        try {
            return (Date) FormDataHandler.convertData2DbType(dbDataType, fieldValue, null, datePattern);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * @param fieldName
     * @return
     */
    public boolean isFileField(String fieldName) {
        try {
            return formDefinitionHandler.isInputModeEqAttach(fieldName);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @return
     */
    public FormDataQueryInfoBuilder createQueryInfoBuilder() {
        return new FormDataQueryInfoBuilder(formDefinitionHandler.getFormId());
    }

}
