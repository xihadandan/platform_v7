package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Description: 组织版本
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_VERSION")
@DynamicUpdate
@DynamicInsert
public class OrgVersionEntity extends SysEntity {

    private static final long serialVersionUID = -8993160483282565609L;
    private String id;
    private Long orgUuid;
    private State state;
    private Float ver;
    private Long sourceUuid;
    private Date publishTime;
    private Approve approve; // 审批状态
    private Date invalidTime; // 失效时间
    private Date effectTime; // 生效时间

    private OrganizationEntity organization;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOrgUuid() {
        return this.orgUuid;
    }

    public void setOrgUuid(final Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Float getVer() {
        return ver;
    }

    public void setVer(Float ver) {
        this.ver = ver;
    }

    public Long getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(Long sourceUuid) {
        this.sourceUuid = sourceUuid;
    }


    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getInvalidTime() {
        return this.invalidTime;
    }

    public void setInvalidTime(final Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public Date getEffectTime() {
        return this.effectTime;
    }

    public void setEffectTime(final Date effectTime) {
        this.effectTime = effectTime;
    }

    public Approve getApprove() {
        return this.approve;
    }

    public void setApprove(final Approve approve) {
        this.approve = approve;
    }

    @Transient
    public OrganizationEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationEntity organization) {
        this.organization = organization;
    }

    public enum State {
        PUBLISHED /* 正式版 */, DESIGNING /* 设计版 */, HISTORY /* 历史版 */
    }

    public enum Approve {
        ING /* 审批中 */, PASSED /* 审批通过 */, DENY /* 审批拒绝 */;
    }

}
