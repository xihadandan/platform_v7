/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.weixin.dao.WeixinDeptDao;
import com.wellsoft.pt.app.weixin.entity.WeixinDeptEntity;
import com.wellsoft.pt.app.weixin.service.WeixinDeptService;
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
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@Service
public class WeixinDeptServiceImpl extends AbstractJpaServiceImpl<WeixinDeptEntity, WeixinDeptDao, Long> implements WeixinDeptService {

    @Override
    public String getOrgElementIdByIdAndConfigUuid(Long id, Long configUuid) {
        Assert.notNull(id, "deptId cannot be null");
        Assert.notNull(configUuid, "configUuid cannot be null");

        String hql = "from WeixinDeptEntity where id = :id and configUuid = :configUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("configUuid", configUuid);
        List<WeixinDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementId() : null;
    }

    @Override
    public Long getOrgElementUuidByOrgElementId(String orgElementId) {
        Assert.notNull(orgElementId, "orgElementId cannot be null");

        String hql = "select orgElementUuid as orgElementUuid from WeixinDeptEntity where orgElementId = :orgElementId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgElementId", orgElementId);
        List<WeixinDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public List<String> listOrgElementIdByIdsAndOrgVersionUuid(List<Long> ids, Long orgVersionUuid) {
        Assert.notEmpty(ids, "ids cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "select orgElementId as orgElementId from WeixinDeptEntity where id in :ids and orgVersionUuid = :orgVersionUuid order by createTime asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public Long getOrgElementUuidByIdAndOrgVersionUuid(Long id, Long orgVersionUuid) {
        Assert.notNull(id, "deptId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from WeixinDeptEntity where id = :id and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("orgVersionUuid", orgVersionUuid);
        List<WeixinDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0).getOrgElementUuid() : null;
    }

    @Override
    public WeixinDeptEntity getByIdAndOrgVersionUuid(Long id, Long orgVersionUuid) {
        Assert.notNull(id, "id cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from WeixinDeptEntity where id = :id and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("orgVersionUuid", orgVersionUuid);
        List<WeixinDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(deptEntities) ? deptEntities.get(0) : null;
    }

    @Override
    public boolean existsByIdAndConfigUuid(Long id, Long configUuid) {
        Assert.notNull(id, "deptId cannot be null");

        String hql = "from WeixinDeptEntity where id = :id and configUuid = :configUuid and status = :status order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("configUuid", configUuid);
        params.put("status", WeixinDeptEntity.Status.NORMAL);
        List<WeixinDeptEntity> deptEntities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return this.dao.countByHQL(hql, params) > 0;
    }

}
