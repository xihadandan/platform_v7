/*
 * @(#)10/31/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.biz.entity.BizBusinessEntity;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizItemDefinitionIexportData;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class BizItemDefinitionIexportDataProvider extends AbstractIexportDataProvider<BizItemDefinitionEntity, String> {

    static {
        // 事项定义
        TableMetaData.register(IexportType.BizItemDefinition, "业务流程_事项定义", BizItemDefinitionEntity.class);
    }

    /**
     * 获取treeName
     *
     * @param bizItemDefinitionEntity
     * @return
     */
    @Override
    public String getTreeName(BizItemDefinitionEntity bizItemDefinitionEntity) {
        return new BizItemDefinitionIexportData(bizItemDefinitionEntity).getName();
    }

    @Override
    public String getType() {
        return IexportType.BizItemDefinition;
    }

    @Override
    public IexportData getData(String uuid) {
        BizItemDefinitionEntity bizItemDefinitionEntity = this.dao.get(BizItemDefinitionEntity.class, uuid);
        if (bizItemDefinitionEntity == null) {
            return new ErrorDataIexportData(IexportType.BizItemDefinition, "找不到对应的事项定义依赖关系,可能已经被删除",
                    "业务流程_事项定义", uuid);
        }
        return new BizItemDefinitionIexportData(bizItemDefinitionEntity);
    }

    @Override
    public void putChildProtoDataHqlParams(BizItemDefinitionEntity bizItemDefinitionEntity, Map<String, BizItemDefinitionEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        // 事项定义使用表单
        if (StringUtils.isNotBlank(bizItemDefinitionEntity.getFormId())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + bizItemDefinitionEntity.getFormId(),
                    bizItemDefinitionEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FormDefinition))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FormDefinition), this.getProtoDataHql("FormDefinition"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FormDefinition)), bizItemDefinitionEntity.getFormId());
        }
        // 业务
        if (StringUtils.isNotBlank(bizItemDefinitionEntity.getBusinessId())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + bizItemDefinitionEntity.getBusinessId(),
                    bizItemDefinitionEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.BizBusiness))) {
                hqlMap.put(this.getChildHqlKey(IexportType.BizBusiness), this.getProtoDataHql("BizBusinessEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.BizBusiness)), bizItemDefinitionEntity.getBusinessId());
        }
    }

    @Override
    public TreeNode treeNode(String uuid) {
        BizItemDefinitionEntity itemDefinitionEntity = getEntity(uuid);
        if (itemDefinitionEntity == null) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(this.getTreeName(itemDefinitionEntity));

        // 事项定义使用表单
        if (StringUtils.isNotBlank(itemDefinitionEntity.getFormId())) {
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            String formUuid = dyFormFacade.getFormUuidById(itemDefinitionEntity.getFormId());
            TreeNode child = exportTreeNodeByDataProvider(formUuid, IexportType.FormDefinition);
            if (child != null) {
                node.getChildren().add(child);
            }
        }

        // 业务
        if (StringUtils.isNotBlank(itemDefinitionEntity.getBusinessId())) {
            BizBusinessService businessService = ApplicationContextHolder.getBean(BizBusinessService.class);
            BizBusinessEntity businessEntity = businessService.getById(itemDefinitionEntity.getBusinessId());
            if (businessEntity != null) {
                TreeNode child = exportTreeNodeByDataProvider(businessEntity.getUuid(), IexportType.BizBusiness);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        }
        return node;
    }
}
