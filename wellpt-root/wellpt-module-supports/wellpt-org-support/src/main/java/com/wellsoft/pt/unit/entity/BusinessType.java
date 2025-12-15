package com.wellsoft.pt.unit.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 业务类别实体
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_business_type")
@DynamicUpdate
@DynamicInsert
public class BusinessType extends IdEntity {
    private static final long serialVersionUID = 22168824460096188L;

    //标识
    @NotBlank
    private String id;

    //名称
    @NotBlank
    private String name;

    //编号
    @NotBlank
    private String code;

    //业务管理单位
    @UnCloneable
    private CommonUnit unit;

    //业务类别单位通讯录管理人员
    private String unitManagerUserId;

    private String unitManagerUserName;
    //业务单位组织树根
    @UnCloneable
    private Set<BusinessUnitTree> businessUnitRoots = new HashSet<BusinessUnitTree>();

    public String getUnitManagerUserName() {
        return unitManagerUserName;
    }

    public void setUnitManagerUserName(String unitManagerUserName) {
        this.unitManagerUserName = unitManagerUserName;
    }

    public String getUnitManagerUserId() {
        return unitManagerUserId;
    }

    public void setUnitManagerUserId(String unitManagerUserId) {
        this.unitManagerUserId = unitManagerUserId;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_uuid")
    public CommonUnit getUnit() {
        return unit;
    }

    public void setUnit(CommonUnit unit) {
        this.unit = unit;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "businessType")
    @Cascade({CascadeType.ALL})
    public Set<BusinessUnitTree> getBusinessUnitRoots() {
        return businessUnitRoots;
    }

    public void setBusinessUnitRoots(Set<BusinessUnitTree> businessUnitRoots) {
        this.businessUnitRoots = businessUnitRoots;
    }

}
