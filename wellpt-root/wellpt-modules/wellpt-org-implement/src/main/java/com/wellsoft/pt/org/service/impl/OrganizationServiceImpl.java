package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrganizationDaoImpl;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.entity.OrganizationEntity;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.org.service.OrganizationService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月21日   chenq	 Create
 * </pre>
 */
@Service
public class OrganizationServiceImpl extends AbstractJpaServiceImpl<OrganizationEntity, OrganizationDaoImpl, Long>
        implements OrganizationService {

    @Resource
    OrgVersionService orgVersionService;

    @Resource
    OrgElementI18nService orgElementI18nService;

    @Override
    @Transactional
    public void enable(Long uuid, boolean enable) {
        OrganizationEntity example = getOne(uuid);
        if (example != null) {
            example.setEnable(enable);
            save(example);
        }
    }

    @Override
    @Transactional
    public void setDefault(Long uuid, boolean isDefault) {
        OrganizationEntity entity = getOne(uuid);
        if (entity != null) {
            // 查询这个租户系统下的默认组织更新为非默认
            OrganizationEntity other = new OrganizationEntity();
            other.setSystem(entity.getSystem());
            other.setTenant(entity.getTenant());
            other.setIsDefault(true);
            List<OrganizationEntity> defaultEntity = dao.listByEntity(other);
            if (CollectionUtils.isNotEmpty(defaultEntity)) {
                defaultEntity.get(0).setIsDefault(false);
                save(defaultEntity.get(0));
            }
            entity.setIsDefault(isDefault);
            save(entity);
        }
    }

    @Override
    @Transactional
    public Long saveOrg(OrganizationEntity organizationEntity) {
        OrganizationEntity entity = organizationEntity.getUuid() != null ? getOne((organizationEntity.getUuid())) : organizationEntity;
        OrgVersionEntity versionEntity = null;
        if (entity.getUuid() != null) {
            BeanUtils.copyProperties(organizationEntity, entity, ArrayUtils.addAll(entity.BASE_FIELDS, "tenant", "system", "id"));
        } else {
            entity.setId(IdPrefix.ORG.getValue() + "_" + SnowFlake.getId());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());

            // 默认生成正式版组织版本
            versionEntity = new OrgVersionEntity();
            versionEntity.setId(IdPrefix.ORG_VERSION.getValue() + "_" + SnowFlake.getId());
            versionEntity.setSystem(entity.getSystem());
            versionEntity.setTenant(entity.getTenant());
        }
        save(entity);
        if (versionEntity != null) {
            versionEntity.setOrgUuid(entity.getUuid());
            versionEntity.setState(OrgVersionEntity.State.PUBLISHED);
            versionEntity.setEffectTime(Calendar.getInstance().getTime());
            orgVersionService.save(versionEntity);
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", entity.getUuid());
        // 第一个组织自动设置为默认组织
        this.dao.updateByHQL("update OrganizationEntity o set o.isDefault = 1 where o.uuid=:uuid and not exists (" +
                " select 1 from OrganizationEntity t where (t.system=o.system or (t.system is null and o.system is null) ) and t.tenant = o.tenant and t.uuid <> o.uuid and t.isDefault = 1 )", params);
        if (CollectionUtils.isNotEmpty(organizationEntity.getI18ns())) {
            for (OrgElementI18nEntity e : organizationEntity.getI18ns()) {
                e.setDataUuid(entity.getUuid());
                e.setDataId(entity.getId());
            }
            orgElementI18nService.saveAllAfterDelete(entity.getUuid(), organizationEntity.getI18ns());
        }

        return entity.getUuid();
    }

    @Override
    @Transactional
    public void deleteOrg(Long uuid) {
        OrganizationEntity example = getOne(uuid);
        if (example != null) {
            // TODO: 存在版本则不可删除
            delete(example);
            orgVersionService.deleteAllByOrgUuid(uuid);
        }
    }

    @Override
    public List<OrganizationEntity> listEnabledBySystem(String system) {
        return this.listEnabledBySystem(StringUtils.isNotBlank(system) ? Lists.newArrayList(system) : null);
    }

    @Override
    public List<OrganizationEntity> listEnabledBySystem(List<String> system) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("enable", true);
        param.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        StringBuilder hql = new StringBuilder("from OrganizationEntity where enable = :enable and tenant=:tenant");
        if (CollectionUtils.isNotEmpty(system)) {
            param.put("system", system);
            hql.append(" and system in :system");
        }
        hql.append(" order by enable desc");
        return listByHQL(hql.toString(), param);
    }

    @Override
    public List<OrganizationEntity> listBySystem(String system) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        StringBuilder hql = new StringBuilder("from OrganizationEntity where tenant=:tenant");
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            hql.append(" and system = :system");
        }
        hql.append(" order by enable desc");
        return listByHQL(hql.toString(), param);
    }

    @Override
    public OrganizationEntity getDefaultOrgBySystem(String system) {
        OrganizationEntity example = new OrganizationEntity();
        example.setIsDefault(true);
        example.setSystem(system);
        example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        List<OrganizationEntity> entities = listByEntity(example);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    /**
     * 根据组织ID获取组织信息
     *
     * @param id
     * @return
     */
    @Override
    public OrganizationEntity getById(String id) {
        Assert.hasLength(id, "组织ID不能为空！");

        OrganizationEntity example = new OrganizationEntity();
        example.setId(id);
        List<OrganizationEntity> entities = listByEntity(example);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    public OrganizationEntity getByBizOrgId(String bizOrgId) {
        Assert.hasLength(bizOrgId, "组织版本ID不能为空！");

        String hql = "from OrganizationEntity t where exists (select b.uuid from BizOrganizationEntity b where b.id = :bizOrgId and b.orgUuid = t.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgId", bizOrgId);
        List<OrganizationEntity> entities = this.dao.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    /**
     * @param orgVersionId
     * @return
     */
    @Override
    public OrganizationEntity getByOrgVersionId(String orgVersionId) {
        Assert.hasLength(orgVersionId, "组织版本ID不能为空！");

        String hql = "from OrganizationEntity t where exists (select v.uuid from OrgVersionEntity v where v.id = :orgVersionId and v.orgUuid = t.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionId", orgVersionId);
        List<OrganizationEntity> entities = this.dao.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    /**
     * 根据组织版本UUID列表获取组织
     *
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrganizationEntity> listByOrgVersionIds(List<String> orgVersionIds) {
        Assert.notEmpty(orgVersionIds, "组织版本UUID列表不能为空！");

        String hql = "from OrganizationEntity t where exists (select v.uuid from OrgVersionEntity v where v.orgUuid = t.uuid and v.id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<String> listIdByBizOrgIds(List<String> bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        String hql = "select id as id from OrganizationEntity t where exists (select b.uuid from BizOrganizationEntity b where b.id in :bizOrgIds and b.orgUuid = t.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("bizOrgIds", bizOrgIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String system = queryInfo.getOtherParams("system");
        if (StringUtils.isBlank(system)) {
            system = RequestSystemContextPathResolver.system();
        }
        List<OrganizationEntity> entities = this.listEnabledBySystem(system);
        List<OrganizationDto> dtos = com.wellsoft.pt.jpa.util.BeanUtils.copyCollection(entities, OrganizationDto.class);
        OrganizationDto defaultOrgDto = dtos.stream().filter(dto -> BooleanUtils.isTrue(dto.getIsDefault())).findFirst().orElse(null);
        if (defaultOrgDto != null) {
            defaultOrgDto.setName(defaultOrgDto.getName() + "（系统默认组织）");
        }
        return new Select2QueryData(dtos, "id", "name", queryInfo.getPagingInfo());
    }

}
