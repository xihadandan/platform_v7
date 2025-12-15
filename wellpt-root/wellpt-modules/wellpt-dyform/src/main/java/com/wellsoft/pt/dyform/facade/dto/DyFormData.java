package com.wellsoft.pt.dyform.facade.dto;

import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumHideSubFormOperateBtn;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.validator.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:单据数据
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */
public interface DyFormData {

    /**
     * 如何描述NESTFORM_DATAS
     */
    public static final String SUBFIELD_NESTFORM_DATAS = "nestformDatas";

    /**
     * 获取数据UUID
     *
     * @return
     */
    String getDataUuid();

    String getTableName();

    String getBeforeSaveFormDataServiceFilter();

    /**
     * 设置数据UUID
     *
     * @param dataUuid
     */
    void setDataUuid(String dataUuid);

    /**
     * 获取表单UUID
     *
     * @return
     */
    String getFormUuid();

    /**
     * 设置表单UUID
     *
     * @param formUuid
     */
    void setFormUuid(String formUuid);

    /**
     * 获取表单Id
     *
     * @return
     */
    public String getFormId();

    /**
     * 获取表单数据
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    Map<String, Object> getFormDataByFormUuidAndDataUuid(String formUuid, String dataUuid);

    /**
     * 加载数据字典（数据字典、数据仓库、职位）
     *
     * @param loadDictionary
     */
    void setLoadDictionary(boolean loadDictionary);

    /**
     * 加载默认值
     *
     * @param loadDefaultFormData
     */
    void setLoadDefaultFormData(boolean loadDefaultFormData);

    /**
     * 加载从表定义
     *
     * @param loadSubformDefinition
     */
    void setLoadSubformDefinition(boolean loadSubformDefinition);

    /**
     * 初始化表单定义（比如：加载数据字典、加载默认值、加载从表定义）
     */
    void initFormDefintion();

    /**
     * 设置表单数据
     *
     * @param formDatas
     * @param isNewFormData
     */
    void setFormDatas(Map<String, List<Map<String, Object>>> formDatas, boolean isNewFormData);

    /**
     * 获取表单数据
     *
     * @return
     */
    Map<String, List<Map<String, Object>>> getFormDatas();

    /**
     * 获取字段值
     *
     * @param name
     * @return
     */
    Object getFieldValue(String name);

    /**
     * 获取文件ID列表
     *
     * @param fieldName
     * @param dataUuid
     * @return
     */
    List<String> getValueOfFileIds(String fieldName, String dataUuid);

    /**
     * 设置字段值
     *
     * @param name
     * @param fieldValue
     */
    void setFieldValue(String name, Object fieldValue);

    /**
     * 获取删除的记录
     *
     * @return
     */
    Map<String, List<String>> getDeletedFormDatas();

    /**
     * 获取添加的记录
     *
     * @return
     */
    Map<String, List<String>> getAddedFormDatas();

    /**
     * 获取更新的字段
     *
     * @return
     */
    Map<String, Map<String, Set<String>>> getUpdatedFormDatas();

    /**
     * 获取表单签名信息
     *
     * @return
     */
    DyformDataSignature getSignature();

    /**
     * 设置表单签名信息
     *
     * @param signature
     */
    void setSignature(DyformDataSignature signature);

    /**
     * 拷贝数据
     *
     * @param depth
     * @return
     */
    DyFormData doCopy(boolean depth);

    /**
     * 获取所有字段名称
     *
     * @return
     */
    List<String> doGetFieldNames();

    /**
     * 判断formUuid是否为主表
     *
     * @param formUuid
     * @return
     */
    boolean isMainform(String formUuid);

    /**
     * 判断fieldName是否为附件字段
     *
     * @param fieldName
     * @return
     */
    boolean isFileField(String fieldName);

    /**
     * 添加（追加）从表数据
     *
     * @param sudformData
     */
    void addSubformData(DyFormData sudformData);

    /**
     * 获取表单数据，DyFormData对象
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    DyFormData getDyFormData(String formUuid, String dataUuid);

    /**
     * 获取表单定义
     *
     * @return
     */
    String getFormDefinition();

