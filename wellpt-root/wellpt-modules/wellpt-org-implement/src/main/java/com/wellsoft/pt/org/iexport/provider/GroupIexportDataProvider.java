/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.org.iexport.acceptor.GroupIexportData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:群组
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-19.1	linz		2016-1-19		Create
 * </pre>
 * @date 2016-1-19
 */
//@Service
//@Transactional(readOnly = true)
public class GroupIexportDataProvider extends AbstractIexportDataProvider<MultiOrgGroup, String> {
    static {
        TableMetaData.register(IexportType.Group, "群组", MultiOrgGroup.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.Group;
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
        MultiOrgGroup group = this.dao.get(MultiOrgGroup.class, uuid);
        if (group == null) {
            return new ErrorDataIexportData(IexportType.Group, "找不到对应的群组依赖关系,可能已经被删除", "群组", uuid);
        }
        return new GroupIexportData(group);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 组件ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.Group), "id",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return idGeneratorService.generate(MultiOrgGroup.class, "G0000000000", true);
                    }

                });
        return iexportMetaData;
    }

    @Override
    public String getTreeName(MultiOrgGroup multiOrgGroup) {
        return new GroupIexportData(multiOrgGroup).getName();
    }

    @Override
    public Map<String, List<MultiOrgGroup>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<MultiOrgGroup>> map = new HashMap<>();
        List<MultiOrgGroup> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), MultiOrgGroup.class);
        // 页面或组件定义依赖的群组
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (MultiOrgGroup multiOrgGroup : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, multiOrgGroup, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (MultiOrgGroup multiOrgGroup : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "group" + Separator.UNDERLINE.getValue() + multiOrgGroup.getId();
                this.putParentMap(map, multiOrgGroup, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (MultiOrgGroup multiOrgGroup : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + multiOrgGroup.getUuid();
                this.putParentMap(map, multiOrgGroup, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
