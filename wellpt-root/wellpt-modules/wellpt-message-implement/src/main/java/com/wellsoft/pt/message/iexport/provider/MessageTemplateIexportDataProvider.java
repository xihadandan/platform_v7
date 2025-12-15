/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.iexport.acceptor.MessageTemplateIexportData;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class MessageTemplateIexportDataProvider extends AbstractIexportDataProvider<MessageTemplate, String> {
    static {
        TableMetaData.register(IexportType.MessageTemplate, "消息模板", MessageTemplate.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.MessageTemplate;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        MessageTemplate messageTemplate = this.dao.get(MessageTemplate.class, uuid);
        if (messageTemplate == null) {
            return new ErrorDataIexportData(IexportType.MessageTemplate, "找不到对应的消息模板依赖关系,可能已经被删除", "消息模板", uuid);
        }
        return new MessageTemplateIexportData(messageTemplate);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 消息模板ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.MessageTemplate), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(MessageTemplate.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(MessageTemplate messageTemplate) {
        return new MessageTemplateIexportData(messageTemplate).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(MessageTemplate messageTemplate, Map<String, MessageTemplate> parentMap, Map<String, ProtoDataHql> hqlMap) {
        if (StringUtils.isNotBlank(messageTemplate.getClassifyUuid())) {
            String start = getType() + Separator.UNDERLINE.getValue();
            String key = start + messageTemplate.getClassifyUuid();
            parentMap.put(key, messageTemplate);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.MessageClassify))) {
                hqlMap.put(this.getChildHqlKey(IexportType.MessageClassify), this.getProtoDataHql("MessageClassify"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.MessageClassify)), messageTemplate.getClassifyUuid());
        }

        if (StringUtils.isNotBlank(messageTemplate.getModuleId())) {
            this.putAppFunctionParentMap(messageTemplate, parentMap, hqlMap);
        }
    }


    @Override
    public Map<String, List<MessageTemplate>> getParentMapList(ProtoDataHql protoDataHql) {
        List<MessageTemplate> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), MessageTemplate.class);
        Map<String, List<MessageTemplate>> map = new HashMap<>();
        // 页面或组件定义依赖的消息模板配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (MessageTemplate messageTemplate : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, messageTemplate, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (MessageTemplate messageTemplate : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "messageTemplate" + Separator.UNDERLINE.getValue() + messageTemplate.getId();
                this.putParentMap(map, messageTemplate, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (MessageTemplate messageTemplate : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + messageTemplate.getUuid();
                this.putParentMap(map, messageTemplate, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
