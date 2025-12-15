/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupMemberDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;

import java.util.List;
import java.util.Set;

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
public interface MultiOrgGroupMemberService extends JpaService<MultiOrgGroupMember, MultiOrgGroupMemberDao, String> {
    /**
     * 获取群组的成员
     *
     * @param groupId
     * @param isRecursion 是否递归判断， true : 是， false : 否
     * @return
     */
    List<MultiOrgGroupMember> queryMemberListOfGroup(String groupId, boolean isRecursion);

    /**
     * 根据群组Id集合获取群组的成员
     *
     * @param groupIdSet
     * @param isRecursion
     * @return
     */
    List<MultiOrgGroupMember> queryMemberListOfIds(Set<String> groupIdSet, boolean isRecursion);

    /**
     * 批量删除群组的所有成员
     *
     * @param groupId
     */
    boolean deleteMemberListOfGroup(String groupId);

    /**
     * 判断memberId是否是groupId中的成员,
     *
     * @param groupId
     * @param memberId
     * @param isRecursion 是否递归判断， true : 是， false : 否
     * @return
     */
    boolean isMember(String groupId, String memberId, boolean isRecursion);

    /**
     * 获取包含了指定ID的群组
     *
     * @param id
     * @return
     */
    List<MultiOrgGroupMember> queryGroupListByMemberId(String id);

    /**
     * 如何描述该方法
     *
     * @param id
     */
    void deleteMemeber(String id);


    List<MultiOrgGroup> queryUserGroupsByUserId(String userid);

    boolean isMemberOfGroup(String userId, Set<String> groupIds);

    Set<String> queryGroupIdsByMemeberId(Set<String> memberIds);
}
