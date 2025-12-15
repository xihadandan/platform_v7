/*
 * @(#)2012-11-14 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据字典实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-14.1	zhulh		2012-11-14		Create
 * </pre>
 * @date 2012-11-14
 */
@Entity
@Table(name = "cd_data_dict")
@DynamicUpdate
@DynamicInsert
public class DataDictionary extends IdEntity {
    private static final long serialVersionUID = 2943854786118950658L;

    /**
     * 名称
     */
    @NotBlank
    @MaxLength(max = 4000)
    private String name;
    /**
     * 代码
     */
    @NotBlank
    private String code;
    /**
     * 字典类型
     */
    @NotBlank
    private String type;
    /**
     * 字典是否可编辑
     */
    @UnCloneable
    private boolean editable = true;
    /**
     * 字典是否可删除
     */
    @UnCloneable
    private boolean deletable = true;
    /**
     * 字典子项是否可编辑
     */
    @UnCloneable
    private boolean childEditable = true;

    private Boolean isRef = false; //是否引用

    /**
     * 父结点
     */
    @UnCloneable
    private DataDictionary parent;

    private String parentUuid;

    /**
     * 自关联
     */
    @UnCloneable
    private List<DataDictionary> children = new ArrayList<>(0);
    /**
     * 字典属性
     */
    @UnCloneable
    private List<DataDictionaryAttribute> attributes = new ArrayList<DataDictionaryAttribute>(0);

    /**
     * 字典所有者，从组织机构中选择直接作为ACL中的SID
     */
    private List<String> owners = new ArrayList<String>(0);

    //来源uuid
    private String sourceUuid;

    //来源类型
    private String sourceType;

    private String moduleId;

    private Integer seq;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public DataDictionary getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(DataDictionary parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("seq asc, code asc, createTime asc")
    public List<DataDictionary> getChildren() {
        return children;
    }

    /**
     * @param childrens 要设置的childrens
     */
    public void setChildren(List<DataDictionary> childrens) {
        this.children = childrens;
    }

    /**
     * @return the attributes
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataDictionary")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("attrOrder")
    public List<DataDictionaryAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes 要设置的attributes
     */
    public void setAttributes(List<DataDictionaryAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the owners
     */
    @Transient
    public List<String> getOwners() {
        return owners;
    }

    /**
     * @param owners 要设置的owners
     */
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isChildEditable() {
        return childEditable;
    }

    public void setChildEditable(boolean childEditable) {
        this.childEditable = childEditable;
    }

    @Transient
    public Boolean getIsRef() {
        return isRef;
    }

    public void setIsRef(Boolean ref) {
        isRef = ref;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Transient
    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

}
