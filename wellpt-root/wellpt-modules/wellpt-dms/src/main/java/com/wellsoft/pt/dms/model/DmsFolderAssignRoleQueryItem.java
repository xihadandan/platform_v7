/*
 * @(#)Jan 8, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 8, 2018.1	zhulh		Jan 8, 2018		Create
 * </pre>
 * @date Jan 8, 2018
 */
public class DmsFolderAssignRoleQueryItem extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4884105655829362574L;

    private String folderUuid;

    private String roleUuid;

    private String permit;

    private String deny;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the roleUuid
     */
    public String getRoleUuid() {
        return roleUuid;
    }

    /**
     * @param roleUuid 要设置的roleUuid
     */
    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    /**
     * @return the permit
     */
    public String getPermit() {
        return permit;
    }

    /**
     * @param permit 要设置的permit
     */
    public void setPermit(String permit) {
        this.permit = permit;
    }

    /**
     * @return the deny
     */
    public String getDeny() {
        return deny;
    }

    /**
     * @param deny 要设置的deny
     */
    public void setDeny(String deny) {
        this.deny = deny;
    }

}
