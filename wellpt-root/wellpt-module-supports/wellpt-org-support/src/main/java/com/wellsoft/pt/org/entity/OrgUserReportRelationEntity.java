package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织用户汇报关系
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_USER_REPORT_RELATION")
@DynamicUpdate
@DynamicInsert
public class OrgUserReportRelationEntity extends SysEntity {

    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgVersionUuid;
    private String orgElementId;
    private String userId;
    private String reportToUserId;

    public OrgUserReportRelationEntity() {
    }

    public OrgUserReportRelationEntity(Long orgVersionUuid, String orgElementId, String userId, String reportToUserId) {
        this.orgVersionUuid = orgVersionUuid;
        this.orgElementId = orgElementId;
        this.userId = userId;
        this.reportToUserId = reportToUserId;
    }

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReportToUserId() {
        return reportToUserId;
    }

    public void setReportToUserId(String reportToUserId) {
        this.reportToUserId = reportToUserId;
    }

    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }
}
