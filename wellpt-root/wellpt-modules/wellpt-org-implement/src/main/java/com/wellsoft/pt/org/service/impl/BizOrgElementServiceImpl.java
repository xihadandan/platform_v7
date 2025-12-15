package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dao.impl.*;
import com.wellsoft.pt.org.dto.BizOrgElementDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.BizOrgElementMemberService;
import com.wellsoft.pt.org.service.BizOrgElementService;
import com.wellsoft.pt.org.service.BizOrganizationService;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月28日   chenq	 Create
 * </pre>
 */
@Service
public class BizOrgElementServiceImpl extends AbstractJpaServiceImpl<BizOrgElementEntity, BizOrgElementDaoImpl, Long> implements BizOrgElementService {
    @Resource
    BizOrgElementPathDaoImpl bizOrgElementPathDao;

    @Resource
    BizOrgElementPathChainDaoImpl bizOrgElementPathChainDao;

    @Resource
    BizOrgElementRoleRelaDaoImpl bizOrgElementRoleRelaDao;

    @Resource
    BizOrgElementMemberService bizOrgElementMemberService;

    @Resource
    BizOrgRoleDaoImpl bizOrgRoleDao;

    @Resource
    RoleService roleService;

    @Resource
    UserInfoService userInfoService;

    @Resource
    BizOrganizationService bizOrganizationService;

    @Resource
    OrgElementI18nService orgElementI18nService;

    @Resource
    TranslateService translateService;

    @Resource
    AppCodeI18nService appCodeI18nService;

    @Override
    @Transactional
    public BizOrgElementEntity saveBizOrgElement(BizOrgElementDto temp) {
        BizOrgElementEntity unsaved = new BizOrgElementEntity();
        BeanUtils.copyProperties(temp, unsaved);
        BizOrgElementEntity entity = temp.getUuid() != null ? getOne(temp.getUuid()) : new BizOrgElementEntity();
        if (unsaved.getUuid() == null) {
            String idPrefix = null;
            if (!unsaved.getIsDimension()) {
                idPrefix = IdPrefix.BIZ_PREFIX.getValue();
            } else {
                idPrefix = IdPrefix.BIZ_ORG_DIM.getValue();
            }
            unsaved.setId(idPrefix + Separator.UNDERLINE.getValue() + SnowFlake.getId());
        } else {
            unsaved.setParentDimensionUuid(entity.getParentDimensionUuid());
        }
        bizOrgElementPathDao.updateBizOrgElementPath(unsaved);
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(temp, entity);
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            entity.setEnabled(true);
            entity.setId(unsaved.getId());
        } else {
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "id", "system", "tenant"));
        }
        entity.setParentDimensionUuid(unsaved.getParentDimensionUuid());
        if (temp.getRoleUuids() != null) {
            bizOrgElementRoleRelaDao.saveBizOrgElementRoleRela(entity, temp.getRoleUuids());
        }
        if (entity.getUuid() == null) {
            save(entity);
        } else {
            update(entity);
        }

        if (CollectionUtils.isNotEmpty(temp.getI18ns())) {
            for (OrgElementI18nEntity i : temp.getI18ns()) {
                i.setDataId(entity.getId());
                i.setDataUuid(entity.getUuid());
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), temp.getI18ns());
        }

        return entity;
    }

    @Override
    @Transactional
    public void deleteBizOrgElementById(String id) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        this.dao.deleteByHQL("delete from BizOrgElementEntity where id=:id", param);
        // 删除关系节点
        this.dao.deleteBySQL("delete from biz_org_element t where exists (" +
                " select 1 from biz_org_element_path_chain c where c.id=:id and c.sub_id = t.id " +
                ")", param);
        this.dao.deleteBySQL("delete from biz_org_element_path t where not exists (" +
                " select 1 from biz_org_element c where c.id=t.biz_org_element_id  " +
                ")", param);
        this.dao.deleteBySQL("delete from biz_org_element_path_chain t where not exists (" +
                " select 1 from biz_org_element c where c.id =t.id or t.sub_id = c.id " +
                ")", param);
        this.dao.deleteBySQL("delete from biz_org_element_role_rela t where not exists (" +
                " select 1 from biz_org_element c where c.id =t.biz_org_element_id   " +
                ")", param);
        this.dao.deleteBySQL("delete from biz_org_element_member t where biz_org_element_id=:id ", param);
    }

    @Override
    public BizOrgElementEntity getById(String id) {
        BizOrgElementEntity elementEntity = dao.getOneByFieldEq("id", id);
        if (elementEntity != null) {
            BizOrgElementPathEntity pathEntity = bizOrgElementPathDao.getOneByFieldEq("bizOrgElementId", id);
            elementEntity.setPathEntity(pathEntity);
        }
        return elementEntity;
    }


    public List<BizOrgElementEntity> getByUserIdAndBizOrgUuid(String userId, Long bizOrgUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgUuid", bizOrgUuid);
        param.put("memberId", userId);
        String hql = "from BizOrgElementEntity where bizOrgUuid=:bizOrgUuid and id in(select m.bizOrgElementId from BizOrgElementMemberEntity m where m.memberId=:memberId) order by parentUuid asc , seq asc";
        return dao.listByHQL(hql, param);
    }

    @Override
    public List<BizOrgElementEntity> getAllByBizOrgUuid(Long bizOrgUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgUuid", bizOrgUuid);
        return dao.listByHQL("from BizOrgElementEntity where bizOrgUuid=:bizOrgUuid order by parentUuid asc , seq asc", param);
    }


    @Override
    public Map<String, String> getNamesByIds(Set<String> ids) {
        List<String> eleIds = Lists.newArrayList();
        List<String> bizRoleIds = Lists.newArrayList();
        ids.forEach(id -> {
            if (StringUtils.contains(id, Separator.SLASH.getValue())) {
                bizRoleIds.add(id);
            } else {
                eleIds.add(id);
            }
        });

        Map<String, String> result = Maps.newHashMap();
        Map<String, Object> params = Maps.newHashMap();
        Set<String> allBizIds = Sets.newHashSet();
        // 业务元素ID
        if (CollectionUtils.isNotEmpty(eleIds)) {
            params.put("ids", ids);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL("SELECT id as id ,name as name FROM BizOrgElementEntity WHERE id in :ids", params, null);
            for (QueryItem item : queryItems) {
                result.put(item.getString("id"), item.getString("name"));
            }
        }
        allBizIds.addAll(result.keySet());

        // 业务角色ID——业务元素ID/角色ID
        if (CollectionUtils.isNotEmpty(bizRoleIds)) {
            Set<String> bizIds = Sets.newHashSet();
            Set<String> roleIds = Sets.newHashSet();
            bizRoleIds.forEach(bizRoleId -> {
                String[] bizIdParts = bizRoleId.split(Separator.SLASH.getValue());
                bizIds.add(bizIdParts[0]);
                roleIds.add(bizIdParts[1]);
            });
            allBizIds.addAll(bizIds);
            allBizIds.addAll(roleIds);
            List<BizOrgElementEntity> elementEntities = this.dao.listByFieldInValues("id", Lists.newArrayList(bizIds));
            Set<Long> bizOrgUuids = elementEntities.stream().flatMap(elementEntity -> Stream.of(elementEntity.getBizOrgUuid())).collect(Collectors.toSet());
            List<BizOrgRoleEntity> roleEntities = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(bizOrgUuids)) {
                params.put("bizOrgUuids", bizOrgUuids);
                params.put("roleIds", roleIds);
                String hql = "from BizOrgRoleEntity where bizOrgUuid in :bizOrgUuids and id in :roleIds";
                roleEntities.addAll(bizOrgRoleDao.listByHQL(hql, params));
            }
            Map<String, BizOrgElementEntity> elementEntityMap = ConvertUtils.convertElementToMap(elementEntities, "id");
            Map<String, BizOrgRoleEntity> roleEntityMap = ConvertUtils.convertElementToMap(roleEntities, "id");
            bizRoleIds.forEach(bizRoleId -> {
                String[] bizIdParts = bizRoleId.split(Separator.SLASH.getValue());
                if (elementEntityMap.containsKey(bizIdParts[0]) && roleEntityMap.containsKey(bizIdParts[1])) {
                    result.put(bizRoleId, elementEntityMap.get(bizIdParts[0]).getName() + Separator.SLASH.getValue() + roleEntityMap.get(bizIdParts[1]).getName());
                }
            });
        }

