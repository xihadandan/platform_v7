package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_GROUP_PRIVILEGE")
@Deprecated
public class GroupPrivilege implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2185098437259550222L;
    private GroupPrivilegeId groupPrivilegeId;

    @EmbeddedId
    public GroupPrivilegeId getGroupPrivilegeId() {
        return groupPrivilegeId;
    }

    public void setGroupPrivilegeId(GroupPrivilegeId groupPrivilegeId) {
        this.groupPrivilegeId = groupPrivilegeId;
    }
}
