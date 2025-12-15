/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.implement.definition.dao.FormDefinitionDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.repository.FormField;
import com.wellsoft.pt.jpa.service.JpaService;

import java.sql.SQLException;
import java.util.Collection;
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
 * 2012-12-20.1	jiangmb		2012-12-20		Create
 * </pre>
 * @date 2012-12-20
 */
public interface FormDefinitionService extends JpaService<FormDefinition, FormDefinitionDao, String> {

    /**
     * 通过form_uuid获取数据表单表的定义
     *
     * @param formUuid
     * @return
     */

    FormDefinition findDyFormFormDefinitionByFormUuid(String formUuid);

    FormDefinition getOne(String uuid);

    List<FormDefinition> getFormDefinitionVjsonNotNull();

    String getFormDefinitionJSONByUuid(String uuid);

    /**
     * 通过tblName获取数据表单表的定义
     *
     * @param formUuid
     * @return
     */
    List<FormDefinition> findDyFormFormDefinitionByTblName(String tblName);

    public List<FormDefinition> findDyFormFormDefinitionsById(String Id);

    /**
     * 保存表单数据表定义信息并生成表单数据表
     *
     * @param formDefinition       定义信息
     * @param deletedFieldsJSONObj
     * @throws SQLException
     */
    void createFormDefinitionAndFormTable(FormDefinition formDefinition);

    /**
     * 判断id是否已存在
     *
     * @param formTblId
     * @return
     */
    public boolean isFormExistById(String formTblId);

    /**
     * 判断数据表单表的表名是否已存在
     *
     * @param formTblName
     * @return
     */
    public boolean isFormExistByFormTblName(String formTblName);

    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * 根据表名，获取对应的所有版本的定义
     *
     * @param tblName
     * @return
     */
    List<FormDefinition> getFormDefinitionsByTblName(String tblName);

    void updateFormDefinitionAndFormTable(FormDefinition formDefinition, List<String> deletedFieldNames);

    /**
     * 获取所有表单最高版本的定义列表
     *
     * @return
     */
    List<FormDefinition> getMaxVersionList();

    /**
     * 获取所有的表单定义
     *
     * @return
     */
    public List<FormDefinition> getAllFormDefintions();

    /**
     * 获取所有的表单定义基本信息
     *
     * @return
     */
    List<DyFormFormDefinition> listDyFormDefinitionBasicInfo();

    /**
     * 获取指定数量的最新更改的表单定义
     *
     * @return
     */
    List<String> getLatestUpdatedUuids(int count);

    /**
     * 根据指定的表名，获取其对应的最高版本的定义
     *
     * @param tableName
     */
    DyFormFormDefinition getFormDefinitionOfMaxVersionByTblName(String tableName);

    String getFormDefinitionUuidOfMaxVersionByTblName(String tableName);

    /**
     * 根据指定的表名，获取其对应的最低版本的定义 add by wujx 20160715
     *
     * @param tableName
     */
    DyFormFormDefinition getFormDefinitionOfMinVersionByTblName(String tableName);

    DyFormFormDefinition getFormDefinition(String tblName, String version);

    /**
     * 删除表单定义及表单表结构
     *
     * @param formUuid
     * @return
     */
    void dropForm(String formUuid);

    public String createRelationTblHbmXML(FormDefinition FormDefinition);

    public String createTblHbmXML(DyFormFormDefinition formDefinition, List<String> deletedFieldNames);

    public void createOrUpdateTbl(FormDefinition formDefinition, String... tblHbmXMLs);

    /**
     * 获取数据库字符集
     * add by wujx 20160621
     *
     * @return
     */
    String queryDbCharacterSet();

    /**
     * 根据单据ID取得最大版本的定义信息
     *
     * @param formId
     * @return
     */
    FormDefinition getDyFormFormDefinitionOfMaxVersionById(String formId);

    Select2QueryData queryByNameOrIdOrTableName(String searchValue, PagingInfo pagingInfo, List<String> excludeUuids,
                                                List<String> systemUnitIds);

    Select2QueryData queryByNameOrIdOrTableName(String searchValue, PagingInfo pagingInfo, List<String> excludeUuids,
                                                List<String> formTypes, List<String> systemUnitIds);

    Select2QueryData queryFormDefinitionSelect(Select2QueryInfo queryInfo);

    /**
     * 返回表单所有的依赖关系
     * 如何描述该方法
     *
     * @param dyFormDefinition
     * @return Map<" String依赖类型 ", List < String类型UUID集合>>
     */
    public abstract Map<String, Set<String>> getResource(FormDefinition dyFormDefinition);