//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
        if (CollectionUtils.isNotEmpty(allBizIds)) {
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(allBizIds, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                Map<String, String> idI18nContents = Maps.newHashMap();
                for (OrgElementI18nEntity i : i18nEntities) {
                    if (StringUtils.isNotBlank(i.getContent())) {
                        idI18nContents.put(i.getDataId(), i.getContent());
                    }
                }
                for (String e : eleIds) {
                    if (idI18nContents.containsKey(e)) {
                        result.put(e, idI18nContents.get(e));
                    }
                }
                for (String e : bizRoleIds) {
                    String[] bizIdParts = e.split(Separator.SLASH.getValue());
                    String[] names = result.get(e).split(Separator.SLASH.getValue());
                    for (int i = 0, len = bizIdParts.length; i < len; i++) {
                        if (idI18nContents.containsKey(bizIdParts[i])) {
                            names[i] = idI18nContents.get(bizIdParts[i]);
                        }
                    }
                    result.put(e, StringUtils.join(names, Separator.SLASH.getValue()));
                }
            }
        }
//        }
        return result;
    }

    @Override
    public void deleteBizOrgElementByUuid(Long uuid) {
        BizOrgElementEntity bizOrgElementEntity = getOne(uuid);
        if (bizOrgElementEntity != null) {
            this.deleteBizOrgElementById(bizOrgElementEntity.getId());
        }
    }

    @Override
    public List<BizOrgElementPathEntity> getAllSubOrgElementPath(String bizOrgElementId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgElementId", bizOrgElementId);
        return bizOrgElementPathDao.listByHQL("from BizOrgElementPathEntity b where exists ( select 1 from BizOrgElementPathChainEntity c" +
                " where c.id=:bizOrgElementId and c.subId = b.bizOrgElementId )", param);
    }

    @Override
    public List<String> getBizOrgElementRelaRoleUuids(String bizOrgElementId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("bizOrgElementId", bizOrgElementId);
        return bizOrgElementRoleRelaDao.listCharSequenceByHQL("select  roleUuid from BizOrgElementRoleRelaEntity where bizOrgElementId =:bizOrgElementId", param);
    }

    @Override
    public List<TreeNode> getBizOrgElementRolePrivilegeTree(String bizOrgElementId) {
        // 当前节点的角色
        List<TreeNode> nodes = Lists.newArrayList();
        TreeNode.TreeContextHolder.remove();
        List<BizOrgElementRoleRelaEntity> roleRelas = bizOrgElementRoleRelaDao.listByFieldEqValue("bizOrgElementId", bizOrgElementId);
        if (CollectionUtils.isNotEmpty(roleRelas)) {
            for (BizOrgElementRoleRelaEntity rela : roleRelas) {
                TreeNode role = roleService.getRolePrivilegeTree(rela.getRoleUuid());
                if (role != null) {
                    nodes.add(role);
                }
            }
        }
        // 获取上一级节点关联的角色
        BizOrgElementPathEntity pathEntity = bizOrgElementPathDao.getOneByFieldEq("bizOrgElementId", bizOrgElementId);
        if (pathEntity != null) {
            String[] idPaths = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            if (idPaths.length >= 2) {
                List<TreeNode> subList = this.getBizOrgElementRolePrivilegeTree(idPaths[idPaths.length - 2]);
                if (CollectionUtils.isNotEmpty(subList)) {
                    nodes.addAll(subList);
                }
            }
        }
        return nodes;
    }


    @Override
    @Transactional
    public void deleteOrgElementPathChainByBizOrgElementIds(Set<String> bizOrgEleIds) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("ids", bizOrgEleIds);
        this.bizOrgElementPathDao.deleteByHQL("delete from BizOrgElementPathEntity where bizOrgElementId in :ids", param);
        this.bizOrgElementPathChainDao.deleteByHQL("delete from BizOrgElementPathChainEntity where id in :ids or subId in :ids", param);
    }

    @Override
    @Transactional
    public void saveBizOrgElementPathChains(List<BizOrgElementPathChainEntity> pathChainEntities) {
        this.bizOrgElementPathChainDao.saveAll(pathChainEntities);
    }

    @Override
    @Transactional
    public void saveBizOrgElementPaths(List<BizOrgElementPathEntity> pathEntities) {
        this.bizOrgElementPathDao.saveAll(pathEntities);

    }

    @Override
    @Transactional
    public void updateAllBizOrgDimensionElementType(String bizOrgDimensionId, Long bizOrgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("elementType", bizOrgDimensionId);
        params.put("bizOrgUuid", bizOrgUuid);
        this.updateByHQL("update BizOrgElementEntity set elementType=:elementType where bizOrgUuid=:bizOrgUuid and isDimension=true", params);
    }

    @Override
    public List<OrgSelectProvider.Node> getTreeByBizOrgUuid(Long bizOrgUuid, OrgSelectProvider.Params params) {
        boolean isMyDetOrgType = "MyDept".equals(params.get("orgType"));
        List<BizOrgElementEntity> list = null;
        if (isMyDetOrgType) {
            list = this.getByUserIdAndBizOrgUuid(SpringSecurityUtils.getCurrentUserId(), bizOrgUuid);
        } else {
            list = this.getAllByBizOrgUuid(bizOrgUuid);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<Long, BizOrgElementEntity> elementUuidMap = Maps.newHashMap();
        Map<String, BizOrgElementEntity> elementIdMap = Maps.newHashMap();
        Map<String, OrgSelectProvider.Node> nodeKeyMap = Maps.newHashMap();
        BizOrgConfigEntity bizOrgConfigEntity = bizOrganizationService.getBizOrgConfigByBizOrgUuid(bizOrgUuid);
        BizOrgDimensionEntity dimensionEntity = null;
        if (StringUtils.isNotBlank(bizOrgConfigEntity.getBizOrgDimensionId())) {
            dimensionEntity = bizOrganizationService.getBizOrgDimensionById(bizOrgConfigEntity.getBizOrgDimensionId());
        }

        // 构建节点树
        List<OrgSelectProvider.Node> nodes = Lists.newArrayList();
        for (BizOrgElementEntity entity : list) {
            if (BooleanUtils.isFalse(entity.getEnabled())) {
                continue;
            }
            elementUuidMap.put(entity.getUuid(), entity);
            elementIdMap.put(entity.getId(), entity);
            OrgSelectProvider.Node node = new OrgSelectProvider.Node();
            node.setTitle(entity.getName());
            node.setTitlePath(entity.getName());
            // 业务维度暂时按部门节点类型返回进行控制
            node.setType(BooleanUtils.isTrue(entity.getIsDimension()) ? OrgElementModelEntity.ORG_DEPT_ID : entity.getElementType());
            node.setKey(entity.getId());
            node.setKeyPath(entity.getId());
            node.setData(entity);
            if (dimensionEntity != null && BooleanUtils.isTrue(entity.getIsDimension())) {
                node.setIconClass(dimensionEntity.getIcon());
                node.setTypeName(dimensionEntity.getName());
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(dimensionEntity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                    if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                        node.setTypeName(i18nEntity.getContent());
                    }
                }
            }
            node.setDisabled(BooleanUtils.isFalse(entity.getEnabled()));
            nodeKeyMap.put(node.getKey(), node);

            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(
                        entity.getOrgElementId() != null ? entity.getOrgElementId() : entity.getId(), "name", LocaleContextHolder.getLocale().toString());
                if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                    node.setTitle(i18nEntity.getContent());
                    node.setTitlePath(node.getTitle());
                }

            }

        }

        for (BizOrgElementEntity entity : list) {
            if (BooleanUtils.isFalse(entity.getEnabled())) {
                continue;
            }
            if (entity.getParentUuid() == null) {
                nodes.add(nodeKeyMap.get(entity.getId()));
            } else {
                BizOrgElementEntity parentEntity = elementUuidMap.get(entity.getParentUuid());
                if (parentEntity == null) {
                    if (isMyDetOrgType) {
                        nodes.add(nodeKeyMap.get(entity.getId()));
                    }
                    continue;
                }
                OrgSelectProvider.Node parentNode = nodeKeyMap.get(parentEntity.getId());
                if (parentNode.getChildren() == null) {
                    parentNode.setChildren(Lists.newArrayList());
                }
                OrgSelectProvider.Node subNode = nodeKeyMap.get(entity.getId());
                parentNode.getChildren().add(subNode);
            }
        }


        List<BizOrgRoleEntity> roleEntities = bizOrgRoleDao.listByFieldEqValue("bizOrgUuid", bizOrgUuid);
        Map<String, BizOrgRoleEntity> bizOrgRoleEntityMap = Maps.newHashMap();
        for (BizOrgRoleEntity role : roleEntities) {
            bizOrgRoleEntityMap.put(role.getId(), role);
        }
        List<BizOrgElementMemberEntity> elementMembers = null;
        if (isMyDetOrgType) {
            List<String> eleIds = list.stream().map(BizOrgElementEntity::getId).collect(Collectors.toList());
            elementMembers = bizOrgElementMemberService.getAllByBizOrgElementIdsAndBizOrgUuid(eleIds, bizOrgUuid);
        } else {
            elementMembers = bizOrgElementMemberService.getAllByBizOrgUuid(bizOrgUuid);
        }
        // 人员过滤
        List<String> filterUserIds = (List<String>) params.get("userIds");
        // 人员身份
        Map<String, String> userJobIdentityMap = (Map<String, String>) params.get("userJobIdentityMap");
        if (userJobIdentityMap == null) {
            userJobIdentityMap = Maps.newHashMap();
        }
        if (CollectionUtils.isNotEmpty(filterUserIds)) {
            List<String> filterIds = Lists.newArrayList(filterUserIds);
            Set<String> filterEleIds = filterUserIds.stream().filter(filterUserId -> !IdPrefix.startsUser(filterUserId)).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(filterEleIds)) {
                String[] bizOrgIds = new String[]{bizOrganizationService.getOne(bizOrgUuid).getId()};
                Map<String, String> userMap = userInfoService.listBizOrgUserAsMapByUserIds(Collections.emptySet(), filterEleIds, bizOrgIds);
                filterIds.addAll(userMap.keySet());
            }
            elementMembers = elementMembers.stream().filter(elementMember -> filterIds.contains(elementMember.getMemberId())).collect(Collectors.toList());
        }
        Map<String, List<OrgSelectProvider.Node>> memberNodeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(elementMembers)) {
            Map<String, OrgSelectProvider.Node> elementRole = Maps.newHashMap();
            for (BizOrgElementMemberEntity member : elementMembers) {
                OrgSelectProvider.Node parentNode = nodeKeyMap.get(member.getBizOrgElementId());
                if (parentNode == null) {
                    continue;
                }
                if (parentNode.getChildren() == null) {
                    parentNode.setChildren(Lists.newArrayList());
                }
                if (StringUtils.isNotBlank(member.getBizOrgRoleId())) {
                    if (!elementRole.containsKey(member.getBizOrgElementId() + member.getBizOrgRoleId())) {
                        // 角色挂在业务组织节点下
                        OrgSelectProvider.Node roleNode = new OrgSelectProvider.Node();
                        // 角色key要以业务组织节点ID+角色ID返回
                        roleNode.setKey(member.getBizOrgElementId() + Separator.SLASH.getValue() + member.getBizOrgRoleId());
                        BizOrgRoleEntity role = bizOrgRoleEntityMap.get(member.getBizOrgRoleId());
                        roleNode.setTitle(role.getName());
                        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                            OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(role.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                            if (i18nEntity != null) {
                                roleNode.setTitle(i18nEntity.getContent());
                            }
                        }
                        roleNode.setParentKey(member.getBizOrgElementId());
                        roleNode.setType("bizRole");
                        elementRole.put(member.getBizOrgElementId() + member.getBizOrgRoleId(), roleNode);
                        parentNode.getChildren().add(roleNode);
                    }

                }
                if (StringUtils.isNotBlank(member.getMemberId())) {
                    OrgSelectProvider.Node memNode = new OrgSelectProvider.Node();
                    // 成员名称待更新
                    memNode.setKey(member.getMemberId());
                    memNode.setIsLeaf(true);
                    memNode.setType("user");
                    memNode.setParentKey(StringUtils.isNotBlank(member.getBizOrgRoleId()) ? member.getBizOrgRoleId() : member.getBizOrgElementId());
                    if (!memberNodeMap.containsKey(member.getMemberId())) {
                        memberNodeMap.put(member.getMemberId(), Lists.newArrayList(memNode));
                    } else {
                        memberNodeMap.get(member.getMemberId()).add(memNode);
                    }
                    if (StringUtils.isNotBlank(member.getBizOrgRoleId())) {
                        parentNode = elementRole.get(member.getBizOrgElementId() + member.getBizOrgRoleId());
                    }
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(Lists.newArrayList());
                    }
                    // 用户身份路径匹配
                    String userJobIdentity = userJobIdentityMap.get(member.getMemberId());
                    if (StringUtils.isNotBlank(userJobIdentity)) {
                        List<String> identityKeys = Arrays.asList(StringUtils.split(userJobIdentity, "; /"));
                        if (identityKeys.contains(memNode.getParentKey())) {
                            parentNode.getChildren().add(memNode);
                        }
                    } else {
                        parentNode.getChildren().add(memNode);
                    }
                }
            }

        }

        Set<String> userIds = memberNodeMap.keySet();
        if (!userIds.isEmpty()) {
            Map<String, Object> query = Maps.newHashMap();
            query.put("userIds", userIds);
            query.put("locale", LocaleContextHolder.getLocale().toString());
            List<QueryItem> userQueryItems = userInfoService.listQueryItemBySQL(
                    "select u.uuid, u.login_name ,u.gender,u.user_name ,i.user_name as i_user_name,u.pin_yin ,u.user_id,u.user_no,u.ceil_phone_number, u.mail" +
                            " from user_info u left join user_name_i18n i on i.user_id = u.user_id and i.locale=:locale where u.user_id in  :userIds", query, null);
            Map<String, QueryItem> userMap = Maps.newHashMap();
            Map<String, QueryItem> userUuidMap = Maps.newHashMap();
            for (QueryItem entity : userQueryItems) {
                userMap.put(entity.getString("userId"), entity);
                userUuidMap.put(entity.getString("uuid"), entity);
            }
            // 查询用户扩展信息
            String userAttrSql = "select attr_key,attr_value,user_uuid from USER_INFO_EXT where user_uuid in :userUuid ";
            Map<String, QueryItem> attrItems = Maps.newHashMap();
            ListUtils.handleSubList(Lists.newArrayList(userUuidMap.keySet()), 200, l -> {
                List<QueryItem> userAttrsQueryItems = userInfoService.getDao().listQueryItemBySQL(userAttrSql,
                        ImmutableMap.<String, Object>builder().put("userUuid", l).build(), null);
                if (CollectionUtils.isNotEmpty(userAttrsQueryItems)) {
                    for (QueryItem q : userAttrsQueryItems) {
                        if (!attrItems.containsKey(q.getString("userUuid"))) {
                            attrItems.put(q.getString("userUuid"), new QueryItem());
                        }
                        attrItems.get(q.getString("userUuid")).put(q.getString("attrKey"), q.getString("attrValue"), false);
                    }
                }
            });

            for (String id : userIds) {
                memberNodeMap.get(id).forEach(new Consumer<OrgSelectProvider.Node>() {
                    @Override
                    public void accept(OrgSelectProvider.Node node) {
                        node.setTitle(userMap.get(id).getString("userName"));
                        if (StringUtils.isNotBlank(userMap.get(id).getString("iUserName"))) {
                            node.setTitle(userMap.get(id).getString("iUserName"));
                        }
                        node.setShortTitle(node.getTitle());
                        Map<String, Object> data = Maps.newHashMap();
                        QueryItem u = userMap.get(id);
                        data.put("loginName", u.getString("loginName"));
                        data.put("gender", u.getInt("gender") != null ? u.getInt("gender") : "0");
                        data.put("userName", node.getTitle());
                        data.put("pinYin", u.getString("pinYin"));
                        data.put("type", "user");
                        data.put("userNo", u.getString("userNo"));
                        data.put("mail", u.getString("mail"));
                        data.put("ceilPhoneNumber", u.getString("ceilPhoneNumber"));
                        if (attrItems.containsKey(u.getString("uuid"))) {
                            data.putAll(attrItems.get(u.getString("uuid")));
                        }
                        node.setData(data);
                    }
                });

            }
        }
        for (OrgSelectProvider.Node node : nodes) {
            setTreeNodePath(node, null, nodeKeyMap);
        }

        Set<Map.Entry<String, OrgSelectProvider.Node>> entries = nodeKeyMap.entrySet();
        for (Map.Entry<String, OrgSelectProvider.Node> ent : entries) {
            if (CollectionUtils.isEmpty(ent.getValue().getChildren())) {
                ent.getValue().setIsLeaf(true);
            }
        }
        return nodes;
    }

    private void setTreeNodePath(OrgSelectProvider.Node node, OrgSelectProvider.Node parentNode, Map<String, OrgSelectProvider.Node> nodeKeyMap) {
        if (parentNode != null) {
            node.setTitlePath(parentNode.getTitlePath() + Separator.SLASH.getValue() + node.getTitle());
            if (StringUtils.contains(node.getKey(), Separator.SLASH.getValue())) {
                String path = parentNode.getKeyPath() + Separator.SLASH.getValue() + node.getKey();
                Set<String> paths = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(path, Separator.SLASH.getValue())));
                node.setKeyPath(StringUtils.join(paths, Separator.SLASH.getValue()));
            } else {
                node.setKeyPath(parentNode.getKeyPath() + Separator.SLASH.getValue() + node.getKey());
            }
            if ("user".equalsIgnoreCase(node.getType())) {
                String[] keys = node.getKeyPath().split(Separator.SLASH.getValue());
                String[] titles = node.getTitlePath().split(Separator.SLASH.getValue());
                for (int i = keys.length - 2; i >= 0; i--) {
                    if (!(keys[i].startsWith(IdPrefix.BIZ_PREFIX.getValue() + Separator.UNDERLINE.getValue())
                            || keys[i].startsWith(IdPrefix.BIZ_ORG_DIM.getValue() + Separator.UNDERLINE.getValue()))) {
                        // 业务角色
                        ((Map) node.getData()).put("bizOrgRoleId", keys[i]);
                        ((Map) node.getData()).put("bizOrgRoleName", titles[i]);
                    } else {
                        if (nodeKeyMap.containsKey(keys[i])) {
                            OrgSelectProvider.Node n = nodeKeyMap.get(keys[i]);
                            if (keys[i].startsWith(IdPrefix.BIZ_ORG_DIM.getValue() + Separator.UNDERLINE.getValue())) {
                                ((Map) node.getData()).put("bizOrgDimId", keys[i]);
                                ((Map) node.getData()).put("bizOrgDimName", titles[i]);
                            } else if (OrgElementModelEntity.ORG_DEPT_ID.equalsIgnoreCase(n.getType())) {
                                ((Map) node.getData()).put("deptId", keys[i]);
                                ((Map) node.getData()).put("deptName", titles[i]);
                            } else if (OrgElementModelEntity.ORG_UNIT_ID.equalsIgnoreCase(n.getType())) {
                                ((Map) node.getData()).put("unitId", keys[i]);
                                ((Map) node.getData()).put("unitName", titles[i]);
                            }
                        }
                    }
                }
            }

        }
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (OrgSelectProvider.Node child : node.getChildren()) {
                setTreeNodePath(child, node, nodeKeyMap);
            }
        }
    }

    @Override
    public List<BizOrgElementEntity> getAllBizDimElementsByBizOrgUuid(Long uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgUuid", uuid);
        return dao.listByHQL("from BizOrgElementEntity  where bizOrgUuid=:bizOrgUuid and isDimension=true", params);
    }

    @Override
    public List<BizOrgElementEntity> getBizOrgElementByParentUuid(Long parentUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentUuid);
        return dao.listByHQL("from BizOrgElementEntity  where " + (parentUuid == null ? " parentUuid is null" : " parentUuid = :parentUuid"), params);
    }

    @Override
    public List<BizOrgElementEntity> getBizOrgElementByParentDimensionUuid(Long parentDimensionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentDimensionUuid", parentDimensionUuid);
        return dao.listByHQL("from BizOrgElementEntity  where parentDimensionUuid = :parentDimensionUuid", params);
    }

    @Override
    @Transient
    public void deleteAllBizOrgElementPathByBizOrgUuid(Long bizOrgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgUuid", bizOrgUuid);
        bizOrgElementPathDao.deleteByHQL("delete from BizOrgElementPathEntity where bizOrgUuid=:bizOrgUuid", params);
        bizOrgElementPathChainDao.deleteByHQL("delete from BizOrgElementPathChainEntity where bizOrgUuid=:bizOrgUuid", params);
    }

    @Override
    public OrgSelectProvider.PageNode getTreeUserNodesByBizOrgUuid(Long bizOrgUuid, OrgSelectProvider.Params params) {
        PagingInfo pageInfo = null;
        Integer pageSize = params.getPageSize();
        Integer pageIndex = params.getPageIndex();
        List<OrgSelectProvider.Node> nodes = Lists.newArrayList();
        OrgSelectProvider.PageNode pageNode = new OrgSelectProvider.PageNode();
        if (pageSize != null && pageIndex != null) {
            //TODO: 分页查询
        } else {
            String bizOrgElementId = params.getOrgElementId();
            List<String> memberIds = bizOrgElementMemberService.getAllSubMemberIdsByBizOrgElementId(bizOrgElementId);
            Map<String, String> userIdNames = userInfoService.getUserNamesByUserIds(memberIds);
            for (String uid : memberIds) {
                OrgSelectProvider.Node n = new OrgSelectProvider.Node();
                n.setKey(uid);
                n.setTitle(userIdNames.get(uid));
                n.setShortTitle(n.getTitle());
                n.setType("user");

                n.setIsLeaf(true);
                nodes.add(n);
            }
            pageNode.setNodes(nodes);
            pageNode.setTotal(nodes.size());

        }
        return pageNode;
    }

    @Override
    public List<BizOrgElementPathEntity> getElementPathsByBizOrgElementIds(Set<String> bizOrgEleIds) {
        return bizOrgElementPathDao.listByFieldInValues("bizOrgElementId", Lists.newArrayList(bizOrgEleIds));
    }

    @Override
    public Set<String> getBizOrgElementAuthRoleUuidsByUserId(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("currentTime", DateUtils.truncate(new Date(), Calendar.DATE));
        return Sets.newHashSet(this.dao.listCharSequenceBySQL(" select distinct role_uuid\n" +
                "   from biz_org_element_role_rela r, biz_org_element_member m\n" +
                "  where m.member_id = :userId\n" +
                "    and (m.biz_org_element_id = r.biz_org_element_id or exists\n" +
                "         (select 1\n" +
                "            from biz_org_element_path_chain c\n" +
                "           where c.sub_id = m.biz_org_element_id\n" +
                "             and c.id = r.biz_org_element_id))\n" +
                "    and exists (select 1\n" +
                "           from biz_org_organization o\n" +
                "          where o.uuid = m.biz_org_uuid\n" +
                "            and o.enable = 1 and " +
                "( o.never_expire =1 or (o.never_expire =0 and o.expired=0 and o.expire_time >= :currentTime) ))\n", params));

    }

