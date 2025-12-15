package com.wellsoft.pt.org.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年01月12日   chenq	 Create
 * </pre>
 */
public class OrgUserJobDto implements Serializable {
    private String jobId;

    private String jobIdPath;

    private String jobName;

    private String jobNamePath;

    private boolean primary; // 主职

    private Long orgVersionUuid;// 归属版本

    private Long bizOrgUuid;// 归属业务组织

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public String getJobIdPath() {
        return this.jobIdPath;
    }

    public void setJobIdPath(final String jobIdPath) {
        this.jobIdPath = jobIdPath;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    public String getJobNamePath() {
        return this.jobNamePath;
    }

    public void setJobNamePath(final String jobNamePath) {
        this.jobNamePath = jobNamePath;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    /**
     * @return the bizOrgUuid
     */
    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    /**
     * @param bizOrgUuid 要设置的bizOrgUuid
     */
    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgUserJobDto that = (OrgUserJobDto) o;
        return Objects.equals(jobId, that.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }
}
