/*
 * @(#)2019年8月20日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.adapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformDataSignature;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.*;
import com.wellsoft.pt.dyform.implement.repository.provider.FormRepositoryProvider;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfoBuilder;
import com.wellsoft.pt.dyform.implement.repository.util.RepositoryConvertUtils;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.datasource.DatabaseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.hibernate.criterion.Criterion;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class FormRepositoryFormDataServiceAdapter implements FormDataServiceAdapter {

    private Logger logger = LoggerFactory.getLogger(FormRepositoryFormDataServiceAdapter.class);

    @Autowired
    @Qualifier(value = "formDataService")
    private FormDataService formDataService;

    @Autowired
    private FormRepositoryProvider formRepositoryProvider;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * @param formUuid
     * @return
     */
    private FormRepositoryContext getFormRepositoryContext(String formUuid) {
        return FormRepositoryContextFactory.getByFormUuid(formUuid);
    }

    /**
     * @param tblName
     * @return
     */
    private FormRepositoryContext getFormRepositoryContextByTableName(String tblName) {
        return FormRepositoryContextFactory.getByTableName(tblName);
    }

    public List<String> queryFormDataList(String tblName, String fieldName) {
        return this.formDataService.queryFormDataList(tblName, fieldName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryFormDataExists(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .condition(fieldName, fieldValue, CriterionOperator.eq).build();
            return formRepository.count(queryInfo, repositoryContext) > 0;
        } else {
            String[] fieldNames = new String[]{fieldName};
            String[] fieldValues = new String[]{fieldValue};
            convertDateFieldValueIfRequired(repositoryContext, fieldNames, fieldValues);
            return this.formDataService.queryFormDataExists(tblName, fieldNames[0], fieldValues[0]);
        }
    }

    @Override
    public boolean queryFormDataExists(String tblName, String[] fieldName,
                                       String[] fieldValue) throws Exception {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfoBuilder builder = repositoryContext.createQueryInfoBuilder();
            for (int i = 0; i < fieldName.length; i++) {
                builder.condition(fieldName[i], fieldValue[i], CriterionOperator.eq);
            }
            return formRepository.count(builder.build(), repositoryContext) > 0;
        } else {
            return this.formDataService.queryFormDataExists(tblName, fieldName, convertDateFieldValueIfRequired(repositoryContext, fieldName, fieldValue));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryFormDataExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean queryFormDataExists(String uuid, String tblName, String fieldName, String fieldValue)
            throws Exception {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .condition(FormData.KEY_UUID, uuid, CriterionOperator.ne)
                    .condition(fieldName, fieldValue, CriterionOperator.eq).build();
            return formRepository.count(queryInfo, repositoryContext) > 0;
        } else {
            String[] fieldNames = new String[]{fieldName};
            String[] fieldValues = new String[]{fieldValue};
            convertDateFieldValueIfRequired(repositoryContext, fieldNames, fieldValues);
            return this.formDataService.queryFormDataExists(uuid, tblName, fieldNames[0], fieldValues[0]);
        }
    }

    @Override
    public boolean queryFormDataExists(String uuid, String tblName, String[] fieldName, String[] fieldValue)
            throws Exception {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfoBuilder builder = repositoryContext.createQueryInfoBuilder();
            builder.condition(FormData.KEY_UUID, uuid, CriterionOperator.ne);
            for (int i = 0; i < fieldName.length; i++) {
                builder.condition(fieldName[i], fieldValue[i], CriterionOperator.eq);
            }
            return formRepository.count(builder.build(), repositoryContext) > 0;
        } else {
            return this.formDataService.queryFormDataExists(uuid, tblName, fieldName, convertDateFieldValueIfRequired(repositoryContext, fieldName, fieldValue));
        }
    }


    /**
     * @param repositoryContext
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private String[] convertDateFieldValueIfRequired(FormRepositoryContext repositoryContext, String[] fieldNames,
                                                     String[] fieldValues) {
        int index = 0;
        for (String fieldName : fieldNames) {
            if (repositoryContext.isDateTimeField(fieldName)) {
                Date dateTime = repositoryContext.convertDateTimeFieldValue(fieldName, fieldValues[index]);
                if (dateTime != null) {
                    // 数据库兼容性处理
                    if (!DatabaseType.MySQL5.getName().equalsIgnoreCase(Config.getValue("database.type"))) {
                        fieldNames[index] = "to_char(" + fieldName + ", 'yyyy-mm-dd hh24:mi:ss')";
                    }
                    fieldValues[index] = DateUtils.formatDateTime(dateTime);
                }
            }
            index++;
        }
        return fieldValues;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.adapter.FormDataServiceAdapter#updateSingleFieldValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Boolean updateFieldValue(String formUuid, Map<String, Object> param) {
        try {
            formDataService.updateFormData(formUuid, param);
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#saveFormData(java.lang.String, java.util.Map, java.util.Map, com.wellsoft.pt.dyform.facade.dto.DyformDataSignature)
     */
    @Override
    public String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                               Map<String, List<String>> deletedFormDatas, DyformDataSignature signature) {
        Map<String, Map<String, Set<String>>> updatedFormDatas = Maps.newHashMapWithExpectedSize(0);
        Map<String, List<String>> addedFormDatas = Maps.newHashMapWithExpectedSize(0);
        FormRepositoryContext repositoryContext = getFormRepositoryContext(mainformUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormData formData = RepositoryConvertUtils.convert2FormData(repositoryContext, mainformUuid, formDatas,
                    deletedFormDatas, updatedFormDatas, addedFormDatas, signature);
            return formRepository.saveFormData(formData, repositoryContext);
        } else {
            return this.formDataService.saveFormData(mainformUuid, formDatas, deletedFormDatas, signature);
        }
    }

    @Override
    public String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                               Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                               Map<String, List<String>> addedFormDatas, DyformDataSignature signature) {
        return saveFormData(mainformUuid, formDatas, deletedFormDatas, updatedFormDatas, addedFormDatas, signature, new DyformDataOptions());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#saveFormData(java.lang.String, java.util.Map, java.util.Map, java.util.Map, java.util.Map, com.wellsoft.pt.dyform.facade.dto.DyformDataSignature)
     */

    @Override
    public String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                               Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                               Map<String, List<String>> addedFormDatas, DyformDataSignature signature, DyformDataOptions dyformDataOptions) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(mainformUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormData formData = RepositoryConvertUtils.convert2FormData(repositoryContext, mainformUuid, formDatas,
                    deletedFormDatas, updatedFormDatas, addedFormDatas, signature);
            return formRepository.saveFormData(formData, repositoryContext);
        } else {
            return this.formDataService.saveFormData(mainformUuid, formDatas, deletedFormDatas, updatedFormDatas,
                    addedFormDatas, signature, dyformDataOptions);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getFormDataOfMainform(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> getFormDataOfMainform(String formUuid, String dataUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            return formRepository.getDataOfMainform(repositoryContext.getFormId(), dataUuid, repositoryContext);
        } else {
            return this.formDataService.getFormDataOfMainform(formUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getDefaultFormData(java.lang.String)
     */
    @Override
    public Map<String, Object> getDefaultFormData(String formUuid) throws JSONException {
        // 公共方法
        return this.formDataService.getDefaultFormData(formUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getFormDataOfParentNodeByPage(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public QueryData getFormDataOfParentNodeByPage(String formUuidOfSubform, String formUuidOfMainform,
                                                   String dataUuidOfMainform, PagingInfo pagingInfo) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuidOfSubform);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport getFormDataOfParentNodeByPage method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getFormDataOfParentNodeByPage(formUuidOfSubform, formUuidOfMainform,
                    dataUuidOfMainform, pagingInfo);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getFormDataOfParentNode(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                             String dataUuidOfMainform, boolean queryAllSubformFieldValue) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuidOfSubform);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            DyFormFormDefinition mainFormDefinition = dyFormFacade.getFormDefinition(formUuidOfMainform);
            List<Map<String, Object>> dataList = formRepository.getDataOfSubform(repositoryContext.getFormId(),
                    mainFormDefinition.getId(), dataUuidOfMainform, repositoryContext);
            QueryData queryData = new QueryData();
            queryData.setDataList(dataList);
            return queryData;
        } else {
            return this.formDataService.getFormDataOfParentNode(formUuidOfSubform, formUuidOfMainform,
                    dataUuidOfMainform, queryAllSubformFieldValue);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getFormDataOfChildNode4ParentNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform,
                                                                       String formUuidOfMainform, String dataUuidOfMainform, String dataUuidOfParentNode) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuidOfSubform);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException(
                    "Unsupport getFormDataOfChildNode4ParentNode method for form repository mode of "
                            + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getFormDataOfChildNode4ParentNode(formUuidOfSubform, formUuidOfMainform,
                    dataUuidOfMainform, dataUuidOfParentNode);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see FormDataServiceAdapter#getSubformDatas(List, String, String, boolean)
     */
    @Override
    public Map<String, List<Map<String, Object>>> getSubformDatas(List<String> subformUuids, String formUuidOfMainform,
                                                                  String dataUuidOfMainform, boolean fetchAllSubformFields) {
        return this.formDataService.getSubformDatas(subformUuids, formUuidOfMainform, dataUuidOfMainform, fetchAllSubformFields);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryFormDataOfMainform(java.lang.String, org.hibernate.criterion.Criterion, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport queryFormDataOfMainform method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.queryFormDataOfMainform(formUuid, conditions, pagingInfo);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryTotalCountOfFormDataOfMainform(java.lang.String, org.hibernate.criterion.Criterion)
     */
    @Override
    public long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException(
                    "Unsupport queryTotalCountOfFormDataOfMainform method for form repository mode of "
                            + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.queryTotalCountOfFormDataOfMainform(tblName, conditions);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getDigestValue(java.lang.String)
     */
    @Override
    public DyformDataSignature getDigestValue(String signedContent) {
        // 公共方法
        return this.formDataService.getDigestValue(signedContent);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getFormDataOfSubform(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<Map<String, Object>> getFormDataOfSubform(String formUuidOfSubform, String dataUuidOfMainform,
                                                          String whereSql, Map<String, Object> values) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuidOfSubform);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport getFormDataOfSubform method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getFormDataOfSubform(formUuidOfSubform, dataUuidOfMainform, whereSql, values);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#query(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, int firstResult, int maxResults) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfoBuilder builder = repositoryContext.createQueryInfoBuilder();
            FormDataQueryInfo queryInfo = builder.projection(projection).selection(selection).groupBy(groupBy)
                    .having(having).orderBy(orderBy).firstResult(firstResult).maxResults(maxResults).build();
            return (List<QueryItem>) queryForObject(formRepository, queryInfo, QueryItem.class, repositoryContext)
                    .getDataList();
        } else {
            return this.formDataService.query(formUuid, projection, selection, selectionArgs, groupBy, having, orderBy,
                    firstResult, maxResults);
        }
    }

    /**
     * @param formRepository
     * @param queryInfo
     * @param class1
     * @param repositoryContext
     * @return
     */
    private <T> QueryData queryForObject(FormRepository formRepository, FormDataQueryInfo queryInfo,
                                         Class<T> resultClass, FormRepositoryContext repositoryContext) {
        FormDataQueryData formDataQueryData = formRepository.query(queryInfo, repositoryContext);
        List<?> dataList = formDataQueryData.getDataList();
        List<T> resultClassDataList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Object data : dataList) {
                resultClassDataList.add(JsonUtils.json2Object(JsonUtils.object2Json(data), resultClass));
            }
        }
        formDataQueryData.setDataList(resultClassDataList);
        return formDataQueryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#query(java.lang.String, boolean, java.lang.String[], java.lang.String, java.util.Map, java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> query(String tableName, boolean distinct, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults) {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tableName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfoBuilder builder = repositoryContext.createQueryInfoBuilder();
            FormDataQueryInfo queryInfo = builder.distinct(distinct).projection(projection).selection(selection)
                    .selectionArgs(selectionArgs).groupBy(groupBy).having(having).orderBy(orderBy)
                    .firstResult(firstResult).maxResults(maxResults).build();
            return (List<QueryItem>) queryForObject(formRepository, queryInfo, QueryItem.class, repositoryContext)
                    .getDataList();
        } else {
            return this.formDataService.query(tableName, distinct, projection, selection, selectionArgs, groupBy,
                    having, orderBy, firstResult, maxResults);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#query2(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                  String groupBy, String having, String orderBy, int firstResult, int maxResults) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfoBuilder builder = repositoryContext.createQueryInfoBuilder();
            FormDataQueryInfo queryInfo = builder.projection(projection).selection(selection).groupBy(groupBy)
                    .having(having).orderBy(orderBy).firstResult(firstResult).maxResults(maxResults).build();
            return (List<QueryItem>) queryForObject(formRepository, queryInfo, QueryItem.class, repositoryContext)
                    .getDataList();
        } else {
            return this.formDataService.query2(formUuid, projection, selection, selectionArgs, groupBy, having,
                    orderBy, firstResult, maxResults);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getEntity(java.lang.Class, java.lang.String)
     */
    @Override
    public <T> T getEntity(Class<T> clazz, String value) {
        // Unsupport
        return this.formDataService.getEntity(clazz, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#executeSql(java.lang.String)
     */
    public void executeSql(String string) {
        // Unsupport
        this.formDataService.executeSql(string);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#validateFormdates(java.lang.String, java.util.List)
     */
    @Override
    public Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid, List<Map<String, Object>> dataList) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport validateFormdates method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.validateFormdates(formUuid, dataList);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#formDefinationToXml(java.lang.String)
     */
    @Override
    public String formDefinationToXml(String formUuid) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport formDefinationToXml method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.formDefinationToXml(formUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#countByFormUuid(java.lang.String)
     */
    @Override
    public long countByFormUuid(String formUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            // 查询信息
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .condition("form_uuid", formUuid, CriterionOperator.eq)// 查询条件
                    .build();
            return formRepository.count(queryInfo, repositoryContext);
        } else {
            return this.formDataService.countByFormUuid(formUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#countDataInForm(java.lang.String)
     */
    @Override
    public long countDataInForm(String tblName) {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder().build();
            return formRepository.count(queryInfo, repositoryContext);
        } else {
            return this.formDataService.countDataInForm(tblName);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryTotalCountOfFormDataOfMainform(java.lang.String, java.lang.String)
     */
    @Override
    public long queryTotalCountOfFormDataOfMainform(String tblName, String conditions) {
        FormRepositoryContext repositoryContext = getFormRepositoryContextByTableName(tblName);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder().selection(conditions)// 查询条件
                    .build();
            return formRepository.count(queryInfo, repositoryContext);
        } else {
            return this.formDataService.queryTotalCountOfFormDataOfMainform(tblName, conditions);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#rewriteFormData(java.lang.String, java.util.Map, com.wellsoft.pt.dyform.facade.dto.DyformDataSignature)
     */
    @Override
    public String rewriteFormData(String formUuid, Map<String, List<Map<String, Object>>> formDatas,
                                  DyformDataSignature signature) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormData formData = RepositoryConvertUtils.convert2FormDataOfUpdateAll(repositoryContext, formUuid,
                    formDatas, signature);
            return formRepository.saveFormData(formData, repositoryContext);
        } else {
            return this.formDataService.rewriteFormData(formUuid, formDatas, signature);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#delFullFormData(java.lang.String, java.lang.String)
     */
    @Override
    public void delFullFormData(String formUuid, String dataUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            // 级联删除
            formRepository.deleteFormData(repositoryContext.getFormId(), dataUuid, true, repositoryContext);
        } else {
            this.formDataService.delFullFormData(formUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#delFormData(java.lang.String, java.lang.String)
     */
    @Override
    public void delFormData(String formUuid, String dataUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            formRepository.deleteFormData(repositoryContext.getFormId(), dataUuid, false, repositoryContext);
        } else {
            this.formDataService.delFormData(formUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#delFullSubFormData(java.lang.String, java.lang.String)
     */
    @Override
    public void delFullSubFormData(String formUuid, String dataUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            formRepository.deleteSubformData(repositoryContext.getFormId(), dataUuid, repositoryContext);
        } else {
            this.formDataService.delFullSubFormData(formUuid, dataUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#delFullSubFormData(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuidOfSubform);
            formRepository.deleteSubformData(repositoryContext.getFormId(), dataUuid, dyFormFormDefinition.getId(),
                    repositoryContext);
        } else {
            this.formDataService.delFullSubFormData(formUuid, dataUuid, formUuidOfSubform);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getMinOrderNo(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(mainformFormUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport getMinOrderNo method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getMinOrderNo(mainformFormUuid, mainformDataUuid, subformFormUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getMaxOrderNo(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(mainformFormUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport getMaxOrderNo method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getMaxOrderNo(mainformFormUuid, mainformDataUuid, subformFormUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getDyFormDataBySubformInfo(java.lang.String, java.lang.String)
     */
    @Override
    public List<DyFormData> getDyFormDataBySubformInfo(String formUuidOfSubform, String dataUuidOfSubform)
            throws Exception {
        // Unsupport
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuidOfSubform);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            throw new RuntimeException("Unsupport getMaxOrderNo method for form repository mode of "
                    + repositoryContext.getRepositoryMode());
        } else {
            return this.formDataService.getDyFormDataBySubformInfo(formUuidOfSubform, dataUuidOfSubform);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#getUniqueFormData(java.lang.String, java.lang.String)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, List<Map<String, Object>>> getUniqueFormData(String formUuid, String systemUnitId) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            Map<String, List<Map<String, Object>>> dataMap = Maps.newHashMap();
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .condition("system_unit_id", systemUnitId, CriterionOperator.eq)// 查询条件
                    .build();
            Map<String, Object> formDataOfMainform = findUniqueData(formRepository, queryInfo, repositoryContext);
            if (MapUtils.isNotEmpty(formDataOfMainform)) {
                // 主表
                List<Map<String, Object>> mainDataList = Lists.newArrayList();
                mainDataList.add(formDataOfMainform);
                dataMap.put(formUuid, mainDataList);
                // 从表
                FormDefinition df = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
                FormDefinitionHandler dUtils = df.doGetFormDefinitionHandler();
                for (String formUuidOfSubform : dUtils.getFormUuidsOfSubform()) {
                    QueryData qd = this.getFormDataOfParentNode(formUuidOfSubform, formUuid,
                            (String) formDataOfMainform.get(EnumSystemField.uuid.name()), false);
                    if (qd == null || qd.getDataList() == null) {
                        continue;
                    }
                    List formDataOfSubform = qd.getDataList();
                    if (formDataOfSubform == null) {
                        continue;
                    }
                    dataMap.put(formUuidOfSubform, formDataOfSubform);
                }
            }
            return dataMap;
        }
        return this.formDataService.getUniqueFormData(formUuid, systemUnitId);
    }

    /**
     * @param formRepository
     * @param queryInfo
     * @param repositoryContext
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> findUniqueData(FormRepository formRepository, FormDataQueryInfo queryInfo,
                                               FormRepositoryContext repositoryContext) {
        FormDataQueryData formDataQueryData = formRepository.query(queryInfo, repositoryContext);
        List<?> dataList = formDataQueryData.getDataList();
        if (CollectionUtils.isEmpty(dataList)) {
            return Maps.newHashMapWithExpectedSize(0);
        } else if (dataList.size() > 1) {
            throw new RuntimeException(String.format("唯一查询的结果集存在%d条数据！", dataList.size()));
        }
        return (Map<String, Object>) dataList.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#isUniqueForFields(java.lang.String, java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> params, String fiterCondition) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .condition(FormData.KEY_UUID, uuid, CriterionOperator.ne)// 查询条件
                    .selection(fiterCondition)// 查询条件
                    .selectionArgs(params)// 查询参数
                    .build();
            return formRepository.count(queryInfo, repositoryContext) > 0;
        }
        return this.formDataService.isUniqueForFields(uuid, formUuid, params, fiterCondition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryUniqueForFields(java.lang.String, java.util.Map, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .projection(new String[]{FormData.KEY_UUID})// 查询列
                    .selection(fiterCondition)// 查询条件
                    .selectionArgs(params)// 查询参数
                    .build();
            FormDataQueryData queryData = formRepository.query(queryInfo, repositoryContext);
            return (List<String>) queryData.getDataList();
        }
        return this.formDataService.queryUniqueForFields(formUuid, params, fiterCondition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.adapter.FormDataServiceAdapter#getFormFields(java.lang.String)
     */
    @Override
    public List<FormField> getFormFields(String formUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            return formRepository.getFormFields(repositoryContext.getFormId(), repositoryContext);
        }
        List<String> fieldNames = repositoryContext.getFieldNames();
        List<FormField> formFields = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            FormField formField = new FormField();
            formField.setName(fieldName);
            formField.setLabel(fieldName);
            formFields.add(formField);
        }
        return formFields;
    }

    @Override
    public void copyAsVersionData(String table, String dataUuid, String newDataUuid) {
        this.formDataService.copyAsVersionData(table, dataUuid, newDataUuid);
    }


    @Override
    public List getFormDataByWhere(String formId, String where, Map<String, Object> namedParams) {
        FormRepositoryContext repositoryContext = FormRepositoryContextFactory.getByFormId(formId);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .selection(where)// 查询条件
                    .build();

            FormDataQueryData dataQueryData = formRepository.query(queryInfo, repositoryContext);
            return dataQueryData.getDataList();
        }

        List<Map<String, Object>> list = this.formDataService.queryFormDataListByFormIdWhere(formId, where, namedParams);
        return list;

    }

    @Override
    public void delOnlyFormData(String formUuid, String dataUuid) {
        FormRepositoryContext repositoryContext = getFormRepositoryContext(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            formRepository.deleteFormData(repositoryContext.getFormId(), dataUuid, false, repositoryContext);
        } else {
            this.formDataService.delOnlyFormData(formUuid, dataUuid);
        }
    }

    @Override
    public String saveFormDataAsNewVersion(DyFormData formData, String dataUuid) {
        return this.formDataService.saveFormDataAsNewVersion(formData, dataUuid);

    }

    @Override
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getAllVersionFormData(String formUuid, String dataUuid) {
        return this.formDataService.getAllVersionFormData(formUuid, dataUuid);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getVersionFormData(String formUuid, String dataUuid) {
        return this.formDataService.getVersionFormData(formUuid, dataUuid);
    }

    @Override
    public List getFormDataByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams) {
        FormRepositoryContext repositoryContext = FormRepositoryContextFactory.getByFormUuid(formUuid);
        FormRepository formRepository = formRepositoryProvider.provide(repositoryContext);
        if (formRepository != null) {
            FormDataQueryInfo queryInfo = repositoryContext.createQueryInfoBuilder()
                    .selection(where)// 查询条件
                    .build();

            FormDataQueryData dataQueryData = formRepository.query(queryInfo, repositoryContext);
            return dataQueryData.getDataList();
        }

        List<Map<String, Object>> list = this.formDataService.queryFormDataListByFormUuidWhere(formUuid, where, namedParams);
        return list;
    }

    @Override
    public List getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams) {
        List<String> list = this.formDataService.getFormDataUuidByWhereByFormUuid(formUuid, where, namedParams);
        return list;
    }

    @Override
    public QueryData getFullFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform, String dataUuidOfMainform) {
        return this.getFormDataOfParentNode(formUuidOfSubform, formUuidOfMainform, dataUuidOfMainform, true);
    }

}
