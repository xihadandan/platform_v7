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
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.iexport.acceptor.PrivilegeIexportData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class PrivilegeIexportDataProvider extends AbstractIexportDataProvider<Privilege, String> {

    static {
        // 1.1、资源&父级资源
        TableMetaData.register(IexportType.Resource, "资源", Resource.class);
        TableMetaData.register(IexportType.ResourceParent, "资源", Resource.class);
        // 1.2、权限
        TableMetaData.register(IexportType.Privilege, "权限", Privilege.class);
        // 1.3、角色
        TableMetaData.register(IexportType.Role, "角色", Role.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Privilege;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        Privilege privilege = this.dao.get(Privilege.class, uuid);
        if (privilege == null) {
            return new ErrorDataIexportData(IexportType.Privilege, "找不到对应的权限依赖关系,可能已经被删除", "权限", uuid);
        }
        return new PrivilegeIexportData(privilege);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 权限编号生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Privilege), "code",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IexportDataColumn dataColumn = dataRecord.getDataColumn("code");
                        String oldCode = ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY);
                        IexportMappingColumn mappingColumn = iexportBundle.getMappingColumn(dataRecord.getTableName(),
                                dataColumn);
                        // 权限编号有新值时，直接返回
                        if (mappingColumn != null) {
                            return oldCode;
                        }
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return oldCode + Separator.UNDERLINE.getValue() + idGeneratorService.getBySysDate();
                    }

                });
        // 权限名称生成唯一值
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Privilege), "name",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IexportDataColumn dataColumn = dataRecord.getDataColumn("name");
                        String oldName = ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY);
                        IexportMappingColumn mappingColumn = iexportBundle.getMappingColumn(dataRecord.getTableName(),
                                dataColumn);
                        // 权限名称有新值时，直接返回
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
    public String getTreeName(Privilege privilege) {
        return new PrivilegeIexportData(privilege).getName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        Privilege entity = getEntity(uuid);
        if (entity != null) {
            if (entity.getSystemDef() == 1) {
                return null;
            }
            TreeNode node = new TreeNode();
            node.setType(getType());
            node.setId(uuid.toString());
            node.setName(getTreeName(entity));
            return node;
        }
        return null;
    }

    @Override
    protected void beforeSaveEntityStream(Privilege entity) {
        // 导入是全量的数据，要先删除权限相关的旧数据
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", entity.getUuid());
        this.nativeDao.batchExecute("delete from audit_privilege_other_resource where privilege_uuid=:uuid", params);
        this.nativeDao.batchExecute("delete from audit_privilege_resource where privilege_uuid=:uuid", params);
    }

    @Override
    protected List<IExportTable> childTableStream() {
        // 导出权限的关联关系
        return Lists.newArrayList(new IExportTable("select * from audit_privilege_other_resource where privilege_uuid=:uuid"),
                new IExportTable(Lists.newArrayList("privilege_uuid", "resource_uuid"), "select * from audit_privilege_resource where privilege_uuid=:uuid"));
    }

    @Override
    public void putChildProtoDataHqlParams(Privilege privilege, Map<String, Privilege> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        parentMap.put(start + privilege.getUuid(), privilege);
        if (privilege.getResources() != null && privilege.getResources().size() > 0) {
            String childHqlkey = this.getChildHqlKey(IexportType.ResourceParent);
            ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
            if (protoDataHql == null) {
                protoDataHql = new ProtoDataHql(getType(), "Resource");
                hqlMap.put(childHqlkey, protoDataHql);
            }
            Set<Resource> resourceSet = (Set<Resource>) protoDataHql.getParams().get("resourceSet");
            if (resourceSet == null) {
                resourceSet = new HashSet<>();
                protoDataHql.getParams().put("resourceSet", resourceSet);
            }
            resourceSet.addAll(privilege.getResources());
        }
        String childHqlkey = this.getChildHqlKey(IexportType.PrivilegeResource);
        ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
        if (protoDataHql == null) {
            protoDataHql = new ProtoDataHql(getType(), "PrivilegeResource");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    HqlUtils.appendSql("privilegeUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("privilegeUuids"));
                }
            });
            hqlMap.put(childHqlkey, protoDataHql);
        }
        Set<String> uuids = (Set<String>) protoDataHql.getParams().get("privilegeUuids");
        if (uuids == null) {
            uuids = new HashSet<>();
            protoDataHql.getParams().put("privilegeUuids", uuids);
        }
        uuids.add(privilege.getUuid());
    }

    @Override
    public JoinTableProcessor<Privilege, String> getJoinTableProcessor() {
        JoinTableProcessor<Privilege, String> privilegeJoinTableProcessor = new JoinTableProcessor<Privilege, String>() {
            @Override
            public <P extends JpaEntity<String>, C extends JpaEntity<String>> void joinTableSave(Session session, ProtoDataBeanTree<Privilege, P, C> t) {
                Set<P> set = t.getParentMap().get(IexportType.Role);
                if (set != null) {
                    for (P p : set) {
                        Query query = session.createSQLQuery("select 1 from audit_role_privilege where role_uuid =? and privilege_uuid =? ");
                        query.setParameter(0, p.getUuid());
                        query.setParameter(1, t.getProtoDataBean().getData().getUuid());
                        Object object = query.uniqueResult();
                        if (object == null) {
                            query = session.createSQLQuery("insert into audit_role_privilege (role_uuid, privilege_uuid) values (?, ?)");
                            query.setParameter(0, p.getUuid());
                            query.setParameter(1, t.getProtoDataBean().getData().getUuid());
                            query.executeUpdate();
                        }
                    }
                }
                Set<C> cSet = t.getChilderMap().get(IexportType.ResourceParent);
                if (cSet != null) {
                    for (C c : cSet) {
                        Query query = session.createSQLQuery("select 1 from audit_privilege_resource where privilege_uuid =? and resource_uuid =? ");
                        query.setParameter(0, t.getProtoDataBean().getData().getUuid());
                        query.setParameter(1, c.getUuid());
                        Object object = query.uniqueResult();
                        if (object == null) {
                            query = session.createSQLQuery("insert into audit_privilege_resource (privilege_uuid, resource_uuid) values (?, ?)");
                            query.setParameter(0, t.getProtoDataBean().getData().getUuid());
                            query.setParameter(1, c.getUuid());
                            query.executeUpdate();
                        }
                    }
                }


            }
        };
        return privilegeJoinTableProcessor;
    }

    @Override
    public Map<String, List<Privilege>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<Privilege>> map = new HashMap<>();
        // 页面或组件定义依赖的权限
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            List<Privilege> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Privilege.class);
            for (Privilege privilege : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, privilege, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            List<Privilege> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Privilege.class);
            for (Privilege privilege : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + privilege.getUuid();
                this.putParentMap(map, privilege, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.Role)) {
            Set<Privilege> privilegeSet = (Set<Privilege>) protoDataHql.getParams().get("privilegeSet");
            for (Privilege privilege : privilegeSet) {
                for (Role role : privilege.getRoles()) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + role.getUuid();
                    this.putParentMap(map, privilege, key);
                }
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
