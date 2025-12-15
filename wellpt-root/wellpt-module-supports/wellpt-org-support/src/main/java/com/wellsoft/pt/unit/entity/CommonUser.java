package com.wellsoft.pt.unit.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 业务用户实体
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_common_user")
@DynamicUpdate
@DynamicInsert
public class CommonUser extends IdEntity {
    private static final long serialVersionUID = -8216934342554547615L;

    //标识
    private String id;

    //姓名
    private String name;

    //编号
    private String code;

    //性别
    private String sex;

    //所属单位
    @UnCloneable
    private CommonUnit unit;

    //所属部门
    @UnCloneable
    private Set<CommonDepartment> departments = new HashSet<CommonDepartment>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_uuid")
    public CommonUnit getUnit() {
        return unit;
    }

    public void setUnit(CommonUnit unit) {
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @ManyToMany
    @JoinTable(name = "unit_department_user",
            joinColumns = {@JoinColumn(name = "common_user_uuid")}, inverseJoinColumns = {@JoinColumn(name = "common_department_uuid")})
    public Set<CommonDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<CommonDepartment> departments) {
        this.departments = departments;
    }
}