    /**
     * 设置表单定义信息
     *
     * @param dyformDefinition
     */
    public abstract void setFormDefinition(DyFormFormDefinition dyformDefinition);

    /**
     * 重新赋值表单定义json字符串
     */
    void reassignFormDefinition();

    /**
     * 获取表单显示数据
     *
     * @return
     */
    Map<String, List<Map<String, Object>>> getDisplayValues();

    /**
     * 获取表单显示数据,数据绑定到formId
     *
     * @return
     */
    Map<String, List<Map<String, Object>>> getDisplayValuesKeyAsFormId();

    /**
     * 获取表单定义的区块
     *
     * @return
     */
    List<DyformBlock> getBlocks();

    /**
     * 通过映射字段获取表单值
     *
     * @param mappingName
     * @return
     */
    Object getFieldValueByMappingName(String mappingName);

    /**
     * 通过映射字段获取表单字段名
     *
     * @param mappingName
     * @return
     */
    List<String> getFieldNamesByMappingName(String mappingName);

    /**
     * 通过映射mappingName的字段设值
     *
     * @param mappingName
     * @param value
     */
    public void setFieldValueByMappingName(String mappingName, Object value);

    /**
     * 判断是否存在mappingName的字段映射
     *
     * @param mappingName
     * @return
     */
    boolean hasFieldMappingName(String mappingName);

    /**
     * 获取主表数据
     *
     * @return
     */
    Map<String, Object> getFormDataOfMainform();

    /**
     * 获取formUuid的表单数据
     *
     * @param formUuid
     * @return
     */
    List<Map<String, Object>> getFormDatas(String formUuid);

    /**
     * 通过formId和dataUuid获取表单数据，DyFormData实例
     *
     * @param formId
     * @param dataUuid
     * @return
     */
    DyFormData getDyFormDataByFormId(String formId, String dataUuid);

    /**
     * 通过formUuid获取表单数据
     *
     * @param formUuid
     * @return
     */
    List<DyFormData> getDyformDatas(String formUuid);

    /**
     * 通过formId获取表单数据
     *
     * @param formId
     * @return
     */
    public List<DyFormData> getDyformDatasByFormId(String formId);

    /**
     * 通过formId获取表单数据
     *
     * @param formId
     * @return
     */
    List<Map<String, Object>> getFormDatasById(String formId);

    /**
     * 获取fieldName字段的展示名称
     *
     * @param fieldName
     * @return
     */
    String getDisplayNameOfField(String fieldName);

    /**
     * 判断字段是否隐藏
     *
     * @param fieldNme
     * @return
     */
    boolean isFieldHide(String fieldNme);

    /**
     * 判读表单定义是否包含formUuid
     *
     * @param formUuid
     * @return
     */
    boolean isFormUuidInThisForm(String formUuid);

    /**
     * 获取formId对应的formUuid
     *
     * @param formId
     * @return
     */
    String getFormUuidByFormId(String formId);

    /**
     * 判断fieldName是否存在
     *
     * @param fileName
     * @return
     */
    boolean isFieldExist(String fileName);

    /**
     * 判断fieldName是否存在
     *
     * @param fieldName
     * @param formUuid
     * @return
     */
    boolean isFieldExist(String fieldName, String formUuid);

    /**
     * 隐藏区块
     *
     * @param blockCode
     */
    void hideBlock(String blockCode);

    /**
     * 设置附件字段的操作按钮
     *
     * @param formUuid
     * @param fieldName
     * @param fileFieldSecDevBtnIdStr
     */
    void setFileFieldSecDevBtnId(String formUuid, String fieldName, String fileFieldSecDevBtnIdStr);

    /**
     * 设置字段是否允许下载
     *
     * @param wrapperKey
     * @param existsFields
     * @param b
     */
    void setAllowDownloadFields(String wrapperKey, List<String> existsFields, boolean b);

    /**
     * 设置字段是否允许删除
     *
     * @param wrapperKey
     * @param existsFields
     * @param b
     */
    void setAllowDeleteFields(String wrapperKey, List<String> existsFields, boolean b);

