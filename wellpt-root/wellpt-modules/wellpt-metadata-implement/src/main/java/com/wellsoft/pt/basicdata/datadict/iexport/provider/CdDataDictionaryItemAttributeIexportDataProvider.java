/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemAttributeEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.CdDataDictionaryItemAttributeIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class CdDataDictionaryItemAttributeIexportDataProvider extends AbstractIexportDataProvider<CdDataDictionaryItemAttributeEntity, Long> {
    static {
        // 字典项扩展属性
        TableMetaData.register(IexportType.CdDataDictionaryItemAttribute, "字典项扩展属性", CdDataDictionaryItemEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.CdDataDictionaryItemAttribute;
    }

    @Override
    public IexportData getData(Long uuid) {
        CdDataDictionaryItemAttributeEntity dataDictionaryItemAttributeEntity = this.dao.get(CdDataDictionaryItemAttributeEntity.class, uuid);
        if (dataDictionaryItemAttributeEntity == null) {
            return new ErrorDataIexportData(IexportType.DataDictionary, "找不到对应的字典项扩展属性依赖关系,可能已经被删除", "字典项扩展属性", Objects.toString(uuid));
        }
        return new CdDataDictionaryItemAttributeIexportData(dataDictionaryItemAttributeEntity);
    }

    /**
     * 获取treeName
     *
     * @param dataDictionaryItemAttributeEntity
     * @return
     */
    @Override
    public String getTreeName(CdDataDictionaryItemAttributeEntity dataDictionaryItemAttributeEntity) {
        return new CdDataDictionaryItemAttributeIexportData(dataDictionaryItemAttributeEntity).getName();
    }

    @Override
    public Map<String, List<CdDataDictionaryItemAttributeEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<CdDataDictionaryItemAttributeEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), CdDataDictionaryItemAttributeEntity.class);
        Map<String, List<CdDataDictionaryItemAttributeEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.CdDataDictionaryItem)) {
            for (CdDataDictionaryItemAttributeEntity itemAttributeEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + itemAttributeEntity.getItemUuid();
                this.putParentMap(map, itemAttributeEntity, key);
            }
        }
        return map;
    }
}
