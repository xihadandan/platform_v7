package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import com.wellsoft.pt.org.dto.OrgJobDutyDto;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 用户添加到组织内产生的数据实体
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_USER")
@DynamicUpdate
@DynamicInsert
public class OrgUserEntity extends SysEntity {


    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgUuid;
    private Long orgVersionUuid;
    private String orgUserId; // 组织用户ID
    private String userId;
    private String userPath; // 用户路径：挂载在组织元素下，形成的唯一路径
    private String orgElementId; //  组织元素
    private String orgElementType;//  组织元素类型
    private Type type; // 用户类型


    private OrgElementEntity orgElement;
    private OrgElementPathEntity orgElementPath;

    private OrgJobDutyDto jobDuty;

    public Long getOrgUuid() {
        return this.orgUuid;
    }

    public void setOrgUuid(final Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserPath() {
        return this.userPath;
    }

    public void setUserPath(final String userPath) {
        this.userPath = userPath;
    }

    public String getOrgElementId() {
        return this.orgElementId;
    }

    public void setOrgElementId(final String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public String getOrgElementType() {
        return this.orgElementType;
    }

    public void setOrgElementType(final String orgElementType) {
        this.orgElementType = orgElementType;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getOrgUserId() {
        return this.orgUserId;
    }

    public void setOrgUserId(final String orgUserId) {
        this.orgUserId = orgUserId;
    }

    public static enum Type {
        MEMBER_USER /* 成员用户 */, PRIMARY_JOB_USER /* 主职 */, SECONDARY_JOB_USER /* 副职*/
    }

    @Transient
    public OrgElementEntity getOrgElement() {
        return orgElement;
    }

    public void setOrgElement(OrgElementEntity orgElement) {
        this.orgElement = orgElement;
    }

    @Transient
    public OrgElementPathEntity getOrgElementPath() {
        return orgElementPath;
    }

    public void setOrgElementPath(OrgElementPathEntity orgElementPath) {
        this.orgElementPath = orgElementPath;
    }

    @Transient
    public OrgJobDutyDto getJobDuty() {
        return jobDuty;
    }

    public void setJobDuty(OrgJobDutyDto jobDuty) {
        this.jobDuty = jobDuty;
    }
}
