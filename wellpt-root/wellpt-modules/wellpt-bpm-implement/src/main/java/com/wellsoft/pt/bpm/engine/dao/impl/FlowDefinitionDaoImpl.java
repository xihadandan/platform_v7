/*
 * @(#)2012-10-24 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.dao.FlowDefinitionDao;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-24.1	zhulh		2012-10-24		Create
 * </pre>
 * @date 2012-10-24
 */
@Repository
public class FlowDefinitionDaoImpl extends AbstractJpaDaoImpl<FlowDefinition, String> implements FlowDefinitionDao {

    /**
     * 根据ID计算已经存在的流程定义数
     */
    public static final String COUNT_BY_ID = "select count(*) from FlowDefinition flow_def where flow_def.id = :id";

    /**
     * 根据流程分类计算已经存在的流程定义数
     */
    public static final String COUNT_BY_CATEGORY = "select count(*) from FlowDefinition flow_def where flow_def.category = :category";

    /**public static final String GET_BY_LASTEST_VERSION = "from FlowDefinition t1, "
     + "(select id, max(version) as version from FlowDefinition flow_def group by flow_def.id) t2 "
     + "where t1.id = t2.id and t1.version = t2.version";*/

    /**
     * 查询最新版本的id及版本号
     */
    public static final String GET_LATEST_VERSION = "select id, max(version) as version from FlowDefinition flow_def group by flow_def.id";

    // 根据流程定义ID获取最新的版本
    public static final String GET_LATEST_VERSION_BY_ID = "select max(version) as version from FlowDefinition flow_def where flow_def.id = :flowDefId group by flow_def.id";

    private static final String QUERY_BY_ID = "from FlowDefinition o where o.id = :id and o.enabled = true "
            + "and exists(select id, max(version) from FlowDefinition wf_flow_definition where wf_flow_definition.id = :id and wf_flow_definition.enabled = true "
            + "group by id having wf_flow_definition.id = o.id and max(version) = o.version)";

    private static final String LIST_BY_ID = "from FlowDefinition o where o.id = :id and o.enabled = true order by o.version desc";
    private static final String LIST_BY_ID_AND_SYSTEM = "from FlowDefinition o where o.id = :id and o.enabled = true and (o.system = :system or o.system is null) order by o.version desc";

    // 根据表单uuid获取最新版本的流程
    private static final String QUERY_BY_FORM_UUID = "from FlowDefinition o where o.formUuid = :formUuid and o.enabled = true "
            + "and exists(select id, max(version) from FlowDefinition wf_flow_definition "
            + "group by id, enabled having wf_flow_definition.id = o.id and max(version) = o.version and enabled = true)";

    /**
     * 根据流程定义ID获取最新的版本
     *
     * @param id
     * @return
     */
    @Override
    public Double getLatestVersionById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefId", id);
        return this.getNumberByHQL(GET_LATEST_VERSION_BY_ID, values);
    }

    /**
     * 通过流程定义ID获取流程定义
     *
     * @param id
     * @return
     */
    @Override
    public FlowDefinition getById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        // 查询优化取流程定义
        List<FlowDefinition> flowDefinitions = null;
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            values.put("system", system);
            flowDefinitions = this.listByHQLAndPage(LIST_BY_ID_AND_SYSTEM, values, new PagingInfo(1, 1, false));
        } else {
            flowDefinitions = this.listByHQLAndPage(LIST_BY_ID, values, new PagingInfo(1, 1, false));
        }
        if (CollectionUtils.isNotEmpty(flowDefinitions)) {
            return flowDefinitions.get(0);
        }
        return this.getOneByHQL(QUERY_BY_ID, values);
    }

    /**
     * 通过表单的uuid获取最新的流程定义
     * lmw 2015-5-25 16:48
     *
     * @param id
     * @return
     */
    @Override
    public List<FlowDefinition> getByFormUuid(String formUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("formUuid", formUuid);
        return this.listByHQL(QUERY_BY_FORM_UUID, values);
    }

    /**
     * @param sn
     * @return
     */
    @Override
    public List<FlowDefinition> getByCategory(String categorySN) {
        return this.listByFieldEqValue("category", categorySN);
    }

    /**
     * 根据ID计算已经存在的流程定义数
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        return this.getNumberByHQL(COUNT_BY_ID, values);
    }

    /**
     * 根据流程分类计算已经存在的流程定义数
     *
     * @param code
     * @return
     */
    @Override
    public Long countByCategory(String category) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("category", category);
        return this.getNumberByHQL(COUNT_BY_CATEGORY, values);
    }

    /**
     * 获取最新版本的流程定义列表
     *
     * @param dataPage
     */
    @Override
    public void getLatest(Page<FlowDefinition> dataPage) {
        // 先查询最新版本的id及版本号，再查出相应的流程定义
        this.findPage(dataPage, GET_LATEST_VERSION);
        List<FlowDefinition> list = dataPage.getResult();
        StringBuilder hql = new StringBuilder();
        hql.append("from FlowDefinition flow_def where ");
        List<Object> values = new ArrayList<Object>();
        for (int index = 0; index < list.size(); index++) {
            FlowDefinition flowDefinition = list.get(index);
            hql.append("(flow_def.id = ? and flow_def.version = ?)");
            values.add(flowDefinition.getId());
            values.add(flowDefinition.getVersion());

            if (index != list.size()) {
                hql.append(" or ");
            }
        }
        this.findPage(dataPage, hql.toString(), values.toArray());
    }

    private Page<FlowDefinition> findPage(final Page<FlowDefinition> page, final String hql, final Object... values) {
        Assert.notNull(page, "page不能为空");
        String orderhql = getOrderHql(page, hql);
        Query q = createHQLQuery(orderhql, null);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                q.setParameter(i, values[i]);
            }
        }
        if (page.isAutoCount()) {
            long totalCount = countHqlResult(orderhql, values);
            page.setTotalCount(totalCount);
        }
        setPageParameterToQuery(q, page);
        List result = q.list();
        page.setResult(result);
        return page;
    }

    protected Query setPageParameterToQuery(final Query q, final Page<FlowDefinition> page) {
        Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");
        // hibernate的firstResult的序号从0开始
        q.setFirstResult(page.getFirst() - 1);
        q.setMaxResults(page.getPageSize());
        return q;
    }

    protected long countHqlResult(final String hql, final Object... values) {
        String countHql = prepareCountHql(hql);

        try {
            Query query = getSession().createQuery(hql);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    query.setParameter(i, values[i]);
                }
            }
            return (Long) query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    private String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");
        String countHql = "select count(*) " + fromHql;
        return countHql;
    }

    /**
     * 获取相应的根据page中设置的orderby，order属性拼装相应的hql查询语句，注意参数的对应要一致 如hql为from User u
     * oderby属性要设置为u.uuid等
     *
     * @param page
     * @param hql
     * @return
     */
    private String getOrderHql(final Page<FlowDefinition> page, String hql) {
        String orderhql = hql;
        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

            orderhql += " order by ";
            for (int i = 0; i < orderByArray.length; i++) {

                if (i == orderByArray.length - 1) {
                    orderhql += orderByArray[i] + " " + orderArray[i];
                } else {
                    orderhql += orderByArray[i] + " " + orderArray[i] + ", ";
                }

            }
            // orderhql = orderhql.substring(0, orderhql.length() - 2);
        }
        return orderhql;
    }
}
