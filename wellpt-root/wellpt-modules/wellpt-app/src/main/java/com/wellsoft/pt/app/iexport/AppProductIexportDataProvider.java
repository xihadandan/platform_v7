/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppProdVersionEntity;
import com.wellsoft.pt.app.entity.AppProduct;
import com.wellsoft.pt.app.iexport.acceptor.AppProductIexportData;
import com.wellsoft.pt.app.service.AppProdVersionService;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
@Service
@Transactional(readOnly = true)
public class AppProductIexportDataProvider extends AbstractIexportDataProvider<AppProduct, String> {
    static {
        TableMetaData.register(IexportType.AppProduct, "产品", AppProduct.class);
    }

    @Autowired
    AppProdVersionService appProdVersionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppProduct;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppProduct appProduct = this.dao.get(AppProduct.class, uuid);
        if (appProduct == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的产品依赖关系", "产品定义", uuid);
        }
        return new AppProductIexportData(appProduct);
    }

    @Override
    public TreeNode treeNode(String uuid) {
        TreeNode node = super.treeNode(uuid);
        AppProduct appProduct = getEntity(uuid);
        if (appProduct != null) {
            //分类
            if (appProduct.getCategoryUuid() != null) {
                node.appendChild(this.exportTreeNodeByDataProvider(appProduct.getCategoryUuid(), IexportType.AppCategory));
            }
            // 产品版本
            List<AppProdVersionEntity> prodVersionEntities = appProdVersionService.getAllByProdId(appProduct.getId());
            if (CollectionUtils.isNotEmpty(prodVersionEntities)) {
                for (AppProdVersionEntity versionEntity : prodVersionEntities) {
                    node.appendChild(this.exportTreeNodeByDataProvider(versionEntity.getUuid(), IexportType.AppProdVersion));
                }
            }
        }

        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
//        APP_DATA_TAG APP_TAG
        return Lists.newArrayList(new IExportTable("select * from APP_PROD_RELA_PAGE where prod_id=:id"),
                new IExportTable(Lists.newArrayList("data_id", "tag_uuid"), "select * from APP_DATA_TAG where data_id=:id"),
                new IExportTable("select * from APP_TAG t where exists ( select 1 from APP_DATA_TAG d where d.tag_uuid=t.uuid and d.data_id=:id )"));
    }

    @Override
    public String getTreeName(AppProduct appProduct) {
        return "产品: " + appProduct.getName();
    }

    @Override
    public void putChildProtoDataHqlParams(AppProduct appProduct, Map<String, AppProduct> parentMap, Map<String, ProtoDataHql> hqlMap) {
        parentMap.put(getType() + Separator.UNDERLINE.getValue() + appProduct.getUuid(), appProduct);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppProductIntegration))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "AppProductIntegration");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    Set<Serializable> set = (Set<Serializable>) protoDataHql.getParams().get("uuids");
                    HqlUtils.appendSql("appProductUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), set);
                    protoDataHql.getSbHql().append(" and parentUuid is null order by sortOrder");
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.AppProductIntegration), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppProductIntegration)), appProduct.getUuid());
    }

}
