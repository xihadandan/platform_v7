/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupMemberDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupMemberService;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserWorkInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
public class MultiOrgGroupMemberServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgGroupMember, MultiOrgGroupMemberDao, String> implements
        MultiOrgGroupMemberService {
    @Autowired
    MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;
    @Autowired
    MultiOrgGroupService multiOrgGroupService;

    @Override
    public List<MultiOrgGroupMember> queryMemberListOfGroup(String groupId, boolean isRecursion) {
        MultiOrgGroupMember q = new MultiOrgGroupMember();
        q.setGroupId(groupId);
        List<MultiOrgGroupMember> list = new ArrayList<MultiOrgGroupMember>();
        List<MultiOrgGroupMember> members = this.dao.listByEntity(q);
        if (members != null) {
            list.addAll(members);
        }
        // 是否需要递归查找子群组的成员
        if (isRecursion) { // 需要递归
            if (!CollectionUtils.isEmpty(members)) {
                for (MultiOrgGroupMember member : members) {
                    if (member.getMemberObjType().equals(IdPrefix.GROUP.getValue())) {
                        List<MultiOrgGroupMember> subList = this.queryMemberListOfGroup(member.getMemberObjId(), true);
                        if (subList != null) {
                            list.addAll(subList);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<MultiOrgGroupMember> queryMemberListOfIds(Set<String> groupIdSet, boolean isRecursion) {
        List<MultiOrgGroupMember> groupMemberList = new ArrayList<>();
        if (CollectionUtils.isEmpty(groupIdSet)) {
            return groupMemberList;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("groupIds", groupIdSet);
        List<MultiOrgGroupMember> groupMembers = this.listByHQL("from MultiOrgGroupMember where groupId in (:groupIds)", paramsMap);
        if (groupMembers != null) {
            groupMemberList.addAll(groupMembers);
        }
        // 是否需要递归查找子群组的成员
        if (isRecursion) { // 需要递归
            if (!CollectionUtils.isEmpty(groupMembers)) {
                Set<String> gIdSet = new HashSet<>();
                for (MultiOrgGroupMember member : groupMembers) {
                    if (member.getMemberObjType().equals(IdPrefix.GROUP.getValue())) {
                        gIdSet.add(member.getMemberObjId());
                    }
                }
                if (!CollectionUtils.isEmpty(gIdSet)) {
                    groupMemberList.addAll(this.queryMemberListOfIds(gIdSet, isRecursion));
                }
            }
        }
        return groupMemberList;
    }

    @Override
    public boolean deleteMemberListOfGroup(String groupId) {
        MultiOrgGroupMember q = new MultiOrgGroupMember();
        q.setGroupId(groupId);
        List<MultiOrgGroupMember> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            deleteByEntities(objs);
        }
        return true;
    }

    @Override
    public boolean isMember(String groupId, String memberId, boolean isRecursion) {
        List<MultiOrgGroupMember> objs = this.queryMemberListOfGroup(groupId, isRecursion);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgGroupMember obj : objs) {
                if (obj.getMemberObjId().equals(memberId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgGroupMemberService#queryGroupListByMemberId(java.lang.String)
     */
    @Override
    public List<MultiOrgGroupMember> queryGroupListByMemberId(String id) {
        MultiOrgGroupMember q = new MultiOrgGroupMember();
        q.setMemberObjId(id);
        return this.dao.listByEntity(q);
    }

    @Override
    public void deleteMemeber(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", id);
        this.dao.updateByNamedSQL("deleteGroupMember", params);
    }

    @Override
    public List<MultiOrgGroup> queryUserGroupsByUserId(String userid) {
        Set<String> eleIds = Sets.newHashSet(userid);
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userid);
        if (workInfo != null && StringUtils.isNotBlank(workInfo.getEleIdPaths())) {
            String[] paths = workInfo.getEleIdPaths().split(";|/");
            for (String p : paths) {
                if (!p.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                    eleIds.add(p);
                }
            }
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("members", eleIds);
        return multiOrgGroupService.listByNameSQLQuery("queryGroupByMemberIds", params);

    }

    @Override
    public boolean isMemberOfGroup(String userId, Set<String> groupIds) {
        MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        if (workInfo == null || StringUtils.isBlank(workInfo.getEleIdPaths())) {
            return false;
        }
        String[] elePathParts = workInfo.getEleIdPaths().split(Separator.SEMICOLON.getValue());
        Set<String> ids = Sets.newHashSet();
        for (String p : elePathParts) {
            ids.addAll(Arrays.asList(p.split(Separator.SLASH.getValue())));
        }
        ids.add(userId);
        Map<String, Object> param = Maps.newHashMap();
        param.put("ids", ids);
        param.put("grpIds", groupIds);
        return this.dao.countBySQL("select 1 from multi_org_group g ,multi_org_group_member m  where g.id=m.group_id and m.member_obj_id in (:ids) and g.id in (:grpIds) ", param) > 0;
    }

    @Override
    public Set<String> queryGroupIdsByMemeberId(Set<String> memberIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", memberIds);
        List list = this.dao.listCharSequenceByHQL("from MultiOrgGroupMember where memberObjId in (:id)", params);
        return org.apache.commons.collections.CollectionUtils.isNotEmpty(list) ? Sets.newHashSet(list.iterator()) : null;
    }

}
