package com.wellsoft.pt.org.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobDuty;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import com.wellsoft.pt.org.dao.impl.OrgVersionDaoImpl;
import com.wellsoft.pt.org.dto.OrgVersionDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.query.OrgVersionQueryItem;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;
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
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
@Service
public class OrgVersionServiceImpl extends AbstractJpaServiceImpl<OrgVersionEntity, OrgVersionDaoImpl, Long> implements OrgVersionService {

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    OrgElementPathChainService orgElementPathChainService;

    @Resource
    OrgSettingService orgSettingService;

    @Resource
    OrgRoleService orgRoleService;

    @Resource
    OrgElementRoleMemberService orgElementRoleMemberService;

    @Resource
    OrgElementManagementService orgElementManagementService;


    @Resource
    OrganizationService organizationService;

    @Resource
    MultiOrgJobDutyService multiOrgJobDutyService;

    @Resource
    OrgElementRoleRelaService orgElementRoleRelaService;

    @Autowired
    OrgUserService orgUserService;

    @Autowired
    OrgUserReportRelationService orgUserReportRelationService;

    @Autowired
    OrgElementI18nService orgElementI18nService;


    @Override
    @Transactional
    public Long saveCopyByUuid(Long uuid, boolean copyUser) {
        OrgVersionEntity versionEntity = new OrgVersionEntity();
        OrgVersionEntity fromVersion = null;
        if (uuid != null) {
            fromVersion = getOne(uuid);
            List<String> ignoreFields = Lists.newArrayList(JpaEntity.BASE_FIELDS);
            ignoreFields.addAll(Lists.newArrayList("publishTime", "approve", "invalidTime", "effectTime"));
            BeanUtils.copyProperties(fromVersion, versionEntity, ignoreFields.toArray(new String[0]));
            versionEntity.setSourceUuid(uuid);
        }
        versionEntity.setId(IdPrefix.ORG_VERSION.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
        versionEntity.setState(OrgVersionEntity.State.DESIGNING);
        boolean enableAuditOrgVersion = orgSettingService.isEnable("ORG_VERSION_AUDIT_FLOW_ENABLE", null, null);
        if (enableAuditOrgVersion) {
            versionEntity.setApprove(OrgVersionEntity.Approve.ING);
        }
        save(versionEntity);
        if (uuid == null) {
            return versionEntity.getUuid();
        }
        // 拷贝组织单元实例
        List<OrgElementEntity> fromOrgElementEnties = orgElementService.listByOrgVersionUuid(fromVersion.getUuid());
        if (CollectionUtils.isNotEmpty(fromOrgElementEnties)) {
            List<OrgElementEntity> waitSaveOrgElementEntityList = Lists.newArrayListWithCapacity(fromOrgElementEnties.size());
            Map<Long, Long> newOldUuidMap = Maps.newHashMap();
            //FIXME: 父级ID映射
            for (OrgElementEntity fromOrgElement : fromOrgElementEnties) {
                OrgElementEntity elementEntity = new OrgElementEntity();
                BeanUtils.copyProperties(fromOrgElement, elementEntity, elementEntity.BASE_FIELDS);
                elementEntity.setOrgVersionId(versionEntity.getId());
                elementEntity.setOrgVersionUuid(versionEntity.getUuid());
                elementEntity.setState(OrgVersionEntity.State.DESIGNING);
                orgElementService.save(elementEntity);
                newOldUuidMap.put(fromOrgElement.getUuid(), elementEntity.getUuid());
                waitSaveOrgElementEntityList.add(elementEntity);
                List<OrgElementI18nEntity> elementI18nEntities = orgElementI18nService.getOrgElementI18ns(fromOrgElement.getUuid());
                if (CollectionUtils.isNotEmpty(elementI18nEntities)) {
                    List<OrgElementI18nEntity> newI18ns = Lists.newArrayList();
                    for (OrgElementI18nEntity e : elementI18nEntities) {
                        OrgElementI18nEntity i18n = new OrgElementI18nEntity();
                        BeanUtils.copyProperties(e, i18n, i18n.BASE_FIELDS);
                        i18n.setDataUuid(elementEntity.getUuid());
                        newI18ns.add(i18n);
                    }
                    orgElementI18nService.saveAll(newI18ns);
                }

            }
            for (OrgElementEntity newOrgElement : waitSaveOrgElementEntityList) {
                if (newOrgElement.getParentUuid() != null) {
                    newOrgElement.setParentUuid(newOldUuidMap.get(newOrgElement.getParentUuid()));
                }
            }
            orgElementService.saveAll(waitSaveOrgElementEntityList);


            ImmutableMap<String, OrgElementEntity> map = Maps.uniqueIndex(waitSaveOrgElementEntityList, OrgElementEntity::getId);
            // 拷贝组织单元实例扩展属性
            List<OrgElementExtAttrEntity> fromExtAttrs = orgElementService.listExtAttrsByOrgVersionUuid(fromVersion.getUuid());
            List<OrgElementExtAttrEntity> waitSaveExtAttrs = Lists.newArrayListWithCapacity(fromExtAttrs.size());
            for (OrgElementExtAttrEntity fromAttr : fromExtAttrs) {
                OrgElementExtAttrEntity attrEntity = new OrgElementExtAttrEntity();
                BeanUtils.copyProperties(fromAttr, attrEntity, attrEntity.BASE_FIELDS);
                attrEntity.setOrgVersionUuid(versionEntity.getUuid());
                attrEntity.setOrgElementUuid(map.get(fromAttr.getOrgElementId()).getUuid());
                waitSaveExtAttrs.add(attrEntity);
            }
            orgElementService.saveExtAttrs(waitSaveExtAttrs);

            // 拷贝组织单元实例管理信息
            List<OrgElementManagementEntity> fromManagements = orgElementManagementService.listByOrgVersionUuid(fromVersion.getUuid());
            List<OrgElementManagementEntity> waitSaveManagements = Lists.newArrayListWithCapacity(fromManagements.size());
            for (OrgElementManagementEntity fm : fromManagements) {
                OrgElementManagementEntity m = new OrgElementManagementEntity();
                BeanUtils.copyProperties(fm, m, m.BASE_FIELDS);
                m.setOrgVersionUuid(versionEntity.getUuid());
                m.setOrgElementUuid(map.get(fm.getOrgElementId()).getUuid());
                waitSaveManagements.add(m);
            }
            orgElementManagementService.saveAll(waitSaveManagements);

            // 拷贝组织单元实例路径
            List<OrgElementPathEntity> fromPathEntities = orgElementPathService.listByOrgVersionUuid(fromVersion.getUuid());
            List<OrgElementPathEntity> waitSaveOrgElementPathEntityList = Lists.newArrayListWithCapacity(fromPathEntities.size());
            for (OrgElementPathEntity fromPath : fromPathEntities) {
                OrgElementPathEntity pathEntity = new OrgElementPathEntity();
                BeanUtils.copyProperties(fromPath, pathEntity, pathEntity.BASE_FIELDS);
                pathEntity.setOrgVersionUuid(versionEntity.getUuid());
                pathEntity.setOrgElementUuid(newOldUuidMap.get(fromPath.getOrgElementUuid()));
                waitSaveOrgElementPathEntityList.add(pathEntity);
            }
            orgElementPathService.saveAll(waitSaveOrgElementPathEntityList);

            // 拷贝组织单元实例路径链
            List<OrgElementPathChainEntity> fromPathChainEntities = orgElementPathChainService.listByOrgVersionUuid(fromVersion.getUuid());
            List<OrgElementPathChainEntity> waitSaveChains = Lists.newArrayListWithCapacity(fromPathEntities.size());
            for (OrgElementPathChainEntity fromPath : fromPathChainEntities) {
                OrgElementPathChainEntity pathEntity = new OrgElementPathChainEntity();
                BeanUtils.copyProperties(fromPath, pathEntity, pathEntity.BASE_FIELDS);
                pathEntity.setOrgVersionUuid(versionEntity.getUuid());
                waitSaveChains.add(pathEntity);
            }
            orgElementPathChainService.saveAll(waitSaveChains);

            // 拷贝职位职务
            List<MultiOrgJobDuty> fromJobDuties = multiOrgJobDutyService.listByOrgVersionUuid(fromVersion.getUuid());
            List<MultiOrgJobDuty> waitSaveDuties = Lists.newArrayListWithCapacity(fromJobDuties.size());
            for (MultiOrgJobDuty fromDuty : fromJobDuties) {
                MultiOrgJobDuty duty = new MultiOrgJobDuty();
                BeanUtils.copyProperties(fromDuty, duty, duty.BASE_FIELDS);
                duty.setOrgVersionUuid(versionEntity.getUuid());
                waitSaveDuties.add(duty);
            }
            multiOrgJobDutyService.saveAll(waitSaveDuties);

            //TODO: 拷贝组织单元用户关系
            if (copyUser) {
//                List<OrgVersionUserObjRelaEntity> relaEntities = orgVersionUserObjRelaService.listByOrgVersionUuid(fromVersion.getUuid());
//                List<OrgVersionUserObjRelaEntity> waitSaveRelas = Lists.newArrayListWithCapacity(relaEntities.size());
//                for (OrgVersionUserObjRelaEntity rela : relaEntities) {
//                    OrgVersionUserObjRelaEntity r = new OrgVersionUserObjRelaEntity();
//                    BeanUtils.copyProperties(rela, r, rela.BASE_FIELDS);
//                    r.setOrgVersionUuid(versionEntity.getUuid());
//                    waitSaveRelas.add(r);
//                }
//                orgVersionUserObjRelaService.saveAll(waitSaveRelas);

                List<OrgUserEntity> orgUserEntities = orgUserService.listByOrgVersionUuid(fromVersion.getUuid());
                List<OrgUserEntity> waitSaveOrgUsers = Lists.newArrayListWithCapacity(orgUserEntities.size());
                for (OrgUserEntity u : orgUserEntities) {
                    OrgUserEntity user = new OrgUserEntity();
                    BeanUtils.copyProperties(u, user, user.BASE_FIELDS);
                    user.setOrgVersionUuid(versionEntity.getUuid());
                    waitSaveOrgUsers.add(user);
                }
                orgUserService.saveAll(waitSaveOrgUsers);

                List<OrgUserReportRelationEntity> orgUserReportRelationEntities = orgUserReportRelationService.listByOrgVersionUuid(fromVersion.getUuid());
                List<OrgUserReportRelationEntity> waitSaveOrgUserRelas = Lists.newArrayListWithCapacity(orgUserReportRelationEntities.size());
                for (OrgUserReportRelationEntity rela : orgUserReportRelationEntities) {
                    OrgUserReportRelationEntity r = new OrgUserReportRelationEntity();
                    BeanUtils.copyProperties(rela, r, rela.BASE_FIELDS);
                    r.setOrgVersionUuid(versionEntity.getUuid());
                    waitSaveOrgUserRelas.add(r);
                }
                orgUserReportRelationService.saveAll(waitSaveOrgUserRelas);
            }

            // 拷贝组织单元实例下的角色关系
            List<OrgElementRoleRelaEntity> fromElementRoleRelas = orgElementRoleRelaService.listByOrgVersionUuid(fromVersion.getUuid());
            List<OrgElementRoleRelaEntity> waitSaveRoleRelas = Lists.newArrayListWithCapacity(fromElementRoleRelas.size());
            for (OrgElementRoleRelaEntity rela : fromElementRoleRelas) {
                if (newOldUuidMap.get(rela.getOrgElementUuid()) == null) {
                    logger.warn("角色关系{}的组织元素{}不存在", rela.getUuid(), rela.getOrgElementUuid());
                    continue;
                }
                OrgElementRoleRelaEntity newRela = new OrgElementRoleRelaEntity();
                BeanUtils.copyProperties(rela, newRela, rela.BASE_FIELDS);
                newRela.setOrgElementUuid(newOldUuidMap.get(rela.getOrgElementUuid()));
                newRela.setOrgVersionUuid(versionEntity.getUuid());
                waitSaveRoleRelas.add(newRela);
            }
            orgElementRoleRelaService.saveAll(waitSaveRoleRelas);


        }

        return versionEntity.getUuid();

    }

    @Override
    @Transactional
    public void updatePublished(Long uuid) {
        OrgVersionEntity entity = getOne(uuid);
        if (entity != null && OrgVersionEntity.State.DESIGNING.equals(entity.getState())) {
            OrgVersionEntity lastPublishedVersion = getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, entity.getOrgUuid());
            entity.setState(OrgVersionEntity.State.PUBLISHED);
            entity.setEffectTime(new Date());
            entity.setPublishTime(null);
            if (lastPublishedVersion != null && lastPublishedVersion.getVer() != null) {
                entity.setVer(lastPublishedVersion.getVer() + 0.1f);
            } else {
                entity.setVer(1.0f);
            }
            save(entity);

            orgElementService.updateStateByOrgVersionUuid(OrgVersionEntity.State.PUBLISHED, entity.getUuid());

            // 修改正式的为历史版
            if (lastPublishedVersion != null) {
                lastPublishedVersion.setState(OrgVersionEntity.State.HISTORY);
                lastPublishedVersion.setInvalidTime(entity.getEffectTime());
//                Map<String, Object> params = Maps.newHashMap();
//                params.put("orgUuid", entity.getOrgUuid());
//                params.put("state", OrgVersionEntity.State.HISTORY);
//                Float ver = dao.getNumberByHQL("select max(ver) from OrgVersionEntity where orgUuid=:orgUuid and state=:state", params, Float.class);
//                if (ver != null) {
//                    lastPublishedVersion.setVer(ver + 0.1f);
//                }
                save(lastPublishedVersion);

                orgElementService.updateStateByOrgVersionUuid(OrgVersionEntity.State.HISTORY, entity.getUuid());
            }
        }
    }

