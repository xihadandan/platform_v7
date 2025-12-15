package com.wellsoft.pt.org.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.OrgUnitCodeDao;
import com.wellsoft.pt.org.dao.OrgUnitExtAttrDao;
import com.wellsoft.pt.org.dao.impl.OrgUnitDaoImpl;
import com.wellsoft.pt.org.dto.OrgUnitDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
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
 * 2022年11月16日   chenq	 Create
 * </pre>
 */
@Service
public class OrgUnitServiceImpl extends AbstractJpaServiceImpl<OrgUnitEntity, OrgUnitDaoImpl, Long> implements OrgUnitService {

    @Resource
    OrgUnitExtAttrDao orgUnitExtAttrDao;

    @Resource
    OrgUnitCodeDao orgUnitCodeDao;

    @Resource
    OrgElementModelService orgElementModelService;

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrganizationService organizationService;

    @Autowired
    OrgElementI18nService orgElementI18nService;

    @Override
    @Transactional
    public Long saveOrgUnit(OrgUnitDto orgUnitDto) {
        // 数据拷贝
        OrgUnitEntity unitEntity = new OrgUnitEntity();
        if (orgUnitDto.getUuid() != null) {
            unitEntity = getOne((orgUnitDto.getUuid()));
            List<OrgUnitExtAttrEntity> orgUnitExtAttrEntities = orgUnitExtAttrDao.getByOrgUnitUuid(unitEntity.getUuid());
            Map<Long, OrgUnitExtAttrEntity> existOrgUnitExtAttrs = Maps.uniqueIndex(orgUnitExtAttrEntities, new Function<OrgUnitExtAttrEntity, Long>() {
                @Nullable
                @Override
                public Long apply(@Nullable OrgUnitExtAttrEntity input) {
                    return input.getUuid();
                }
            });

            List<OrgUnitCodeEntity> orgUnitCodeEntities = orgUnitCodeDao.getByOrgUnitUuid(unitEntity.getUuid());
            Map<Long, OrgUnitCodeEntity> existOrgUnitCodes = Maps.uniqueIndex(orgUnitCodeEntities, new Function<OrgUnitCodeEntity, Long>() {
                @Nullable
                @Override
                public Long apply(@Nullable OrgUnitCodeEntity input) {
                    return input.getUuid();
                }
            });
            Set<Long> updateUuids = Sets.newHashSet();
            List<OrgUnitExtAttrEntity> updateOrgUnitExtAttrEntityList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(orgUnitDto.getOrgUnitExtAttrs())) {
                for (OrgUnitExtAttrEntity extAttrEntity : orgUnitDto.getOrgUnitExtAttrs()) {
                    extAttrEntity.setOrgUnitUuid(unitEntity.getUuid());
                    extAttrEntity.setOrgUnitId(unitEntity.getId());
                    if (extAttrEntity.getUuid() != null) {
                        OrgUnitExtAttrEntity unitExtAttrEntity = existOrgUnitExtAttrs.get(extAttrEntity.getUuid());
                        BeanUtils.copyProperties(extAttrEntity, unitExtAttrEntity, unitExtAttrEntity.BASE_FIELDS);
                        updateOrgUnitExtAttrEntityList.add(unitExtAttrEntity);
                        updateUuids.add(extAttrEntity.getUuid());
                    } else {
                        updateOrgUnitExtAttrEntityList.add(extAttrEntity);
                    }
                }

            }

            if (!updateOrgUnitExtAttrEntityList.isEmpty()) {
                orgUnitExtAttrDao.saveAll(updateOrgUnitExtAttrEntityList);
            }

            Set<Long> existExtAttrUuids = existOrgUnitExtAttrs.keySet();
            Set<Long> deleteUuids = SetUtils.difference(
                    existExtAttrUuids.size() > updateUuids.size() ? existExtAttrUuids : updateUuids
                    , existExtAttrUuids.size() > updateUuids.size() ? updateUuids : existExtAttrUuids).toSet();
            if (CollectionUtils.isNotEmpty(deleteUuids)) {
                orgUnitExtAttrDao.deleteByUuids(deleteUuids);
            }


            updateUuids.clear();
            List<OrgUnitCodeEntity> updateOrgUnitCodeEntityList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(orgUnitDto.getOrgUnitCodes())) {
                for (OrgUnitCodeEntity unitCodeEntity : orgUnitDto.getOrgUnitCodes()) {
                    unitCodeEntity.setOrgUnitUuid(unitEntity.getUuid());
                    unitCodeEntity.setOrgUnitId(unitEntity.getId());
                    if (unitCodeEntity.getUuid() != null) {
                        OrgUnitCodeEntity existUnitCodeEntity = existOrgUnitCodes.get(unitCodeEntity.getUuid());
                        BeanUtils.copyProperties(unitCodeEntity, existUnitCodeEntity, OrgUnitCodeEntity.BASE_FIELDS);
                        updateOrgUnitCodeEntityList.add(existUnitCodeEntity);
                        updateUuids.add(existUnitCodeEntity.getUuid());
                    } else {
                        updateOrgUnitCodeEntityList.add(unitCodeEntity);
                    }
                }

            }

