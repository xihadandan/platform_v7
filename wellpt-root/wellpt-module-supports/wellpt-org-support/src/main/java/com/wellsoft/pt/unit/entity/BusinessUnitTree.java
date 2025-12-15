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
 * 公共库业务单位树
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_business_tree")
@DynamicUpdate
@DynamicInsert
public class BusinessUnitTree extends IdEntity {
    private static final long serialVersionUID = -1454279796570108614L;

    private String name;

    private String code;

    //公共库单位
    @UnCloneable
    private CommonUnit unit;

    //业务单位通讯录树对应的业务类别
    @UnCloneable
    private BusinessType businessType;

    //父单位节点
    @UnCloneable
    private BusinessUnitTree parent;

    //业务类别的单位内业务负责人
    private String businessManagerUserId;
    //业务类别的单位内发送人员
    private String businessSenderId;
    //业务类别的单位内接受人员
    private String businessReceiverId;
    // 子结点
    @UnCloneable
    private List<BusinessUnitTree> children = new ArrayList<BusinessUnitTree>(0);

    @Column(length = 2000)
    public String getBusinessSenderId() {
        return businessSenderId;
    }

    public void setBusinessSenderId(String businessSenderId) {
        this.businessSenderId = businessSenderId;
    }

    @Column(length = 2000)
    public String getBusinessReceiverId() {
        return businessReceiverId;
    }

    public void setBusinessReceiverId(String businessReceiverId) {
        this.businessReceiverId = businessReceiverId;
    }

    public String getBusinessManagerUserId() {
        return businessManagerUserId;
    }

    public void setBusinessManagerUserId(String businessManagerUserId) {
        this.businessManagerUserId = businessManagerUserId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.MERGE})
    @JoinColumn(name = "business_type_uuid")
    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_uuid")
    public CommonUnit getUnit() {
        return unit;
    }

    public void setUnit(CommonUnit unit) {
        this.unit = unit;
    }

    @ManyToOne
    @JoinColumn(name = "parent_uuid")
    public BusinessUnitTree getParent() {
        return parent;
    }

    public void setParent(BusinessUnitTree parent) {
        this.parent = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @OrderBy("code asc")
    public List<BusinessUnitTree> getChildren() {
        return children;
    }

    public void setChildren(List<BusinessUnitTree> children) {
        this.children = children;
    }

}
