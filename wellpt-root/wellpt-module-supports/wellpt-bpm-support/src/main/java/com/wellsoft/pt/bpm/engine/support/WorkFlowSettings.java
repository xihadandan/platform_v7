/*
 * @(#)7/3/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.form.Button;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程设置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/3/24.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 7/3/24
 */
public class WorkFlowSettings extends BaseObject {
    private static final long serialVersionUID = -3670199214388614528L;

    private static final String KEY_GENERAL = "GENERAL";
    private static final String KEY_FLOW_DEFINITION = "FLOW_DEFINITION";
    private static final String KEY_OPINION_FILE = "OPINION_FILE";
    private static final String KEY_PROCESS_VIEWER = "PROCESS_VIEWER";
    private static final String KEY_DONE = "DONE";
    private static final String KEY_COPY_TO = "COPY_TO";
    private static final String KEY_ACTION = "ACTION";
    private static final String KEY_FLOW_SIMULATION = "FLOW_SIMULATION";
    private static final String KEY_FLOW_REPORT = "REPORT";

    // 流程定义标题
    private String titleExpression;

    // 是否启用职务体系相关审批功能
    private boolean enabledJobDuty;

    // 保存流程定义时数据传递编码方式
    private String saveEncoder;

    private boolean autoTranslateTitle;

    // 多角色时流程操作按角色隔离
    private boolean aclRoleIsolation = true;

    // 启用连续签批模式
    private boolean enabledContinuousWorkView = false;

    // 是否物理删除
    private boolean todoPhysicalDelete = false;

    // 日志保留天数
    private Integer logRetentionDays;

    // 通用设置
    private Map<String, Object> general = Maps.newHashMapWithExpectedSize(0);

    // 签署意见时添加附件
    private boolean enabledOpinionFile = false;

    // 办理过程办理人身份显示
    private String operatorIdentityMode;

    // 已办流程表单权限，done限于已办人所办理环节的表单内容、current查看流程当前环节的表单内容
    private String doneViewFormMode = "done";
    // 撤回时默认撤回已抄送数据
    private boolean cancelCopyTo = false;
    // 含系统自动抄送数据
    private boolean cancelAutoCopyTo = false;

    // 已办结流程可撤回
    private boolean allowCancelOver = true;

    // 被抄送人表单权限，copy固定为抄送时的表单内容、current流程当前环节的表单内容
    private String coptyToViewFormMode = "copy";

    private Map<String, Button> buttonMap = Maps.newHashMap();

    // 流程仿真
    private Map<String, Object> flowSimulation = Maps.newHashMapWithExpectedSize(0);

    // 统计报表
    private Map<String, Object> report = Maps.newHashMapWithExpectedSize(0);

    /**
     * @param flowSettingEntities
     */
    public WorkFlowSettings(List<WfFlowSettingEntity> flowSettingEntities) {
        parse(flowSettingEntities);
    }

    /**
     * @param flowSettingEntities
     */
    private void parse(List<WfFlowSettingEntity> flowSettingEntities) {
        flowSettingEntities.forEach(entity -> {
            parse(entity);
        });
    }

