/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.iexport.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.iexport.acceptor.RoleIexportData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Description:角色
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-18.1	linz		2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
@Service
@Transactional(readOnly = true)
public class RoleIexportDataProvider extends AbstractIexportDataProvider<Role, String> {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Role;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        Role role = this.dao.get(Role.class, uuid);
        if (role == null) {
            return new ErrorDataIexportData(IexportType.Role, "找不到对应的角色依赖关系,可能已经被删除", "角色", uuid);
        }
        return new RoleIexportData(role);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 角色ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Role), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(Role.class));
        // 角色名称生成唯一值
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Role), "name",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IexportDataColumn dataColumn = dataRecord.getDataColumn("name");
                        String oldName = ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY);
                        IexportMappingColumn mappingColumn = iexportBundle.getMappingColumn(dataRecord.getTableName(),
                                dataColumn);
                        // 角色名称有新值时，直接返回
                        if (mappingColumn != null) {
                            return oldName;
                        }
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return oldName + Separator.UNDERLINE.getValue() + idGeneratorService.getBySysDate();
                    }

                });
        return iexportMetaData;
    }


    @Override
    public String getTreeName(Role role) {
        return new RoleIexportData(role).getName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        Role r = getEntity(uuid);
        // 获取嵌套的子角色与权限一并导出
        if (r != null) {
            if (r.getSystemDef() == 1) {
                return null;
            }
            TreeNode node = new TreeNode();
            node.setType(getType());
            node.setId(uuid.toString());
            node.setName(getTreeName(r));
            obtainNestedRoleAndPrivilegeTreeNode(node);
            return node;
        }
        return null;
    }

    @Override
    protected void beforeSaveEntityStream(Role entity) {
        // 导入是全量的数据，要先删除角色相关的旧数据
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", entity.getUuid());
        this.nativeDao.batchExecute("delete from AUDIT_ROLE_PRIVILEGE where role_uuid=:uuid", params);
        this.nativeDao.batchExecute("delete from audit_nested_role r where exists (" +
                " select 1 from AUDIT_ROLE_NESTED_ROLE nr where nr.role_uuid=:uuid and nr.nested_role_uuid = r.uuid" +
                ") ", params);
        this.nativeDao.batchExecute("delete from AUDIT_ROLE_NESTED_ROLE where role_uuid=:uuid", params);
    }

    private void obtainNestedRoleAndPrivilegeTreeNode(TreeNode parent) {
        Set<NestedRole> nestedRoles = roleService.getNestedRolesByRoleUuid(parent.getId());
        if (!CollectionUtils.isEmpty(nestedRoles)) {
            for (NestedRole nestedRole : nestedRoles) {
                TreeNode nestedRoleNode = this.exportTreeNodeByDataProvider(nestedRole.getRoleUuid(), IexportType.Role);
                if (nestedRoleNode != null) {
                    parent.getChildren().add(nestedRoleNode);
                }
            }
        }
        this.obtainRolePrivileges(getEntity(parent.getId()), parent);
    }

    private void obtainRolePrivileges(Role role, TreeNode parent) {
        // 导出权限
        Set<Privilege> privileges = role.getPrivileges();
        if (!CollectionUtils.isEmpty(privileges)) {
            for (Privilege privilege : privileges) {
                TreeNode privilegeNode = this.exportTreeNodeByDataProvider(privilege.getUuid(), IexportType.Privilege);
                if (privilegeNode != null) {
                    parent.getChildren().add(privilegeNode);
                }
            }
        }
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(new IExportTable(Lists.newArrayList("role_uuid", "privilege_uuid"), "select * from AUDIT_ROLE_PRIVILEGE where role_uuid=:uuid"),
                new IExportTable(Lists.newArrayList("role_uuid", "nested_role_uuid"), "select * from AUDIT_ROLE_NESTED_ROLE where role_uuid=:uuid"),
                new IExportTable(Lists.newArrayList("uuid"), "select * from audit_nested_role r where exists (" +
                        " select 1 from AUDIT_ROLE_NESTED_ROLE nr where nr.role_uuid=:uuid and nr.nested_role_uuid = r.uuid" +
                        ") "),

                // 导出关联的内置角色
                new IExportTable("select r.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r where r.system_def=1 and r.uuid = nr.role_uuid " +
                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid and a.uuid=:uuid"),
                // 导出关联的内置权限
                new IExportTable("select p.* from audit_role r ,audit_privilege p ,audit_role_privilege arp where r.uuid =arp.role_uuid and p.system_def = 1 and p.uuid =arp.privilege_uuid and r.uuid=:uuid"),
                // 导出关联的内置权限关联的内置角色
                new IExportTable("select ar.* from audit_role r ,audit_privilege p ,audit_role_privilege arp," +
                        "audit_role_privilege rarp ,audit_role ar" +
                        " where r.uuid =arp.role_uuid and p.system_def = 1 and p.uuid =arp.privilege_uuid and r.uuid=:uuid" +
                        " and ar.system_def =1 and ar.uuid =rarp.role_uuid and rarp.privilege_uuid = p.uuid"),
//                new IExportTable(Lists.newArrayList("role_uuid", "nested_role_uuid"), "select rnr.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r where r.system_def=1 and r.uuid = nr.role_uuid " +
//                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid and a.uuid=:uuid"),
//                new IExportTable("select nr.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r where r.system_def=1 and r.uuid = nr.role_uuid " +
//                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid and a.uuid=:uuid"),

                // 导出内嵌角色下的内嵌权限以及内嵌权限资源关系
                new IExportTable("select p.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r,\n" +
                        "audit_privilege p ,audit_role_privilege arp where r.system_def=1 and r.uuid = nr.role_uuid \n" +
                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid  \n" +
                        "and p.system_def=1 and arp.role_uuid=r.uuid and arp.privilege_uuid=p.uuid and a.uuid=:uuid"),
                new IExportTable(Lists.newArrayList("role_uuid", "privilege_uuid"), "select arp.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r,\n" +
                        "audit_privilege p ,audit_role_privilege arp where r.system_def=1 and r.uuid = nr.role_uuid \n" +
                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid  \n" +
                        "and p.system_def=1 and arp.role_uuid=r.uuid and arp.privilege_uuid=p.uuid and a.uuid=:uuid"),
                new IExportTable("select apor.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r,\n" +
                        "audit_privilege p ,audit_role_privilege arp ,audit_privilege_other_resource apor where r.system_def=1 and r.uuid = nr.role_uuid \n" +
                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid  \n" +
                        "and p.system_def=1 and arp.role_uuid=r.uuid and arp.privilege_uuid=p.uuid and apor.privilege_uuid = p.uuid and a.uuid=:uuid"),
                new IExportTable(Lists.newArrayList("privilege_uuid", "resource_uuid"), "select apr.* from audit_role a, AUDIT_ROLE_NESTED_ROLE rnr, audit_nested_role nr ,audit_role r,\n" +
                        "audit_privilege p ,audit_role_privilege arp ,audit_privilege_resource apr where r.system_def=1 and r.uuid = nr.role_uuid \n" +
                        "and rnr.nested_role_uuid=nr.uuid and a.uuid =rnr.role_uuid  \n" +
                        "and p.system_def=1 and arp.role_uuid=r.uuid and arp.privilege_uuid=p.uuid and apr.privilege_uuid = p.uuid and a.uuid=:uuid")

        );
    }

    @Override
    public JoinTableProcessor<Role, String> getJoinTableProcessor() {
        JoinTableProcessor<Role, String> roleJoinTableProcessor = new JoinTableProcessor<Role, String>() {
            @Override
            public <P extends JpaEntity<String>, C extends JpaEntity<String>> void joinTableSave(Session session, ProtoDataBeanTree<Role, P, C> t) {
                Set<C> set = t.getChilderMap().get(IexportType.Privilege);
                if (set != null) {
                    for (C c : set) {
                        Query query = session.createSQLQuery("select 1 from audit_role_privilege where role_uuid =? and privilege_uuid =? ");
                        query.setParameter(0, t.getProtoDataBean().getData().getUuid());
                        query.setParameter(1, c.getUuid());
                        Object object = query.uniqueResult();
                        if (object == null) {
                            query = session.createSQLQuery("insert into audit_role_privilege (role_uuid, privilege_uuid) values (?, ?)");
                            query.setParameter(0, t.getProtoDataBean().getData().getUuid());
                            query.setParameter(1, c.getUuid());
                            query.executeUpdate();
                        }
                    }
                }
            }
        };
        return roleJoinTableProcessor;
    }

    @Override
    public void putChildProtoDataHqlParams(Role role, Map<String, Role> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (role.getPrivileges() != null && role.getPrivileges().size() > 0) {
            parentMap.put(start + role.getUuid(), role);
            String childHqlkey = this.getChildHqlKey(IexportType.Privilege);
            ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
            if (protoDataHql == null) {
                protoDataHql = new ProtoDataHql(getType(), "Privilege");
                hqlMap.put(childHqlkey, protoDataHql);
            }
            Set<Privilege> privilegeSet = (Set<Privilege>) protoDataHql.getParams().get("privilegeSet");
            if (privilegeSet == null) {
                privilegeSet = new HashSet<>();
                protoDataHql.getParams().put("privilegeSet", privilegeSet);
            }
            privilegeSet.addAll(role.getPrivileges());
        }
    }


    @Override
    public Map<String, List<Role>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<Role>> map = new HashMap<>();
        // 页面或组件定义依赖的角色
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            List<Role> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Role.class);
            for (Role role : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, role, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            List<Role> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Role.class);
            for (Role role : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + role.getUuid();
                this.putParentMap(map, role, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
