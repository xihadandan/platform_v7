package com.wellsoft.pt.repository.entity.jcr;

import java.io.Serializable;

public class Permission implements Serializable {
    public static final String USERS_READ = "okm:authUsersRead";
    public static final String USERS_WRITE = "okm:authUsersWrite";
    public static final String USERS_DELETE = "okm:authUsersDelete";
    public static final String USERS_SECURITY = "okm:authUsersSecurity";
    public static final String ROLES_READ = "okm:authRolesRead";
    public static final String ROLES_WRITE = "okm:authRolesWrite";
    public static final String ROLES_DELETE = "okm:authRolesDelete";
    public static final String ROLES_SECURITY = "okm:authRolesSecurity";
    public static final byte NONE = 0;
    public static final byte READ = 1;
    public static final byte WRITE = 2;
    public static final byte DELETE = 4;
    public static final byte SECURITY = 8;
    private static final long serialVersionUID = -6594786775079108975L;
    private String item;
    private int permissions;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("item=");
        sb.append(item);
        sb.append(", permissions=");
        sb.append(permissions);
        sb.append("}");
        return sb.toString();
    }
}
