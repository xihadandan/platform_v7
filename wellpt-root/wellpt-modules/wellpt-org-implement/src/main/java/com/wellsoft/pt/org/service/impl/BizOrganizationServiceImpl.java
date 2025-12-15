package com.wellsoft.pt.org.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.*;
import com.wellsoft.pt.org.dto.BizOrgConfigDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Service
public class BizOrganizationServiceImpl extends AbstractJpaServiceImpl<BizOrganizationEntity, BizOrganizationDaoImpl, Long>
        implements BizOrganizationService {

    @Resource
    BizOrgDimensionDaoImpl dimensionDao;

    @Resource
    BizOrgRoleDaoImpl bizOrgRoleDao;

    @Resource
    BizOrgConfigDaoImpl bizOrgConfigDao;

    @Resource
    BizOrgRoleTemplateDaoImpl bizOrgRoleTemplateDao;

    @Resource
    BizOrgElementService bizOrgElementService;

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgElementModelService orgElementModelService;

    @Resource
    BizOrgElementMemberService bizOrgElementMemberService;

    @Resource
    OrgElementI18nService orgElementI18nService;


    @Override
    @Transactional
    public Long saveBizOrg(BizOrganizationEntity temp) {
        BizOrganizationEntity entity = temp.getUuid() != null ? getOne(temp.getUuid()) : new BizOrganizationEntity();
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(temp, entity);
            entity.setId(IdPrefix.BIZ_ORG.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
            entity.setSystem(RequestSystemContextPathResolver.system());
            if (StringUtils.isNotBlank(entity.getSystem())) {
                entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
        } else {
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "id", "expired", "system", "tenant", "syncLocked"));
            if (BooleanUtils.isTrue(entity.getNeverExpire())) {
                entity.setExpired(false);
                entity.setExpireTime(null);
            }
        }
        save(entity);

        if (CollectionUtils.isNotEmpty(temp.getI18ns())) {
            for (OrgElementI18nEntity e : temp.getI18ns()) {
                e.setDataUuid(entity.getUuid());
                e.setDataId(entity.getId());
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), temp.getI18ns());
        }

        return entity.getUuid();
    }

    @Override
    @Transactional
    public int updateSyncLocked(Long uuid, Boolean locked) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("locked", BooleanUtils.isTrue(locked));
        params.put("lockedCondition", !BooleanUtils.isTrue(locked));
        params.put("uuid", uuid);
        return this.dao.updateByHQL("update BizOrganizationEntity set syncLocked = :locked where uuid=:uuid and syncLocked = :lockedCondition ", params);
    }

    @Override
    public BizOrganizationEntity getById(String id) {
        BizOrganizationEntity entity = dao.getOneByFieldEq("id", id);
        if (entity.getExpireTime() != null) {
            entity.setExpired(entity.getExpireTime().compareTo(new Date()) < 0);
        }
        return entity;
    }

    @Override
    @Transactional
    public void deleteBizOrg(List<Long> uuids) {
        dao.deleteByUuids(uuids);

        //FIXME: 删除组织下的元素
    }

    @Override
    @Transactional
    public Long saveBizOrgRoleTemplate(BizOrgRoleTemplateEntity temp) {
        BizOrgRoleTemplateEntity entity = temp.getUuid() != null ? bizOrgRoleTemplateDao.getOne(temp.getUuid()) : new BizOrgRoleTemplateEntity();
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(temp, entity);
            entity.setSystem(RequestSystemContextPathResolver.system());
            if (StringUtils.isNotBlank(entity.getSystem())) {
                entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
        } else {
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "system", "tenant"));
        }
        bizOrgRoleTemplateDao.save(entity);
        return entity.getUuid();
    }

    @Override
    @Transactional
    public Long saveBizOrgDimension(BizOrgDimensionEntity temp) {
        BizOrgDimensionEntity entity = temp.getUuid() != null ? dimensionDao.getOne(temp.getUuid()) : new BizOrgDimensionEntity();
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(temp, entity);
            entity.setSystem(RequestSystemContextPathResolver.system());
            if (StringUtils.isNotBlank(entity.getSystem())) {
                entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
        } else {
            BeanUtils.copyProperties(temp, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "id", "system", "tenant"));
        }
        dimensionDao.save(entity);
        if (CollectionUtils.isNotEmpty(temp.getI18ns())) {
            for (OrgElementI18nEntity e : temp.getI18ns()) {
                e.setDataUuid(entity.getUuid());
                e.setDataId(entity.getId());
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), temp.getI18ns());
        }
        return entity.getUuid();
    }

    @Override
    @Transactional
    public void deleteBizOrgRoleTemplate(List<Long> uuids) {
        bizOrgRoleTemplateDao.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void deleteBizOrgDimension(List<Long> uuids) {
        dimensionDao.deleteByUuids(uuids);
    }

    @Override
    public List<BizOrgDimensionEntity> getBizOrgDimensionsBySystemAndTenant(String system, String tenant) {
        BizOrgDimensionEntity example = new BizOrgDimensionEntity();
        if (StringUtils.isNotBlank(system)) {
            example.setSystem(system);
            example.setTenant(tenant);
        }
        return dimensionDao.listByEntity(example);
    }

    @Override
    public List<BizOrgRoleTemplateEntity> getBizOrgRoleTemplateBySystemAndTenant(String system, String tenant) {
        BizOrgRoleTemplateEntity example = new BizOrgRoleTemplateEntity();
        if (StringUtils.isNotBlank(system)) {
            example.setSystem(system);
            example.setTenant(tenant);
        }
        return bizOrgRoleTemplateDao.listByEntity(example);
    }

    @Override
    public List<BizOrgRoleEntity> getBizOrgRolesByBizOrgUuid(Long bizOrgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgUuid", bizOrgUuid);
        return bizOrgRoleDao.listByHQL("from BizOrgRoleEntity where bizOrgUuid=:bizOrgUuid order by seq asc", params);
    }

    @Override
    public List<BizOrgRoleEntity> getBizOrgRolesByBizOrgId(String bizOrgId) {
        Map<String, Object> params = Maps.newHashMap();
        String hql = "from BizOrgRoleEntity where bizOrgUuid in(select t.uuid from BizOrganizationEntity t where t.id = :bizOrgId) order by seq asc";
        params.put("bizOrgId", bizOrgId);
        return bizOrgRoleDao.listByHQL(hql, params);
    }

    @Override
    public BizOrgConfigEntity getBizOrgConfigByBizOrgUuid(Long bizOrgUuid) {
        return bizOrgConfigDao.getOneByFieldEq("bizOrgUuid", bizOrgUuid);
    }

    @Override
    public BizOrgDimensionEntity getBizOrgDimensionByUuid(Long uuid) {
        return dimensionDao.getOne(uuid);
    }

    @Override
    public BizOrgDimensionEntity getBizOrgDimensionById(String id) {
        BizOrgDimensionEntity example = new BizOrgDimensionEntity();
        example.setId(id);
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            example.setSystem(RequestSystemContextPathResolver.system());
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<BizOrgDimensionEntity> list = dimensionDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public BizOrgRoleEntity getBizOrgRoleById(String id, Long bizOrgUuid) {
        BizOrgRoleEntity example = new BizOrgRoleEntity();
        example.setId(id);
        example.setBizOrgUuid(bizOrgUuid);
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            example.setSystem(RequestSystemContextPathResolver.system());
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        List<BizOrgRoleEntity> list = bizOrgRoleDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<BizOrgRoleEntity> getBizOrgRolesByIds(List<String> ids, Long bizOrgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgUuid", bizOrgUuid);
        params.put("id", ids);
        return bizOrgRoleDao.listByHQL("from BizOrgRoleEntity where id in :id " +
                (bizOrgUuid != null ? " and bizOrgUuid=:bizOrgUuid " : "") +
                " order by seq asc", params);
    }

    @Override
    @Transactional
    public void syncBizOrg(Long uuid) {
        BizOrgConfigEntity bizOrgConfigEntity = bizOrgConfigDao.getOneByFieldEq("bizOrgUuid", uuid);
        if (bizOrgConfigEntity.getEnableSyncOrg()) {
            String syncOption = bizOrgConfigEntity.getSyncOrgOption();
            BizOrganizationEntity bizOrganizationEntity = getOne(uuid);
            if (BooleanUtils.isTrue(bizOrganizationEntity.getExpired())
                    || (BooleanUtils.isFalse(bizOrganizationEntity.getNeverExpire()) && bizOrganizationEntity.getExpireTime() != null
                    && bizOrganizationEntity.getExpireTime().compareTo(new Date()) < 0)
            ) {
                throw new BusinessException("业务组织已失效，无法进行同步");
            }
            if (this.updateSyncLocked(uuid, true) == 0) {
                throw new LockedException("业务组织同步锁中，无法在进行同步操作");
            }


            List<BizOrgElementEntity> syncUnderBizDimElements = null;
            if (StringUtils.isNotBlank(bizOrgConfigEntity.getBizOrgDimensionId())) {
                syncUnderBizDimElements = bizOrgElementService.getAllBizDimElementsByBizOrgUuid(uuid);
                if (CollectionUtils.isEmpty(syncUnderBizDimElements)) {
                    throw new BusinessException("不存在业务维度无法同步");
                }
            }


            List<BizOrgElementEntity> bizOrgElementEntities = bizOrgElementService.getAllByBizOrgUuid(uuid);
            List<OrgElementEntity> orgElementEntities = orgElementService.getAllPublishedByOrgUuid(bizOrganizationEntity.getOrgUuid());
            if (CollectionUtils.isNotEmpty(orgElementEntities)) {
                List<OrgElementModelEntity> orgElementModelEntities = orgElementModelService.listOrgElementModels(SpringSecurityUtils.getCurrentTenantId(), RequestSystemContextPathResolver.system());
                ImmutableMap<String, OrgElementModelEntity> orgElementModelMap = Maps.uniqueIndex(orgElementModelEntities, new Function<OrgElementModelEntity, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable OrgElementModelEntity entity) {
                        return entity.getId();
                    }
                });
                Set<String> orgElementIds = Sets.newHashSet();
                Map<Long, OrgElementEntity> orgElementUuidMap = Maps.newHashMap();
                Map<String, OrgElementEntity> orgElementIdMap = Maps.newHashMap();
                for (OrgElementEntity entity : orgElementEntities) {
                    orgElementIds.add(entity.getId());
                    orgElementUuidMap.put(entity.getUuid(), entity);
                    orgElementIdMap.put(entity.getId(), entity);
                }

                Set<Long> disableUuids = Sets.newHashSet();
                Map<Long, BizOrgElementEntity> bizUuidElementMap = Maps.newHashMap();
                Map<String, BizOrgElementEntity> bizIdElementMap = Maps.newHashMap();
                Map<String, List<BizOrgElementEntity>> bizOrgEleIdMap = Maps.newHashMap();
                for (BizOrgElementEntity entity : bizOrgElementEntities) {
                    bizUuidElementMap.put(entity.getUuid(), entity);
                    bizIdElementMap.put(entity.getId(), entity);
                    if (!entity.getIsDimension() && StringUtils.isNotBlank(entity.getOrgElementId())
                            && !orgElementIds.contains(entity.getOrgElementId())) {
                        // 不存在的组织元素节点要禁用在业务组织中的使用
                        disableUuids.add(entity.getUuid());
                    }
                    if (StringUtils.isNotBlank(entity.getOrgElementId())) {
                        if (!bizOrgEleIdMap.containsKey(entity.getOrgElementId())) {
                            bizOrgEleIdMap.put(entity.getOrgElementId(), Lists.newArrayList(entity));
                        } else {
                            bizOrgEleIdMap.get(entity.getOrgElementId()).add(entity);
                        }

                    }
                }


                if (CollectionUtils.isNotEmpty(syncUnderBizDimElements)) {
                    for (BizOrgElementEntity entity : syncUnderBizDimElements) {
                        this.syncOrgElementUnderBizOrg(uuid, entity, orgElementEntities, syncOption, disableUuids, orgElementModelMap, orgElementUuidMap, bizUuidElementMap);
                    }
                } else {
                    this.syncOrgElementUnderBizOrg(uuid, null, orgElementEntities, syncOption, disableUuids, orgElementModelMap, orgElementUuidMap, bizUuidElementMap);
                }

                this.bizOrgElementService.deleteAllBizOrgElementPathByBizOrgUuid(uuid);

                // 重新计算路径
                List<BizOrgElementPathEntity> pathEntities = Lists.newArrayList();
                List<BizOrgElementPathChainEntity> pathChainEntities = Lists.newArrayList();
                Iterator<BizOrgElementEntity> iterator = bizUuidElementMap.values().iterator();
                while (iterator.hasNext()) {
                    BizOrgElementEntity entity = iterator.next();
                    BizOrgElementEntity parent = bizUuidElementMap.get(entity.getParentUuid());
                    List<String> idPaths = Lists.newArrayList(entity.getId());
                    List<String> namePaths = Lists.newArrayList(entity.getName());
                    List<String> pyPaths = Lists.newArrayList(PinyinUtil.getPinYinMulti(entity.getName(), true));
                    List<String> typePaths = Lists.newArrayList(entity.getElementType());
                    while (parent != null) {
                        idPaths.add(parent.getId());
                        namePaths.add(parent.getName());
                        pyPaths.add(PinyinUtil.getPinYinMulti(parent.getName(), true));
                        typePaths.add(parent.getElementType());
                        parent = bizUuidElementMap.get(parent.getParentUuid());
                    }
                    String[] idPathArray = idPaths.toArray(new String[]{});
                    ArrayUtils.reverse(idPathArray);
                    String[] namePathArray = namePaths.toArray(new String[]{});
                    ArrayUtils.reverse(namePathArray);
                    String[] pyPathArray = pyPaths.toArray(new String[]{});
                    ArrayUtils.reverse(pyPathArray);
                    String[] typePathArray = typePaths.toArray(new String[]{});
                    ArrayUtils.reverse(typePathArray);
                    BizOrgElementPathEntity pathEntity = new BizOrgElementPathEntity(entity.getId(), StringUtils.join(namePathArray, Separator.SLASH.getValue()),
                            StringUtils.join(pyPathArray, Separator.SLASH.getValue()), StringUtils.join(idPathArray, Separator.SLASH.getValue()), false);
                    pathEntity.setBizOrgUuid(uuid);
                    pathEntities.add(pathEntity);
                    for (int i = 0, len = idPathArray.length; i <= len - 1; i++) {
                        String id = idPathArray[i];
                        int level = 1;
                        for (int j = i + 1; j <= len - 1; j++) {
                            BizOrgElementPathChainEntity chainEntity = new BizOrgElementPathChainEntity(id, idPathArray[j], typePathArray[i], typePathArray[j], level++);
                            chainEntity.setBizOrgUuid(uuid);
                            pathChainEntities.add(chainEntity);
                        }
                    }
                }

                bizOrgElementService.saveBizOrgElementPaths(pathEntities);
                bizOrgElementService.saveBizOrgElementPathChains(pathChainEntities);


                if (!disableUuids.isEmpty()) {
                    Map<String, Object> params = Maps.newHashMap();
                    params.put("uuids", disableUuids);
                    this.updateByHQL("update BizOrgElementEntity set enabled = false where uuid in :uuids", params);
                }


            } else if (CollectionUtils.isNotEmpty(bizOrgElementEntities)) {
                // 对应的组织节点都没有的情况下，要把业务组织内的组织节点置为不可用
                this.updateByHQL("update BizOrgElementEntity set enabled = false where orgElementId is not null", null);
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("uuid", uuid);
            this.updateByHQL("update from BizOrganizationEntity set syncLocked=false where uuid=:uuid", params);
        }

    }


    private void syncOrgElementUnderBizOrg(Long bizOrgUuid, BizOrgElementEntity specifyDimensionElement,
                                           List<OrgElementEntity> orgElementEntities, String syncOption,
                                           Set<Long> disableUuids, ImmutableMap<String, OrgElementModelEntity> orgElementModelMap,
                                           Map<Long, OrgElementEntity> orgElementUuidMap, Map<Long, BizOrgElementEntity> savedBizUuidElementMap) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgUuid", bizOrgUuid);
        Set<String> bizOrgEleIds = Sets.newHashSet();
        List<BizOrgElementEntity> bizOrgElements = specifyDimensionElement == null ?
                // 获取之前同步的组织单元实例且非挂载在业务维度下的
                this.bizOrgElementService.listByHQL("from BizOrgElementEntity where parentDimensionUuid is null and orgElementId is not null and bizOrgUuid=:bizOrgUuid", params) :
                // 获取业务维度下的节点，组织树需要同步在业务维度下
                this.bizOrgElementService.getBizOrgElementByParentDimensionUuid(specifyDimensionElement.getUuid());
        List<BizOrgElementEntity> createBizOrgElementEntities = Lists.newArrayList();
        List<BizOrgElementEntity> updateBizOrgElementEntities = Lists.newArrayList();
        Map<String, BizOrgElementEntity> sourceOrgElementIdBizOrgElementMap = Maps.newHashMap();
        Map<String, BizOrgElementEntity> orgElementIdBizEntityMap = Maps.newHashMap();
        Map<Long, BizOrgElementEntity> bizOrgEleUuidMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(bizOrgElements)) {
            for (BizOrgElementEntity element : bizOrgElements) {
                if (StringUtils.isNotBlank(element.getOrgElementId())) {
                    orgElementIdBizEntityMap.put(element.getOrgElementId(), element);
                }
                bizOrgEleUuidMap.put(element.getUuid(), element);
            }
        }

        // 同步组织树的节点到各个业务维度节点下
        for (OrgElementEntity orgElement : orgElementEntities) {
            if (StringUtils.isNotBlank(syncOption)) {
                if (orgElement.getParentUuid() == null && !syncOption.contains("includeSyncTopOrgElement")) {
                    if (orgElementIdBizEntityMap.containsKey(orgElement.getId())) {
                        disableUuids.add(orgElementIdBizEntityMap.get(orgElement.getId()).getUuid());
                    }
                    // 不同步顶级节点
                    continue;
                }
                OrgElementModelEntity eleModel = orgElementModelMap.get(orgElement.getType());
                if (eleModel != null && ((OrgElementModelEntity.Type.MANAGE.equals(eleModel.getType()) && !syncOption.contains("managementOrgElement"))
                        || ((OrgElementModelEntity.Type.MANAGELESS.equals(eleModel.getType()) && !syncOption.contains("noManagementOrgElement"))))) {
                    if (orgElementIdBizEntityMap.containsKey(orgElement.getId())) {
                        disableUuids.add(orgElementIdBizEntityMap.get(orgElement.getId()).getUuid());
                    }
                    continue;
                }
            }
            // 不同步职位节点
            if (orgElement.getType().equalsIgnoreCase(OrgElementModelEntity.ORG_JOB_ID)) {
                continue;
            }
            BizOrgElementEntity bizOrgElement = orgElementIdBizEntityMap.get(orgElement.getId());
            if (bizOrgElement == null) {
                // 新增节点
                BizOrgElementEntity entity = new BizOrgElementEntity();
                entity.setTenant(orgElement.getTenant());
                entity.setSystem(orgElement.getSystem());
                entity.setId(IdPrefix.BIZ_PREFIX.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
                entity.setOrgElementId(orgElement.getId());
                entity.setBizOrgUuid(bizOrgUuid);
                entity.setElementType(orgElement.getType());
                entity.setIsDimension(false);
                entity.setName(orgElement.getName());
                entity.setRemark(orgElement.getRemark());
                entity.setSeq(orgElement.getSeq());
                entity.setEnabled(true);
                if (specifyDimensionElement != null) {
                    entity.setParentDimensionUuid(specifyDimensionElement.getUuid());
                }
                createBizOrgElementEntities.add(entity);
                sourceOrgElementIdBizOrgElementMap.put(entity.getOrgElementId(), entity);

                OrgElementEntity parent = null;
                if (orgElement.getParentUuid() != null) {
                    // 找到合适的父级
                    parent = orgElementUuidMap.get(orgElement.getParentUuid());
                    while (parent != null) {
                        if (parent.getType().equalsIgnoreCase(OrgElementModelEntity.ORG_JOB_ID)) {
                            // 当前节点是职位节点，由于职位节点不同步，则继续向上找父级
                            parent = orgElementUuidMap.get(parent.getParentUuid());
                            continue;
                        }
                        OrgElementModelEntity eleModel = orgElementModelMap.get(parent.getType());
                        if (eleModel != null && ((OrgElementModelEntity.Type.MANAGE.equals(eleModel.getType()) && !syncOption.contains("managementOrgElement"))
                                || ((OrgElementModelEntity.Type.MANAGELESS.equals(eleModel.getType()) && !syncOption.contains("noManagementOrgElement"))))) {
                            parent = orgElementUuidMap.get(parent.getParentUuid());
                            continue;
                        }
                        if (parent.getParentUuid() == null) {
                            parent = !syncOption.contains("includeSyncTopOrgElement") ? null : parent;
                            break;
                        }
                        break;
                    }
                }
                if (parent == null) {
                    entity.setParentUuid(specifyDimensionElement != null ? specifyDimensionElement.getUuid() : null);
                } else {
                    entity.setParentUuid(parent.getUuid());
                }

            } else {
                // 找到合适的父级
                OrgElementEntity parent = orgElementUuidMap.get(orgElement.getParentUuid());
                while (parent != null) {
                    if (parent.getType().equalsIgnoreCase(OrgElementModelEntity.ORG_JOB_ID)) {
                        // 当前节点是职位节点，由于职位节点不同步，则继续向上找父级
                        parent = orgElementUuidMap.get(parent.getParentUuid());
                        continue;
                    }

                    OrgElementModelEntity eleModel = orgElementModelMap.get(parent.getType());
                    if (eleModel != null && ((OrgElementModelEntity.Type.MANAGE.equals(eleModel.getType()) && !syncOption.contains("managementOrgElement"))
                            || ((OrgElementModelEntity.Type.MANAGELESS.equals(eleModel.getType()) && !syncOption.contains("noManagementOrgElement"))))) {
                        parent = orgElementUuidMap.get(parent.getParentUuid());
                        continue;
                    }
                    if (parent.getParentUuid() == null) {
                        parent = !syncOption.contains("includeSyncTopOrgElement") ? null : parent;
                        break;
                    }
                    break;
                }
                boolean changed = false;
                if (parent == null) {
                    changed = bizOrgElement.getParentUuid() != null;
                    bizOrgElement.setParentUuid(specifyDimensionElement != null ? specifyDimensionElement.getUuid() : null);
                } else {
                    BizOrgElementEntity bizParent = bizOrgEleUuidMap.get(bizOrgElement.getParentUuid());
                    if (bizParent != null) {
                        changed = !bizParent.getOrgElementId().equals(parent.getId());
                    }
                    if (changed) {
                        bizOrgElement.setParentUuid(parent.getUuid());
                    }
                }
                OrgElementModelEntity eleModel = orgElementModelMap.get(bizOrgElement.getElementType());
                boolean enabled = !(eleModel != null && ((OrgElementModelEntity.Type.MANAGE.equals(eleModel.getType()) && !syncOption.contains("managementOrgElement"))
                        || ((OrgElementModelEntity.Type.MANAGELESS.equals(eleModel.getType()) && !syncOption.contains("noManagementOrgElement")))));
                if (!changed) {
                    changed = !bizOrgElement.getEnabled().equals(enabled);
                }
                bizOrgElement.setEnabled(enabled);
                if (!changed) {
                    changed = !bizOrgElement.getName().equals(orgElement.getName());
                }
                bizOrgElement.setName(orgElement.getName());
                if (!changed && bizOrgElement.getRemark() != null) {
                    changed = !bizOrgElement.getRemark().equals(orgElement.getRemark());
                }
                bizOrgElement.setRemark(orgElement.getRemark());
                if (changed) {
                    updateBizOrgElementEntities.add(bizOrgElement);
                } else {
                    savedBizUuidElementMap.put(bizOrgElement.getUuid(), bizOrgElement);
                }

                sourceOrgElementIdBizOrgElementMap.put(bizOrgElement.getOrgElementId(), bizOrgElement);
            }
        }

        List<OrgElementI18nEntity> orgElementI18ns = orgElementI18nService.listOrgElementI18ns(orgElementUuidMap.keySet(), "name", null);
        Map<String, List<OrgElementI18nEntity>> i18nMap = Maps.newHashMap();
        for (OrgElementI18nEntity i : orgElementI18ns) {
            if (!i18nMap.containsKey(i.getDataId())) {
                i18nMap.put(i.getDataId(), Lists.newArrayList());
            }
            i18nMap.get(i.getDataId()).add(i);
        }
        if (CollectionUtils.isNotEmpty(createBizOrgElementEntities)) {
            this.bizOrgElementService.saveAll(createBizOrgElementEntities);
            for (BizOrgElementEntity entity : createBizOrgElementEntities) {
                if (i18nMap.containsKey(entity.getOrgElementId())) {
                    List<OrgElementI18nEntity> i18nEntities = Lists.newArrayList();
                    List<OrgElementI18nEntity> list = i18nMap.get(entity.getOrgElementId());
                    for (OrgElementI18nEntity e : list) {
                        i18nEntities.add(new OrgElementI18nEntity(entity.getUuid(), entity.getId(), "name", e.getLocale(), e.getContent()));
                    }
                    orgElementI18nService.saveAll(i18nEntities);
                }
                bizOrgEleIds.add(entity.getId());
                // 更新父级uuid
                if (entity.getParentUuid() != null) {
                    OrgElementEntity orgElementEntity = orgElementUuidMap.get(entity.getParentUuid());
                    if (orgElementEntity != null) {
                        BizOrgElementEntity parent = sourceOrgElementIdBizOrgElementMap.get(orgElementEntity.getId());
                        if (parent != null) {
                            entity.setParentUuid(parent.getUuid());
                        }
                    }
                }

                savedBizUuidElementMap.put(entity.getUuid(), entity);
            }
            this.bizOrgElementService.saveAll(createBizOrgElementEntities);
        }
        if (CollectionUtils.isNotEmpty(updateBizOrgElementEntities)) {
            this.bizOrgElementService.saveAll(updateBizOrgElementEntities);
            for (BizOrgElementEntity entity : updateBizOrgElementEntities) {
                bizOrgEleIds.add(entity.getId());
                // 更新父级uuid
                if (entity.getParentUuid() != null) {
                    OrgElementEntity orgElementEntity = orgElementUuidMap.get(entity.getParentUuid());
                    if (orgElementEntity != null) {
                        BizOrgElementEntity parent = sourceOrgElementIdBizOrgElementMap.get(orgElementEntity.getId());
                        if (parent != null) {
                            entity.setParentUuid(parent.getUuid());
                        }
                    }
                }

                savedBizUuidElementMap.put(entity.getUuid(), entity);
            }
            this.bizOrgElementService.saveAll(updateBizOrgElementEntities);
        }


    }


    @Override
    public List<BizOrganizationEntity> getBizOrgByOrgUuid(Long orgUuid) {
        return dao.listByFieldEqValue("orgUuid", orgUuid);
    }

    @Override
    public List<BizOrganizationEntity> getValidBizOrgByOrgUuid(Long orgUuid) {
        List<BizOrganizationEntity> list = this.getBizOrgByOrgUuid(orgUuid);
        return filterValidBizOrg(list);
    }

    /**
     * @param list
     * @return
     */
    private List<BizOrganizationEntity> filterValidBizOrg(List<BizOrganizationEntity> list) {
        Iterator<BizOrganizationEntity> iterator = list.iterator();
        while (iterator.hasNext()) {
            BizOrganizationEntity entity = iterator.next();
            if (BooleanUtils.isFalse(entity.getEnable())
                    || BooleanUtils.isTrue(entity.getExpired()) || (BooleanUtils.isFalse(entity.getNeverExpire()) && entity.getExpireTime() != null
                    && DateUtils.addDays(entity.getExpireTime(), 1).compareTo(new Date()) < 0)) {
                iterator.remove();
            }
        }
        return list;
    }

    @Override
    public List<BizOrganizationEntity> listBizOrgByOrgId(String orgId) {
        String hql = "from BizOrganizationEntity t where t.orgUuid in(select t1.uuid from OrganizationEntity t1 where t1.id = :orgId)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgId", orgId);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<BizOrganizationEntity> listValidBizOrgByOrgId(String orgId) {
        List<BizOrganizationEntity> list = this.listBizOrgByOrgId(orgId);
        return filterValidBizOrg(list);
    }

    @Override
    @Transactional
    public void batchUpdateAllBizOrgExpiredExceedExpiredTime() {
        Map<String, Object> param = Maps.newHashMap();
        param.put("sysdate", DateUtils.ceiling(new Date(), Calendar.DATE));
        this.dao.updateByHQL("update BizOrganizationEntity set expired = true where neverExpire = false and expireTime is not null and expireTime < :sysdate ", param);
    }

    @Override
    public Long getBizOrgUuidByBizOrgId(String bizOrgId) {
        BizOrganizationEntity bizOrganization = getById(bizOrgId);
        return bizOrganization != null ? bizOrganization.getUuid() : null;
    }

    @Override
    @Transactional
    public void saveBizOrgConfig(BizOrgConfigDto dto) {
        BizOrgConfigEntity config = dto.getUuid() != null ? bizOrgConfigDao.getOne(dto.getUuid()) : new BizOrgConfigEntity();
        String lastBizOrgDimensionId = null;
        if (config.getUuid() != null) {
            lastBizOrgDimensionId = config.getBizOrgDimensionId();
            BeanUtils.copyProperties(dto, config, ArrayUtils.addAll(config.BASE_FIELDS, "system", "tenant"));
        } else {
            BeanUtils.copyProperties(dto, config);
            config.setSystem(RequestSystemContextPathResolver.system());
            if (StringUtils.isNotBlank(config.getSystem())) {
                config.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
        }
        bizOrgConfigDao.save(config);
        if (dto.getUuid() != null && StringUtils.isNotBlank(dto.getBizOrgDimensionId())
                && StringUtils.isNotBlank(lastBizOrgDimensionId) && !dto.getBizOrgDimensionId().equals(lastBizOrgDimensionId)) {
            // 维度变更，需要更新该业务组织内的节点实例维度类型
            bizOrgElementService.updateAllBizOrgDimensionElementType(dto.getBizOrgDimensionId(), dto.getBizOrgUuid());
        }
        List<BizOrgRoleEntity> existRoles = bizOrgRoleDao.listByFieldEqValue("bizOrgUuid", dto.getBizOrgUuid());
        Set<String> roleIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(existRoles)) {
            for (BizOrgRoleEntity r : existRoles) {
                roleIds.add(r.getId());
            }
        }
        bizOrgRoleDao.deleteByBizOrgUuid(dto.getBizOrgUuid());
        if (CollectionUtils.isNotEmpty(dto.getBizOrgRoles())) {
            List<BizOrgRoleEntity> roles = Lists.newArrayList();
            for (BizOrgRoleEntity temp : dto.getBizOrgRoles()) {
                BizOrgRoleEntity role = new BizOrgRoleEntity();
                BeanUtils.copyProperties(temp, role, role.BASE_FIELDS);
                role.setSystem(RequestSystemContextPathResolver.system());
                if (StringUtils.isNotBlank(role.getSystem())) {
                    role.setTenant(SpringSecurityUtils.getCurrentTenantId());
                }
                role.setBizOrgUuid(config.getBizOrgUuid());
                roles.add(role);
                roleIds.remove(role.getId());
                bizOrgRoleDao.save(role);
                if (CollectionUtils.isNotEmpty(temp.getI18ns())) {
                    for (OrgElementI18nEntity e : temp.getI18ns()) {
                        e.setDataUuid(role.getUuid());
                        e.setDataId(role.getId());
                    }
                    orgElementI18nService.saveAllAfterDelete(role.getUuid(), temp.getI18ns());
                }
            }
        }
        if (!roleIds.isEmpty()) {
            bizOrgElementMemberService.deleteAllMemberByBizOrgRoleIdAndBizOrgUuid(roleIds, dto.getBizOrgUuid());
        }


    }
}
