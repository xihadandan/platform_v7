/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.iexport.provider;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.enums.ResourceType;
import com.wellsoft.pt.security.iexport.acceptor.ResourceIexportData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ResourceIexportDataProvider extends AbstractIexportDataProvider<Resource, String> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Resource;
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
        return new ResourceIexportData(resource);
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
        return ((ResourceType.MENU.getValue().equals(resource.getType()) ? "链接: " :
                (ResourceType.nameByValue(resource.getType())) + ": ")) + resource.getName();
    }

    @Override
    protected List<IExportTable> childTableStream() {
        // 导出表单依赖的资源定义关系数据
        return Lists.newArrayList(
                /* 资源组 */
                new IExportTable("select * from app_module_res_group g where exists ( select 1 from app_module_res_group_member m where m.member_uuid=:uuid and m.group_uuid = g.uuid  ) "),
                new IExportTable("select * from app_module_res_group_member m where m.member_uuid=:uuid ")
        );
    }

    @Override
    public void putChildProtoDataHqlParams(Resource resource, Map<String, Resource> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (resource.getChildren() != null && resource.getChildren().size() > 0) {
            parentMap.put(start + resource.getUuid(), resource);
            String childHqlkey = this.getChildHqlKey(IexportType.Resource);
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
            resourceSet.addAll(resource.getChildren());
        }
        if (StringUtils.isNotBlank(resource.getModuleId())) {
            this.putAppFunctionParentMap(resource, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<Resource>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<Resource>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.Resource)) {
            Set<Resource> resourceSet = (Set<Resource>) protoDataHql.getParams().get("resourceSet");
            for (Resource resource : resourceSet) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + resource.getParent().getUuid();
                this.putParentMap(map, resource, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
