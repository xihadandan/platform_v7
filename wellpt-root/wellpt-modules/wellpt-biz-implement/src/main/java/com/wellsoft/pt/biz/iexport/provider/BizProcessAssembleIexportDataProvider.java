/*
 * @(#)3/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.provider;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.biz.entity.BizProcessAssembleEntity;
import com.wellsoft.pt.biz.iexport.acceptor.BizProcessAssembleIexportData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 3/6/24.1	zhulh		3/6/24		Create
 * </pre>
 * @date 3/6/24
 */
@Service
@Transactional(readOnly = true)
public class BizProcessAssembleIexportDataProvider extends AbstractIexportDataProvider<BizProcessAssembleEntity, Long> {

    static {
        // 业务流程装配
        TableMetaData.register(IexportType.BizProcessAssemble, "业务流程_业务流程装配", BizProcessAssembleEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.BizProcessAssemble;
    }

    /**
     * 获取treeName
     *
     * @param bizProcessAssembleEntity
     * @return
     */
    @Override
    public String getTreeName(BizProcessAssembleEntity bizProcessAssembleEntity) {
        return new BizProcessAssembleIexportData(bizProcessAssembleEntity).getName();
    }

    @Override
    public TreeNode treeNode(Long uuid) {
        BizProcessAssembleEntity assembleEntity = getEntity(uuid);
        if (assembleEntity == null) {
            return null;
        }

        TreeNode node = new TreeNode();
        node.setId(uuid + StringUtils.EMPTY);
        node.setType(getType());
        node.setName(this.getTreeName(assembleEntity));

        // 业务流程定义
        TreeNode child = exportTreeNodeByDataProvider(assembleEntity.getProcessDefUuid(), IexportType.BizProcessDefinition);
        if (child != null) {
            node.getChildren().add(child);
        }

        String definitionJson = assembleEntity.getDefinitionJson();
        if (StringUtils.isNotBlank(definitionJson)) {
            BizProcessAssembleJson processAssembleJson = JsonUtils.json2Object(definitionJson, BizProcessAssembleJson.class);
            // 业务主体
            BizFormAndPageConfig entity = processAssembleJson.getEntity();
            addChildOfBizProcessEntity(node, entity);

            // 事项
            BizProcessItem item = processAssembleJson.getItem();
            if (item != null && MapUtils.isNotEmpty(item.getPages())) {
                for (Map.Entry<String, BizFormAndPageConfig> entry : item.getPages().entrySet()) {
                    BizFormAndPageConfig itemEntity = entry.getValue();
                    addChildOfBizProcessEntity(node, itemEntity);
                }
            }
            // 办理单
            BizProcessHandleForm handleForm = processAssembleJson.getHandleForm();
            if (handleForm != null) {
                addChildOfBizProcessEntity(node, handleForm.getProcess());

                List<BizFormAndPageConfig> nodes = handleForm.getNodes();
                if (CollectionUtils.isNotEmpty(nodes)) {
                    nodes.forEach(config -> addChildOfBizProcessEntity(node, config));
                }

                List<BizFormAndPageConfig> items = handleForm.getItems();
                if (CollectionUtils.isNotEmpty(items)) {
                    items.forEach(config -> addChildOfBizProcessEntity(node, config));
                }
            }
        }
        return node;
    }

    private void addChildOfBizProcessEntity(TreeNode node, BizFormAndPageConfig entity) {
        if (entity == null) {
            return;
        }
        if (StringUtils.isNotBlank(entity.getFormUuid()) && !StringUtils.equals(entity.getFormUuid(), entity.getRefFormUuid())) {
            TreeNode child = exportTreeNodeByDataProvider(entity.getFormUuid(), IexportType.FormDefinition);
            if (child != null) {
                node.getChildren().add(child);
            }
        }
        if (StringUtils.isNotBlank(entity.getPageUuid())) {
            TreeNode child = exportTreeNodeByDataProvider(entity.getPageUuid(), IexportType.AppPageDefinition);
            if (child != null) {
                node.getChildren().add(child);
            }
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BizProcessAssembleJson extends BaseObject {
        private static final long serialVersionUID = 6943845110185235189L;
        // 业务主体
        private BizFormAndPageConfig entity;
        // 事项
        private BizProcessItem item;
        // 办理单
        private BizProcessHandleForm handleForm;

        /**
         * @return the entity
         */
        public BizFormAndPageConfig getEntity() {
            return entity;
        }

        /**
         * @param entity 要设置的entity
         */
        public void setEntity(BizFormAndPageConfig entity) {
            this.entity = entity;
        }

        /**
         * @return the item
         */
        public BizProcessItem getItem() {
            return item;
        }

        /**
         * @param item 要设置的item
         */
        public void setItem(BizProcessItem item) {
            this.item = item;
        }

        /**
         * @return the handleForm
         */
        public BizProcessHandleForm getHandleForm() {
            return handleForm;
        }

        /**
         * @param handleForm 要设置的handleForm
         */
        public void setHandleForm(BizProcessHandleForm handleForm) {
            this.handleForm = handleForm;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BizFormAndPageConfig extends BaseObject {
        private static final long serialVersionUID = 6174194246625387248L;

        private String formUuid;
        private String refFormUuid;
        private String pageUuid;

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the refFormUuid
         */
        public String getRefFormUuid() {
            return refFormUuid;
        }

        /**
         * @param refFormUuid 要设置的refFormUuid
         */
        public void setRefFormUuid(String refFormUuid) {
            this.refFormUuid = refFormUuid;
        }

        /**
         * @return the pageUuid
         */
        public String getPageUuid() {
            return pageUuid;
        }

        /**
         * @param pageUuid 要设置的pageUuid
         */
        public void setPageUuid(String pageUuid) {
            this.pageUuid = pageUuid;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BizProcessItem extends BaseObject {
        private static final long serialVersionUID = -6812327775270798422L;

        private Map<String, BizFormAndPageConfig> pages;

        /**
         * @return the pages
         */
        public Map<String, BizFormAndPageConfig> getPages() {
            return pages;
        }

        /**
         * @param pages 要设置的pages
         */
        public void setPages(Map<String, BizFormAndPageConfig> pages) {
            this.pages = pages;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BizProcessHandleForm extends BaseObject {
        private static final long serialVersionUID = 5691041853372527256L;

        // 业务流程办理单
        private BizFormAndPageConfig process;

        // 过程节点办理单
        private List<BizFormAndPageConfig> nodes;

        // 事项办理单
        private List<BizFormAndPageConfig> items;

        /**
         * @return the process
         */
        public BizFormAndPageConfig getProcess() {
            return process;
        }

        /**
         * @param process 要设置的process
         */
        public void setProcess(BizFormAndPageConfig process) {
            this.process = process;
        }

        /**
         * @return the nodes
         */
        public List<BizFormAndPageConfig> getNodes() {
            return nodes;
        }

        /**
         * @param nodes 要设置的nodes
         */
        public void setNodes(List<BizFormAndPageConfig> nodes) {
            this.nodes = nodes;
        }

        /**
         * @return the items
         */
        public List<BizFormAndPageConfig> getItems() {
            return items;
        }

        /**
         * @param items 要设置的items
         */
        public void setItems(List<BizFormAndPageConfig> items) {
            this.items = items;
        }
    }

}
