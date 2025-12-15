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
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.iexport.acceptor.AppPageDefinitionIexportData;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.ReadonlyWidgetDefinitionView;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElementDependenciesConfiguration;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class AppPageDefinitionIexportDataProvider extends AbstractIexportDataProvider<AppPageDefinition, String> {
    static {
        TableMetaData.register(IexportType.AppPageDefinition, "页面", AppPageDefinition.class);
    }

    Pattern pattern = Pattern.compile("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=[0-9]+");

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private AppPageDefinitionService appPageDefinitionService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;
    @Autowired
    private AppFunctionService appFunctionService;

    @Autowired
    private AppPageResourceService appPageResourceService;

    @Autowired
    private AppDataDefinitionRefResourceService appDataDefinitionRefResourceService;

    @Autowired
    AppProductService appProductService;

    @Autowired
    AppModuleService appModuleService;

    @Autowired
    AppProdVersionService appProdVersionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppPageDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppPageDefinition appPageDefinition = this.dao.get(AppPageDefinition.class, uuid);
        if (appPageDefinition == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的页面依赖关系", "页面定义", uuid);
        }
        return new AppPageDefinitionIexportData(appPageDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        // 页面组件定义
        DataRelation appWidgetDefinitionDataRelation = new DataRelation();
        appWidgetDefinitionDataRelation.setSourceColumnName(IdEntity.UUID);
        appWidgetDefinitionDataRelation.setReferencedType(IexportType.AppWidgetDefinition);
        appWidgetDefinitionDataRelation.setReferencedColumnName("app_page_uuid");
        appWidgetDefinitionDataRelation.setSourceSelf(true);

        IexportMetaData iexportMetaData = new IexportMetaData();
        List<DataRelation> dataRelations = iexportMetaData.getDataRelations();
        dataRelations.add(appWidgetDefinitionDataRelation);

        // 页面ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppPageDefinition), "id",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord,
                                            IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        String uuid = dataRecord.getString("uuid");
                        String wtype = dataRecord.getString("wtype");
                        String widgetId = getWidgetIdOfPage(wtype, iexportData.getChildren());
                        return uuid + Separator.UNDERLINE.getValue() + widgetId;
                    }

                    private String getWidgetIdOfPage(String wtype, List<IexportData> children) {
                        List<String> pageWidgetIds = Lists.newArrayList();
                        for (IexportData child : children) {
                            IexportDataRecord childDataRecord = child.getRecord();
                            if (StringUtils.equals(childDataRecord.getString("wtype"), wtype)) {
                                pageWidgetIds.add(childDataRecord.getString("id"));
                            }
                        }
                        if (pageWidgetIds.size() != 1) {
                            throw new RuntimeException("页面定义无法找到自身的组件定义！");
                        }
                        return pageWidgetIds.get(0);
                    }

                });

        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppPageDefinition appPageDefinition) {
        if (appProdVersionService.isProdVersionPage(appPageDefinition.getUuid())) {
            return "系统首页: " + appPageDefinition.getName() + "(" + appPageDefinition.getVersion() + ")";
        } else {
            AppModule appModule = appModuleService.getById(appPageDefinition.getAppId());
            if (appModule != null && BooleanUtils.isTrue(appPageDefinition.getIsDefault())) {
                return "模块主页: " + appPageDefinition.getName() + "(" + appPageDefinition.getVersion() + ")";
            }
            return new AppPageDefinitionIexportData(appPageDefinition).getName();
        }
    }


    @Override
    @Deprecated
    public void putChildProtoDataHqlParams(AppPageDefinition appPageDefinition, Map<String, AppPageDefinition> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String uuid = appPageDefinition.getUuid();
        String key = start + uuid;
        parentMap.put(key, appPageDefinition);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppWidgetDefinition))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "AppWidgetDefinition");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    HqlUtils.appendSql("appPageUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                    protoDataHql.getSbHql().append(" order by createTime ");
                    protoDataHql.setComparator(new Comparator<AppWidgetDefinition>() {
                        @Override
                        public int compare(AppWidgetDefinition o1, AppWidgetDefinition o2) {
                            if (StringUtils.contains(o1.getDefinitionJson(), o2.getDefinitionJson())) {
                                return 1;
                            } else if (StringUtils.contains(o2.getDefinitionJson(), o1.getDefinitionJson())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.AppWidgetDefinition), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppWidgetDefinition)), uuid);

        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppPageResource))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "AppPageResourceEntity");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("select a from AppPageResourceEntity a,AppFunction b ").append(" where a.appFunctionUuid = b.uuid and ");
                    HqlUtils.appendSql("a.appPageUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.AppPageResource), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppPageResource)), uuid);
        if (StringUtils.isNotBlank(appPageDefinition.getDefinitionJson())) {
            // 页面定义依赖的元素
            Widget widget = WidgetDefinitionUtils.parseWidget(appPageDefinition.getDefinitionJson(), ReadonlyWidgetDefinitionView.class);
            FunctionElementDependenciesConfiguration dependenciesConfiguration = widget.getConfiguration(FunctionElementDependenciesConfiguration.class);
            List<FunctionElement> dependenciesFunctionElements = dependenciesConfiguration.getDependencies();
            Map<String, List<FunctionElement>> exportTypeMap = ListUtils.list2group(dependenciesFunctionElements, "exportType");
            for (Map.Entry<String, List<FunctionElement>> entry : exportTypeMap.entrySet()) {
                List<FunctionElement> functionElements = entry.getValue();
                for (FunctionElement functionElement : functionElements) {
                    String type = functionElement.getExportType();
                    String childHqlkey = this.getChildHqlKey(type);
                    ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
                    if (protoDataHql == null) {
                        String entityName = IexportType.exportType2EntityName(type);
                        protoDataHql = new ProtoDataHql(getType(), entityName);
                        protoDataHql.setGenerateHql(new GenerateHql() {
                            @Override
                            public void build(ProtoDataHql protoDataHql) {
                                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                                HqlUtils.appendSql("uuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                            }
                        });
                        hqlMap.put(childHqlkey, protoDataHql);
                        protoDataHql.getParams().put("dependencyUuid", uuid);
                    }
                    this.addDataUuid(hqlMap.get(childHqlkey), functionElement.getUuid());
                }
            }
        }
    }

    @Override
    @Deprecated
    public Map<String, List<AppPageDefinition>> getParentMapList(ProtoDataHql protoDataHql) {

        Map<String, List<AppPageDefinition>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
            HqlUtils.appendSql("appPiUuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("appPiUuids"));
            List<AppPageDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppPageDefinition.class);
            for (AppPageDefinition appPageDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appPageDefinition.getAppPiUuid();
                this.putParentMap(map, appPageDefinition, key);
            }
            if (protoDataHql.getParams().get("uuids") != null) {
                protoDataHql.setSbHql(new StringBuilder());
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("uuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                List<AppPageDefinition> list2 = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppPageDefinition.class);
                for (AppPageDefinition appPageDefinition : list2) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appPageDefinition.getUuid();
                    this.putParentMap(map, appPageDefinition, key);
                }
            }

        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }


    public TreeNode treeNode(String uuid) {
        AppPageDefinition pageDefinition = appPageDefinitionService.getOne(uuid);
        if (pageDefinition == null || !ExportTreeContextHolder.add(getType() + pageDefinition.getId())) {
            // 避免导出多个版本的页面
            return null;
        }
        TreeNode node = new TreeNode();
        node.setId(uuid);
        Map<String, Object> param = Maps.newHashMap();
        node.setType(getType());
        node.setName(getTreeName(pageDefinition));
        // 导出页面上依赖的导出功能元素
        Map<String, Object> hqlParams = Maps.newHashMap();
        hqlParams.put("uuid", uuid);
        List<AppFunction> appFunctions = appFunctionService.listByHQL("from AppFunction a where  a.exportable = true and a.exportType is not null and a.exportType <> 'appWidgetDefinition' and exists (" +
                " select 1 from AppPageResourceEntity r where r.appPageUuid=:uuid and r.appFunctionUuid =a.uuid " +
                ")", hqlParams);
        if (CollectionUtils.isNotEmpty(appFunctions)) {
            for (AppFunction func : appFunctions) {
                if (StringUtils.isNotBlank(func.getExportType())) {
                    this.exportTreeNodeByDataProvider(func.getUuid(),func.getExportType(),node);
//                    TreeNode child = this.exportTreeNodeByDataProvider(func.getUuid(), func.getExportType());
//                    node.appendChild(child);
                }
            }
        }
        // 解析定义中涉及的附件文件
        if (StringUtils.isNotBlank(pageDefinition.getDefinitionJson())) {
            Matcher matcher = pattern.matcher(pageDefinition.getDefinitionJson());
            while (matcher.find()) {
                this.exportTreeNodeByDataProvider(matcher.group()
                        .replaceFirst("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=", ""), IexportType.LogicFileInfo,node);
//                node.appendChild(this.exportTreeNodeByDataProvider(matcher.group()
//                        .replaceFirst("/proxy-repository/repository/file/mongo/(download|downloadVideoSegment)\\?fileID=", ""), IexportType.LogicFileInfo));
            }
        }


        return node;
    }

    @Override
    protected List<IExportTable> childTableStream() {
        List<IExportTable> tables = Lists.newArrayList(
                /* 页面资源 */
                new IExportTable("select * from APP_PAGE_RESOURCE r where r.app_page_uuid=:uuid"),
                /* 页面可导出的功能 */
                new IExportTable("select * from app_function a where exists (" +
                        " select 1 from APP_PAGE_RESOURCE r where r.app_page_uuid=:uuid and r.app_function_uuid =a.uuid " +
                        ")"),
                /* 组件定义 */
                new IExportTable("select * from APP_WIDGET_DEFINITION a where a.app_page_uuid=:uuid"),
                /* 页面与产品的关系 */
                new IExportTable("select * from APP_PROD_RELA_PAGE where page_uuid=:uuid"),
                /* 国际化语言定义 */
                new IExportTable("select * from APP_DEF_ELEMENT_I18N where def_id=:id and apply_to='" + IexportType.AppPageDefinition + "'"),
                /* 资源组 */
                new IExportTable("select * from app_module_res_group g where exists ( select 1 from app_module_res_group_member m where m.member_uuid=:uuid and m.group_uuid = g.uuid  ) "),
                new IExportTable("select * from app_module_res_group_member m where m.member_uuid=:uuid ")
        );
        return tables;
    }


//    @Override
//    public <P extends IdEntity, C extends IdEntity> BusinessProcessor<AppPageDefinition> saveOrUpdate(Map<String, ProtoDataBeanTree<AppPageDefinition, P, C>> map, Collection<String> uuids) {
//        List<AppPageDefinition> oldList = this.getList(uuids);
//        List<AppPageDefinition> list = new ArrayList<>();
//        for (AppPageDefinition old : oldList) {
//            ProtoDataBeanTree<AppPageDefinition, P, C> t = map.get(old.getUuid());
//            //版本号不一致 修改
//            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
//                String sql = super.entityToUpdateSql(t.getProtoDataBean().getData());
//                if (Boolean.TRUE.equals(t.getProtoDataBean().getData().getIsDefault())) {
//                    list.add(t.getProtoDataBean().getData());
//                }
//                super.executeUpdateSql(sql, t);
//            }
//            map.remove(old.getUuid());
//        }
//        //剩余的添加
//        for (ProtoDataBeanTree<AppPageDefinition, P, C> t : map.values()) {
//            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
//            if (Boolean.TRUE.equals(t.getProtoDataBean().getData().getIsDefault())) {
//                list.add(t.getProtoDataBean().getData());
//            }
//            this.executeUpdateSql(sql, t);
//        }
//
//        BusinessProcessor<AppPageDefinition> businessProcessor = new BusinessProcessor<AppPageDefinition>(list) {
//            @Override
//            public void handle(Map<String, ProtoDataBean> beanMap) {
//                for (AppPageDefinition appPageDefinition : this.getList()) {
//                    if (BooleanUtils.isTrue(appPageDefinition.getIsDefault())) {
//                        AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPageDefinition.getAppPiUuid());
//                        if (appProductIntegration != null && appProductIntegration.getDataType().equals(AppType.SYSTEM.toString())) {
//                            //默认的，则重置其他未非默认的
//                            appPageDefinitionService.updateDefaultTrueByUuid(appPageDefinition.getAppPiUuid(), appPageDefinition.getUuid());
//                            //生成默认工作台权限角色等
//                            privilegeFacadeService.saveAppPageDef(appPageDefinition.getIsDefault(), appPageDefinition.getUuid(), appPageDefinition.getAppPiUuid());
//                        }
//                    }
//                }
//            }
//        };
//        return businessProcessor;
//    }

}
