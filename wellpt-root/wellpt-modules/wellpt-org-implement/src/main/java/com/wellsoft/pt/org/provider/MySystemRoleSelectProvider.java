package com.wellsoft.pt.org.provider;

import com.google.common.collect.Lists;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年03月01日   chenq	 Create
 * </pre>
 */
@Component
public class MySystemRoleSelectProvider implements OrgSelectProvider {


    @Autowired
    RoleService roleService;


    @Override
    public String type() {
        return "MySystemRole";
    }

    @Override
    public List<Node> fetch(Params params) {
        List<Node> nodes = Lists.newArrayList();
        // 获取当前系统的可访问到角色：系统下创建的角色
        if (params.get("system") != null) {
            List<RoleDto> roleDtos = roleService.getRolesInTenantSystem(params.get("system").toString(), SpringSecurityUtils.getCurrentTenantId());
            if (CollectionUtils.isNotEmpty(roleDtos)) {
                for (RoleDto item : roleDtos) {
                    if (item.getSystemDef() == 1) {
                        continue;
                    }
                    Node n = new Node();
                    n.setKey(item.getUuid());
                    n.setTitle(item.getName());
                    n.setType("securityRole");
                    n.setShortTitle(item.getName());
                    n.setKeyPath(item.getUuid());
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
            List<RoleDto> roles = roleService.getRolesByUuids(params.getKeys());
            if (CollectionUtils.isNotEmpty(roles)) {
                for (RoleDto item : roles) {
                    Node n = new Node();
                    n.setKey(item.getUuid());
                    n.setTitle(item.getName());
                    n.setType("securityRole");
                    n.setShortTitle(item.getName());
                    n.setKeyPath(item.getUuid());
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
