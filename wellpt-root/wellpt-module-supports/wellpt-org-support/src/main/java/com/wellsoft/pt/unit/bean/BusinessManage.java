package com.wellsoft.pt.unit.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务管理对象
 *
 * @author liuzq
 */
public class BusinessManage {
    //业务类别ID
    private String businessTypeId;
    //业务类别名称
    private String businessTypeName;
    //用户ID
    private String userId;
    //用户姓名
    private String userName;
    //单位ID
    private String unitId;
    //单位名称
    private String unitName;
    //是否是业务负责人
    private boolean isBusinessManager;
    //是否是业务发送人
    private boolean isBusinessSender;
    //是否是业务接收人
    private boolean isBusinessReceiver;
    // 业务角色列表
    private List<String> businessUnitRoles = new ArrayList<String>(0);

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public boolean isBusinessManager() {
        return isBusinessManager;
    }

    public void setBusinessManager(boolean isBusinessManager) {
        this.isBusinessManager = isBusinessManager;
    }

    public boolean isBusinessSender() {
        return isBusinessSender;
    }

    public void setBusinessSender(boolean isBusinessSender) {
        this.isBusinessSender = isBusinessSender;
    }

    public boolean isBusinessReceiver() {
        return isBusinessReceiver;
    }

    public void setBusinessReceiver(boolean isBusinessReceiver) {
        this.isBusinessReceiver = isBusinessReceiver;
    }

    /**
     * @return the businessUnitRoles
     */
    public List<String> getBusinessUnitRoles() {
        return businessUnitRoles;
    }

    /**
     * @param businessUnitRoles 要设置的businessUnitRoles
     */
    public void setBusinessUnitRoles(List<String> businessUnitRoles) {
        this.businessUnitRoles = businessUnitRoles;
    }

}
