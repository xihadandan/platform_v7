/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.bean;

import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
@ApiModel("权限资源Bean")
public class PrivilegeBean extends Privilege {

    private static final long serialVersionUID = 1713664570812949046L;

    @ApiModelProperty("其他权限资源")
    private Set<PrivilegeResource> otherResources = new HashSet<PrivilegeResource>();
    @ApiModelProperty("orgRows")
    private Set<String> orgRows = new HashSet<String>();
    @ApiModelProperty("orgAddRows")
    private Set<String> orgAddRows = new HashSet<String>();
    @ApiModelProperty("orgDeleteRows")
    private Set<String> orgDeleteRows = new HashSet<String>();

    /**
     * @return the otherResources
     */
    public Set<PrivilegeResource> getOtherResources() {
        return otherResources;
    }

    /**
     * @param otherResources 要设置的otherResources
     */
    public void setOtherResources(Set<PrivilegeResource> otherResources) {
        this.otherResources = otherResources;
    }

    public Set<String> getOrgAddRows() {
        return orgAddRows;
    }

    public void setOrgAddRows(Set<String> orgAddRows) {
        this.orgAddRows = orgAddRows;
    }

    public Set<String> getOrgDeleteRows() {
        return orgDeleteRows;
    }

    public void setOrgDeleteRows(Set<String> orgDeleteRows) {
        this.orgDeleteRows = orgDeleteRows;
    }

    public Set<String> getOrgRows() {
        return orgRows;
    }

    public void setOrgRows(Set<String> orgRows) {
        this.orgRows = orgRows;
    }

}
