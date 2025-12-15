package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 业务组织角色
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ROLE")
@DynamicUpdate
@DynamicInsert
public class BizOrgRoleEntity extends SysEntity {

    private static final long serialVersionUID = 5729585346027639965L;
    private Long bizOrgUuid;
    private String id;
    private String name;
    private String applyTo;
    private Integer seq;
    private String remark;
    private String allowMemberType;
    private Boolean multipleSelectMember;
    private List<OrgElementI18nEntity> i18ns;
    private String localName;

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAllowMemberType() {
        return allowMemberType;
    }

    public void setAllowMemberType(String allowMemberType) {
        this.allowMemberType = allowMemberType;
    }

    public Boolean getMultipleSelectMember() {
        return multipleSelectMember;
    }

    public void setMultipleSelectMember(Boolean multipleSelectMember) {
        this.multipleSelectMember = multipleSelectMember;
    }

    @Transient
    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }

    public static enum ApplyTo {
        BIZ_DIMENSION_ELEMENT, ORG_ELEMENT;
    }

    @Transient
    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
