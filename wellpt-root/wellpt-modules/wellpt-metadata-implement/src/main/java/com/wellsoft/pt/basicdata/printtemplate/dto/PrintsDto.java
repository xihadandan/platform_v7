package com.wellsoft.pt.basicdata.printtemplate.dto;

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
 * 2022/3/9.1	    zenghw		2022/3/9		    Create
 * </pre>
 * @date 2022/3/9
 */
@ApiModel("套打输入对象")
public class PrintsDto {

    @ApiModelProperty("打印模板Uuid")
    private String printTemplateUuId;
    @ApiModelProperty("dataUuids")
    private List<String> dataUuids;
    @ApiModelProperty("formUuid 没有传空字符串，不可传Null")
    private List<String> formUuids;
    @ApiModelProperty("contentType集合")
    private List<String> contentTypes;

    public List<String> getContentTypes() {
        return this.contentTypes;
    }

    public void setContentTypes(final List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public String getPrintTemplateUuId() {
        return this.printTemplateUuId;
    }

    public void setPrintTemplateUuId(final String printTemplateUuId) {
        this.printTemplateUuId = printTemplateUuId;
    }

    public List<String> getDataUuids() {
        return this.dataUuids;
    }

    public void setDataUuids(final List<String> dataUuids) {
        this.dataUuids = dataUuids;
    }

    public List<String> getFormUuids() {
        return this.formUuids;
    }

    public void setFormUuids(final List<String> formUuids) {
        this.formUuids = formUuids;
    }
}
