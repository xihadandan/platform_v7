/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.dingtalk.dao.DingtalkDeptDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkDeptEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkDeptService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
@Service
public class DingtalkDeptServiceImpl extends AbstractJpaServiceImpl<DingtalkDeptEntity, DingtalkDeptDao, Long> implements
        DingtalkDeptService {
    @Override
    public String getOrgElementIdByDeptId(Long deptId) {
        Assert.notNull(deptId, "deptId cannot be null");

        String hql = "from DingtalkDeptEntity where deptId = :deptId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptId", deptId);
        List<DingtalkDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementId() : null;
    }

    @Override
    public Long getOrgElementUuidByOrgElementId(String orgElementId) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");

        String hql = "select orgElementUuid as orgElementUuid from DingtalkDeptEntity where orgElementId = :orgElementId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        List<DingtalkDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public List<String> listOrgElementIdByDeptIdsAndOrgVersionUuid(List<Long> deptIds, Long orgVersionUuid) {
        Assert.notEmpty(deptIds, "deptIds cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "select orgElementId as orgElementId from DingtalkDeptEntity where deptId in :deptIds and orgVersionUuid = :orgVersionUuid order by createTime asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptIds", deptIds);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public Long getOrgElementUuidByDeptIdAndOrgVersionUuid(Long deptId, Long orgVersionUuid) {
        Assert.notNull(deptId, "deptId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkDeptEntity where deptId = :deptId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptId", deptId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public DingtalkDeptEntity getByDeptIdAndOrgVersionUuid(Long deptId, Long orgVersionUuid) {
        Assert.notNull(deptId, "deptId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkDeptEntity where deptId = :deptId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptId", deptId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0) : null;
    }

    @Override
    public DingtalkDeptEntity getByDeptId(Long deptId) {
        Assert.notNull(deptId, "deptId cannot be null");

        String hql = "from DingtalkDeptEntity where deptId = :deptId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptId", deptId);
        List<DingtalkDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0) : null;
    }

}
