package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 手写签批附件列表 输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/23.1	    zenghw		2021/8/23		    Create
 * </pre>
 * @date 2021/8/23
 */
@ApiModel("手写签批附件列表输出对象")
public class GetInspectionFileListDto implements Serializable {
    private static final long serialVersionUID = 7719630605874776242L;

    private List<GetInspectionFileDto> getInspectionFileDtos;
    @ApiModelProperty("附件字段编码")
    private String fieldCode;

    public List<GetInspectionFileDto> getGetInspectionFileDtos() {
        return this.getInspectionFileDtos;
    }

    public void setGetInspectionFileDtos(final List<GetInspectionFileDto> getInspectionFileDtos) {
        this.getInspectionFileDtos = getInspectionFileDtos;
    }

    public String getFieldCode() {
        return this.fieldCode;
    }

    public void setFieldCode(final String fieldCode) {
        this.fieldCode = fieldCode;
    }
}
