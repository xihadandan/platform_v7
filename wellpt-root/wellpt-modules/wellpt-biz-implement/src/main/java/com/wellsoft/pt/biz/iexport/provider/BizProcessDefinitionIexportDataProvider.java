/*
 * @(#)10/31/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.provider;

import com.google.common.collect.Lists;
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
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizTagEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizProcessDefinitionIexportData;
import com.wellsoft.pt.biz.service.BizBusinessService;
import com.wellsoft.pt.biz.service.BizItemDefinitionService;
import com.wellsoft.pt.biz.service.BizTagService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 10/31/22.1	zhulh		10/31/22		Create
 * </pre>
 * @date 10/31/22
 */
@Service
@Transactional(readOnly = true)
public class BizProcessDefinitionIexportDataProvider extends AbstractIexportDataProvider<BizProcessDefinitionEntity, String> {

    static {
        // 业务流程定义
        TableMetaData.register(IexportType.BizProcessDefinition, "业务流程_业务流程定义", BizProcessDefinitionEntity.class);
    }

    /**
     * 获取treeName
     *
     * @param bizProcessDefinitionEntity
     * @return
     */
    @Override
    public String getTreeName(BizProcessDefinitionEntity bizProcessDefinitionEntity) {
        return new BizProcessDefinitionIexportData(bizProcessDefinitionEntity).getName();
    }

    @Override
    public String getType() {
        return IexportType.BizProcessDefinition;
    }

    @Override
    public IexportData getData(String uuid) {
        BizProcessDefinitionEntity bizProcessDefinitionEntity = this.dao.get(BizProcessDefinitionEntity.class, uuid);
        if (bizProcessDefinitionEntity == null) {
            return new ErrorDataIexportData(IexportType.BizTag, "找不到对应的业务流程定义依赖关系,可能已经被删除",
                    "业务流程_业务流程定义", uuid);
        }
        return new BizProcessDefinitionIexportData(bizProcessDefinitionEntity);
    }

    @Override
    public void putChildProtoDataHqlParams(BizProcessDefinitionEntity bizProcessDefinitionEntity, Map<String, BizProcessDefinitionEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(bizProcessDefinitionEntity.getUuid());
        Set<String> formUuidSet = parser.getFormUuidSet();
//        Set<String> flowBizDefIdSet = parser.getFlowBizDefIdSet();
        Set<String> tagIdSet = parser.getTagIdSet();
        // 事项定义使用表单
        if (CollectionUtils.isNotEmpty(formUuidSet)) {
            for (String formUuid : formUuidSet) {
                parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + formUuid, bizProcessDefinitionEntity);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FormDefinition))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.FormDefinition), this.getProtoDataHql("FormDefinition"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FormDefinition)), formUuid);
            }
        }
        // 业务标签
        if (CollectionUtils.isNotEmpty(tagIdSet)) {
            for (String tagId : tagIdSet) {
                parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + tagId, bizProcessDefinitionEntity);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.BizTag))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.BizTag), this.getProtoDataHql("BizTagEntity"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.BizTag)), tagId);
            }
        }
        // 业务
        String businessId = parser.getProcessBusinessId();
        if (StringUtils.isNotBlank(businessId)) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + businessId,
                    bizProcessDefinitionEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.BizBusiness))) {
                hqlMap.put(this.getChildHqlKey(IexportType.BizBusiness), this.getProtoDataHql("BizBusinessEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.BizBusiness)), businessId);
        }
//        // 流程业务
//        if (CollectionUtils.isNotEmpty(flowBizDefIdSet)) {
//            for (String flowBizDefId : flowBizDefIdSet) {
//                parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + flowBizDefId, bizProcessDefinitionEntity);
//                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowBusinessDefinition))) {
//                    hqlMap.put(this.getChildHqlKey(IexportType.FlowBusinessDefinition), this.getProtoDataHql("WfFlowBusinessDefinitionEntity"));
//                }
//                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowBusinessDefinition)), flowBizDefId);
//            }
//        }
    }

    @Override
    public TreeNode treeNode(String uuid) {
        BizProcessDefinitionEntity processDefinitionEntity = getEntity(uuid);
        if (processDefinitionEntity == null) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(this.getTreeName(processDefinitionEntity));

        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefinitionEntity.getUuid());
        Set<String> formUuidSet = parser.getFormUuidSet();
        Set<String> tagIdSet = parser.getTagIdSet();
        // 事项定义使用表单
        if (CollectionUtils.isNotEmpty(formUuidSet)) {
            formUuidSet.forEach(formUuid -> {
                TreeNode child = exportTreeNodeByDataProvider(formUuid, IexportType.FormDefinition);
                if (child != null) {
                    node.getChildren().add(child);
                }
            });
        }

        // 业务标签
        if (CollectionUtils.isNotEmpty(tagIdSet)) {
            BizTagService tagService = ApplicationContextHolder.getBean(BizTagService.class);
            List<BizTagEntity> tagEntities = tagService.listByIds(Lists.newArrayList(tagIdSet));
            tagEntities.forEach(tagEntity -> {
                TreeNode child = exportTreeNodeByDataProvider(tagEntity.getUuid(), IexportType.BizTag);
                if (child != null) {
                    node.getChildren().add(child);
                }
            });
        }

        // 业务
        String businessId = parser.getProcessBusinessId();
        if (StringUtils.isNotBlank(businessId)) {
            BizBusinessService businessService = ApplicationContextHolder.getBean(BizBusinessService.class);
            BizBusinessEntity businessEntity = businessService.getById(businessId);
            if (businessEntity != null) {
                TreeNode child = exportTreeNodeByDataProvider(businessEntity.getUuid(), IexportType.BizBusiness);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        }

        // 事项定义
        BizItemDefinitionService itemDefinitionService = ApplicationContextHolder.getBean(BizItemDefinitionService.class);
        List<BizItemDefinitionEntity> itemDefinitionEntities = itemDefinitionService.listByProcessDefUuid(processDefinitionEntity.getUuid());
        if (CollectionUtils.isNotEmpty(itemDefinitionEntities)) {
            itemDefinitionEntities.forEach(itemDefinition -> {
                TreeNode child = exportTreeNodeByDataProvider(itemDefinition.getUuid(), IexportType.BizItemDefinition);
                if (child != null) {
                    node.getChildren().add(child);
                }
            });
        }
        return node;
    }
}
