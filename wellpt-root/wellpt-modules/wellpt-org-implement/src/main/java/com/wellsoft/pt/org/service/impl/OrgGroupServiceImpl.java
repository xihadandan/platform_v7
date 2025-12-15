package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupDaoImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupMemberDaoImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupOwnerDaoImpl;
import com.wellsoft.pt.org.dao.impl.OrgGroupRoleDaoImpl;
import com.wellsoft.pt.org.dto.OrgGroupDto;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;
import com.wellsoft.pt.org.entity.OrgGroupRoleEntity;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.org.service.OrgGroupRoleService;
import com.wellsoft.pt.org.service.OrgGroupService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
@Service
public class OrgGroupServiceImpl extends AbstractJpaServiceImpl<OrgGroupEntity, OrgGroupDaoImpl, Long> implements OrgGroupService {


    @Autowired
    OrgGroupMemberDaoImpl memberDao;

    @Autowired
    OrgGroupOwnerDaoImpl ownerDao;

    @Autowired
    OrgGroupRoleDaoImpl roleDao;

    @Autowired
    OrgGroupRoleService groupRoleService;

    @Autowired
    OrgElementI18nService orgElementI18nService;

    @Override
    @Transactional
    public Long saveOrgGroup(OrgGroupDto dto) {
        OrgGroupEntity entity = new OrgGroupEntity();
        if (dto.getUuid() == null) {
            entity.setId(IdPrefix.GROUP.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
            if (StringUtils.isNotBlank(dto.getSystem())) {
                entity.setSystem(dto.getSystem());
                entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
        } else {
            entity = getOne(dto.getUuid());
        }
        entity.setName(dto.getName());
        entity.setRemark(dto.getRemark());
        entity.setCode(dto.getCode());
        save(entity);

        memberDao.saveGroupMember(entity.getUuid(), dto.getMember(), dto.getMemberPath());
        ownerDao.saveOwner(entity.getUuid(), dto.getOwner());
        roleDao.saveRole(entity.getUuid(), dto.getRoleUuids());
        if (CollectionUtils.isNotEmpty(dto.getI18ns())) {
            for (OrgElementI18nEntity i : dto.getI18ns()) {
                i.setDataUuid(entity.getUuid());
                i.setDataId(entity.getId());
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), dto.getI18ns());
        }

        return entity.getUuid();
    }

    @Override
    public OrgGroupDto getOrgGroupDetails(Long uuid) {
        OrgGroupEntity entity = getOne(uuid);
        if (entity != null) {
            OrgGroupDto dto = new OrgGroupDto();
            dto.setUuid(uuid);
            dto.setName(entity.getName());
            dto.setCode(entity.getCode());
            dto.setId(entity.getId());
            dto.setRemark(entity.getRemark());

            List<OrgGroupMemberEntity> groupMemberEntities = memberDao.listGroupMember(entity.getUuid());

            dto.setMember(groupMemberEntities.stream().map(OrgGroupMemberEntity::getMemberId).collect(Collectors.toList()));
            dto.setMemberPath(groupMemberEntities.stream().map(OrgGroupMemberEntity::getMemberIdPath).collect(Collectors.toList()));
            dto.setOwner(ownerDao.listGroupOwnerIds(entity.getUuid()));
            dto.setRoleUuids(roleDao.listGroupRoleUuids(entity.getUuid()));

            return dto;

        }
        return null;
    }

    @Override
    @Transactional
    public void deleteOrgGroup(Long uuid) {
        delete(uuid);
        memberDao.deleteByGroupUuid(uuid);
        ownerDao.deleteByGroupUuid(uuid);
        roleDao.deleteByGroupUuid(uuid);
    }

    @Override
    public Map<String, String> getNamesByIds(List<String> ids) {
        Map<String, String> map = Maps.newLinkedHashMap();
        // 初始化
        ids.forEach(id -> map.put(id, StringUtils.EMPTY));

        List<OrgGroupEntity> entities = this.dao.listByFieldInValues("id", ids);

        // 更新名称
        entities.forEach(entity -> {
            map.put(entity.getId(), entity.getName());
        });
//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
        List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(map.keySet(), "name", LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(i18nEntities)) {
            for (OrgElementI18nEntity i : i18nEntities) {
                if (map.containsKey(i.getDataId()) && StringUtils.isNotBlank(i.getContent())) {
                    map.put(i.getDataId(), i.getContent());
                }
            }
        }
//        }
        return map;
    }

    /**
     * 获取用户相关的群组ID
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> listRelatedGroupIdByUserId(String userId) {
        Assert.hasLength(userId, "用户ID不能为空！");
        String hql = "select t1.id from OrgGroupEntity t1 where exists (select uuid from OrgGroupMemberEntity t2 where t1.uuid = t2.groupUuid and t2.memberId  = :userId )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * @param memberIds
     * @return
     */
    @Override
    public List<String> listRelatedGroupIdByMemberIds(Collection<String> memberIds) {
        Assert.notEmpty(memberIds, "成员ID列表不能为空！");

        String hql = "select t1.id from OrgGroupEntity t1 where exists (select uuid from OrgGroupMemberEntity t2 where t1.uuid = t2.groupUuid and t2.memberId  in (:memberIds) )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberIds", memberIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * 通过群组ID，获取群组成员
     *
     * @param ids
     * @return
     */
    @Override
    public Set<String> listMemberIdByIds(Collection<String> ids) {
        Assert.notEmpty(ids, "群组ID列表不能为空！");

        String hql = "select t1.memberId from OrgGroupMemberEntity t1 where" +
                " exists (select t2.uuid from OrgGroupEntity t2 where t1.groupUuid = t2.uuid and t2.id in(:ids)) order by t1.seq asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        return Sets.newLinkedHashSet(this.dao.listCharSequenceByHQL(hql, params));
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public List<OrgGroupMemberEntity> listMemberById(String groupId) {
        Assert.hasLength(groupId, "群组ID不能为空！");

        String hql = "from OrgGroupMemberEntity t1 where" +
                " exists (select t2.uuid from OrgGroupEntity t2 where t1.groupUuid = t2.uuid and t2.id = :groupId) order by t1.seq asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupId", groupId);
        return memberDao.listByHQL(hql, params);
    }

    /**
     * 根据角色UUID删除群组角色
     *
     * @param roleUuid
     */
    @Override
    @Transactional
    public void deleteGroupRoleByRoleUuid(String roleUuid) {
        groupRoleService.deleteByRoleUuid(roleUuid);
    }

    /**
     * 根据群组ID列表、角色UUID添加用户角色
     *
     * @param groupIds
     * @param roleUuid
     */
    @Override
    @Transactional
    public void addGroupRoleByIdsAndRoleUuid(List<String> groupIds, String roleUuid) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return;
        }

        List<OrgGroupRoleEntity> roleEntities = Lists.newArrayList();
        List<OrgGroupEntity> groupEntities = this.dao.listByFieldInValues("id", groupIds);
        groupEntities.forEach(groupEntity -> {
            OrgGroupRoleEntity roleEntity = new OrgGroupRoleEntity();
            roleEntity.setGroupUuid(groupEntity.getUuid());
            roleEntity.setRoleUuid(roleUuid);
            roleEntities.add(roleEntity);
        });

        if (CollectionUtils.isNotEmpty(roleEntities)) {
            groupRoleService.saveAll(roleEntities);
        }
    }

