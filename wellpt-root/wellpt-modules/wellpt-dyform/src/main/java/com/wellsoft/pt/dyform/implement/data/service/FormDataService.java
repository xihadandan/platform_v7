/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformDataSignature;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.repository.dao.DbTableDao;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.hibernate.criterion.Criterion;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 动态表单定义service类
 *
 * @author jiangmb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-20.1	hunt		2012-12-20		Create
 * </pre>
 * @date 2012-12-20
 */
public interface FormDataService {

    List<String> queryFormDataList(String tblName, String fieldName);

    List<String> queryFormDataList(String tblName, String fieldName, String where, Map<String, Object> params);

    /**
     * 检查指定的字段是否已存在
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception;

    boolean queryFormDataExists(String tblName, String[] fieldName, String[] fieldValue) throws Exception;

    /**
     * 检查指定的字段是否存在于uuid指定的记录外的其他记录中
     *
     * @param uuid
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    boolean queryFormDataExists(String uuid, String tblName, String fieldName, String fieldValue) throws Exception;

    boolean queryFormDataExists(String uuid, String tblName, String[] fieldName, String[] fieldValue) throws Exception;

    String saveFormData(String mainformUuid,
                        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas,
                        Map<String, List<String>> deletedFormDatas, DyformDataSignature signature);

    /**
     * 保存表单数据
     *
     * @param mainformUuid     表单主表formUuid
     * @param formDatas        表单的所有数据
     * @param deletedFormDatas 被删除的数据uuid集合
     * @param updatedFormDatas 　被更新的字段及数据uuid组成的集合
     * @param addedFormDatas   被添加的数据uuid的集合
     * @param signature        签名
     * @return
     */
    String saveFormData(String mainformUuid,
                        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas,
                        Map<String, List<String>> deletedFormDatas,
                        Map<String/* 表单定义id */, Map<String /* 数据记录uuid */, Set<String> /* 字段值 */>> updatedFormDatas,
                        // 被新增的数据
                        Map<String/* 表单定义id */, List<String>/* 表单数据id */> addedFormDatas, DyformDataSignature signature, DyformDataOptions dyformDataOptions);


    /**
     * 获取主表的数据
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    Map<String/* 字段名 */, Object/* 字段值 */> getFormDataOfMainform(String formUuid, String dataUuid);

    Map<String, Object> getDefaultFormData(String formUuid) throws JSONException;

