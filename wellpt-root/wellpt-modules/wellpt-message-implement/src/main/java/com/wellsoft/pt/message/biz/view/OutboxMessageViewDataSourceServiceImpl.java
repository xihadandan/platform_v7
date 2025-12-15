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
import com.wellsoft.pt.message.service.MessageOutboxService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
public class OutboxMessageViewDataSourceServiceImpl extends AbstractDataSourceProvider {

    @Autowired
    protected MessageOutboxService messageOutboxService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.biz.view.InboxMessageViewDataSourceServiceImpl#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "我的消息--发件箱";
    }

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
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getAllViewColumns()
     */
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
        // 消息模板ID
        DataSourceColumn templateId = new DataSourceColumn();

        templateId.setFieldName("templateId");
        templateId.setColumnName("templateId");
        templateId.setColumnAliase("templateId");
        templateId.setTitleName("消息模板ID");
        dataSourceColumns.add(templateId);
        // 名称
        DataSourceColumn name = new DataSourceColumn();

        name.setFieldName("name");
        name.setColumnName("name");
        name.setColumnAliase("name");
        name.setTitleName("名称");
        dataSourceColumns.add(name);
        // 消息类型
        DataSourceColumn type = new DataSourceColumn();

        type.setFieldName("type");
        type.setColumnName("type");
        type.setColumnAliase("type");
        type.setTitleName("消息类型");
        dataSourceColumns.add(type);
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

        // 消息标识
        DataSourceColumn messageId = new DataSourceColumn();
        messageId.setFieldName("messageId");
        messageId.setColumnName("messageId");
        messageId.setColumnAliase("messageId");
        messageId.setTitleName("消息标识");
        dataSourceColumns.add(messageId);

        // 是否删除
        DataSourceColumn iscancel = new DataSourceColumn();
        iscancel.setFieldName("iscancel");
        iscancel.setColumnName("iscancel");
        iscancel.setColumnAliase("iscancel");
        iscancel.setTitleName("是否删除");
        dataSourceColumns.add(iscancel);
        // 是否删除
        DataSourceColumn markFlag = new DataSourceColumn();
        markFlag.setFieldName("markFlag");
        markFlag.setColumnName("markFlag");
        markFlag.setColumnAliase("markFlag");
        markFlag.setTitleName("标记");
        dataSourceColumns.add(markFlag);
        // 关联url
        DataSourceColumn relatedUrl = new DataSourceColumn();
        relatedUrl.setFieldName("relatedUrl");
        relatedUrl.setColumnName("relatedUrl");
        relatedUrl.setColumnAliase("relatedUrl");
        relatedUrl.setTitleName("relatedUrl");
        dataSourceColumns.add(relatedUrl);
        // 关联url
        DataSourceColumn relatedTitle = new DataSourceColumn();
        relatedTitle.setFieldName("relatedTitle");
        relatedTitle.setColumnName("relatedTitle");
        relatedTitle.setColumnAliase("relatedTitle");
        relatedTitle.setTitleName("relatedTitle");
        dataSourceColumns.add(relatedTitle);
        return dataSourceColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public List<QueryItem> query(Set<DataSourceColumn> dataSourceColumn, String whereHql,
                                 Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        for (String key : queryParams.keySet()) {
            System.out.println(queryParams.get(key));
            if ("endDate_sentTime".equals(key)) {
                Date date = (Date) queryParams.get(key);
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String formatResult = s.format(date);
                whereHql = whereHql.replaceAll(":endDate_sentTime", "to_date('" + formatResult
                        + "'||'23:59:59','yyyy-mm-dd hh24:mi:ss')");
            }
            if ("startDate_sentTime".equals(key)) {
                Date date = (Date) queryParams.get(key);
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String formatResult = s.format(date);
                whereHql = whereHql.replaceAll(":startDate_sentTime", "to_date('" + formatResult
                        + "'||'00:00:00','yyyy-mm-dd hh24:mi:ss')");
            }
        }
        Iterator<DataSourceColumn> it = null;
        if (dataSourceColumn.isEmpty()) {
            it = getAllDataSourceColumns().iterator();
        } else {
            it = dataSourceColumn.iterator();
        }
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            DataSourceColumn viewColumn = it.next();
            sb.append("o." + viewColumn.getFieldName());
            sb.append(" as ");
            sb.append(viewColumn.getColumnAliase());
            if (it.hasNext()) {
                sb.append(Separator.COMMA.getValue());
            }
        }

        StringBuilder hql = new StringBuilder("select " + sb + "  from MessageOutbox o where o.sender = :userId ");
        if (StringUtils.isNotBlank(whereHql)) {
            hql.append(" and (" + whereHql + ")");
        }

        if (StringUtils.isNotBlank(orderBy)) {
            hql.append(" order by ");
            StringBuilder temp = new StringBuilder("");
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            for (String string : orderBys) {
                temp.append(" , " + string);
            }
            hql.append(temp.toString().replaceFirst(",", ""));
        }

        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);

        List<QueryItem> items = messageOutboxService.listQueryItemByHQL(hql.toString(), values, pagingInfo);
        Clob recipient = null;
        Clob recipientName = null;
        Clob body = null;
        String recipientName_str = "";
        String body_str = "";
        for (QueryItem item : items) {
            recipient = (Clob) item.get("recipient");
            recipientName = (Clob) item.get("recipientName");
            body = (Clob) item.get("body");
            try {
                if (recipient != null) {
                    item.put("recipient", IOUtils.toString(recipient.getCharacterStream()));
                } else {
                    item.put("recipient", "  ");
                }
                if (recipientName != null) {
                    recipientName_str = IOUtils.toString(recipientName.getCharacterStream());
                } else {
                    recipientName_str = "   ";
                }
                if (body != null) {

                    body_str = IOUtils.toString(body.getCharacterStream());
                } else {
                    body_str = "   ";
                }

                if (recipientName_str.length() > 100) {
                    recipientName_str = recipientName_str.substring(0, 100);
                }
                // if (body_str.length() > 100) {
                // body_str = body_str.substring(0, 100);
                // }
                item.put("recipientName", recipientName_str);
                item.put("body", body_str);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return items;
    }
}
