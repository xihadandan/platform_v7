/*
 * @(#)2019年8月23日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.adapter;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformDataSignature;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.repository.FormField;
import org.hibernate.criterion.Criterion;
import org.json.JSONException;

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
 * 2019年8月23日.1	zhulh		2019年8月23日		Create
 * </pre>
 * @date 2019年8月23日
 */
public interface FormDataServiceAdapter {

    List<String> queryFormDataList(String tblName, String fieldName);

    /**
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception;

    boolean queryFormDataExists(String tblName, String[] fieldName, String[] fieldValue) throws Exception;

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    boolean queryFormDataExists(String uuid, String tblName, String fieldName, String fieldValue) throws Exception;

    boolean queryFormDataExists(String uuid, String tblName, String fieldName[], String fieldValue[]) throws Exception;

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param uuid
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Boolean updateFieldValue(String formUuid, Map<String, Object> param);

    /**
     * 如何描述该方法
     *
     * @param mainformUuid
     * @param formDatas
     * @param deletedFormDatas
     * @param signature
     * @return
     */
    String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                        Map<String, List<String>> deletedFormDatas, DyformDataSignature signature);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param formDatas
     * @param deletedFormDatas
     * @param updatedFormDatas
     * @param addedFormDatas
     * @param signature
     * @return
     */
    String saveFormData(String formUuid, Map<String, List<Map<String, Object>>> formDatas,
                        Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                        Map<String, List<String>> addedFormDatas, DyformDataSignature signature);

    String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                        Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                        Map<String, List<String>> addedFormDatas, DyformDataSignature signature, DyformDataOptions dyformDataOptions);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param formDatas
     * @param signature
     * @return
     */
    String rewriteFormData(String formUuid, Map<String, List<Map<String, Object>>> formDatas,
                           DyformDataSignature signature);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     */
    void delFullFormData(String formUuid, String dataUuid);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     */
    void delFormData(String formUuid, String dataUuid);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     */
    void delFullSubFormData(String formUuid, String dataUuid);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     * @param formUuidOfSubform
     */
    void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    Map<String, Object> getFormDataOfMainform(String formUuid, String dataUuid);

    <T> T getEntity(Class<T> clazz, String value);

    /**
     * 如何描述该方法
     *
     * @param formUuidOfSubform
     * @param dataUuidOfMainform
     * @param whereSql
     * @param values
     * @return
     */
    List<Map<String, Object>> getFormDataOfSubform(String formUuidOfSubform, String dataUuidOfMainform,
                                                   String whereSql, Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @return
     */
    Map<String, Object> getDefaultFormData(String formUuid) throws JSONException;

    /**
     * 如何描述该方法
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param pagingInfo
     * @return
     */
    QueryData getFormDataOfParentNodeByPage(String formUuidOfSubform, String formUuidOfMainform,
                                            String dataUuidOfMainform, PagingInfo pagingInfo);

    /**
     * 如何描述该方法
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @return
     */
    QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform, String dataUuidOfMainform, boolean queryAllSubformFieldValue);

    /**
     * 如何描述该方法
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param dataUuidOfParentNode
     * @return
     */
    List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                                                String dataUuidOfMainform, String dataUuidOfParentNode);

    /**
     * @param subformUuids
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param fetchAllSubformFields
     * @return
     */
    Map<String, List<Map<String, Object>>> getSubformDatas(List<String> subformUuids, String formUuidOfMainform,
                                                           String dataUuidOfMainform, boolean fetchAllSubformFields);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param conditions
     * @param pagingInfo
     * @return
     */
    QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                          String groupBy, String having, String orderBy, int firstResult, int maxResults);

    /**
     * 如何描述该方法
     *
     * @param tblNameByFormUuid
     * @param distinct
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<QueryItem> query(String tblNameByFormUuid, boolean distinct, String[] projection, String selection,
                          Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                          int maxResults);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                           String groupBy, String having, String orderBy, int firstResult, int maxResults);

    /**
     * 如何描述该方法
     *
     * @param signedContent
     * @return
     */
    DyformDataSignature getDigestValue(String signedContent);

    /**
     * 如何描述该方法
     *
     * @param tblName
     * @param conditions
     * @return
     */
    long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions);

    /**
     * 如何描述该方法
     *
     * @param tblName
     * @param conditions
     * @return
     */
    long queryTotalCountOfFormDataOfMainform(String tblName, String conditions);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataList
     * @return
     */
    Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid, List<Map<String, Object>> dataList);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @return
     */
    String formDefinationToXml(String formUuid);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @return
     */
    long countByFormUuid(String formUuid);

    /**
     * 如何描述该方法
     *
     * @param tblName
     * @return
     */
    long countDataInForm(String tblName);

    /**
     * 如何描述该方法
     *
     * @param mainformFormUuid
     * @param mainformDataUuid
     * @param subformFormUuid
     * @return
     */
    Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 如何描述该方法
     *
     * @param mainformFormUuid
     * @param mainformDataUuid
     * @param subformFormUuid
     * @return
     */
    Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 如何描述该方法
     *
     * @param formUuidOfSubform
     * @param dataUuidOfSubform
     * @return
     */
    List<DyFormData> getDyFormDataBySubformInfo(String formUuidOfSubform, String dataUuidOfSubform) throws Exception;

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param systemUnitId
     * @return
     */
    Map<String, List<Map<String, Object>>> getUniqueFormData(String formUuid, String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param formUuid
     * @param fieldKeyValues
     * @param isFiterCondition
     * @return
     */
    boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> fieldKeyValues, String isFiterCondition);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param params
     * @param fiterCondition
     * @return
     */
    List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @return
     */
    List<FormField> getFormFields(String formUuid);

    void copyAsVersionData(String table, String dataUuid, String uuid);


    List<Map<String, Object>> getFormDataByWhere(String formId, String where, Map<String, Object> namedParams);

    void delOnlyFormData(String formUuid, String dataUuid);

    String saveFormDataAsNewVersion(DyFormData formData, String dataUuid);

    Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getAllVersionFormData(String formUuid, String dataUuid);

    Map<String, List<Map<String, Object>>> getVersionFormData(String formUuid, String dataUuid);

    List getFormDataByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams);

    List getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams);

    QueryData getFullFormDataOfParentNode(String formUuidOfSubform, String formUuid, String dataUuid);
}
