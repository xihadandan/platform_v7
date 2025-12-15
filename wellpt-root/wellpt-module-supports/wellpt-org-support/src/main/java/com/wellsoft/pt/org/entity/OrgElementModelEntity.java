package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 组织单元模型
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_MODEL")
@DynamicUpdate
@DynamicInsert
public class OrgElementModelEntity extends SysEntity {

    public static final String ORG_UNIT_ID = "unit";
    public static final String ORG_DEPT_ID = "dept";
    public static final String ORG_JOB_ID = "job";
    public static final String ORG_CLASSIFY_ID = "classify";
    public static final String[] DEFAULT_ID = new String[]{ORG_UNIT_ID, ORG_DEPT_ID, ORG_JOB_ID, ORG_CLASSIFY_ID};
    private static final long serialVersionUID = -5790509465827213407L;
    private String name;

    private String id; // 组织模型id

    private Type type; // 组织单元类型

    private String icon; // 图标类


    private Boolean enable; // 是否启用

    private String remark; // 备注

    private String defJson;// 非持久化字段：定义json


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Transient
    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public enum Type {
        MANAGE/*管理职能*/, MANAGELESS/*非管理职能*/
    }


}
