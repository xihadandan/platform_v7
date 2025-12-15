/*
 * @(#)2018年12月24日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.dto;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.app.workflow.facade.service.impl.WorkFlowSimulationServiceImpl;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRuntimeParams;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.work.bean.FlowHandingStateInfoDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Set;

/**
 * Description: 流程仿真数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月24日.1	zhulh		2018年12月24日		Create
 * </pre>
 * @date 2018年12月24日
 */
@ApiModel("流程仿真数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowSimulationDataDto extends InteractionTaskData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3035517420981071848L;

    // 仿真参数
    @ApiModelProperty("仿真参数")
    private SimulationParams simulationParams;

    @ApiModelProperty("启动人索引")
    private int startUserIndex;

    @ApiModelProperty("当前仿真运行次数")
    private int currentRunNum;

    // 用户已交互的环节
    @ApiModelProperty("用户已交互的环节")
    private Set<String> userInteractedTasks;

    // 办理人ID
    @ApiModelProperty("办理人ID")
    private String todoUserId;

    // 办理人名称
    @ApiModelProperty("办理人名称")
    private String todoUserName;

    // 督办人ID
    @ApiModelProperty("督办人ID")
    private String superviseUserId;

    // 督办人名称
    @ApiModelProperty("督办人名称")
    private String superviseUserName;

    // 流程定义UUID
    @ApiModelProperty("流程定义UUID")
    private String flowDefUuid;

    // 流程定义ID
    @ApiModelProperty("流程定义ID")
    private String flowDefId;

    // 流程实例UUID
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    // 环节实例UUID
    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;

    // 环节名称
    @ApiModelProperty("环节名称")
    private String taskName;

    // 环节ID
    @ApiModelProperty("环节ID")
    private String taskId;

    // 环节类型
    @ApiModelProperty("环节类型")
    private Integer taskType;

    // 表单定义UUID
    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    // 表单数据UUID
    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    // 是否办结
    @ApiModelProperty("是否办结")
    private Boolean isOver;

    @ApiModelProperty("仿真状态，running仿真中、pause暂停、success成功")
    private String state;

    @ApiModelProperty("流程办理状态信息")
    private FlowHandingStateInfoDto handingStateInfo;

    @ApiModelProperty("仿真记录UUID")
    private String recordUuid;

    @ApiModelProperty("运行时参数")
    private FlowSimulationRuntimeParams runtimeParams = new FlowSimulationRuntimeParams();

    @ApiModelProperty("子流程信息")
    private WorkFlowSimulationServiceImpl.SimulationTaskInfo subTaskInfo;

    /**
     * @return the simulationParams
     */
    public SimulationParams getSimulationParams() {
        return simulationParams;
    }

    /**
     * @param simulationParams 要设置的simulationParams
     */
    public void setSimulationParams(SimulationParams simulationParams) {
        this.simulationParams = simulationParams;
    }

    /**
     * @return the startUserIndex
     */
    public int getStartUserIndex() {
        return startUserIndex;
    }

    /**
     * @param startUserIndex 要设置的startUserIndex
     */
    public void setStartUserIndex(int startUserIndex) {
        this.startUserIndex = startUserIndex;
    }

    /**
     * @return the currentRunNum
     */
    public int getCurrentRunNum() {
        return currentRunNum;
    }

    /**
     * @param currentRunNum 要设置的currentRunNum
     */
    public void setCurrentRunNum(int currentRunNum) {
        this.currentRunNum = currentRunNum;
    }

    /**
     * @return the userInteractedTasks
     */
    public Set<String> getUserInteractedTasks() {
        return userInteractedTasks;
    }

    /**
     * @param userInteractedTasks 要设置的userInteractedTasks
     */
    public void setUserInteractedTasks(Set<String> userInteractedTasks) {
        this.userInteractedTasks = userInteractedTasks;
    }

    /**
     * @return the todoUserId
     */
    public String getTodoUserId() {
        return todoUserId;
    }

    /**
     * @param todoUserId 要设置的todoUserId
     */
    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

    /**
     * @return the todoUserName
     */
    public String getTodoUserName() {
        return todoUserName;
    }

    /**
     * @param todoUserName 要设置的todoUserName
     */
    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
    }

    /**
     * @return the superviseUserId
     */
    public String getSuperviseUserId() {
        return superviseUserId;
    }

    /**
     * @param superviseUserId 要设置的superviseUserId
     */
    public void setSuperviseUserId(String superviseUserId) {
        this.superviseUserId = superviseUserId;
    }

    /**
     * @return the superviseUserName
     */
    public String getSuperviseUserName() {
        return superviseUserName;
    }

    /**
     * @param superviseUserName 要设置的superviseUserName
     */
    public void setSuperviseUserName(String superviseUserName) {
        this.superviseUserName = superviseUserName;
    }

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskType
     */
    public Integer getTaskType() {
        return taskType;
    }

    /**
     * @param taskType 要设置的taskType
     */
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the isOver
     */
    public Boolean getIsOver() {
        return isOver;
    }

    /**
     * @param isOver 要设置的isOver
     */
    public void setIsOver(Boolean isOver) {
        this.isOver = isOver;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the handingStateInfo
     */
    public FlowHandingStateInfoDto getHandingStateInfo() {
        return handingStateInfo;
    }

    /**
     * @param handingStateInfo 要设置的handingStateInfo
     */
    public void setHandingStateInfo(FlowHandingStateInfoDto handingStateInfo) {
        this.handingStateInfo = handingStateInfo;
    }

    /**
     * @return the recordUuid
     */
    public String getRecordUuid() {
        return recordUuid;
    }

    /**
     * @param recordUuid 要设置的recordUuid
     */
    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    /**
     * @return the runtimeParams
     */
    @JsonIgnore
    public FlowSimulationRuntimeParams getRuntimeParams() {
        return runtimeParams;
    }

    /**
     * @param runtimeParams 要设置的runtimeParams
     */
    public void setRuntimeParams(FlowSimulationRuntimeParams runtimeParams) {
        this.runtimeParams = runtimeParams;
    }

    /**
     * @return the subTaskInfo
     */
    public WorkFlowSimulationServiceImpl.SimulationTaskInfo getSubTaskInfo() {
        return subTaskInfo;
    }

    /**
     * @param subTaskInfo 要设置的subTaskInfo
     */
    public void setSubTaskInfo(WorkFlowSimulationServiceImpl.SimulationTaskInfo subTaskInfo) {
        this.subTaskInfo = subTaskInfo;
    }

    /**
     * Description: 仿真参数
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年2月2日.1	zhulh		2021年2月2日		Create
     * </pre>
     * @date 2021年2月2日
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真参数")
    public static class SimulationParams extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3102304239046787261L;

        public static final String FORM_DATA_SOURCE_RECORD = "record";

        public static final String INTERACTION_MODE_REQUIREDFIELD = "requiredField";

        // 启动人ID
        @ApiModelProperty("启动人ID")
        private String startUserId;

        @ApiModelProperty("办理人ID")
        private String taskUserId;

        @ApiModelProperty("执行次数")
        private Integer runNum = 1;

        @ApiModelProperty("开始执行环节ID")
        private String startTaskId;

        @ApiModelProperty("暂停执行环节")
        private List<String> pauseTasks;

        @ApiModelProperty("结束执行环节")
        private List<String> endTasks;

        // 是否生成流水号
        @ApiModelProperty("是否生成流水号")
        private boolean generateSerialNumber;

        // 是否发送消息
        @ApiModelProperty("是否发送消息")
        private boolean sendMsg;

        // 是否归档
        @ApiModelProperty("是否归档")
        private boolean archive;

        // 仿真结束后自动打开查看流程数据
        @ApiModelProperty("仿真结束后自动打开查看流程数据")
        private boolean autoViewWorkAfterSimulation;

        // 表单数据来源
        @ApiModelProperty("表单数据来源，create新建表单数据、report使用仿真报告数据")
        private String formDataSource;

        @ApiModelProperty("仿真记录数据UUID")
        private String recordUuid;

        @ApiModelProperty("表单数据交互方式，requiredField按必填字段、task按环节")
        private String interactionMode = INTERACTION_MODE_REQUIREDFIELD;

        // 交互数据的环节ID列表
        @ApiModelProperty("交互数据的环节ID列表")
        private List<String> interactionTasks;

        // 环节办理意见
        @ApiModelProperty("环节办理意见")
        private List<SimulationOpinion> opinions;

        @ApiModelProperty("仿真环节")
        private List<SimulationTask> tasks;

        /**
         * @return the startUserId
         */
        public String getStartUserId() {
            return startUserId;
        }

        /**
         * @param startUserId 要设置的startUserId
         */
        public void setStartUserId(String startUserId) {
            this.startUserId = startUserId;
        }

        /**
         * @return the taskUserId
         */
        public String getTaskUserId() {
            return taskUserId;
        }

        /**
         * @param taskUserId 要设置的taskUserId
         */
        public void setTaskUserId(String taskUserId) {
            this.taskUserId = taskUserId;
        }

        /**
         * @return the runNum
         */
        public Integer getRunNum() {
            return runNum;
        }

        /**
         * @param runNum 要设置的runNum
         */
        public void setRunNum(Integer runNum) {
            this.runNum = runNum;
        }

        /**
         * @return the startTaskId
         */
        public String getStartTaskId() {
            return startTaskId;
        }

        /**
         * @param startTaskId 要设置的startTaskId
         */
        public void setStartTaskId(String startTaskId) {
            this.startTaskId = startTaskId;
        }

        /**
         * @return the pauseTasks
         */
        public List<String> getPauseTasks() {
            return pauseTasks;
        }

        /**
         * @param pauseTasks 要设置的pauseTasks
         */
        public void setPauseTasks(List<String> pauseTasks) {
            this.pauseTasks = pauseTasks;
        }

        /**
         * @return the endTasks
         */
        public List<String> getEndTasks() {
            return endTasks;
        }

        /**
         * @param endTasks 要设置的endTasks
         */
        public void setEndTasks(List<String> endTasks) {
            this.endTasks = endTasks;
        }

        /**
         * @return the generateSerialNumber
         */
        public boolean isGenerateSerialNumber() {
            return generateSerialNumber;
        }

        /**
         * @param generateSerialNumber 要设置的generateSerialNumber
         */
        public void setGenerateSerialNumber(boolean generateSerialNumber) {
            this.generateSerialNumber = generateSerialNumber;
        }

        /**
         * @return the sendMsg
         */
        public boolean isSendMsg() {
            return sendMsg;
        }

        /**
         * @param sendMsg 要设置的sendMsg
         */
        public void setSendMsg(boolean sendMsg) {
            this.sendMsg = sendMsg;
        }

        /**
         * @return the archive
         */
        public boolean isArchive() {
            return archive;
        }

        /**
         * @param archive 要设置的archive
         */
        public void setArchive(boolean archive) {
            this.archive = archive;
        }

        /**
         * @return the autoViewWorkAfterSimulation
         */
        public boolean isAutoViewWorkAfterSimulation() {
            return autoViewWorkAfterSimulation;
        }

        /**
         * @param autoViewWorkAfterSimulation 要设置的autoViewWorkAfterSimulation
         */
        public void setAutoViewWorkAfterSimulation(boolean autoViewWorkAfterSimulation) {
            this.autoViewWorkAfterSimulation = autoViewWorkAfterSimulation;
        }

        /**
         * @return the formDataSource
         */
        public String getFormDataSource() {
            return formDataSource;
        }

        /**
         * @param formDataSource 要设置的formDataSource
         */
        public void setFormDataSource(String formDataSource) {
            this.formDataSource = formDataSource;
        }

        /**
         * @return the recordUuid
         */
        public String getRecordUuid() {
            return recordUuid;
        }

        /**
         * @param recordUuid 要设置的recordUuid
         */
        public void setRecordUuid(String recordUuid) {
            this.recordUuid = recordUuid;
        }

        /**
         * @return the interactionMode
         */
        public String getInteractionMode() {
            return interactionMode;
        }

        /**
         * @param interactionMode 要设置的interactionMode
         */
        public void setInteractionMode(String interactionMode) {
            this.interactionMode = interactionMode;
        }

        /**
         * @return the interactionTasks
         */
        public List<String> getInteractionTasks() {
            return interactionTasks;
        }

        /**
         * @param interactionTasks 要设置的interactionTasks
         */
        public void setInteractionTasks(List<String> interactionTasks) {
            this.interactionTasks = interactionTasks;
        }

        /**
         * @return the opinions
         */
        public List<SimulationOpinion> getOpinions() {
            return opinions;
        }

        /**
         * @param opinions 要设置的opinions
         */
        public void setOpinions(List<SimulationOpinion> opinions) {
            this.opinions = opinions;
        }

        /**
         * @return the tasks
         */
        public List<SimulationTask> getTasks() {
            return tasks;
        }

        /**
         * @param tasks 要设置的tasks
         */
        public void setTasks(List<SimulationTask> tasks) {
            this.tasks = tasks;
        }

    }

    /**
     * Description: 仿真办理意见
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年2月2日.1	zhulh		2021年2月2日		Create
     * </pre>
     * @date 2021年2月2日
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真意见")
    public static class SimulationOpinion extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7093657616365132595L;
        @ApiModelProperty("唯一标识")
        private String id;
        // 环节ID
        @ApiModelProperty("环节ID")
        private String taskId;
        // 环节办理人名称
        @ApiModelProperty("环节办理人名称")
        private String taskUserName;
        // 环节办理人ID
        @ApiModelProperty("环节办理人ID")
        private String taskUserId;
        // 办理意见内容
        @ApiModelProperty("办理意见内容")
        private String opinionText;
        // 意见立场值
        @ApiModelProperty("意见立场值")
        private String opinionValue;
        // 意见立场名称
        @ApiModelProperty("意见立场名称")
        private String opinionLabel;
        @ApiModelProperty("意见附件")
        private List<LogicFileInfo> opinionFiles;

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
         * @return the taskId
         */
        public String getTaskId() {
            return taskId;
        }

        /**
         * @param taskId 要设置的taskId
         */
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        /**
         * @return the taskUserName
         */
        public String getTaskUserName() {
            return taskUserName;
        }

        /**
         * @param taskUserName 要设置的taskUserName
         */
        public void setTaskUserName(String taskUserName) {
            this.taskUserName = taskUserName;
        }

        /**
         * @return the taskUserId
         */
        public String getTaskUserId() {
            return taskUserId;
        }

        /**
         * @param taskUserId 要设置的taskUserId
         */
        public void setTaskUserId(String taskUserId) {
            this.taskUserId = taskUserId;
        }

        /**
         * @return the opinionText
         */
        public String getOpinionText() {
            return opinionText;
        }

        /**
         * @param opinionText 要设置的opinionText
         */
        public void setOpinionText(String opinionText) {
            this.opinionText = opinionText;
        }

        /**
         * @return the opinionValue
         */
        public String getOpinionValue() {
            return opinionValue;
        }

        /**
         * @param opinionValue 要设置的opinionValue
         */
        public void setOpinionValue(String opinionValue) {
            this.opinionValue = opinionValue;
        }

        /**
         * @return the opinionLabel
         */
        public String getOpinionLabel() {
            return opinionLabel;
        }

        /**
         * @param opinionLabel 要设置的opinionLabel
         */
        public void setOpinionLabel(String opinionLabel) {
            this.opinionLabel = opinionLabel;
        }

        /**
         * @return the opinionFiles
         */
        public List<LogicFileInfo> getOpinionFiles() {
            return opinionFiles;
        }

        /**
         * @param opinionFiles 要设置的opinionFiles
         */
        public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
            this.opinionFiles = opinionFiles;
        }
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真环节")
    public static class SimulationTask extends BaseObject {

        private static final long serialVersionUID = 2216106874383637422L;

        @ApiModelProperty("环节名称")
        private String name;

        @ApiModelProperty("环节ID")
        private String id;

        @ApiModelProperty("环节类型")
        private String type;

        @ApiModelProperty("仿真办理人ID")
        private String simulationTaskUserId;

        @ApiModelProperty("仿真办理人名称")
        private String simulationTaskUserName;

        @ApiModelProperty("仿真决策人ID")
        private String simulationDecisionMakerId;

        @ApiModelProperty("仿真决策人名称")
        private String simulationDecisionMakerName;

        @ApiModelProperty("环节办理意见")
        private List<SimulationOpinion> opinions;

        @ApiModelProperty("环节字段设值")
        private List<SimulationFormField> formFields;

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
         * @return the simulationTaskUserId
         */
        public String getSimulationTaskUserId() {
            return simulationTaskUserId;
        }

        /**
         * @param simulationTaskUserId 要设置的simulationTaskUserId
         */
        public void setSimulationTaskUserId(String simulationTaskUserId) {
            this.simulationTaskUserId = simulationTaskUserId;
        }

        /**
         * @return the simulationTaskUserName
         */
        public String getSimulationTaskUserName() {
            return simulationTaskUserName;
        }

        /**
         * @param simulationTaskUserName 要设置的simulationTaskUserName
         */
        public void setSimulationTaskUserName(String simulationTaskUserName) {
            this.simulationTaskUserName = simulationTaskUserName;
        }

        /**
         * @return the simulationDecisionMakerId
         */
        public String getSimulationDecisionMakerId() {
            return simulationDecisionMakerId;
        }

        /**
         * @param simulationDecisionMakerId 要设置的simulationDecisionMakerId
         */
        public void setSimulationDecisionMakerId(String simulationDecisionMakerId) {
            this.simulationDecisionMakerId = simulationDecisionMakerId;
        }

        /**
         * @return the simulationDecisionMakerName
         */
        public String getSimulationDecisionMakerName() {
            return simulationDecisionMakerName;
        }

        /**
         * @param simulationDecisionMakerName 要设置的simulationDecisionMakerName
         */
        public void setSimulationDecisionMakerName(String simulationDecisionMakerName) {
            this.simulationDecisionMakerName = simulationDecisionMakerName;
        }

        /**
         * @return the opinions
         */
        public List<SimulationOpinion> getOpinions() {
            return opinions;
        }

        /**
         * @param opinions 要设置的opinions
         */
        public void setOpinions(List<SimulationOpinion> opinions) {
            this.opinions = opinions;
        }

        /**
         * @return the formFields
         */
        public List<SimulationFormField> getFormFields() {
            return formFields;
        }

        /**
         * @param formFields 要设置的formFields
         */
        public void setFormFields(List<SimulationFormField> formFields) {
            this.formFields = formFields;
        }
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真字段")
    public static class SimulationFormField extends BaseObject {

        private static final long serialVersionUID = -9021497913669197132L;

        @ApiModelProperty("唯一标识")
        private String id;

        @ApiModelProperty("字段名称")
        private String name;

        @ApiModelProperty("字段值")
        private Object value;

        @ApiModelProperty("字段类型")
        private String type;

        @ApiModelProperty("设值条件")
        private SimulationFormFieldConditionConfig conditionConfig;

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
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(Object value) {
            this.value = value;
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
         * @return the conditionConfig
         */
        public SimulationFormFieldConditionConfig getConditionConfig() {
            return conditionConfig;
        }

        /**
         * @param conditionConfig 要设置的conditionConfig
         */
        public void setConditionConfig(SimulationFormFieldConditionConfig conditionConfig) {
            this.conditionConfig = conditionConfig;
        }

    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真字段条件配置")
    public static class SimulationFormFieldConditionConfig extends BaseObject {

        private static final long serialVersionUID = -1603866872010587669L;

        public static final String MATCH_ALL = "all";

        @ApiModelProperty("匹配方式，all满足全部条件、any满足任一条件")
        private String match;

        @ApiModelProperty("条件列表")
        private List<SimulationFormFieldCondition> conditions;

        /**
         * @return the match
         */
        public String getMatch() {
            return match;
        }

        /**
         * @param match 要设置的match
         */
        public void setMatch(String match) {
            this.match = match;
        }

        /**
         * @return the conditions
         */
        public List<SimulationFormFieldCondition> getConditions() {
            return conditions;
        }

        /**
         * @param conditions 要设置的conditions
         */
        public void setConditions(List<SimulationFormFieldCondition> conditions) {
            this.conditions = conditions;
        }
    }

    /**
     *
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ApiModel("仿真字段条件")
    public static class SimulationFormFieldCondition extends BaseObject {

        private static final long serialVersionUID = -6381030044220152202L;

        @ApiModelProperty("条件代码，字段名或其他定义的名称")
        private String code;

        @ApiModelProperty("操作符")
        private String operator;

        @ApiModelProperty("条件值")
        private String value;

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
         * @return the operator
         */
        public String getOperator() {
            return operator;
        }

        /**
         * @param operator 要设置的operator
         */
        public void setOperator(String operator) {
            this.operator = operator;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

}