    @Override
    public OrgVersionEntity getByStateAndOrgUuid(OrgVersionEntity.State state, Long orgUuid) {
        OrgVersionEntity example = new OrgVersionEntity();
        example.setState(state);
        example.setOrgUuid(orgUuid);
        List<OrgVersionEntity> list = listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void updatePublishTime(Long uuid, Date date) {
        OrgVersionEntity entity = getOne(uuid);
        if (entity != null) {
            entity.setPublishTime(date);
            save(entity);
        }
    }


    @Override
    @Transactional
    public void deleteOrgVersion(Long uuid) {
        delete(uuid);
        orgElementService.deleteByOrgVersionUuid(uuid);
        orgElementPathService.deleteByOrgVersionUuid(uuid);
        orgElementPathChainService.deleteByOrgVersionUuid(uuid);
        orgRoleService.delteOrgRoleByOrgVersionUuid(uuid);
        orgElementManagementService.deleteByOrgVersionUuid(uuid);
        orgElementRoleMemberService.deleteByOrgVersionUuid(uuid);
//        orgVersionUserObjRelaService.deleteByOrgVersionUuid(uuid);
        orgUserService.deleteByOrgVersionUuid(uuid);
        orgUserReportRelationService.deleteByOrgVersionUuid(uuid);
        orgElementRoleRelaService.deleteByOrgVersionUuid(uuid);
        multiOrgJobDutyService.deleteByJobIdAndOrgVersionUuid(uuid);

    }

    @Override
    public OrgVersionDto getDetailsByUuid(Long uuid) {
        OrgVersionDto dto = new OrgVersionDto();
        OrgVersionEntity entity = getOne(uuid);
        BeanUtils.copyProperties(entity, dto);
        List<OrgElementEntity> orgElementEntities = orgElementService.listByOrgVersionUuid(uuid);
        dto.setOrgElements(orgElementEntities);
        List<OrgRoleEntity> orgRoleEntities = orgRoleService.listByOrgVersionUuid(uuid);
        dto.setOrgRoles(orgRoleEntities);
        dto.setOrganization(organizationService.getOne(entity.getOrgUuid()));
        return dto;
    }

    @Override
    @Transactional
    public void deleteAllByOrgUuid(Long orgUuid) {
        List<OrgVersionEntity> versionEntities = dao.listByFieldEqValue("orgUuid", orgUuid);
        if (CollectionUtils.isNotEmpty(versionEntities)) {
            for (OrgVersionEntity v : versionEntities) {
                this.deleteOrgVersion(v.getUuid());
            }
        }
    }

    @Override
    public OrgVersionEntity getById(String orgVersionId) {
        return this.dao.getOneByFieldEq("id", orgVersionId);
    }

    /**
     * 获取默认组织使用的组织版本
     *
     * @param system
     * @return
     */
    @Override
    public OrgVersionEntity getDefaultOrgVersionBySystem(String system) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.getOneByNameHQLQuery("getDefaultOrgVersionBySystem", params);
    }

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgId
     * @return
     */
    @Override
    public OrgVersionEntity getOrgVersionByOrgId(String orgId) {
        Assert.hasLength(orgId, "组织ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("orgId", orgId);
        return this.dao.getOneByNameHQLQuery("getOrgVersionByOrgIdAndSystem", params);
    }

    @Override
    public OrgVersionEntity getOrgVersionByOrgUuid(Long orgUuid) {
        Assert.notNull(orgUuid, "组织UUID不能为空！");

        String hql = "from OrgVersionEntity t where t.orgUuid = :orgUuid and t.state = :state";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgUuid", orgUuid);
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        List<OrgVersionEntity> orgVersionEntities = this.dao.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(orgVersionEntities) ? orgVersionEntities.get(0) : null;
    }

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    @Override
    public OrgVersionEntity getLatestOrgVersionByOrgVersionId(String orgVersionId) {
        Assert.hasLength(orgVersionId, "组织版本ID不能为空！");

        String hql = "from OrgVersionEntity t where t.orgUuid = (select v.orgUuid from OrgVersionEntity v where v.id = :orgVersionId) and t.state = :state";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionId", orgVersionId);
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        List<OrgVersionEntity> entities = this.dao.listByHQL(hql, params);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 根据组织ID列表，获取组织版本列表
     *
     * @param orgIds
     * @return
     */
    @Override
    public List<OrgVersionEntity> listByOrgIds(List<String> orgIds) {
        Assert.notEmpty(orgIds, "组织ID列表不能为空！");

        String hql = "from OrgVersionEntity t where t.state = :state and t.orgUuid in (select o.uuid from OrganizationEntity o where o.id in(:orgIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        params.put("orgIds", orgIds);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgVersionEntity> listByOrgIdsAndSystem(List<String> orgIds, String system) {
        Assert.notEmpty(orgIds, "组织ID列表不能为空！");
        Assert.hasLength(system, "归属系统不能为空！");

        String hql = "from OrgVersionEntity t where t.state = :state and t.orgUuid in (select o.uuid from OrganizationEntity o where o.id in(:orgIds)) and t.system = :system";
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        params.put("orgIds", orgIds);
        params.put("system", system);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgVersionEntity> listByIds(Set<String> ids) {
        Assert.notEmpty(ids, "组织版本ID列表不能为空！");

        String hql = "from OrgVersionEntity t where t.state = :state and t.id in (:ids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        params.put("ids", ids);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgVersionEntity> listAllByOrgUuid(Long orgUuid) {
        String hql = "from OrgVersionEntity t where t.orgUuid = :orgUuid order by state asc,ver desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgUuid", orgUuid);
        return this.listByHQL(hql, params);
    }

    /**
     * 根据组织版本ID列表，获取组织版本信息列表
     *
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgVersionQueryItem> listItemByOrgVersionIds(List<String> orgVersionIds) {
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        params.put("orgVersionIds", orgVersionIds);
        return this.listItemByNameHQLQuery("listOrgVersionQueryItem", OrgVersionQueryItem.class, params, new PagingInfo(1, Integer.MAX_VALUE));
    }

    @Override
    public List<OrgVersionEntity> listAllByOrgId(String orgId) {
        String hql = "from OrgVersionEntity t where exists ( select 1 from OrganizationEntity o where o.uuid = t.orgUuid and o.id =:id ) order by state asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", orgId);
        return this.listByHQL(hql, params);
    }


    /**
     * 获取系统可使用的组织版本
     *
     * @param system
     * @return
     */
    @Override
    public List<OrgVersionEntity> listPublishedBySystemAndTenant(String system, String tenant) {
        OrgVersionEntity entity = new OrgVersionEntity();
        entity.setSystem(system);
        entity.setState(OrgVersionEntity.State.PUBLISHED);
        entity.setTenant(tenant);
        return this.dao.listByEntity(entity);
    }

    /**
     * 从组织版本中创建空的组织版本
     *
     * @param fromVersion
     * @param copyUser
     * @return
     */
    @Override
    @Transactional
    public OrgVersionEntity createEmptyOrgVersionFromOrgVersion(OrgVersionEntity fromVersion, boolean copyUser) {
        OrgVersionEntity newVersion = new OrgVersionEntity();
        newVersion.setState(OrgVersionEntity.State.DESIGNING);
        newVersion.setOrgUuid(fromVersion.getOrgUuid());
        newVersion.setId(IdPrefix.ORG_VERSION.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
        newVersion.setTenant(fromVersion.getTenant());
        newVersion.setSystem(fromVersion.getSystem());
        this.save(newVersion);

        if (copyUser) {
            List<OrgUserEntity> orgUserEntities = orgUserService.listByOrgVersionUuidAndType(fromVersion.getUuid(), OrgUserEntity.Type.MEMBER_USER);
            List<OrgUserEntity> waitSaveOrgUsers = Lists.newArrayListWithCapacity(orgUserEntities.size());
            for (OrgUserEntity u : orgUserEntities) {
                OrgUserEntity user = new OrgUserEntity();
                BeanUtils.copyProperties(u, user, user.BASE_FIELDS);
                user.setUserPath(user.getUserId());
                user.setOrgElementId(null);
                user.setOrgElementType(null);
                user.setSystem(newVersion.getSystem());
                user.setTenant(newVersion.getTenant());
                user.setOrgVersionUuid(newVersion.getUuid());
                waitSaveOrgUsers.add(user);
            }
            orgUserService.saveAll(waitSaveOrgUsers);

//            List<OrgUserReportRelationEntity> orgUserReportRelationEntities = orgUserReportRelationService.listByOrgVersionUuid(fromVersion.getUuid());
//            List<OrgUserReportRelationEntity> waitSaveOrgUserRelas = Lists.newArrayListWithCapacity(orgUserReportRelationEntities.size());
//            for (OrgUserReportRelationEntity rela : orgUserReportRelationEntities) {
//                OrgUserReportRelationEntity r = new OrgUserReportRelationEntity();
//                BeanUtils.copyProperties(rela, r, rela.BASE_FIELDS);
//                r.setOrgVersionUuid(newVersion.getUuid());
//                waitSaveOrgUserRelas.add(r);
//            }
//            orgUserReportRelationService.saveAll(waitSaveOrgUserRelas);
        }

        return newVersion;
    }

    @Override
    public List<OrgVersionEntity> getAllPublishedVersionBySystem(String system) {
        OrgVersionEntity example = new OrgVersionEntity();
        example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        example.setSystem(system);
        example.setState(OrgVersionEntity.State.PUBLISHED);
        return listByEntity(example);
    }

    @Override
    @Transactional
    public void batchUpdatePublish(Date date) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("state", OrgVersionEntity.State.DESIGNING);
        params.put("time", date);
        List<OrgVersionEntity> list = listByHQL("from OrgVersionEntity where state = :state and publishTime is not null and publishTime <=:time", params);
        if (CollectionUtils.isNotEmpty(list)) {
            for (OrgVersionEntity entity : list) {
                try {
                    this.updatePublished(entity.getUuid());
                } catch (Exception e) {
                    logger.error("组织版本{}发布更新失败：{}", entity.getUuid(), Throwables.getStackTraceAsString(e));
                }
            }
        }
    }


}
