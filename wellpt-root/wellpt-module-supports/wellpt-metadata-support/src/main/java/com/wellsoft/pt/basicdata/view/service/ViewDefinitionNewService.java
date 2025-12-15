/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.view.bean.ColumnDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.entity.PageDefinitionNew;
import com.wellsoft.pt.basicdata.view.entity.ViewDefinitionNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 视图自定义service类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
public interface ViewDefinitionNewService {
    /**
     * 根据视图ID获取视图定义
     *
     * @param id
     * @return
     */
    ViewDefinitionNew getByViewId(String viewId);

    /**
     * 获取所有视图定义列表
     *
     * @return
     */
    List<ViewDefinitionNew> searchViewDefinition();

    /**
     * 获取所有带有权限列的视图定义列表
     *
     * @return
     */
    List<ViewDefinitionNew> searchViewDefinitionByPermission();

    /**
     * 获取所有视图带权限的列
     *
     * @return
     */
    List<ColumnDefinitionNew> getViewColumnByPermission();

    /**
     * 保存自定义视图的基本信息
     *
     * @param entity
     */
    void saveViewDefinition(ViewDefinitionNew entity);

    /**
     * 保存视图
     */
    String saveBean(ViewDefinitionNewBean bean);

    /**
     * 删除定义的视图对象
     *
     * @param viewUuid
     */
    void deleteViewDefinition(String viewUuid);

    /**
     * 获取列的Bean
     *
     * @param s
     * @return
     */
    public abstract ColumnDefinitionNewBean getBean(String s);

    /**
     * 根据uuid获取视图的Bean
     *
     * @param s
     * @return
     */
    public abstract ViewDefinitionNewBean getBeanByUuid(String uuid);

    public abstract List<QueryItem> getViewData(String defaultCondition, String whereSql, String dataSourceDefId,
                                                Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                                DyViewQueryInfoNew dyViewQueryInfoNew, String rowIdKey);

    /**
     * 根据uuid获取视图对应的列的数据 (调用动态表单接口)
     *
     * @param uuid
     * @return
     */
    public abstract List<QueryItem> getColumnData(String defaultCondition, String dataSourceDefId,
                                                  Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                                  DyViewQueryInfoNew dyViewQueryInfoNew);

    /**
     * 根据uuid获取视图对应的列的数据 (调用系统表接口)
     *
     * @param uuid
     * @return
     */
    public abstract List<QueryItem> getColumnData2(String tableUuid, String defaultCondition,
                                                   Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, String roleType, String roleValue,
                                                   PageDefinitionNew pageDefinitionNew, DyViewQueryInfoNew dyViewQueryInfoNew, String count);

    /**
     * 根据模块id获取视图对应的列的数据 (调用模块数据的实现接口)
     *
     * @param uuid
     * @return
     */
    public abstract List<QueryItem> getColumnData3(String defaultCondition, String tableName,
                                                   Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                                   DyViewQueryInfoNew dyViewQueryInfoNew, String count);

    /**
     * 根据uuid获取视图对应的排序的列的数据 (调用动态表单接口)
     *
     * @param uuid
     * @return
     */
    public abstract List<QueryItem> getSortColumnData(String defaultCondition, String tableName,
                                                      Set<ColumnDefinitionNew> columnDefinitionNews, String title, PageDefinitionNew pageDefinitionNew,
                                                      PagingInfo page, String orderbyArr);

    /**
     * 根据uuid获取视图对应的排序的列的数据 (调用系统表接口)
     *
     * @param uuid
     * @return
     */
    public abstract List<QueryItem> getSortColumnData2(String tableUuid, String defaultCondition,
                                                       Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, String title,
                                                       PageDefinitionNew pageDefinitionNew, PagingInfo page, String roleType, String roleValue, String orderbyArr);

    /**
     * 根据查询关键字条件获取对应的表数据(调用动态表单接口)
     *
     * @return
     */
    public abstract List<QueryItem> getSelectColumnData(String defaultCondition, String tableName,
                                                        Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                        List keyWords);

