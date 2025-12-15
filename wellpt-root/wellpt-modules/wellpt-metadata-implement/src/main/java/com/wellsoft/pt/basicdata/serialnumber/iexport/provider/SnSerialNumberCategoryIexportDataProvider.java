/*
 * @(#)8/1/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberCategoryEntity;
import com.wellsoft.pt.basicdata.serialnumber.iexport.acceptor.SnSerialNumberCategoryIexportData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/1/22.1	zhulh		8/1/22		Create
 * </pre>
 * @date 8/1/22
 */
@Service
@Transactional(readOnly = true)
public class SnSerialNumberCategoryIexportDataProvider extends AbstractIexportDataProvider<SnSerialNumberCategoryEntity, String> {
    static {
        // 4.1、 流水号
        TableMetaData.register(IexportType.SnSerialNumberCategory, "流水号分类", SnSerialNumberCategoryEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.SnSerialNumberCategory;
    }

    @Override
    public IexportData getData(String uuid) {
        SnSerialNumberCategoryEntity categoryEntity = this.dao.get(SnSerialNumberCategoryEntity.class, uuid);
        if (categoryEntity == null) {
            return new ErrorDataIexportData(IexportType.SnSerialNumberCategory, "找不到对应的流水号分类依赖关系,可能已经被删除", "流水号分类", uuid);
        }
        return new SnSerialNumberCategoryIexportData(categoryEntity);
    }

    /**
     * 获取treeName
     *
     * @param categoryEntity
     * @return
     */
    @Override
    public String getTreeName(SnSerialNumberCategoryEntity categoryEntity) {
        return new SnSerialNumberCategoryIexportData(categoryEntity).getName();
    }

    @Override
    public Map<String, List<SnSerialNumberCategoryEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<SnSerialNumberCategoryEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), SnSerialNumberCategoryEntity.class);
        Map<String, List<SnSerialNumberCategoryEntity>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.SnSerialNumberDefinition)) {
            for (SnSerialNumberCategoryEntity flowCategory : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "snSerialNumberCategoroy" + Separator.UNDERLINE.getValue() + flowCategory.getUuid();
                this.putParentMap(map, flowCategory, key);
            }
        }
        return map;
    }

}
