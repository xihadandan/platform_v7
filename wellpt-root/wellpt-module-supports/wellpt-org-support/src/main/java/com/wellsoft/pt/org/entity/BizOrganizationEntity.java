package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Description: 业务组织
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ORGANIZATION")
@DynamicUpdate
@DynamicInsert
public class BizOrganizationEntity extends SysEntity {

    private static final long serialVersionUID = -1061430642064009579L;
    private String name;
    private String id;
    private Long orgUuid;
    private Boolean neverExpire;
    private Date expireTime;
    private Boolean enable;
    private Boolean expired;// 是否失效
    private String remark;
    private String orgName;
    //    private Date syncOrgTime; // 同步时间
    private Boolean syncLocked;

    private String localName;

    private List<OrgElementI18nEntity> i18ns;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Boolean getNeverExpire() {
        return neverExpire;
    }

    public void setNeverExpire(Boolean neverExpire) {
        this.neverExpire = neverExpire;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Long getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }


    @Transient
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Boolean getSyncLocked() {
        return syncLocked;
    }

    public void setSyncLocked(Boolean syncLocked) {
        this.syncLocked = syncLocked;
    }

    @Transient
    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }

    @Transient
    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
