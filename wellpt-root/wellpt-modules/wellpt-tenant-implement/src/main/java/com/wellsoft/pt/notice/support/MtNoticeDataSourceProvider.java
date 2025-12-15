/*
 * @(#)2015-5-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.notice.support;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.provider.AbstractDataSourceProvider;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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
 * 2015-5-28.1	zhulh		2015-5-28		Create
 * </pre>
 * @date 2015-5-28
 */
@Component
public class MtNoticeDataSourceProvider extends AbstractDataSourceProvider {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private UnitApiFacade unitApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getAllDataSourceColumns()
     */
    @Override
    public Collection<DataSourceColumn> getAllDataSourceColumns() {
        Collection<DataSourceColumn> viewColumns = new ArrayList<DataSourceColumn>();
        // 标题
        DataSourceColumn title = new DataSourceColumn();
        title.setFieldName("title");
        title.setColumnAliase("title");
        title.setColumnName("title");
        title.setTitleName("标题");
        title.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(title);

        // 标题颜色
        DataSourceColumn titleColor = new DataSourceColumn();
        titleColor.setFieldName("titleColor");
        titleColor.setColumnAliase("titleColor");
        titleColor.setColumnName("titleColor");
        titleColor.setTitleName("标题颜色");
        titleColor.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(titleColor);

        // 分类名称
        DataSourceColumn categoryName = new DataSourceColumn();
        categoryName.setFieldName("categoryName");
        categoryName.setColumnAliase("categoryName");
        categoryName.setColumnName("categoryName");
        categoryName.setTitleName("分类名称");
        categoryName.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(categoryName);

        // 分类编号
        DataSourceColumn categoryCode = new DataSourceColumn();
        categoryCode.setFieldName("categoryCode");
        categoryCode.setColumnAliase("categoryCode");
        categoryCode.setColumnName("categoryCode");
        categoryCode.setTitleName("分类编号");
        categoryCode.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(categoryCode);

        // 责任者
        DataSourceColumn author = new DataSourceColumn();
        author.setFieldName("author");
        author.setColumnAliase("author");
        author.setColumnName("author");
        author.setTitleName("责任者");
        author.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(author);

        // 发布者ID
        DataSourceColumn publisher = new DataSourceColumn();
        publisher.setFieldName("publisher");
        publisher.setColumnAliase("publisher");
        publisher.setColumnName("publisher");
        publisher.setTitleName("发布者ID");
        publisher.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(publisher);

        // 发布者名称
        DataSourceColumn publisherName = new DataSourceColumn();
        publisherName.setFieldName("publisherName");
        publisherName.setColumnAliase("publisherName");
        publisherName.setColumnName("publisherName");
        publisherName.setTitleName("发布者名称");
        publisherName.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(publisherName);

        // 发布时间
        DataSourceColumn publishTime = new DataSourceColumn();
        publishTime.setFieldName("publishTime");
        publishTime.setColumnAliase("publishTime");
        publishTime.setColumnName("publishTime");
        publishTime.setTitleName("发布时间");
        publishTime.setColumnDataType(ViewColumnType.DATE.name());
        viewColumns.add(publishTime);

        // 发布对象ID
        DataSourceColumn reader = new DataSourceColumn();
        reader.setFieldName("reader");
        reader.setColumnAliase("reader");
        reader.setColumnName("reader");
        reader.setTitleName("发布对象ID");
        reader.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(reader);

        // 发布对象名称
        DataSourceColumn readerName = new DataSourceColumn();
        readerName.setFieldName("readerName");
        readerName.setColumnAliase("readerName");
        readerName.setColumnName("readerName");
        readerName.setTitleName("发布对象名称");
        readerName.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(readerName);

        // 文件数据UUID
        DataSourceColumn noticeDataUuid = new DataSourceColumn();
        noticeDataUuid.setFieldName("dataUuid");
        noticeDataUuid.setColumnAliase("dataUuid");
        noticeDataUuid.setColumnName("dataUuid");
        noticeDataUuid.setTitleName("文件数据UUID");
        noticeDataUuid.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(noticeDataUuid);

        // 置顶状态
        DataSourceColumn topState = new DataSourceColumn();
        topState.setFieldName("topState");
        topState.setColumnAliase("topState");
        topState.setColumnName("topState");
        topState.setTitleName("置顶状态");
        topState.setColumnDataType(ViewColumnType.STRING.name());
        viewColumns.add(topState);
        return viewColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getModuleId()
     */
    @Override
    public String getModuleId() {
        return "mt_notice";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "全局性公告";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider#query(java.util.Set, java.lang.String, java.util.Map, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public List<QueryItem> query(Set<DataSourceColumn> viewColumns, String whereHql, Map<String, Object> queryParams, String orderBy, PagingInfo pagingInfo) {
        Iterator<DataSourceColumn> it = null;
        if (viewColumns.isEmpty()) {
            it = getAllDataSourceColumns().iterator();
        } else {
            it = viewColumns.iterator();
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
        queryParams.put("whereHql", whereHql);
        pagingInfo.setAutoCount(true);

        // 组织机构ID
        String userId = SpringSecurityUtils.getCurrentUserId();
        Set<String> tmpOrgIds = orgApiFacade.getUserOrgIds(userId);
        List<String> orgIds = new ArrayList<String>();
        for (String orgId : tmpOrgIds) {
            if (!orgId.startsWith(IdPrefix.USER.getValue())) {
                orgIds.add(orgId);
            }
        }
        orgIds.add(userId);
        queryParams.put("orgIds", orgIds);
        // update by linz 2015年9月6日 12:39:12 全部的时候显示全部不过滤时间，目前先用时间控制
        if (!"( (1=1))".equals(whereHql)) {
            queryParams.put("currentTime", Calendar.getInstance().getTime());
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");// 设置日期格式
            try {
                queryParams.put("currentTime", df.parse("1900-01-01 00:00:00"));
            } catch (ParseException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }

        // 单位ID
        List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitByTenantId(SpringSecurityUtils.getCurrentTenantId());
        for (CommonUnit commonUnit : commonUnits) {
            orgIds.add(commonUnit.getId());
            orgIds.add(commonUnit.getUnitId());
        }

        List<QueryItem> queryItems = this.getCommonDao().namedQuery("mtNoticeQuery", queryParams, QueryItem.class, pagingInfo);
        return queryItems;
    }
}
