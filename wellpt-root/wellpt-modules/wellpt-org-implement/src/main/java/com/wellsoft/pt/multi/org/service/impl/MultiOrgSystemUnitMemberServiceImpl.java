/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgSystemUnitMemberDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnitMember;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUnitMemberService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgSystemUnitMemberServiceImpl extends AbstractJpaServiceImpl<MultiOrgSystemUnitMember, MultiOrgSystemUnitMemberDao, String> implements
        MultiOrgSystemUnitMemberService {

    @Override
    public List<MultiOrgSystemUnitMember> queryMemberListOfUnit(String unitId, boolean isRecursion) {
        MultiOrgSystemUnitMember q = new MultiOrgSystemUnitMember();
        q.setSystemUnitId(unitId);
        List<MultiOrgSystemUnitMember> members = this.dao.listByEntity(q);
        List<MultiOrgSystemUnitMember> list = new ArrayList<MultiOrgSystemUnitMember>();
        if (members != null) {
            list.addAll(members);
        }
        // 是否需要递归查找子群组的成员
        if (isRecursion) { // 需要递归
            if (!CollectionUtils.isEmpty(members)) {
                for (MultiOrgSystemUnitMember member : members) {
                    List<MultiOrgSystemUnitMember> subList = this.queryMemberListOfUnit(member.getMemberUnitId(), true);
                    if (subList != null) {
                        list.addAll(subList);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public boolean isMember(String unitId, String memberId, boolean isRecursion) {
        List<MultiOrgSystemUnitMember> objs = this.queryMemberListOfUnit(unitId, isRecursion);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgSystemUnitMember obj : objs) {
                if (obj.getMemberUnitId().equals(memberId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteMemberListOfUnit(String unitId) {
        MultiOrgSystemUnitMember q = new MultiOrgSystemUnitMember();
        q.setSystemUnitId(unitId);
        List<MultiOrgSystemUnitMember> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
        return true;
    }
}
