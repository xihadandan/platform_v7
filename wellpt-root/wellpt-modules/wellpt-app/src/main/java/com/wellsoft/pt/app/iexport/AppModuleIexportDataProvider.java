/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppModuleResGroupEntity;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppUserWidgetDefEntity;
import com.wellsoft.pt.app.iexport.acceptor.AppModuleIexportData;
import com.wellsoft.pt.app.service.AppModuleResGroupService;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppUserWidgetDefService;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
@Service
@Transactional(readOnly = true)
public class AppModuleIexportDataProvider extends AbstractIexportDataProvider<AppModule, String> {
    static {
        TableMetaData.register(IexportType.AppModule, "模块", AppModule.class);
    }

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private FormDefinitionService dyFormDefinitionService;

    @Autowired
    private AppModuleResGroupService appModuleResGroupService;

    @Autowired
    private ResourceFacadeService resourceFacadeService;

    @Autowired
    private FlowDefineService flowDefineService;

    @Autowired
    private FlowCategoryService flowCategoryService;

    @Autowired
    private RoleFacadeService roleFacadeService;

    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    @Autowired
    private AppUserWidgetDefService appUserWidgetDefService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppModule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppModule appModule = this.dao.get(AppModule.class, uuid);
        if (appModule == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的模块依赖关系", "模块定义", uuid);
        }
        return new AppModuleIexportData(appModule);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 模块ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppModule), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(AppModule.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppModule appModule) {
        return "模块: " + appModule.getName();
    }


    @Override
    public Map<String, List<AppModule>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppModule> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppModule.class);
        Map<String, List<AppModule>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            for (AppModule appModule : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appModule.getUuid();
                this.putParentMap(map, appModule, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

    @Override
    public TreeNode treeNode(String uuid) {
        AppModule appModule = getEntity(uuid);
        if (appModule == null) {
            return null;
        }
        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(this.getTreeName(appModule));

        // 模块下的资源导出
        if (appModule.getCategoryUuid() != null) {
            node.appendChild(this.exportTreeNodeByDataProvider(appModule.getCategoryUuid(), IexportType.AppCategory));
        }

        // 页面
        List<AppPageDefinition> appPageDefinitions = appPageDefinitionService.listRecentVersionPageByAppId(appModule.getId());
        if (CollectionUtils.isNotEmpty(appPageDefinitions)) {
            for (AppPageDefinition page : appPageDefinitions) {
//                TreeNode child = this.exportTreeNodeByDataProvider(page.getUuid(), IexportType.AppPageDefinition);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }
                this.exportTreeNodeByDataProvider(page.getUuid(), IexportType.AppPageDefinition, node);
            }
        }

        // 功能组件
        List<AppUserWidgetDefEntity> appUserWidgetDefEntities = appUserWidgetDefService.getWidgetDefinitionByTypeAndAppId(AppUserWidgetDefEntity.Type.FUNCTION_WIDGET, appModule.getId());
        for (AppUserWidgetDefEntity defEntity : appUserWidgetDefEntities) {
            this.exportTreeNodeByDataProvider(defEntity.getUuid(), IexportType.AppUserWidgetDef, node);
//            TreeNode child = this.exportTreeNodeByDataProvider(defEntity.getUuid(), IexportType.AppUserWidgetDef);
//            if (child != null) {
//                node.getChildren().add(child);
//            }
        }

        // 表单
        List<FormDefinition> formDefinitions = dyFormDefinitionService.listRecentVersionFormByModuleId(appModule.getId());
        if (CollectionUtils.isNotEmpty(formDefinitions)) {
            for (FormDefinition formDef : formDefinitions) {
                IexportDataProvider iexportDataProvider = IexportDataProviderFactory.getDataProvider(IexportType.FormDefinition);
                if (iexportDataProvider != null) {
                    this.exportTreeNodeByDataProvider(formDef.getUuid(), IexportType.FormDefinition, node);
//                    TreeNode child = iexportDataProvider.exportAsTreeNode(formDef.getUuid());
//                    if (child != null) {
//                        node.getChildren().add(child);
//                    }
                }
            }
        }

        // 流程
        List<FlowDefinitionQueryItem> flowDefinitionQueryItems = flowDefineService.queryAllModuleFlowDefs(appModule.getId());
        if (CollectionUtils.isNotEmpty(flowDefinitionQueryItems)) {
            for (FlowDefinitionQueryItem item : flowDefinitionQueryItems) {
//                TreeNode child = exportTreeNodeByDataProvider(item.getUuid(), IexportType.FlowDefinition);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }
                this.exportTreeNodeByDataProvider(item.getUuid(), IexportType.FlowDefinition, node);

            }
        }

        // 流程分类
        FlowCategory category = new FlowCategory();
        category.setModuleId(appModule.getId());
        List<FlowCategory> flowCategories = flowCategoryService.listByEntity(category);
        if (CollectionUtils.isNotEmpty(flowCategories)) {
            for (FlowCategory cat : flowCategories) {
                TreeNode child = exportTreeNodeByDataProvider(cat.getUuid(), IexportType.FlowCategory);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        }

        // 资源分组
        List<AppModuleResGroupEntity> resGroupEntities = appModuleResGroupService.listByModuleId(appModule.getId());
        if (CollectionUtils.isNotEmpty(resGroupEntities)) {
            for (AppModuleResGroupEntity groupEntity : resGroupEntities) {
                TreeNode child = exportTreeNodeByDataProvider(groupEntity.getUuid(), IexportType.AppModuleResGroup);
                if (child != null) {
                    node.getChildren().add(child);
                }

            }
        }
        // 链接
        List<Resource> resourceList = resourceFacadeService.getModuleMenuResources(appModule.getId());
        if (CollectionUtils.isNotEmpty(resourceList)) {
            for (Resource resource : resourceList) {
                TreeNode child = exportTreeNodeByDataProvider(resource.getUuid(), IexportType.Resource);
                if (child != null) {
                    node.getChildren().add(child);
                }

            }
        }

        // 数据模型对象
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", appModule.getId());
        List<QueryItem> dataModelList = dao.query("from DataModelEntity where module=:moduleId and type in ( 0,1 ) order by type asc ", params, QueryItem.class);
        if (CollectionUtils.isNotEmpty(dataModelList)) {
            for (QueryItem item : dataModelList) {
                this.exportTreeNodeByDataProvider(item.getLong("uuid"), IexportType.DataModel, node);
//                TreeNode child = exportTreeNodeByDataProvider(item.getLong("uuid"), IexportType.DataModel);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }
            }
        }

        // 数据字典
        List<QueryItem> dataDicList = dao.query("from CdDataDictionaryEntity where moduleId=:moduleId", params, QueryItem.class);
        if (CollectionUtils.isNotEmpty(dataDicList)) {
            for (QueryItem item : dataDicList) {
                this.exportTreeNodeByDataProvider(item.getLong("uuid"), IexportType.CdDataDictionary, node);
//                TreeNode child = exportTreeNodeByDataProvider(item.getLong("uuid"), IexportType.CdDataDictionary);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }

            }
        }


        // 角色、权限
        List<Role> roles = roleFacadeService.getRolesByAppId(appModule.getId());
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                this.exportTreeNodeByDataProvider(r.getUuid(), IexportType.Role, node);
//                TreeNode child = exportTreeNodeByDataProvider(r.getUuid(), IexportType.Role);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }
            }
        }

        List<Privilege> privileges = privilegeFacadeService.getPrivilegeByAppId(appModule.getId());
        if (CollectionUtils.isNotEmpty(privileges)) {
            for (Privilege p : privileges) {
//                this.exportFutureTreeNodeByDataProvider(p.getUuid(), IexportType.Privilege, node);
                TreeNode child = exportTreeNodeByDataProvider(p.getUuid(), IexportType.Privilege);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        }

        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        return Lists.newArrayList(
                new IExportTable("select * from APP_DATA_TAG where data_id=:id"),
                new IExportTable("select * from APP_TAG t where exists ( select 1 from APP_DATA_TAG d where d.tag_uuid=t.uuid and d.data_id=:id )"));
    }
}
