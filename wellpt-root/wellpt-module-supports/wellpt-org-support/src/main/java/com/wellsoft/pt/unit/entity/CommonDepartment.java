package com.wellsoft.pt.unit.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共库单位实体
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_common_department")
@DynamicUpdate
@DynamicInsert
public class CommonDepartment extends IdEntity {
    private static final long serialVersionUID = 3310651726535543104L;

    //标识
    private String id;

    //名称
    private String name;

    //编号
    private String code;

    //父部门
    @UnCloneable
    private CommonDepartment parent;

    //所属单位
    @UnCloneable
    private CommonUnit unit;

    //是否允许显示
    private Boolean isVisible;
    // 自关联
    @UnCloneable
    private List<CommonDepartment> children = new ArrayList<CommonDepartment>(0);

    public Boolean getIsVisible() {
        return isVisible;
    }

    //部门下用户
    //	@UnCloneable
    //	private Set<CommonUser> users = new HashSet<CommonUser>(0);

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
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

    @ManyToOne
    @JoinColumn(name = "parent_uuid")
    public CommonDepartment getParent() {
        return parent;
    }

    public void setParent(CommonDepartment parent) {
        this.parent = parent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_uuid")
    public CommonUnit getUnit() {
        return unit;
    }

    public void setUnit(CommonUnit unit) {
        this.unit = unit;
    }

    //	@ManyToMany(mappedBy = "departments")
    //	@Cascade(CascadeType.DELETE)
    //	@OrderBy("code asc")
    //	public Set<CommonUser> getUsers() {
    //		return users;
    //	}

    //	public void setUsers(Set<CommonUser> users) {
    //		this.users = users;
    //	}
    //
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade(CascadeType.DELETE)
    @OrderBy("code asc")
    public List<CommonDepartment> getChildren() {
        return children;
    }

    public void setChildren(List<CommonDepartment> children) {
        this.children = children;
    }
}
