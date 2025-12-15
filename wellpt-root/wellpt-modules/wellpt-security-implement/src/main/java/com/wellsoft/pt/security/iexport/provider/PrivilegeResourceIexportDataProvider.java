package com.wellsoft.pt.security.iexport.provider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataBean;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.iexport.acceptor.PrivilegeResourceIexportData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * @author yt
 * @title: PrivilegeResourceIexportDataProvider
 * @date 2020/9/17 15:38
 */
@Service
@Transactional(readOnly = true)
public class PrivilegeResourceIexportDataProvider extends AbstractIexportDataProvider<PrivilegeResource, String> {
    static {
        // 权限资源
        TableMetaData.register(IexportType.PrivilegeResource, "权限资源", PrivilegeResource.class);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public IexportData getData(String uuid) {
        PrivilegeResource privilegeResource = this.dao.get(PrivilegeResource.class, uuid);
        if (privilegeResource == null) {
            return new ErrorDataIexportData(IexportType.PrivilegeResource, "找不到对应的权限依赖关系,可能已经被删除", "权限资源", uuid);
        }
        return new PrivilegeResourceIexportData(privilegeResource);
    }

    @Override
    public String getTreeName(PrivilegeResource privilegeResource) {
        return new PrivilegeResourceIexportData(privilegeResource).getName();
    }

    @Override
    public Map<String, String> getTreeNameMap(Collection<PrivilegeResource> list) {
        Multimap<String, String> uuidMap = HashMultimap.create();
        Multimap<String, PrivilegeResource> privilegeResourceMap = HashMultimap.create();
        this.putMultimap(list, uuidMap, privilegeResourceMap);
        Map<String, String> map = new HashMap<>();
        if (uuidMap.containsKey(IexportType.AppPageDefinition)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppPageDefinition));
            List<AppPageDefinition> appPageDefinitionList = this.getResourceList("AppPageDefinition", AppPageDefinition.class, uuidSet);
            for (AppPageDefinition appPageDefinition : appPageDefinitionList) {
                for (PrivilegeResource privilegeResource : privilegeResourceMap.get(IexportType.AppPageDefinition + Separator.UNDERLINE.getValue() + appPageDefinition.getUuid())) {
                    map.put(privilegeResource.getUuid(), "引用资源：页面-" + appPageDefinition.getName());
                }
            }
        }
        if (uuidMap.containsKey(IexportType.AppFunction)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppFunction));
            List<AppFunction> appFunctionList = this.getResourceList("AppFunction", AppFunction.class, uuidSet);
            for (AppFunction appFunction : appFunctionList) {
                for (PrivilegeResource privilegeResource : privilegeResourceMap.get(IexportType.AppFunction + Separator.UNDERLINE.getValue() + appFunction.getUuid())) {
                    map.put(privilegeResource.getUuid(), "引用资源：功能-" + appFunction.getName());
                }
            }
        }
        if (uuidMap.containsKey(IexportType.AppProductIntegration)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppProductIntegration));
            List<AppProductIntegration> appProductIntegrationList = this.getResourceList("AppProductIntegration", AppProductIntegration.class, uuidSet);
            for (AppProductIntegration appProductIntegration : appProductIntegrationList) {
                for (PrivilegeResource privilegeResource : privilegeResourceMap.get(IexportType.AppProductIntegration + Separator.UNDERLINE.getValue() + appProductIntegration.getUuid())) {
                    map.put(privilegeResource.getUuid(), "引用资源：" + AppType.getName(Integer.valueOf(appProductIntegration.getDataType())) + "-" + appProductIntegration.getDataName());
                }
            }
        }
        return map;
    }


    @Override
    public Set<String> dataCheck(Collection<PrivilegeResource> list, Map<String, ProtoDataBean> beanMap) {
        Multimap<String, String> uuidMap = HashMultimap.create();
        Multimap<String, PrivilegeResource> privilegeResourceMap = HashMultimap.create();
        this.putMultimap(list, uuidMap, privilegeResourceMap);
        if (uuidMap.containsKey(IexportType.AppPageDefinition)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppPageDefinition));
            this.removeSetMap(IexportType.AppPageDefinition, beanMap, uuidSet, privilegeResourceMap);
            if (uuidSet.size() > 0) {
                List<JpaEntity> idEntityList = this.getResourceList("AppPageDefinition", AppPageDefinition.class, uuidSet);
                this.removeSetMap(IexportType.AppPageDefinition, idEntityList, privilegeResourceMap);
            }

        }
        if (uuidMap.containsKey(IexportType.AppFunction)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppFunction));
            this.removeSetMap(IexportType.AppFunction, beanMap, uuidSet, privilegeResourceMap);
            if (uuidSet.size() > 0) {
                List<JpaEntity> idEntityList = this.getResourceList("AppFunction", AppFunction.class, uuidSet);
                this.removeSetMap(IexportType.AppFunction, idEntityList, privilegeResourceMap);
            }
        }
        if (uuidMap.containsKey(IexportType.AppProductIntegration)) {
            Set<Serializable> uuidSet = new HashSet<>(uuidMap.get(IexportType.AppProductIntegration));
            this.removeSetMap(IexportType.AppProductIntegration, beanMap, uuidSet, privilegeResourceMap);
            if (uuidSet.size() > 0) {
                List<JpaEntity> idEntityList = this.getResourceList("AppProductIntegration", AppProductIntegration.class, uuidSet);
                this.removeSetMap(IexportType.AppProductIntegration, idEntityList, privilegeResourceMap);
            }
        }
        Set<String> set = new HashSet<>();
        for (PrivilegeResource resource : privilegeResourceMap.values()) {
            set.add(resource.getUuid());
        }
        return set;
    }

    private void removeSetMap(String type, List<JpaEntity> idEntityList, Multimap<String, PrivilegeResource> privilegeResourceMap) {
        for (JpaEntity idEntity : idEntityList) {
            String key = type + Separator.UNDERLINE.getValue() + idEntity.getUuid();
            privilegeResourceMap.removeAll(key);
        }
    }

    private void removeSetMap(String type, Map<String, ProtoDataBean> beanMap, Set<Serializable> uuidSet, Multimap<String, PrivilegeResource> privilegeResourceMap) {
        Iterator<Serializable> iterator = uuidSet.iterator();
        while (iterator.hasNext()) {
            Serializable uuid = iterator.next();
            String key = type + Separator.UNDERLINE.getValue() + uuid;
            if (beanMap.containsKey(key)) {
                privilegeResourceMap.removeAll(key);
                iterator.remove();
            }
        }
    }

    @Override
    public Map<String, List<PrivilegeResource>> getParentMapList(ProtoDataHql protoDataHql) {
        List<PrivilegeResource> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), PrivilegeResource.class);
        Map<String, List<PrivilegeResource>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.Privilege)) {
            Multimap<String, String> uuidMap = HashMultimap.create();
            Multimap<String, PrivilegeResource> privilegeResourceMap = HashMultimap.create();
            this.putMultimap(list, uuidMap, privilegeResourceMap);
            if (uuidMap.containsKey(IexportType.AppPageDefinition)) {
                List<IdEntity> idEntityList = this.getResourceList("AppPageDefinition", AppPageDefinition.class, (Set) uuidMap.get(IexportType.AppPageDefinition));
                this.putParentMap(idEntityList, protoDataHql.getParentType(), map, privilegeResourceMap, IexportType.AppPageDefinition);
            }
            if (uuidMap.containsKey(IexportType.AppFunction)) {
                List<IdEntity> idEntityList = this.getResourceList("AppFunction", AppFunction.class, (Set) uuidMap.get(IexportType.AppFunction));
                this.putParentMap(idEntityList, protoDataHql.getParentType(), map, privilegeResourceMap, IexportType.AppFunction);
            }
            if (uuidMap.containsKey(IexportType.AppProductIntegration)) {
                List<IdEntity> idEntityList = this.getResourceList("AppProductIntegration", AppProductIntegration.class, (Set) uuidMap.get(IexportType.AppProductIntegration));
                this.putParentMap(idEntityList, protoDataHql.getParentType(), map, privilegeResourceMap, IexportType.AppProductIntegration);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

    private void putMultimap(Collection<PrivilegeResource> list, Multimap<String, String> uuidMap, Multimap<String, PrivilegeResource> privilegeResourceMap) {
        for (PrivilegeResource privilegeResource : list) {
            String uuid = privilegeResource.getResourceUuid();
            if (uuid.startsWith(AppConstants.PAGE_PREFIX) || uuid.startsWith(AppConstants.PAGEREF_PREFIX)) {
                uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
                uuidMap.put(IexportType.AppPageDefinition, uuid);
                privilegeResourceMap.put(IexportType.AppPageDefinition + Separator.UNDERLINE.getValue() + uuid, privilegeResource);
            } else if (uuid.startsWith(AppConstants.FUNCTIONREF_OF_PAGE_PREFIX)) {
                uuid = uuid.substring(uuid.lastIndexOf(Separator.UNDERLINE.getValue()) + 1);
                uuidMap.put(IexportType.AppFunction, uuid);
                privilegeResourceMap.put(IexportType.AppFunction + Separator.UNDERLINE.getValue() + uuid, privilegeResource);
            } else {
                uuid = privilegeResource.getResourceUuid();
                uuidMap.put(IexportType.AppProductIntegration, uuid);
                privilegeResourceMap.put(IexportType.AppProductIntegration + Separator.UNDERLINE.getValue() + uuid, privilegeResource);
            }
        }
    }

    private void putParentMap(List<IdEntity> idEntityList, String parentType, Map<String, List<PrivilegeResource>> map, Multimap<String, PrivilegeResource> privilegeResourceMap, String type) {
        for (IdEntity idEntity : idEntityList) {
            for (PrivilegeResource privilegeResource : privilegeResourceMap.get(type + Separator.UNDERLINE.getValue() + idEntity.getUuid())) {
                String key = parentType + Separator.UNDERLINE.getValue() + privilegeResource.getPrivilegeUuid();
                super.putParentMap(map, privilegeResource, key);
            }
        }
    }

    private List getResourceList(String entityName, Class clazz, Set<Serializable> uuidSet) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("from ").append(entityName).append(" where ");
        HqlUtils.appendSql("uuid", query, hql, uuidSet);
        List resourceList = this.dao.find(hql.toString(), query, clazz);
        return resourceList;
    }
}
