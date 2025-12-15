package com.wellsoft.pt.unit.bean;

import com.wellsoft.pt.unit.entity.BusinessUnitTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: BusinessUnitTreeBean VO
 *
 * @author liuzq
 * @date 2013-11-5
 */
public class BusinessUnitTreeBean extends BusinessUnitTree {
    private static final long serialVersionUID = -3971160336321774590L;

    private String parentName;

    private String parentUuid;
    // 组织单元UUID
    private String unitId;
    // 组织单元名称
    private String unitName;
    // 业务类别UUID
    private String businessTypeUuid;
    // 业务类别名称
    private String businessTypeName;
    // 业务类别单位通讯录管理人员
    private String unitManagerUserId;
    private String unitManagerUserName;
    // 业务类别的单位内业务负责人
    private String businessManagerUserName;
    // 业务类别的单位内收发业务具体发送人员
    private String businessSenderName;
    // 业务类别的单位内收发业务具体接受人员
    private String businessReceiverName;

    // 当前用户是管理员
    private boolean currentUserIsAdmin;

    private List<BusinessUnitTreeRoleBean> businessUnitTreeRoles = new ArrayList<BusinessUnitTreeRoleBean>();

    private Set<BusinessUnitTreeRoleBean> changedBusinessUnitTreeRoles = new HashSet<BusinessUnitTreeRoleBean>(0);
    private Set<BusinessUnitTreeRoleBean> deletedBusinessUnitTreeRoles = new HashSet<BusinessUnitTreeRoleBean>(0);

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getUnitManagerUserId() {
        return unitManagerUserId;
    }

    public void setUnitManagerUserId(String unitManagerUserId) {
        this.unitManagerUserId = unitManagerUserId;
    }

    public String getUnitManagerUserName() {
        return unitManagerUserName;
    }

    public void setUnitManagerUserName(String unitManagerUserName) {
        this.unitManagerUserName = unitManagerUserName;
    }

    public String getBusinessManagerUserName() {
        return businessManagerUserName;
    }

    public void setBusinessManagerUserName(String businessManagerUserName) {
        this.businessManagerUserName = businessManagerUserName;
    }

    public String getBusinessSenderName() {
        return businessSenderName;
    }

    public void setBusinessSenderName(String businessSenderName) {
        this.businessSenderName = businessSenderName;
    }

    public String getBusinessReceiverName() {
        return businessReceiverName;
    }

    public void setBusinessReceiverName(String businessReceiverName) {
        this.businessReceiverName = businessReceiverName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getBusinessTypeUuid() {
        return businessTypeUuid;
    }

    public void setBusinessTypeUuid(String businessTypeUuid) {
        this.businessTypeUuid = businessTypeUuid;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the currentUserIsAdmin
     */
    public boolean isCurrentUserIsAdmin() {
        return currentUserIsAdmin;
    }

    /**
     * @param currentUserIsAdmin 要设置的currentUserIsAdmin
     */
    public void setCurrentUserIsAdmin(boolean currentUserIsAdmin) {
        this.currentUserIsAdmin = currentUserIsAdmin;
    }

    /**
     * @return the businessUnitTreeRoles
     */
    public List<BusinessUnitTreeRoleBean> getBusinessUnitTreeRoles() {
        return businessUnitTreeRoles;
    }

    /**
     * @param businessUnitTreeRoles 要设置的businessUnitTreeRoles
     */
    public void setBusinessUnitTreeRoles(List<BusinessUnitTreeRoleBean> businessUnitTreeRoles) {
        this.businessUnitTreeRoles = businessUnitTreeRoles;
    }

    /**
     * @return the changedBusinessUnitTreeRoles
     */
    public Set<BusinessUnitTreeRoleBean> getChangedBusinessUnitTreeRoles() {
        return changedBusinessUnitTreeRoles;
    }

    /**
     * @param changedBusinessUnitTreeRoles 要设置的changedBusinessUnitTreeRoles
     */
    public void setChangedBusinessUnitTreeRoles(Set<BusinessUnitTreeRoleBean> changedBusinessUnitTreeRoles) {
        this.changedBusinessUnitTreeRoles = changedBusinessUnitTreeRoles;
    }

    /**
     * @return the deletedBusinessUnitTreeRoles
     */
    public Set<BusinessUnitTreeRoleBean> getDeletedBusinessUnitTreeRoles() {
        return deletedBusinessUnitTreeRoles;
    }

    /**
     * @param deletedBusinessUnitTreeRoles 要设置的deletedBusinessUnitTreeRoles
     */
    public void setDeletedBusinessUnitTreeRoles(Set<BusinessUnitTreeRoleBean> deletedBusinessUnitTreeRoles) {
        this.deletedBusinessUnitTreeRoles = deletedBusinessUnitTreeRoles;
    }

}
