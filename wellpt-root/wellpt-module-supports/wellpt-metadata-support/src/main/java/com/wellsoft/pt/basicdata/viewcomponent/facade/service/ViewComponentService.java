/*
 * @(#)6 Dec 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.viewcomponent.facade.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.basicdata.datastore.bean.CdDataStoreColumnBean;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.support.Condition;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6 Dec 2016.1	Xiem		6 Dec 2016		Create
 * </pre>
 * @date 6 Dec 2016
 */
public interface ViewComponentService {
    /**
     * 获取数据仓库信息(select2格式)
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取数据仓库支持的导出方式(select2格式)
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadExportTypeSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取数据仓库的所有列信息(select2格式)
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadColumnsSelectData(Select2QueryInfo queryInfo);

    /**
     * 获取数据仓库提供条件查询的操作类型
     *
     * @return
     */
    public List<Map<String, String>> getQueryOperators();

    /**
     * 获取数据仓库的所有列信息
     *
     * @param id
     * @return
     */
    public List<CdDataStoreColumnBean> getColumnsById(String id);

    /**
     * 获取数据源提供选择器(select2格式)
     *
     * @param queryInfo
     * @return
     */
    public Select2QueryData loadRendererSelectData(Select2QueryInfo queryInfo);

    /**
     * 查询所有数据
     *
     * @param dataStoreId
     * @return
     */
    public DataStoreData loadAllData(String dataStoreId);

    /**
     * 查询分页数据
     *
     * @param dataStoreId
     * @param key              关键字
     * @param keyConditions    关键字 嵌套查询条件
     * @param defaultCondition 列表组件默认查询条件
     * @param pageNum
     * @param pageSize
     * @return
     */
    public DataStoreData loadAllData(String dataStoreId, String key, List<Condition> keyConditions,
                                     String defaultCondition, Integer pageNum, Integer pageSize);

    public DataStoreData loadAllDataWithNewTransaction(String dataStoreId);

    /**
     * 查询所有下拉数据
     *
     * @param dataStoreId
     * @return
     */
    public DataStoreData loadAllSelectData(boolean distinct, String dataStoreId, String idColumnIndex,
                                           String textColumnIndex);

    /**
     * 过滤出未读信息
     *
     * @param uuid
     * @return
     */
    public List<String> filterUnread(List<String> uuids);
}
