/*
 * @(#)11/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dms.dao.DmsRoleModelDao;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.entity.DmsRoleModelEntity;
import com.wellsoft.pt.dms.service.DmsRoleModelService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/25/25.1	    zhulh		11/25/25		    Create
 * </pre>
 * @date 11/25/25
 */
@Service
public class DmsRoleModelServiceImpl extends AbstractJpaServiceImpl<DmsRoleModelEntity, DmsRoleModelDao, Long> implements DmsRoleModelService {

    @Autowired
    private DmsRoleService roleService;

    @Override
    @Transactional
    public void initBySystemAndTenant(String system, String tenant) {
        DmsRoleModelEntity entity = new DmsRoleModelEntity();
        entity.setSystem(system);
        entity.setTenant(tenant);
        long count = this.dao.countByEntity(entity);
        if (count > 0) {
            return;
        }

        List<DmsRoleModelEntity> modelEntities = Lists.newArrayList();
        modelEntities.add(createRoleModelEntity("信息发布", "info_publish", "001", Lists.newArrayList("manager", "editor", "reader"), system, tenant));
        modelEntities.add(createRoleModelEntity("文档管理", "doc_manage", "002", Lists.newArrayList("manager", "editor", "reader"), system, tenant));
        modelEntities.add(createRoleModelEntity("知识库", "knowledge_base", "003", Lists.newArrayList("manager", "editor", "reader"), system, tenant));
        this.dao.saveAll(modelEntities);
    }

    @Override
    public List<DmsRoleModelEntity> listBySystem(String system) {
        DmsRoleModelEntity entity = new DmsRoleModelEntity();
        entity.setSystem(system);
        return this.dao.listByEntityAndPage(entity, new PagingInfo(1, Integer.MAX_VALUE), "code asc");
    }

    @Override
    public List<DmsRoleModelEntity> listByRoleUuids(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return Collections.emptyList();
        }

        Map<String, Object> params = Maps.newHashMap();
        List<String> conditions = Lists.newArrayList();
        for (int index = 0; index < uuids.size(); index++) {
            params.put("uuid" + index, uuids.get(index));
            conditions.add("roleUuids like '%' || :uuid" + index + " || '%'");
        }
        String hql = "from DmsRoleModelEntity where " + String.join(" or ", conditions);
        return this.dao.listByHQL(hql, params);
    }

    /**
     * @param name
     * @param id
     * @param code
     * @param roleDefIds
     * @param system
     * @param tenant
     * @return
     */
    private DmsRoleModelEntity createRoleModelEntity(String name, String id, String code, List<String> roleDefIds, String system, String tenant) {
        DmsRoleModelEntity entity = new DmsRoleModelEntity();
        entity.setName(name);
        entity.setId(id);
        entity.setCode(code);
        entity.setBuiltIn(true);
        if (CollectionUtils.isNotEmpty(roleDefIds)) {
            List<DmsRoleEntity> definitionEntities = roleService.listByIdsAndSystem(roleDefIds, system);
            entity.setRoleUuids(definitionEntities.stream().map(definition -> definition.getUuid().toString())
                    .collect(Collectors.joining(Separator.SEMICOLON.getValue())));
        }
        entity.setRemark(name);
        entity.setSystem(system);
        entity.setTenant(tenant);
        return entity;
    }

}
