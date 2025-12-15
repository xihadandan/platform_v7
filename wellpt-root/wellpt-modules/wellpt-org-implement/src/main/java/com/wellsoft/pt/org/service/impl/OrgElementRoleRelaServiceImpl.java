package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgElementRoleRelaDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementRoleRelaEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.service.OrgElementRoleRelaService;
import org.apache.commons.collections.CollectionUtils;
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
 * 2023年02月01日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementRoleRelaServiceImpl extends AbstractJpaServiceImpl<OrgElementRoleRelaEntity, OrgElementRoleRelaDaoImpl, Long> implements OrgElementRoleRelaService {

    @Override
    @Transactional
    public void saveOrgElementRoleRela(Long orgElementUuid, List<String> roleUuids, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementUuid", orgElementUuid);
        this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity where orgElementUuid=:orgElementUuid", params);
        if (CollectionUtils.isNotEmpty(roleUuids)) {
            List<OrgElementRoleRelaEntity> relaEntities = Lists.newArrayListWithCapacity(roleUuids.size());
            for (String role : roleUuids) {
                OrgElementRoleRelaEntity rela = new OrgElementRoleRelaEntity();
                rela.setOrgElementUuid(orgElementUuid);
                rela.setRoleUuid(role);
                rela.setOrgVersionUuid(orgVersionUuid);
                relaEntities.add(rela);
            }
            this.dao.saveAll(relaEntities);
        }


    }

    @Override
    public List<OrgElementRoleRelaEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity where orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    @Transactional
    public void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgElementIds", orgElementIds);
        this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity o where exists ( select 1 from OrgElementEntity e where e.id in :orgElementIds and e.uuid = o.orgElementUuid  ) and   o.orgVersionUuid=:orgVersionUuid  ", params);
    }

    @Override
    @Transactional
    public void deleteByRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity where roleUuid=:roleUuid", params);
    }

    @Override
    public List<OrgElementRoleRelaEntity> listByOrgElementUuid(Long orgElementUuid) {
        return this.dao.listByFieldEqValue("orgElementUuid", orgElementUuid);
    }

    @Override
    public List<QueryItem> queryRoleAndUserPaths(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        return this.dao.listQueryItemBySQL("SELECT ROLE.ROLE_UUID, U.USER_PATH\n" +
                "  FROM ORG_ELEMENT_ROLE_RELA ROLE, ORG_USER U, ORG_ELEMENT O\n" +
                " WHERE U.USER_ID = :userId\n" +
                "   AND U.ORG_ELEMENT_ID = O.ID\n" +
                "   AND O.ORG_VERSION_UUID = U.ORG_VERSION_UUID\n" +
                "   AND (O.UUID = ROLE.ORG_ELEMENT_UUID OR\n" +
                "   " +//上级节点直接关联
                "       EXISTS (SELECT 1\n" +
                "                 FROM ORG_ELEMENT_PATH_CHAIN C, ORG_ELEMENT SUP\n" +
                "                WHERE C.SUB_ORG_ELEMENT_ID = O.ID\n" +
                "                  AND C.ORG_ELEMENT_ID = SUP.ID\n" +
                "                  AND SUP.UUID = ROLE.ORG_ELEMENT_UUID))\n" +
                "      \n" +
                "   and exists (select 1\n" +
                "          from ORG_ORGANIZATION ORG, ORG_VERSION OV\n" +
                "         where OV.ORG_UUID = ORG.UUID\n" +
                "           and ORG.ENABLE = 1\n" +
                "           and ORG.EXPIRED = 0\n" +
                "           and OV.STATE = 0\n" +
                "           and OV.UUID = O.ORG_VERSION_UUID)", params, null);
    }

    @Override
    @Transactional
    public void deleteOrgElementRoleRelaByIdsAndRoleUuid(List<String> orgEleIds, String roleUuid, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgElementIds", orgEleIds);
        params.put("roleUuid", roleUuid);
        if (orgVersionUuid == null) {
            params.put("state", OrgVersionEntity.State.PUBLISHED);
            this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity o where o.roleUuid =:roleUuid and  exists ( select 1 from OrgElementEntity e ,OrgVersionEntity v  where e.id in :orgElementIds and e.uuid = o.orgElementUuid and v.uuid = e.orgVersionUuid and v.state=:state )  ", params);
        } else {
            this.dao.deleteByHQL("delete from OrgElementRoleRelaEntity o where o.roleUuid =:roleUuid and  exists ( select 1 from OrgElementEntity e where e.id in :orgElementIds and e.uuid = o.orgElementUuid  )  " + (orgVersionUuid != null ? " and  o.orgVersionUuid=:orgVersionUuid" : ""), params);
        }

    }


}