//    @Override
//    public List<BizOrgElementEntity> listByUserId(String userId, boolean isDimension, String[] bizOrgIds) {
//        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");
//
//        String hql = "from BizOrgElementEntity t where t.id in (select m.bizOrgElementId from BizOrgElementMemberEntity m " +
//                "where m.memberId = :memberId and m.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds)))" +
//                " and t.isDimension = :isDimension and t.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds))";
//        Map<String, Object> params = Maps.newHashMap();
//        params.put("memberId", userId);
//        params.put("isDimension", isDimension);
//        params.put("bizOrgIds", bizOrgIds);
//        return this.dao.listByHQL(hql, params);
//    }

    @Override
    public List<BizOrgElementEntity> listByUserIdAndElementTypes(String userId, List<String> elementTypes, String[] bizOrgIds) {
        Assert.notEmpty(elementTypes, "元素类型列表不能为空！");
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        String hql = "from BizOrgElementEntity t where t.id in (select m.bizOrgElementId from BizOrgElementMemberEntity m " +
                "where m.memberId = :memberId and m.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds)))" +
                " and t.elementType in (:elementTypes) and t.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", userId);
        params.put("elementTypes", elementTypes);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<BizOrgElementEntity> listParentByUserId(String userId, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        String hql = "from BizOrgElementEntity e where exists (select t.parentUuid from BizOrgElementEntity t where t.id in (select m.bizOrgElementId from BizOrgElementMemberEntity m " +
                "where m.memberId = :memberId and m.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds)))" +
                " and t.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds))" +
                " and t.parentUuid is not null and t.parentUuid = e.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", userId);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<BizOrgElementEntity> listParentByUserIdAndElementTypes(String userId, List<String> elementTypes, String[] bizOrgIds) {
        Assert.notEmpty(elementTypes, "元素类型列表不能为空！");
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        String hql = "from BizOrgElementEntity e where e.elementType in (:elementTypes) and exists (select t.parentUuid from BizOrgElementEntity t where t.id in (select m.bizOrgElementId from BizOrgElementMemberEntity m " +
                "where m.memberId = :memberId and m.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds)))" +
                " and t.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds))" +
                " and t.parentUuid is not null and t.parentUuid = e.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", userId);
        params.put("elementTypes", elementTypes);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<String> listIdPathsByUserId(String userId, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID不能为空！");

        String hql = "select t.idPath as idPath from BizOrgElementPathEntity t where t.bizOrgElementId in (select m.bizOrgElementId from BizOrgElementMemberEntity m " +
                "where m.memberId = :memberId and m.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds)))" +
                " and t.bizOrgUuid in(select b.uuid from BizOrganizationEntity b where b.id in(:bizOrgIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("memberId", userId);
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public List<BizOrgElementEntity> listByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    @Transactional
    public void resortOrgElements(List<Long> uuids) {
        BizOrgElementEntity elementEntity = getOne(uuids.get(0));
        if (elementEntity != null) {
            List<BizOrgElementEntity> list = getBizOrgElementByParentUuid(elementEntity.getParentUuid());
            Map<String, Object> params = Maps.newHashMap();
            for (BizOrgElementEntity ele : list) {
                if (uuids.contains(ele.getUuid())) {
                    params.put("seq", uuids.indexOf(ele.getUuid()) + 1);
                    params.put("uuid", ele.getUuid());
                    updateByHQL("update BizOrgElementEntity set seq =:seq where uuid=:uuid", params);
                }
            }

        }
    }

    @Override
    @Transactional
    public void resetBizOrgElementPathChains(Long bizOrgUuid) {
        if (bizOrgUuid != null) {
            bizOrgElementPathDao.resetBizOrgElementPathChains(bizOrgUuid);
        } else {
            List<BizOrganizationEntity> orgs = bizOrganizationService.listAll();
            for (BizOrganizationEntity entity : orgs) {
                bizOrgElementPathDao.resetBizOrgElementPathChains(entity.getUuid());
            }
        }
    }

    @Override
    public Long countByIdAndBizOrgId(String id, String bizOrgId) {
        String hql = "select count(*) from BizOrgElementEntity t where t.id = :id and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id = :bizOrgId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("bizOrgId", bizOrgId);
        return this.dao.countByHQL(hql, params);
    }

    @Override
    public boolean containsBizOrgElement(List<String> bizOrgElementIds, String subBizOrgElementId, String bizOrgId) {
        Assert.notEmpty(bizOrgElementIds, "上级组织元素ID列表不能为空！");
        Assert.hasLength(subBizOrgElementId, "下级组织单元ID不能为空！");
        Assert.hasLength(bizOrgId, "业务组织ID不能为空！");

        if (bizOrgElementIds.contains(subBizOrgElementId)) {
            return true;
        }

        String hql = "select count(t.uuid) from BizOrgElementPathChainEntity t where t.id in(:bizOrgElementIds) and t.subId = :subBizOrgElementId and t.bizOrgUuid = (select b.uuid from BizOrganizationEntity b where b.id = :bizOrgId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgElementIds", bizOrgElementIds);
        params.put("subBizOrgElementId", subBizOrgElementId);
        params.put("bizOrgId", bizOrgId);
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    public String getLocaleBizElementPathById(String bizOrgElementId, String locale) {
        BizOrgElementPathEntity pathEntity = bizOrgElementPathDao.getOneByFieldEq("bizOrgElementId", bizOrgElementId);
        if (pathEntity != null) {
            String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            String preSystem = RequestSystemContextPathResolver.system();
            RequestSystemContextPathResolver.setSystem(pathEntity.getSystem());
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(Sets.newHashSet(ids), "name", locale);
            RequestSystemContextPathResolver.setSystem(preSystem);
            String[] paths = pathEntity.getCnPath().split(Separator.SLASH.getValue());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (OrgElementI18nEntity i18n : i18nEntities) {
                    int i = ArrayUtils.indexOf(ids, i18n.getDataId());
                    if (i > -1 && StringUtils.isNotBlank(i18n.getContent())) {
                        paths[i] = i18n.getContent();
                    }
                }
            }
            return StringUtils.join(paths, Separator.SLASH.getValue());

        }
        return null;
    }

    @Override
    public Map<String, String> getNamePathsByIds(List<String> ids) {

        List<String> eleIds = Lists.newArrayList();
        List<String> bizRoleIds = Lists.newArrayList();
        ids.forEach(id -> {
            if (StringUtils.contains(id, Separator.SLASH.getValue())) {
                bizRoleIds.add(id);
            } else {
                eleIds.add(id);
            }
        });

        Map<String, String> result = Maps.newHashMap();
        Map<String, Object> params = Maps.newHashMap();
        Set<String> allBizIds = Sets.newHashSet();
        Map<String, String> idPaths = Maps.newHashMap();
        // 业务元素ID
        if (CollectionUtils.isNotEmpty(eleIds)) {
            params.put("ids", ids);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL("SELECT b.id as id ,b.name as name,p.idPath ,p.cnPath FROM BizOrgElementEntity b, BizOrgElementPathEntity p" +
                    " WHERE b.id in :ids and b.id =p.bizOrgElementId ", params, null);
            for (QueryItem item : queryItems) {
                result.put(item.getString("id"), item.getString("cnPath"));
                allBizIds.addAll(Arrays.asList(item.getString("idPath").split(Separator.SLASH.getValue())));
                idPaths.put(item.getString("id"), item.getString("idPath"));
            }
        }
        allBizIds.addAll(result.keySet());

        // 业务角色ID——业务元素ID/角色ID
        if (CollectionUtils.isNotEmpty(bizRoleIds)) {
            Set<String> bizIds = Sets.newHashSet();
            Set<String> roleIds = Sets.newHashSet();
            bizRoleIds.forEach(bizRoleId -> {
                String[] bizIdParts = bizRoleId.split(Separator.SLASH.getValue());
                bizIds.add(bizIdParts[0]);
                roleIds.add(bizIdParts[1]);
            });
            allBizIds.addAll(bizIds);
            allBizIds.addAll(roleIds);
            List<BizOrgElementEntity> elementEntities = this.dao.listByFieldInValues("id", Lists.newArrayList(bizIds));
            Set<Long> bizOrgUuids = elementEntities.stream().flatMap(elementEntity -> Stream.of(elementEntity.getBizOrgUuid())).collect(Collectors.toSet());
            List<BizOrgRoleEntity> roleEntities = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(bizOrgUuids)) {
                params.put("bizOrgUuids", bizOrgUuids);
                params.put("roleIds", roleIds);
                String hql = "from BizOrgRoleEntity where bizOrgUuid in :bizOrgUuids and id in :roleIds";
                roleEntities.addAll(bizOrgRoleDao.listByHQL(hql, params));
            }
            Map<String, BizOrgElementEntity> elementEntityMap = ConvertUtils.convertElementToMap(elementEntities, "id");
            Map<String, BizOrgRoleEntity> roleEntityMap = ConvertUtils.convertElementToMap(roleEntities, "id");
            bizRoleIds.forEach(bizRoleId -> {
                String[] bizIdParts = bizRoleId.split(Separator.SLASH.getValue());
                if (elementEntityMap.containsKey(bizIdParts[0]) && roleEntityMap.containsKey(bizIdParts[1])) {
                    result.put(bizRoleId, elementEntityMap.get(bizIdParts[0]).getName() + Separator.SLASH.getValue() + roleEntityMap.get(bizIdParts[1]).getName());
                }
            });
        }

