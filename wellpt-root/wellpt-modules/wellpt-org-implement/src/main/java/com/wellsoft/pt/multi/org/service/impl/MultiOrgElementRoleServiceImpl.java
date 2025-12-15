/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementRole;
import com.wellsoft.pt.multi.org.service.MultiOrgElementRoleService;
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
public class MultiOrgElementRoleServiceImpl extends AbstractJpaServiceImpl<MultiOrgElementRole, MultiOrgElementRoleDao, String> implements
        MultiOrgElementRoleService {

    /**
     * 批量添加指定元素节点的角色信息
     */
    @Override
    public List<MultiOrgElementRole> addRoleListOfElement(String eleId, String roleUuids) {
        String[] uuidArray = roleUuids.split(";");
        ArrayList<MultiOrgElementRole> list = new ArrayList<MultiOrgElementRole>();
        for (String uuid : uuidArray) {
            MultiOrgElementRole r = new MultiOrgElementRole();
            r.setEleId(eleId);
            r.setRoleUuid(uuid);
            this.save(r);
            list.add(r);
        }
        return list;

    }

    /**
     * 删除节点元素的角色信息
     */
    @Override
    public boolean deleteRoleListOfElement(String eleId) {
        MultiOrgElementRole q = new MultiOrgElementRole();
        q.setEleId(eleId);
        List<MultiOrgElementRole> objs = this.dao.listByEntity(q);
        if (!CollectionUtils.isEmpty(objs)) {
            deleteByEntities(objs);
        }
        return true;
    }

    @Override
    public List<MultiOrgElementRole> queryRoleListOfElement(String eleId) {
        MultiOrgElementRole q = new MultiOrgElementRole();
        q.setEleId(eleId);
        return this.dao.listByEntity(q);
    }

    @Override
    public void deleteElementListOfRole(String roleUuid) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("roleUuid", roleUuid);
        this.dao.deleteByNamedSQL("deleteElementListOfRole", params);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgElementRoleService#queryElementByRole(java.lang.String)
     */
    @Override
    public List<MultiOrgElementRole> queryElementByRole(String roleUuid) {
        MultiOrgElementRole q = new MultiOrgElementRole();
        q.setRoleUuid(roleUuid);
        return this.dao.listByEntity(q);
    }

    @Override
    public List<QueryItem> queryRoleListOfElementIds(Set<String> userOrgIds) {
        if (CollectionUtils.isEmpty(userOrgIds)) {
            return new ArrayList<>();
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", userOrgIds);
        return this.dao.listQueryItemBySQL("select r.role_uuid , g.name from multi_org_element_role" +
                " r left join  multi_org_element g on r.ele_id=g.id where r.ele_id in (:ids)", params, null);
    }

}
