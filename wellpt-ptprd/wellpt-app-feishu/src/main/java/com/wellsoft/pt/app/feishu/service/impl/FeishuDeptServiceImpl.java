package com.wellsoft.pt.app.feishu.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.feishu.dao.FeishuDeptDao;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptEntity;
import com.wellsoft.pt.app.feishu.service.FeishuDeptService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FeishuDeptServiceImpl extends AbstractJpaServiceImpl<FeishuDeptEntity, FeishuDeptDao, Long> implements FeishuDeptService {

    @Override
    public String getOrgElementIdByOpenDepartmentIdAndOrgUuid(String openDepartmentId, Long orgUuid) {
        Assert.notNull(openDepartmentId, "openDepartmentId cannot be null");
        Assert.notNull(orgUuid, "orgUuid cannot be null");

        String hql = "from FeishuDeptEntity where openDepartmentId = :openDepartmentId and orgUuid = :orgUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentId", openDepartmentId);
        params.put("orgUuid", orgUuid);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementId() : null;
    }

    @Override
    public Long getOrgElementUuidByOpenDepartmentIdAndOrgVersionUuid(String openDepartmentId, Long orgVersionUuid) {
        Assert.notNull(openDepartmentId, "openDepartmentId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from FeishuDeptEntity where openDepartmentId = :openDepartmentId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentId", openDepartmentId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public List<FeishuDeptEntity> listHasLeaderUserIdByOrgVersionUuid(Long orgVersionUuid) {
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from FeishuDeptEntity where orgVersionUuid = :orgVersionUuid and leaderUserId is not null";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public FeishuDeptEntity getByOpenDepartmentIdAndOrgVersionUuid(String openDepartmentId, Long orgVersionUuid) {
        Assert.notNull(openDepartmentId, "openDepartmentId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from FeishuDeptEntity where openDepartmentId = :openDepartmentId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentId", openDepartmentId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0) : null;
    }

    @Override
    public List<String> listOrgElementIdByOpenDepartmentIdsAndOrgVersionUuid(List<String> openDepartmentIds, Long orgVersionUuid) {
        if (CollectionUtils.isEmpty(openDepartmentIds)) {
            return Collections.emptyList();
        }
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");
        String hql = "select distinct orgElementId as orgElementId from FeishuDeptEntity where openDepartmentId in (:openDepartmentIds) and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentIds", openDepartmentIds);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public String getOrgElementIdByOpenDepartmentId(String openDepartmentId) {
        Assert.notNull(openDepartmentId, "openDepartmentId cannot be null");

        String hql = "from FeishuDeptEntity where openDepartmentId = :openDepartmentId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentId", openDepartmentId);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementId() : null;
    }

    @Override
    public Long getOrgElementUuidByOrgElementId(String orgElementId) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");

        String hql = "select orgElementUuid as orgElementUuid from FeishuDeptEntity where orgElementId = :orgElementId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public FeishuDeptEntity getByOpenDepartmentId(String openDepartmentId) {
        Assert.notNull(openDepartmentId, "openDepartmentId cannot be null");

        String hql = "from FeishuDeptEntity where openDepartmentId = :openDepartmentId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openDepartmentId", openDepartmentId);
        List<FeishuDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0) : null;
    }

}