    /**
     * 分页查询从表的父节点记录
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
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param pagingInfo
     * @return
     */
    QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform, String dataUuidOfMainform, boolean queryAllSubformFieldValue);

    /**
     * 获取指定的dataUUid下一层的子节点
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
     * 批量获取从表数据
     *
     * @param subformUuids
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param fetchAllSubformFields
     * @return
     */
    Map<String, List<Map<String, Object>>> getSubformDatas(List<String> subformUuids, String formUuidOfMainform,
                                                           String dataUuidOfMainform, boolean fetchAllSubformFields);

    /**
     * 复杂搜索
     *
     * @param formUuid
     * @param conditions
     * @param pagingInfo 如果该值为null ，返回所有数据，当结果大于1000时将自动分页，pageSize=1000
     * @return
     */
    QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo);

    public long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions);

    /**
     * 获取指定字符串的签名摘要
     *
     * @param formData
     * @return
     */
    public DyformDataSignature getDigestValue(String signedContent);

    /**
     * 获取指定字段的表单的数据
     *
     * @param tblName
     * @param dataUuid
     * @param fieldNames
     * @return
     */
    public Map<String, Object> getFormDataOfMainform(String tblName, String dataUuid, List<String> fieldNames);

    List<Map<String, Object>> getFormDataOfSubform(String formUuidOfSubform, String dataUuidOfMainform,
                                                   String whereSql, Map<String, Object> values);

    public String updateFormData(String formUuid, Map<String, Object> formDataColMap) throws JSONException,
            JsonParseException, JsonMappingException, IOException, ParseException;

    public String insertFormData(String formUuid, Map<String, Object> formDataColMap) throws JSONException,
            JsonParseException, JsonMappingException, IOException, ParseException;

    /**
     * 更新表单数据
     *
     * @param formUuid       被更新的表单formUuid
     * @param formDataColMap 被更新的数据
     * @param updateFields   被更新的字段
     * @return
     * @throws JSONException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws ParseException
     */
    public String updateFormData(String formUuid, Map<String, Object> formDataColMap, Set<String> updateFields)
            throws JSONException, JsonParseException, JsonMappingException, IOException, ParseException;

    List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                          String groupBy, String having, String orderBy, int firstResult, int maxResults);

    List<QueryItem> query(String tableName, boolean distinct, String[] projection, String selection,
                          Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                          int maxResults);

    public List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                  String groupBy, String having, String orderBy, int firstResult, int maxResults);

    List<Map<String, Object>> getFormDataOfMainform(String formUuid);

    public <T> T getEntity(Class<T> clazz, String value);

    void update(Object obj);

    void executeSql(String string);

    public Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid, List<Map<String, Object>> dataList);

    public String formDefinationToXml(String formUuid);

    /**
     * 判断数据表中指定的formUuid有多少数据
     *
     * @param formUuid
     * @return
     */
    long countByFormUuid(String formUuid);

    long countDataInForm(String tblName);

    long queryTotalCountOfFormDataOfMainform(String tblName, String conditions);

    public DbTableDao getDbTableDao();

    /**
     * 覆盖表单数据
     *
     * @param formUuid
     * @param formDatas
     * @param signature
     * @return
     */
    String rewriteFormData(String formUuid, Map<String, List<Map<String, Object>>> formDatas,
                           DyformDataSignature signature);

    /**
     * 删除整个表单数据
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    void delFullFormData(String formUuid, String dataUuid);

    /**
     * 删除指定的表单单条记录
     *
     * @param formUuid
     * @param dataUuid
     */
    public void delFormData(String formUuid, String dataUuid);

    /**
     * 全量删除从表数据
     *
     * @param formUuid
     * @param dataUuid
     */
    public void delFullSubFormData(String formUuid, String dataUuid);

    /**
     * 删除指定的从表的所有数据
     *
     * @param formUuid
     * @param dataUuid
     * @param formUuidOfSubform
     */
    void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform);

    /**
     * 获取某主表的一条数据对应的某从表数据的最小排序号
     *
     * @param mainformFormUuid
     * @param mainformDataUuid
     * @param subformFormUuid
     * @return
     */
    Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 获取某主表的一条数据对应的某从表数据的最大排序号
     *
     * @param mainformFormUuid
     * @param mainformDataUuid
     * @param subformFormUuid
     * @return
     */
    Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 通过从表信息获取表单数据
     *
     * @param dataUuidOfSubform
     * @return
     * @throws Exception
     */
    List<DyFormData> getDyFormDataBySubformInfo(String formUuidOfSubform, String dataUuidOfSubform) throws Exception;

    /**
     * 获取表单中指定系统单位下的唯一数据<br/>
     * <p>
     * 使用场景: 模块设置, 一般只有一条记录
     *
     * @param formUuid     单据定义uuid
     * @param systemUnitId 系统单位ID
     * @return
     */
    Map<String, List<Map<String, Object>>> getUniqueFormData(String formUuid, String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param formUuid
     * @param params         组合字段名和值
     * @param fiterCondition 过滤条件
     * @return
     */
    boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> params, String fiterCondition);

    /**
     * 查询重复数据
     *
     * @param formUuid
     * @param params         组合字段名和值
     * @param fiterCondition 过滤条件
     * @return
     */
    List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition);

    /**
     * 表单数据查询
     *
     * @param queryInfo
     * @return
     */
    FormDataQueryData query(FormDataQueryInfo queryInfo);

    /**
     * 获取数据总数
     *
     * @param queryInfo
     * @return
     */
    long count(FormDataQueryInfo queryInfo);

    void copyAsVersionData(String table, String dataUuid, String newDataUuid);

    List<Map<String, Object>> queryFormDataListByFormIdWhere(String formId, String where, Map<String, Object> namedParams);

    void delOnlyFormData(String formUuid, String dataUuid);

    String saveFormDataAsNewVersion(DyFormData formData, String dataUuid);

    Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getAllVersionFormData(String formUuid, String dataUuid);

    Map<String, List<Map<String, Object>>> getVersionFormData(String formUuid, String dataUuid);

    List<Map<String, Object>> queryFormDataListByFormUuidWhere(String formUuid, String where, Map<String, Object> namedParams);

    List<String> getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams);
}
