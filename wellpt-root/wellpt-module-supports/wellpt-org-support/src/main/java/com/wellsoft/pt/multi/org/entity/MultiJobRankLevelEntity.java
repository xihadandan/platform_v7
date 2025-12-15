package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 * 职位职级职档关联关系
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/27   Create
 * </pre>
 */
@Entity
@Table(name = "MULTI_ORG_JOB_RANK_LEVEL")
public class MultiJobRankLevelEntity extends TenantEntity {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("工作Id")
    private String jobId;

    @ApiModelProperty("职级Id")
    private String jobRankId;

    @ApiModelProperty("职档")
    private String jobLevel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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
}
