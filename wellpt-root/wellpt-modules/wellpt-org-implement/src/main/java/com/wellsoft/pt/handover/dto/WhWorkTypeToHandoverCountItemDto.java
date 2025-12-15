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
 * 2022/3/28.1	    zenghw		2022/3/28		    Create
 * </pre>
 * @date 2022/3/28
 */
@ApiModel(value = "工作类型对应的交接内容")
public class WhWorkTypeToHandoverCountItemDto {

    // 是否含已办结流程 0代表不含 1代表含
    @ApiModelProperty("是否含已办结流程 0代表不含 1代表含")
    private Integer completedFlowFlag;
    // 交接内容类型
    @ApiModelProperty("交接内容类型  待办流程:todo\n" + "\t// ;查阅流程:consult;监控流程:monitor;已办流程:done;督办流程:supervise;")
    private String handoverContentType;

    // 交接内容类型显示值
    @ApiModelProperty("交接内容类型显示值")
    private String handoverContentTypeName;

    public String getHandoverContentTypeName() {
        return this.handoverContentTypeName;
    }

    public void setHandoverContentTypeName(final String handoverContentTypeName) {
        this.handoverContentTypeName = handoverContentTypeName;
    }

    public Integer getCompletedFlowFlag() {
        return this.completedFlowFlag;
    }

    public void setCompletedFlowFlag(final Integer completedFlowFlag) {
        this.completedFlowFlag = completedFlowFlag;
    }

    public String getHandoverContentType() {
        return this.handoverContentType;
    }

    public void setHandoverContentType(final String handoverContentType) {
        this.handoverContentType = handoverContentType;
    }
}
