/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.CdDataDictionaryIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * 8/13/23.1	zhulh		8/13/23		Create
 * </pre>
 * @date 8/13/23
 */
@Service
@Transactional(readOnly = true)
public class CdDataDictionaryIexportDataProvider extends AbstractIexportDataProvider<CdDataDictionaryEntity, Long> {
    static {
        // 数据字典
        TableMetaData.register(IexportType.CdDataDictionary, "新版本数据字典", CdDataDictionaryEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.CdDataDictionary;
    }

    @Override
    public IexportData getData(Long uuid) {
        CdDataDictionaryEntity dataDictionaryEntity = this.dao.get(CdDataDictionaryEntity.class, uuid);
        if (dataDictionaryEntity == null) {
            return new ErrorDataIexportData(IexportType.DataDictionary, "找不到对应的数据字典依赖关系,可能已经被删除", "数据字典", Objects.toString(uuid));
        }
        return new CdDataDictionaryIexportData(dataDictionaryEntity);
    }

    /**
     * 获取treeName
     *
     * @param cdDataDictionaryEntity
     * @return
     */
    @Override
    public String getTreeName(CdDataDictionaryEntity cdDataDictionaryEntity) {
        return new CdDataDictionaryIexportData(cdDataDictionaryEntity).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(CdDataDictionaryEntity dataDictionaryEntity, Map<String, CdDataDictionaryEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        Long uuid = dataDictionaryEntity.getUuid();
        String key = start + uuid;
        parentMap.put(key, dataDictionaryEntity);
        // 字典项
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.CdDataDictionaryItem))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "CdDataDictionaryItemEntity");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where parentUuid is null and ");
                    HqlUtils.appendSql("dataDictUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                    protoDataHql.getSbHql().append(" order by sortOrder asc ");
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.CdDataDictionaryItem), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.CdDataDictionaryItem)), uuid);

        // 字典分类
        if (dataDictionaryEntity.getCategoryUuid() != null) {
            parentMap.put(start + "cdDataDictionaryCategory" + Separator.UNDERLINE.getValue() + dataDictionaryEntity.getCategoryUuid(),
                    dataDictionaryEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.CdDataDictionaryCategory))) {
                hqlMap.put(this.getChildHqlKey(IexportType.CdDataDictionaryCategory), this.getProtoDataHql("CdDataDictionaryCategoryEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.CdDataDictionaryCategory)), dataDictionaryEntity.getCategoryUuid());
        }

//        if (StringUtils.isNotBlank(dataDictionaryEntity.getModuleId())) {
//            this.putAppFunctionParentMap(dataDictionaryEntity, parentMap, hqlMap);
//        }
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        TreeNode node = new TreeNode();
        CdDataDictionaryEntity dataDictionaryEntity = this.dao.get(CdDataDictionaryEntity.class, uuid);
        if (dataDictionaryEntity != null) {
            node.setName("字典: " + dataDictionaryEntity.getName());
            node.setId(dataDictionaryEntity.getUuid().toString());
            node.setType(IexportType.CdDataDictionary);
            if (dataDictionaryEntity.getCategoryUuid() != null) {
                CdDataDictionaryCategoryEntity categoryEntity = this.dao.get(CdDataDictionaryCategoryEntity.class, dataDictionaryEntity.getCategoryUuid());
                if (categoryEntity != null) {
                    // 分类
                    TreeNode categoryNode = new TreeNode();
                    categoryNode.setId(categoryEntity.getUuid().toString());
                    categoryNode.setName("字典分类: " + categoryEntity.getName());
                    categoryNode.setType(IexportType.CdDataDictionaryCategory);
                    node.getChildren().add(categoryNode);
                }
            }

            List<CdDataDictionaryItemEntity> itemEntities = this.dao.findBy(CdDataDictionaryItemEntity.class, "dataDictUuid", uuid);
            if (CollectionUtils.isNotEmpty(itemEntities)) {
                for (CdDataDictionaryItemEntity item : itemEntities) {
                    TreeNode itemNode = new TreeNode(item.getUuid().toString(), "字典项: " + item.getLabel(), null);
                    itemNode.setType(IexportType.CdDataDictionaryItem);
                    itemNode.setVersion(item.getModifyTime() != null ? item.getModifyTime().getTime() : null);
                    node.getChildren().add(itemNode);
                }
            }
            return node;
        }
        return null;
    }


    @Override
    public TreeNode exportAsTreeNodeByFunction(AppFunction function) {
        if (StringUtils.isNotBlank(function.getId())) {
            CdDataDictionaryEntity entity = dao.findUniqueBy(CdDataDictionaryEntity.class, "code", function.getId());
            if (entity != null) {
                return this.treeNode(entity.getUuid());
            }
        }
        return null;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        List<IExportTable> tables = Lists.newArrayList(
                new IExportTable("select a.* from cd_data_dictionary_item_attr a ,cd_data_dictionary_item i where i.uuid = a.item_uuid and i.data_dict_uuid=:uuid "),
                /* 国际化语言定义 */
                new IExportTable("select * from cd_data_dict_i18n i where exists ( select 1 from cd_data_dictionary_item d where d.data_dict_uuid = :uuid )")
        );
        return tables;
    }
}
