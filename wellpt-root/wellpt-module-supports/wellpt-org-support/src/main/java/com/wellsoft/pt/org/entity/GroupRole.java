package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_GROUP_ROLE")
@Deprecated
public class GroupRole implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2302447621743103949L;

    private GroupRoleId groupRoleId;

    @EmbeddedId
    public GroupRoleId getGroupRoleId() {
        return groupRoleId;
    }

    public void setGroupRoleId(GroupRoleId groupRoleId) {
        this.groupRoleId = groupRoleId;
    }

}
