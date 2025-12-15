package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
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
@Table(name = "ORG_GROUP")
@DynamicUpdate
@DynamicInsert
public class OrgGroupEntity extends SysEntity {

    private String id;

    private String code;

    private String name;

    private String remark;

//    private GroupType type;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

//    public GroupType getType() {
//        return type;
//    }
//
//    public void setType(GroupType type) {
//        this.type = type;
//    }
//
//    public static enum GroupType {
//        PUBLIC, PRIVATE
//    }
}
