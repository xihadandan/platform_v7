/*
 * @(#)2013-3-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-28.1	zhulh		2013-3-28		Create
 * </pre>
 * @date 2013-3-28
 */
public class CustomDynamicButton {
    // 替换
    public static final String REPLACE = "1";
    // 新增操作
    public static final String NEW_OPERATE = "2";

    private String id;
    // 原权限编号
    private String code;
    // 新的权限编号
    private String newCode;
    // 新的权限编号是否需要权限
    private boolean requiredNewCodePrivilege = true;
    // 新的名称
    private String name;
    // 使用方式，0(替换名称)、1(全部替换)、2(新增操作)
    private String useWay;

    private String script;
    // 样式名
    private String className;

    // 用户自定义动态按钮的提交环节
    private String taskId;

    private List<String> owners = new ArrayList<String>(0);

    private List<String> users = new ArrayList<String>(0);

    private List<String> copyUsers = new ArrayList<String>(0);

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * @return the newCode
     */
    public String getNewCode() {
        return newCode;
    }

    /**
     * @param newCode 要设置的newCode
     */
    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    /**
     * @return the requiredNewCodePrivilege
     */
    public boolean isRequiredNewCodePrivilege() {
        return requiredNewCodePrivilege;
    }

    /**
     * @param requiredNewCodePrivilege 要设置的requiredNewCodePrivilege
     */
    public void setRequiredNewCodePrivilege(boolean requiredNewCodePrivilege) {
        this.requiredNewCodePrivilege = requiredNewCodePrivilege;
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
     * @return the useWay
     */
    public String getUseWay() {
        return useWay;
    }

    /**
     * @param useWay 要设置的useWay
     */
    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script 要设置的script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the owners
     */
    public List<String> getOwners() {
        return owners;
    }

    /**
     * @param owners 要设置的owners
     */
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    /**
     * @return the users
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }

    /**
     * @return the copyUsers
     */
    public List<String> getCopyUsers() {
        return copyUsers;
    }

    /**
     * @param copyUsers 要设置的copyUsers
     */
    public void setCopyUsers(List<String> copyUsers) {
        this.copyUsers = copyUsers;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomDynamicButton other = (CustomDynamicButton) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
