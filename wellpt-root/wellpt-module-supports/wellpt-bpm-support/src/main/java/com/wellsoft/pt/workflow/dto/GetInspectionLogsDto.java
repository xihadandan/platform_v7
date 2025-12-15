package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 获取手写签批记录对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/9.1	    zenghw		2021/9/9		    Create
 * </pre>
 * @date 2021/9/9
 */
@ApiModel("获取手写签批记录对象")
public class GetInspectionLogsDto implements Serializable {

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("原附件uuid")
    private String oldFileUuid;

    @ApiModelProperty("签批记录日志")
    private String inspectionLog;

    public String getOldFileUuid() {
        return this.oldFileUuid;
    }

    public void setOldFileUuid(final String oldFileUuid) {
        this.oldFileUuid = oldFileUuid;
    }

    public String getFieldCode() {
        return this.fieldCode;
    }

    public void setFieldCode(final String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getInspectionLog() {
        return this.inspectionLog;
    }

    public void setInspectionLog(final String inspectionLog) {
        this.inspectionLog = inspectionLog;
    }
}
