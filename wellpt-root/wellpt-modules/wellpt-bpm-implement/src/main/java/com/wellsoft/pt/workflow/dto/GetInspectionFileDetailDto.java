package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 流程签批文件详情
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/11/9.1	    zenghw		2021/11/9		    Create
 * </pre>
 * @date 2021/11/9
 */
@ApiModel("流程签批文件详情")
public class GetInspectionFileDetailDto implements Serializable {

    private static final long serialVersionUID = -1836565164131173540L;
    @ApiModelProperty("字节文件-base64编码字符串")
    private String fileBytes;
    @ApiModelProperty("文件名称")
    private String fileName;

    public String getFileBytes() {
        return this.fileBytes;
    }

    public void setFileBytes(final String fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
}
