package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 * 职等实体
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@ApiModel("职等")
@Entity
@Table(name = "ORG_JOB_GRADE")
public class OrgJobGradeEntity extends TenantEntity {

    @ApiModelProperty("职等")
    private Integer jobGrade;

    @ApiModelProperty("职等名称")
    private String jobGradeName;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("是否有效: 1:有效，0：无效")
    private Integer isValid;

    @ApiModelProperty("是否有效: 1:异常，0：无异常")
    private Integer isException;

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

    @Column(name = "\"DESCRIBE\"")
    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getIsException() {
        return isException;
    }

    public void setIsException(Integer isException) {
        this.isException = isException;
    }
}
