/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.GenerateHql;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.iexport.acceptor.SystemTableIexportData;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Description:流水号定义
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-18	linz		2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
@Service
@Transactional(readOnly = true)
public class SystemTableIexportDataProvider extends AbstractIexportDataProvider<SystemTable, String> {

    static {
        TableMetaData.register(IexportType.SystemTable, "系统表结构", SystemTable.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.SystemTable;
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
        SystemTable systemTable = this.dao.get(SystemTable.class, uuid);
        if (systemTable == null) {
            return new ErrorDataIexportData(IexportType.SystemTable, "找不到对应的系统表结构定义依赖关系,可能已经被删除", "系统表结构", uuid);
        }
        return new SystemTableIexportData(systemTable);
    }

    @Override
    public String getTreeName(SystemTable systemTable) {
        return new SystemTableIexportData(systemTable).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(SystemTable systemTable, Map<String, SystemTable> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (StringUtils.isNotBlank(systemTable.getModuleName())) {
            parentMap.put(start + "MODULE_ID", systemTable);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataDictionaryParent))) {
                ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "DataDictionary");
                protoDataHql.setGenerateHql(new GenerateHql() {
                    @Override
                    public void build(ProtoDataHql protoDataHql) {
                        protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                        HqlUtils.appendSql("type", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("types"));
                    }
                });
                hqlMap.put(this.getChildHqlKey(IexportType.DataDictionaryParent), protoDataHql);
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataDictionaryParent)), "MODULE_ID", "types");
        }
    }

}
