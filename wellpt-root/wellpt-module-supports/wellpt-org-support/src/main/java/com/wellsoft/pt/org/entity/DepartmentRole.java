package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


//@Entity
//@Table(name = "ORG_DEPARTMENT_ROLE")
@Deprecated
public class DepartmentRole implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -374249738831757487L;

    private DepartmentRoleId departmentRoleId;

    @EmbeddedId
    public DepartmentRoleId getDepartmentRoleId() {
        return departmentRoleId;
    }

    public void setDepartmentRoleId(DepartmentRoleId departmentRoleId) {
        this.departmentRoleId = departmentRoleId;
    }

}
