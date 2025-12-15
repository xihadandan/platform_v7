package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Order;
import com.wellsoft.pt.jpa.dao.NativeDao;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 查询接口上下文环境
 *
 * @author Xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 23 Nov 2016.1	Xiem		23 Nov 2016		Create
 * </pre>
 * @date 23 Nov 2016
 */
public interface QueryContext {
    /**
     * 添加项
     *
     * @param key
     * @param value
     * @return
     */
    public Object put(Object key, Object value);

    /**
     * 获取项
     *
     * @param key
     * @return
     */
    public Object get(Object key);

    /**
     * 如何描述该方法
     *
     * @return
     */
    public Set<Object> keySet();

    /**
     * 如何描述该方法
     *
     * @param key
     * @return
     */
    public Object remove(Object key);

    /**
     * 如何描述该方法
     *
     * @param key
     * @return
     */
    public boolean contains(Object key);

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Map<Object, Object> getContext();

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
     * 获取查询参数
     *
     * @return
     */
    public Map<String, Object> getQueryParams();

    /**
     * 获取分页信息
     *
     * @return PagingInfo
     */
    public PagingInfo getPagingInfo();

    /**
     * 返回排序信息
     *
     * @return
     */
    public List<Order> getOrders();

    /**
     * 返回NativeDao
     *
     * @return
     */
    public NativeDao getNativeDao();

    /**
     * 使用getWhereSqlString
     *
     * @return
     */
    @Deprecated
    public String getSqlString();

    /**
     * 如何描述该方法
     *
     * @return
     */
    public String getWhereSqlString();

    /**
     * 如何描述该方法
     *
     * @return
     */
    public String getOrderString();

    /**
     * 如何描述该方法
     *
     * @return
     */
    public CriteriaMetadata getCriteriaMetadata();

    /**
     * 如何描述该方法
     *
     * @return
     */
    public Criteria getCriteria();

    /**
     * 是否关键字查询
     *
     * @return
     */
    public boolean isKeywordQuery();

    /**
     * 获取关键字
     *
     * @return
     */
    public String getKeyword();

    /**
     * 获取接口参数
     *
     * @return
     */
    public String getInterfaceParam();


    public <T> T interfaceParam(Class<T> i);


}
