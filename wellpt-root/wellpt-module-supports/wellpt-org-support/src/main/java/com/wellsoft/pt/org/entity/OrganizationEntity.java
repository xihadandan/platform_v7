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
 * Description: 组织
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ORGANIZATION")
@DynamicUpdate
@DynamicInsert
public class OrganizationEntity extends SysEntity {


    private static final long serialVersionUID = -4995611018990401610L;
    private String name;
    private String id;
    private String code;
    private Boolean isDefault; // 是否默认组织
    private Boolean neverExpire;
    private Date expireTime;
    private Boolean enable;
    private Boolean expired;// 是否失效
    private String manager;// 组织管理员
    private String publishFlowUuid;// 发布流程
    private String remark;

    private List<BizOrganizationEntity> bizOrgs;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getPublishFlowUuid() {
        return this.publishFlowUuid;
    }

    public void setPublishFlowUuid(final String publishFlowUuid) {
        this.publishFlowUuid = publishFlowUuid;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    @Transient
    public List<BizOrganizationEntity> getBizOrgs() {
        return bizOrgs;
    }

    public void setBizOrgs(List<BizOrganizationEntity> bizOrgs) {
        this.bizOrgs = bizOrgs;
    }

    @Transient
    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
