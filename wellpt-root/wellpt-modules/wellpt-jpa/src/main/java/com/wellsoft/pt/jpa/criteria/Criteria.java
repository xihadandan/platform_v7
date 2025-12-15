/*
 * @(#)2016年10月25日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月25日.1	xiem		2016年10月25日		Create
 * </pre>
 * @date 2016年10月25日
 */
public interface Criteria {

    /**
     * 设置是否去重
     *
     * @param distinct
     * @return
     */
    public void setDistinct(boolean distinct);

    /**
     * 设置查询的列索引
     *
     * @param projection
     * @return
     */
    public void setProjection(Set<String> projection);

    /**
     * 添加一个查询条件
     *
     * @param criterion 查询条件
     */
    public void addCriterion(Criterion criterion);

    /**
     * 获取查询条件
     *
     * @return
     */
    public Criterion getCriterion();

    /**
     * 获取别名
     *
     * @param columnIndex
     * @return
     */
    public String getAlais();

    /**
     * 通过列索引获取真实列名
     *
     * @param columnIndex 列索引
     * @return
     */
    public String getColumnName(String columnIndex);

    /**
     * 获取查询参数
     *
     * @return
     */
    public Map<String, Object> getQueryParams();

    /**
     * 添加一个查询参数
     *
     * @param columnIndex
     * @param paramName
     * @param value
     * @return
     */
    public Criteria addQueryParams(String columnIndex, String paramName, Object value);

    /**
     * 添加查询参数
     *
     * @param paramName
     * @param value
     * @return
     */
    public Criteria addQueryParams(String paramName, Object value);

    /**
     * 根据列索引生成不重复的参数名
     *
     * @param columnIndex
     * @return
     */
    public String generateParamsName(String columnIndex);

    /**
     * 获取分页信息
     *
     * @return PagingInfo
     */
    public PagingInfo getPagingInfo();

    /**
     * 设置分页信息
     *
     * @param pagingInfo
     * @return
     */
    public Criteria setPagingInfo(PagingInfo pagingInfo);

    /**
     * 添加排序
     *
     * @param order
     * @return
     */
    public Criteria addOrder(Order order);

    public Criteria addOrders(Collection<Order> orders);

    /**
     * 返回排序信息
     *
     * @return
     */
    public List<Order> getOrders();

    /**
     * 获取当前条件查询的数据库信息
     *
     * @return
     */
    public CriteriaMetadata getCriteriaMetadata();

    /**
     * 设置当前条件查询的数据库信息
     *
     * @return
     */
    public void setCriteriaMetadata(CriteriaMetadata criteriaMetadata);

    /**
     * 返回查询结果
     *
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> list();

    /**
     * 返回某个类型的查询结果
     *
     * @param itemClass
     * @return
     */
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass);

    /**
     * 查询
     *
     * @return
     */
    public long count();

    /**
     * 获取sessionFactory
     *
     * @return
     */
    public SessionFactory getSessionFactory();

    /**
     * 获取session
     *
     * @return
     */
    public Session getSession();
}
