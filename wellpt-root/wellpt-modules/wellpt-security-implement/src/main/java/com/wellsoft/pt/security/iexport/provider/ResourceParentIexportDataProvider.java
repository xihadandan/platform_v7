/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.iexport.acceptor.ResourceParentIexportData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ResourceParentIexportDataProvider extends AbstractIexportDataProvider<Resource, String> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.ResourceParent;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        Resource resource = this.dao.get(Resource.class, uuid);
        if (resource == null) {
            return new ErrorDataIexportData(IexportType.Resource, "找不到对应的资源依赖关系,可能已经被删除", "资源", uuid);
        }
        return new ResourceParentIexportData(resource);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 资源编号生成
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Resource), "code",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IexportDataColumn dataColumn = dataRecord.getDataColumn("code");
                        String oldCode = ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY);
                        IexportMappingColumn mappingColumn = iexportBundle.getMappingColumn(dataRecord.getTableName(),
                                dataColumn);
                        // 资源编号有新值时，直接返回
                        if (mappingColumn != null) {
                            return oldCode;
                        }
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return oldCode + Separator.UNDERLINE.getValue() + idGeneratorService.getBySysDate();
                    }

                });
        return iexportMetaData;
    }

    @Override
    public String getTreeName(Resource resource) {
        return new ResourceParentIexportData(resource).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(Resource resource, Map<String, Resource> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (resource.getParent() != null) {
            parentMap.put(start + resource.getParent().getUuid(), resource);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.ResourceParent))) {
                hqlMap.put(this.getChildHqlKey(IexportType.ResourceParent), this.getProtoDataHql("Resource"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.ResourceParent)), resource.getParent().getUuid());
        }
        if (StringUtils.isNotBlank(resource.getModuleId())) {
            this.putAppFunctionParentMap(resource, parentMap, hqlMap);
        }
    }

    @Override
    public JoinTableProcessor<Resource, String> getJoinTableProcessor() {
        JoinTableProcessor<Resource, String> privilegeJoinTableProcessor = new JoinTableProcessor<Resource, String>() {
            @Override
            public <P extends JpaEntity<String>, C extends JpaEntity<String>> void joinTableSave(Session session, ProtoDataBeanTree<Resource, P, C> t) {
                Set<P> set = t.getParentMap().get(IexportType.Privilege);
                if (set != null) {
                    for (P p : set) {
                        Query query = session.createSQLQuery("select 1 from audit_privilege_resource where privilege_uuid =? and resource_uuid =? ");
                        query.setParameter(0, p.getUuid());
                        query.setParameter(1, t.getProtoDataBean().getData().getUuid());
                        Object object = query.uniqueResult();
                        if (object == null) {
                            query = session.createSQLQuery("insert into audit_privilege_resource (privilege_uuid, resource_uuid) values (?, ?)");
                            query.setParameter(0, p.getUuid());
                            query.setParameter(1, t.getProtoDataBean().getData().getUuid());
                            query.executeUpdate();
                        }
                    }
                }
            }
        };
        return privilegeJoinTableProcessor;
    }

    @Override
    public Map<String, List<Resource>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<Resource>> map = new HashMap<>();
        // 页面或组件定义依赖的上级资源
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            List<Resource> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Resource.class);
            for (Resource resource : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, resource, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            List<Resource> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Resource.class);
            for (Resource resource : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "resourceParent" + Separator.UNDERLINE.getValue() + resource.getCode();
                this.putParentMap(map, resource, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.ResourceParent)) {
            List<Resource> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Resource.class);
            for (Resource resource : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + resource.getUuid();
                this.putParentMap(map, resource, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.Privilege)) {
            Set<Resource> resourceSet = (Set<Resource>) protoDataHql.getParams().get("resourceSet");
            for (Resource resource : resourceSet) {
                for (Privilege privilege : resource.getPrivileges()) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + privilege.getUuid();
                    this.putParentMap(map, resource, key);
                }
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            List<Resource> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), Resource.class);
            for (Resource resource : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + resource.getUuid();
                this.putParentMap(map, resource, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
