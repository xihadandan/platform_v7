package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:文档交换-通讯录单位
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */
@Table(name = "DMS_DOC_EXC_CONTACT_BOOK_UNIT")
@DynamicUpdate
@DynamicInsert
@Entity
public class DmsDocExcContactBookUnitEntity extends TenantEntity {
    private static final long serialVersionUID = 6559194528732501165L;


    private String unitId;

    private String unitName;//简称

    private String fullUnitName;//全称

    private String unitCode;

    private String moduleId;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getFullUnitName() {
        return fullUnitName;
    }

    public void setFullUnitName(String fullUnitName) {
        this.fullUnitName = fullUnitName;
    }
}
