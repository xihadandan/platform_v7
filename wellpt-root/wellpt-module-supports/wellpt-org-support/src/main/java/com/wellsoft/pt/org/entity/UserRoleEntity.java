package com.wellsoft.pt.org.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织群组
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_ROLE")
@DynamicUpdate
@DynamicInsert
public class UserRoleEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private String userUuid;
    private String roleUuid;

    public UserRoleEntity() {
    }

    public UserRoleEntity(String userUuid, String roleUuid) {
        this.userUuid = userUuid;
        this.roleUuid = roleUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }
}
