package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_TYPE_ROLE")
@DynamicUpdate
@DynamicInsert
public class UserTypeRoleEntity extends IdEntity {

    private String role;

    private UserTypeEnum type;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserTypeEnum getType() {
        return type;
    }

    public void setType(UserTypeEnum type) {
        this.type = type;
    }
}
