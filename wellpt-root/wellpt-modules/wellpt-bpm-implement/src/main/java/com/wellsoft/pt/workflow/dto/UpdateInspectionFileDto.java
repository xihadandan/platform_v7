package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

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
@ApiModel("更新手写签批附件对象")
public class UpdateInspectionFileDto implements Serializable {

    private static final long serialVersionUID = 7719630605874776211L;
    @ApiModelProperty("附件uuid")
    private String fileUuid;

    @ApiModelProperty("新的手写签批附件-base64编码字符串")
    private String inspectionFile;

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public String getInspectionFile() {
        return inspectionFile;
    }

    public void setInspectionFile(String inspectionFile) {
        this.inspectionFile = inspectionFile;
    }
}
