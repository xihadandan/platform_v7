package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;


/**
 * @author yuyq
 * createtime 2014-12-22
 * @table org_group_member
 */
//@Entity
//@Table(name = "org_group_member")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class GroupMember extends IdEntity {

    private static final long serialVersionUID = 3685052730682781954L;
    //群组
    @UnCloneable
    private Group group;
    //用户
    @UnCloneable
    private User user;

    //是否管理员
    private Boolean isAdmin;

    //是否消息提醒
    private Boolean iswarnmsg;

    //推荐人
    private String remanfid;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIswarnmsg() {
        return iswarnmsg;
    }

    public void setIswarnmsg(Boolean iswarnmsg) {
        this.iswarnmsg = iswarnmsg;
    }

    public String getRemanfid() {
        return remanfid;
    }

    public void setRemanfid(String remanfid) {
        this.remanfid = remanfid;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_uuid")
    @LazyToOne(LazyToOneOption.FALSE)
    @Fetch(FetchMode.SELECT)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    @LazyToOne(LazyToOneOption.FALSE)
    @Fetch(FetchMode.SELECT)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
