package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 手写签批提交对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/6.1	    zenghw		2021/9/6		    Create
 * </pre>
 * @date 2021/9/6
 */
@ApiModel("手写签批提交对象")
public class SubmitFlowDto implements Serializable {

    private static final long serialVersionUID = 7719630605874776210L;
    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;
    @ApiModelProperty("环节实例uuid")
    private String taskInstUuid;
    @ApiModelProperty("附件uuid集合")
    private List<String> fileUuids;
    @ApiModelProperty("新的手写签批附件fileuuid集合")
    private List<String> inspectionFileUuids;
    @ApiModelProperty("签批日志 以json串字符串结构")
    private String inspectionLog;
    @ApiModelProperty("签署意见")
    private String signOpinion;
    @ApiModelProperty("意见立场-显示值")
    private String opinionPositionLable;
    @ApiModelProperty("意见立场-值")
    private String opinionPositionValue;

    public String getFlowInstUuid() {
        return this.flowInstUuid;
    }

    public void setFlowInstUuid(final String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    public String getTaskInstUuid() {
        return this.taskInstUuid;
    }

    public void setTaskInstUuid(final String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    public List<String> getFileUuids() {
        return this.fileUuids;
    }

    public void setFileUuids(final List<String> fileUuids) {
        this.fileUuids = fileUuids;
    }

    public List<String> getInspectionFileUuids() {
        return this.inspectionFileUuids;
    }

    public void setInspectionFileUuids(final List<String> inspectionFileUuids) {
        this.inspectionFileUuids = inspectionFileUuids;
    }

    public String getInspectionLog() {
        return this.inspectionLog;
    }

    public void setInspectionLog(final String inspectionLog) {
        this.inspectionLog = inspectionLog;
    }

    public String getSignOpinion() {
        return this.signOpinion;
    }

    public void setSignOpinion(final String signOpinion) {
        this.signOpinion = signOpinion;
    }

    public String getOpinionPositionLable() {
        return this.opinionPositionLable;
    }

    public void setOpinionPositionLable(final String opinionPositionLable) {
        this.opinionPositionLable = opinionPositionLable;
    }

    public String getOpinionPositionValue() {
        return this.opinionPositionValue;
    }

    public void setOpinionPositionValue(final String opinionPositionValue) {
        this.opinionPositionValue = opinionPositionValue;
    }
}
