/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.iexport.acceptor.AppFunctionIexportData;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.cfg.DataRelation;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
public class AppFunctionIexportDataProvider extends AbstractIexportDataProvider<AppFunction, String> {
    static {
        TableMetaData.register(IexportType.AppFunction, "功能", AppFunction.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppFunction;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppFunction appFunction = this.dao.get(AppFunction.class, uuid);
        if (appFunction == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的功能依赖关系", "功能定义", uuid);
        }
        return new AppFunctionIexportData(appFunction);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        // 集成信息配置的页面
        // UUID
        DataRelation anyTypeFunctionDataRelation = new DataRelation();
        anyTypeFunctionDataRelation.setReferencedColumnName(IdEntity.UUID);
        // 数据来源类型为任意类型
        anyTypeFunctionDataRelation.setSourceType(Separator.ASTERISK.getValue());
        anyTypeFunctionDataRelation.setSourceColumnName(IdEntity.UUID);

        IexportMetaData iexportMetaData = new IexportMetaData();
        List<DataRelation> dataRelations = iexportMetaData.getDataRelations();
        dataRelations.add(anyTypeFunctionDataRelation);

        // 功能ID列值处理器
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppFunction), "id",
                new AppFunctionIdColumnValueProcessor());

        // 设置数据可复制性检测器，可能不可复制
        iexportMetaData.setRecordReplicableChecker(new IexportDataRecordReplicableChecker() {

            @Override
            public boolean checkReplicable(IexportDataRecord iexportDataRecord) {
                return false;
            }

        });
        //		// 功能UUID列不可变更
        //		List<String> immutableColumns = Lists.newArrayList();
        //		immutableColumns.add(IdEntity.UUID);
        //		iexportMetaData.getImmutableColumnMap().put(TableMetaData.getTableName(IexportType.AppFunction),
        //				immutableColumns);
        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppFunction appFunction) {
        return new AppFunctionIexportData(appFunction).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(AppFunction appFunction, Map<String, AppFunction> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + appFunction.getUuid();
        parentMap.put(key, appFunction);

        String uuid = appFunction.getUuid();
        String type = appFunction.getExportType();
        if (Boolean.TRUE.equals(appFunction.getExportable())) {
            if (IexportType.hasDependencyParentType(type)) {
                type = IexportType.getDependencyParentType(type);
            }
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
            }
            Set<String> uuids = (Set<String>) protoDataHql.getParams().get("uuids");
            if (uuids == null) {
                uuids = new HashSet<>();
                protoDataHql.getParams().put("uuids", uuids);
            }
            uuids.add(uuid);
        }
    }

    @Override
    public Map<String, List<AppFunction>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppFunction> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppFunction.class);
        Map<String, List<AppFunction>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppProductIntegration)) {
            for (AppFunction appFunction : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appFunction.getUuid();
                this.putParentMap(map, appFunction, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppPageResource)) {
            for (AppFunction appFunction : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appFunction.getUuid();
                this.putParentMap(map, appFunction, key);
            }
        } else {
            super.getParentMapList(protoDataHql);

        }
        return map;
    }

    // 功能ID列值处理器
    private class AppFunctionIdColumnValueProcessor implements ColumnValueProcessor {

        /**
         * (non-Javadoc)
         *
         * @see ColumnValueProcessor#doProcess(Object, IexportDataRecord, IexportData, IexportBundle)
         */
        @Override
        public Object doProcess(Object sourceValue, IexportDataRecord dataRecord,
                                IexportData iexportData,
                                IexportBundle iexportBundle) {
            // 功能类型
            String functionUuid = dataRecord.getString(IdEntity.UUID);
            String exportType = dataRecord.getString("export_type");
            IexportDataRecord functionDataRecord = getFunctionDataRecord(functionUuid, exportType,
                    iexportData.getChildren(), iexportBundle);
            String functionId = null;
            if (functionDataRecord != null) {
                switch (exportType) {
                    // 权限
                    case AppFunctionType.Privilege:
                        functionId = functionDataRecord.getString("code");
                        break;
                    case AppFunctionType.Resource:
                        // 资源
                    case AppFunctionType.MENU:
                        // 资源菜单
                    case AppFunctionType.BUTTON:
                        // 资源按钮
                        functionId = functionDataRecord.getString("code");
                        break;
                    case AppFunctionType.DataDictionary:
                        // 数据字典
                        functionId = functionDataRecord.getString("code");
                        break;
                    case AppFunctionType.PrintTemplate:
                        // 打印模板
                        functionId = functionDataRecord.getString(
                                "id") + "_v" + functionDataRecord.getString("version");
                        break;
                    case AppFunctionType.JobDetails:
                        // 任务数据
                        functionId = functionDataRecord.getString("id") + "_"
                                + functionDataRecord.getString("job_class_name");
                        break;
                    case AppFunctionType.FlowDefinition:
                        // 流程定义
                        functionId = functionDataRecord.getString(
                                "id") + "_" + functionDataRecord.getString("version");
                        break;
                    case AppFunctionType.FlowCategory:
                        // 流程分类
                        functionId = functionDataRecord.getString("code");
                    case IexportType.DmsFolder:
                        // 数据管理文件夹
                        functionId = functionDataRecord.getString(IdEntity.UUID);
                        break;
                    default:
                        functionId = functionDataRecord.getString("id");
                        break;
                }
            } else {
                // Web控制器、门面服务、JavaScript模块、JavaScript模板、CssFile模块、DmsAction数据管理操作等没有具体功能数据的ID取原功能ID本身
                functionId = dataRecord.getString("id");
            }
            if (StringUtils.isBlank(functionId)) {
                functionId = functionUuid;
            }
            return functionId;
        }

        /**
         * @param functionUuid
         * @param functionType
         * @param iexportDatas
         * @return
         */
        private IexportDataRecord getFunctionDataRecord(String functionUuid, String functionType,
                                                        List<IexportData> iexportDatas,
                                                        IexportBundle iexportBundle) {
            for (IexportData iexportData : iexportDatas) {
                if (!StringUtils.equals(iexportData.getType(), functionType)) {
                    continue;
                }
                IexportDataRecord record = iexportData.getRecord();
                IexportDataColumn dataColumn = record.getDataColumn(IdEntity.UUID);
                if (StringUtils.equals(ObjectUtils.toString(dataColumn.getValue()), functionUuid)) {
                    return record;
                } else {
                    IexportMappingColumn oldMappingColumn = iexportBundle.getMappingColumn(
                            record.getTableName(),
                            dataColumn);
                    if (oldMappingColumn != null
                            && StringUtils.equals(ObjectUtils.toString(oldMappingColumn.getValue()),
                            functionUuid)) {
                        return record;
                    }
                }
            }
            return null;
        }

    }
}
