/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;

import javax.persistence.Entity;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
public class OrgUserJobDto extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1533268995391800701L;

    // 职位对应的组织树节点信息
    private OrgTreeNodeDto orgTreeNodeDto;
    // 是否主职
    private Integer isMain;

    private String userId;

    /**
     * @return the orgTreeNodeDto
     */
    public OrgTreeNodeDto getOrgTreeNodeDto() {
        return orgTreeNodeDto;
    }

    /**
     * @param orgTreeNodeDto 要设置的orgTreeNodeDto
     */
    public void setOrgTreeNodeDto(OrgTreeNodeDto orgTreeNodeDto) {
        this.orgTreeNodeDto = orgTreeNodeDto;
    }

    /**
     * @return the isMain
     */
    public Integer getIsMain() {
        return isMain;
    }

    /**
     * @param isMain 要设置的isMain
     */
    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