            if (!updateOrgUnitCodeEntityList.isEmpty()) {
                orgUnitCodeDao.saveAll(updateOrgUnitCodeEntityList);
            }
            Set<Long> exitCodeUuids = existOrgUnitCodes.keySet();
            deleteUuids = SetUtils.difference(exitCodeUuids.size() > updateUuids.size() ? exitCodeUuids : updateUuids, exitCodeUuids.size() > updateUuids.size() ? updateUuids : exitCodeUuids).toSet();
            if (CollectionUtils.isNotEmpty(deleteUuids)) {
                orgUnitCodeDao.deleteByUuids(deleteUuids);
            }
            if (!orgUnitDto.getName().equals(unitEntity.getName())) {
                orgElementService.updateUnitOrgElementName(orgUnitDto.getName(), unitEntity.getId());
            }
            BeanUtils.copyProperties(orgUnitDto, unitEntity, unitEntity.BASE_FIELDS);
            save(unitEntity);


        } else {

            BeanUtils.copyProperties(orgUnitDto, unitEntity, unitEntity.BASE_FIELDS);
            unitEntity.setId(IdPrefix.SYSTEM_UNIT.getValue() + "_" + SnowFlake.getId());
            if (StringUtils.isNotBlank(orgUnitDto.getSystem())) {
                unitEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            }
            save(unitEntity);
            if (CollectionUtils.isNotEmpty(orgUnitDto.getOrgUnitExtAttrs())) {
                for (OrgUnitExtAttrEntity extAttrEntity : orgUnitDto.getOrgUnitExtAttrs()) {
                    extAttrEntity.setOrgUnitUuid(unitEntity.getUuid());
                }
                orgUnitExtAttrDao.saveAll(orgUnitDto.getOrgUnitExtAttrs());
            }
            if (CollectionUtils.isNotEmpty(orgUnitDto.getOrgUnitCodes())) {
                for (OrgUnitCodeEntity code : orgUnitDto.getOrgUnitCodes()) {
                    code.setOrgUnitUuid(unitEntity.getUuid());
                }
                orgUnitCodeDao.saveAll(orgUnitDto.getOrgUnitCodes());
            }


        }

        if (CollectionUtils.isNotEmpty(orgUnitDto.getI18ns())) {
            Map<String, String> content = Maps.newHashMap();
            for (OrgElementI18nEntity e : orgUnitDto.getI18ns()) {
                e.setDataId(unitEntity.getId());
                e.setDataUuid(unitEntity.getUuid());
                content.put(e.getDataCode() + e.getLocale(), e.getContent());
            }
            if (orgUnitDto.getUuid() != null) {
                List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18ns(unitEntity.getUuid());
                boolean updateI18n = CollectionUtils.isNotEmpty(i18nEntities) && i18nEntities.size() != orgUnitDto.getI18ns().size();
                if (!updateI18n) {
                    for (OrgElementI18nEntity e : i18nEntities) {
                        if (!e.getContent().equals(content.get(e.getDataCode() + e.getLocale()))) {
                            updateI18n = true;
                            break;
                        }
                    }
                }
                if (updateI18n) {
                    orgElementService.updateUnitOrgElementI18ns(unitEntity.getId(), orgUnitDto.getI18ns());
                }

            }
            orgElementI18nService.saveAllAfterDelete(unitEntity.getId(), orgUnitDto.getI18ns());
        }

        return unitEntity.getUuid();
    }

    @Override
    @Transactional
    public void enable(long uuid, Boolean enable) {
        OrgUnitEntity orgUnitEntity = getOne(uuid);
        if (orgUnitEntity != null) {
            orgUnitEntity.setEnable(enable);
            save(orgUnitEntity);
        }
    }

    @Override
    @Transactional
    public boolean deleteByUuid(Long uuid) {
        List<OrganizationEntity> usedOrgs = listUsedOrganizationByUuid(uuid);
        if (CollectionUtils.isNotEmpty(usedOrgs)) {
            return false;
        }
        delete(uuid);
        return true;
    }

    @Override
    public OrgUnitEntity getById(String id) {
        return this.dao.getOneByFieldEq("id", id);
    }

    @Override
    public OrgUnitDto getOrgUnitDetailsByUuid(Long uuid) {
        OrgUnitEntity orgUnitEntity = getOne(uuid);
        OrgUnitDto dto = new OrgUnitDto();
        if (orgUnitEntity != null) {
            BeanUtils.copyProperties(orgUnitEntity, dto);
            dto.setOrgUnitCodes(orgUnitCodeDao.getByOrgUnitUuid(uuid));
            dto.setOrgUnitExtAttrs(orgUnitExtAttrDao.getByOrgUnitUuid(uuid));
            dto.setI18ns(orgElementI18nService.getOrgElementI18ns(orgUnitEntity.getUuid()));
        }
        return dto;
    }

    @Override
    public List<OrgUnitEntity> listBySystemAndTenant(String system, String tenant) {
        OrgUnitEntity example = new OrgUnitEntity();
        example.setSystem(system);
        example.setTenant(tenant);
        return this.dao.listByEntity(example);
    }

    @Override
    public List<OrganizationEntity> listUsedOrganizationByUuid(Long uuid) {
        List<OrganizationEntity> organizationEntities = Lists.newArrayListWithCapacity(0);
        OrgUnitEntity unitEntity = this.getOne(uuid);
        if (unitEntity != null) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("sourceId", unitEntity.getId());
            List<String> orgVersionIds = dao.listCharSequenceBySQL(" select distinct org_version_id from org_element where source_id=:sourceId", param);
            if (CollectionUtils.isNotEmpty(orgVersionIds)) {
                organizationEntities = organizationService.listByOrgVersionIds(orgVersionIds);
            }
        }
        return organizationEntities;
    }
}
