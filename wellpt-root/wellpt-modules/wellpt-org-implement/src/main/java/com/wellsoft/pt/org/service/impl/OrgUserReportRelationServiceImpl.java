package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.org.dao.impl.OrgUserReportRelationDaoImpl;
import com.wellsoft.pt.org.entity.OrgUserReportRelationEntity;
import com.wellsoft.pt.org.service.OrgUserReportRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * 2023年12月01日   chenq	 Create
 * </pre>
 */
@Service
public class OrgUserReportRelationServiceImpl extends AbstractJpaServiceImpl<OrgUserReportRelationEntity, OrgUserReportRelationDaoImpl, Long> implements OrgUserReportRelationService {
    @Override
    public List<OrgUserReportRelationEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionUuid", orgVersionUuid);
        dao.deleteByHQL("delete from OrgUserReportRelationEntity where orgVersionUuid=:orgVersionUuid", param);
    }

    @Override
    @Transactional
    public void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid) {
        this.deleteByOrgVersionUuidAndUserId(userId, orgVersionUuid);
        if (MapUtils.isNotEmpty(orgElementIdReport)) {
            Set<Map.Entry<String, List<String>>> entrySet = orgElementIdReport.entrySet();
            List<OrgUserReportRelationEntity> relationEntities = Lists.newArrayList();
            for (Map.Entry<String, List<String>> ent : entrySet) {
                if (CollectionUtils.isNotEmpty(ent.getValue())) {
                    for (String id : ent.getValue()) {
                        relationEntities.add(new OrgUserReportRelationEntity(orgVersionUuid, ent.getKey(), userId, id));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(relationEntities)) {
                saveAll(relationEntities);
            }
        }
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuidAndUserId(String userId, Long orgVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionUuid", orgVersionUuid);
        param.put("userId", userId);
        dao.deleteByHQL("delete from OrgUserReportRelationEntity where userId=:userId " + (orgVersionUuid != null ? " and orgVersionUuid=:orgVersionUuid " : ""), param);
    }

    @Override
    @Transactional
    public void deleteByOrgVersionUuidAndReportToUserId(String reportToUserId, Long orgVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionUuid", orgVersionUuid);
        param.put("reportToUserId", reportToUserId);
        dao.deleteByHQL("delete from OrgUserReportRelationEntity where reportToUserId=:reportToUserId" + (orgVersionUuid != null ? " and orgVersionUuid=:orgVersionUuid " : ""), param);
    }

    @Override
    public List<OrgUserReportRelationEntity> listByOrgVersionUuidAndUserId(Long orgVersionUuid, String userId) {
        OrgUserReportRelationEntity example = new OrgUserReportRelationEntity();
        example.setUserId(userId);
        example.setOrgVersionUuid(orgVersionUuid);
        return dao.listByEntity(example);
    }

    @Override
    public List<OrgUserReportRelationEntity> listByOrgVersionIdsAndReportToUserId(String[] orgVersionIds, String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionIds", orgVersionIds);
        param.put("userId", userId);
        return dao.listByHQL("from OrgUserReportRelationEntity r where r.reportToUserId=:userId and" +
                " exists ( select 1 from OrgVersionEntity v where v.id in :orgVersionIds and v.uuid = r.orgVersionUuid )", param);
    }

    @Override
    public List<OrgUserReportRelationEntity> listByOrgVersionIdsAndUserId(String[] orgVersionIds, String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionIds", orgVersionIds);
        param.put("userId", userId);
        return dao.listByHQL("from OrgUserReportRelationEntity r where r.userId=:userId and" +
                " exists ( select 1 from OrgVersionEntity v where v.id in :orgVersionIds and v.uuid = r.orgVersionUuid )", param);
    }

    @Override
    public List<OrgUserReportRelationEntity> listByOrgVersionIdsAndUserIdAndOrgElementIds(String[] orgVersionIds, String userId, List<String> eleIds) {
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(eleIds, "组织元素ID列表不能为空！");

        String hql = "from OrgUserReportRelationEntity r where r.userId=:userId and r.orgElementId in(:eleIds) and" +
                " exists ( select 1 from OrgVersionEntity v where v.id in :orgVersionIds and v.uuid = r.orgVersionUuid )";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionIds", orgVersionIds);
        params.put("userId", userId);
        params.put("eleIds", eleIds);
        return dao.listByHQL(hql, params);
    }
}
