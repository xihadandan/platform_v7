package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.Group;
import com.wellsoft.pt.org.entity.GroupMember;

import java.util.List;

/**
 * Description: GroupMemberService.java
 *
 * @author yuyq
 * @date 2014-12-24
 */
public interface GroupMemberService {
    /**
     * 保存群成员
     *
     * @param groupMember
     */
    void save(GroupMember groupMember);

    /**
     * 单个删除群成员
     *
     * @param groupMember
     */
    void delete(GroupMember groupMember);

    /**
     * 批量删除群成员
     *
     * @param list
     */
    void delete(List<GroupMember> list);

    /**
     * @param groupuuid
     * @param useruuid
     * @Title: delete1
     * @Description: 通过群组uuid和成员uuid删除
     */
    void delete(String groupuuid, String useruuid);

    /**
     * 获取全部数据
     *
     * @return
     */
    List<GroupMember> getAll();

    /**
     * @param useruuid 用户uuid
     * @return
     * @Title: getGroups
     * @Description: 获取全部数据
     */
    List<Group> getGroups(String useruuid);

    /**
     * 获取群成员
     *
     * @return
     */
    List<GroupMember> getGroupMembers(String groupuuid);

    /**
     * 获取群管理员
     *
     * @return
     */
    List<GroupMember> getGroupAdmins(String groupuuid);

    /**
     * 获取第一个加入群的用户
     *
     * @param group_uuid
     * @return
     */
    GroupMember getFirstGroupMember(String group_uuid);

    /**
     * 通过主键获取群成员
     *
     * @param uuid
     * @return
     */
    GroupMember get(String uuid);

    /**
     * @param groupuuid
     * @param useruuid
     * @Title: get
     * @Description: 通过群组uuid和成员uuid获取对象
     */
    GroupMember get(String groupuuid, String useruuid);


    /**
     * 如何描述该方法
     * 通过群组的uuid
     *
     * @param uuid
     */
    public void deleteByGroupMember(String uuid);

    /**
     * 如何描述该方法
     * 通过人员的uuid
     *
     * @param uuid
     */
    public void deleteByMember(String groupuuid, String useruuid);


    /**
     * 获取第一个加入群的人员
     *
     * @param useruuid
     * @return
     */
    public List<GroupMember> getFirstMembers(String groupuuid);


    /**
     * 获取群管理员
     *
     * @param useruuid
     * @return
     */
    public List<GroupMember> getAdmins(String groupuuid);


}