    List<FormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId);

    public abstract Select2QueryData queryTypeByNameOrIdOrTableName(String pFormUuid, PagingInfo pagingInfo,
                                                                    String searchValue, String... fromType);

    public abstract Select2QueryData queryTypeByNameOrIdOrTableName(PagingInfo pagingInfo, String searchValue,
                                                                    String... fromType);

    public abstract List<FormDefinition> getVformDefinitionsByPformId(String pFormUuid);

    public abstract List<FormDefinition> getMformDefinitionsByPformId(String pFormUuid);

    public abstract List<FormDefinition> getFormDefinitionsById(String id);

    public abstract String createMstTblHbmXML(DyFormFormDefinition formDefinition, List<String> deletedFieldNames);

    /**
     * 获取从表应用于数据字典,如果数据字段不存在，则创建
     *
     * @param type
     * @return
     */
    List<TreeNode> getSubformApplyToRootDicts(String uuid);

    /**
     * 获取主表应用于数据字典,如果数据字段不存在，则创建
     *
     * @param type
     * @return
     */
    List<TreeNode> getFormIdApplyToRootDicts(String uuid);

    /**
     * 获取主表字段应用于数据字典,如果数据字段不存在，则创建
     *
     * @param type
     * @return
     */
    List<TreeNode> getFormFieldApplyToRootDicts(String uuid);

    public List<String> findVFormAndMForm(Map<String, Object> map);

    boolean isTableExist(String s);

    boolean existDataInFormByFormUuid(String formUuid);

    Select2QueryData querySelectDataFromFormDefinition(Select2QueryInfo select2QueryInfo);

    Select2QueryData loadSelectDataFromFormDefinition(Select2QueryInfo select2QueryInfo);

    /**
     * 根据表单id，获取表单最高版本的表单定义
     *
     * @param id
     * @return
     */
    DyFormFormDefinition getFormDefinitionOfMaxVersionById(String id);

    /**
     * 根据name和tableName 查询表单定义
     *
     * @param tableName
     * @param searchValue
     * @param pagingInfo
     * @return
     */
    public Select2QueryData queryByNameAndTableName(String tableName, String searchValue, PagingInfo pagingInfo);

    List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByModuleId(String moduleId);

    List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByPiUuid(String piUuid);

    /**
     * 读取所有的自定义存储接口实现
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllCustomFormRepositories(Select2QueryInfo queryInfo);

    /**
     * 获取可选的表单字段
     *
     * @param formUuid
     * @return
     */
    public List<FormField> getCandidateFormFields(String formUuid);

    /**
     * 根据拓展UUID获取存储单据新增字段
     *
     * @param extformUuid
     * @param update
     * @return
     */
    public List<String> getDiffFieldNamesByExtFormUuid(String extformUuid, boolean update);

    /**
     * 根据pFormUuid获取指定formType类型扩展单据最大的UUID
     *
     * @param pFormUuid
     * @param formType
     * @return
     */
    public abstract String getMaxExtFormUuidByFormUuid(String pFormUuid, String formType);

    /**
     * 根据系统单位Id 表单类型
     * 统计数量
     *
     * @param systemUnitId
     * @param formType
     * @return
     */
    long countBySystemUnitId(String systemUnitId, String formType);

    /**
     * 修改 BusinessCategoryOrgEntityId
     *
     * @param idSet
     */
    public Map<String, Collection<String>> updateBusinessCategoryOrgEntityId(Set<String> idSet);

    List<FormDefinition> listByModuleId(String moduleId);

    List<FormDefinition> listByIds(List<String> ids);

    FormDefinition getLatestUuidAndVersion(String id);

    List<FormDefinition> getFormDefinitionVersions(String id);

    List<FormDefinition> listRecentVersionFormByModuleId(String moduleId);

    List<FormDefinition> getModuleFormDefinitionWithoutJson(String moduleId, Boolean vJson);

    List<FormDefinition> queryFormDefinitionIgnoreJsonByModuleIds(List<String> moduleIds);

    List<FormDefinition> queryMaxVersionFormDefinitionIgnoreJsonByModuleIds(List<String> moduleIds);

    List<FormDefinition> queryFormDefinitionByModuleIds(List<String> moduleIds);

    List<FormDefinition> queryFormDefinitionNoJsonByModuleIds(List<String> moduleIds);

    List<QueryItem> queryFormDefinitionIgnoreJsonByKeyword(String keyword, Integer pageSize, Integer pageIndex);

    List<DyFormFormDefinition> listByUuidsWithoutJson(List<String> formUuids);

    List<FormDefinition> queryFormDefinitionIgnoreJsonByTableName(String tableName);
}
