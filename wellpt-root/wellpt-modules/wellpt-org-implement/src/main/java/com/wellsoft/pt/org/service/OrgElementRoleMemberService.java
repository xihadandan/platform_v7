package com.wellsoft.pt.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgElementRoleMemberDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementRoleMemberEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月24日   chenq	 Create
 * </pre>
 */
public interface OrgElementRoleMemberService extends JpaService<OrgElementRoleMemberEntity, OrgElementRoleMemberDaoImpl, Long> {
    List<OrgElementRoleMemberEntity> listByOrgVersionUuid(Long orgVersionUuid);

    void deleteByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid);

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    List<OrgElementRoleMemberEntity> listByOrgVersionUuidAndOrgElementId(Long orgVersionUuid, String orgElementId);

    void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid);

    void deleteByMemberAndOrgVersionUuid(String member, Long orgVersionUuid);

    void deleteByMember(String member);

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<OrgElementRoleMemberEntity> listUserSameElementRoleMember(String userId, String[] orgVersionIds);

    /**
     * 获取组织元素下对应的组织角色人员
     *
     * @param eleId
     * @param orgRoleId
     * @param orgId
     * @return
     */
    List<OrgElementRoleMemberEntity> listByElementIdWithOrgRoleIdAndOrgId(String eleId, String orgRoleId, String orgId);

    void addOrgRoleMember(List<OrgElementRoleMemberEntity> list);

    void removeOrgRoleMember(List<String> userIds, Long orgVersionUuid);

    Long countOrgRoleUserByOrgVersionUuid(Long orgRoleUuid, Long orgVersionUuid);
}
