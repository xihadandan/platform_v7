package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.OrgElementI18nEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月29日   chenq	 Create
 * </pre>
 */
public class BizOrgElementDto implements Serializable {
    private static final long serialVersionUID = 2935628856241997026L;

    private Long uuid;
    private Long bizOrgUuid;
    private String id;
    private String orgElementId;
    private String elementType;
    private Boolean isDimension;
    private String name;
    private Long parentUuid;
    private String remark;
    private Integer seq;

    private List<OrgElementI18nEntity> i18ns;

    private List<String> roleUuids;

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

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public Boolean getIsDimension() {
        return isDimension;
    }

    public void setIsDimension(Boolean dimension) {
        isDimension = dimension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(Long parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public List<String> getRoleUuids() {
        return roleUuids;
    }

    public void setRoleUuids(List<String> roleUuids) {
        this.roleUuids = roleUuids;
    }

    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
