package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * 职务体系视图职务职级信息
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/21   Create
 * </pre>
 */
public class OrgDutyBodyDto {

    @ApiModelProperty("职等")
    private String jobGrade;

    @ApiModelProperty("职务序列uuid")
    private String dutySeqUuid;

    @ApiModelProperty("职级名称")
    private String jobRankName;

    @ApiModelProperty("职务名称")
    private String dutyName;

    public OrgDutyBodyDto(String jobGrade, String dutySeqUuid) {
        this.jobGrade = jobGrade;
        this.dutySeqUuid = dutySeqUuid;
    }

    public String getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(String jobGrade) {
        this.jobGrade = jobGrade;
    }

    public String getDutySeqUuid() {
        return dutySeqUuid;
    }

    public void setDutySeqUuid(String dutySeqUuid) {
        this.dutySeqUuid = dutySeqUuid;
    }

    public String getJobRankName() {
        return jobRankName;
    }

    public void setJobRankName(String jobRankName) {
        this.jobRankName = jobRankName;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }
}
