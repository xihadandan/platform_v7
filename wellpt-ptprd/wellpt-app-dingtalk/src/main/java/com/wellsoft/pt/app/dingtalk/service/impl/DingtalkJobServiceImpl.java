/*
 * @(#)4/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.dingtalk.dao.DingtalkJobDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkJobEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkJobService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
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
 * 4/20/25.1	    zhulh		4/20/25		    Create
 * </pre>
 * @date 4/20/25
 */
@Service
public class DingtalkJobServiceImpl extends AbstractJpaServiceImpl<DingtalkJobEntity, DingtalkJobDao, Long> implements
        DingtalkJobService {

    @Override
    public String getOrgElementIdByDeptIdAndTitle(Long deptId, String title) {
        String hql = "from DingtalkJobEntity where deptId = :deptId and title = :title order by createTime desc";
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("title", title);
        List<DingtalkJobEntity> jobs = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isEmpty(jobs) ? null : jobs.get(0).getOrgElementId();
    }

    @Override
    public Long getOrgElementUuidByOrgElementId(String orgElementId) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");

        String hql = "select orgElementUuid as orgElementUuid from DingtalkJobEntity where orgElementId = :orgElementId order by createTime desc";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("orgElementId", orgElementId);
        List<DingtalkJobEntity> jobEntities = this.dao.listByHQLAndPage(hql, parmas, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(jobEntities) ? jobEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public Long getOrgElementUuidByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "select orgElementUuid as orgElementUuid from DingtalkJobEntity where orgElementId = :orgElementId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("orgElementId", orgElementId);
        parmas.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkJobEntity> jobEntities = this.dao.listByHQLAndPage(hql, parmas, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(jobEntities) ? jobEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public DingtalkJobEntity getByOrgElementIdAndOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkJobEntity where orgElementId = :orgElementId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("orgElementId", orgElementId);
        parmas.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkJobEntity> jobEntities = this.dao.listByHQLAndPage(hql, parmas, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(jobEntities) ? jobEntities.get(0) : null;
    }

    @Override
    public List<String> listOrgElementIdByDeptIdsAndOrgVersionUuid(String title, List<Long> deptIds, Long orgVersionUuid) {
        Assert.notNull(title, "title cannot be null");
        Assert.notEmpty(deptIds, "deptIds cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "select orgElementId as orgElementId from DingtalkJobEntity where title = :title and deptId in :deptIds and orgVersionUuid = :orgVersionUuid order by createTime asc";
        Map<String, Object> parmas = Maps.newHashMap();
        parmas.put("title", title);
        parmas.put("deptIds", deptIds);
        parmas.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listCharSequenceByHQL(hql, parmas);
    }

}