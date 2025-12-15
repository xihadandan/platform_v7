package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务组织角色模板
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ROLE_TEMPLATE")
@DynamicUpdate
@DynamicInsert
public class BizOrgRoleTemplateEntity extends SysEntity {

    private static final long serialVersionUID = 7041691237792099328L;
    private String name;
    private String icon;
    private String remark;
    private String roleTemplate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRoleTemplate() {
        return roleTemplate;
    }

    public void setRoleTemplate(String roleTemplate) {
        this.roleTemplate = roleTemplate;
    }


}
