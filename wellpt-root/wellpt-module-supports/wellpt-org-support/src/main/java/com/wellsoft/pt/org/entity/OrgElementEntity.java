package com.wellsoft.pt.org.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 组织单元实例
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT")
@DynamicUpdate
@DynamicInsert
public class OrgElementEntity extends SysEntity {


    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgVersionUuid;
    private String orgVersionId;
    private String id;
    private OrgVersionEntity.State state;
    private String type; // 等于组织单元模型ID
    private String name;
    private String shortName;
    private String code;
    private Long parentUuid;
    private String remark;
    private Integer seq;// 序号
    private String sourceId;

    private String localName;
    private String localShortName;

    private OrgElementPathEntity pathEntity;

    private List<OrgElementEntity> children = Lists.newArrayList();


    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getOrgVersionId() {
        return orgVersionId;
    }

    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrgVersionEntity.State getState() {
        return state;
    }

    public void setState(OrgVersionEntity.State state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        return this.seq;
    }

    public void setSeq(final Integer seq) {
        this.seq = seq;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Transient
    public OrgElementPathEntity getPathEntity() {
        return pathEntity;
    }

    public void setPathEntity(OrgElementPathEntity pathEntity) {
        this.pathEntity = pathEntity;
    }

    @Transient
    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Transient
    public String getLocalShortName() {
        return localShortName;
    }

    public void setLocalShortName(String localShortName) {
        this.localShortName = localShortName;
    }

    @Transient
    public List<OrgElementEntity> getChildren() {
        return children;
    }

    public void setChildren(List<OrgElementEntity> children) {
        this.children = children;
    }
}