    /**
     * 设置只读字段
     *
     * @param wrapperKey
     * @param existsFields
     */
    void setLabelFields(String wrapperKey, List<String> existsFields);

    /**
     * 隐藏从表操作按钮
     *
     * @param wrapperKey
     */
    void hideSubformOperateBtn(String wrapperKey);

    /**
     * 显示从表操作按钮
     *
     * @param wrapperKey
     */
    void showSubformOperateBtn(String wrapperKey);

    /**
     * 设置可编辑字段
     *
     * @param wrapperKey
     * @param existsFields
     */
    void setEditableFields(String wrapperKey, List<String> existsFields);

    /**
     * 获取可编辑字段
     *
     * @param wrapperKey
     * @return
     */
    List<String> getEditableFields(String wrapperKey);

    /**
     * 设置必填字段
     *
     * @param wrapperKey
     * @param existsFields
     */
    void setRequiredFields(String wrapperKey, List<String> existsFields);

    /**
     * 设置必填及非必填字段
     *
     * @param formUuid
     * @param requiredFields
     * @param noRequiredFields
     */
    public void setRequiredFields(String formUuid, List<String> requiredFields, List<String> noRequiredFields);

    /**
     * 获取必填字段
     *
     * @param wrapperKey
     * @return
     */
    List<String> getRequiredFields(String wrapperKey);

    /**
     * 设置允许上传字段
     *
     * @param wrapperKey
     * @param existsFields
     * @param allowUpload
     */
    void setAllowUploadFields(String wrapperKey, List<String> existsFields, boolean allowUpload);

    /**
     * 设置隐藏字段
     *
     * @param key
     * @param existsFields
     */
    void setHiddenFields(String key, List<String> existsFields);

    /**
     * 设置隐藏及显示字段
     *
     * @param formUuid
     * @param hiddenFields
     * @param showFields
     */
    public void setHiddenFields(String formUuid, List<String> hiddenFields, List<String> showFields);

    /**
     * 设置子表单编辑字段
     *
     * @param key
     * @param editFields
     */
    void setTemplateEditableFields(String key, List<String> editFields);

    /**
     * 设置子表单只读字段
     *
     * @param key
     * @param onlyReadFields
     */
    void setTemplateLabelFields(String key, List<String> onlyReadFields);

    /**
     * 设置子表单隐藏字段
     *
     * @param key
     * @param hideFields
     */
    void setTemplateHiddenFields(String key, List<String> hideFields);

    /**
     * 设置子表单必填字段
     *
     * @param key
     * @param requiredFields
     */
    void setTemplateRequiredFields(String key, List<String> requiredFields);

    /**
     * 判断字段是否为空
     *
     * @param fieldName
     * @return
     */
    boolean isValueBlank(String fieldName);

    /**
     * 判断字段是否为JSON的类型字段
     *
     * @param fieldName
     * @return
     */
    boolean isValueAsMapField(String fieldName);

    /**
     * 获取字段真实值
     *
     * @param fieldName
     * @return
     */
    @Deprecated
    Object getFieldRealValue(String fieldName);

    /**
     * 获取字段展示值，对于JSON类型的控件有效
     *
     * @param fieldName
     * @return
     */
    Object getFieldDisplayValue(String fieldName);

    /**
     * 强制覆盖掉数据库中的记录，不做修改时间对比
     *
     * @param formUuid
     * @param dataUuid
     */
    public void doForceCover(final String formUuid, final String dataUuid);

    /**
     * 强制覆盖掉当前表单数据库中的记录，不做修改时间对比
     */
    public void doForceCover();

    /**
     * 获取表单页签定义
     *
     * @return
     */
    List<DyformTab> getTabs();

    /**
     * 隐藏subTab的页签
     *
     * @param subTab
     */
    void hideTab(String subTab);

    /**
     * 设置排序序号
     *
     * @param order
     */
    void setSortOrder(Integer order);

    /**
     * 隐藏从表导出按钮
     *
     * @param wrapperKey
     */
    void hideExp(String formUuid);

    /**
     * 显示从表导出按钮
     *
     * @param wrapperKey
     */
    void showExp(String formUuid);

