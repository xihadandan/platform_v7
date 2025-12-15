package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.org.dao.impl.BizOrgElementMemberDaoImpl;
import com.wellsoft.pt.org.dto.BizOrgElementMemberDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月03日   chenq	 Create
 * </pre>
 */
@Service
public class BizOrgElementMemberServiceImpl extends AbstractJpaServiceImpl<BizOrgElementMemberEntity, BizOrgElementMemberDaoImpl, Long>
        implements BizOrgElementMemberService {

    @Autowired
    BizOrgRoleService bizOrgRoleService;

    @Autowired
    BizOrganizationService bizOrganizationService;

    @Autowired
    BizOrgElementService bizOrgElementService;

    @Autowired
    OrgElementI18nService orgElementI18nService;


    @Override
    @Transactional
    public void addBizOrgElementMember(List<BizOrgElementMemberEntity> elementMembers) {
        Set<String> elementIds = Sets.newHashSet();
        Set<String> members = Sets.newHashSet();
        for (BizOrgElementMemberEntity m : elementMembers) {
            elementIds.add(m.getBizOrgElementId());
            members.add(m.getMemberId());
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("members", members);
        params.put("bizOrgElementIds", elementIds);
        List<BizOrgElementMemberEntity> existMembers = dao.listByHQL("from BizOrgElementMemberEntity where memberId in :members and bizOrgElementId in :bizOrgElementIds", params);
        Set<String> relas = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(existMembers)) {
            for (BizOrgElementMemberEntity m : existMembers) {
                relas.add(new StringBuilder(m.getBizOrgElementId()).append(m.getMemberId())
                        .append(StringUtils.defaultIfEmpty(m.getBizOrgRoleId(), "")).toString());
            }
        }
        List<BizOrgElementMemberEntity> list = Lists.newArrayList();
        for (BizOrgElementMemberEntity m : elementMembers) {
            if (relas.contains(new StringBuilder(m.getBizOrgElementId()).append(m.getMemberId())
                    .append(StringUtils.defaultIfEmpty(m.getBizOrgRoleId(), "")).toString())) {
                continue;
            }
            m.setSystem(RequestSystemContextPathResolver.system());
            m.setTenant(SpringSecurityUtils.getCurrentTenantId());
            list.add(m);
        }
        saveAll(list);

    }

    @Override
    @Transactional
    public void removeBizOrgElementMember(List<BizOrgElementMemberEntity> elementMembers) {
        for (BizOrgElementMemberEntity example : elementMembers) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("member", example.getMemberId());
            params.put("bizOrgElementId", example.getBizOrgElementId());
            params.put("bizOrgRoleId", example.getBizOrgRoleId());
            StringBuilder hql = new StringBuilder("delete from BizOrgElementMemberEntity where memberId =:member");
            if (StringUtils.isNotBlank(example.getBizOrgRoleId())) {
                hql.append(" and bizOrgRoleId=:bizOrgRoleId");
            }
            if (StringUtils.isNotBlank(example.getBizOrgElementId())) {
                hql.append(" and bizOrgElementId =:bizOrgElementId");
            }
            this.dao.deleteByHQL(hql.toString(), params);
        }
    }

    @Override
    public List<BizOrgElementMemberEntity> listByBizOrgElementIdAndRoleIds(String bizOrgElementId, Set<String> roleIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgElementId", bizOrgElementId);
        params.put("roleIds", roleIds);
        StringBuilder hql = new StringBuilder("from BizOrgElementMemberEntity where 1=1  ");
        if (CollectionUtils.isNotEmpty(roleIds)) {
            hql.append(" and bizOrgRoleId in :roleIds ");
        }
        if (StringUtils.isNotBlank(bizOrgElementId)) {
            hql.append(" and bizOrgElementId =:bizOrgElementId");
        }
        return this.dao.listByHQL(hql.toString(), params);
    }

    @Override
    @Transactional
    public void saveBizOrgElementMember(String bizOrgElementId, String bizOrgRoleId, List<BizOrgElementMemberEntity> elementMembers) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgElementId", bizOrgElementId);
        params.put("bizOrgRoleId", bizOrgRoleId);
        this.dao.deleteByHQL("delete from BizOrgElementMemberEntity where bizOrgElementId=:bizOrgElementId" + (StringUtils.isNotBlank(bizOrgRoleId) ? " and bizOrgRoleId=:bizOrgRoleId" : ""), params);
        this.dao.saveAll(elementMembers);
    }

    @Override
    public List<BizOrgElementMemberEntity> getAllByBizOrgUuid(Long uuid) {
        return dao.listByFieldEqValue("bizOrgUuid", uuid);
    }

    @Override
    public List<BizOrgElementMemberEntity> getAllByBizOrgElementIdsAndBizOrgUuid(List<String> bizOrgElementIds, Long bizOrgUuid) {
        if (CollectionUtils.isEmpty(bizOrgElementIds)) {
            return Collections.emptyList();
        }

        List<BizOrgElementMemberEntity> entities = Lists.newArrayList();
        String hql = "from BizOrgElementMemberEntity where bizOrgElementId in :bizOrgElementIds and bizOrgUuid =:bizOrgUuid";
        ListUtils.handleSubList(bizOrgElementIds, 1000, eleIds -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("bizOrgElementIds", eleIds);
            params.put("bizOrgUuid", bizOrgUuid);
            entities.addAll(this.listByHQL(hql, params));
        });
        return entities;
    }

    @Override
    public List<String> getAllSubMemberIdsByBizOrgElementId(String bizOrgElementId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", bizOrgElementId);
        return dao.listCharSequenceBySQL("select distinct t.member_id from biz_org_element_member t where t.biz_org_element_id =:id or  exists (" +
                " select 1 from biz_org_element_path_chain c where c.id=:id and c.sub_id = t.id " +
                ")", params);
    }

    @Override
    public List<BizOrgElementMemberDto> getDetailsByMemberId(String userId, String system) {
        BizOrgElementMemberEntity example = new BizOrgElementMemberEntity();
        example.setMemberId(userId);
        if (StringUtils.isNotBlank(system)) {
            example.setSystem(system);
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<BizOrgElementMemberEntity> memberEntities = listByEntity(example);
        List<BizOrgElementMemberDto> list = Lists.newArrayList();
        Set<String> bizRoleIds = Sets.newHashSet();
        Set<String> bizOrgEleIds = Sets.newHashSet();
        Set<Long> bizOrgUuids = Sets.newHashSet();
        Map<Long, List<BizOrgElementMemberDto>> bizOrgMap = Maps.newHashMap();
        Map<String, List<BizOrgElementMemberDto>> roleMap = Maps.newHashMap();
        Map<String, List<BizOrgElementMemberDto>> elementMap = Maps.newHashMap();

        for (BizOrgElementMemberEntity mem : memberEntities) {
            bizOrgEleIds.add(mem.getBizOrgElementId());
            bizOrgUuids.add(mem.getBizOrgUuid());
            BizOrgElementMemberDto dto = new BizOrgElementMemberDto();
            dto.setBizOrgElementId(mem.getBizOrgElementId());
            dto.setBizOrgRoleId(mem.getBizOrgRoleId());
            dto.setBizOrgUuid(mem.getBizOrgUuid());
            if (!bizOrgMap.containsKey(mem.getBizOrgUuid())) {
                bizOrgMap.put(mem.getBizOrgUuid(), Lists.newArrayList());
            }
            bizOrgMap.get(mem.getBizOrgUuid()).add(dto);
            if (StringUtils.isNotBlank(mem.getBizOrgRoleId())) {
                if (!roleMap.containsKey(mem.getBizOrgRoleId() + mem.getBizOrgUuid())) {
                    roleMap.put(mem.getBizOrgRoleId() + mem.getBizOrgUuid(), Lists.newArrayList());
                }
                roleMap.get(mem.getBizOrgRoleId() + mem.getBizOrgUuid()).add(dto);
                bizRoleIds.add(mem.getBizOrgRoleId());
            }

            if (!elementMap.containsKey(mem.getBizOrgElementId())) {
                elementMap.put(mem.getBizOrgElementId(), Lists.newArrayList());
            }
            elementMap.get(mem.getBizOrgElementId()).add(dto);
            list.add(dto);
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("id", bizRoleIds);
        params.put("bizOrgUuids", bizOrgUuids);
        if (!bizRoleIds.isEmpty()) {
            List<BizOrgRoleEntity> bizOrgRoleEntities = bizOrgRoleService.listByHQL("from BizOrgRoleEntity where id in :id and bizOrgUuid in :bizOrgUuids", params);
            for (BizOrgRoleEntity role : bizOrgRoleEntities) {
                if (roleMap.containsKey(role.getId() + role.getBizOrgUuid())) {
                    roleMap.get(role.getId() + role.getBizOrgUuid()).forEach(new Consumer<BizOrgElementMemberDto>() {
                        @Override
                        public void accept(BizOrgElementMemberDto bizOrgElementMemberDto) {
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(role.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                                if (i18nEntity != null) {
                                    role.setLocalName(i18nEntity.getContent());
                                }
                            }
                            bizOrgElementMemberDto.setBizOrgRole(role);
                        }
                    });
                }
            }
        }
        if (!bizOrgEleIds.isEmpty()) {
            List<BizOrgElementPathEntity> bizOrgElementPathEntities = bizOrgElementService.getElementPathsByBizOrgElementIds(bizOrgEleIds);
            for (BizOrgElementPathEntity entity : bizOrgElementPathEntities) {
                if (elementMap.containsKey(entity.getBizOrgElementId())) {
                    elementMap.get(entity.getBizOrgElementId()).forEach(new Consumer<BizOrgElementMemberDto>() {
                        @Override
                        public void accept(BizOrgElementMemberDto bizOrgElementMemberDto) {
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                entity.setLocalPath(bizOrgElementService.getLocaleBizElementPathById(entity.getBizOrgElementId(), LocaleContextHolder.getLocale().toString()));
                            }
                            bizOrgElementMemberDto.setBizOrgElementPath(entity);
                        }
                    });
                }
            }
        }
        if (!bizOrgUuids.isEmpty()) {
            List<BizOrganizationEntity> bizOrgRoleEntities = bizOrganizationService.listByUuids(Lists.newArrayList(bizOrgUuids));
            for (BizOrganizationEntity entity : bizOrgRoleEntities) {
                if (bizOrgMap.containsKey(entity.getUuid())) {
                    bizOrgMap.get(entity.getUuid()).forEach(new Consumer<BizOrgElementMemberDto>() {
                        @Override
                        public void accept(BizOrgElementMemberDto bizOrgElementMemberDto) {
                            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                                OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(entity.getId(), "name", LocaleContextHolder.getLocale().toString());
                                if (i18nEntity != null) {
                                    entity.setLocalName(i18nEntity.getContent());
                                }
                            }
                            bizOrgElementMemberDto.setBizOrg(entity);
                        }
                    });
                }
            }
        }

        return list;
    }

    @Override
    @Transactional
    public void deleteAllMemberByBizOrgRoleIdAndBizOrgUuid(Set<String> bizOrgRoleIds, Long bizOrgUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgRoleIds", bizOrgRoleIds);
        param.put("bizOrgUuid", bizOrgUuid);
        dao.deleteByHQL("delete from BizOrgElementMemberEntity where bizOrgUuid=:bizOrgUuid and bizOrgRoleId in :bizOrgRoleIds", param);
    }

    /**
     * @param eleIds
     * @param bizRoleIds
     * @param includeSubMemberId
     * @param includeSubItemMember
     * @return
     */
    @Override
    public List<String> listMemberIdByBizOrgElementIdsAndBizRoleIds(List<String> eleIds, List<String> bizRoleIds,
                                                                    boolean includeSubMemberId, boolean includeSubItemMember) {
        if (CollectionUtils.isEmpty(eleIds)) {
            return Collections.emptyList();
        }
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> params = Maps.newHashMap();
        params.put("eleIds", eleIds);
        params.put("bizRoleIds", bizRoleIds);
        params.put("system", system);
        StringBuilder hql = new StringBuilder("select distinct t.memberId as memberId from BizOrgElementMemberEntity t where 1 = 1 ");
        if (CollectionUtils.isNotEmpty(bizRoleIds)) {
            hql.append(" and t.bizOrgRoleId in (:bizRoleIds) ");
        }
        if (includeSubMemberId) {
            hql.append(" and (t.bizOrgElementId in (:eleIds) or exists (select 1 from BizOrgElementPathChainEntity c where c.id in (:eleIds) and c.subId = t.bizOrgElementId)) ");
        } else {
            hql.append(" and t.bizOrgElementId in (:eleIds) ");
        }
        if (!includeSubItemMember) {
            String systemCondition = " 1 = 1";
            if (StringUtils.isNotBlank(system)) {
                systemCondition += " and d.system =:system ";
            }
            hql.append(" and t.bizOrgElementId not in(select c1.subId from BizOrgElementPathChainEntity c1 where c1.id in" +
                    " (select c.subId from BizOrgElementPathChainEntity c where c.id in (:eleIds) and c.subElementType in(" +
                    "select d.id from BizOrgDimensionEntity d where " + systemCondition + ")))");
            hql.append(" and t.bizOrgElementId not in(select c.subId from BizOrgElementPathChainEntity c where c.id in (:eleIds) and c.subElementType in(" +
                    "select d.id from BizOrgDimensionEntity d where " + systemCondition + "))");
        }
        return dao.listCharSequenceByHQL(hql.toString(), params);
    }

    @Override
    public List<BizOrgElementMemberEntity> listByMemberIdAndBizOrgElementIds(String memberId, List<String> eleIds, String[] bizOrgIds) {
        Assert.notEmpty(eleIds, "业务组织元素ID列表不能为空");
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空");

        String hql = "from BizOrgElementMemberEntity t where t.memberId =:memberId and t.bizOrgElementId in (:eleIds) and t.bizOrgUuid in (select b.uuid from BizOrganizationEntity b where b.id in (:bizOrgIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", memberId);
        params.put("eleIds", eleIds);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<String> listSameBizRoleMemberId(String memberId, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空");

        String hql = "select distinct t.memberId as memberId from BizOrgElementMemberEntity t where " +
                " t.bizOrgRoleId in (select m.bizOrgRoleId from BizOrgElementMemberEntity m where m.memberId =:memberId " +
                " and m.bizOrgUuid in (select b.uuid from BizOrganizationEntity b where b.id in (:bizOrgIds))) " +
                " and t.bizOrgUuid in (select b.uuid from BizOrganizationEntity b where b.id in (:bizOrgIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", memberId);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public boolean isMemberOf(String id, String bizOrgId) {
        Assert.hasText(id, "成员ID不能为空");
        Assert.hasText(bizOrgId, "业务组织ID不能为空");

        String hql = "select count(t.uuid) from BizOrgElementMemberEntity t where t.memberId =:id and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id =:bizOrgId)";
        if (StringUtils.startsWith(id, IdPrefix.BIZ_PREFIX.getValue())) {
            hql = "select count(t.uuid) from BizOrgElementEntity t where t.id =:id and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id =:bizOrgId)";
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("bizOrgId", bizOrgId);
        Long count = this.dao.getNumberByHQL(hql, params, Long.class);
        return count > 0;
    }

    @Override
    public List<String> listMemberIdByMemberIdsAndBizOrgId(List<String> userIds, String bizOrgId) {
        Assert.hasText(bizOrgId, "业务组织ID不能为空");

        String hql = "select distinct t.memberId as memberId from BizOrgElementMemberEntity t where t.memberId in (:userIds) and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id =:bizOrgId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgId", bizOrgId);

        List<String> memberIds = Lists.newArrayList();
        ListUtils.handleSubList(userIds, 1000, ids -> {
            params.put("userIds", ids);
            memberIds.addAll(dao.listCharSequenceByHQL(hql, params));
        });
        return memberIds;
    }

    @Override
    public boolean isInBizOrgElement(String userId, List<String> bizOrgElementIds, String bizOrgId) {
        String hql = "select count(t.uuid) from BizOrgElementMemberEntity t where t.memberId =:userId " +
                " and (t.bizOrgElementId in (:eleIds) or exists (select 1 from BizOrgElementPathChainEntity c where c.id in (:eleIds) and c.subId = t.bizOrgElementId)) " +
                " and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id =:bizOrgId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("eleIds", bizOrgElementIds);
        params.put("bizOrgId", bizOrgId);
        Long count = this.dao.getNumberByHQL(hql, params, Long.class);
        return count > 0;
    }

    @Override
    public List<OrgTreeNodeDto> getUserBizOrgRolesByOrgUuid(String userId, Long orgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgUuid", orgUuid);
        params.put("userId", userId);
        params.put("locale", LocaleContextHolder.getLocale().toString());
        StringBuilder sql = new StringBuilder("select bor.name as role_name ,bor.id as biz_org_role_id, bo.id as biz_org_id, bo.name as biz_org_name ,boe.name as biz_org_element_name,boe.id as biz_org_element_id ,  boep.id_path ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            sql.append(",role_i18n.content as i_role_name ,biz_org_i18n.content as i_biz_org_name,biz_org_ele_i18n.content as i_biz_org_element_name ");
        }
        sql.append(" from biz_org_element_member m inner join biz_org_organization bo on m.biz_org_uuid= bo.uuid ");
        sql.append(" inner join org_organization o on bo.org_uuid = o.uuid and o.uuid=:orgUuid ");
        sql.append(" inner join biz_org_role bor on bor.id = m.biz_org_role_id and bor.biz_org_uuid = bo.uuid ");
        sql.append(" inner join biz_org_element boe on boe.id = m.biz_org_element_id ");
        sql.append(" inner join biz_org_element_path boep on boep.biz_org_element_id =  m.biz_org_element_id ");
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            // 关联国际化语言
            sql.append(" left join org_element_i18n role_i18n on role_i18n.data_uuid = bor.uuid and role_i18n.locale=:locale ");
            sql.append(" left join org_element_i18n biz_org_i18n on biz_org_i18n.data_uuid = bo.uuid and biz_org_i18n.locale=:locale ");
            sql.append(" left join org_element_i18n biz_org_ele_i18n on biz_org_ele_i18n.data_uuid = boe.uuid and biz_org_ele_i18n.locale=:locale ");
        }
        sql.append(" where m.member_id=:userId");
        List<QueryItem> members = this.dao.listQueryItemBySQL(sql.toString(), params, null);
        List<OrgTreeNodeDto> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(members)) {
            for (QueryItem item : members) {
                OrgTreeNodeDto dto = new OrgTreeNodeDto();
                dto.setName(StringUtils.defaultString(item.getString("iRoleName"), item.getString("roleName")));
                dto.setEleId(item.getString("bizOrgRoleId"));
                dto.setEleIdPath(StringUtils.join(new String[]{item.getString("idPath"), dto.getEleId()}, Separator.SLASH.getValue()));
                dto.setEleNamePath(dto.getName());
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    String path = bizOrgElementService.getLocaleBizElementPathById(item.getString("bizOrgElementId"), LocaleContextHolder.getLocale().toString());
                    dto.setEleNamePath(path + Separator.SLASH.getValue() + dto.getName());
                }
                list.add(dto);
            }
        }
        return list;
    }

}
