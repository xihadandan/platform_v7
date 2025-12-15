package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.pt.multi.org.entity.MultiOrgElementRole;

/**
 * @author yt
 * @title: OrgElementRole
 * @date 2020/7/28 10:05
 */
public class OrgElementRole extends MultiOrgElementRole {

    private OrgElementVo orgElement;

    public OrgElementVo getOrgElement() {
        return orgElement;
    }

    public void setOrgElement(OrgElementVo orgElement) {
        this.orgElement = orgElement;
    }
}