    /**
     * @param entity
     */
    private void parse(WfFlowSettingEntity entity) {
        String attrKey = entity.getAttrKey();
        // 通用设置
        if (StringUtils.equals(KEY_GENERAL, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            this.aclRoleIsolation = jsonObject.getBoolean("aclRoleIsolation");
            this.enabledContinuousWorkView = jsonObject.getBoolean("enabledContinuousWorkView");
            if (jsonObject.has("todoDeleteMode")) {
                this.todoPhysicalDelete = "physical".equals(jsonObject.getString("todoDeleteMode"));
            }
            if (jsonObject.has("logRetentionDays")) {
                this.logRetentionDays = jsonObject.getInt("logRetentionDays");
            }
            this.general = JsonUtils.json2Object(entity.getAttrVal(), Map.class);
        }

        // 流程定义
        if (StringUtils.equals(KEY_FLOW_DEFINITION, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            this.titleExpression = jsonObject.getString("titleExpression");
            this.autoTranslateTitle = jsonObject.has("autoTranslateTitle") ? jsonObject.getBoolean("autoTranslateTitle") : false;
            if (jsonObject.has("enabledJobDuty")) {
                this.enabledJobDuty = jsonObject.getBoolean("enabledJobDuty");
            }
            if (jsonObject.has("saveEncoder")) {
                this.saveEncoder = jsonObject.getString("saveEncoder");
            }
        }

        // 签署意见时添加附件
        if (StringUtils.equals(KEY_OPINION_FILE, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            this.enabledOpinionFile = jsonObject.getBoolean("enabled");
        }

        // 办理过程办理人身份显示
        if (StringUtils.equals(KEY_PROCESS_VIEWER, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            if (jsonObject.has("operatorIdentityMode")) {
                this.operatorIdentityMode = jsonObject.getString("operatorIdentityMode");
            }
        }

        // 已办流程表单权限
        if (StringUtils.equals(KEY_DONE, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            this.doneViewFormMode = jsonObject.getString("viewFormMode");
            // 撤回时默认撤回已抄送数据
            this.cancelCopyTo = jsonObject.getBoolean("cancelCopyTo");
            // 含系统自动抄送数据
            this.cancelAutoCopyTo = jsonObject.getBoolean("cancelAutoCopyTo");
            // 已办结流程可撤回
            this.allowCancelOver = jsonObject.getBoolean("allowCancelOver");
        }

        // 被抄送人表单权限
        if (StringUtils.equals(KEY_COPY_TO, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            this.coptyToViewFormMode = jsonObject.getString("viewFormMode");
        }

        // 操作设置
        if (StringUtils.equals(KEY_ACTION, attrKey)) {
            JSONObject jsonObject = new JSONObject(entity.getAttrVal());
            JSONArray jsonArray = jsonObject.getJSONArray("buttons");
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject buttonJson = jsonArray.getJSONObject(index);
                boolean multistate = buttonJson.getBoolean("multistate");
                if (multistate) {
                    JSONArray stateArray = buttonJson.getJSONArray("states");
                    for (int stateIndex = 0; stateIndex < stateArray.length(); stateIndex++) {
                        JSONObject stateButtonJson = stateArray.getJSONObject(stateIndex);
                        String btnCode = stateButtonJson.getString("code");
                        String btnTitle = stateButtonJson.getString("title");
                        this.buttonMap.put(btnCode, new Button(btnTitle, btnCode));
                    }
                } else {
                    String btnCode = buttonJson.getString("code");
                    String btnTitle = buttonJson.getString("title");
                    this.buttonMap.put(btnCode, new Button(btnTitle, btnCode));
                }
            }
        }

        // 流程仿真
        if (StringUtils.equals(KEY_FLOW_SIMULATION, attrKey)) {
            this.flowSimulation = JsonUtils.json2Object(entity.getAttrVal(), Map.class);
        }

        // 统计报表
        if (StringUtils.equals(KEY_FLOW_REPORT, attrKey)) {
            this.report = JsonUtils.json2Object(entity.getAttrVal(), Map.class);
        }
    }

    /**
     * @return the titleExpression
     */
    public String getTitleExpression() {
        return titleExpression;
    }

    /**
     * @return the enabledJobDuty
     */
    public boolean isEnabledJobDuty() {
        return enabledJobDuty;
    }

    /**
     * @param enabledJobDuty 要设置的enabledJobDuty
     */
    public void setEnabledJobDuty(boolean enabledJobDuty) {
        this.enabledJobDuty = enabledJobDuty;
    }

    /**
     * @return the saveEncoder
     */
    public String getSaveEncoder() {
        return saveEncoder;
    }

    /**
     * @return the aclRoleIsolation
     */
    public boolean isAclRoleIsolation() {
        return aclRoleIsolation;
    }

    /**
     * @param aclRoleIsolation 要设置的aclRoleIsolation
     */
    public void setAclRoleIsolation(boolean aclRoleIsolation) {
        this.aclRoleIsolation = aclRoleIsolation;
    }

    /**
     * @return the enabledContinuousWorkView
     */
    public boolean isEnabledContinuousWorkView() {
        return enabledContinuousWorkView;
    }

    /**
     * @param enabledContinuousWorkView 要设置的enabledContinuousWorkView
     */
    public void setEnabledContinuousWorkView(boolean enabledContinuousWorkView) {
        this.enabledContinuousWorkView = enabledContinuousWorkView;
    }

    /**
     * @return the todoPhysicalDelete
     */
    public boolean isTodoPhysicalDelete() {
        return todoPhysicalDelete;
    }

    /**
     * @return the logRetentionDays
     */
    public Integer getLogRetentionDays() {
        return logRetentionDays != null ? logRetentionDays : 90;
    }

    /**
     * @return the general
     */
    @JsonIgnore
    public Map<String, Object> getGeneral() {
        return general;
    }

    /**
     * @return the enabledOpinionFile
     */
    public boolean isEnabledOpinionFile() {
        return enabledOpinionFile;
    }

    /**
     * @param enabledOpinionFile 要设置的enabledOpinionFile
     */
    public void setEnabledOpinionFile(boolean enabledOpinionFile) {
        this.enabledOpinionFile = enabledOpinionFile;
    }

    /**
     * @return the operatorIdentityMode
     */
    public String getOperatorIdentityMode() {
        return operatorIdentityMode;
    }

    /**
     * @return the operatorIdentityMode
     */
    public boolean isShowOperatorPrimaryIdentity() {
        return "primary".equals(operatorIdentityMode);
    }

    /**
     * @return the doneViewFormMode
     */
    public String getDoneViewFormMode() {
        return doneViewFormMode;
    }

    /**
     * @param doneViewFormMode 要设置的doneViewFormMode
     */
    public void setDoneViewFormMode(String doneViewFormMode) {
        this.doneViewFormMode = doneViewFormMode;
    }

    /**
     * @return the cancelCopyTo
     */
    public boolean isCancelCopyTo() {
        return cancelCopyTo;
    }

    /**
     * @param cancelCopyTo 要设置的cancelCopyTo
     */
    public void setCancelCopyTo(boolean cancelCopyTo) {
        this.cancelCopyTo = cancelCopyTo;
    }

    /**
     * @return the cancelAutoCopyTo
     */
    public boolean isCancelAutoCopyTo() {
        return cancelAutoCopyTo;
    }

    /**
     * @param cancelAutoCopyTo 要设置的cancelAutoCopyTo
     */
    public void setCancelAutoCopyTo(boolean cancelAutoCopyTo) {
        this.cancelAutoCopyTo = cancelAutoCopyTo;
    }

    /**
     * @return the allowCancelOver
     */
    public boolean isAllowCancelOver() {
        return allowCancelOver;
    }

    /**
     * @param allowCancelOver 要设置的allowCancelOver
     */
    public void setAllowCancelOver(boolean allowCancelOver) {
        this.allowCancelOver = allowCancelOver;
    }

    /**
     * @return the coptyToViewFormMode
     */
    public String getCoptyToViewFormMode() {
        return coptyToViewFormMode;
    }

    /**
     * @param coptyToViewFormMode 要设置的coptyToViewFormMode
     */
    public void setCoptyToViewFormMode(String coptyToViewFormMode) {
        this.coptyToViewFormMode = coptyToViewFormMode;
    }

    /**
     * @return the flowSimulation
     */
    @JsonIgnore
    public Map<String, Object> getFlowSimulation() {
        return flowSimulation;
    }

    /**
     * @return the report
     */
    @JsonIgnore
    public Map<String, Object> getReport() {
        return report;
    }

    /**
     * @param code
     * @return
     */
    public String getButtonTitleByCode(String code) {
        Button button = this.buttonMap.get(code);
        return button != null ? button.getTitle() : code;
    }

    /**
     * @param code
     * @return
     */
    public Button getButtonByCode(String code) {
        return this.buttonMap.get(code);
    }

    public boolean isAutoTranslateTitle() {
        return autoTranslateTitle;
    }


}
