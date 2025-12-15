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
 * 公共库单位树
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_common_unit_tree")
@DynamicUpdate
@DynamicInsert
public class CommonUnitTree extends IdEntity {
    private static final long serialVersionUID = -5043011856430218212L;

    private String code;

    //公共库单位
    @UnCloneable
    private CommonUnit unit;

    //父单位节点
    @UnCloneable
    private CommonUnitTree parent;

    // 子结点
    @UnCloneable
    private List<CommonUnitTree> children = new ArrayList<CommonUnitTree>(0);

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.DELETE})
    @JoinColumn(name = "unit_uuid")
    public CommonUnit getUnit() {
        return unit;
    }

    public void setUnit(CommonUnit unit) {
        this.unit = unit;
    }

    @ManyToOne
    @JoinColumn(name = "parent_uuid")
    public CommonUnitTree getParent() {
        return parent;
    }

    public void setParent(CommonUnitTree parent) {
        this.parent = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @OrderBy("code asc")
    public List<CommonUnitTree> getChildren() {
        return children;
    }

    public void setChildren(List<CommonUnitTree> children) {
        this.children = children;
    }
}
