package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dao.OrgElementExtAttrDao;
import com.wellsoft.pt.org.dao.impl.OrgElementDaoImpl;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementServiceImpl extends AbstractJpaServiceImpl<OrgElementEntity, OrgElementDaoImpl, Long> implements OrgElementService {


    @Resource
    OrgElementPathChainService orgElementPathChainService;

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    OrgElementExtAttrDao orgElementExtAttrDao;

    @Resource
    OrgElementManagementService orgElementManagementService;

    @Resource
    OrgElementRoleMemberService orgElementRoleMemberService;

    @Autowired
    List<OrgSelectProvider> orgSelectProviders;

    @Autowired
    MultiOrgJobDutyService multiOrgJobDutyService;

    @Autowired
    OrgElementRoleRelaService orgElementRoleRelaService;

    @Autowired
    OrgUserService orgUserService;

    @Autowired
    RoleService roleService;

    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    OrgVersionService orgVersionService;

    @Autowired
    OrgElementI18nService orgElementI18nService;
    @Autowired
    AppCodeI18nService appCodeI18nService;
    @Autowired
    TranslateService translateService;
    @Autowired
    BizOrgElementService bizOrgElementService;
    @Autowired
    OrgGroupRoleService orgGroupRoleService;


    @Override
    @Transactional
    public Long saveOrgElement(OrgElementDto temp) {
        OrgElementEntity entity = temp.getUuid() != null ? getOne(temp.getUuid()) : new OrgElementEntity();
        Long oldParentUuid = entity.getParentUuid();
        String oldElementName = null;
        if (temp.getUuid() != null) {
            oldElementName = entity.getName();
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "system", "tenant"));
        } else {
            BeanUtils.copyProperties(temp, entity, entity.BASE_FIELDS);
            OrgVersionEntity orgVersionEntity = orgVersionService.getOne(temp.getOrgVersionUuid());
            if (orgVersionEntity != null) {
                entity.setSystem(orgVersionEntity.getSystem());
                entity.setTenant(orgVersionEntity.getTenant());
            }
        }

        if (temp.getUuid() == null && StringUtils.isBlank(temp.getId())) {
            String idPrefix = IdPrefix.EXTEND_ELEMENT.getValue();
            if (OrgElementModelEntity.ORG_UNIT_ID.equalsIgnoreCase(temp.getType())) {
                idPrefix = IdPrefix.SYSTEM_UNIT.getValue();
            } else if (OrgElementModelEntity.ORG_DEPT_ID.equalsIgnoreCase(temp.getType())) {
                idPrefix = IdPrefix.DEPARTMENT.getValue();
            } else if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(temp.getType())) {
                idPrefix = IdPrefix.JOB.getValue();
            } else if (OrgElementModelEntity.ORG_CLASSIFY_ID.equalsIgnoreCase(temp.getType())) {
                idPrefix = IdPrefix.CATEGORY.getValue();
            }
            entity.setId(idPrefix + Separator.UNDERLINE.getValue() + SnowFlake.getId());
        }
        try {
            save(entity);
            dao.getSession().flush();
            if (temp.getUuid() == null || (temp.getUuid() != null && ObjectUtils.notEqual(temp.getParentUuid(), oldParentUuid))) {
                int seq = 1;
                if (temp.getUuid() == null && temp.getSeq() != null) { // 前端设置的递增排序号，非直接设置排序号值
                    seq += temp.getSeq();
                }
                dao.updateBySQL("update org_element set seq = ( select nvl(max(o.seq),0) + " + seq + " from org_element o where "
                                + (temp.getParentUuid() == null ? " o.parent_uuid is null " : " o.parent_uuid=:parentUuid  ") + " and o.org_version_uuid=:orgVersionUuid ) where uuid=:uuid ",
                        ImmutableMap.<String, Object>builder()
                                .put("parentUuid", temp.getParentUuid() == null ? "" : temp.getParentUuid())
                                .put("uuid", entity.getUuid())
                                .put("orgVersionUuid", entity.getOrgVersionUuid()).build());
            }
        } catch (ConstraintViolationException e) {

            if (e.getConstraintName().endsWith("UNQ_ORG_NAME_PARENT_UUID")) {
//                String typeName = "数据";
//                if (OrgElementModelEntity.ORG_UNIT_ID.equalsIgnoreCase(temp.getType())) {
//                    typeName = "单位";
//                } else if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(temp.getType())) {
//                    typeName = "职位";
//                } else if (OrgElementModelEntity.ORG_DEPT_ID.equalsIgnoreCase(temp.getType())) {
//                    typeName = "部门";
//                } else if (OrgElementModelEntity.ORG_CLASSIFY_ID.equalsIgnoreCase(temp.getType())) {
//                    typeName = "分类";
//                }
                throw new RuntimeException(temp.getName() + "已存在");
            }
            throw new RuntimeException("数据违反约束性规则");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 更新职务
        if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(entity.getType())) {
            multiOrgJobDutyService.saveUpdateJobDutyByJobIdAndOrgVersionUuid(entity.getId(), temp.getJobDuty() != null ? temp.getJobDuty().getDutyId() : null, entity.getOrgVersionUuid());
        }

        // 组织单元管理信息保存
        if (temp.getManagement() != null) {
            OrgElementManagementEntity managementEntity = temp.getUuid() != null ?
                    orgElementManagementService.getByOrgElementUuid(temp.getUuid()) : new OrgElementManagementEntity();
            if (managementEntity == null) {
                managementEntity = new OrgElementManagementEntity();
            }
            BeanUtils.copyProperties(temp.getManagement(), managementEntity, managementEntity.BASE_FIELDS);
            managementEntity.setOrgElementUuid(entity.getUuid());
            managementEntity.setOrgVersionUuid(entity.getOrgVersionUuid());
            managementEntity.setOrgElementId(entity.getId());
            managementEntity.setSystem(entity.getSystem());
            managementEntity.setTenant(entity.getTenant());
            orgElementManagementService.save(managementEntity);
        }


        orgElementPathChainService.saveOrUpdatePathChainByOrgElementUuid(entity.getUuid(), entity.getParentUuid(), oldParentUuid, oldElementName);

        orgElementRoleRelaService.saveOrgElementRoleRela(entity.getUuid(), temp.getRoleUuids(), entity.getOrgVersionUuid());

        if (CollectionUtils.isNotEmpty(temp.getI18ns())) {
            for (OrgElementI18nEntity e : temp.getI18ns()) {
                e.setDataUuid(entity.getUuid());
                if (entity.getState().equals(OrgVersionEntity.State.PUBLISHED)) {
                    // 只有正式版才会设置元素ID（正式版使用时候会根据元素ID查询，避免查到两个）
                    e.setDataId(entity.getId());
                }
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), temp.getI18ns());
        } else if (temp.getUuid() == null && OrgElementModelEntity.ORG_UNIT_ID.equalsIgnoreCase(temp.getType())) {
            // 拷贝单位数据的国际化数据
            List<OrgElementI18nEntity> unitI18ns = orgElementI18nService.getOrgElementI18ns(temp.getSourceId());
            if (CollectionUtils.isNotEmpty(unitI18ns)) {
                List<OrgElementI18nEntity> i18ns = Lists.newArrayList();
                for (OrgElementI18nEntity ui : unitI18ns) {
                    OrgElementI18nEntity i18n = new OrgElementI18nEntity();
                    if (entity.getState().equals(OrgVersionEntity.State.PUBLISHED)) {
                        i18n.setDataId(temp.getId());
                    }
                    i18n.setDataUuid(entity.getUuid());
                    i18n.setDataCode(ui.getDataCode());
                    i18n.setContent(ui.getContent());
                    i18n.setLocale(ui.getLocale());
                    i18ns.add(i18n);
                }
                orgElementI18nService.saveAll(i18ns);
            }
        }
        return entity.getUuid();
    }

    @Override
    @Transactional
    public void deleteOrgElement(Long uuid) {
        OrgElementEntity entity = getOne(uuid);
        if (entity != null) {
            // 获取所有的下级节点一并删除
            List<OrgElementPathChainEntity> subordinates = orgElementPathChainService.getAllSubordinate(entity.getOrgVersionUuid(), entity.getId());
            List<String> ids = Lists.newArrayList();
            List<Long> uuids = Lists.newArrayList(uuid);
            for (OrgElementPathChainEntity chainEntity : subordinates) {
                ids.add(chainEntity.getSubOrgElementId());
            }
            ids.add(entity.getId());

            orgElementRoleRelaService.deleteByOrgElementIdsAndOrgVersionUuid(ids, entity.getOrgVersionUuid());

            // 删除用户职位信息
            OrgElementPathEntity elementPathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(entity.getId(), entity.getOrgVersionUuid());
            if (elementPathEntity != null) {
                orgUserService.deleteAllOrgUserJobByOrgVersionUuidAndElementPathPrefix(entity.getOrgVersionUuid(), elementPathEntity.getIdPath());
            }
            // 组织元素下的用户路径更新为上级节点路径
            if (entity.getParentUuid() != null) {
                OrgElementPathEntity parentElementPath = orgElementPathService.getByOrgEleUuid(entity.getParentUuid());
                orgUserService.updateAllOrgUserByOrgVersionUuidAndElementPathPrefix(entity.getOrgVersionUuid(), elementPathEntity.getIdPath(), parentElementPath);
            } else {
                orgUserService.updateAllOrgUserByOrgVersionUuidAndElementPathPrefix(entity.getOrgVersionUuid(), elementPathEntity.getIdPath(), null);
            }

            // 删除组织单元实例路径
            orgElementPathService.deleteByIdsAndOrgVersionUuid(ids, entity.getOrgVersionUuid());

            // 删除组织单元实例
            dao.deleteByIdsAndOrgVersionUuid(ids, entity.getOrgVersionUuid());

            // 删除关系链
            orgElementPathChainService.deleteBySubOrgEleIdsAndOrgVersionUuid(ids, entity.getOrgVersionUuid());


            // 删除组织单元实例的管理信息
            orgElementManagementService.deleteByOrgElementIdsAndOrgVersionUuid(ids, entity.getOrgVersionUuid());

            orgElementI18nService.deleteByDataId(entity.getId());

            // 删除职务
            if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(entity.getType())) {
                multiOrgJobDutyService.deleteByJobIdAndOrgVersionUuid(entity.getId(), entity.getOrgVersionUuid());
            }

            if (entity.getParentUuid() != null) {
                // 重排序
                List<OrgElementEntity> elementEntities = listByParentUuidAndOrgVersionUuid(entity.getParentUuid(), entity.getOrgVersionUuid());
                if (elementEntities.size() > 1) {
                    int seq = 1;
                    Iterator<OrgElementEntity> iterator = elementEntities.iterator();
                    while ((iterator.hasNext())) {
                        OrgElementEntity el = iterator.next();
                        if (!uuid.equals(el.getUuid())) {
                            el.setSeq(seq++);
                        } else {
                            iterator.remove();
                        }
                    }
                    if (CollectionUtils.isNotEmpty(elementEntities)) {
                        saveAll(elementEntities);
                    }
                } else {
                    // 父级下无节点了，则更新父节点为末级
                    OrgElementPathEntity parentPathEntity = orgElementPathService.getByOrgEleUuid(entity.getParentUuid());
                    if (parentPathEntity != null) {
                        parentPathEntity.setLeaf(true);
                        orgElementPathService.save((parentPathEntity));
                    }
                }
            }
        }
    }

    private List<OrgElementEntity> listByParentUuidAndOrgVersionUuid(Long parentUuid, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentUuid);
        params.put("orgVersionUuid", orgVersionUuid);
        return listByHQL("from OrgElementEntity where "
                        + (parentUuid == null ? "parentUuid is null" : "parentUuid=:parentUuid") + " and orgVersionUuid = :orgVersionUuid order by seq asc "
                , params);
    }

    @Override
    public List<OrgElementEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    public List<OrgElementExtAttrEntity> listExtAttrsByOrgVersionUuid(Long orgVersionUuid) {
        return orgElementExtAttrDao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    public void saveExtAttrs(List<OrgElementExtAttrEntity> waitSaveExtAttrs) {
        orgElementExtAttrDao.saveAll(waitSaveExtAttrs);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete from OrgElementEntity where orgVersionUuid=:orgVersionUuid", params);
        orgElementExtAttrDao.deleteByHQL("delete from OrgElementExtAttrEntity where orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public void updateStateByOrgVersionUuid(OrgVersionEntity.State state, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("state", state);
        dao.updateByHQL("update OrgElementEntity set state=:state  where orgVersionUuid=:orgVersionUuid", params);
        dao.updateByHQL("update OrgElementI18nEntity e set e.dataId = null where exists ( select 1 from OrgElementEntity o where o.uuid=e.dataUuid and o.orgVersionUuid=:orgVersionUuid)", params);
    }

    @Override
    public OrgElementDto getDetails(Long uuid) {
        OrgElementEntity entity = getOne(uuid);
        if (entity != null) {
            OrgElementDto dto = new OrgElementDto();
            BeanUtils.copyProperties(entity, dto);

            // 查询管理信息
            dto.setManagement(orgElementManagementService.getByOrgElementUuid(entity.getUuid()));
            // 查询组织单元角色成员信息
            dto.setOrgElementRoleMembers(orgElementRoleMemberService.listByOrgVersionUuidAndOrgElementId(entity.getOrgVersionUuid(), entity.getId()));
            // 查询职务
            if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(entity.getType())) {
                dto.setJobDuty(multiOrgJobDutyService.getJobDutyDetailsByJobIdAndOrgVersionUuid(entity.getId(), entity.getOrgVersionUuid()));
            }
            // 查询关联的角色
            List<OrgElementRoleRelaEntity> relaEntities = orgElementRoleRelaService.listByOrgElementUuid(entity.getUuid());
            if (CollectionUtils.isNotEmpty(relaEntities)) {
                List<String> roleUuids = Lists.newArrayListWithCapacity(relaEntities.size());
                for (OrgElementRoleRelaEntity rela : relaEntities) {
                    roleUuids.add(rela.getRoleUuid());
                }
                dto.setRoleUuids(roleUuids);
            }
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(entity.getUuid());
            dto.setI18ns(i18nEntities);
            return dto;
        }
        return null;
    }

    @Override
    public List<OrgElementEntity> listByIdsAndOrgVersionId(String[] ids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("ids", ids);
        if (orgVersionUuid == null) {
            // 查找正式版的组织单元实例
            params.put("state", OrgVersionEntity.State.PUBLISHED);
            params.put("system", RequestSystemContextPathResolver.system());
            params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            return this.dao.listByHQL("from OrgElementEntity o where o.id in (:ids) and exists (" +
                    " select 1 from OrgVersionEntity v where v.uuid = o.orgVersionUuid and v.state =:state and v.system=:system and v.tenant=:tenant" +
                    ")", params);

        }
        return this.dao.listByHQL("from OrgElementEntity where id in (:ids) and orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public OrgElementEntity getByIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("id", orgElementId);
        List<OrgElementEntity> list = this.dao.listByHQL("from OrgElementEntity where id=:id and orgVersionUuid=:orgVersionUuid", params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<OrgElementEntity> listByOrgVersionUuidAndParentUuidIsNull(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listByHQL("from OrgElementEntity o where orgVersionUuid=:orgVersionUuid and parentUuid is null order by seq asc", params);
    }

    @Override
    public List<OrgSelectProvider.Node> getTree(String type, OrgSelectProvider.Params params) {
        for (OrgSelectProvider provider : orgSelectProviders) {
            if (type != null && type.equalsIgnoreCase(provider.type())) {
                return provider.fetch(params);
            }
        }
        logger.error("不存在类型为{}的组织树服务提供", type);
        return null;
    }

    @Override
    public OrgSelectProvider.PageNode getTreeUserNodes(String type, OrgSelectProvider.Params params) {
        for (OrgSelectProvider provider : orgSelectProviders) {
            if (type != null && type.equalsIgnoreCase(provider.type())) {
                return provider.fetchUser(params);
            }
        }
        logger.error("不存在类型为{}的组织树服务提供", type);
        return null;
    }

    @Override
    public List<OrgSelectProvider.Node> getTreeNodesByKeys(String type, OrgSelectProvider.Params params) {
        List<OrgSelectProvider.Node> results = Lists.newArrayList();
        for (OrgSelectProvider provider : orgSelectProviders) {
            if (type != null && type.equalsIgnoreCase(provider.type())) {
                return provider.fetchByKeys(params);
            } else if (type == null) {
                // 依次匹配所有的组织数据提供器，返回节点
                List<OrgSelectProvider.Node> subNodes = provider.fetchByKeys(params);
                if (CollectionUtils.isNotEmpty(subNodes)) {
                    results.addAll(subNodes);
                    if (CollectionUtils.isEmpty(params.getKeys())) {
                        return results;
                    }
                }
            }
        }
        logger.error("不存在类型为{}的组织树服务提供", type);
        return results;
    }

    @Override
    public Map<String, String> getNamesByIds(List<String> ids) {
        Map<String, String> map = Maps.newLinkedHashMap();
        // 初始化
        ids.forEach(id -> map.put(id, StringUtils.EMPTY));

        List<OrgElementEntity> entities = this.dao.listByFieldInValues("id", ids);
        // 按创建时间升序
        Collections.sort(entities, (o1, o2) -> {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        });

        // 更新名称
        entities.forEach(entity -> {
            map.put(entity.getId(), entity.getName());
        });

//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(map.keySet(), "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                for (OrgElementI18nEntity i : i18nEntities) {
                    if (map.containsKey(i.getDataId()) && StringUtils.isNotBlank(i.getContent())) {
                        map.put(i.getDataId(), i.getContent());
                    }
                }
            }
        }
//        }
        return map;
    }

    @Override
    public Map<String, String> getNamePathsByIds(List<String> ids) {
        Map<String, String> map = Maps.newLinkedHashMap();
        // 初始化
        ids.forEach(id -> map.put(id, StringUtils.EMPTY));

        List<OrgElementPathEntity> pathEntities = this.orgElementPathService.listByOrgEleIds(ids);
        // 按创建时间升序
        Collections.sort(pathEntities, (o1, o2) -> {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        });

        // 更新名称
        List<Long> uuids = Lists.newArrayList();
        pathEntities.forEach(entity -> {
            map.put(entity.getOrgElementId(), entity.getCnPath());
            uuids.add(entity.getUuid());
        });

        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<Long, String> uuidNamePaths = orgElementPathService.getLocaleOrgElementPaths(uuids, LocaleContextHolder.getLocale().toString());
            List<OrgElementEntity> entities = this.dao.listByFieldInValues("id", ids);
            if (MapUtils.isNotEmpty(uuidNamePaths)) {
                entities.forEach(entity -> {
                    if (uuidNamePaths.containsKey(entity.getUuid())) {
                        map.put(entity.getId(), uuidNamePaths.get(entity.getUuid()));
                    }
                });
            }
        }
        return map;
    }

    /**
     * 通过组织元素ID,获取该节点下的所有职位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgElementEntity> listJobElementInIds(List<String> ids, String[] orgVersionIds) {
        Assert.notEmpty(ids, "组织元素ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID不能为空！");

        String hql = "from OrgElementEntity t where t.type = :type and t.orgVersionId in(:orgVersionIds) and (t.id in(:eleIds) or exists(select p.uuid from OrgElementPathChainEntity p where p.orgElementId in(:eleIds) and t.id = p.subOrgElementId))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", OrgElementModelEntity.ORG_JOB_ID);
        params.put("eleIds", ids);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    /**
     * 通过组织元素ID,获取该节点下的所有部门ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgElementEntity> listDepartmentElementInIds(List<String> ids, String[] orgVersionIds) {
        Assert.notEmpty(ids, "组织元素ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID不能为空！");

        String hql = "from OrgElementEntity t where t.type = :type and t.orgVersionId in(:orgVersionIds) and (t.id in(:eleIds) " +
                "or exists(select p.uuid from OrgElementPathChainEntity p where p.orgElementId in(:eleIds) and t.id = p.subOrgElementId " +
                " and p.orgVersionUuid in(select uuid from OrgVersionEntity v where v.id in(:orgVersionIds)) ))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", OrgElementModelEntity.ORG_DEPT_ID);
        params.put("eleIds", ids);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    /**
     * 通过组织元素ID,获取该节点下的所有单位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgElementEntity> listUnitElementInIds(List<String> ids, String[] orgVersionIds) {
        Assert.notEmpty(ids, "组织元素ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID不能为空！");

        String hql = "from OrgElementEntity t where t.type = :type and t.orgVersionId in(:orgVersionIds) and (t.id in(:eleIds) " +
                "or exists(select p.uuid from OrgElementPathChainEntity p where p.orgElementId in(:eleIds) and t.id = p.subOrgElementId " +
                "and p.orgVersionUuid in(select uuid from OrgVersionEntity v where v.id in(:orgVersionIds)) ))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", OrgElementModelEntity.ORG_UNIT_ID);
        params.put("eleIds", ids);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgElementEntity> listOrgElementInIds(List<String> ids, String[] orgVersionIds) {
        Assert.notEmpty(ids, "组织元素ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID不能为空！");

        String hql = "from OrgElementEntity t where t.orgVersionId in(:orgVersionIds) and (t.id in(:eleIds) " +
                "or exists(select p.uuid from OrgElementPathChainEntity p where p.orgElementId in(:eleIds) and t.id = p.subOrgElementId " +
                "and p.orgVersionUuid in(select uuid from OrgVersionEntity v where v.id in(:orgVersionIds)) ))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("eleIds", ids);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    /**
     * 通过组织元素ID列表,获取组织元素
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgElementEntity> listElementByIds(List<String> ids, String[] orgVersionIds) {
        Assert.notEmpty(ids, "组织元素ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID不能为空！");

        String hql = "from OrgElementEntity t where t.orgVersionId in(:orgVersionIds) and t.id in(:eleIds)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("eleIds", ids);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<TreeNode> getOrgElementRolePrivilegeTree(Long orgElementUuid) {
        // 当前节点的角色
        List<TreeNode> nodes = Lists.newArrayList();
        TreeNode.TreeContextHolder.remove();
        List<OrgElementRoleRelaEntity> roleRelas = orgElementRoleRelaService.listByOrgElementUuid(orgElementUuid);
        if (CollectionUtils.isNotEmpty(roleRelas)) {
            for (OrgElementRoleRelaEntity rela : roleRelas) {
                TreeNode role = roleService.getRolePrivilegeTree(rela.getRoleUuid());
                if (role != null) {
                    nodes.add(role);
                }
            }
        }
        OrgElementEntity orgElementEntity = getOne(orgElementUuid);
        Set<String> roleUuids = orgGroupRoleService.queryRoleUuidsByGroupMemberIds(Lists.newArrayList(orgElementEntity.getId()));
        if (CollectionUtils.isNotEmpty(roleUuids)) {
            for (String r : roleUuids) {
                TreeNode role = roleService.getRolePrivilegeTree(r);
                if (role != null) {
                    nodes.add(role);
                }
            }
        }
        // 获取上一级节点关联的角色
        OrgElementPathEntity orgElementPathEntity = orgElementPathService.getByOrgEleUuid(orgElementUuid);
        if (orgElementPathEntity != null) {
            String[] idPaths = orgElementPathEntity.getIdPath().split(Separator.SLASH.getValue());
            if (idPaths.length >= 2) {
                OrgElementEntity elementEntity = getByIdAndOrgVersionUuid(idPaths[idPaths.length - 2], orgElementPathEntity.getOrgVersionUuid());
                if (elementEntity != null) {
                    List<TreeNode> subList = this.getOrgElementRolePrivilegeTree(elementEntity.getUuid());
                    if (CollectionUtils.isNotEmpty(subList)) {
                        nodes.addAll(subList);
                    }
                }
            }
        }

        return nodes;
    }

    @Override
    public List<OrgElementEntity> getOrgElementsByTypesAndOrgVersionUuid(Long orgVersionUuid, List<String> types) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("types", types);
        return this.dao.listByHQL("from OrgElementEntity where orgVersionUuid=:orgVersionUuid " + (CollectionUtils.isNotEmpty(types) ? " and type in :types" : ""), params);
    }

    @Override
    public boolean existOrgElementType(String type, String system) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", type);
        if (StringUtils.isNotBlank(system)) {
            params.put("system", system);
            params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        }
        return dao.getNumberByHQL("select distinct 1 from OrgElementEntity where type=:type "
                + (StringUtils.isNotBlank(system) ? " and system=:system and tenant=:tenant " : ""), params, Integer.class) != null;
    }

    /**
     * 根据组织元素ID获取组织版本ID列表
     *
     * @param id
     * @return
     */
    @Override
    public List<String> listOrgVersionIdById(String id) {
        Assert.notNull(id, "组织元素ID不能为空");

        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        return dao.listCharSequenceByHQL("select distinct orgVersionId from OrgElementEntity where id=:id", params);
    }

    @Override
    @Transactional
    public void deleteElementRoleRelaByRoleUuid(String roleUuid) {
        orgElementRoleRelaService.deleteByRoleUuid(roleUuid);
    }

    /**
     * 根据组织元素ID列表、角色UUID添加用户角色
     *
     * @param orgEleIds
     * @param roleUuid
     * @param orgVersionUuid
     */
    @Override
    @Transactional
    public void addElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long orgVersionUuid) {
        if (CollectionUtils.isEmpty(orgEleIds)) {
            return;
        }

        List<OrgElementEntity> orgElementEntities = listByIdsAndOrgVersionId(orgEleIds.toArray(new String[0]), orgVersionUuid);
        List<OrgElementRoleRelaEntity> relaEntities = Lists.newArrayList();
        orgElementEntities.forEach(elementEntity -> {
            OrgElementRoleRelaEntity relaEntity = new OrgElementRoleRelaEntity();
            relaEntity.setOrgElementUuid(elementEntity.getUuid());
            relaEntity.setRoleUuid(roleUuid);
            relaEntity.setOrgVersionUuid(elementEntity.getOrgVersionUuid());
            relaEntities.add(relaEntity);
        });

        if (CollectionUtils.isNotEmpty(relaEntities)) {
            orgElementRoleRelaService.saveAll(relaEntities);
        }
    }

    @Override
    @Transactional
    public void addElementRoleRelaByUuidsAndRoleUuid(List<Long> orgEleUuids, String roleUuid) {
        if (CollectionUtils.isNotEmpty(orgEleUuids)) {
            List<OrgElementEntity> orgElementEntities = listByUuids(orgEleUuids);
            List<OrgElementRoleRelaEntity> relaEntities = Lists.newArrayList();
            orgElementEntities.forEach(elementEntity -> {
                OrgElementRoleRelaEntity relaEntity = new OrgElementRoleRelaEntity();
                relaEntity.setOrgElementUuid(elementEntity.getUuid());
                relaEntity.setRoleUuid(roleUuid);
                relaEntity.setOrgVersionUuid(elementEntity.getOrgVersionUuid());
                relaEntities.add(relaEntity);
            });

            if (CollectionUtils.isNotEmpty(relaEntities)) {
                orgElementRoleRelaService.saveAll(relaEntities);
            }
        }
    }

    /**
     * 根据权限角色UUID、组织版本获取组织元素
     *
     * @param roleUuid
     * @param orgVersionUuid
     * @return
     */
    @Override
    public List<OrgElementEntity> listByRelaRoleUuidOrgVersionUuid(String roleUuid, Long orgVersionUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");
        Assert.notNull(orgVersionUuid, "组织版本UUID不为空！");

        String hql = "from OrgElementEntity t1 where exists(select t2.uuid from OrgElementRoleRelaEntity t2 where t2.roleUuid = :roleUuid and t2.orgVersionUuid = :orgVersionUuid and t2.orgElementUuid = t1.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listByHQL(hql, params);
    }

    /**
     * 根据权限角色UUID获取组织元素
     *
     * @param roleUuid
     * @return
     */
    @Override
    public List<OrgElementEntity> listByRelaRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");

        String hql = "from OrgElementEntity t1 where exists(select t2.uuid from OrgElementRoleRelaEntity t2 where t2.roleUuid = :roleUuid and t2.orgElementUuid = t1.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public void deleteOrgElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long orgVersionUuid) {
        orgElementRoleRelaService.deleteOrgElementRoleRelaByIdsAndRoleUuid(orgEleIds, roleUuid, orgVersionUuid);
    }

    @Override
    public List<OrgElementEntity> getRoleOrgElementMemberByRoleSystemTenant(String roleUuid, String system, String tenant) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        params.put("tenant", tenant);
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        // 仅获取当前租户系统已发布产品版本的组织单元实例
        StringBuilder hql = new StringBuilder("from OrgElementEntity o where tenant=:tenant and  exists (" +
                " select 1 from OrgElementRoleRelaEntity rela , OrgVersionEntity v where v.uuid = rela.orgVersionUuid and v.state=:state and  rela.orgElementUuid = o.uuid and rela.roleUuid=:roleUuid " +
                ") ");
        if (StringUtils.isNotBlank(system)) {
            params.put("system", system);
            hql.append(" and o.system=:system");
        }
        return this.dao.listByHQL(hql.toString(), params);
    }

    @Override
    public OrgElementEntity getOrgElementByIdPublished(String id) {
        OrgElementEntity example = new OrgElementEntity();
        example.setId(id);
        example.setState(OrgVersionEntity.State.PUBLISHED);
        List<OrgElementEntity> list = dao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<OrgSelectProvider.Node> getTreeNodesByTitles(String type, OrgSelectProvider.Params params) {
        for (OrgSelectProvider provider : orgSelectProviders) {
            if (type != null && type.equalsIgnoreCase(provider.type())) {
                return provider.fetchByTitles(params);
            }
        }
        logger.error("不存在类型为{}的组织树服务提供", type);
        return null;
    }

    @Override
    public List<OrgElementEntity> getOrgElementsByNameAndVersion(String name, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("name", name);
        return this.dao.listByHQL("from OrgElementEntity where name=:name and orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public List<OrgSelectProvider.Node> getTreeNodesByTypeKeys(OrgSelectProvider.Params params) {
        List<OrgSelectProvider.Node> results = Lists.newArrayList();
        List<String> types = params.get("orgTypes") != null ? (List<String>) params.get("orgTypes") : null;
        if (CollectionUtils.isNotEmpty(types)) {
            for (OrgSelectProvider provider : orgSelectProviders) {
                if (types.contains(provider.type())) {
                    // 依次匹配所有的组织数据提供器，返回节点
                    List<OrgSelectProvider.Node> subNodes = provider.fetchByKeys(params);
                    if (CollectionUtils.isNotEmpty(subNodes)) {
                        results.addAll(subNodes);
                        if (CollectionUtils.isEmpty(params.getKeys())) {
                            return results;
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public List<OrgElementEntity> getAllPublishedByOrgUuid(Long orgUuid) {
        OrgVersionEntity orgVersionEntity = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, orgUuid);
        if (orgVersionEntity != null) {
            return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionEntity.getUuid());
        }
        return null;
    }

    @Override
    public Long countByIdAndOrgVersionUuid(String id, Long orgVersionUuid) {
        String hql = "select count(*) from OrgElementEntity t where t.id = :id and t.orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.countByHQL(hql, params);
    }

    @Override
    @Transactional
    public void updateUnitOrgElementName(String name, String sourceId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", name);
        params.put("sourceId", sourceId);
        List<OrgElementEntity> unitElement = dao.listByHQL("from OrgElementEntity where sourceId=:sourceId", params);
        Map<String, OrgElementEntity> idElement = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(unitElement)) {
            for (OrgElementEntity ele : unitElement) {
                idElement.put(ele.getId(), ele);
            }


            this.dao.updateByHQL("update OrgElementEntity set name =:name where sourceId =:sourceId", params);
            Set<String> idSet = idElement.keySet();
            for (String elementId : idSet) {
                params.put("orgElementId", elementId);
                List<OrgElementPathEntity> pathEntities = orgElementPathService.listByHQL("from OrgElementPathEntity p where p.orgElementId=:orgElementId or (" +
                        " exists ( select 1 from OrgElementPathChainEntity c where ( c.orgElementId=:orgElementId and c.subOrgElementId=p.orgElementId ) or ( c.orgElementId=p.orgElementId and c.subOrgElementId=:orgElementId) )" +
                        ")", params);
                if (CollectionUtils.isNotEmpty(pathEntities)) {
                    for (OrgElementPathEntity p : pathEntities) {
                        String[] ids = p.getIdPath().split(Separator.SLASH.getValue());
                        int i = ArrayUtils.indexOf(ids, elementId);
                        if (i != -1) {
                            String[] names = p.getCnPath().split(Separator.SLASH.getValue());
                            String[] pinyins = p.getPinYinPath().split(Separator.SLASH.getValue());
                            names[i] = name;
                            pinyins[i] = PinyinUtil.getPinYinMulti(name, true);
                            p.setCnPath(StringUtils.join(names, Separator.SLASH.getValue()));
                            p.setPinYinPath(StringUtils.join(pinyins, Separator.SLASH.getValue()));
                        }
                    }
                    orgElementPathService.saveAll(pathEntities);
                }
            }

        }

    }

    @Override
    @Transactional
    public void updateUnitOrgElementI18ns(String sourceId, List<OrgElementI18nEntity> i18ns) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sourceId", sourceId);
        List<OrgElementEntity> unitElement = dao.listByHQL("from OrgElementEntity where sourceId=:sourceId", params);
        Map<Long, OrgElementEntity> uuidElement = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(unitElement)) {
            for (OrgElementEntity ele : unitElement) {
                uuidElement.put(ele.getUuid(), ele);
            }
            Set<Long> uuids = uuidElement.keySet();
            for (Long uuid : uuids) {
                OrgElementEntity element = uuidElement.get(uuid);
                Map<String, Object> bizParams = Maps.newHashMap();
                bizParams.put("id", element.getId());
                if (CollectionUtils.isNotEmpty(i18ns)) {
                    List<OrgElementI18nEntity> newI18ns = Lists.newArrayList();
                    for (OrgElementI18nEntity e : i18ns) {
                        OrgElementI18nEntity i18n = new OrgElementI18nEntity();
                        i18n.setDataUuid(element.getUuid());
                        if (element.getState().equals(OrgVersionEntity.State.PUBLISHED)) {
                            // 只有正式版才会设置元素ID（正式版使用时候会根据元素ID查询，避免查到两个）
                            i18n.setDataId(element.getId());
                        }
                        i18n.setDataCode(e.getDataCode());
                        i18n.setLocale(e.getLocale());
                        i18n.setContent(e.getContent());
                        newI18ns.add(i18n);
                    }
                    orgElementI18nService.saveAllAfterDelete(element.getUuid(), newI18ns);
                    List<BizOrgElementEntity> bizOrgElementEntities = bizOrgElementService.listByHQL("from BizOrgElementEntity where orgElementId =:id", bizParams);
                    if (CollectionUtils.isNotEmpty(bizOrgElementEntities)) {
                        for (BizOrgElementEntity b : bizOrgElementEntities) {
                            newI18ns.clear();
                            for (OrgElementI18nEntity e : i18ns) {
                                OrgElementI18nEntity i18n = new OrgElementI18nEntity();
                                i18n.setDataUuid(b.getUuid());
                                i18n.setDataId(b.getId());
                                i18n.setDataCode(e.getDataCode());
                                i18n.setLocale(e.getLocale());
                                i18n.setContent(e.getContent());
                                newI18ns.add(i18n);
                            }
                            orgElementI18nService.saveAllAfterDelete(b.getUuid(), newI18ns);
                        }
                    }
                }


            }

        }
    }

    @Override
    public Map<String, Long> getUuidsByIds(String[] ids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("orgVersionUuid", orgVersionUuid);
        List<OrgElementEntity> elementEntities = this.dao.listByHQL("from OrgElementEntity where id in :ids and orgVersionUuid=:orgVersionUuid ", params);
        Map<String, Long> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(elementEntities)) {
            for (OrgElementEntity e : elementEntities) {
                map.put(e.getId(), e.getUuid());
            }
        }
        return map;
    }

    @Override
    public String getLocaleOrgElementName(String id, Long orgVersionUuid, String locale) {
        OrgElementEntity entity = getByIdAndOrgVersionUuid(id, orgVersionUuid);
        if (entity != null) {
            OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(entity.getUuid(), "name", locale);
            if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                return i18nEntity.getContent();
            }
        }
        return null;
    }

    @Override
    public String getLocalOrgElementName(Long orgElementUuid, String locale) {
        OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(orgElementUuid, "name", locale);
        if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
            return i18nEntity.getContent();
        }
        return null;
    }

    @Override
    public String getLocalOrgElementShortName(Long orgElementUuid, String locale) {
        OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(orgElementUuid, "short_name", locale);
        if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
            return i18nEntity.getContent();
        }
        return null;
    }

    @Override
    @Transactional
    public void translateAllElements(Long orgVersionUuid, Boolean onlyTranslateEmpty) {
        List<OrgElementEntity> orgElementEntities = dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
        List<OrgElementI18nEntity> i18nEntities = Lists.newArrayList();
        Set<String> locales = appCodeI18nService.getAllLocaleString();
        if (CollectionUtils.isNotEmpty(orgElementEntities)) {
            if (BooleanUtils.isTrue(onlyTranslateEmpty)) {
                Map<Long, OrgElementEntity> uuidEntitys = Maps.newHashMap();
                Set<Long> notEmptyShortNameUuids = Sets.newHashSet();
                Set<String> texts = Sets.newHashSet();
                for (OrgElementEntity entity : orgElementEntities) {
                    uuidEntitys.put(entity.getUuid(), entity);
                    texts.add(entity.getName());
                    if (StringUtils.isNotEmpty(entity.getShortName())) {
                        notEmptyShortNameUuids.add(entity.getUuid());
                        texts.add(entity.getShortName());
                    }
                }
                Set<Long> uuids = uuidEntitys.keySet();
                for (String locale : locales) {
                    if (locale.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.toString())) {
                        continue;
                    }
                    List<OrgElementI18nEntity> nameI18ns = orgElementI18nService.listOrgElementI18ns(uuids, "name", locale);
                    List<OrgElementI18nEntity> shortNameI18ns = notEmptyShortNameUuids.isEmpty() ? null : orgElementI18nService.listOrgElementI18ns(notEmptyShortNameUuids, "short_name", locale);
                    Set<String> names = Sets.newHashSet(texts);
                    Set<Long> lackNameUuids = Sets.newHashSet(uuids);
                    Set<Long> lackShortNameUuids = Sets.newHashSet(notEmptyShortNameUuids);
                    if (CollectionUtils.isNotEmpty(nameI18ns)) {
                        for (OrgElementI18nEntity i : nameI18ns) {
                            if (lackNameUuids.contains(i.getDataUuid())) {
                                lackNameUuids.remove(i.getDataUuid());
                                names.remove(uuidEntitys.get(i.getDataUuid()).getName());
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(shortNameI18ns)) {
                        for (OrgElementI18nEntity i : shortNameI18ns) {
                            if (lackShortNameUuids.contains(i.getDataUuid())) {
                                lackShortNameUuids.remove(i.getDataUuid());
                                names.remove(uuidEntitys.get(i.getDataUuid()).getShortName());
                            }
                        }
                    }
                    if (!names.isEmpty()) {
                        Map<String, String> result = translateService.translate(names, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                        for (Long uid : lackNameUuids) {
                            OrgElementEntity entity = uuidEntitys.get(uid);
                            if (result.containsKey(entity.getName().trim())) {
                                i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "name", locale, result.get(entity.getName().trim())));
                            }
                        }
                        for (Long uid : lackShortNameUuids) {
                            OrgElementEntity entity = uuidEntitys.get(uid);
                            if (result.containsKey(entity.getShortName().trim())) {
                                i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "short_name", locale, result.get(entity.getShortName().trim())));
                            }
                        }
                    }
                }
            } else {
                Set<String> names = Sets.newHashSet();
                for (OrgElementEntity entity : orgElementEntities) {
                    names.add(entity.getName().trim());
                    if (StringUtils.isNotEmpty(entity.getShortName())) {
                        names.add(entity.getShortName().trim());
                    }
                }
                for (String locale : locales) {
                    if (!locale.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE.toString())) {
                        Map<String, String> result = translateService.translate(names, "zh", locale.split(Separator.UNDERLINE.getValue())[0]);
                        for (OrgElementEntity entity : orgElementEntities) {
                            orgElementI18nService.deleteByDataUuid(entity.getUuid());
                            if (result.containsKey(entity.getName().trim())) {
                                i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "name", locale, result.get(entity.getName().trim())));
                            }
                            if (StringUtils.isNotBlank(entity.getShortName()) && result.containsKey(entity.getShortName().trim())) {
                                i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "short_name", locale, result.get(entity.getShortName().trim())));
                            }
                        }
                    }
                }
            }
            if (!i18nEntities.isEmpty()) {
                orgElementI18nService.saveAll(i18nEntities);
            }
        }
    }

    @Override
    public long countByOrgVersionUuid(Long orgVersionUuid) {
        OrgElementEntity entity = new OrgElementEntity();
        entity.setOrgVersionUuid(orgVersionUuid);
        return this.dao.countByEntity(entity);
    }

    @Override
    @Transactional
    public void saveOrgElementLeader(Long orgElementUuid, String director, String leader, String manager) {
        OrgElementEntity entity = this.dao.getOne(orgElementUuid);
        if (entity == null) {
            return;
        }

        OrgElementManagementEntity managementEntity = orgElementManagementService.getByOrgElementUuid(orgElementUuid);
        if (managementEntity == null) {
            managementEntity = new OrgElementManagementEntity();
        }
        managementEntity.setDirector(director);
        managementEntity.setLeader(leader);
        managementEntity.setOrgManager(manager);
        managementEntity.setOrgElementUuid(entity.getUuid());
        managementEntity.setOrgVersionUuid(entity.getOrgVersionUuid());
        managementEntity.setOrgElementId(entity.getId());
        managementEntity.setSystem(entity.getSystem());
        managementEntity.setTenant(entity.getTenant());
        orgElementManagementService.save(managementEntity);
    }

    @Override
    public List<String> listRoleUuidByUuid(Long orgElementUuid) {
        return orgElementRoleRelaService.listByOrgElementUuid(orgElementUuid).stream().map(OrgElementRoleRelaEntity::getRoleUuid).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrgElementSeq(List<OrgElementDto> list) {
        Map<String, Object> params = Maps.newHashMap();
        for (OrgElementDto dto : list) {
            params.put("uuid", dto.getUuid());
            params.put("seq", dto.getSeq());
            dao.updateByHQL("update OrgElementEntity set seq=:seq where uuid=:uuid", params);
        }
    }

    @Override
    public Set<String> getRelaRoleUuids(Long orgElementUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", orgElementUuid);
        return Sets.newHashSet(this.dao.listCharSequenceBySQL("select role_uuid from ORG_ELEMENT_ROLE_RELA where org_element_uuid=:uuid", params));
    }

    @Override
    @Transactional
    public void updateOrgElementPathChain(Long orgUuid, Long orgVersionUuid) {
        if (orgVersionUuid == null && orgUuid != null) {
            OrgVersionEntity entity = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, orgUuid);
            if (entity != null) {
                orgVersionUuid = entity.getUuid();
            }
        }
        if (orgVersionUuid == null) {
            return;
        }
        List<OrgElementEntity> orgElementEntities = listByOrgVersionUuid(orgVersionUuid);
        Map<Long, OrgElementEntity> orgElementMap = Maps.newHashMap();
        Map<String, OrgElementEntity> orgElementIdMap = Maps.newHashMap();
        orgElementPathService.deleteByOrgVersionUuid(orgVersionUuid);
        orgElementPathChainService.deleteByOrgVersionUuid(orgVersionUuid);
        if (CollectionUtils.isNotEmpty(orgElementEntities)) {

            for (OrgElementEntity entity : orgElementEntities) {
                orgElementMap.put(entity.getUuid(), entity);
                orgElementIdMap.put(entity.getId(), entity);
            }
            // 构建父子关系
            List<OrgElementEntity> root = Lists.newArrayList();
            for (OrgElementEntity entity : orgElementEntities) {
                if (entity.getParentUuid() == null) {
                    root.add(entity);
                } else {
                    orgElementMap.get(entity.getParentUuid()).getChildren().add(entity);
                }
            }
            List<OrgElementPathEntity> paths = Lists.newArrayList();
            Map<Long, OrgElementPathEntity> pathMap = Maps.newHashMap();
            List<OrgElementPathChainEntity> chains = Lists.newArrayList();
            for (OrgElementEntity entity : root) {
                addOrgElementPaths(paths, entity, null, orgElementMap, pathMap, chains);
            }
            orgElementPathService.saveAll(paths);
            orgElementPathChainService.saveAll(chains);

        }
    }

    @Override
    @Transactional
    public void updateOrgUserUnderOrgElement(Long orgElementUuid) {
        OrgElementEntity elementEntity = getOne(orgElementUuid);
        if (elementEntity == null) {
            return;
        }

        // 获取单元实例关联的所有组织用户
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionUuid", elementEntity.getOrgVersionUuid());
        param.put("path", "%" + elementEntity.getId() + "%");
        List<OrgUserEntity> orgUserEntities = orgUserService.listByHQL("from OrgUserEntity where userPath like :path and orgVersionUuid=:orgVersionUuid", param);
        OrgElementPathEntity orgElementPathEntity = orgElementPathService.getByOrgEleUuid(orgElementUuid);
        if (CollectionUtils.isNotEmpty(orgUserEntities) && orgElementPathEntity != null) {
            int len = elementEntity.getId().length();
            for (OrgUserEntity u : orgUserEntities) {
                int index = u.getUserPath().indexOf(elementEntity.getId());
                if (index != -1) {
                    u.setUserPath(orgElementPathEntity.getIdPath() + u.getUserPath().substring(index + len));
                    orgUserService.save(u);
                }
            }
        }


    }


    private void addOrgElementPaths(List<OrgElementPathEntity> paths, OrgElementEntity entity, OrgElementEntity parent,
                                    Map<Long, OrgElementEntity> orgElementMap, Map<Long, OrgElementPathEntity> pathMap, List<OrgElementPathChainEntity> chains) {

        OrgElementPathEntity pathEntity = new OrgElementPathEntity();
        pathEntity.setTenant(entity.getTenant());
        pathEntity.setSystem(entity.getSystem());
        pathEntity.setIdPath(parent != null ? pathMap.get(parent.getUuid()).getIdPath() + Separator.SLASH.getValue() + entity.getId() : entity.getId());
        pathEntity.setCnPath(parent != null ? pathMap.get(parent.getUuid()).getCnPath() + Separator.SLASH.getValue() + entity.getName() : entity.getName());
        pathEntity.setPinYinPath(parent != null ? pathMap.get(parent.getUuid()).getPinYinPath() + Separator.SLASH.getValue() + PinyinUtil.getPinYinMulti(entity.getName(), true)
                : PinyinUtil.getPinYinMulti(entity.getName(), true));
        orgUserService.getDao().updateBySQL("update org_user set user_path=:orgElementPath || '/' || user_id where org_element_id = :orgElementId and org_version_uuid=:orgVersionUuid"
                , ImmutableMap.<String, Object>builder().put("orgElementPath", pathEntity.getIdPath()).put("orgElementId", entity.getId()).put("orgVersionUuid", entity.getOrgVersionUuid()).build());
        pathEntity.setOrgElementUuid(entity.getUuid());
        pathEntity.setOrgElementId(entity.getId());
        pathEntity.setOrgVersionUuid(entity.getOrgVersionUuid());
        pathEntity.setLeaf(orgElementMap.get(entity.getUuid()).getChildren().isEmpty());
        pathMap.put(entity.getUuid(), pathEntity);
        paths.add(pathEntity);
        OrgElementEntity p = parent;
        int level = 1;
        while (p != null) {
            OrgElementPathChainEntity chain = new OrgElementPathChainEntity();
            chain.setSubOrgElementId(entity.getId());
            chain.setSubOrgElementType(entity.getType());
            chain.setOrgElementId(p.getId());
            chain.setOrgElementType(p.getType());
            chain.setOrgVersionUuid(entity.getOrgVersionUuid());
            chain.setTenant(entity.getTenant());
            chain.setSystem(entity.getSystem());
            chain.setLevel(level++);
            chains.add(chain);
            p = p.getParentUuid() != null ? orgElementMap.get(p.getParentUuid()) : null;
        }

        if (!entity.getChildren().isEmpty()) {
            for (OrgElementEntity e : entity.getChildren()) {
                addOrgElementPaths(paths, e, entity, orgElementMap, pathMap, chains);
            }
        }

    }


    @Override
    public List<OrgElementEntity> listByParentUUid(Long parentUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentUuid);
        return this.dao.listByHQL("from OrgElementEntity where parentUuid=:parentUuid order by seq asc", params);
    }

    public Integer getMaxSeq(Long parentUuid, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentUuid", parentUuid);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.getNumberByHQL("select max(seq) from OrgElementEntity where orgVersionUuid=:orgVersionUuid and " +
                (parentUuid == null ? "parentUuid is null" : "parentUuid=:parentUuid"), params, Integer.class);
    }

}
