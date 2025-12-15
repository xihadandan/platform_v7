/*
 * @(#)2020年1月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.manager.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月3日.1	wangrf		2020年1月3日		Create
 * </pre>
 * @date 2020年1月3日
 */
@Component
public class MessageQueueMonitorDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_消息管理_消息队列监控";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        // 返回字段四个
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();

        metadata.add("name", "name", "消息模板名称", String.class);
        metadata.add("templateId", "template_id", "消息模板ID", String.class);
        // 四个表中的字段名称不统一，sendTime/sentTime
        metadata.add("sendTime", "send_time", "发送时间", Date.class);

        // messageQueue/messageQueueHis专有
        metadata.add("code", "code", "编号", String.class);
        metadata.add("correlationUuid", "correlation_uuid", "消息回复时的相关联消息UUID", String.class);
        // scheduleMessageQueueEntity专有
        metadata.add("businessId", "business_id", "业务id，用于撤销定时", String.class);

        metadata.add("systemUnitId", "system_unit_id", "归属组织ID", String.class);
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("recVer", "rec_ver", "版本号", Integer.class);
        metadata.add("creator", "creator", "创建人", String.class);
        metadata.add("createTime", "create_time", "创建时间", Date.class);
        metadata.add("modifier", "modifier", "修改人", String.class);
        metadata.add("modifyTime", "modify_time", "修改时间", Date.class);
        metadata.add("attach", "attach", "附件属性", String.class);
        return metadata;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * 如何描述该方法
     * <p>
     * 参数列表说明
     * name：按照like进行查询
     * queryType：查询数据类型
     * messageQueue
     * messageQueueHis
     * scheduleMessageQueueEntity
     * scheduleMessageQueueHisEntity
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        // 主要写的逻辑代码
        Map<String, Object> params = getParams(context.getQueryParams(), context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("messageQueueQuery", params, QueryItem.class,
                context.getPagingInfo());
        // dealwith blob字段
        for (QueryItem item : queryItems) {
            // 移除message字段
            item.remove("message");
        }
        return queryItems;
    }

    private Map<String, Object> getParams(Map<String, Object> raws, QueryContext context) {
        Map<String, Object> params = new HashMap<String, Object>();
        String queryType = (String) raws.get("queryType");
        if (StringUtils.isEmpty(queryType)) {
            // 默认为 即时消息-待处理队列
            queryType = "messageQueue";
        }
        if (queryType.equals("messageQueue")) {
            params.put("table", "MSG_MESSAGE_QUEUE");
        } else if (queryType.equals("messageQueueHis")) {
            params.put("table", "MSG_HIS_MESSAGE_QUEUE");
        } else if (queryType.equals("scheduleMessageQueueEntity")) {
            params.put("table", "MSG_SCHEDULE_MESSAGE_QUEUE");
        } else if (queryType.equals("scheduleMessageQueueHisEntity")) {
            params.put("table", "MSG_SCHEDULE_MESSAGE_QUEUE_HIS");
        } else {
            throw new RuntimeException("查询类型错误,请核对queryType字段");
        }
        // 根据name进行过滤查询
        if (StringUtils.isNotBlank((String) raws.get("name"))) {
            params.put("name", raws.get("name").toString());
        }
        String orderBy = context.getOrderString();
        if ("messageQueue".equals(queryType) || "messageQueueHis".equals(queryType)) {
            if (StringUtils.isNotBlank(orderBy) && orderBy.contains("send_time")) {
                orderBy = orderBy.replace("send_time", "sent_time");
            }
            params.put("isNeedAlais", "true");
        }
        params.put("orderBy", orderBy);

        return params;
    }

}
