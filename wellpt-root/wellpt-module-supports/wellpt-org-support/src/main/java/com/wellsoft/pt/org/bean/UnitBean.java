/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.pt.org.entity.Unit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description: 组织单元VO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
public class UnitBean extends Unit {

    private static final long serialVersionUID = 3338259851634263946L;

    // 组织单元UUId
    private String parentUuid;

    private Set<UnitMemberBean> memberBeans = new HashSet<UnitMemberBean>(0);

    private Set<UnitMemberBean> changedMemberBeans = new HashSet<UnitMemberBean>(0);

    // <userId, username>
    private Map<String, String> userCache = new HashMap<String, String>();

    /**
     * @return the parentUuid
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the memberBeans
     */
    public Set<UnitMemberBean> getMemberBeans() {
        return memberBeans;
    }

    /**
     * @param memberBeans 要设置的memberBeans
     */
    public void setMemberBeans(Set<UnitMemberBean> memberBeans) {
        this.memberBeans = memberBeans;
    }

    /**
     * @return the changedMemberBeans
     */
    public Set<UnitMemberBean> getChangedMemberBeans() {
        return changedMemberBeans;
    }

    /**
     * @param changedMemberBeans 要设置的changedMemberBeans
     */
    public void setChangedMemberBeans(Set<UnitMemberBean> changedMemberBeans) {
        this.changedMemberBeans = changedMemberBeans;
    }

    /**
     * @return the userCache
     */
    public Map<String, String> getUserCache() {
        return userCache;
    }

    /**
     * @param userCache 要设置的userCache
     */
    public void setUserCache(Map<String, String> userCache) {
        this.userCache = userCache;
    }

}
