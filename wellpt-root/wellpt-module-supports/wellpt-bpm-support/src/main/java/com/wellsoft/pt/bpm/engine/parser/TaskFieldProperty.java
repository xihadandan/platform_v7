package com.wellsoft.pt.bpm.engine.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-7-28.1	wujx	2016-7-28		Create
 * </pre>
 * @date 2016-7-28
 */
public class TaskFieldProperty {

    // 只读域
    private List<String> onlyReadFields;

    // 编辑域
    private List<String> editFields;

    // 隐藏域
    private List<String> hideFields;

    // 不可为空域
    private List<String> notNullFields;

    // 允许上传
    private List<String> allowUploadFields;

    // 允许下载
    private List<String> allowDownloadFields;

    // 允许删除
    private List<String> allowDeleteFields;

    public List<String> getOnlyReadFields() {
        if (onlyReadFields == null) {
            onlyReadFields = new ArrayList<String>();
        }
        return onlyReadFields;
    }

    public void setOnlyReadFields(List<String> onlyReadFields) {
        this.onlyReadFields = onlyReadFields;
    }

    public List<String> getEditFields() {
        if (editFields == null) {
            editFields = new ArrayList<String>();
        }
        return editFields;
    }

    public void setEditFields(List<String> editFields) {
        this.editFields = editFields;
    }

    public List<String> getHideFields() {
        if (hideFields == null) {
            hideFields = new ArrayList<String>();
        }
        return hideFields;
    }

    public void setHideFields(List<String> hideFields) {
        this.hideFields = hideFields;
    }

    public List<String> getNotNullFields() {
        if (notNullFields == null) {
            notNullFields = new ArrayList<String>();
        }
        return notNullFields;
    }

    public void setNotNullFields(List<String> notNullFields) {
        this.notNullFields = notNullFields;
    }

    public List<String> getAllowUploadFields() {
        if (allowUploadFields == null) {
            allowUploadFields = new ArrayList<String>();
        }
        return allowUploadFields;
    }

    public void setAllowUploadFields(List<String> allowUploadFields) {
        this.allowUploadFields = allowUploadFields;
    }

    public List<String> getAllowDownloadFields() {
        if (allowDownloadFields == null) {
            allowDownloadFields = new ArrayList<String>();
        }
        return allowDownloadFields;
    }

    public void setAllowDownloadFields(List<String> allowDownloadFields) {
        this.allowDownloadFields = allowDownloadFields;
    }

    public List<String> getAllowDeleteFields() {
        if (allowDeleteFields == null) {
            allowDeleteFields = new ArrayList<String>();
        }
        return allowDeleteFields;
    }

    public void setAllowDeleteFields(List<String> allowDeleteFields) {
        this.allowDeleteFields = allowDeleteFields;
    }
}
