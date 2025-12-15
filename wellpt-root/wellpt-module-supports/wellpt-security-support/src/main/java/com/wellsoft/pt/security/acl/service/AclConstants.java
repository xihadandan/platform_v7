package com.wellsoft.pt.security.acl.service;

import com.wellsoft.context.base.key.KeyConstants;

public class AclConstants {
    public static final String DEFAULT_ERROR_VIEW = "/common/acl_error";

    public final static int TYPE_GROUP = 2;
    public final static int TYPE_DEPARTMENT = 1;
    public final static int TYPE_USER = 0;

    public final static byte PERMISSION_READ = 1;
    public final static byte PERMISSION_WRITE = 11;
    public final static byte PERMISSION_DELETE = 100;
    public final static byte PERMISSION_WRITE_READ = 011;
    public final static byte PERMISSION_DELETE_READ = 101;
    public final static byte PERMISSION_DELETE_WRITE = 110;
    public final static byte PERMISSION_ALL = 111;

    public static int getUserType(String prefix) {
        int type = AclConstants.TYPE_USER;
        if (prefix.equals(KeyConstants.PREFIX_USER)) {
            type = AclConstants.TYPE_USER;

        } else if (prefix.equals(KeyConstants.PREFIX_GROUP)) {
            type = AclConstants.TYPE_GROUP;
        } else if (prefix.equals(KeyConstants.PREFIX_DEP)) {
            type = AclConstants.TYPE_DEPARTMENT;
        }
        return type;
    }
}
