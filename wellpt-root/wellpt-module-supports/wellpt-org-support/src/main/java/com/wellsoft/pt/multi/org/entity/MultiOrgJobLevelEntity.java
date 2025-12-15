package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 * 职档实体
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@Entity
@Table(name = "MULTI_ORG_JOB_LEVEL")
public class MultiOrgJobLevelEntity extends TenantEntity {

    @ApiModelProperty("职级UUID")
    private String jobRankUuid;

    @ApiModelProperty("职档")
    private String jobLevel;

    @ApiModelProperty("职档序号")
    private Integer jobLevelSeq;

    public String getJobRankUuid() {
        return jobRankUuid;
    }

    public void setJobRankUuid(String jobRankUuid) {
        this.jobRankUuid = jobRankUuid;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Integer getJobLevelSeq() {
        return jobLevelSeq;
    }

    public void setJobLevelSeq(Integer jobLevelSeq) {
        this.jobLevelSeq = jobLevelSeq;
    }
}
