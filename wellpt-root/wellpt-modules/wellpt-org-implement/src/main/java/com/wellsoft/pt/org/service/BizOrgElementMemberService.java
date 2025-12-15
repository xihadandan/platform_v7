package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.org.dao.impl.BizOrgElementMemberDaoImpl;
import com.wellsoft.pt.org.dto.BizOrgElementMemberDto;
import com.wellsoft.pt.org.entity.BizOrgElementMemberEntity;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月29日   chenq	 Create
 * </pre>
 */
public interface BizOrgElementMemberService extends JpaService<BizOrgElementMemberEntity, BizOrgElementMemberDaoImpl, Long> {
    void addBizOrgElementMember(List<BizOrgElementMemberEntity> elementMembers);

    void removeBizOrgElementMember(List<BizOrgElementMemberEntity> elementMembers);

    List<BizOrgElementMemberEntity> listByBizOrgElementIdAndRoleIds(String bizOrgElementId, Set<String> roleIds);

    void saveBizOrgElementMember(String bizOrgElementId, String bizOrgRoleId, List<BizOrgElementMemberEntity> elementMembers);

    List<BizOrgElementMemberEntity> getAllByBizOrgUuid(Long uuid);

    List<BizOrgElementMemberEntity> getAllByBizOrgElementIdsAndBizOrgUuid(List<String> bizOrgElementIds, Long bizOrgUuid);

    List<String> getAllSubMemberIdsByBizOrgElementId(String bizOrgElementId);

    List<BizOrgElementMemberDto> getDetailsByMemberId(String userId, String system);

    void deleteAllMemberByBizOrgRoleIdAndBizOrgUuid(Set<String> bizOrgRoleIds, Long bizOrgUuid);

    /**
     * 根据组织元素ID列表、业务角色ID列表，获取成员ID列表
     *
     * @param eleIds
     * @param bizRoleIds
     * @param includeSubMemberId
     * @param includeSubItemMember
     * @return
     */
    List<String> listMemberIdByBizOrgElementIdsAndBizRoleIds(List<String> eleIds, List<String> bizRoleIds, boolean includeSubMemberId, boolean includeSubItemMember);

    /**
     * 根据成员ID、组织元素ID列表、业务组织ID列表，获取成员列表
     *
     * @param memberId
     * @param eleIds
     * @param bizOrgIds
     * @return
     */
    List<BizOrgElementMemberEntity> listByMemberIdAndBizOrgElementIds(String memberId, List<String> eleIds, String[] bizOrgIds);

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param memberId
     * @param bizOrgIds
     * @return
     */
    List<String> listSameBizRoleMemberId(String memberId, String[] bizOrgIds);

    /**
     * @param id
     * @param bizOrgId
     * @return
     */
    boolean isMemberOf(String id, String bizOrgId);

    /**
     * @param userIds
     * @param bizOrgId
     * @return
     */
    List<String> listMemberIdByMemberIdsAndBizOrgId(List<String> userIds, String bizOrgId);

    /**
     * @param userId
     * @param bizOrgElementIds
     * @param bizOrgId
     * @return
     */
    boolean isInBizOrgElement(String userId, List<String> bizOrgElementIds, String bizOrgId);

    List<OrgTreeNodeDto> getUserBizOrgRolesByOrgUuid(String userId, Long orgUuid);
}
