package com.wellsoft.pt.app.feishu.model;

import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.User;

import java.util.List;

public class DepartmentNode {
    private Department department;
    private List<User> users;
    private List<DepartmentNode> children;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<DepartmentNode> getChildren() {
        return children;
    }

    public void setChildren(List<DepartmentNode> children) {
        this.children = children;
    }
}
