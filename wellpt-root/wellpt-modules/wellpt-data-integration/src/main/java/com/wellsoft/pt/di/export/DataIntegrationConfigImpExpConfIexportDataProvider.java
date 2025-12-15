package com.wellsoft.pt.di.export;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.task.iexport.acceptor.JobDetailsIexportData;
import com.wellsoft.pt.task.iexport.provider.JobDetailsIexportDataProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/26    chenq		2018/9/26		Create
 * </pre>
 */
@Service
public class DataIntegrationConfigImpExpConfIexportDataProvider extends
        AbstractIexportDataProvider<DiConfigEntity, String> {

    static {
        TableMetaData.register(IexportType.DataIntegrationConfImpExp, "数据交换导入导出配置",
                DiConfigEntity.class);
    }

    @Autowired
    DiConfigService diConfigService;

    @Autowired
    MongoFileService mongoFileService;

    @Autowired
    JobDetailsIexportDataProvider jobDetailsIexportDataProvider;

    @Override
    public String getType() {
        return IexportType.DataExchangeImpExpConf;
    }

    @Override
    @Transactional
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        Map<String, Object> dataMap = (Map<String, Object>) object;
        IexportDataRecordSet printIexportData = (IexportDataRecordSet) dataMap
                .get(IexportDataResultSetUtils.ENTITY_BEAN);
        iexportDataMetaDataService.save(printIexportData);

        List<IexportData> depIexportDataList = iexportData.getDependencies();
        if (CollectionUtils.isNotEmpty(depIexportDataList)) {
            //导入管理的任务配置
            for (IexportData depIexp : depIexportDataList) {
                if (depIexp instanceof JobDetailsIexportData) {
                    jobDetailsIexportDataProvider.storeData(depIexp, true);
                }
            }
        }


    }

    @Override
    public IexportData getData(String uuid) {
        DiConfigEntity entity = diConfigService.getOne(uuid);
        return new DataIntegrationConfigImpExpConfIexportData(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 交换配置ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.DataIntegrationConfImpExp), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(
                        DiConfigEntity.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(DiConfigEntity diConfigEntity) {
        return new DataIntegrationConfigImpExpConfIexportData(diConfigEntity).getName();
    }

    @Override
    public Set<String> getFileIds(DiConfigEntity diConfigEntity) {
        diConfigEntity.setIsEnable(false);//导出去，默认不启动
        return super.getFileIds(diConfigEntity);
    }

    @Override
    public void putChildProtoDataHqlParams(DiConfigEntity diConfigEntity, Map<String, DiConfigEntity> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (StringUtils.isNotBlank(diConfigEntity.getJobUuid())) {
            parentMap.put(start + diConfigEntity.getJobUuid(), diConfigEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.JobDetails))) {
                hqlMap.put(this.getChildHqlKey(IexportType.JobDetails), this.getProtoDataHql("JobDetails"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.JobDetails)), diConfigEntity.getJobUuid());
        }
    }


    @Override
    public Map<String, List<DiConfigEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DiConfigEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DiConfigEntity.class);
        Map<String, List<DiConfigEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的数据交换配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DiConfigEntity diConfigEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, diConfigEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DiConfigEntity diConfigEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + diConfigEntity.getUuid();
                this.putParentMap(map, diConfigEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
