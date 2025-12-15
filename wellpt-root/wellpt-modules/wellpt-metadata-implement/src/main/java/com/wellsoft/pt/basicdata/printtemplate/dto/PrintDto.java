package com.wellsoft.pt.basicdata.printtemplate.dto;

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
 * 2022/3/9.1	    zenghw		2022/3/9		    Create
 * </pre>
 * @date 2022/3/9
 */
@ApiModel("套打输入对象")
public class PrintDto {

    @ApiModelProperty("打印模板Uuid")
    private String printTemplateUuId;
    @ApiModelProperty("dataUuid")
    private String dataUuid;
    @ApiModelProperty("formUuid")
    private String formUuid;

    public String getPrintTemplateUuId() {
        return this.printTemplateUuId;
    }

    public void setPrintTemplateUuId(final String printTemplateUuId) {
        this.printTemplateUuId = printTemplateUuId;
    }

    public String getDataUuid() {
        return this.dataUuid;
    }

    public void setDataUuid(final String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getFormUuid() {
        return this.formUuid;
    }

    public void setFormUuid(final String formUuid) {
        this.formUuid = formUuid;
    }
}
