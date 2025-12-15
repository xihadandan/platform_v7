package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_DEPARTMENT_PRIVILEGE")
@Deprecated
public class DepartmentPrivilege implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7778671937046339422L;

    private DepartmentPrivilegeId departmentPrivilegeId;

    @EmbeddedId
    public DepartmentPrivilegeId getDepartmentPrivilegeId() {
        return departmentPrivilegeId;
    }

    public void setDepartmentPrivilegeId(DepartmentPrivilegeId departmentPrivilegeId) {
        this.departmentPrivilegeId = departmentPrivilegeId;
    }


}
