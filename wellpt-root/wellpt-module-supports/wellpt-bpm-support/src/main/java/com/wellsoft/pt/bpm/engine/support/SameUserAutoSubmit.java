/*
 * @(#)12/25/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/25/24.1	    zhulh		12/25/24		    Create
 * </pre>
 * @date 12/25/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SameUserAutoSubmit extends BaseObject {
    private static final long serialVersionUID = 8015811524890727188L;

    // 审批去重模式，before前置审批、after后置审批
    private String mode;

    // 用户自动提交的环节<用户ID，环节ID>
    private Map<String, String> userSubmitMap = Maps.newLinkedHashMap();

    // 用户跳过的环节<用户ID，环节ID>
    private Map<String, String> userSkipMap = Maps.newLinkedHashMap();

    // 用户跳过的环节
    private List<String> skipTaskIds = Lists.newArrayList();

    // 环节待办标识
    // private List<String> taskIdentities = Lists.newArrayList();

    // 后续环节数据版本发生变更
    private boolean dataChanged;

    // 后续环节可编辑表单时/前序环节可编辑表单时
    private boolean canEditForm;

    // 后续环节可编辑表单且存在必填字段时/前序环节可编辑表单且存在必填字段时
    private boolean editAndRequiredField;

    // 后续环节需要选择流向时/前序环节需要选择流向时
    private boolean chooseDirection;

    // 后续环节需要选择下一环节办理人/抄送人时/前序环节需要选择下一环节办理人/抄送人时
    private boolean chooseUser;

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode 要设置的mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return the userSubmitMap
     */
    public Map<String, String> getUserSubmitMap() {
        return userSubmitMap;
    }

    /**
     * @param userSubmitMap 要设置的userSubmitMap
     */
    public void setUserSubmitMap(Map<String, String> userSubmitMap) {
        this.userSubmitMap = userSubmitMap;
    }

    /**
     * @return the userSkipMap
     */
    public Map<String, String> getUserSkipMap() {
        return userSkipMap;
    }

    /**
     * @param userSkipMap 要设置的userSkipMap
     */
    public void setUserSkipMap(Map<String, String> userSkipMap) {
        this.userSkipMap = userSkipMap;
    }

    /**
     * @return the skipTaskIds
     */
    public List<String> getSkipTaskIds() {
        return skipTaskIds;
    }

    /**
     * @param skipTaskIds 要设置的skipTaskIds
     */
    public void setSkipTaskIds(List<String> skipTaskIds) {
        this.skipTaskIds = skipTaskIds;
    }

//    /**
//     * @return the taskIdentities
//     */
//    public List<String> getTaskIdentities() {
//        return taskIdentities;
//    }
//
//    /**
//     * @param taskIdentities 要设置的taskIdentities
//     */
//    public void setTaskIdentities(List<String> taskIdentities) {
//        this.taskIdentities = taskIdentities;
//    }

    /**
     * @return the dataChanged
     */
    public boolean isDataChanged() {
        return dataChanged;
    }

    /**
     * @param dataChanged 要设置的dataChanged
     */
    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    /**
     * @return the canEditForm
     */
    public boolean isCanEditForm() {
        return canEditForm;
    }

    /**
     * @param canEditForm 要设置的canEditForm
     */
    public void setCanEditForm(boolean canEditForm) {
        this.canEditForm = canEditForm;
    }

    /**
     * @return the editAndRequiredField
     */
    public boolean isEditAndRequiredField() {
        return editAndRequiredField;
    }

    /**
     * @param editAndRequiredField 要设置的editAndRequiredField
     */
    public void setEditAndRequiredField(boolean editAndRequiredField) {
        this.editAndRequiredField = editAndRequiredField;
    }

    /**
     * @return the chooseDirection
     */
    public boolean isChooseDirection() {
        return chooseDirection;
    }

    /**
     * @param chooseDirection 要设置的chooseDirection
     */
    public void setChooseDirection(boolean chooseDirection) {
        this.chooseDirection = chooseDirection;
    }

    /**
     * @return the chooseUser
     */
    public boolean isChooseUser() {
        return chooseUser;
    }

    /**
     * @param chooseUser 要设置的chooseUser
     */
    public void setChooseUser(boolean chooseUser) {
        this.chooseUser = chooseUser;
    }

    @JsonIgnore
    public boolean isExit() {
        return dataChanged || canEditForm || editAndRequiredField || chooseDirection || chooseUser;
    }

    @JsonIgnore
    public void reset() {
        this.dataChanged = false;
        this.canEditForm = false;
        this.editAndRequiredField = false;
        this.chooseDirection = false;
        this.chooseUser = false;
    }
}
