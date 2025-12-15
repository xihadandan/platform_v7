package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 单位扩展属性
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_UNIT_EXT_ATTR")
@DynamicUpdate
@DynamicInsert
public class OrgUnitExtAttrEntity extends SysEntity {

    private static final long serialVersionUID = 8192442904709529267L;
    private String orgUnitId;
    private Long orgUnitUuid;
    private String attrName;// 属性名称
    private String attrKey;//属性key
    private String attrVal;//属性值

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrVal() {
        return attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public void setOrgUnitId(String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public Long getOrgUnitUuid() {
        return this.orgUnitUuid;
    }

    public void setOrgUnitUuid(Long orgUnitUuid) {
        this.orgUnitUuid = orgUnitUuid;
    }

    @Transient
    public String getAttrName() {
        return this.attrName;
    }

    public void setAttrName(final String attrName) {
        this.attrName = attrName;
    }
}
