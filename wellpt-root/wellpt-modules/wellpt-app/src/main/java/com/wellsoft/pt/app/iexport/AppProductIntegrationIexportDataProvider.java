/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.iexport.acceptor.AppProductIntegrationIexportData;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

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
public class AppProductIntegrationIexportDataProvider extends AbstractIexportDataProvider<AppProductIntegration, String> {
    static {
        TableMetaData.register(IexportType.AppProductIntegration, "产品信息集成", AppProductIntegration.class);
    }

    @Autowired
    SecurityApiFacade securityApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppProductIntegration;
    }

    @Override
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        IexportDataRecordSet object = IexportDataResultSetUtils
                .inputStream2IexportDataResultSet(iexportData.getInputStream());
        iexportDataMetaDataService.save((IexportDataRecordSet) object);
        if (!((AppProductIntegration) object.getRawData()).getIsProtected()) {// 非保护资源
            securityApiFacade.addAnonymousResource(((AppProductIntegration) object.getRawData()).getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppProductIntegration appProductIntegration = this.dao.get(AppProductIntegration.class, uuid);
        if (appProductIntegration == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的产品集成依赖关系", "产品集成", uuid);
        }
        return new AppProductIntegrationIexportData(appProductIntegration);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        // 集成信息配置的页面
        DataRelation appPageDefinitioDataRelation = new DataRelation();
        appPageDefinitioDataRelation.setReferencedColumnName("app_page_uuid");
        appPageDefinitioDataRelation.setSourceType(IexportType.AppPageDefinition);
        appPageDefinitioDataRelation.setSourceColumnName(IdEntity.UUID);

        // 集成信息本身数据依赖
        // 系统
        DataRelation appSystemDataRelation = new DataRelation();
        appSystemDataRelation.setReferencedColumnName("data_uuid;data_id");
        appSystemDataRelation.setSourceType(IexportType.AppSystem);
        appSystemDataRelation.setSourceColumnName(IdEntity.UUID + ";id");
        // 模块
        DataRelation appModuleDataRelation = new DataRelation();
        appModuleDataRelation.setReferencedColumnName("data_uuid;data_id");
        appModuleDataRelation.setSourceType(IexportType.AppModule);
        appModuleDataRelation.setSourceColumnName(IdEntity.UUID + ";id");
        // 应用
        DataRelation appApplicationDataRelation = new DataRelation();
        appApplicationDataRelation.setReferencedColumnName("data_uuid;data_id");
        appApplicationDataRelation.setSourceType(IexportType.AppApplication);
        appApplicationDataRelation.setSourceColumnName(IdEntity.UUID + ";id");
        // 功能
        DataRelation appFunctionDataRelation = new DataRelation();
        appFunctionDataRelation.setReferencedColumnName("data_uuid;data_id");
        appFunctionDataRelation.setSourceType(IexportType.AppFunction);
        appFunctionDataRelation.setSourceColumnName(IdEntity.UUID + ";id");

        // 集成信息子结点
        DataRelation appProductIntegrationDataRelation = new DataRelation();
        appProductIntegrationDataRelation.setReferencedColumnName("parent_uuid");
        appProductIntegrationDataRelation.setReferencedType(IexportType.AppProductIntegration);
        appProductIntegrationDataRelation.setSourceColumnName(IdEntity.UUID);
        appProductIntegrationDataRelation.setSourceSelf(true);

        IexportMetaData iexportMetaData = new IexportMetaData();
        List<DataRelation> dataRelations = iexportMetaData.getDataRelations();
        dataRelations.add(appPageDefinitioDataRelation);
        dataRelations.add(appSystemDataRelation);
        dataRelations.add(appModuleDataRelation);
        dataRelations.add(appApplicationDataRelation);
        dataRelations.add(appFunctionDataRelation);
        dataRelations.add(appProductIntegrationDataRelation);

        // 列值处理器
        // iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.AppProductIntegration),
        // "data_path", new ColumnValueProcessor() {
        //
        // @Override
        // public Object doProcess(Object sourceValue, IexportDataRecord dataRecord,
        // IexportData iexportData,
        // IexportBundle iexportBundle) {
        // IexportMappingColumn newMappingColumn = new
        // IexportMappingColumn(dataRecord.getTableName(),
        // "data_id", dataRecord.getDataColumn("data_id").getValue());
        // IexportMappingColumn oldMappingColumn =
        // iexportBundle.getMappingColumn(newMappingColumn);
        // if (oldMappingColumn != null) {
        // // 替换data_path最后的
        // return StringUtils.replace(ObjectUtils.toString(sourceValue,
        // StringUtils.EMPTY), "/"
        // + oldMappingColumn.getValue(), "/" + newMappingColumn.getValue());
        // }
        // return sourceValue;
        // }
        //
        // });
        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppProductIntegration appProductIntegration) {
        return new AppProductIntegrationIexportData(appProductIntegration).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(AppProductIntegration appProductIntegration,
                                           Map<String, AppProductIntegration> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + appProductIntegration.getUuid();
        parentMap.put(key, appProductIntegration);
        if (StringUtils.isNotBlank(appProductIntegration.getAppPageUuid())) {
            String key1 = start + appProductIntegration.getAppPageUuid();
            parentMap.put(key1, appProductIntegration);
        }
        if (StringUtils.isNotBlank(appProductIntegration.getDataUuid())) {
            String key2 = start + appProductIntegration.getDataUuid();
            parentMap.put(key2, appProductIntegration);
        }

        String appPageUuid = appProductIntegration.getAppPageUuid();
        String dataUuid = appProductIntegration.getDataUuid();
        Integer appType = Integer.valueOf(appProductIntegration.getDataType());
        String uuid = appProductIntegration.getUuid();

        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppPageDefinition))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "AppPageDefinition");
            hqlMap.put(this.getChildHqlKey(IexportType.AppPageDefinition), protoDataHql);
        }
        if (StringUtils.isNotBlank(appPageUuid)) {
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppPageDefinition)), appPageUuid);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppPageDefinition)), uuid, "appPiUuids");

        // 集成信息本身数据依赖
        if (AppType.SYSTEM.equals(appType)) {
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppSystem))) {
                hqlMap.put(this.getChildHqlKey(IexportType.AppSystem), this.getProtoDataHql("AppSystem"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppSystem)), dataUuid);
        }
        if (AppType.MODULE.equals(appType)) {
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppModule))) {
                hqlMap.put(this.getChildHqlKey(IexportType.AppModule), this.getProtoDataHql("AppModule"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppModule)), dataUuid);
        }
        if (AppType.APPLICATION.equals(appType)) {
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppApplication))) {
                hqlMap.put(this.getChildHqlKey(IexportType.AppApplication), this.getProtoDataHql("AppApplication"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppApplication)), dataUuid);
        }
        if (AppType.FUNCTION.equals(appType)) {
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppFunction))) {
                hqlMap.put(this.getChildHqlKey(IexportType.AppFunction), this.getProtoDataHql("AppFunction"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppFunction)), dataUuid);
        }
        // 集成信息子结点
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.AppProductIntegration))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "AppProductIntegration");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    HqlUtils.appendSql("parentUuid", protoDataHql.getParams(), protoDataHql.getSbHql(),
                            (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                    protoDataHql.getSbHql().append(" order by sortOrder");
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.AppProductIntegration), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.AppProductIntegration)), uuid);
    }

    @Override
    public Map<String, List<AppProductIntegration>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppProductIntegration> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                AppProductIntegration.class);
        Map<String, List<AppProductIntegration>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProduct)) {
            for (AppProductIntegration appProductIntegration : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + appProductIntegration.getAppProductUuid();
                this.putParentMap(map, appProductIntegration, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            for (AppProductIntegration appProductIntegration : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + appProductIntegration.getParentUuid();
                this.putParentMap(map, appProductIntegration, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

    @Override
    public <P extends JpaEntity<String>, C extends JpaEntity<String>> BusinessProcessor<AppProductIntegration> saveOrUpdate(
            Map<String, ProtoDataBeanTree<AppProductIntegration, P, C>> map, Collection<Serializable> uuids) {
        List<AppProductIntegration> oldList = this.getList(uuids);
        List<AppProductIntegration> list = new ArrayList<>();
        for (AppProductIntegration old : oldList) {
            ProtoDataBeanTree<AppProductIntegration, P, C> t = map.get(old.getUuid());
            // 版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = this.entityToUpdateSql(t.getProtoDataBean().getData());
                if (!t.getProtoDataBean().getData().getIsProtected()) {
                    list.add(t.getProtoDataBean().getData());
                }
                this.executeUpdateSql(sql, t);
            }
            map.remove(old.getUuid());
        }
        // 剩余的添加
        for (ProtoDataBeanTree<AppProductIntegration, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            if (!t.getProtoDataBean().getData().getIsProtected()) {
                list.add(t.getProtoDataBean().getData());
            }
            this.executeUpdateSql(sql, t);
        }
        BusinessProcessor<AppProductIntegration> businessProcessor = new BusinessProcessor<AppProductIntegration>(
                list) {
            @Override
            public void handle(Map<String, ProtoDataBean> beanMap) {
                for (AppProductIntegration appProductIntegration : this.getAddList()) {
                    if (!appProductIntegration.getIsProtected()) {// 非保护资源
                        securityApiFacade.addAnonymousResource(appProductIntegration.getUuid());
                    }
                }
            }
        };
        return businessProcessor;
    }
}
