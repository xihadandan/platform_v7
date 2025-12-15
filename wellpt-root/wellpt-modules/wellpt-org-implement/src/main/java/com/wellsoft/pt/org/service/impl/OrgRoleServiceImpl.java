package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgRoleDaoImpl;
import com.wellsoft.pt.org.entity.OrgRoleEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.OrgRoleService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
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
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
@Service
public class OrgRoleServiceImpl extends AbstractJpaServiceImpl<OrgRoleEntity, OrgRoleDaoImpl, Long> implements OrgRoleService {

    @Autowired
    OrgVersionService orgVersionService;

    @Override
    @Transactional
    public Long saveOrgRole(OrgRoleEntity entity) {
        OrgRoleEntity orgRoleEntity = entity.getUuid() != null ? getOne(entity.getUuid()) : new OrgRoleEntity();
        if (entity.getUuid() == null) {
            BeanUtils.copyProperties(entity, orgRoleEntity, orgRoleEntity.BASE_FIELDS);
            OrgVersionEntity orgVersionEntity = orgVersionService.getOne(entity.getOrgVersionUuid());
            orgRoleEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            orgRoleEntity.setSystem(orgVersionEntity.getSystem());
        } else {
            BeanUtils.copyProperties(entity, orgRoleEntity, ArrayUtils.addAll(entity.BASE_FIELDS, "tenant", "system"));
        }
        save(orgRoleEntity);
        return orgRoleEntity.getUuid();
    }

    @Override
    @Transactional
    public void deleteOrgRole(Long uuid) {
        OrgRoleEntity entity = getOne(uuid);
        if (entity != null) {
            delete(entity);
        }
    }

    @Override
    @Transactional
    public void delteOrgRoleByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgRoleEntity where orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public List<OrgRoleEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    public List<OrgRoleEntity> listByOrgId(String orgId) {
        Assert.hasLength(orgId, "组织ID不能为空！");

        String hql = "from OrgRoleEntity t where exists(select uuid from OrgVersionEntity v where v.state = :state and v.orgUuid = (select o.uuid from OrganizationEntity o where o.id = :orgId) and v.uuid = t.orgVersionUuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgId", orgId);
        params.put("state", OrgVersionEntity.State.PUBLISHED);
        return this.listByHQL(hql, params);
    }

    @Override
    public boolean existRoleIdExcludeUuid(String id, Long orgVersionUuid, Long uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("orgVersionUuid", orgVersionUuid);
        if (uuid != null) {
            params.put("uuid", uuid);
        }
        return this.dao.countByHQL("select count(1) from OrgRoleEntity where id=:id and orgVersionUuid =:orgVersionUuid " + (uuid != null ? " and uuid <> :uuid" : ""), params) > 0;
    }


}
