package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年7月3日.1	zhongzh		2020年7月3日		Create
 * </pre>
 * @date 2020年7月3日
 */
@Entity
@Table(name = "MULTI_ORG_DING_ROLE")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiOrgDingRole extends TenantEntity {

    private static final long serialVersionUID = -7154646885062875692L;

    private String roleId; // 平台用户id
    private String dingRoleId; // 钉钉角色
    private String dingRoleName; // 钉钉角色名称
    private String dingGroupId; // 钉钉组
    private String dingGroupName; // 钉钉组名称

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDingRoleId() {
        return dingRoleId;
    }

    public void setDingRoleId(String dingRoleId) {
        this.dingRoleId = dingRoleId;
    }

    public String getDingRoleName() {
        return dingRoleName;
    }

    public void setDingRoleName(String dingRoleName) {
        this.dingRoleName = dingRoleName;
    }

    public String getDingGroupId() {
        return dingGroupId;
    }

    public void setDingGroupId(String dingGroupId) {
        this.dingGroupId = dingGroupId;
    }

    public String getDingGroupName() {
        return dingGroupName;
    }

    public void setDingGroupName(String dingGroupName) {
        this.dingGroupName = dingGroupName;
    }
}