//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
        if (CollectionUtils.isNotEmpty(allBizIds)) {
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(allBizIds, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                Map<String, String> idI18nContents = Maps.newHashMap();
                for (OrgElementI18nEntity i : i18nEntities) {
                    if (StringUtils.isNotBlank(i.getContent())) {
                        idI18nContents.put(i.getDataId(), i.getContent());
                    }
                }
                for (String e : eleIds) {
                    if (idI18nContents.containsKey(e)) {
                        String[] name = result.get(e).split(Separator.SLASH.getValue());
                        String[] id = idPaths.get(e).split(Separator.SLASH.getValue());
                        for (int i = 0, len = id.length; i < len; i++) {
                            if (idI18nContents.containsKey(id[i])) {
                                name[i] = idI18nContents.get(id[i]);
                            }
                        }
                        result.put(e, StringUtils.join(name, Separator.SLASH.getValue()));
                    }
                }
                for (String e : bizRoleIds) {
                    if (!result.containsKey(e)) {
                        continue;
                    }
                    String[] bizIdParts = e.split(Separator.SLASH.getValue());
                    String[] names = result.get(e).split(Separator.SLASH.getValue());
                    for (int i = 0, len = bizIdParts.length; i < len; i++) {
                        if (idI18nContents.containsKey(bizIdParts[i])) {
                            names[i] = idI18nContents.get(bizIdParts[i]);
                        }
                    }
                    result.put(e, StringUtils.join(names, Separator.SLASH.getValue()));
                }
            }
        }
