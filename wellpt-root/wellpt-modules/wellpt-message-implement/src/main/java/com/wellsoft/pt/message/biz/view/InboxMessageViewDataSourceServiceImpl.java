/*
 * @(#)2014-10-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.biz.view;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
 * 2014-10-12.1	zhulh		2014-10-12		Create
 * </pre>
 * @date 2014-10-12
 */
@Component
public class InboxMessageViewDataSourceServiceImpl extends AbstractDataSourceProvider {

    @Autowired
    private MessageInboxService MessageInboxService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return "MESSAGE";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "我的消息--收件箱";
    }

    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> dataSourceColumns = new ArrayList<DataSourceColumn>();
        // 消息UUID
        DataSourceColumn uuid = new DataSourceColumn();

        uuid.setFieldName("uuid");
        uuid.setColumnName("uuid");
        uuid.setColumnAliase("uuid");
        uuid.setTitleName("消息UUID");
        dataSourceColumns.add(uuid);

        // 发送人ID
        DataSourceColumn sender = new DataSourceColumn();

        sender.setFieldName("sender");
        sender.setColumnName("sender");
        sender.setColumnAliase("sender");
        sender.setTitleName("发送人ID");
        dataSourceColumns.add(sender);

        // 接收人ID
        DataSourceColumn recipient = new DataSourceColumn();

        recipient.setFieldName("recipient");
        recipient.setColumnName("recipient");
        recipient.setColumnAliase("recipient");
        recipient.setTitleName("接收人ID");
        dataSourceColumns.add(recipient);

        // 发送人名称
        DataSourceColumn senderName = new DataSourceColumn();
        senderName.setFieldName("senderName");
        senderName.setColumnName("senderName");
        senderName.setColumnAliase("senderName");
        senderName.setTitleName("发送人名称");
        dataSourceColumns.add(senderName);
        // 接收人名称
        DataSourceColumn recipientName = new DataSourceColumn();
        recipientName.setFieldName("recipientName");
        recipientName.setColumnName("recipientName");
        recipientName.setColumnAliase("recipientName");
        recipientName.setTitleName("接收人名称");
        dataSourceColumns.add(recipientName);

        // 主题

        DataSourceColumn subject = new DataSourceColumn();
        subject.setFieldName("subject");
        subject.setColumnName("subject");
        subject.setColumnAliase("subject");
        subject.setTitleName("主题");
        dataSourceColumns.add(subject);

        // 内容

        DataSourceColumn body = new DataSourceColumn();
        body.setFieldName("body");
        body.setColumnName("body");
        body.setColumnAliase("body");
        body.setTitleName("内容");
        dataSourceColumns.add(body);

        // 发送时间

        DataSourceColumn sentTime = new DataSourceColumn();
        sentTime.setFieldName("sentTime");
        sentTime.setColumnName("sentTime");
        sentTime.setColumnAliase("sentTime");
        sentTime.setTitleName("发送时间");
        dataSourceColumns.add(sentTime);
        // 接收时间
        DataSourceColumn receivedTime = new DataSourceColumn();
        receivedTime.setFieldName("receivedTime");
        receivedTime.setColumnName("receivedTime");
        receivedTime.setColumnAliase("receivedTime");
        receivedTime.setTitleName("接收时间");
        dataSourceColumns.add(receivedTime);

        // 是否已阅
        DataSourceColumn isread = new DataSourceColumn();
        isread.setFieldName("isread");
        isread.setColumnName("isread");
        isread.setColumnAliase("isread");
        isread.setTitleName("是否已阅");
        dataSourceColumns.add(isread);
        // 标记
        DataSourceColumn markFlag = new DataSourceColumn();
        markFlag.setFieldName("markFlag");
        markFlag.setColumnName("markFlag");
        markFlag.setColumnAliase("markFlag");
        markFlag.setTitleName("标记");
        dataSourceColumns.add(markFlag);

        DataSourceColumn messageParm = new DataSourceColumn();
        messageParm.setFieldName("messageParm");
        messageParm.setColumnName("messageParm");
        messageParm.setColumnAliase("messageParm");
        messageParm.setTitleName("消息template");
        dataSourceColumns.add(messageParm);

        DataSourceColumn note = new DataSourceColumn();
        note.setFieldName("note");
        note.setColumnName("note");
        note.setColumnAliase("note");
        note.setTitleName("说明");
        dataSourceColumns.add(note);

        DataSourceColumn viewpoint = new DataSourceColumn();
        viewpoint.setFieldName("viewpoint");
        viewpoint.setColumnName("viewpoint");
        viewpoint.setColumnAliase("viewpoint");
        viewpoint.setTitleName("立场");
        dataSourceColumns.add(viewpoint);

        // 关联url
        DataSourceColumn relatedUrl = new DataSourceColumn();
        relatedUrl.setFieldName("relatedUrl");
        relatedUrl.setColumnName("relatedUrl");
        relatedUrl.setColumnAliase("relatedUrl");
        relatedUrl.setTitleName("relatedUrl");
        dataSourceColumns.add(relatedUrl);

        // 关联url
        DataSourceColumn messageOutboxUuid = new DataSourceColumn();
        messageOutboxUuid.setFieldName("outboxuuid");
        messageOutboxUuid.setColumnName("outboxuuid");
        messageOutboxUuid.setColumnAliase("outboxuuid");
        messageOutboxUuid.setTitleName("outboxuuid");
        dataSourceColumns.add(messageOutboxUuid);

        return dataSourceColumns;
    }

    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumn, String whereHql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        // Iterator<DataSourceColumn> it = null;
        // if (dataSourceColumn.isEmpty()) {
        // it = getAllDataSourceColumns().iterator();
        // } else {
        // it = dataSourceColumn.iterator();
        // }
        // if(pagingInfo.getCurrentPage()==-1){//解决首页无法查询出资料的问题
        // pagingInfo.setCurrentPage(1);
        // }
        //
        // StringBuilder sb = new StringBuilder();
        // while (it.hasNext()) {
        // DataSourceColumn viewColumn = it.next();
        // if("messageParm".equals(viewColumn.getFieldName())){
        // sb.append("to_char(o." + viewColumn.getFieldName()+")");
        // }else if("outboxuuid".equals(viewColumn.getFieldName())){
        // sb.append("1" );
        // }else{
        // sb.append("o." + viewColumn.getFieldName());
        // }
        //
        // sb.append(" as ");
        // sb.append(viewColumn.getColumnAliase());
        // if (it.hasNext()) {
        // sb.append(Separator.COMMA.getValue());
        // }
        // }
        for (String key : queryParams.keySet()) {
            if ("endDate_receivedTime".equals(key)) {
                Date date = (Date) queryParams.get(key);
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String formatResult = s.format(date);
                whereHql = whereHql.replaceAll(":endDate_receivedTime", "to_date('" + formatResult
                        + "'||'23:59:59','yyyy-mm-dd hh24:mi:ss')");
            }
            if ("startDate_receivedTime".equals(key)) {
                Date date = (Date) queryParams.get(key);
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String formatResult = s.format(date);
                whereHql = whereHql.replaceAll(":startDate_receivedTime", "to_date('" + formatResult
                        + "'||'00:00:00','yyyy-mm-dd hh24:mi:ss')");
            }
        }

        String userId = SpringSecurityUtils.getCurrentUserId();
        StringBuilder sql = new StringBuilder(
                "select uuid,sender,recipient,sender_name as senderName,recipient_name as recipientName,subject,body,sent_time as sentTime,received_time as receivedTime,"
                        + "isread,mark_flag as markFlag,message_parm as messageParm,note,viewpoint,related_url as relatedUrl,message_outbox_uuid outboxuuid  from msg_message_inbox o  where o.recipient ='"
                        + userId + "'");
        String sqltotoal = "";
        String total_sql = "select count(1) as coutnum from msg_message_inbox  where recipient ='" + userId + "'";
        if (StringUtils.isNotBlank(whereHql)) {
            sql.append(" and (" + whereSql(whereHql) + ")");
            total_sql = total_sql + "  " + whereSql("and (" + whereHql + ")");
        }
        if (StringUtils.isNotBlank(orderBy)) {
            sql.append(" order by ");
            StringBuilder temp = new StringBuilder("");
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            for (String string : orderBys) {
                temp.append(" , " + string);
            }
            sql.append(temp.toString().replaceFirst(",", ""));
        }
        if (pagingInfo.getCurrentPage() != 0 && pagingInfo.getPageSize() != 0) {
            int currentPage = pagingInfo.getCurrentPage();
            if (currentPage == -1) {
                currentPage = 1;
            }
            int pagesize = pagingInfo.getPageSize();
            sqltotoal = "select * from (select l.*,rownum rownum_ from (" + sql.toString() + ") l where rownum<="
                    + pagesize * currentPage + ") where rownum_>" + (currentPage - 1) * pagesize;
        } else {
            sqltotoal = sql.toString();
        }
        List<QueryItem> list = new ArrayList<QueryItem>();
        List<QueryItem> total_list = new ArrayList<QueryItem>();
        try {
            Map queryMap = new HashMap<String, Object>();
            list = this.MessageInboxService.query(sqltotoal, queryMap, QueryItem.class);
            total_list = MessageInboxService.query(total_sql, queryMap, QueryItem.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        List<QueryItem> items = new ArrayList<QueryItem>();
        // Clob body = null;
        Clob messageparm = null;
        String body_str = "";
        String messageparm_str = "";
        for (Map<String, Object> detail : list) {
            QueryItem item = new QueryItem();
            // body = (Clob) detail.get("body");
            messageparm = (Clob) detail.get("messageparm");
            try {
                // if (body != null) {
                // body_str = IOUtils.toString(body.getCharacterStream());
                // } else {
                // body_str = "  ";
                // }
                if (messageparm != null) {
                    messageparm_str = IOUtils.toString(messageparm.getCharacterStream());
                } else {
                    messageparm_str = "  ";
                }

                if (body_str.length() > 100) {
                    body_str = body_str.substring(0, 100);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
            item.put("uuid", detail.get("uuid"));
            item.put("sender", detail.get("sender"));
            item.put("recipient", detail.get("recipient"));
            item.put("senderName", detail.get("sendername"));
            item.put("recipientName", detail.get("recipientname"));
            item.put("subject", detail.get("subject"));
            item.put("body", body_str);
            item.put("sentTime", String.valueOf(detail.get("senttime")));
            item.put("receivedTime", String.valueOf(detail.get("receivedtime")));
            item.put("isread", detail.get("isread"));
            item.put("markFlag", detail.get("markflag"));
            item.put("messageParm", messageparm_str);
            item.put("note", detail.get("note"));
            item.put("viewpoint", detail.get("viewpoint"));
            item.put("relatedUrl", detail.get("relatedurl"));
            item.put("outboxuuid", detail.get("outboxuuid"));
            items.add(item);
        }
        if (total_list != null) {
            BigDecimal num = (BigDecimal) total_list.get(0).get("coutnum");
            if (num != null) {
                pagingInfo.setTotalCount(num.longValue());
            } else {
                pagingInfo.setTotalCount(0);
            }

        } else {
            pagingInfo.setTotalCount(0);
        }

        return items;
    }

    private String whereSql(String whereHql) {
        String whereSql = whereHql.replaceAll("senderName", "sender_name")
                .replaceAll("recipientName", "recipient_name").replaceAll("sentTime", "sent_time")
                .replaceAll("markFlag", "mark_flag").replaceAll("receivedTime", "received_time")
                .replaceAll("messageParm", "message_parm").replaceAll("relatedUrl", "related_url")
                .replaceAll("outboxuuid", "message_outbox_uuid").replaceAll("标记", "mark_flag")
                .replaceAll("是否已阅", "isread");
        return whereSql;
    }
}