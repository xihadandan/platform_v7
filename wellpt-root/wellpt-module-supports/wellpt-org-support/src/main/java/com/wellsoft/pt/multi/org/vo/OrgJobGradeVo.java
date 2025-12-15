package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * 职等请求数据
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@ApiModel("职等")
public class OrgJobGradeVo {

    @ApiModelProperty("职等")
    private Integer jobGrade;

    @ApiModelProperty("职等名称")
    private String jobGradeName;

    @ApiModelProperty("描述")
    private String describe;

    public Integer getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(Integer jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getJobGradeName() {
        return jobGradeName;
    }

    public void setJobGradeName(String jobGradeName) {
        this.jobGradeName = jobGradeName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
