/*
 * @(#)11/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support.org.provider;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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
 * 11/28/25.1	    zhulh		11/28/25		    Create
 * </pre>
 * @date 11/28/25
 */
@Component
public class DmsRoleSelectProvider implements OrgSelectProvider {

    @Autowired
    private DmsRoleService roleService;

    @Override
    public String type() {
        return "DmsRoleDefinition";
    }

    @Override
    public List<Node> fetch(Params params) {
        List<Node> nodes = Lists.newArrayList();
        List<String> roleUuids = (List<String>) params.get("roleUuids");
        String system = Objects.toString(params.get("system"), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(system)) {
            List<DmsRoleEntity> roleDtos = null;
            if (CollectionUtils.isNotEmpty(roleUuids)) {
                roleDtos = roleService.listByUuids(roleUuids);
            } else {
                roleDtos = roleService.listBySystem(system);
            }
            if (CollectionUtils.isNotEmpty(roleDtos)) {
                for (DmsRoleEntity item : roleDtos) {
                    Node n = new Node();
                    n.setKey("dms_role_" + item.getUuid());
                    n.setTitle(item.getName());
                    n.setType("securityRole");
                    n.setShortTitle(item.getName());
                    n.setKeyPath("dms_role_" + item.getUuid());
                    n.setTitlePath(item.getName());
                    n.setIsLeaf(true);
                    n.setParentKey(null);
                    nodes.add(n);
                }
            }
        }
        return nodes;
    }

    @Override
    public List<Node> fetchByKeys(Params params) {
        List<Node> nodes = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(params.getKeys())) {
            List<DmsRoleEntity> roles = roleService.listByUuids(params.getKeys().stream()
                    .filter(key -> StringUtils.startsWith(key, "dms_role_"))
                    .map(key -> key.replace("dms_role_", ""))
                    .collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(roles)) {
                for (DmsRoleEntity item : roles) {
                    Node n = new Node();
                    n.setKey("dms_role_" + item.getUuid());
                    n.setTitle(item.getName());
                    n.setType("securityRole");
                    n.setShortTitle(item.getName());
                    n.setKeyPath("dms_role_" + item.getUuid());
                    n.setTitlePath(item.getName());
                    n.setData(item);
                    n.setIsLeaf(true);
                    n.setParentKey(null);
                    nodes.add(n);
                }
            }
        }
        return nodes;
    }

    @Override
    public PageNode fetchUser(Params params) {
        return null;
    }

    @Override
    public List<Node> fetchByTitles(Params params) {
        return null;
    }
}
