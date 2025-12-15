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
public class AclEntryDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5518721178424905249L;

    private String aclObjectIdentity;
    private String sid;
    private Integer mask;
    private Boolean granting;
    private String objectIdIdentity;

    /**
     * @return the aclObjectIdentity
     */
    public String getAclObjectIdentity() {
        return aclObjectIdentity;
    }

    /**
     * @param aclObjectIdentity 要设置的aclObjectIdentity
     */
    public void setAclObjectIdentity(String aclObjectIdentity) {
        this.aclObjectIdentity = aclObjectIdentity;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * @param sid 要设置的sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * @return the mask
     */
    public Integer getMask() {
        return mask;
    }

    /**
     * @param mask 要设置的mask
     */
    public void setMask(Integer mask) {
        this.mask = mask;
    }

    /**
     * @return the granting
     */
    public Boolean getGranting() {
        return granting;
    }

    /**
     * @param granting 要设置的granting
     */
    public void setGranting(Boolean granting) {
        this.granting = granting;
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

}
