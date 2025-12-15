/*
 * @(#)2013-3-7 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.form;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
@Deprecated
public class TaskForm {

    public static final String BTN_ALL_PREFIX = "btn_all_";
    public static final String BTN_ADD_PREFIX = "btn_add_";
    public static final String BTN_DEL_PREFIX = "btn_del_";
    public static final String BTN_IMP_PREFIX = "btn_imp_";
    public static final String BTN_EXP_PREFIX = "btn_exp_";

    // 动态表单UUID
    private String formUuid;
    // 只读域
    private List<String> onlyReadFields;
    // 编辑域
    private List<String> editFields;
    // 隐藏域
    private List<String> hideFields;
    // 不可为空域
    private List<String> notNullFields;
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

    public TaskForm(String formUuid) {
        this.formUuid = formUuid;
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
        return (BTN_IMP_PREFIX + formUuid).equals(field);
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
        return (BTN_EXP_PREFIX + formUuid).equals(field);
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
        return getFieldMap(hideFields);
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
        return getFieldMap(notNullFields);
    }

    /**
     * @param notNullFields 要设置的notNullFields
     */
    public void setNotNullFields(List<String> notNullFields) {
        this.notNullFields = notNullFields;
    }

    //	public List<String> getAllowUpload() {
    //		return allowUpload;
    //	}

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

}
