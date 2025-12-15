/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserRole;
import com.wellsoft.pt.multi.org.service.MultiOrgUserRoleService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MultiOrgUserRoleServiceImpl extends AbstractJpaServiceImpl<MultiOrgUserRole, MultiOrgUserRoleDao, String>
        implements MultiOrgUserRoleService {

    @Autowired
    private RoleFacadeService roleFacadeService;

    /**
     * 批量添加用户的角色信息
     */
    @Override
    public List<MultiOrgUserRole> addRoleListOfUser(String userId, String roleUuids) {
        String[] uuidArray = roleUuids.split(";");
        ArrayList<MultiOrgUserRole> list = new ArrayList<MultiOrgUserRole>();
        for (String uuid : uuidArray) {
            if (StringUtils.isNotBlank(uuid)) {
                MultiOrgUserRole r = new MultiOrgUserRole();
                r.setUserId(userId);
                r.setRoleUuid(uuid);
                this.save(r);
                list.add(r);
            }
        }
        return list;

    }

    /**
     * 删除用户的角色信息
     */
    @Override
    public boolean deleteRoleListOfUser(String userId) {
        MultiOrgUserRole q = new MultiOrgUserRole();
        q.setUserId(userId);
        List<MultiOrgUserRole> objs = this.dao.listByEntity(q);
        this.deleteByEntities(objs);
        this.flushSession();
        return true;
    }

    @Override
    public List<MultiOrgUserRole> queryRoleListOfUser(String userId) {
        MultiOrgUserRole q = new MultiOrgUserRole();
        q.setUserId(userId);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<String> queryRoleUuidsOfUser(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        return this.dao.listCharSequenceByHQL("select roleUuid from  MultiOrgUserRole where userId=:userId", params);
    }

    @Override
    public void deleteUserListOfRole(String roleUuid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("roleUuid", roleUuid);
        this.dao.deleteByNamedSQL("deleteUserListOfRole", params);
    }

    @Override
    public List<MultiOrgUserRole> queryUserListByRole(String roleUuid) {
        MultiOrgUserRole q = new MultiOrgUserRole();
        q.setRoleUuid(roleUuid);
        return this.dao.listByEntity(q);
    }
}
