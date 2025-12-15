/*
 * @(#)2013-3-7 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.*;

/**
 * Description: 环节表单配置信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-7.1	zhulh		2013-3-7		Create
 * </pre>
 * @date 2013-3-7
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskForm {

    public static final String BTN_ALL_PREFIX = "btn_all_";
    public static final String BTN_ADD_PREFIX = "btn_add_";
    public static final String BTN_DEL_PREFIX = "btn_del_";
    public static final String BTN_IMP_PREFIX = "btn_imp_";
    public static final String BTN_EXP_PREFIX = "btn_exp_";
    public static final String BTN_EXP_SUBFORM_PREFIX = "btn_exp_subform_";
    public static final String BTN_IMP_SUBFORM_PREFIX = "btn_imp_subform_";
    public static final Set<String> SUBFORM_DEFAULT_BTNS = Sets.newHashSet("btn_add", "btn_edit", "btn_del",
            "btn_clear", "btn_exp_subform", "btn_imp_subform");
    public static final String PLACEHOLDER_CTR_PREFIX = "placeholderCtr:";
    public static final String FILE_LIBRARY_PREFIX = "fileLibrary:";
    public static final String TABLE_VIEW_PREFIX = "tableView:";

    // 动态表单UUID
    private String formUuid;
    // 显示表单
    private String vformUuid;
    // 只读域
    private List<String> onlyReadFields;
    // 编辑域
    private List<String> editFields;
    // 隐藏域
    private List<String> hideFields;
    // 不可为空域
    private List<String> notNullFields;
    // 附件按钮权限
    private List<String> fileRights;
    // 表单所有字段
    private List<String> allFormFields;
    private List<String> allFormFieldWidgetIds;
    private List<String> formBtnRightSettings;
    // 表单所有主表字段
    private List<String> allMainformFields = Lists.newArrayList();
    // 表单所有从表字段
    private Map<String, List<String>> allSubformFieldMap = Maps.newHashMap();
    // 表单所有从表定义UUID
    private Set<String> allSubformUuids = Sets.newHashSet();
    // add by wujx 20160728 begin
    // 允许上传
    private List<String> allowUpload;
    // 允许下载
    private List<String> allowDownload;
    // 允许删除
    private List<String> allowDelete;
    // add by wujx 20160728 end
    // 隐藏区块
    private List<String> hideBlocks;
    // 信息格式列表
    private List<Record> records;
    // 隐藏页签
    private List<String> hideTabs;

    public TaskForm() {
    }

    public TaskForm(String formUuid) {
        this.formUuid = formUuid;
    }

    public TaskForm(String formUuid, String vformUuid) {
        this.formUuid = formUuid;
        this.vformUuid = vformUuid;
    }

    /**
     * 判断是否则为从表添加删除按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isSubformAllBtnField(String field) {
        if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
            String[] fieldInfos = StringUtils.split(field, ":");
            String subformUuid = fieldInfos[0];
            String subformField = fieldInfos[1];
            return (BTN_ALL_PREFIX + subformUuid).equals(subformField);
        }
        return false;
    }

    public static final boolean isSubformAddBtnField(String field) {
        if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
            String[] fieldInfos = StringUtils.split(field, ":");
            String subformUuid = fieldInfos[0];
            String subformField = fieldInfos[1];
            return (BTN_ADD_PREFIX + subformUuid).equals(subformField);
        }
        return false;
    }

    public static final boolean isSubformDelBtnField(String field) {
        if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
            String[] fieldInfos = StringUtils.split(field, ":");
            String subformUuid = fieldInfos[0];
            String subformField = fieldInfos[1];
            return (BTN_DEL_PREFIX + subformUuid).equals(subformField);
        }
        return false;
    }

    public static final boolean isSubformImpBtnField(String field) {
        if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
            String[] fieldInfos = StringUtils.split(field, ":");
            String subformUuid = fieldInfos[0];
            String subformField = fieldInfos[1];
            return (BTN_IMP_PREFIX + subformUuid).equals(subformField);
        }
        return false;
    }

    public static final boolean isSubformExpBtnField(String field) {
        if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
            String[] fieldInfos = StringUtils.split(field, ":");
            String subformUuid = fieldInfos[0];
            String subformField = fieldInfos[1];
            return (BTN_EXP_PREFIX + subformUuid).equals(subformField);
        }
        return false;
    }

    /**
     * 判断是否则为从表添加删除按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isAllBtnField(String formUuid, String field) {
        return (BTN_ALL_PREFIX + formUuid).equals(field);
    }

    /**
     * 判断是否则为从表添加删除按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final String getAllBtnField(String formUuid) {
        return (BTN_ALL_PREFIX + formUuid);
    }

    /**
     * 判断是否则为从表添加按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isAddBtnField(String formUuid, String field) {
        return (BTN_ADD_PREFIX + formUuid).equals(field);
    }

    /**
     * 判断是否则为从表添加按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final String getAddBtnField(String formUuid) {
        return (BTN_ADD_PREFIX + formUuid);
    }

    /**
     * 判断是否则为从表删除按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isDelBtnField(String formUuid, String field) {
        return (BTN_DEL_PREFIX + formUuid).equals(field);
    }

    /**
     * 判断是否则为从表删除按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final String getDelBtnField(String formUuid) {
        return (BTN_DEL_PREFIX + formUuid);
    }

    /**
     * 判断是否则为从表导入按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isImpBtnField(String formUuid, String field) {
        return (BTN_IMP_PREFIX + formUuid).equals(field) || (BTN_IMP_SUBFORM_PREFIX + formUuid).equals(field);
    }

    /**
     * 判断是否则为从表导入按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final String getImpBtnField(String formUuid) {
        return (BTN_IMP_PREFIX + formUuid);
    }

    /**
     * 判断是否则为从表导出按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final boolean isExpBtnField(String formUuid, String field) {
        return (BTN_EXP_PREFIX + formUuid).equals(field) || (BTN_EXP_SUBFORM_PREFIX + formUuid).equals(field);
    }

    /**
     * @param field
     * @return
     */
    public static boolean isFileLibraryField(String field) {
        return StringUtils.startsWith(field, FILE_LIBRARY_PREFIX);
    }

    /**
     * @param field
     * @return
     */
    public static boolean isTableViewField(String field) {
        return StringUtils.startsWith(field, TABLE_VIEW_PREFIX);
    }

    /**
     * 判断是否则为从表导出按钮字段
     *
     * @param mainFormUuid
     * @return
     */
    public static final String getExpBtnField(String formUuid) {
        return (BTN_EXP_PREFIX + formUuid);
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @return the vformUuid
     */
    public String getVformUuid() {
        return vformUuid;
    }

    /**
     * @param vformUuid 要设置的vformUuid
     */
    public void setVformUuid(String vformUuid) {
        this.vformUuid = vformUuid;
    }

    /**
     * 判断是否为主表UUID
     *
     * @param mainFormUuid
     * @return
     */
    public boolean isMainForm(String mainFormUuid) {
        return this.formUuid.equals(mainFormUuid);
    }

    //	/**
    //	 * @return the onlyReadFields
    //	 */
    //	public List<String> getOnlyReadFields() {
    //		return onlyReadFields;
    //	}

    /**
     * @return the onlyReadFields
     */
    public Map<String, List<String>> getOnlyReadFieldMap() {
        return getFieldMap(onlyReadFields);
    }

    /**
     * @param onlyReadFields 要设置的onlyReadFields
     */
    public void setOnlyReadFields(List<String> onlyReadFields) {
        this.onlyReadFields = onlyReadFields;
    }

    //	/**
    //	 * @return the editFields
    //	 */
    //	public List<String> getEditFields() {
    //		return editFields;
    //	}

    /**
     * @return
     */
    public Map<String, List<String>> getEditFieldMap() {
        return getFieldMap(editFields);
    }

    /**
     * @param editFields 要设置的editFields
     */
    public void setEditFields(List<String> editFields) {
        this.editFields = editFields;
    }

    //	/**
    //	 * @return the hideFields
    //	 */
    //	public List<String> getHideFields() {
    //		return hideFields;
    //	}

    /**
     * @return
     */
    public Map<String, List<String>> getHideFieldMap() {
        return initAllFormUUids(getFieldMap(hideFields));
    }

    /**
     * @param hideFields 要设置的hideFields
     */
    public void setHideFields(List<String> hideFields) {
        this.hideFields = hideFields;
    }

    //	/**
    //	 * @return the notNullFields
    //	 */
    //	public List<String> getNotNullFields() {
    //		return notNullFields;
    //	}

    /**
     * @return
     */
    public Map<String, List<String>> getNotNullFieldMap() {
        return initAllFormUUids(getFieldMap(notNullFields));
    }

    /**
     * @param map
     */
    private Map<String, List<String>> initAllFormUUids(Map<String, List<String>> map) {
        if (!map.containsKey(formUuid)) {
            map.put(formUuid, new ArrayList<String>(0));
        }
        Set<String> subformUuids = getAllSubformUuids();
        for (String subformUuid : subformUuids) {
            if (!map.containsKey(subformUuid)) {
                map.put(subformUuid, new ArrayList<String>(0));
            }
        }
        return map;
    }

    /**
     * @param notNullFields 要设置的notNullFields
     */
    public void setNotNullFields(List<String> notNullFields) {
        this.notNullFields = notNullFields;
    }

    public List<String> getFileRights() {
        return fileRights;
    }

    public void setFileRights(List<String> fileRights) {
        this.fileRights = fileRights;
    }

    //	public List<String> getAllowUpload() {
    //		return allowUpload;
    //	}

    public List<String> getAllFormFields() {
        return allFormFields;
    }

    public void setAllFormFields(List<String> allFormFields) {
        this.allFormFields = allFormFields;
        if (CollectionUtils.isNotEmpty(allFormFields)) {
            initAllFormFieldsInfo(this.allFormFields);
        }
    }

    /**
     * @return the allFormFieldWidgetIds
     */
    public List<String> getAllFormFieldWidgetIds() {
        return allFormFieldWidgetIds;
    }

    /**
     * @param allFormFieldWidgetIds 要设置的allFormFieldWidgetIds
     */
    public void setAllFormFieldWidgetIds(List<String> allFormFieldWidgetIds) {
        this.allFormFieldWidgetIds = allFormFieldWidgetIds;
    }

    /**
     * @return the formBtnRightSettings
     */
    public List<String> getFormBtnRightSettings() {
        return formBtnRightSettings;
    }

    /**
     * @param formBtnRightSettings 要设置的formBtnRightSettings
     */
    public void setFormBtnRightSettings(List<String> formBtnRightSettings) {
        this.formBtnRightSettings = formBtnRightSettings;
    }

    /**
     *
     */
    private void initAllFormFieldsInfo(List<String> allFormFields) {
        for (String formField : allFormFields) {
            if (StringUtils.contains(formField, Separator.COLON.getValue())) {
                String[] fields = StringUtils.split(formField, Separator.COLON.getValue());
                String subformUuid = fields[0];
                allSubformUuids.add(subformUuid);
                if (!allSubformFieldMap.containsKey(subformUuid)) {
                    allSubformFieldMap.put(subformUuid, new ArrayList<String>());
                }
                allSubformFieldMap.get(subformUuid).add(fields[1]);
            } else {
                allMainformFields.add(formField);
            }
        }
    }

    public List<String> getAllMainformFields() {
        return allMainformFields;
    }

    public List<String> cloneAllMainformFields() {
        return Lists.newArrayList(allMainformFields);
    }

    public List<String> getAllSubformFields(String subformUuid) {
        List<String> fields = allSubformFieldMap.get(subformUuid);
        if (fields != null) {
            return fields;
        } else {
            return Collections.emptyList();
        }
    }

    public List<String> cloneAllSubformFields(String subformUuid) {
        List<String> fields = allSubformFieldMap.get(subformUuid);
        if (fields != null) {
            return Lists.newArrayList(fields);
        } else {
            return Collections.emptyList();
        }
    }

    public Set<String> getAllSubformUuids() {
        return allSubformUuids;
    }

    public Map<String, List<String>> getAllowUploadFieldMap() {
        return getFieldMap(allowUpload);
    }

    public void setAllowUpload(List<String> allowUpload) {
        this.allowUpload = allowUpload;
    }

    //	public List<String> getAllowDownload() {
    //		return allowDownload;
    //	}

    public Map<String, List<String>> getAllowDownloadFieldMap() {
        return getFieldMap(allowDownload);
    }

    public void setAllowDownload(List<String> allowDownload) {
        this.allowDownload = allowDownload;
    }

    //	public List<String> getAllowDelete() {
    //		return allowDelete;
    //	}

    public Map<String, List<String>> getAllowDeleteFieldMap() {
        return getFieldMap(allowDelete);
    }

    public void setAllowDelete(List<String> allowDelete) {
        this.allowDelete = allowDelete;
    }

    /**
     * @return the hideBlocks
     */
    public List<String> getHideBlocks() {
        return hideBlocks;
    }

    /**
     * @param hideBlocks 要设置的hideBlocks
     */
    public void setHideBlocks(List<String> hideBlocks) {
        this.hideBlocks = hideBlocks;
    }

    /**
     * @return the records
     */
    public List<Record> getRecords() {
        return records;
    }

    /**
     * @param records 要设置的records
     */
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    /**
     * @return
     */
    private Map<String, List<String>> getFieldMap(List<String> fields) {
        Map<String, List<String>> map = new HashMap<String, List<String>>(0);
        if (fields != null) {
            for (String field : fields) {
                if (StringUtils.isNotBlank(field) && field.indexOf(":") != -1) {
                    String[] fieldInfos = StringUtils.split(field, ":");
                    String subformUuid = fieldInfos[0];
                    String subformField = fieldInfos[1];
                    if (!map.containsKey(subformUuid)) {
                        map.put(subformUuid, new ArrayList<String>());
                    }
                    map.get(subformUuid).add(subformField);
                } else {
                    if (!map.containsKey(formUuid)) {
                        map.put(formUuid, new ArrayList<String>());
                    }
                    map.get(formUuid).add(field);
                }
            }
        }
        return map;
    }

    /**
     * @return the hideTabs
     */
    public List<String> getHideTabs() {
        return hideTabs;
    }

    /**
     * @param hideTabs 要设置的hideTabs
     */
    public void setHideTabs(List<String> hideTabs) {
        this.hideTabs = hideTabs;
    }

}
