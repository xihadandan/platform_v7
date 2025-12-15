/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
@Repository
public class TaskInstanceDaoImpl extends AbstractJpaDaoImpl<TaskInstance, String> implements TaskInstanceDao {

    private static final String GET_TASK_INST_BY_USERID_AND_INSTUID = "from TaskInstance task_inst where task_inst.assignee = :userId and task_inst.flowInstance.uuid = :flowInstUuid";
    private static final String GET_TASK_INST_BY_DATE_UUID = "from TaskInstance task_inst where task_inst.dataUuid = :dataUuid and task_inst.formUuid = :formUuid";

    /**
     * @param userId
     * @param flowInstUuid
     * @return
     */
    @Override
    public TaskInstance getRuntimeTask(String userId, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("flowInstUuid", flowInstUuid);
        return this.getOneByHQL(GET_TASK_INST_BY_USERID_AND_INSTUID, values);
    }

    /**
     * @param taskPage
     */
    @Override
    public void query(Page<TaskInstance> taskPage, String hql, Object... values) {
        this.findPage(taskPage, hql, values);
    }

    private Page<TaskInstance> findPage(final Page<TaskInstance> page, final String hql, final Object... values) {
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

    protected Query setPageParameterToQuery(final Query q, final Page<TaskInstance> page) {
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
    private String getOrderHql(final Page<TaskInstance> page, String hql) {
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

    /**
     * 如何描述该方法
     *
     * @param task
     */
    @Override
    public TaskInstance load(TaskInstance task) {
        return (TaskInstance) this.getSession().load(TaskInstance.class, task.getUuid());
    }

    /**
     * @param formUuid
     * @param serialNo
     * @return
     */
    @Override
    public List<TaskInstance> getByDateUuid(String formUuid, String dateUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("formUuid", formUuid);
        values.put("dataUuid", dateUuid);
        return this.listByHQL(GET_TASK_INST_BY_DATE_UUID, values);
    }

}
