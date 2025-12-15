/*
 * @(#)2014-1-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.integration.service.ExchangeDataFileUploadService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 2014-1-13.1	zhulh		2014-1-13		Create
 * </pre>
 * @date 2014-1-13
 */
@Component
public class ExchangeDataFJFileUploadLogViewDataSource extends AbstractDataExchangeViewDataSource {

    @Autowired
    private ExchangeDataFileUploadService exchangeDataFileUploadService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getAllViewColumns()
     */
    @Override
    public Collection<ViewColumn> getAllViewColumns() {
        Collection<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        ViewColumn uuid = new ViewColumn();
        uuid.setAttributeName("uuid");
        uuid.setColumnAlias("uuid");
        uuid.setColumnName("uuid");
        uuid.setColumnType(ViewColumnType.STRING);
        viewColumns.add(uuid);

        // 数据类型ID
        ViewColumn typeId = new ViewColumn();
        typeId.setAttributeName("typeId");
        typeId.setColumnAlias("typeId");
        typeId.setColumnName("数据类型ID");
        typeId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(typeId);

        // 数据类型名称
        ViewColumn typeName = new ViewColumn();
        typeName.setAttributeName("typeName");
        typeName.setColumnAlias("typeName");
        typeName.setColumnName("数据类型");
        typeName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(typeName);

        // 批次ID
        ViewColumn batchId = new ViewColumn();
        batchId.setAttributeName("batchId");
        batchId.setColumnAlias("batchId");
        batchId.setColumnName("批次ID");
        batchId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(batchId);

        // 单位ID
        ViewColumn unitId = new ViewColumn();
        unitId.setAttributeName("unitId");
        unitId.setColumnAlias("unitId");
        unitId.setColumnName("单位ID");
        unitId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(unitId);

        // 单位名称
        ViewColumn unitName = new ViewColumn();
        unitName.setAttributeName("unitName");
        unitName.setColumnAlias("unitName");
        unitName.setColumnName("单位名称");
        unitName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(unitName);

        // 用户ID
        ViewColumn userId = new ViewColumn();
        userId.setAttributeName("userId");
        userId.setColumnAlias("userId");
        userId.setColumnName("用户ID");
        userId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(userId);

        // 用户名
        ViewColumn userName = new ViewColumn();
        userName.setAttributeName("userName");
        userName.setColumnAlias("userName");
        userName.setColumnName("用户名");
        userName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(userName);

        // 部门ID
        ViewColumn departmentId = new ViewColumn();
        departmentId.setAttributeName("departmentId");
        departmentId.setColumnAlias("departmentId");
        departmentId.setColumnName("部门ID");
        departmentId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(departmentId);

        // 部门名称
        ViewColumn departmentName = new ViewColumn();
        departmentName.setAttributeName("departmentName");
        departmentName.setColumnAlias("departmentName");
        departmentName.setColumnName("部门名称");
        departmentName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(departmentName);

        // 部门名称
        ViewColumn moduleName = new ViewColumn();
        moduleName.setAttributeName("moduleName");
        moduleName.setColumnAlias("moduleName");
        moduleName.setColumnName("模块名称");
        moduleName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(moduleName);

        // 业务类型ID
        ViewColumn businessTypeId = new ViewColumn();
        businessTypeId.setAttributeName("businessTypeId");
        businessTypeId.setColumnAlias("businessTypeId");
        businessTypeId.setColumnName("业务类型ID");
        businessTypeId.setColumnType(ViewColumnType.STRING);
        viewColumns.add(businessTypeId);

        // 业务类型名称
        ViewColumn businessTypeName = new ViewColumn();
        businessTypeName.setAttributeName("businessTypeName");
        businessTypeName.setColumnAlias("businessTypeName");
        businessTypeName.setColumnName("业务类型名称");
        businessTypeName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(businessTypeName);

        // 结点名称
        ViewColumn nodeName = new ViewColumn();
        nodeName.setAttributeName("nodeName");
        nodeName.setColumnAlias("nodeName");
        nodeName.setColumnName("结点名称");
        nodeName.setColumnType(ViewColumnType.STRING);
        viewColumns.add(nodeName);

        // 上传时间
        ViewColumn uploadTime = new ViewColumn();
        uploadTime.setAttributeName("uploadTime");
        uploadTime.setColumnAlias("uploadTime");
        uploadTime.setColumnName("上传时间");
        uploadTime.setColumnType(ViewColumnType.DATE);
        viewColumns.add(uploadTime);

        // 文件名称
        ViewColumn filename = new ViewColumn();
        filename.setAttributeName("filename");
        filename.setColumnAlias("filename");
        filename.setColumnName("文件名称");
        filename.setColumnType(ViewColumnType.STRING);
        viewColumns.add(filename);

        // 文件类型
        ViewColumn contentType = new ViewColumn();
        contentType.setAttributeName("contentType");
        contentType.setColumnAlias("contentType");
        contentType.setColumnName("文件类型");
        contentType.setColumnType(ViewColumnType.STRING);
        viewColumns.add(contentType);

        // 文件大小
        ViewColumn fileSize = new ViewColumn();
        fileSize.setAttributeName("fileSize");
        fileSize.setColumnAlias("fileSize");
        fileSize.setColumnName("文件大小");
        fileSize.setColumnType(ViewColumnType.STRING);
        viewColumns.add(fileSize);

        // 消息摘要
        ViewColumn digestValue = new ViewColumn();
        digestValue.setAttributeName("digestValue");
        digestValue.setColumnAlias("digestValue");
        digestValue.setColumnName("消息摘要");
        digestValue.setColumnType(ViewColumnType.STRING);
        viewColumns.add(digestValue);

        return viewColumns;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleId()
     */
    @Override
    public String getModuleId() {
        return ModuleID.DATA_EXCHANGE.getValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "数据交换-发件-文件上传日志";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource#query(java.util.Collection, java.lang.String, java.util.Map, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String departmentId = userDetails.getMainDepartmentId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("departmentId", departmentId);
        String hql = "from ExchangeDataFileUpload o where o.departmentId = :departmentId ";
        if (StringUtils.isNotBlank(whereHql)) {
            hql += " and (" + whereHql + ")";
        }

        if (StringUtils.isNotBlank(orderBy)) {
            hql += orderBy;
        }

        List<QueryItem> queryItems = exchangeDataFileUploadService.listQueryItemByHQL(hql, values, pagingInfo);
        for (QueryItem item : queryItems) {
            long fileSize = (Long) item.get("fileSize");
            if (fileSize < 1024) {
                item.put("fileSize", "1KB");
            } else if (fileSize >= 1024) {
                item.put("fileSize", fileSize / 1024 + "KB");
            }
        }
        return queryItems;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource#count(java.util.Collection, java.lang.String, java.util.Map)
     */
    @Override
    public Long count(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String departmentId = userDetails.getMainDepartmentId();
        Map<String, Object> values = new HashMap<String, Object>();
        values.putAll(queryParams);
        values.put("departmentId", departmentId);
        String hql = "select count(*) from ExchangeDataFileUpload o where o.departmentId = :departmentId ";
        if (StringUtils.isNotBlank(whereHql)) {
            hql += " and (" + whereHql + ")";
        }

        return exchangeDataFileUploadService.findUnique(hql, values);
    }

}
