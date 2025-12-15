package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


//@Entity
//@Table(name = "ORG_USER_PRIVILEGE")
@Deprecated
public class UserPrivilege implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8752640307606986196L;
    private UserPrivilegeId userPrivilegeId;

    @EmbeddedId
    public UserPrivilegeId getUserPrivilegeId() {
        return userPrivilegeId;
    }

    public void setUserPrivilegeId(UserPrivilegeId userPrivilegeId) {
        this.userPrivilegeId = userPrivilegeId;
    }


}
