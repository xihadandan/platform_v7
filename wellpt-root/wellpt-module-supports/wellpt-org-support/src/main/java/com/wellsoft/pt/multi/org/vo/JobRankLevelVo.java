package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description:
 * 工作职位职级关系
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/27   Create
 * </pre>
 */
public class JobRankLevelVo implements Serializable {

    private static final long serialVersionUID = 5872964586555353227L;

    @ApiModelProperty("工作Id")
    private String jobId;

    @ApiModelProperty("职级ID")
    private String jobRankId;

    @ApiModelProperty("职级")
    private String jobRank;

    @ApiModelProperty("职等")
    private Integer jobGrade;

    @ApiModelProperty("职档")
    private String jobLevel;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobRank() {
        return jobRank;
    }

    public void setJobRank(String jobRank) {
        this.jobRank = jobRank;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getJobRankId() {
        return jobRankId;
    }

    public void setJobRankId(String jobRankId) {
        this.jobRankId = jobRankId;
    }

    public Integer getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(Integer jobGrade) {
        this.jobGrade = jobGrade;
    }
}