    public List<QueryItem> getSelectColumnData3(String defaultCondition, String tableName,
                                                Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                List keyWords, String beginTime, String endTime, String searchField);

    /**
     * 根据查询关键字条件获取对应的表数据(调用系统表接口)
     *
     * @return
     */
    public abstract List<QueryItem> getSelectColumnData2(String tableUuid, String defaultCondition,
                                                         Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, PageDefinitionNew pageDefinitionNew,
                                                         PagingInfo page, List keyWords, String roleType, String roleValue, String beginTime, String endTime,
                                                         String searchField);

    /**
     * 根据条件备选项来获取对应的表数据(调用动态表单接口)
     *
     * @return
     */
    public abstract List<QueryItem> getCondColumnData(String defaultCondition, String tableName,
                                                      Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                      String condValue, String appointColumn);

    /**
     * 根据条件备选项来获取对应的表数据(调用系统表接口)
     *
     * @return
     */
    public abstract List<QueryItem> getCondColumnData2(String tableUuid, String defaultCondition,
                                                       Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, PageDefinitionNew pageDefinitionNew,
                                                       PagingInfo page, String condValue, String appointColumn, String roleType, String roleValue);

    /**
     * 根据条件备选项来获取对应的表数据(调用系统接口)
     *
     * @return
     */
    public abstract List<QueryItem> getCondColumnData3(String defaultCondition, String tableName,
                                                       Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                       String condValue, String appointColumn);

    /**
     * 根据日期备选项来获取对应的表数据(调用动态表单接口)
     */
    public abstract List<QueryItem> getDateColumnData(String defaultCondition, String tableName,
                                                      Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                      String beginTime, String endTime, String appointColumn);

    /**
     * 根据日期备选项来获取对应的表数据(调用系统表接口)
     */
    public abstract List<QueryItem> getDateColumnData2(String tableUuid, String defaultCondition,
                                                       Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                       String beginTime, String endTime, String appointColumn);

    public List<QueryItem> getSortColumnData3(String defaultCondition, String tableName,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, String title, PageDefinitionNew pageDefinitionNew,
                                              PagingInfo page, String orderbyArr);

    /**
     * 根据传入的查询字段名查询相应的字段数据(调用动态表单接口)
     */
    public abstract List<QueryItem> getSelectData(String formUuid, String fieldName, PagingInfo page);

    /**
     * 根据传入的查询字段名查询相应的字段数据(调用系统表单接口)
     */
    public abstract List<Map<String, Object>> getSelectData2(String formUuid, String fieldName, PagingInfo page);

    /**
     * 根据jqgrid的行id来获取视图的详细信息
     *
     * @param id
     * @return
     */
    public abstract ViewDefinitionNewBean getBeanById(String id);

    /**
     * 根据jqgrid的行id来删除视图的详细信息
     *
     * @param id
     */
    public abstract void deleteById(String id);

    /**
     * 批量删除视图
     *
     * @param id
     */
    public abstract void deleteAllById(String[] ids);

    /**
     * 根据传入的按钮编码来获取按钮的信息
     *
     * @param code
     * @return
     */
    public abstract List getCustomButton(String code);

    /**
     * 模块数据（提供给动态表单树形下拉框使用）
     *
     * @param s
     * @param viewUuid
     * @return
     */
    public abstract List<TreeNode> getColumnDataForTree(String s, String viewUuid);

    /**
     * 分类获得所有的视图
     *
     * @return
     */
    public abstract List<TreeNode> getViewAsTreeAsync(String id);

    public abstract List<QueryItem> getViewDataByKey(String val, String key);

    /**
     * 根据视图uuid获得所有的列定义
     *
     * @param viewUuid
     * @return
     */
    public abstract Set<ColumnDefinitionNew> getColumnDefinitions(String viewUuid);

    public long getView3Count(String tableName, String whereHql);

    public abstract List<QueryItem> getViewSingleData(String viewUuid, String relationDataDefiantion, String TempId);

}
