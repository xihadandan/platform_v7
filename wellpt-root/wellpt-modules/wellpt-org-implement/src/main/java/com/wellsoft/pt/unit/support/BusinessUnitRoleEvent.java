/*
 * @(#)2015-1-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support;

import com.wellsoft.context.event.WellptEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-27.1	zhulh		2015-1-27		Create
 * </pre>
 * @date 2015-1-27
 */
public class BusinessUnitRoleEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2166262264855866943L;

    private String unitId;

    private Collection<BusinessUnitRole> addedRoles = new ArrayList<BusinessUnitRole>(0);

    private Collection<BusinessUnitRole> deletedRoles = new ArrayList<BusinessUnitRole>(0);

    /**
     * @param source
     */
    public BusinessUnitRoleEvent(Object source) {
        super(source);
    }

    public BusinessUnitRoleEvent(String unitId, Collection<BusinessUnitRole> addedRoles,
                                 Collection<BusinessUnitRole> deletedRoles) {
        super(unitId);
        this.unitId = unitId;
        this.addedRoles = addedRoles;
        this.deletedRoles = deletedRoles;
    }

    /**
     * @return the unitId
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * @param unitId 要设置的unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the addedRoles
     */
    public Collection<BusinessUnitRole> getAddedRoles() {
        return addedRoles;
    }

    /**
     * @param addedRoles 要设置的addedRoles
     */
    public void setAddedRoles(Collection<BusinessUnitRole> addedRoles) {
        this.addedRoles = addedRoles;
    }

    /**
     * @return the deletedRoles
     */
    public Collection<BusinessUnitRole> getDeletedRoles() {
        return deletedRoles;
    }

    /**
     * @param deletedRoles 要设置的deletedRoles
     */
    public void setDeletedRoles(Collection<BusinessUnitRole> deletedRoles) {
        this.deletedRoles = deletedRoles;
    }

}
