package com.wellsoft.pt.timer.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.iexport.acceptor.TsTimerConfigIexportData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:流程计时器定义
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2023-5-20	liuyz		2023-5-20		Create
 * </pre>
 * @date 2023-5-20
 */
@Service
@Transactional(readOnly = true)
public class TsTimerConfigIexportDataProvider extends AbstractIexportDataProvider<TsTimerConfigEntity, String> {

    static {
        // 流程计时器配置
        TableMetaData.register(IexportType.TsTimerConfig, "流程计时器配置", TsTimerConfigEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.TsTimerConfig;
    }

    @Override
    public IexportData getData(String uuid) {
        TsTimerConfigEntity tsTimerConfigEntity = this.dao.get(TsTimerConfigEntity.class, uuid);
        if (tsTimerConfigEntity == null) {
            return new ErrorDataIexportData(IexportType.TsTimerConfig, "找不到对应的流程计时器配置,可能已经被删除", "流程计时器配置", uuid);
        }
        return new TsTimerConfigIexportData(tsTimerConfigEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 计时器ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.TsTimerConfig), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(TsTimerConfigEntity.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(TsTimerConfigEntity tsTimerConfigEntity) {
        return new TsTimerConfigIexportData(tsTimerConfigEntity).getName();
    }

    @Override
    public Map<String, List<TsTimerConfigEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<TsTimerConfigEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), TsTimerConfigEntity.class);
        Map<String, List<TsTimerConfigEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的流程分类
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (TsTimerConfigEntity entity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, entity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (TsTimerConfigEntity entity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + IexportType.TsTimerConfig + Separator.UNDERLINE.getValue() + entity.getUuid();
                this.putParentMap(map, entity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (TsTimerConfigEntity entity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + entity.getUuid();
                this.putParentMap(map, entity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }

        return map;
    }

}
