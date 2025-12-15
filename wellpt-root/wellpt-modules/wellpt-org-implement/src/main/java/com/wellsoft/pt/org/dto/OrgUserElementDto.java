package com.wellsoft.pt.org.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月02日   chenq	 Create
 * </pre>
 */
public class OrgUserElementDto implements Serializable {

    private List<String> userIds;
    private String orgElementId;
    private Long orgVersionUuid;
    private String jobId;
    private List<String> directReporter;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public List<String> getDirectReporter() {
        return directReporter;
    }

    public void setDirectReporter(List<String> directReporter) {
        this.directReporter = directReporter;
    }
}
