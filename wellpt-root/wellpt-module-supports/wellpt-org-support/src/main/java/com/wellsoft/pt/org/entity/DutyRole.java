package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_DUTY_ROLE")
@Deprecated
public class DutyRole implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3607902369594780137L;
    private DutyRoleId dutyRoleId;

    @EmbeddedId
    public DutyRoleId getDutyRoleId() {
        return dutyRoleId;
    }

    public void setDutyRoleId(DutyRoleId dutyRoleId) {
        this.dutyRoleId = dutyRoleId;
    }

}
