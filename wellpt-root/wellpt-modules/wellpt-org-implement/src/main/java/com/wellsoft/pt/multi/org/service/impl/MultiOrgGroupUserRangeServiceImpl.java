/*
 * @(#)2022-04-16 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupUserRangeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupUserRangeEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupUserRangeService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表MULTI_ORG_GROUP_USER_RANGE的service服务接口实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-16.1	zenghw		2022-04-16		Create
 * </pre>
 * @date 2022-04-16
 */
@Service
public class MultiOrgGroupUserRangeServiceImpl
        extends AbstractJpaServiceImpl<MultiOrgGroupUserRangeEntity, MultiOrgGroupUserRangeDao, String>
        implements MultiOrgGroupUserRangeService {

    @Override
    public void deleteByGroupId(String groupId) {
        MultiOrgGroupUserRangeEntity q = new MultiOrgGroupUserRangeEntity();
        q.setGroupId(groupId);
        List<MultiOrgGroupUserRangeEntity> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            deleteByEntities(objs);
        }
    }

    @Override
    public List<MultiOrgGroupUserRangeEntity> getUserRangeListBygroupId(String groupId) {
        MultiOrgGroupUserRangeEntity q = new MultiOrgGroupUserRangeEntity();
        q.setGroupId(groupId);
        List<MultiOrgGroupUserRangeEntity> objs = this.dao.listByEntity(q);
        return objs;
    }

    @Override
    public List<MultiOrgGroupUserRangeEntity> getUserRangeListBygroupIds(List<String> groupIds) {
        Map<String, Object> values = Maps.newHashMap();
        if (CollectionUtils.isEmpty(groupIds)) {
            return null;
        }
        values.put("groupIds", groupIds);
        return this.dao.listByNameSQLQuery("getUserRangeListBygroupIds", values);
    }
}
