package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户额外角色表
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年11月04日   shenhb	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_EXTRA_ROLE_TYPE")
@DynamicUpdate
@DynamicInsert
public class UserExtraRoleTypeEntity extends IdEntity {

    private String loginName;
    private UserTypeEnum userExtraRoleType;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public UserTypeEnum getUserExtraRoleType() {
        return userExtraRoleType;
    }

    public void setUserExtraRoleType(UserTypeEnum userExtraRoleType) {
        this.userExtraRoleType = userExtraRoleType;
    }
}
