/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
@Service
public class MultiOrgGroupServiceImpl extends AbstractJpaServiceImpl<MultiOrgGroup, MultiOrgGroupDao, String> implements
        MultiOrgGroupService {

    @Override
    public MultiOrgGroup getById(String groupId) {
        MultiOrgGroup q = new MultiOrgGroup();
        q.setId(groupId);
        List<MultiOrgGroup> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    // 按类型获取本单位的所有群组
    @Override
    public List<MultiOrgGroup> queryGroupByType(int type) {
        MultiOrgGroup q = new MultiOrgGroup();
        q.setType(type);
        if (type == MultiOrgGroup.TYPE_MY_GROUP) {
            // 个人群组只能自己查看自己的
            q.setCreator(SpringSecurityUtils.getCurrentUserId());
        } else {
            // 公共群组看整个单位的
            q.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        }
        return this.dao.listByEntity(q, null, "code asc", null);
        // return this.dao.listByEntity(q);
    }

    @Override
    public List<MultiOrgGroup> getGroupsByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgGroup where systemUnitId in (:unitIds)", query);
        return count;
    }
}
