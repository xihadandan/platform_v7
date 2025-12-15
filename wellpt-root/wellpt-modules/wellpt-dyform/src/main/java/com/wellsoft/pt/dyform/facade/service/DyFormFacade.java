package com.wellsoft.pt.dyform.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.UniqueException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.hibernate.criterion.Criterion;
import org.json.JSONException;

import java.io.IOException;
import java.sql.Clob;
import java.util.List;
import java.util.Map;
import java.util.Set;

@GroovyUseable
public interface DyFormFacade extends BaseService {

    /**
     * 获取表单字段列表
     *
     * @param formUuid
     * @return
     */
    public List<DyformField> getFormFieldDefintion(String formUuid);

    public List<String> queryFormDataList(String tblName, String fieldName);

    /**
     * 检查指定的字段的值是否存在于指定的表中
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception;

    /**
     * 检查指定的字段的值是否存在于指定的表中
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    public boolean queryFormDataExists(String tblName, String fieldName[], String[] fieldValue) throws Exception;

    /**
     * 检查指定的字段的值除了指定的uuid的记录外，还有没有存在于其他记录中
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @param uuid
     * @return
     * @throws Exception
     */
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue, String uuid)
            throws Exception;

    /**
     * 检查指定的字段的值除了指定的uuid的记录外，还有没有存在于其他记录中
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @param uuid
     * @return
     * @throws Exception
     */
    public boolean queryFormDataExists(String tblName, String fieldName[], String[] fieldValue, String uuid)
            throws Exception;

    /**
     * 生成一个UUID
     *
     * @return
     */
    public String createUuid();

    /**
     * 保存表单数据
     *
     * @param mainformUuid     主表的表定义uuid
     * @param formDatas        表单数据列表
     * @param deletedFormDatas 被删除 的表单数据
     * @param signature
     * @return
     */
    public String saveFormData(String mainformUuid/* 主表表单定义uuid */,
                               Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas,
                               Map<String/* 表单定义id */, List<String>/* 表单数据id */> deletedFormDatas, DyformDataSignature signature);

    /**
     * 更新字段值
     *
     * @param formUuid   表单定义表uuid
     * @param uuid       表单定义表中table_name表的UUID
     * @param fieldName  字段名称
     * @param fieldValue 字段值
     * @return
     */
    public Boolean updateFieldValue(String formUuid, Map<String, Object> param);

    /**
     * 保存表单数据
     *
     * @param formData DyFormData对象
     * @return dataUuid
     */
    public String saveFormData(DyFormData formData);

    String saveDataModelFormDataAsNewVersion(DyFormData formData, String dataUuid);

    /**
     * 获取表单签名(函数未完整实现)
     *
     * @param formUuid
     * @param dataUUid
     * @return
     */
    @Deprecated
    public DyformDataSignature getDyformDataSignature(String formUuid, String dataUuid);

    /**
     * 全量覆盖表单数据
     *
     * @param dyformData 表单数据实体
     * @return 主表单数据UUID
     */
    public String rewriteFormData(DyFormData dyformData);

    /**
     * 删除单条单据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    public void delFormData(String tblName, String dataUuid);

    /**
     * 删除整个单据包括从表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    public void delFullFormData(String formUuid, String dataUuid);

    /**
     * 全量删除从表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    public void delFullSubFormData(String formUuid, String dataUuid);

    /**
     * 全量删除指定从表的数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    public void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform);

    /**
     * 获取主表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据(主表)
     */
    public Map<String /* 表单字段名 */, Object/* 表单字段值 */> getFormDataOfMainform(String formUuid, String dataUuid);

    /**
     * 获取表单数据(包括主表、从表)
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据(包括主表 、 从表)
     */
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getFormData(String formUuid,
                                                                                                   String dataUuid);

    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getFullFormData(String formUuid,
                                                                                                       String dataUuid);

    /**
     * 获取表单数据(包括主表、从表)
     *
     * @param formUuid   定义UUID
     * @param dataUuid   数据UUID
     * @param pagingInfo 分页信息
     * @return 表单数据(包括主表 、 从表)
     */
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getFormDataByPage(
            String formUuid, String dataUuid, PagingInfo pagingInfo);

    /**
     * 获取动态表单数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据
     */
    public DyFormData getDyFormData(String formUuid, String dataUuid);

    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getAllVersionFormData(String formUuid, String dataUuid);

    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getVersionFormData(String formUuid, String dataUuid);


    /**
     * 获取动态表单数据附带签名
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据
     */
    public DyFormData getDyFormDataWithSignature(String formUuid, String dataUuid);

    /**
     * 获取表单定义
     *
     * @param formUuid 表单定义uuid
     * @return 表单定义
     */
    public DyFormFormDefinition getFormDefinition(String formUuid);

    /**
     * 获取表单定义集合
     *
     * @param formUuids 表单定义uuid集合
     * @return 表单定义集合
     */
    public List<DyFormFormDefinition> getFormDefinitionByFormUuids(Set<String> formUuids);

    /**
     * 根据表单定义ID获取表单定义
     *
     * @param formDefId 表单定义ID
     * @return 表单定义
     */
    public DyFormFormDefinition getFormDefinitionById(String outerId);

    /**
     * 获取表单的定义
     *
     * @param isMaxVersion <br>
     *                     true 获取所有最新版本的表单定义基本信息<br>
     *                     false 获取所有的表单定义基本信息
     * @return 表单的定义集合
     */
    public List<DyFormFormDefinition> getAllFormDefinitions(boolean isMaxVersion);

    /**
     * 获取表单定义基本信息
     *
     * @return
     */
    List<DyFormFormDefinition> listDyFormDefinitionBasicInfo();

    /**
     * 根据存储单据UUID获取对应的显示单据
     *
     * @return 表单的定义集合
     */
    public List<FormDefinition> getVformDefinitionsByPformId(String pformUuid);

    /**
     * 根据存储单据UUID获取对应的手机单据
     *
     * @return 表单的定义集合
     */
    public List<FormDefinition> getMformDefinitionsByPformId(String pformUuid);

    /**
     * 根据指定的表单表名和版本号查找对应的字段定义信息列表
     *
     * @param tblName 表单表名
     * @param version 版本号
     * @return 对应的字段定义信息列表
     */
    public List<DyformFieldDefinition> getFieldDefinitions(String tblName, String version);

    /**
     * 根据指定的表单表名和版本号查找表单定义
     *
     * @param tblName 表单表名
     * @param version 版本号
     * @return 表单定义
     */
    public DyFormFormDefinition getFormDefinition(String tblName, String version);

    /**
     * 拷贝,并将拷贝完的副本保存到目的表单中
     *
     * @param srcFormUuid  定义UUID
     * @param srcDataUuid  数据UUID
     * @param destFormUuid 目标定义UUID
     * @return 拷贝失败时返回 null,成功时返回数据uuid
     */
    public String copyAndSaveFormDataOfMainform(String srcFormUuid, String srcDataUuid, String destFormUuid);

    /**
     * 动态表单主表数据复制
     *
     * @param sourceFormUuid 源表单定义UUID
     * @param sourceDataUuid 源表单数据UUID
     * @param targetFormUuid 目标表单定义UUID
     * @return 返回复制后的表单数据
     */
    public String copyFormData(String sourceFormUuid, String sourceDataUuid, String targetFormUuid);

    /**
     * 动态表单主表数据复制
     *
     * @param dyFormData 源表单dyFormData
     * @return 返回复制后的表单数据
     */
    public DyFormData copyFormData(DyFormData dyFormData);

    /**
     * 动态表单从表数据复制
     *
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param destFormUuid
     * @param destDataUuid
     * @param formUuidOfSubform
     * @param whereSql
     * @param values
     * @return
     */
    public List<String> copySubFormData(String formUuidOfMainform, String dataUuidOfMainform, String destFormUuid,
                                        String destDataUuid, String formUuidOfSubform, String whereSql, Map<String, Object> values);

    /**
     * 动态表单从表数据复制
     *
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param formUuidOfSubform
     * @param whereSql
     * @param values
     * @param destDyformData
     * @return
     */
    /*
     * public List<String> copySubFormData(String formUuidOfMainform, String
     * dataUuidOfMainform, String formUuidOfSubform, String whereSql, Map<String,
     * Object> values, DyFormData destDyformData) {
     *
     * List<String> destUuids = new ArrayList<String>(); try { List<Map<String,
     * Object>> list =
     * this.dyFormDataService.getFormDataOfSubform(formUuidOfSubform,
     * dataUuidOfMainform, whereSql, values);
     *
     * DyFormDefinitionJSON jsonHandlerOfSubform =
     * this.getFormDefinition(formUuidOfSubform).doGetJsonHandler();
     * DyFormDefinitionJSON jsonHandlerOfSrcMainform =
     * this.getFormDefinition(formUuidOfMainform) .doGetJsonHandler();
     *
     * for (int i = 0; i < list.size(); i++) { String uuid = createUuid();
     * DyFormData subdyformdata = destDyformData.getDyFormData(formUuidOfSubform,
     * uuid); Map<String, Object> formdata = list.get(i); String srcSubFormDataUuid
     * = (String) formdata.get("uuid");
     * formdata.remove(EnumSystemField.uuid.name()); for (String field :
     * formdata.keySet()) { if (field.equalsIgnoreCase("sort_order")) {
     * System.out.println(); } if (jsonHandlerOfSubform.isInputModeEqAttach(field))
     * { continue; } if (jsonHandlerOfSubform.isFieldInDefinition(field) ||
     * DyFormDefinitionJSON.isSysTypeAsSystem(field) ||
     * DyFormDefinitionJSON.isRelationTblField(field)) {
     * subdyformdata.setFieldValue(field, formdata.get(field)); } }
     *
     * //复制文件 Set<String> fileFieldNames = new HashSet<String>(); for (String
     * fieldName :
     * jsonHandlerOfSrcMainform.getFieldNamesOfSubform(formUuidOfSubform)) { if
     * (jsonHandlerOfSubform.isInputModeEqAttach(fieldName)) {
     * fileFieldNames.add(fieldName); } }
     *
     * if (fileFieldNames.size() > 0) { List<LogicFileInfo> files =
     * this.mongoFileService.getNonioFilesFromFolder(srcSubFormDataUuid, null); for
     * (String fileFieldName : fileFieldNames) { if (files.size() > 0) {
     * List<LogicFileInfo> fieldFiles = new ArrayList<LogicFileInfo>(); for
     * (LogicFileInfo lf : files) { if
     * (fileFieldName.equalsIgnoreCase(lf.getPurpose())) { fieldFiles.add(lf); } }
     * subdyformdata.setFieldValue(fileFieldName, fieldFiles);
     *
     * } } }
     *
     * destUuids.add(uuid);
     *
     * }
     *
     * this.saveFormData(destDyformData);
     *
     * destDyformData.getAddedFormDatas().clear();
     * destDyformData.getUpdatedFormDatas().clear();
     * destDyformData.getDeletedFormDatas().clear(); } catch (JSONException e) {
     * logger.error(e.getMessage(), e); }
     *
     * return destUuids; }
     */

    /**
     * 动态表单数据复制
     *
     * @param dyFormData     动态表单数据
     * @param targetFormUuid 目标表单定义UUID
     * @return 返回复制后的表单数据
     */
    public String copyFormData(DyFormData dyFormData, String targetFormUuid);

    /**
     * 将源表单的数据深拷贝成一个新表单,没进行持久化
     *
     * @param srcDyformData 源表单数据
     * @param physicalCopy  false：浅拷贝, true：深拷贝
     * @return 新表单数据
     */
    public DyFormData createDyformDataFrom(DyFormData srcDyformData, boolean physicalCopy);

    /**
     * 深拷贝表单,没进行持久化
     *
     * @param srcDyformData  源表单
     * @param destDyformData 目标表单
     * @param physicalCopy   false：浅拷贝, true：深拷贝
     */
    public void copyFormData(DyFormData srcDyformData, DyFormData destDyformData, boolean physicalCopy);

    /**
     * 获取默认值
     *
     * @param formUuid 定义UUID
     * @return 表单默认值MAP集合
     * @throws JSONException
     */
    public Map<String, Object> getDefaultFormData(String formUuid) throws JSONException;

    /**
     * 分页查询从表父节点记录
     *
     * @param formUuid           从表的表单uuid
     * @param dataUuidOfMainform 主表数据UUID
     * @param pagingInfo         分页信息
     * @return 查询数据
     */
    public QueryData getFormDataOfParentNodeByPage(String formUuidOfSubform, String formUuidOfMainform,
                                                   String dataUuidOfMainform, PagingInfo pagingInfo);

    /**
     * 获取从表一级节点的数据（从表数据可以是树形结构）
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @return
     */
    public QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                             String dataUuidOfMainform, boolean fetchAllSubformField);

    /**
     * 获取从表一级节点的子节点
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param dataUuidOfParentNode
     * @return
     */
    public List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform,
                                                                       String formUuidOfMainform, String dataUuidOfMainform, String dataUuidOfParentNode);

    /**
     * 查询主表数据
     *
     * @param formUuid
     * @param conditions
     * @param pagingInfo 如果该值为null ，返回所有数据，当结果大于1000时将自动分页，pageSize=1000
     * @return
     */
    public QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo);

    /**
     * 根据formUuid获取表名
     *
     * @param formUuid
     * @return
     */
    public String getTblNameByFormUuid(String formUuid);

    /**
     * 获取formUUid获取关系表表名
     *
     * @param formUuid
     * @return
     */
    public String getRlTblNameByFormUuid(String formUuid);

    /**
     * 动态表单数据查询
     *
     * @param formUuid      表单uuid
     * @param projection    查询的列名，为空查询所有列
     * @param selection     查询where条件语句
     * @param selectionArgs 查询where条件语句参数
     * @param groupBy       分组语句
     * @param having        分组条件语句
     * @param orderBy       排序
     * @param firstResult   首条记录索引号
     * @param maxResults    最大记录集
     * @return List<QueryItem> 查询结果列表
     */
    public List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, int firstResult, int maxResults);

    /**
     * 动态表单数据查询
     *
     * @param formUuid      表单uuid
     * @param projection    查询的列名，为空查询所有列
     * @param selection     查询where条件语句
     * @param selectionArgs 查询where条件语句参数
     * @param groupBy       分组语句
     * @param having        分组条件语句
     * @param orderBy       排序
     * @param firstResult   首条记录索引号
     * @param maxResults    最大记录集
     * @return List<QueryItem> 查询结果列表
     */
    public List<QueryItem> query(boolean distinct, String formUuid, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults);

    /**
     * 动态表单数据查询
     *
     * @param tableName
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
    public List<QueryItem> query(String tableName, boolean distinct, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults);

    /**
     * 动态表单数据查询
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
    public List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                  String groupBy, String having, String orderBy, int firstResult, int maxResults);

    /**
     * 根据表名，获取对应的所有版本的定义
     *
     * @param tblName 表名
     * @return 对应的所有版本的定义
     */
    public List<DyFormFormDefinition> getFormDefinitionsByTblName(String tblName);

    /**
     * 将从表及从表对应的数据关系表组成一个视图subformdata的sql语句
     *
     * @param formUuidOfSubform 从表UUID
     * @return sql语句
     */
    public String getSqlOfSubformView(String formUuidOfSubform);

    /**
     * 获取数字签名信息
     *
     * @param signedContent
     * @return
     */
    public DyformDataSignature getDigestValue(String signedContent);

    /**
     * 根据定义uuid创建一个空表单
     *
     * @param formUuid 定义UUID
     * @return 空表单
     */
    public DyFormData createDyformData(final String formUuid);

    /**
     * 根据定义uuid创建一个表单带有一笔默认值的数据
     *
     * @param formUuid 定义UUID
     * @return 有一笔默认值的表单数据
     */
    public DyFormData createDyformDataWithDefaultData(final String formUuid);

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName    表名
     * @param conditions 条件
     * @return 表单数据条数
     */
    public long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions);

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName    表名
     * @param conditions 条件
     * @return 表单数据条数
     */
    public long queryTotalCountOfFormDataOfMainform(String tblName, String conditions);

    /**
     * 获取所有的字段定义
     *
     * @param formUuid 定义UUID
     * @return 所有的字段定义
     */
    public List<DyformFieldDefinition> getFieldDefinitions(String formUuid);

    /**
     * 获取指定的表单下面的从表的定义
     *
     * @param formUuid 定义UUID
     * @return 从表的定义
     */
    public List<DyformSubformFormDefinition> getSubformDefinitions(String formUuid);

    /**
     * 根据指定的表名获取其对应的最高版本的定义
     * 使用此接口要注意：手机单据和PC端的单据是相同的表名，会导致取错表单
     *
     * @param tableName 表名
     * @return 最高版本的定义
     */
    public DyFormFormDefinition getFormDefinitionOfMaxVersionByTblName(String tableName);

    /**
     * 根据指定的表单ID获取对应的最高版本的定义
     *
     * @param id
     * @return
     */
    public DyFormFormDefinition getFormDefinitionOfMaxVersionById(String id);

    /**
     * 根据指定的表名获取其对应的最低版本的定义
     *
     * @param tableName 表名
     * @return 最低版本的定义
     */
    public DyFormFormDefinition getFormDefinitionOfMinVersionByTblName(String tableName);

    /**
     * 通过对外暴露的id获得对应的表单定义uuid
     *
     * @param id 定义ID
     * @return 表单定义uuid
     */
    public String getFormUuidById(String id);

    /**
     * 获取实体
     *
     * @param clazz 实体类型
     * @param value ID/UUID
     * @return 实体
     */
    public <T> T getEntity(Class<T> clazz, String value);

    /**
     * 获取真实值
     *
     * @param value
     * @return
     */
    public String getRealValue(String jsonValue);

    /**
     * 获取展示值
     *
     * @param jsonValue
     * @return 展示值
     */
    public String getDisplayValue(String jsonValue);

    /**
     * 通过表单定义UUID返回ID
     *
     * @param formUuid 表单定义UUID
     * @return 定义ID
     */
    public String getFormIdByFormUuid(String formUuid);

    /*
     * public static void main(String[] args) throws JSONException { JSONObject json
     * = new JSONObject(); json.put("a", "is"); json.put("b", "no");
     *
     * System.out.println(DyFormFacade.getDisplayValue(json.toString()));
     * System.out.println(DyFormFacade.getRealValue(json.toString())); }
     */

    /**
     * 验证表单数据是否满足约束条件
     *
     * @param formUuid 表单定义UUID
     * @param dataList 数据集合
     * @return 验证结果
     */
    // Map<Integer/*第几行*/, List<Map<String/*列名*/, String/*不满足的约束*/>>>
    public Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid,
                                                                     List<Map<String, Object>> dataList);

    /**
     * 表单数据验证
     *
     * @param dyFormData 表单数据
     * @return 验证结果
     */
    // public ValidationResult validate(DyFormData dyFormData);
    public ValidateMsg validate(DyFormData dyFormData);

    /**
     * 将表单定义转换为xml标准串
     *
     * @param formUuid 表单定义UUID
     * @return xml标准串
     */
    public String formDefinationToXml(String formUuid);

    /**
     * 删除表单定义及表单结构
     *
     * @param formUuid
     */
    public void dropForm(String formUuid);

    public void dropFormById(String id);

    /**
     * 判断数据表中指定的formUuid有多少数据
     *
     * @param formUuid 表单定义UUID
     * @return 数据数
     */
    public long countByFormUuid(String formUuid);

    /**
     * 查询表下有多少条数据
     *
     * @param tblName 表名
     * @return 数据数
     */
    public long countDataInForm(String tblName);

    /**
     * 取得多个form的定义
     *
     * @param formUuids 定义UUID集合
     * @return 定义集合
     */
    public List<DyFormFormDefinition> getFormDefinition(List<String> formUuids);

    /**
     * 取得多个form的定义
     *
     * @param formUuids 定义UUID集合
     * @return 定义JSON集合
     */
    public List<String> getFormDefinitionJSON(List<String> formUuids);

    /**
     * 获取当前用户主职位
     *
     * @return
     */
    public String getCurrentUserMainJobName();

    /**
     * 获取当前用户副职位
     *
     * @return
     */
    public List<String> getCurrentUserSecondaryJobs();

    /**
     * 获取当前用户位置，包括主职位和副职位
     *
     * @return
     */
    public List<String> getCurrentUserSJobs();

    /**
     * 获取区块
     *
     * @param formUuid 定义UUID
     * @return 取卡列表
     */
    public List<DyformBlock> getBlocksByformUuid(String formUuid);

    /**
     * 获取区块
     *
     * @param formId 定义ID
     * @return 区块列表
     */
    public List<DyformBlock> getBlocksByFormId(String formId);

    /**
     * 获取手机端HTML
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 手机端HTML
     */
    public String getMobileHtml(String formUuid, String dataUuid);

    /**
     * Clob转String
     *
     * @param clob Clob值
     * @return String 值
     */
    public String ClobToString(Clob clob);

    /**
     * 获取某主表的一条数据对应的某从表数据的最小排序号
     *
     * @param mainformFormUuid 主表定义UUID
     * @param mainformDataUuid 主表数据UUID
     * @param subformFormUuid  从表定义UUID
     * @return 最小排序号
     */
    public Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 获取某主表的一条数据对应的某从表数据的最大排序号
     *
     * @param mainformFormUuid 主表定义UUID
     * @param mainformDataUuid 主表数据UUID
     * @param subformFormUuid  从表定义UUID
     * @return 最大排序号
     */
    public Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid);

    /**
     * 返回表单所有的依赖关系
     * 如何描述该方法
     *
     * @param dyFormDefinition
     * @return Map<" String依赖类型 ", List < String类型UUID集合>>
     */
    public Map<String, Set<String>> getResource(DyFormFormDefinition dyFormDefinition);

    /**
     * 获取数据库字符集
     * add by wujx 20160621
     */
    public String getDbCharacterSet();

    /**
     * 通过从表信息获取表单数据 add by wujx 20161019
     *
     * @param dataUuidOfSubform
     * @return
     */
    public List<DyFormData> getDyFormDataBySubformInfo(String formUuidOfSubform, String dataUuidOfSubform)
            throws Exception;

    /**
     * 读取所有的存储单据, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllPforms(Select2QueryInfo queryInfo);

    /**
     * 读取所有的展示单据, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllVforms(Select2QueryInfo queryInfo);

    /**
     * 读取所有的手机单据, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllMforms(Select2QueryInfo queryInfo);

    /**
     * 读取所有的子表单, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllMstforms(Select2QueryInfo queryInfo);

    /**
     * 获取表单定义
     *
     * @param formUuid 表单定义uuid
     * @return 表单定义
     */
    public Select2QueryData getSelectedFormDefinition(Select2QueryInfo queryInfo);

    /**
     * json解析表单实例
     *
     * @param jsonData
     * @return
     */
    public DyFormData parseDyformData(String jsonData);

    /**
     * 获取systemUnitId下的所有表单定义
     *
     * @param systemUnitId
     * @return
     */
    List<DyFormFormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId);

    /**
     * 反序列化表单数据
     *
     * @param jp
     * @param ctxt
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    public DyFormData deserializeDyformData(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException;

    /**
     * 获取表单中指定系统单位下的唯一数据<br/>
     * <p>
     * 使用场景: 模块设置, 一般只有一条记录
     *
     * @param formUuid     单据定义uuid
     * @param systemUnitId 系统单位ID
     * @return
     */
    public DyFormData findUniqueData(String formUuid, String systemUnitId) throws UniqueException;

    /**
     * 判断单据是否存在
     *
     * @param formUuid
     * @return
     */
    public boolean isFormExistByFormUuid(String formUuid);

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

    /**
     * 字段是否唯一
     * true 为 唯一，false 为不唯一
     *
     * @param formUuid         表单formUuid
     * @param fieldKeyValues   表单唯一性判断字段组合（字段名，字段值）
     * @param isFiterCondition 过滤条件（范围）（D：部门，C:当前用户 ，O：组织单位，A：全部）
     * @return
     */
    boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> fieldKeyValues,
                              String isFiterCondition);

    /**
     * 查询重复数据
     *
     * @param formUuid
     * @param params         组合字段名和值
     * @param fiterCondition 过滤条件
     * @return
     */
    public List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition);

    /**
     * 获取页签
     *
     * @param formUuid 定义UUID
     * @return 页签列表
     */
    public List<DyformTab> getSubTabsByformUuid(String formUuid);

    /**
     * 获取页签
     *
     * @param formId 定义ID
     * @return 页签列表
     */
    public List<DyformTab> getSubTabsByFormId(String formId);

    /**
     * 通过表单UUID获取该表单的所有数据,因表单存在多版本，所以返回的是最新版本的数据
     *
     * @param formId
     * @return
     */
    public List<QueryItem> queryAllDyformDataByFormId(String formId);

    /**
     * 通过表单UUID获取该表单的所有数据
     *
     * @param formUuid
     * @return
     */
    public List<QueryItem> queryAllDyformDataByFormUuid(String formUuid);

    /**
     * 加载表单默认数据项
     *
     * @param definitonJson
     * @param formType
     * @param name
     * @param pformUuid
     * @return
     * @throws JSONException
     */
    public String loadDefinitionJsonDefaultInfo(String definitonJson, String formType, String name, String pformUuid)
            throws JSONException;

    /**
     * 读取所有的存储和展现单据, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData queryAllForms(Select2QueryInfo queryInfo);

    /**
     * 获取模块化的表单定义数据，包括引用其他模块的表单定义
     *
     * @param moduleId 模块ID
     * @return
     */
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByModuleId(String moduleId);

    /**
     * 获取模块化的表单定义数据，包括引用其他模块的表单定义
     *
     * @param piUuid 产品集成树UUID
     * @return
     */
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByPiUuid(String piUuid);

    /**
     * 加载表单定义和数据,dataUuid为空或者null只加载定义
     *
     * @param formUuid       定义UUID
     * @param dataUuid       数据UUID
     * @param justDataAndDef true表示加载定义时是去加载各字段中对应的选项,false则反之
     * @return DyFormData
     */
    public abstract DyFormData getDyFormData(String formUuid, String dataUuid, boolean justDataAndDef);

    /**
     * 更新表单定义及表单数据库表
     *
     * @param formDefinition
     * @param deletedFieldNames
     * @return
     */
    public abstract FormDefinition updateFormDefinitionAndFormTable(String formDefinition, String deletedFieldNames);

    /**
     * 根据pFormUuid获取指定formType类型扩展单据最大的UUID
     *
     * @param pFormUuid
     * @param formType
     * @return
     */
    public abstract String getMaxExtFormUuidByFormUuid(String pFormUuid, String formType);


    List getFormDataByWhere(String formId, String where, Map<String, Object> namedParams);

    List<DyFormFormDefinition> listDyFormDefinitionByUuids(List<String> formUuids);

    List getFormDataByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams);

    List getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams);
}
