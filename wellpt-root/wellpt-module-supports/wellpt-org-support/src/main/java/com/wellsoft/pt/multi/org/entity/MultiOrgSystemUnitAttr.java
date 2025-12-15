package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/18    chenq		2020/4/18		Create
 * </pre>
 */
@Entity
@Table(name = "MULTI_ORG_SYSTEM_UNIT_ATTR")
@DynamicUpdate
@DynamicInsert
public class MultiOrgSystemUnitAttr extends TenantEntity {

    private String attrCode;

    private String attrValue;

    private String remark;

    private String attrName;

    public String getAttrCode() {
        return attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
}
