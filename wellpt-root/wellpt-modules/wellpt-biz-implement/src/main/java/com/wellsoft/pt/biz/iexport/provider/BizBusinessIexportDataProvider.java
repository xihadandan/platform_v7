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
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizBusinessIexportData;
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
public class BizBusinessIexportDataProvider extends AbstractIexportDataProvider<BizBusinessEntity, String> {

    static {
        // 业务
        TableMetaData.register(IexportType.BizBusiness, "业务流程_业务", BizBusinessEntity.class);
    }

    /**
     * 获取treeName
     *
     * @param bizBusinessEntity
     * @return
     */
    @Override
    public String getTreeName(BizBusinessEntity bizBusinessEntity) {
        return new BizBusinessIexportData(bizBusinessEntity).getName();
    }

    @Override
    public String getType() {
        return IexportType.BizBusiness;
    }

    @Override
    public IexportData getData(String uuid) {
        BizBusinessEntity bizBusinessEntity = this.dao.get(BizBusinessEntity.class, uuid);
        if (bizBusinessEntity == null) {
            return new ErrorDataIexportData(IexportType.BizBusiness, "找不到对应的业务依赖关系,可能已经被删除",
                    "业务流程_业务", uuid);
        }
        return new BizBusinessIexportData(bizBusinessEntity);
    }

    @Override
    public void putChildProtoDataHqlParams(BizBusinessEntity bizBusinessEntity, Map<String, BizBusinessEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        // 业务分类
        if (StringUtils.isNotBlank(bizBusinessEntity.getCategoryUuid())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + bizBusinessEntity.getCategoryUuid(),
                    bizBusinessEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.BizCategory))) {
                hqlMap.put(this.getChildHqlKey(IexportType.BizCategory), this.getProtoDataHql("BizCategoryEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.BizCategory)), bizBusinessEntity.getCategoryUuid());
        }
    }

    @Override
    public Map<String, List<BizBusinessEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<BizBusinessEntity>> map = new HashMap<>();

        if (protoDataHql.getParentType().equals(IexportType.BizProcessDefinition)
                || protoDataHql.getParentType().equals(IexportType.BizItemDefinition)) {
            String hql = "from BizBusinessEntity t where t.id in(:uuids)";
            List<BizBusinessEntity> list = this.dao.find(hql, protoDataHql.getParams(), BizBusinessEntity.class);
            for (BizBusinessEntity bizBusinessEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + bizBusinessEntity.getId();
                this.putParentMap(map, bizBusinessEntity, key);
            }
        }

        return map;
    }

    @Override
    public TreeNode treeNode(String uuid) {
        BizBusinessEntity businessEntity = getEntity(uuid);
        if (businessEntity == null) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(this.getTreeName(businessEntity));

        // 业务分类
        if (StringUtils.isNotBlank(businessEntity.getCategoryUuid())) {
            TreeNode child = exportTreeNodeByDataProvider(businessEntity.getCategoryUuid(), IexportType.BizCategory);
            if (child != null) {
                node.getChildren().add(child);
            }
        }
        return node;
    }
}
