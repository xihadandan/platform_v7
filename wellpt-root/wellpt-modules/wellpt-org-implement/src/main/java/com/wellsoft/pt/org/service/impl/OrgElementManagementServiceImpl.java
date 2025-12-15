package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgElementManagementDaoImpl;
import com.wellsoft.pt.org.entity.OrgElementManagementEntity;
import com.wellsoft.pt.org.service.OrgElementManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月24日   chenq	 Create
 * </pre>
 */
@Service
public class OrgElementManagementServiceImpl extends AbstractJpaServiceImpl<OrgElementManagementEntity, OrgElementManagementDaoImpl, Long> implements OrgElementManagementService {
    @Override
    public List<OrgElementManagementEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    public OrgElementManagementEntity getByOrgElementUuid(Long orgElementUuid) {
        return dao.getOneByFieldEq("orgElementUuid", orgElementUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete OrgElementManagementEntity where orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    @Transactional
    public void deleteByOrgElementIdsAndOrgVersionUuid(List<String> orgElementIds, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("orgElementIds", orgElementIds);
        dao.deleteByHQL("delete OrgElementManagementEntity where orgElementId in (:orgElementIds) and  orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public List<OrgElementManagementEntity> listByElementIds(Collection<String> orgElementIds, Long[] orgVersionUuids) {
        Assert.notEmpty(orgElementIds, "组织元素ID列表不能为空！");
        Assert.notEmpty(orgVersionUuids, "组织版本UUID列表不能为空！");

        String hql = "from OrgElementManagementEntity t where t.orgElementId in (:orgElementIds) and t.orgVersionUuid in (:orgVersionUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementIds", orgElementIds);
        params.put("orgVersionUuids", orgVersionUuids);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgElementManagementEntity> listByDirector(String director, String[] orgVersionIds) {
        Assert.hasLength(director, "负责人ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgElementManagementEntity t where t.director = :director and exists ( select 1 from OrgVersionEntity v where v.id in (:orgVersionIds) and v.uuid = t.orgVersionUuid )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("director", director);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<OrgElementManagementEntity> listByLeader(String leader, String[] orgVersionIds) {
        Assert.hasLength(leader, "分管领导ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgElementManagementEntity t where t.leader = :leader and exists ( select 1 from OrgVersionEntity v where v.id in (:orgVersionIds) and v.uuid = t.orgVersionUuid )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("leader", leader);
        params.put("orgVersionIds", orgVersionIds);
        return this.listByHQL(hql, params);
    }

    /**
     * 根据分管领导ID，获取分管的组织ID路径
     *
     * @param leader
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listOrgElementPathByLeader(String leader, String[] orgVersionIds) {
        Assert.hasLength(leader, "分管领导ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "select p.idPath from OrgElementPathEntity p where p.orgElementUuid in(select t.orgElementUuid from OrgElementManagementEntity t where t.leader = :leader and t.orgVersionUuid in(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds)))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("leader", leader);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public boolean isOrgElementManager(String userId, Long orgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("matchUserId", "%" + userId + "%");
        params.put("orgUuid", orgUuid);
        String hql = "select count(1) from OrgElementManagementEntity m where m.orgManager like :matchUserId and exists (" +
                "select 1 from OrgVersionEntity v where orgUuid=:orgUuid and v.uuid = m.orgVersionUuid ) ";
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    public List<OrgElementManagementEntity> listUserOrgElementManagement(String userId, Long orgUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("matchUserId", "%" + userId + "%");
        params.put("orgUuid", orgUuid);
        String hql = "from OrgElementManagementEntity m where m.orgManager like :matchUserId and exists (" +
                "select 1 from OrgVersionEntity v where orgUuid=:orgUuid and v.uuid = m.orgVersionUuid ) ";
        return this.listByHQL(hql, params);
    }

}
