package com.wellsoft.pt.ei.dto.flow.exportNew;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;

public class TaskActivityExportData implements Serializable {

    // UUID
    @FieldType(desc = "UUID，系统字段", type = ExportFieldTypeEnum.STRING)
    protected String uuid;
    /**
     * 所在任务实例ID
     */
    @FieldType(desc = "所在任务实例ID", type = ExportFieldTypeEnum.STRING)
    private String taskId;
    /**
     * 所在任务实例
     */
    @FieldType(desc = "所在任务实例uuid", type = ExportFieldTypeEnum.STRING)
    private String taskInstUuid;
    /**
     * 前一任务实例ID
     */
    @FieldType(desc = "前一任务实例ID", type = ExportFieldTypeEnum.STRING)
    private String preTaskId;
    /**
     * 前一任务实例
     */
    @FieldType(desc = "前一任务实例uuid", type = ExportFieldTypeEnum.STRING)
    private String preTaskInstUuid;
    /**
     * 所在流程实例
     */
    @FieldType(desc = "所在流程实例uuid", type = ExportFieldTypeEnum.STRING)
    private String flowInstUuid;
    /**
     * 开始时间
     */
    @FieldType(desc = "开始时间", type = ExportFieldTypeEnum.DATE)
    private Date startTime;
    /**
     * 结束时间
     */
    @FieldType(desc = "结束时间", type = ExportFieldTypeEnum.DATE)
    private Date endTime;

    // -------------------------------------------------------
    /**
     * 任务流转类型
     */
    @FieldType(desc = "任务流转类型", type = ExportFieldTypeEnum.INTEGER)
    private Integer transferCode;
    // 版本号
    @FieldType(desc = "数据版本号", type = ExportFieldTypeEnum.INTEGER)
    private Integer recVer;

    // 创建人
    @FieldType(desc = "创建人", type = ExportFieldTypeEnum.STRING)
    private String creator;

    // 创建时间
    @FieldType(desc = "创建时间", type = ExportFieldTypeEnum.DATE)
    private Date createTime;

    // 修改人
    @FieldType(desc = "修改人", type = ExportFieldTypeEnum.STRING)
    private String modifier;

    // 修改时间
    @FieldType(desc = "修改时间", type = ExportFieldTypeEnum.DATE)
    private Date modifyTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public String getPreTaskId() {
        return preTaskId;
    }

    public void setPreTaskId(String preTaskId) {
        this.preTaskId = preTaskId;
    }

    public String getPreTaskInstUuid() {
        return preTaskInstUuid;
    }

    public void setPreTaskInstUuid(String preTaskInstUuid) {
        this.preTaskInstUuid = preTaskInstUuid;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(Integer transferCode) {
        this.transferCode = transferCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
