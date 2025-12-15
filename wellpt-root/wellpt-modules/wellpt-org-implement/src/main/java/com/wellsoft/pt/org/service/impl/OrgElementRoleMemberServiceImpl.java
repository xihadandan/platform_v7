package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgElementRoleMemberDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementRoleMemberEntity;
import com.wellsoft.pt.org.entity.OrgRoleEntity;
import com.wellsoft.pt.org.service.OrgElementRoleMemberService;
import com.wellsoft.pt.org.service.OrgRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

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
@Service
public class OrgElementRoleMemberServiceImpl extends AbstractJpaServiceImpl<OrgElementRoleMemberEntity, OrgElementRoleMemberDaoImpl, Long> implements OrgElementRoleMemberService {

    @Autowired
    OrgRoleService orgRoleService;

    @Override
    public List<OrgElementRoleMemberEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementRoleMemberEntity where orgElementId=:orgElementId and orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementRoleMemberEntity where orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public List<OrgElementRoleMemberEntity> listByOrgVersionUuidAndOrgElementId(Long orgVersionUuid, String orgElementId) {
        OrgElementRoleMemberEntity example = new OrgElementRoleMemberEntity();
        example.setOrgVersionUuid(orgVersionUuid);
        example.setOrgElementId(orgElementId);
        return dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgElementIds", orgElementIds);
        dao.deleteByHQL("delete OrgElementRoleMemberEntity where orgElementId in (:orgElementIds) and orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    @Transactional
    public void deleteByMemberAndOrgVersionUuid(String member, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("userId", member);
        dao.deleteBySQL("delete from ORG_ELEMENT_ROLE_MEMBER where member=:userId " + (orgVersionUuid != null ? " and org_version_uuid=:orgVersionUuid" : ""), params);
    }

    @Override
    @Transactional
    public void deleteByMember(String member) {
        this.deleteByMemberAndOrgVersionUuid(member, null);
    }

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgElementRoleMemberEntity> listUserSameElementRoleMember(String userId, String[] orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgElementRoleMemberEntity o where o.member <> :userId and o.orgRoleUuid in (select t.orgRoleUuid from OrgElementRoleMemberEntity t where t.member = :userId and exists(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds) and v.uuid = t.orgVersionUuid))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    /**
     * 获取组织元素下对应的组织角色人员
     *
     * @param eleId
     * @param orgRoleId
     * @param orgId
     * @return
     */
    @Override
    public List<OrgElementRoleMemberEntity> listByElementIdWithOrgRoleIdAndOrgId(String eleId, String orgRoleId, String orgId) {
        Assert.hasLength(eleId, "组织元素ID不能为空！");
        Assert.hasLength(orgRoleId, "组织角色ID不能为空！");
        Assert.hasLength(orgId, "组织ID不能为空！");

        String hql = "from OrgElementRoleMemberEntity o where o.orgElementId = :orgElementId and exists(select r.uuid from OrgRoleEntity r, OrgVersionEntity v, OrganizationEntity org where r.id = :orgRoleId and org.id = :orgId and r.orgUuid = org.uuid and r.orgVersionUuid = v.uuid and v.orgUuid = org.uuid and v.state = 0 and o.orgRoleUuid = r.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", eleId);
        params.put("orgRoleId", orgRoleId);
        params.put("orgId", orgId);
        return this.listByHQL(hql, params);
    }

    @Override
    @Transactional
    public void addOrgRoleMember(List<OrgElementRoleMemberEntity> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (OrgElementRoleMemberEntity member : list) {
                List exist = dao.listByEntity(member);
                if (CollectionUtils.isEmpty(exist)) {
                    dao.save(member);
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeOrgRoleMember(List<String> userIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("userIds", userIds);
        this.dao.deleteBySQL("delete from ORG_ELEMENT_ROLE_MEMBER where member in :userIds and org_version_uuid=:orgVersionUuid", params);
    }

    @Override
    public   Long  countOrgRoleUserByOrgVersionUuid(Long orgRoleUuid, Long orgVersionUuid) {
        Map<String, Long> map = Maps.newHashMap();
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgRoleUuid",orgRoleUuid);
        Long cnt = dao.countBySQL("select count(distinct r.member) from ORG_ELEMENT_ROLE_MEMBER r where r.org_version_uuid=:orgVersionUuid and  r.org_role_uuid=:orgRoleUuid", params);

        return cnt;
    }


}