//        }
        return result;
    }

    @Override
    @Transactional
    public void translateAllElements(Long bizOrgUuid, Boolean onlyTranslateEmpty) {
        List<BizOrgElementEntity> orgElementEntities = dao.listByFieldEqValue("bizOrgUuid", bizOrgUuid);
        List<OrgElementI18nEntity> i18nEntities = Lists.newArrayList();
        Set<String> locales = appCodeI18nService.getAllLocaleString();
        if (CollectionUtils.isNotEmpty(orgElementEntities)) {
            if (BooleanUtils.isTrue(onlyTranslateEmpty)) {
                Map<Long, BizOrgElementEntity> uuidEntitys = Maps.newHashMap();
                Set<String> allNames = Sets.newHashSet();
                for (BizOrgElementEntity entity : orgElementEntities) {
                    uuidEntitys.put(entity.getUuid(), entity);
                    allNames.add(entity.getName());
                }
                Set<Long> uuids = uuidEntitys.keySet();
                for (String locale : locales) {
                    if (locale.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.toString())) {
                        continue;
                    }
                    List<OrgElementI18nEntity> nameI18ns = orgElementI18nService.listOrgElementI18ns(uuids, "name", locale);
                    Set<String> names = Sets.newHashSet(allNames);
                    Set<Long> lackNameUuids = Sets.newHashSet(uuids);
                    if (CollectionUtils.isNotEmpty(nameI18ns)) {
                        for (OrgElementI18nEntity i : nameI18ns) {
                            if (lackNameUuids.contains(i.getDataUuid())) {
                                lackNameUuids.remove(i.getDataUuid());
                                names.remove(uuidEntitys.get(i.getDataUuid()).getName());
                            }
                        }

                    }
                    if (!names.isEmpty()) {
                        Map<String, String> result = translateService.translate(names, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                        for (Long uid : lackNameUuids) {
                            BizOrgElementEntity entity = uuidEntitys.get(uid);
                            if (result.containsKey(entity.getName().trim())) {
                                i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "name", locale, result.get(entity.getName().trim())));
                            }
                        }

                    }
                }
            } else {
                Set<String> names = Sets.newHashSet();
                for (BizOrgElementEntity entity : orgElementEntities) {
                    names.add(entity.getName());
                }
                for (String locale : locales) {
                    if (locale.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.toString())) {
                        continue;
                    }
                    Map<String, String> result = translateService.translate(names, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                    for (BizOrgElementEntity entity : orgElementEntities) {
                        orgElementI18nService.deleteByDataUuid(entity.getUuid());
                        if (result.containsKey(entity.getName().trim())) {
                            i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "name", locale, result.get(entity.getName().trim())));
                        }

                    }
                }
            }
            if (!i18nEntities.isEmpty()) {
                orgElementI18nService.saveAll(i18nEntities);
            }
        }
    }

}
