/*
 * @(#)2013-4-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Unit;
import com.wellsoft.pt.org.entity.UnitMember;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-12.1	zhulh		2013-4-12		Create
 * </pre>
 * @date 2013-4-12
 */
@Repository
public class UnitMemberDao extends OrgHibernateDao<UnitMember, String> {

    private static final String QUERY_UNIT_BY_USER = "select unit_member.unit from UnitMember unit_member where unit_member.member like '%:userUuid%'";

    /**
     * @param userUuid
     * @return
     */
    public List<Unit> getUnitByUser(String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userUuid", userUuid);
        return this.find(QUERY_UNIT_BY_USER, values);
    }
}
