/*
 * @(#)2013-1-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.bean;

import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据字典PO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-25.1	zhulh		2013-1-25		Create
 * </pre>
 * @date 2013-1-25
 */
public class DataDictionaryBean extends DataDictionary {

    private static final long serialVersionUID = 4542567638431968255L;

    // jqGrid的行标识
    private String id;

    private String parentName;

    private String parentUuid;

    /**
     * 字典所有者，从组织机构中选择直接作为ACL中的SID
     */
    private String ownerNames;

    private String ownerIds;

    private List<DataDictionaryBean> changedChildren = new ArrayList<DataDictionaryBean>();
    private List<DataDictionaryBean> deletedChildren = new ArrayList<DataDictionaryBean>();

    private List<DataDictionaryAttributeBean> changedAttributes = new ArrayList<DataDictionaryAttributeBean>();
    private List<DataDictionaryAttributeBean> deletedAttributes = new ArrayList<DataDictionaryAttributeBean>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public List<DataDictionaryBean> getChangedChildren() {
        return changedChildren;
    }

    public void setChangedChildren(List<DataDictionaryBean> changedChildren) {
        this.changedChildren = changedChildren;
    }

    /**
     * @return the deletedChildren
     */
    public List<DataDictionaryBean> getDeletedChildren() {
        return deletedChildren;
    }

    /**
     * @param deletedChildren 要设置的deletedChildren
     */
    public void setDeletedChildren(List<DataDictionaryBean> deletedChildren) {
        this.deletedChildren = deletedChildren;
    }

    /**
     * @return the ownerNames
     */
    public String getOwnerNames() {
        return ownerNames;
    }

    /**
     * @param ownerNames 要设置的ownerNames
     */
    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }

    /**
     * @return the ownerIds
     */
    public String getOwnerIds() {
        return ownerIds;
    }

    /**
     * @param ownerIds 要设置的ownerIds
     */
    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

    /**
     * @return the changedAttributes
     */
    public List<DataDictionaryAttributeBean> getChangedAttributes() {
        return changedAttributes;
    }

    /**
     * @param changedAttributes 要设置的changedAttributes
     */
    public void setChangedAttributes(List<DataDictionaryAttributeBean> changedAttributes) {
        this.changedAttributes = changedAttributes;
    }

    /**
     * @return the deletedAttributes
     */
    public List<DataDictionaryAttributeBean> getDeletedAttributes() {
        return deletedAttributes;
    }

    /**
     * @param deletedAttributes 要设置的deletedAttributes
     */
    public void setDeletedAttributes(List<DataDictionaryAttributeBean> deletedAttributes) {
        this.deletedAttributes = deletedAttributes;
    }

}
