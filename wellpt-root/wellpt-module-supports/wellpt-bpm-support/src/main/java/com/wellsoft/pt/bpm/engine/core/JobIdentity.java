/*
 * @(#)1/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.context.base.BaseObject;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/24/25.1	    zhulh		1/24/25		    Create
 * </pre>
 * @date 1/24/25
 */
public class JobIdentity extends BaseObject {

    // 多职流转设置,flow_by_user_all_jobs以全部身份流转、flow_by_user_main_job以主身份流转、flow_by_user_select_job选择具体身份流转
    private String multiJobFlowType;

    // 获取不到主身份时，flow_by_user_select_job选择具体身份、flow_by_user_all_jobs以全部身份流转
    private String mainJobNotFoundFlowType;

    // 身份选择，single单选身份、multiple多选身份
    private String selectJobMode;

    // 发起流程时通过表单字段选择身份
    private boolean selectJobField;

    // 身份选择字段
    private String jobField;

    // 是否为环节配置
    private boolean taskScope;

    // 选择的身份ID
    private String jobId;

    /**
     * @return the multiJobFlowType
     */
    public String getMultiJobFlowType() {
        return multiJobFlowType;
    }

    /**
     * @param multiJobFlowType 要设置的multiJobFlowType
     */
    public void setMultiJobFlowType(String multiJobFlowType) {
        this.multiJobFlowType = multiJobFlowType;
    }

    /**
     * @return the mainJobNotFoundFlowType
     */
    public String getMainJobNotFoundFlowType() {
        return mainJobNotFoundFlowType;
    }

    /**
     * @param mainJobNotFoundFlowType 要设置的mainJobNotFoundFlowType
     */
    public void setMainJobNotFoundFlowType(String mainJobNotFoundFlowType) {
        this.mainJobNotFoundFlowType = mainJobNotFoundFlowType;
    }

    /**
     * @return the selectJobMode
     */
    public String getSelectJobMode() {
        return selectJobMode;
    }

    /**
     * @param selectJobMode 要设置的selectJobMode
     */
    public void setSelectJobMode(String selectJobMode) {
        this.selectJobMode = selectJobMode;
    }

    /**
     * @return the selectJobField
     */
    public boolean isSelectJobField() {
        return selectJobField;
    }

    /**
     * @param selectJobField 要设置的selectJobField
     */
    public void setSelectJobField(boolean selectJobField) {
        this.selectJobField = selectJobField;
    }

    /**
     * @return the jobField
     */
    public String getJobField() {
        return jobField;
    }

    /**
     * @param jobField 要设置的jobField
     */
    public void setJobField(String jobField) {
        this.jobField = jobField;
    }

    /**
     * @return the taskScope
     */
    public boolean isTaskScope() {
        return taskScope;
    }

    /**
     * @param taskScope 要设置的taskScope
     */
    public void setTaskScope(boolean taskScope) {
        this.taskScope = taskScope;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId 要设置的jobId
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return
     */
    public boolean isUserSelectJob() {
        return FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType);
    }

    /**
     * @return
     */
    public boolean isUserSelectJobWhileMainJobNotFound() {
        return FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(mainJobNotFoundFlowType);
    }

    /**
     * @return
     */
    public boolean isUserAllJob() {
        return FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType);
    }

    /**
     * @return
     */
    public boolean isUserMainJob() {
        return FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType);
    }

    /**
     * @return
     */
    public boolean isMultiSelectJob() {
        return "multiple".equals(selectJobMode);
    }


}