    /**
     * 隐藏从表导入按钮
     *
     * @param wrapperKey
     */
    void hideImp(String formUuid);

    /**
     * 显示从表导入按钮
     *
     * @param wrapperKey
     */
    void showImp(String formUuid);

    /**
     * 设置从表按钮状态
     *
     * @param wrapperKey
     * @param showdel
     */
    void showSubformOperateBtn(String formUuid, EnumHideSubFormOperateBtn operateBtn);

    /**
     * 通过显示值设置表单数据
     *
     * @param displayFormDatas
     * @param isNewData
     */
    public void setDisplayFormDatas(Map<String, List<Map<String, Object>>> displayFormDatas, boolean isNewData);

    /**
     * 通过显示值设置字段值
     *
     * @param fieldName
     * @param fieldValue
     */
    public void setFieldValueByDisplayValue(String fieldName, Object fieldValue);

    /**
     * 校验表单数据
     *
     * @return
     */
    public ValidateMsg validateFormData();

    /**
     * 校验表单数据的数据库约束——长度、类型、必填
     *
     * @return
     */
    public ValidateMsg validateFormDataWithDatabaseConstraints();

    /**
     * 添加自定义校验
     *
     * @param fieldName
     * @param validator
     * @return
     */
    public boolean addValidator(String fieldName, Validator validator);

    /**
     * 添加自定义从表校验
     *
     * @param formUuid
     * @param fieldName
     * @param validator
     * @return
     */
    public boolean addValidator(String formUuid, String fieldName, Validator validator);

    /**
     * 加载从表数据
     */
    public abstract void loadSubformDatas();

    /**
     * 设置从表操作按钮
     *
     * @param formUuid
     * @param operationBtnCodes
     */
    void setEditableSubformOperateBtns(String formUuid, Set<String> operationBtnCodes);

    /**
     * 获取表单显示值字符串
     *
     * @return
     */
    String geFormDataDisplayValueString();

    /**
     * 获取字段名称
     *
     * @param files
     * @return
     */
    String getFileNamesOfField(Object files);

    /**
     * 获取表单数据选项
     *
     * @return
     */
    public abstract DyformDataOptions doGetDyformDataOptions();

    /**
     * 克隆JSON数据
     *
     * @return 表单数据的json
     */
    public abstract String cloneDyFormDatasToJson();

    /**
     * 隐藏placeholder控件
     *
     * @param placeholderCtrField
     */
    void hidePlaceholderCtr(String placeholderCtrField);

    /**
     * 显示placeholder控件
     *
     * @param placeholderCtrField
     */
    void showPlaceholderCtr(String placeholderCtrField);

    /**
     * 隐藏fileLibrary控件
     *
     * @param fileLibraryFieldId
     */
    void hideFileLibrary(String fileLibraryFieldId);

    /**
     * 显示fileLibrary控件
     *
     * @param fileLibraryFieldId
     */
    void showFileLibrary(String fileLibraryFieldId);

    /**
     * 隐藏tableView控件
     *
     * @param tableViewId
     */
    void hideTableView(String tableViewId);

    /**
     * 显示tableView控件
     *
     * @param tableViewId
     */
    void showTableView(String tableViewId);

    /**
     * 设置隐藏的从表
     *
     * @param wrapperKey
     * @param subformIds
     */
    void setHiddenSubforms(String wrapperKey, List<String> subformIds);

    /**
     * 设置可编辑的从表
     *
     * @param wrapperKey
     * @param subformIds
     */
    void setEditableSubforms(String wrapperKey, List<String> subformIds);

    /**
     * 删除指定表单数据
     *
     * @param formUuid
     * @param dataUuids
     */
    public abstract void deleteFormData(String formUuid, List<String> dataUuids);

    /**
     * 删除所有表单数据
     *
     * @param formUuid
     */
    public abstract void deleteFormData(String formUuid);

    /**
     * 删除所有从表数据
     */
    public abstract void deleteAllSubFormData();

    void setI18ns(List<AppDefElementI18nEntity> i18ns);

    Map<String, String> getNewOldUuidMap();
}
