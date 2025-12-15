/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao.impl;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.workflow.dao.FlowFormatDao;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Description: 工作流信息格式持久层操作类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@Repository
public class FlowFormatDaoImpl extends AbstractJpaDaoImpl<FlowFormat, String> implements FlowFormatDao {

    /**
     * @param code
     * @return
     */
    @Override
    public FlowFormat getByCode(String code) {
        List<FlowFormat> flowFormats = listByFieldEqValue("code", code);
        return CollectionUtils.isNotEmpty(flowFormats) ? flowFormats.get(0) : null;
    }

    @Override
    public Page<FlowFormat> findPage(final Page<FlowFormat> page, Criterion... criterions) {
        Assert.notNull(page, "page不能为空");
        Criteria c = createCriteria(criterions);

        if (page.isAutoCount()) {
            long totalCount = countCriteriaResult(c);
            page.setTotalCount(totalCount);
        }
        setPageParameterToCriteria(c, page);

        List result = c.list();
        page.setResult(result);
        return page;
    }

    public Criteria createCriteria(final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(FlowFormat.class);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    protected Criteria setPageParameterToCriteria(final Criteria c, final Page<FlowFormat> page) {

        Assert.isTrue(page.getPageSize() > 0, "Page Size must larger than zero");

        // hibernate的firstResult的序号从0开始
        c.setFirstResult(page.getFirst() - 1);
        c.setMaxResults(page.getPageSize());

        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

            for (int i = 0; i < orderByArray.length; i++) {
                if (Page.ASC.equals(orderArray[i])) {
                    c.addOrder(Order.asc(orderByArray[i]));
                } else {
                    c.addOrder(Order.desc(orderByArray[i]));
                }
            }
        }
        return c;
    }

}
