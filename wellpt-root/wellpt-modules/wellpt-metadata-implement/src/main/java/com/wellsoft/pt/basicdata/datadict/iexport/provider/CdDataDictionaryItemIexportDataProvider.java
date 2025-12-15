/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.CdDataDictionaryItemIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
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
 * 8/13/23.1	zhulh		8/13/23		Create
 * </pre>
 * @date 8/13/23
 */
@Service
@Transactional(readOnly = true)
public class CdDataDictionaryItemIexportDataProvider extends AbstractIexportDataProvider<CdDataDictionaryItemEntity, Long> {
    static {
        // 字典项
        TableMetaData.register(IexportType.CdDataDictionaryItem, "字典项", CdDataDictionaryItemEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.CdDataDictionaryItem;
    }

    @Override
    public IexportData getData(Long uuid) {
        CdDataDictionaryItemEntity dataDictionaryItemEntity = this.dao.get(CdDataDictionaryItemEntity.class, uuid);
        if (dataDictionaryItemEntity == null) {
            return new ErrorDataIexportData(IexportType.DataDictionary, "找不到对应的字典项依赖关系,可能已经被删除", "字典项", Objects.toString(uuid));
        }
        return new CdDataDictionaryItemIexportData(dataDictionaryItemEntity);
    }

    /**
     * 获取treeName
     *
     * @param cdDataDictionaryItemEntity
     * @return
     */
    @Override
    public String getTreeName(CdDataDictionaryItemEntity cdDataDictionaryItemEntity) {
        return new CdDataDictionaryItemIexportData(cdDataDictionaryItemEntity).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(CdDataDictionaryItemEntity cdDataDictionaryItemEntity, Map<String, CdDataDictionaryItemEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        Long uuid = cdDataDictionaryItemEntity.getUuid();
        String key = start + uuid;
        parentMap.put(key, cdDataDictionaryItemEntity);
        // 子节点
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.CdDataDictionaryItem))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "CdDataDictionaryItemEntity");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from CdDataDictionaryItemEntity where ");
                    HqlUtils.appendSql("parentUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("parentUuids"));
                    protoDataHql.getSbHql().append(" order by sortOrder asc ");
                }
            });

            hqlMap.put(this.getChildHqlKey(IexportType.CdDataDictionaryItem), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.CdDataDictionaryItem)), cdDataDictionaryItemEntity.getUuid(), "parentUuids");

        // 字典项扩展属性
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.CdDataDictionaryItemAttribute))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "CdDataDictionaryItemAttributeEntity");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    HqlUtils.appendSql("itemUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                    protoDataHql.getSbHql().append(" order by createTime asc ");
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.CdDataDictionaryItemAttribute), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.CdDataDictionaryItemAttribute)), uuid);
    }

    @Override
    public Map<String, List<CdDataDictionaryItemEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<CdDataDictionaryItemEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), CdDataDictionaryItemEntity.class);
        Map<String, List<CdDataDictionaryItemEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.CdDataDictionary)) {
            for (CdDataDictionaryItemEntity dictionaryItem : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dictionaryItem.getDataDictUuid();
                this.putParentMap(map, dictionaryItem, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.CdDataDictionaryItem)) {
            for (CdDataDictionaryItemEntity itemEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + itemEntity.getParentUuid();
                this.putParentMap(map, itemEntity, key);
            }
        }
        return map;
    }


}
