/*
 * @(#)2012-11-16 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.form.Button;
import com.wellsoft.pt.bpm.engine.parser.TaskElementDeserializer;
import com.wellsoft.pt.bpm.engine.support.WorkFlowAclRole;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.ArrayList;
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
 * 2012-11-16.1	zhulh		2012-11-16		Create
 * </pre>
 * @date 2012-11-16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = TaskElementDeserializer.class)
public class TaskElement implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6301304888550548591L;

    private String name;
    private String id;
    private String type;
    private String code;

    private String x;
    private String y;
    private String conditionName;
    private String conditionBody;
    private String conditionX;
    private String conditionY;
    private String conditionLine;

    /* lmw 2015-4-22 10:00 begin */
    private String formID;
    /* lmw 2015-4-22 10:00 end */

    private List<UnitElement> untreadTasks = new ArrayList<UnitElement>(0);
    private List<UnitElement> readFields = new ArrayList<UnitElement>(0);
    private List<UnitElement> editFields = new ArrayList<UnitElement>(0);
    private List<UnitElement> hideFields = new ArrayList<UnitElement>(0);
    private List<UnitElement> notNullFields = new ArrayList<UnitElement>(0);
    private List<UnitElement> fileRights = new ArrayList<UnitElement>(0);
    private List<UnitElement> allFormFields = new ArrayList<UnitElement>(0);
    private List<UnitElement> allFormFieldWidgetIds = new ArrayList<UnitElement>(0);
    private List<UnitElement> formBtnRightSettings = new ArrayList<UnitElement>(0);
    // add by wujx 2010728 begin
    private FieldPropertyElement fieldProperty = new FieldPropertyElement();
    // add by wujx 2010728 begin
    private List<UnitElement> hideBlocks = new ArrayList<UnitElement>(0);
    private List<UnitElement> hideTabs = new ArrayList<UnitElement>(0);//隐藏页签

    private List<RightUnitElement> startRights = new ArrayList<>(0);
    private RightConfigElement startRightConfig;
    private List<RightUnitElement> rights = new ArrayList<>(0);
    private RightConfigElement todoRightConfig;
    private List<RightUnitElement> doneRights = new ArrayList<>(0);
    private RightConfigElement doneRightConfig;
    private List<RightUnitElement> monitorRights = new ArrayList<>(0);
    private RightConfigElement monitorRightConfig;
    private List<RightUnitElement> adminRights = new ArrayList<>(0);
    private RightConfigElement adminRightConfig;
    private List<RightUnitElement> copyToRights = new ArrayList<>(0);
    private RightConfigElement copyToRightConfig;
    private List<RightUnitElement> viewerRights = new ArrayList<>(0);
    private RightConfigElement viewerRightConfig;
    private List<ButtonElement> buttons = new ArrayList<ButtonElement>(0);
    private List<UnitElement> optNames = new ArrayList<UnitElement>(0);
    private List<RecordElement> records = new ArrayList<RecordElement>(0);
    private List<UserUnitElement> users = new ArrayList<>(0);
    private List<UserUnitElement> transferUsers = new ArrayList<>(0);
    private List<UserUnitElement> copyUsers = new ArrayList<>(0);
    private List<UserUnitElement> emptyToUsers = new ArrayList<>(0);
    private List<UserUnitElement> monitors = new ArrayList<>(0);
    private List<UserUnitElement> decisionMakers = new ArrayList<>(0);
    private String isSetUser;
    private String isSetCopyUser;
    private String isSetTransferUser;
    // 是否二次确认指定的抄送人(前一环节办理人可二次选择抄送人)
    private String isConfirmCopyUser;
    private String copyUserCondition;
    private String isSetUserEmpty;
    private String emptyToTask;
    private String emptyNoteDone;
    private String isSelectAgain;
    private String isOnlyOne;
    private String isAnyone;
    private String isByOrder;
    private String sameUserSubmit;
    private String isSetMonitor;
    private String isInheritMonitor;
    private String granularity;
    private String untreadType;
    private String snName;
    private String serialNo;
    private String printTemplate;
    private String printTemplateId;
    private String printTemplateUuid;
    private String listener;
    // 加载的JS模块
    private String customJsModule;
    private String isAllowApp;
    private String canEditForm;
    // 运转模式
    private ParallelGatewayElement parallelGateway = new ParallelGatewayElement();
    // 退回设置
    // 退回后允许直接提交至本环节
    private String allowReturnAfterRollback;
    // 仅允许提交至本环节
    private String onlyReturnAfterRollback;
    // 本环节不可被退回
    private String notRollback;
    // 本环节不可被撤回，1、不可被撤回
    private String notCancel;
    // 启用待办意见立场
    private String enableOpinionPosition;
    // 意见立场必填
    private String requiredOpinionPosition;
    // 显示用户意见立场值
    private String showUserOpinionPosition;
    // 显示意见立场统计
    private String showOpinionPositionStatistics;
    // 事件脚本
    private List<ScriptElement> eventScripts;
    // 列表默认展现
    private String expandList;

    // 是否开启多身份流转设置
    private boolean enabledJobFlowType;

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

    private Map<String, Map<String, String>> i18n = Maps.newHashMap();// 国际化配置


    /**
     * @return
     */
    @JsonIgnore
    public boolean isSubTask() {
        return "2".equals(this.type);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the x
     */
    public String getX() {
        return x;
    }

    /**
     * @param x 要设置的x
     */
    public void setX(String x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public String getY() {
        return y;
    }

    /**
     * @param y 要设置的y
     */
    public void setY(String y) {
        this.y = y;
    }

    /**
     * @return the conditionName
     */
    public String getConditionName() {
        return conditionName;
    }

    /**
     * @param conditionName 要设置的conditionName
     */
    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    /**
     * @return the conditionBody
     */
    public String getConditionBody() {
        return conditionBody;
    }

    /**
     * @param conditionBody 要设置的conditionBody
     */
    public void setConditionBody(String conditionBody) {
        this.conditionBody = conditionBody;
    }

    /**
     * @return the conditionX
     */
    public String getConditionX() {
        return conditionX;
    }

    /**
     * @param conditionX 要设置的conditionX
     */
    public void setConditionX(String conditionX) {
        this.conditionX = conditionX;
    }

    /**
     * @return the conditionY
     */
    public String getConditionY() {
        return conditionY;
    }

    /**
     * @param conditionY 要设置的conditionY
     */
    public void setConditionY(String conditionY) {
        this.conditionY = conditionY;
    }

    /**
     * @return the conditionLine
     */
    public String getConditionLine() {
        return conditionLine;
    }

    /**
     * @param conditionLine 要设置的conditionLine
     */
    public void setConditionLine(String conditionLine) {
        this.conditionLine = conditionLine;
    }

    /**
     * @return the formID
     */

    /* lmw 2015-4-22 10:00 begin */
    public String getFormID() {
        return formID;
    }

    /* lmw 2015-4-22 10:00 end */

    /**
     * @param formID 要设置的formID
     */
    /* lmw 2015-4-22 10:00 begin */
    public void setFormID(String formID) {
        this.formID = formID;
    }

    /* lmw 2015-4-22 10:00 end */

    /**
     * @return the untreadTasks
     */
    public List<UnitElement> getUntreadTasks() {
        return untreadTasks;
    }

    /**
     * @param untreadTasks 要设置的untreadTasks
     */
    public void setUntreadTasks(List<UnitElement> untreadTasks) {
        this.untreadTasks = untreadTasks;
    }

    /**
     * @return the readFields
     */
    public List<UnitElement> getReadFields() {
        return readFields;
    }

    /**
     * @param readFields 要设置的readFields
     */
    public void setReadFields(List<UnitElement> readFields) {
        this.readFields = readFields;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getReadFieldValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement readField : readFields) {
            list.add(readField.getValue());
        }
        return list;
    }

    /**
     * @return the editFields
     */
    public List<UnitElement> getEditFields() {
        return editFields;
    }

    /**
     * @param editFields 要设置的editFields
     */
    public void setEditFields(List<UnitElement> editFields) {
        this.editFields = editFields;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getEditFieldValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement editField : editFields) {
            list.add(editField.getValue());
        }
        return list;
    }

    /**
     * @return the hideFields
     */
    public List<UnitElement> getHideFields() {
        return hideFields;
    }

    /**
     * @param hideFields 要设置的hideFields
     */
    public void setHideFields(List<UnitElement> hideFields) {
        this.hideFields = hideFields;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getHideFieldValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement hideField : hideFields) {
            list.add(hideField.getValue());
        }
        return list;
    }

    /**
     * @return the notNullFields
     */
    public List<UnitElement> getNotNullFields() {
        return notNullFields;
    }

    /**
     * @param notNullFields 要设置的notNullFields
     */
    public void setNotNullFields(List<UnitElement> notNullFields) {
        this.notNullFields = notNullFields;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getNotNullFieldValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement notNullField : notNullFields) {
            list.add(notNullField.getValue());
        }
        return list;
    }

    @JsonIgnore
    public List<String> getFileRightsValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement fileRight : fileRights) {
            list.add(fileRight.getValue());
        }
        return list;
    }

    public List<UnitElement> getFileRights() {
        return fileRights;
    }

    public void setFileRights(List<UnitElement> fileRights) {
        this.fileRights = fileRights;
    }

    public List<UnitElement> getAllFormFields() {
        return allFormFields;
    }

    public void setAllFormFields(List<UnitElement> allFormFields) {
        this.allFormFields = allFormFields;
    }

    @JsonIgnore
    public List<String> getAllFormFieldsValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unitElement : allFormFields) {
            list.add(unitElement.getValue());
        }
        return list;
    }

    /**
     * @return the allFormFieldWidgetIds
     */
    public List<UnitElement> getAllFormFieldWidgetIds() {
        return allFormFieldWidgetIds;
    }

    @JsonIgnore
    public List<String> getAllFormFieldWidgetIdValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unitElement : allFormFieldWidgetIds) {
            list.add(unitElement.getValue());
        }
        return list;
    }

    /**
     * @param allFormFieldWidgetIds 要设置的allFormFieldWidgetIds
     */
    public void setAllFormFieldWidgetIds(List<UnitElement> allFormFieldWidgetIds) {
        this.allFormFieldWidgetIds = allFormFieldWidgetIds;
    }

    /**
     * @return the formBtnRightSettings
     */
    public List<UnitElement> getFormBtnRightSettings() {
        return formBtnRightSettings;
    }

    @JsonIgnore
    public List<String> getFormBtnRightSettingValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unitElement : formBtnRightSettings) {
            list.add(unitElement.getValue());
        }
        return list;
    }

    /**
     * @param formBtnRightSettings 要设置的formBtnRightSettings
     */
    public void setFormBtnRightSettings(List<UnitElement> formBtnRightSettings) {
        this.formBtnRightSettings = formBtnRightSettings;
    }

    public FieldPropertyElement getFieldProperty() {
        return fieldProperty;
    }

    public void setFieldProperty(FieldPropertyElement fieldProperty) {
        this.fieldProperty = fieldProperty;
    }

    @JsonIgnore
    public String getFieldPropertyValues() {
        return fieldProperty.getValue();
    }

    /**
     * @return the hideBlocks
     */
    public List<UnitElement> getHideBlocks() {
        return hideBlocks;
    }

    /**
     * @param hideBlocks 要设置的hideBlocks
     */
    public void setHideBlocks(List<UnitElement> hideBlocks) {
        this.hideBlocks = hideBlocks;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getHideBlockValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement hideBlock : hideBlocks) {
            list.add(hideBlock.getValue());
        }
        return list;
    }

    /**
     * @return the startRights
     */
    public List<RightUnitElement> getStartRights() {
        return startRights;
    }

    /**
     * @param startRights 要设置的startRights
     */
    public void setStartRights(List<RightUnitElement> startRights) {
        this.startRights = startRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonStartRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : startRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getStartButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : startRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the startRightConfig
     */
    public RightConfigElement getStartRightConfig() {
        return startRightConfig;
    }

    /**
     * @param startRightConfig 要设置的startRightConfig
     */
    public void setStartRightConfig(RightConfigElement startRightConfig) {
        this.startRightConfig = startRightConfig;
    }

    /**
     * @return the rights
     */
    public List<RightUnitElement> getRights() {
        return rights;
    }

    /**
     * @param rights 要设置的rights
     */
    public void setRights(List<RightUnitElement> rights) {
        this.rights = rights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonTodoRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : rights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getTodoButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : rights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the todoRightConfig
     */
    public RightConfigElement getTodoRightConfig() {
        return todoRightConfig;
    }

    /**
     * @param todoRightConfig 要设置的todoRightConfig
     */
    public void setTodoRightConfig(RightConfigElement todoRightConfig) {
        this.todoRightConfig = todoRightConfig;
    }

    /**
     * @return the doneRights
     */
    public List<RightUnitElement> getDoneRights() {
        return doneRights;
    }

    /**
     * @param doneRights 要设置的doneRights
     */
    public void setDoneRights(List<RightUnitElement> doneRights) {
        this.doneRights = doneRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonDoneRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : doneRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getDoneButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : doneRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the doneRightConfig
     */
    public RightConfigElement getDoneRightConfig() {
        return doneRightConfig;
    }

    /**
     * @param doneRightConfig 要设置的doneRightConfig
     */
    public void setDoneRightConfig(RightConfigElement doneRightConfig) {
        this.doneRightConfig = doneRightConfig;
    }

    /**
     * @return the monitorRights
     */
    public List<RightUnitElement> getMonitorRights() {
        return monitorRights;
    }

    /**
     * @param monitorRights 要设置的monitorRights
     */
    public void setMonitorRights(List<RightUnitElement> monitorRights) {
        this.monitorRights = monitorRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonMonitorRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : monitorRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getMonitorButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : monitorRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the monitorRightConfig
     */
    public RightConfigElement getMonitorRightConfig() {
        return monitorRightConfig;
    }

    /**
     * @param monitorRightConfig 要设置的monitorRightConfig
     */
    public void setMonitorRightConfig(RightConfigElement monitorRightConfig) {
        this.monitorRightConfig = monitorRightConfig;
    }

    /**
     * @return the adminRights
     */
    public List<RightUnitElement> getAdminRights() {
        return adminRights;
    }

    /**
     * @param adminRights 要设置的adminRights
     */
    public void setAdminRights(List<RightUnitElement> adminRights) {
        this.adminRights = adminRights;
    }


    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonAdminRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : adminRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getAdminButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : adminRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the adminRightConfig
     */
    public RightConfigElement getAdminRightConfig() {
        return adminRightConfig;
    }

    /**
     * @param adminRightConfig 要设置的adminRightConfig
     */
    public void setAdminRightConfig(RightConfigElement adminRightConfig) {
        this.adminRightConfig = adminRightConfig;
    }

    /**
     * @return the copyToRights
     */
    public List<RightUnitElement> getCopyToRights() {
        return copyToRights;
    }

    /**
     * @param copyToRights 要设置的copyToRights
     */
    public void setCopyToRights(List<RightUnitElement> copyToRights) {
        this.copyToRights = copyToRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonCopyToRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : copyToRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getCopyToButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : copyToRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @return the copyToRightConfig
     */
    public RightConfigElement getCopyToRightConfig() {
        return copyToRightConfig;
    }

    /**
     * @param copyToRightConfig 要设置的copyToRightConfig
     */
    public void setCopyToRightConfig(RightConfigElement copyToRightConfig) {
        this.copyToRightConfig = copyToRightConfig;
    }

    /**
     * @return the viewerRights
     */
    public List<RightUnitElement> getViewerRights() {
        return viewerRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getButtonViewerRights() {
        List<String> btnRights = new ArrayList<String>();
        for (UnitElement unit : viewerRights) {
            btnRights.add(unit.getValue());
        }
        return btnRights;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<Button> getViewerButtons() {
        List<Button> btnRights = new ArrayList<>();
        for (RightUnitElement unit : viewerRights) {
            btnRights.add(new Button(unit.getTitle(), unit.getValue(), unit.getUuid(), unit.getI18n()));
        }
        return btnRights;
    }

    /**
     * @param viewerRights 要设置的viewerRights
     */
    public void setViewerRights(List<RightUnitElement> viewerRights) {
        this.viewerRights = viewerRights;
    }

    /**
     * @return the viewerRightConfig
     */
    public RightConfigElement getViewerRightConfig() {
        return viewerRightConfig;
    }

    /**
     * @param viewerRightConfig 要设置的viewerRightConfig
     */
    public void setViewerRightConfig(RightConfigElement viewerRightConfig) {
        this.viewerRightConfig = viewerRightConfig;
    }

    @JsonIgnore
    public Map<String, RightConfigElement> getRightConfigMap(String btnCode) {
        Map<String, RightConfigElement> map = Maps.newLinkedHashMap();
        if (startRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonStartRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.DRAFT, startRightConfig);
            }
        }
        if (todoRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonTodoRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.TODO, todoRightConfig);
            }
        }
        if (doneRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonDoneRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.DONE, doneRightConfig);
            }
        }
        if (monitorRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonMonitorRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.SUPERVISE, monitorRightConfig);
            }
        }
        if (adminRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonAdminRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.MONITOR, adminRightConfig);
            }
        }
        if (copyToRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonCopyToRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.UNREAD, copyToRightConfig);
                map.put(WorkFlowAclRole.FLAG_READ, copyToRightConfig);
            }
        }
        if (viewerRightConfig != null) {
            if (StringUtils.isBlank(btnCode) || getButtonViewerRights().contains(btnCode)) {
                map.put(WorkFlowAclRole.ATTENTION, viewerRightConfig);
                map.put(WorkFlowAclRole.VIEWER, viewerRightConfig);
            }
        }
        return map;
    }

    /**
     * @return the buttons
     */
    public List<ButtonElement> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<ButtonElement> buttons) {
        this.buttons = buttons;
    }

    /**
     * @return the optNames
     */
    public List<UnitElement> getOptNames() {
        return optNames;
    }

    /**
     * @param optNames 要设置的optNames
     */
    public void setOptNames(List<UnitElement> optNames) {
        this.optNames = optNames;
    }

    /**
     * @return the records
     */
    public List<RecordElement> getRecords() {
        return records;
    }

    /**
     * @param records 要设置的records
     */
    public void setRecords(List<RecordElement> records) {
        this.records = records;
    }

    /**
     * @return the users
     */
    public List<UserUnitElement> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<UserUnitElement> users) {
        this.users = users;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getUnitUserValues() {
        List<String> list = new ArrayList<String>();
        if ("1".equals(this.isSetUser)) {
            for (UnitElement unit : users) {
                // 1 组织选择框
                if (Integer.valueOf(1).equals(unit.getType())) {
                    list.add(unit.getValue());
                }
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getFormFieldUserValues() {
        List<String> list = new ArrayList<String>();
        if ("1".equals(this.isSetUser)) {
            for (UnitElement unit : users) {
                // 2 表单域
                if (Integer.valueOf(2).equals(unit.getType())) {
                    list.add(unit.getValue());
                }
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getHistoryUserValues() {
        List<String> list = new ArrayList<String>();
        if ("1".equals(this.isSetUser)) {
            for (UnitElement unit : users) {
                // 4 历史流程环节
                if (Integer.valueOf(4).equals(unit.getType())) {
                    list.add(unit.getValue());
                }
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getOptionUserValues() {
        List<String> list = new ArrayList<String>();
        if ("1".equals(this.isSetUser)) {
            for (UnitElement unit : users) {
                // 8 参与者
                if (Integer.valueOf(8).equals(unit.getType())) {
                    list.add(unit.getValue());
                }
            }
        }
        return list;
    }

    public List<UserUnitElement> getTransferUsers() {
        return transferUsers;
    }

    public void setTransferUsers(List<UserUnitElement> transferUsers) {
        this.transferUsers = transferUsers;
    }

    /**
     * @return the copyUsers
     */
    public List<UserUnitElement> getCopyUsers() {
        return copyUsers;
    }

    /**
     * @param copyUsers 要设置的copyUsers
     */
    public void setCopyUsers(List<UserUnitElement> copyUsers) {
        this.copyUsers = copyUsers;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getUnitCopyUserValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : copyUsers) {
            // 1 组织选择框
            if (Integer.valueOf(1).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getFormFieldCopyUserValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : users) {
            // 2 表单域
            if (Integer.valueOf(2).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getHistoryCopyUserValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : copyUsers) {
            // 4 历史流程环节
            if (Integer.valueOf(4).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getOptionCopyUserValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement unit : copyUsers) {
            // 8 参与者
            if (Integer.valueOf(8).equals(unit.getType())) {
                list.add(unit.getValue());
            }
        }
        return list;
    }

    /**
     * @return
     */
    public List<UserUnitElement> getEmptyToUsers() {
        return emptyToUsers;
    }

    /**
     * @param emptyToUsers 要设置的emptyToUsers
     */
    public void setEmptyToUsers(List<UserUnitElement> emptyToUsers) {
        this.emptyToUsers = emptyToUsers;
    }

    /**
     * @return the monitors
     */
    public List<UserUnitElement> getMonitors() {
        return monitors;
    }

    /**
     * @param monitors 要设置的monitors
     */
    public void setMonitors(List<UserUnitElement> monitors) {
        this.monitors = monitors;
    }

    /**
     * @return the decisionMakers
     */
    public List<UserUnitElement> getDecisionMakers() {
        return decisionMakers;
    }

    /**
     * @param decisionMakers 要设置的decisionMakers
     */
    public void setDecisionMakers(List<UserUnitElement> decisionMakers) {
        this.decisionMakers = decisionMakers;
    }

    /**
     * @return the isSetUser
     */
    public String getIsSetUser() {
        return isSetUser;
    }

    /**
     * @param isSetUser 要设置的isSetUser
     */
    public void setIsSetUser(String isSetUser) {
        this.isSetUser = isSetUser;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetUser() {
        return "1".equals(isSetUser);
    }

    /**
     * @return the isSetCopyUser
     */
    public String getIsSetCopyUser() {
        return isSetCopyUser;
    }

    /**
     * @param isSetCopyUser 要设置的isSetCopyUser
     */
    public void setIsSetCopyUser(String isSetCopyUser) {
        this.isSetCopyUser = isSetCopyUser;
    }

    /**
     * @return the isSetCopyUser
     */
    @JsonIgnore
    public boolean isSetCopyUser() {
        return "1".equals(isSetCopyUser);
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetTransferUser() {
        return "1".equals(isSetTransferUser);
    }

    public String getIsSetTransferUser() {
        return isSetTransferUser;
    }

    public void setIsSetTransferUser(String isSetTransferUser) {
        this.isSetTransferUser = isSetTransferUser;
    }

    /**
     * @return the isConfirmCopyUser
     */
    public String getIsConfirmCopyUser() {
        return isConfirmCopyUser;
    }

    /**
     * @param isConfirmCopyUser 要设置的isConfirmCopyUser
     */
    public void setIsConfirmCopyUser(String isConfirmCopyUser) {
        this.isConfirmCopyUser = isConfirmCopyUser;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isConfirmCopyUser() {
        return "1".equals(isConfirmCopyUser);
    }

    /**
     * @return the copyUserCondition
     */
    public String getCopyUserCondition() {
        return copyUserCondition;
    }

    /**
     * @param copyUserCondition 要设置的copyUserCondition
     */
    public void setCopyUserCondition(String copyUserCondition) {
        this.copyUserCondition = copyUserCondition;
    }

    /**
     * @return the isSetUserEmpty
     */
    public String getIsSetUserEmpty() {
        return isSetUserEmpty;
    }

    /**
     * @param isSetUserEmpty 要设置的isSetUserEmpty
     */
    public void setIsSetUserEmpty(String isSetUserEmpty) {
        this.isSetUserEmpty = isSetUserEmpty;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetUserEmptyToTask() {
        return "1".equals(isSetUserEmpty);
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetUserEmptyToUser() {
        return "2".equals(isSetUserEmpty);
    }

    /**
     * @return the emptyToTask
     */
    public String getEmptyToTask() {
        return emptyToTask;
    }

    /**
     * @param emptyToTask 要设置的emptyToTask
     */
    public void setEmptyToTask(String emptyToTask) {
        this.emptyToTask = emptyToTask;
    }

    /**
     * @return the emptyNoteDone
     */
    public String getEmptyNoteDone() {
        return emptyNoteDone;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEmptyNoteDone() {
        return "1".equals(emptyNoteDone);
    }

    /**
     * @param emptyNoteDone 要设置的emptyNoteDone
     */
    public void setEmptyNoteDone(String emptyNoteDone) {
        this.emptyNoteDone = emptyNoteDone;
    }

    /**
     * @return the isSelectAgain
     */
    public String getIsSelectAgain() {
        return isSelectAgain;
    }

    /**
     * @param isSelectAgain 要设置的isSelectAgain
     */
    public void setIsSelectAgain(String isSelectAgain) {
        this.isSelectAgain = isSelectAgain;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSelectAgain() {
        return "1".equals(isSelectAgain);
    }

    /**
     * @return the isOnlyOne
     */
    public String getIsOnlyOne() {
        return isOnlyOne;
    }

    /**
     * @param isOnlyOne 要设置的isOnlyOne
     */
    public void setIsOnlyOne(String isOnlyOne) {
        this.isOnlyOne = isOnlyOne;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isOnlyOne() {
        return "1".equals(isOnlyOne);
    }

    /**
     * @return the isAnyone
     */
    public String getIsAnyone() {
        return isAnyone;
    }

    /**
     * @param isAnyone 要设置的isAnyone
     */
    public void setIsAnyone(String isAnyone) {
        this.isAnyone = isAnyone;
    }

    /**
     * @return
     */
    @JsonIgnore
    public Boolean isAnyone() {
        return "1".equals(isAnyone);
    }

    /**
     * @return the isByOrder
     */
    public String getIsByOrder() {
        return isByOrder;
    }

    /**
     * @param isByOrder 要设置的isByOrder
     */
    public void setIsByOrder(String isByOrder) {
        this.isByOrder = isByOrder;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isByOrder() {
        return "1".equals(isByOrder);
    }

    /**
     * @return the sameUserSubmit
     */
    public String getSameUserSubmit() {
        return sameUserSubmit;
    }

    /**
     * @param sameUserSubmit 要设置的sameUserSubmit
     */
    public void setSameUserSubmit(String sameUserSubmit) {
        this.sameUserSubmit = sameUserSubmit;
    }

    /**
     * @return the isSetMonitor
     */
    public String getIsSetMonitor() {
        return isSetMonitor;
    }

    /**
     * @param isSetMonitor 要设置的isSetMonitor
     */
    public void setIsSetMonitor(String isSetMonitor) {
        this.isSetMonitor = isSetMonitor;
    }

    /**
     * @return the isInheritMonitor
     */
    public String getIsInheritMonitor() {
        return isInheritMonitor;
    }

    /**
     * @param isInheritMonitor 要设置的isInheritMonitor
     */
    public void setIsInheritMonitor(String isInheritMonitor) {
        this.isInheritMonitor = isInheritMonitor;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isInheritMonitor() {
        return "1".equals(isInheritMonitor);
    }

    /**
     * @return the granularity
     */
    public String getGranularity() {
        return granularity;
    }

    /**
     * @param granularity 要设置的granularity
     */
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    /**
     * @return the untreadType
     */
    public String getUntreadType() {
        return untreadType;
    }

    /**
     * @param untreadType 要设置的untreadType
     */
    public void setUntreadType(String untreadType) {
        this.untreadType = untreadType;
    }

    /**
     * @return the snName
     */
    public String getSnName() {
        return snName;
    }

    /**
     * @param snName 要设置的snName
     */
    public void setSnName(String snName) {
        this.snName = snName;
    }

    /**
     * @return the serialNo
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @param serialNo 要设置的serialNo
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the printTemplate
     */
    public String getPrintTemplate() {
        return printTemplate;
    }

    /**
     * @param printTemplate 要设置的printTemplate
     */
    public void setPrintTemplate(String printTemplate) {
        this.printTemplate = printTemplate;
    }

    /**
     * @return the printTemplateId
     */
    public String getPrintTemplateId() {
        return printTemplateId;
    }

    /**
     * @param printTemplateId 要设置的printTemplateId
     */
    public void setPrintTemplateId(String printTemplateId) {
        this.printTemplateId = printTemplateId;
    }

    /**
     * @return the printTemplateUuid
     */
    public String getPrintTemplateUuid() {
        return printTemplateUuid;
    }

    /**
     * @param printTemplateUuid 要设置的printTemplateUuid
     */
    public void setPrintTemplateUuid(String printTemplateUuid) {
        this.printTemplateUuid = printTemplateUuid;
    }

    /**
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
    }

    /**
     * @return the listener
     */
    @JsonIgnore
    public String[] getListeners() {
        if (listener == null) {
            return new String[0];
        }
        return StringUtils.split(listener, Separator.SEMICOLON.getValue());
    }

    /**
     * @return the customJsModule
     */
    public String getCustomJsModule() {
        return customJsModule;
    }

    /**
     * @param customJsModule 要设置的customJsModule
     */
    public void setCustomJsModule(String customJsModule) {
        this.customJsModule = customJsModule;
    }

    /**
     * @return the parallelGateway
     */
    public ParallelGatewayElement getParallelGateway() {
        return parallelGateway;
    }

    /**
     * @param parallelGateway 要设置的parallelGateway
     */
    public void setParallelGateway(ParallelGatewayElement parallelGateway) {
        this.parallelGateway = parallelGateway;
    }

    /**
     * @return the allowReturnAfterRollback
     */
    public String getAllowReturnAfterRollback() {
        return allowReturnAfterRollback;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsAllowReturnAfterRollback() {
        return "1".equals(allowReturnAfterRollback);
    }

    /**
     * @param allowReturnAfterRollback 要设置的allowReturnAfterRollback
     */
    public void setAllowReturnAfterRollback(String allowReturnAfterRollback) {
        this.allowReturnAfterRollback = allowReturnAfterRollback;
    }

    @JsonIgnore
    public boolean getIsNotRollback() {
        return "1".equals(notRollback);
    }

    public String getNotRollback() {
        return notRollback;
    }

    public void setNotRollback(String notRollback) {
        this.notRollback = notRollback;
    }

    /**
     * @return the notCancel
     */
    public String getNotCancel() {
        return notCancel;
    }

    /**
     * @param notCancel 要设置的notCancel
     */
    public void setNotCancel(String notCancel) {
        this.notCancel = notCancel;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsNotCancel() {
        return "1".equals(notCancel);
    }

    /**
     * @return the onlyReturnAfterRollback
     */
    public String getOnlyReturnAfterRollback() {
        return onlyReturnAfterRollback;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsOnlyReturnAfterRollback() {
        return "1".equals(onlyReturnAfterRollback);
    }


    /**
     * @param onlyReturnAfterRollback 要设置的onlyReturnAfterRollback
     */
    public void setOnlyReturnAfterRollback(String onlyReturnAfterRollback) {
        this.onlyReturnAfterRollback = onlyReturnAfterRollback;
    }

    public String getIsAllowApp() {
        return isAllowApp;
    }

    public void setIsAllowApp(String isAllowApp) {
        this.isAllowApp = isAllowApp;
    }

    /**
     * @return the enableOpinionPosition
     */
    public String getEnableOpinionPosition() {
        return enableOpinionPosition;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsEnableOpinionPosition() {
        return "1".equals(enableOpinionPosition) || StringUtils.isBlank(enableOpinionPosition) && CollectionUtils.isNotEmpty(optNames);
    }

    /**
     * @param enableOpinionPosition 要设置的enableOpinionPosition
     */
    public void setEnableOpinionPosition(String enableOpinionPosition) {
        this.enableOpinionPosition = enableOpinionPosition;
    }

    /**
     * @return the requiredOpinionPosition
     */
    public String getRequiredOpinionPosition() {
        return requiredOpinionPosition;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsRequiredOpinionPosition() {
        return "1".equals(requiredOpinionPosition);
    }

    /**
     * @param requiredOpinionPosition 要设置的requiredOpinionPosition
     */
    public void setRequiredOpinionPosition(String requiredOpinionPosition) {
        this.requiredOpinionPosition = requiredOpinionPosition;
    }

    /**
     * @return the showUserOpinionPosition
     */
    public String getShowUserOpinionPosition() {
        return showUserOpinionPosition;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean getIsShowUserOpinionPosition() {
        return "1".equals(showUserOpinionPosition);
    }

    /**
     * @param showUserOpinionPosition 要设置的showUserOpinionPosition
     */
    public void setShowUserOpinionPosition(String showUserOpinionPosition) {
        this.showUserOpinionPosition = showUserOpinionPosition;
    }

    /**
     * @return the showOpinionPositionStatistics
     */
    public String getShowOpinionPositionStatistics() {
        return showOpinionPositionStatistics;
    }

    /**
     * @return the showOpinionPositionStatistics
     */
    @JsonIgnore
    public boolean getIsShowOpinionPositionStatistics() {
        return "1".equals(showOpinionPositionStatistics);
    }

    /**
     * @param showOpinionPositionStatistics 要设置的showOpinionPositionStatistics
     */
    public void setShowOpinionPositionStatistics(String showOpinionPositionStatistics) {
        this.showOpinionPositionStatistics = showOpinionPositionStatistics;
    }

    /**
     * @return the eventScripts
     */
    public List<ScriptElement> getEventScripts() {
        return eventScripts;
    }

    /**
     * @param eventScripts 要设置的eventScripts
     */
    public void setEventScripts(List<ScriptElement> eventScripts) {
        this.eventScripts = eventScripts;
    }

    /**
     * @return the hideTabs
     */
    public List<UnitElement> getHideTabs() {
        return hideTabs;
    }

    /**
     * @param hideTabs 要设置的hideTabs
     */
    public void setHideTabs(List<UnitElement> hideTabs) {
        this.hideTabs = hideTabs;
    }

    /**
     * @return
     */
    @JsonIgnore
    public List<String> getHideTabValues() {
        List<String> list = new ArrayList<String>();
        for (UnitElement hideTab : hideTabs) {
            list.add(hideTab.getValue());
        }
        return list;
    }

    public String getCanEditForm() {
        return canEditForm;
    }

    @JsonIgnore
    public boolean getIsCanEditForm() {
        return "1".equals(canEditForm);
    }

    public void setCanEditForm(String canEditForm) {
        this.canEditForm = canEditForm;
    }

    public String getExpandList() {
        return expandList;
    }

    public void setExpandList(String expandList) {
        this.expandList = expandList;
    }

    /**
     * @return the enabledJobFlowType
     */
    public boolean isEnabledJobFlowType() {
        return enabledJobFlowType;
    }

    /**
     * @param enabledJobFlowType 要设置的enabledJobFlowType
     */
    public void setEnabledJobFlowType(boolean enabledJobFlowType) {
        this.enabledJobFlowType = enabledJobFlowType;
    }

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

    public Map<String, Map<String, String>> getI18n() {
        return i18n;
    }

    public void setI18n(Map<String, Map<String, String>> i18n) {
        this.i18n = i18n;
    }
}
