package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务组织节点路径链
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ELEMENT_PATH_CHAIN")
@DynamicUpdate
@DynamicInsert
public class BizOrgElementPathChainEntity extends SysEntity {

    private String id;
    private String subId;
    private String elementType;
    private String subElementType;
    private Integer level;
    private Long bizOrgUuid;

    public BizOrgElementPathChainEntity() {
    }

    public BizOrgElementPathChainEntity(String id, String subId, String elementType, String subElementType, Integer level) {
        this.id = id;
        this.subId = subId;
        this.elementType = elementType;
        this.subElementType = subElementType;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getSubElementType() {
        return subElementType;
    }

    public void setSubElementType(String subElementType) {
        this.subElementType = subElementType;
    }

    @Column(name = "\"LEVEL\"")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }
}
