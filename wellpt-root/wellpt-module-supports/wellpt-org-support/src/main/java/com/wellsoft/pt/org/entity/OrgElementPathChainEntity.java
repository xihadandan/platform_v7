package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元实例路径链
 * <p>
 * 记录组织单元在整个组织树的向下关联的所有节点。快速查下父节点下所有子节点
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_PATH_CHAIN")
@DynamicUpdate
@DynamicInsert
public class OrgElementPathChainEntity extends SysEntity {


    private static final long serialVersionUID = -7872097847018963293L;
    private String orgElementId; // 上级组织单元ID
    private String orgElementType;// 上级组单元类型
    private Long orgVersionUuid;
    private String subOrgElementId;// 下级组织单元ID
    private String subOrgElementType; // 下级组织单元类型
    private Integer level;

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public String getOrgElementType() {
        return orgElementType;
    }

    public void setOrgElementType(String orgElementType) {
        this.orgElementType = orgElementType;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getSubOrgElementId() {
        return this.subOrgElementId;
    }

    public void setSubOrgElementId(final String subOrgElementId) {
        this.subOrgElementId = subOrgElementId;
    }

    public String getSubOrgElementType() {
        return this.subOrgElementType;
    }

    public void setSubOrgElementType(final String subOrgElementType) {
        this.subOrgElementType = subOrgElementType;
    }

    @Column(name = "\"LEVEL\"")
    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }
}
