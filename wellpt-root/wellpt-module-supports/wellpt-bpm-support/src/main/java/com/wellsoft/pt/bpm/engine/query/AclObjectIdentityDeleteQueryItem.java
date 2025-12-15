/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
public class AclObjectIdentityDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2938587753320426517L;

    private String ownerSid;
    private String parentObject;
    private String objectIdClass;
    private String objectIdIdentity;
    private Boolean entriesInheriting;

    /**
     * @return the ownerSid
     */
    public String getOwnerSid() {
        return ownerSid;
    }

    /**
     * @param ownerSid 要设置的ownerSid
     */
    public void setOwnerSid(String ownerSid) {
        this.ownerSid = ownerSid;
    }

    /**
     * @return the parentObject
     */
    public String getParentObject() {
        return parentObject;
    }

    /**
     * @param parentObject 要设置的parentObject
     */
    public void setParentObject(String parentObject) {
        this.parentObject = parentObject;
    }

    /**
     * @return the objectIdClass
     */
    public String getObjectIdClass() {
        return objectIdClass;
    }

    /**
     * @param objectIdClass 要设置的objectIdClass
     */
    public void setObjectIdClass(String objectIdClass) {
        this.objectIdClass = objectIdClass;
    }

    /**
     * @return the objectIdIdentity
     */
    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    /**
     * @param objectIdIdentity 要设置的objectIdIdentity
     */
    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    /**
     * @return the entriesInheriting
     */
    public Boolean getEntriesInheriting() {
        return entriesInheriting;
    }

    /**
     * @param entriesInheriting 要设置的entriesInheriting
     */
    public void setEntriesInheriting(Boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

}
