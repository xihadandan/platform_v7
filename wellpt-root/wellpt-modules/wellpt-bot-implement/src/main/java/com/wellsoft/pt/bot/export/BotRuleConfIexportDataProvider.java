/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bot.export;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.bot.entity.BotRuleConfEntity;
import com.wellsoft.pt.bot.service.BotRuleConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 消息模板
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-19.1	linz		2016-1-19		Create
 * </pre>
 * @date 2016-1-19
 */
@Service
public class BotRuleConfIexportDataProvider extends AbstractIexportDataProvider<BotRuleConfEntity, String> {
    static {
        TableMetaData.register(IexportType.BotRuleConf, "单据转换规则导出", BotRuleConfEntity.class);
    }

    @Autowired
    BotRuleConfService botRuleConfService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.BotRuleConf;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        BotRuleConfEntity confEntity = botRuleConfService.getOne(uuid);
        if (confEntity == null) {
            return new ErrorDataIexportData(IexportType.BotRuleConf, "找不到对应的单据转换规则,可能已经被删除",
                    "消息模板", uuid);
        }
        return new BotRuleConfIexportData(confEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 组件ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.BotRuleConf), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(BotRuleConfEntity.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(BotRuleConfEntity botRuleConfEntity) {
        return new BotRuleConfIexportData(botRuleConfEntity).getName();
    }

    @Override
    public Map<String, List<BotRuleConfEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<BotRuleConfEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), BotRuleConfEntity.class);
        Map<String, List<BotRuleConfEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的单据转换规则
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (BotRuleConfEntity botRuleConfEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, botRuleConfEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (BotRuleConfEntity botRuleConfEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + botRuleConfEntity.getUuid();
                this.putParentMap(map, botRuleConfEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
