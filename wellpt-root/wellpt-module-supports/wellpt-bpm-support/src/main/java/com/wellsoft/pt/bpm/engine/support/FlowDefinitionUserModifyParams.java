/*
 * @(#)12/4/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/4/24.1	    zhulh		12/4/24		    Create
 * </pre>
 * @date 12/4/24
 */
public class FlowDefinitionUserModifyParams extends BaseObject {
    private static final long serialVersionUID = -3303890073911907490L;

    // replace、add、delete
    private String modifyMode;

    private String oldUserId;
    private String oldUserName;

    private String newUserId;
    private String newUserName;
    private String newUserPath;

    /**
     * @return the modifyMode
     */
    public String getModifyMode() {
        return modifyMode;
    }

    /**
     * @param modifyMode 要设置的modifyMode
     */
    public void setModifyMode(String modifyMode) {
        this.modifyMode = modifyMode;
    }

    /**
     * @return the oldUserId
     */
    public String getOldUserId() {
        return oldUserId;
    }

    /**
     * @param oldUserId 要设置的oldUserId
     */
    public void setOldUserId(String oldUserId) {
        this.oldUserId = oldUserId;
    }

    /**
     * @return the oldUserName
     */
    public String getOldUserName() {
        return oldUserName;
    }

    /**
     * @param oldUserName 要设置的oldUserName
     */
    public void setOldUserName(String oldUserName) {
        this.oldUserName = oldUserName;
    }

    /**
     * @return the newUserId
     */
    public String getNewUserId() {
        return newUserId;
    }

    /**
     * @param newUserId 要设置的newUserId
     */
    public void setNewUserId(String newUserId) {
        this.newUserId = newUserId;
    }

    /**
     * @return the newUserName
     */
    public String getNewUserName() {
        return newUserName;
    }

    /**
     * @param newUserName 要设置的newUserName
     */
    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    /**
     * @return the newUserPath
     */
    public String getNewUserPath() {
        return newUserPath;
    }

    /**
     * @param newUserPath 要设置的newUserPath
     */
    public void setNewUserPath(String newUserPath) {
        this.newUserPath = newUserPath;
    }

}
