package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 业务组织节点实例
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ELEMENT")
@DynamicUpdate
@DynamicInsert
public class BizOrgElementEntity extends SysEntity {


    private static final long serialVersionUID = -5597563431400156709L;

    private Long bizOrgUuid;
    private String id;
    private String orgElementId;
    private String elementType;
    private Boolean isDimension;
    private String name;
    private Long parentUuid;
    private Long parentDimensionUuid;
    private String remark;
    private Integer seq;
    private Boolean enabled;

    private BizOrgElementPathEntity pathEntity;


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

    public void setIsDimension(Boolean dimensionElement) {
        this.isDimension = dimensionElement;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getParentDimensionUuid() {
        return parentDimensionUuid;
    }

    public void setParentDimensionUuid(Long parentDimensionUuid) {
        this.parentDimensionUuid = parentDimensionUuid;
    }

    @Transient
    public BizOrgElementPathEntity getPathEntity() {
        return pathEntity;
    }

    public void setPathEntity(BizOrgElementPathEntity pathEntity) {
        this.pathEntity = pathEntity;
    }
}
