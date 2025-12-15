package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_USER_ROLE")
@Deprecated
public class UserRole implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4592795475310785434L;
    private UserRoleId userRoleId;

    @EmbeddedId
    public UserRoleId getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(UserRoleId userRoleId) {
        this.userRoleId = userRoleId;
    }

}
