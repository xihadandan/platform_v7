package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Description:
 * 职务序列请求参数
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@ApiModel("职务序列")
public class OrgDutySeqVo {
    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("职务序列编号")
    private String dutySeqCode;

    @NotBlank
    @ApiModelProperty("职务序列名称")
    private String dutySeqName;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标背景色")
    private String backgroundColor;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("父类uuid")
    private String parentUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDutySeqCode() {
        return dutySeqCode;
    }

    public void setDutySeqCode(String dutySeqCode) {
        this.dutySeqCode = dutySeqCode;
    }

    public String getDutySeqName() {
        return dutySeqName;
    }

    public void setDutySeqName(String dutySeqName) {
        this.dutySeqName = dutySeqName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
