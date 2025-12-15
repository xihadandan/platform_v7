/*
 * @(#)10/31/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.biz.entity.BizTagEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizTagIexportData;
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
 * 10/31/22.1	zhulh		10/31/22		Create
 * </pre>
 * @date 10/31/22
 */
@Service
@Transactional(readOnly = true)
public class BizTagIexportDataProvider extends AbstractIexportDataProvider<BizTagEntity, String> {
    static {
        // 业务标签
        TableMetaData.register(IexportType.BizTag, "业务流程_业务标签", BizTagEntity.class);
    }

    /**
     * 获取treeName
     *
     * @param bizTagEntity
     * @return
     */
    @Override
    public String getTreeName(BizTagEntity bizTagEntity) {
        return new BizTagIexportData(bizTagEntity).getName();
    }

    @Override
    public String getType() {
        return IexportType.BizTag;
    }

    @Override
    public IexportData getData(String uuid) {
        BizTagEntity bizTagEntity = this.dao.get(BizTagEntity.class, uuid);
        if (bizTagEntity == null) {
            return new ErrorDataIexportData(IexportType.BizTag, "找不到对应的业务标签依赖关系,可能已经被删除",
                    "业务流程_业务标签", uuid);
        }
        return new BizTagIexportData(bizTagEntity);
    }

    @Override
    public Map<String, List<BizTagEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<BizTagEntity>> map = new HashMap<>();

        if (protoDataHql.getParentType().equals(IexportType.BizProcessDefinition)) {
            String hql = "from BizTagEntity t where t.id in(:uuids)";
            List<BizTagEntity> list = this.dao.find(hql, protoDataHql.getParams(), BizTagEntity.class);
            for (BizTagEntity bizTagEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + bizTagEntity.getId();
                this.putParentMap(map, bizTagEntity, key);
            }
        }

        return map;
    }
}