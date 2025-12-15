package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元实例扩展属性
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_EXT_ATTR")
@DynamicUpdate
@DynamicInsert
public class OrgElementExtAttrEntity extends SysEntity {

    private static final long serialVersionUID = 8192442904709529267L;
    private Long orgElementUuid;
    private Long orgVersionUuid;
    private String orgElementId;
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

    public Long getOrgElementUuid() {
        return orgElementUuid;
    }

    public void setOrgElementUuid(Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getOrgElementId() {
        return this.orgElementId;
    }

    public void setOrgElementId(final String orgElementId) {
        this.orgElementId = orgElementId;
    }
}
