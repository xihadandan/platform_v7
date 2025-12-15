package com.wellsoft.pt.handover.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/30.1	    zenghw		2022/3/30		    Create
 * </pre>
 * @date 2022/3/30
 */
@ApiModel(value = "工作交接流程数据量记录")
public class WhFlowDatasRecordDto {
    @ApiModelProperty("待办流程数量")
    private Integer todoCount;
    @ApiModelProperty("查阅流程数量")
    private Integer consultCount;
    @ApiModelProperty("监控流程数量")
    private Integer monitorCount;
    @ApiModelProperty("工作交接UUID 未创建时为空")
    private String whWorkHandoverUuid;
    @ApiModelProperty("已办流程数量")
    private Integer doneCount;
    @ApiModelProperty("督办流程数量")
    private Integer superviseCount;

    public Integer getTodoCount() {
        return this.todoCount;
    }

    public void setTodoCount(final Integer todoCount) {
        this.todoCount = todoCount;
    }

    public Integer getConsultCount() {
        return this.consultCount;
    }

    public void setConsultCount(final Integer consultCount) {
        this.consultCount = consultCount;
    }

    public Integer getMonitorCount() {
        return this.monitorCount;
    }

    public void setMonitorCount(final Integer monitorCount) {
        this.monitorCount = monitorCount;
    }

    public String getWhWorkHandoverUuid() {
        return this.whWorkHandoverUuid;
    }

    public void setWhWorkHandoverUuid(final String whWorkHandoverUuid) {
        this.whWorkHandoverUuid = whWorkHandoverUuid;
    }

    public Integer getDoneCount() {
        return this.doneCount;
    }

    public void setDoneCount(final Integer doneCount) {
        this.doneCount = doneCount;
    }

    public Integer getSuperviseCount() {
        return this.superviseCount;
    }

    public void setSuperviseCount(final Integer superviseCount) {
        this.superviseCount = superviseCount;
    }
}
