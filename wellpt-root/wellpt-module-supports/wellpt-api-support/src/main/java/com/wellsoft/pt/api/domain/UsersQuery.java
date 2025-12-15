package com.wellsoft.pt.api.domain;

import com.wellsoft.pt.api.WellptObject;

public class UsersQuery extends WellptObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4810429238935599617L;

    private String uuid;
    private String id;
    private String majorJobName;
    private String userName;
    private String departmentName;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMajorJobName() {
        return majorJobName;
    }

    public void setMajorJobName(String majorJobName) {
        this.majorJobName = majorJobName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