    /**
     * 根据权限角色UUID获取群组
     *
     * @param roleUuid
     * @return
     */
    @Override
    public List<OrgGroupEntity> listByRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");

        String hql = "from OrgGroupEntity t1 where exists(select t2.uuid from OrgGroupRoleEntity t2 where t2.roleUuid = :roleUuid and t2.groupUuid = t1.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    @Transactional
    public void deleteGroupRoleByIdsAndRoleUuid(List<String> groupIds, String roleUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("groupIds", groupIds);
        params.put("roleUuid", roleUuid);
        this.dao.deleteByHQL("delete from OrgGroupRoleEntity o  where " +
                "exists ( select 1 from OrgGroupEntity  e where e.id in :groupIds and e.uuid = o.groupUuid ) and o.roleUuid=:roleUuid", params);
    }

    @Override
    public List<OrgGroupEntity> listGroupsIncludeMember(Set<String> member) {
        String hql = "  from OrgGroupEntity t1 where exists (select 1 from OrgGroupMemberEntity t2 where t1.uuid = t2.groupUuid and t2.memberId in (:member) )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("member", member);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<OrgGroupEntity> listByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    public List<OrgGroupEntity> getRoleRelaGroups(String roleUuid, String system, String tenant) {
        String hql = "from OrgGroupEntity t1 where exists (select 1 from OrgGroupRoleEntity t2 where t2.roleUuid =:roleUuid and " +
                "  t1.uuid = t2.groupUuid ) and t1.system=:system and t1.tenant =:tenant ";
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        params.put("system", system);
        params.put("tenant", tenant);
        return this.dao.listByHQL(hql, params);
    }

}
