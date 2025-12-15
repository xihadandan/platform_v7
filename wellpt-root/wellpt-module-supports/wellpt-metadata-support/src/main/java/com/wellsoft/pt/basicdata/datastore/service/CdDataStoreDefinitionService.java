/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreDefinitionBean;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月19日.1	xiem		2016年10月19日		Create
 * </pre>
 * @date 2016年10月19日
 */
public interface CdDataStoreDefinitionService {
    public CdDataStoreDefinitionBean getBeanById(String id);

    public String saveBean(CdDataStoreDefinitionBean bean);

    public void deleteByUuids(String[] uuids);

    public void deleteByIds(String[] ids);

    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    public Select2QueryData loadColumnsSelectData(Select2QueryInfo queryInfo);

    public List<CdDataStoreColumnBean> getColumnsById(String id);

    /**
     * select2查询数据库表
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectDataByTable(Select2QueryInfo queryInfo);

    /**
     * 根据表名获取所有列
     *
     * @param tableName
     * @return
     */
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName);

    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, Long dbLinkConfUuid);

    /**
     * 根据表名、是否使用驼峰风格列索引获取所有列
     *
     * @param tableName
     * @param camelColumnIndex
     * @return
     */
    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, boolean camelColumnIndex);

    public List<CdDataStoreColumnBean> loadTableColumns(String tableName, boolean camelColumnIndex, Long dbLinkConfUuid);


    /**
     * select2查询数据库视图
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectDataByView(Select2QueryInfo queryInfo);

    /**
     * 根据视图名获取所有列
     *
     * @param viewName
     * @return
     */
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName);

    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, Long dbConfUuid);

    /**
     * 根据表名、是否使用驼峰风格列索引获取所有列
     *
     * @param viewName
     * @param camelColumnIndex
     * @return
     */
    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, boolean camelColumnIndex);

    public List<CdDataStoreColumnBean> loadViewColumns(String viewName, boolean camelColumnIndex, Long dbConfUuid);

    /**
     * select2查询Entity实例
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectDataByEntity(Select2QueryInfo queryInfo);

    public List<CdDataStoreColumnBean> loadEntityColumns(String entityName);

    public Select2QueryData loadSelectDataBySqlName(Select2QueryInfo queryInfo);

    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName);

    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, Long dbLinkConfUuid);

    /**
     * 根据sql命名查询、是否使用驼峰风格列索引获取所有列
     *
     * @param sql
     * @param camelColumnIndex
     * @return
     */
    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, boolean camelColumnIndex);

    public List<CdDataStoreColumnBean> loadSqlNameColumns(String sqlName, boolean camelColumnIndex, Long dbLinkConfUuid);

    public Select2QueryData loadSelectDataByDataInterface(Select2QueryInfo queryInfo);

    public List<CdDataStoreColumnBean> loadDataInterfaceColumns(String dataInterfaceName, String interfaceParam);

    public List<CdDataStoreColumnBean> loadSqlColumns(String sql);

    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, Long dbLinkConfUuid);

    /**
     * 根据sql、是否使用驼峰风格列索引获取所有列
     *
     * @param sql
     * @param camelColumnIndex
     * @return
     */
    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, boolean camelColumnIndex);

    public List<CdDataStoreColumnBean> loadSqlColumns(String sql, boolean camelColumnIndex, Long dbLinkConfUuid);

    // 返回接口使用说明
    public String getInterfaceDesc(String dataInterfaceName);

    Select2QueryData loadSelectDefinitionByModule(Select2QueryInfo select2QueryInfo);

    public abstract Select2QueryData loadSelectData2(Select2QueryInfo queryInfo);

    public abstract Select2QueryData loadSelectData2ByIds(Select2QueryInfo queryInfo);
}
