/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.CdDataDictionaryCategoryIexportData;
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
public class CdDataDictionaryCategoryIexportDataProvider extends AbstractIexportDataProvider<CdDataDictionaryCategoryEntity, Long> {

    static {
        // 字典分类
        TableMetaData.register(IexportType.CdDataDictionaryCategory, "字典分类", CdDataDictionaryCategoryEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.CdDataDictionaryCategory;
    }

    @Override
    public IexportData getData(Long uuid) {
        CdDataDictionaryCategoryEntity dataDictionaryCategoryEntity = this.dao.get(CdDataDictionaryCategoryEntity.class, uuid);
        if (dataDictionaryCategoryEntity == null) {
            return new ErrorDataIexportData(IexportType.CdDataDictionaryCategory, "找不到对应的字典分类依赖关系,可能已经被删除", "数据字典分类", Objects.toString(uuid));
        }
        return new CdDataDictionaryCategoryIexportData(dataDictionaryCategoryEntity);
    }

    /**
     * 获取treeName
     *
     * @param dataDictionaryCategoryEntity
     * @return
     */
    @Override
    public String getTreeName(CdDataDictionaryCategoryEntity dataDictionaryCategoryEntity) {
        return new CdDataDictionaryCategoryIexportData(dataDictionaryCategoryEntity).getName();
    }

    @Override
    public Map<String, List<CdDataDictionaryCategoryEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<CdDataDictionaryCategoryEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), CdDataDictionaryCategoryEntity.class);
        Map<String, List<CdDataDictionaryCategoryEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.CdDataDictionary)) {
            for (CdDataDictionaryCategoryEntity dataDictionaryCategory : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "cdDataDictionaryCategory" + Separator.UNDERLINE.getValue() + dataDictionaryCategory.getUuid();
                this.putParentMap(map, dataDictionaryCategory, key);
            }
        }
        return map;
    }
}
