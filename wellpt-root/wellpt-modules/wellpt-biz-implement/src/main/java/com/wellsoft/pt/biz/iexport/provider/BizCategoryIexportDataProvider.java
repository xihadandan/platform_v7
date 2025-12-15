/*
 * @(#)11/1/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.biz.entity.BizCategoryEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizCategoryIexportData;
import org.apache.commons.lang.StringUtils;
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
 * 11/1/22.1	zhulh		11/1/22		Create
 * </pre>
 * @date 11/1/22
 */
@Service
@Transactional(readOnly = true)
public class BizCategoryIexportDataProvider extends AbstractIexportDataProvider<BizCategoryEntity, String> {

    static {
        // 业务分类
        TableMetaData.register(IexportType.BizCategory, "业务流程_业务分类", BizCategoryEntity.class);
    }

    /**
     * 获取treeName
     *
     * @param bizCategoryEntity
     * @return
     */
    @Override
    public String getTreeName(BizCategoryEntity bizCategoryEntity) {
        return new BizCategoryIexportData(bizCategoryEntity).getName();
    }

    @Override
    public String getType() {
        return IexportType.BizCategory;
    }

    @Override
    public IexportData getData(String uuid) {
        BizCategoryEntity bizCategoryEntity = this.dao.get(BizCategoryEntity.class, uuid);
        if (bizCategoryEntity == null) {
            return new ErrorDataIexportData(IexportType.BizBusiness, "找不到对应的业务分类依赖关系,可能已经被删除",
                    "业务流程_业务分类", uuid);
        }
        return new BizCategoryIexportData(bizCategoryEntity);
    }


    @Override
    public void putChildProtoDataHqlParams(BizCategoryEntity bizCategoryEntity, Map<String, BizCategoryEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        // 上级业务分类
        if (StringUtils.isNotBlank(bizCategoryEntity.getParentUuid())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + bizCategoryEntity.getParentUuid(),
                    bizCategoryEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.BizCategory))) {
                hqlMap.put(this.getChildHqlKey(IexportType.BizCategory), this.getProtoDataHql("BizCategoryEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.BizCategory)), bizCategoryEntity.getParentUuid());
        }
    }

    @Override
    public Map<String, List<BizCategoryEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<BizCategoryEntity>> map = new HashMap<>();

        if (protoDataHql.getParentType().equals(IexportType.BizBusiness)) {
            List<BizCategoryEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), BizCategoryEntity.class);
            for (BizCategoryEntity bizCategoryEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + bizCategoryEntity.getUuid();
                this.putParentMap(map, bizCategoryEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.BizCategory)) {
            // 上级业务分类
            List<BizCategoryEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), BizCategoryEntity.class);
            for (BizCategoryEntity bizCategoryEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + bizCategoryEntity.getUuid();
                this.putParentMap(map, bizCategoryEntity, key);
            }
        }

        return map;
    }

    @Override
    public TreeNode treeNode(String uuid) {
        BizCategoryEntity categoryEntity = getEntity(uuid);
        if (categoryEntity == null) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(this.getTreeName(categoryEntity));

        // 上级业务分类
        if (StringUtils.isNotBlank(categoryEntity.getParentUuid())) {
            TreeNode child = exportTreeNodeByDataProvider(categoryEntity.getParentUuid(), IexportType.BizCategory);
            if (child != null) {
                node.getChildren().add(child);
            }
        }
        return node;
    }
}
