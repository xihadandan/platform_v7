package com.wellsoft.pt.handover.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

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
@ApiModel(value = "工作交接未创建时查询交接结果接口输入参数对象")
public class GetFlowDatasRecordsDto {
    @ApiModelProperty("工作类型对应的交接内容集合")
    private List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtos;
    @ApiModelProperty("交接内容：流程定义内容 多个用;隔开")
    private String handoverContentsId;

    @ApiModelProperty("交接人UserId")
    private String handoverPersonId;

    public String getHandoverPersonId() {
        return this.handoverPersonId;
    }

    public void setHandoverPersonId(final String handoverPersonId) {
        this.handoverPersonId = handoverPersonId;
    }

    public String getHandoverContentsId() {
        return this.handoverContentsId;
    }

    public void setHandoverContentsId(final String handoverContentsId) {
        this.handoverContentsId = handoverContentsId;
    }

    public List<WhWorkTypeToHandoverCountItemDto> getWhWorkTypeToHandoverCountItemDtos() {
        return this.whWorkTypeToHandoverCountItemDtos;
    }

    public void setWhWorkTypeToHandoverCountItemDtos(
            final List<WhWorkTypeToHandoverCountItemDto> whWorkTypeToHandoverCountItemDtos) {
        this.whWorkTypeToHandoverCountItemDtos = whWorkTypeToHandoverCountItemDtos;
    }
}
