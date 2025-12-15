/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.serialnumber.dao.SnSerialNumberDefinitionDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.query.SnSerialNumberDefinitionUsedQueryItem;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberDefinitionService;
import com.wellsoft.pt.basicdata.serialnumber.service.SnSerialNumberMaintainService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Description: 流水号定义服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
@Service
public class SnSerialNumberDefinitionServiceImpl extends AbstractJpaServiceImpl<SnSerialNumberDefinitionEntity, SnSerialNumberDefinitionDao, String>
        implements SnSerialNumberDefinitionService {

    @Autowired
    private SnSerialNumberMaintainService snSerialNumberMaintainService;

    /**
     * 删除没用的流水号定义
     *
     * @param uuid
     * @return
     */
    @Override
    @Transactional
    public int deleteWhenNotUsed(String uuid) {
        // 已生成流水号的定义不可删除
        long count = snSerialNumberMaintainService.countBySerialNumberDefUuid(uuid);
        if (count > 0) {
            return -1;
        }
        // 被表单定义等资源引用不可删除
        if (CollectionUtils.isNotEmpty(listUsedQueryItemByUuids(Lists.newArrayList(uuid)))) {
            return -1;
        }
        this.dao.delete(uuid);
        return 1;
    }

    /**
     * 根据流水号分类UUID获取流水号定义列表
     *
     * @param categoryUuid
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionEntity> listByCategoryUuid(String categoryUuid) {
        SnSerialNumberDefinitionEntity entity = new SnSerialNumberDefinitionEntity();
        entity.setCategoryUuid(categoryUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * 通过流水号ID获取流水号定义
     *
     * @param id
     * @return
     */
    @Override
    public SnSerialNumberDefinitionEntity getById(String id) {
        Assert.hasLength(id, "流水号ID不能为空！");
        return this.dao.getOneByFieldEq("id", id);
    }

    /**
     * 根据流水号分类UUID或流水号定义ID获取流水号定义列表
     *
     * @param categoryUuidOrIds
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionEntity> listByCategoryUuidOrId(List<String> categoryUuidOrIds) {
        String hql = "from SnSerialNumberDefinitionEntity t where t.id in(:categoryUuidOrIds) or t.categoryUuid in (:categoryUuidOrIds) order by t.code asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("categoryUuidOrIds", categoryUuidOrIds);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * 获取UUID列表，获取被引用的流水号
     *
     * @param uuids
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionUsedQueryItem> listUsedQueryItemByUuids(List<String> uuids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuids", uuids);
        params.put("whereSql", "function_type is not null");
        return this.dao.listItemByNameSQLQuery("snSerialNumberDefinitionUsedQuery", SnSerialNumberDefinitionUsedQueryItem.class, params, new PagingInfo(1, Integer.MAX_VALUE));
    }

    @Override
    public List<SnSerialNumberDefinitionEntity> listBySystem(String system) {
        StringBuilder hql = new StringBuilder("from SnSerialNumberDefinitionEntity t where 1 = 1 ");
        if (StringUtils.isNotBlank(system)) {
            hql.append("and ( exists(select pm.moduleId from AppProdModuleEntity pm, AppSystemInfoEntity si ");
            hql.append("where pm.prodVersionUuid = si.prodVersionUuid and t.moduleId = pm.moduleId and si.system = :system) ");
            hql.append("or t.system = :system) ");
        }
        hql.append(" order by t.code asc");
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByHQL(hql.toString(), params);
    }

}
