/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupRole;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
public class MultiOrgGroupRoleServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgGroupRole, MultiOrgGroupRoleDao, String> implements MultiOrgGroupRoleService {

    /**
     * 批量添加用户的角色信息
     */
    @Override
    public List<MultiOrgGroupRole> addRoleListOfGroup(String groupId, String roleUuids) {
        String[] uuidArray = roleUuids.split(";");
        ArrayList<MultiOrgGroupRole> list = new ArrayList<MultiOrgGroupRole>();
        for (String uuid : uuidArray) {
            MultiOrgGroupRole r = new MultiOrgGroupRole();
            r.setGroupId(groupId);
            r.setRoleUuid(uuid);
            this.save(r);
            list.add(r);
        }
        return list;

    }

    /**
     * 删除用户的角色信息
     */
    @Override
    public boolean deleteRoleListOfGroup(String groupId) {
        MultiOrgGroupRole q = new MultiOrgGroupRole();
        q.setGroupId(groupId);
        List<MultiOrgGroupRole> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            this.deleteByEntities(objs);
        }
        return true;
    }

    @Override
    public List<MultiOrgGroupRole> queryRoleListOfGroup(String groupId) {
        MultiOrgGroupRole q = new MultiOrgGroupRole();
        q.setGroupId(groupId);
        return this.dao.listByEntity(q);
    }

    @Override
    public void deleteGroupListOfRole(String roleUuid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("roleUuid", roleUuid);
        this.dao.deleteByNamedSQL("deleteGroupListOfRole", params);
    }

    @Override
    public List<MultiOrgGroupRole> queryGroupListByRole(String roleUuid) {
        MultiOrgGroupRole q = new MultiOrgGroupRole();
        q.setRoleUuid(roleUuid);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<QueryItem> queryRoleListOfGroupIds(Set<String> groupIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("grpIds", groupIds);
        return this.dao.listQueryItemBySQL("select r.role_uuid , g.name from multi_org_group_role r" +
                " left join  multi_org_group g on r.group_id=g.id where r.group_id in (:grpIds)", params, null);
    }
}
