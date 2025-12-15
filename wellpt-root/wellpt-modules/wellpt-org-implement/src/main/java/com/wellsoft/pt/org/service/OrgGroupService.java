package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgGroupDaoImpl;
import com.wellsoft.pt.org.dto.OrgGroupDto;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月07日   chenq	 Create
 * </pre>
 */
public interface OrgGroupService extends JpaService<OrgGroupEntity, OrgGroupDaoImpl, Long> {


    Long saveOrgGroup(OrgGroupDto dto);

    OrgGroupDto getOrgGroupDetails(Long uuid);

    void deleteOrgGroup(Long uuid);

    Map<String, String> getNamesByIds(List<String> ids);

    /**
     * 获取用户相关的群组ID
     *
     * @param userId
     * @return
     */
    List<String> listRelatedGroupIdByUserId(String userId);

    /**
     * 根据成员ID列表获取相关的群组ID
     *
     * @param memberIds
     * @return
     */
    List<String> listRelatedGroupIdByMemberIds(Collection<String> memberIds);

    /**
     * 通过群组ID，获取群组成员
     *
     * @param ids
     * @return
     */
    Set<String> listMemberIdByIds(Collection<String> ids);

    /**
     * 通过群组ID，获取群组成员
     *
     * @param groupId
     * @return
     */
    List<OrgGroupMemberEntity> listMemberById(String groupId);

    /**
     * 根据角色UUID删除群组角色
     *
     * @param roleUuid
     */
    void deleteGroupRoleByRoleUuid(String roleUuid);

    /**
     * 根据群组ID列表、角色UUID添加用户角色
     *
     * @param groupIds
     * @param roleUuid
     */
    void addGroupRoleByIdsAndRoleUuid(List<String> groupIds, String roleUuid);

    /**
     * 根据权限角色UUID获取群组
     *
     * @param roleUuid
     * @return
     */
    List<OrgGroupEntity> listByRoleUuid(String roleUuid);

    void deleteGroupRoleByIdsAndRoleUuid(List<String> groupIds, String roleUuid);

    List<OrgGroupEntity> listGroupsIncludeMember(Set<String> member);

    List<OrgGroupEntity> listByIds(List<String> ids);

    List<OrgGroupEntity> getRoleRelaGroups(String roleUuid, String system, String tenant);

}
